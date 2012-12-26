/*
 * Copyright 2011-2013, by Vladimir Kostyukov and Contributors.
 * 
 * This file is part of la4j project (http://la4j.org)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * Contributor(s): -
 * 
 */

package org.la4j.decomposition;

import org.la4j.factory.Factory;
import org.la4j.matrix.Matrices;
import org.la4j.matrix.Matrix;
import org.la4j.vector.Vector;

public class EigenDecompositor implements MatrixDecompositor {

    public static final int MAX_ITERATIONS = 10000000;

    @Override
    public Matrix[] decompose(Matrix matrix, Factory factory) {

        if (matrix.is(Matrices.SYMMETRIC_MATRIX)) {
            return decomposeSymmetricMatrix(matrix, factory);
        } else if (matrix.rows() == matrix.columns()) {
            return decomposeNonSymmetricMatrix(matrix, factory);
        } else {
            throw new IllegalArgumentException(
                    "Can't decompose rectangle matrix");
        }
    }

    private Matrix[] decomposeSymmetricMatrix(Matrix matrix, Factory factory) {

        Matrix d = matrix.copy();
        Vector r = generateR(d, factory);

        Matrix v = factory.createIdentityMatrix(matrix.rows());

        int iteration = 0;

        do {

            int k = findMax(r, -1);
            int l = findMax(d.getRow(k), k);

            Matrix u = generateU(d, factory, k, l);

            v = v.unsafe_multiply(u);
            d = u.transpose().unsafe_multiply(d.unsafe_multiply(u));

            r.unsafe_set(k, generateRi(d.getRow(k), k));

            r.unsafe_set(l, generateRi(d.getRow(l), l));

            iteration++;

        } while (r.norm() > Matrices.EPS && iteration < MAX_ITERATIONS);

        if (iteration > MAX_ITERATIONS) {
            throw new IllegalArgumentException("Can't decompose this matrix.");
        }

        return new Matrix[] { v, d };
    }

    private int findMax(Vector vector, int exl) {

        int result = exl == 0 ? 1 : 0;
        for (int i = 0; i < vector.length(); i++) {
            if (i != exl
                    && Math.abs(vector.unsafe_get(result)) < Math.abs(vector
                            .unsafe_get(i))) {
                result = i;
            }
        }

        return result;
    }

    private Vector generateR(Matrix matrix, Factory factory) {

        Vector result = factory.createVector(matrix.rows());

        for (int i = 0; i < matrix.rows(); i++) {
            result.unsafe_set(i, generateRi(matrix.getRow(i), i));
        }

        return result;
    }

    private double generateRi(Vector vector, int position) {

        double summand = 0;

        for (int i = 0; i < vector.length(); i++) {
            if (i != position) {
                summand += vector.unsafe_get(i) * vector.unsafe_get(i);
            }
        }

        return summand;
    }

    private Matrix generateU(Matrix matrix, Factory factory, int k, int l) {

        Matrix result = factory.createIdentityMatrix(matrix.rows());

        double alpha = 0.0, beta = 0.0;

        if ((matrix.unsafe_get(k, k) - matrix.unsafe_get(l, l)) < Matrices.EPS) {
            alpha = beta = Math.sqrt(0.5);
        } else {
            double mu = 2 * matrix.unsafe_get(k, l)
                    / (matrix.unsafe_get(k, k) - matrix.unsafe_get(l, l));
            mu = 1 / Math.sqrt(1 + mu * mu);
            alpha = Math.sqrt(0.5 * (1 + mu));
            beta = Math.signum(mu) * Math.sqrt(0.5 * (1 - mu));
        }

        result.unsafe_set(k, k, alpha);
        result.unsafe_set(l, l, alpha);
        result.unsafe_set(k, l, -beta);
        result.unsafe_set(l, k, beta);

        return result;
    }

    private Matrix[] decomposeNonSymmetricMatrix(Matrix matrix, Factory factory) {

        Matrix A = matrix.copy();
        int n = matrix.columns();

        Matrix v = factory.createIdentityMatrix(n);
        Vector d = factory.createVector(n);
        Vector e = factory.createVector(n);

        Matrix h = A.copy();
        Vector ort = factory.createVector(n);

        // Reduce to Hessenberg form.
        orthes(h, v, ort);

        // Reduce Hessenberg to real Schur form.
        hqr2(h, v, d, e);

        Matrix dd = factory.createMatrix(n, n);
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                dd.unsafe_set(i, j, 0.0);
            }

            dd.unsafe_set(i, i, d.unsafe_get(i));

            if (e.unsafe_get(i) > 0) {
                dd.unsafe_set(i, i + 1, e.unsafe_get(i));
            } else if (e.unsafe_get(i) < 0) {
                dd.unsafe_set(i, i - 1, e.unsafe_get(i));
            }
        }

        return new Matrix[] { v, dd };
    }

    // Nonsymmetric reduction to Hessenberg form.

    private void orthes(Matrix h, Matrix v, Vector ort) {

        // This is derived from the Algol procedures orthes and ortran,
        // by Martin and Wilkinson, Handbook for Auto. Comp.,
        // Vol.ii-Linear Algebra, and the corresponding
        // Fortran subroutines in EISPACK.

        int n = ort.length();
        int low = 0;
        int high = n - 1;

        for (int m = low + 1; m <= high - 1; m++) {

            // Scale column.

            double scale = 0.0;

            for (int i = m; i <= high; i++) {
                scale = scale + Math.abs(h.unsafe_get(i, m - 1));
            }

            if (scale != 0.0) {

                // Compute Householder transformation.

                double hh = 0.0;
                for (int i = high; i >= m; i--) {
                    ort.unsafe_set(i, h.unsafe_get(i, m - 1) / scale);
                    hh += ort.unsafe_get(i) * ort.unsafe_get(i);
                }

                double g = Math.sqrt(hh);

                if (ort.unsafe_get(m) > Matrices.EPS) {
                    g = -g;
                }

                hh = hh - ort.unsafe_get(m) * g;
                ort.unsafe_set(m, ort.unsafe_get(m) - g);

                // Apply Householder similarity transformation
                // H = (I-u*u'/h)*H*(I-u*u')/h)

                for (int j = m; j < n; j++) {
                    double f = 0.0;
                    for (int i = high; i >= m; i--) {
                        f += ort.unsafe_get(i) * h.unsafe_get(i, j);
                    }
                    f = f / hh;
                    for (int i = m; i <= high; i++) {
                        h.unsafe_set(i, j, h.unsafe_get(i, j) 
                                     -  (f * ort.unsafe_get(i)));
                    }
                }

                for (int i = 0; i <= high; i++) {
                    double f = 0.0;
                    for (int j = high; j >= m; j--) {
                        f += ort.unsafe_get(j) * h.unsafe_get(i, j);
                    }
                    f = f / hh;
                    for (int j = m; j <= high; j++) {
                        h.unsafe_set(i, j, h.unsafe_get(i, j) 
                                     - (f * ort.unsafe_get(j)));
                    }
                }
                ort.unsafe_set(m, scale * ort.unsafe_get(m));
                h.unsafe_set(m,  m - 1, scale * g);
            }
        }

        // Accumulate transformations (Algol's ortran).

        for (int m = high - 1; m >= low + 1; m--) {
            if (Math.abs(h.unsafe_get(m, m - 1)) > Matrices.EPS) {
                for (int i = m + 1; i <= high; i++) {
                    ort.unsafe_set(i, h.unsafe_get(i, m - 1));
                }
                for (int j = m; j <= high; j++) {
                    double g = 0.0;
                    for (int i = m; i <= high; i++) {
                        g += ort.unsafe_get(i) * v.unsafe_get(i, j);
                    }
                    // Double division avoids possible underflow
                    g = (g / ort.unsafe_get(m)) / h.unsafe_get(m, m - 1);
                    for (int i = m; i <= high; i++) {
                        v.unsafe_set(i, j, v.unsafe_get(i, j) + g * ort.unsafe_get(i));
                    }
                }
            }
        }
    }

    // Nonsymmetric reduction from Hessenberg to real Schur form.

    private void hqr2(Matrix H, Matrix V, Vector d, Vector e) {

        // This is derived from the Algol procedure hqr2,
        // by Martin and Wilkinson, Handbook for Auto. Comp.,
        // Vol.ii-Linear Algebra, and the corresponding
        // Fortran subroutine in EISPACK.

        // Initialize

        int nn = e.length();
        int n = nn - 1;
        int low = 0;
        int high = nn - 1;
        double eps = Math.pow(2.0, -52.0);
        double exshift = 0.0;
        double p = 0, q = 0, r = 0, s = 0, z = 0, t, w, x, y;

        // Store roots isolated by balanc and compute matrix norm

        double norm = 0.0;
        for (int i = 0; i < nn; i++) {
            if (i < low | i > high) {
                d.unsafe_set(i, H.unsafe_get(i, i));
                e.unsafe_set(i, 0.0);
            }
            for (int j = Math.max(i - 1, 0); j < nn; j++) {
                norm = norm + Math.abs(H.unsafe_get(i, j));
            }
        }

        // Outer loop over eigenvalue index

        int iter = 0;
        while (n >= low) {

            // Look for single small sub-diagonal element

            int l = n;
            while (l > low) {
                s = Math.abs(H.unsafe_get(l - 1, l - 1)) 
                    + Math.abs(H.unsafe_get(l, l));

                if (s == 0.0) {
                    s = norm;
                }
                if (Math.abs(H.unsafe_get(l, l - 1)) < eps * s) {
                    break;
                }
                l--;
            }

            // Check for convergence
            // One root found

            if (l == n) {
                H.unsafe_set(n, n, H.unsafe_get(n, n) + exshift);
                d.unsafe_set(n, H.unsafe_get(n, n));
                e.unsafe_set(n, 0.0);
                n--;
                iter = 0;

                // Two roots found

            } else if (l == n - 1) {
                w = H.unsafe_get(n, n - 1) * H.unsafe_get(n - 1, n);
                p = (H.unsafe_get(n - 1, n - 1) - H.unsafe_get(n, n)) / 2.0;
                q = p * p + w;
                z = Math.sqrt(Math.abs(q));
                H.unsafe_set(n, n, H.unsafe_get(n, n) + exshift);
                H.unsafe_set(n - 1, n - 1, H.unsafe_get(n - 1, n - 1) + exshift);
                x = H.unsafe_get(n, n);

                // Real pair

                if (q >= 0) {
                    if (p >= 0) {
                        z = p + z;
                    } else {
                        z = p - z;
                    }
                    d.unsafe_set(n - 1, x + z);
                    d.unsafe_set(n, d.unsafe_get(n - 1));
                    if (z != 0.0) {
                        d.unsafe_set(n, x - w / z);
                    }
                    e.unsafe_set(n - 1, 0.0);
                    e.unsafe_set(n, 0.0);
                    x = H.unsafe_get(n, n - 1);
                    s = Math.abs(x) + Math.abs(z);
                    p = x / s;
                    q = z / s;
                    r = Math.sqrt(p * p + q * q);
                    p = p / r;
                    q = q / r;

                    // Row modification

                    for (int j = n - 1; j < nn; j++) {
                        z = H.unsafe_get(n - 1, j);
                        H.unsafe_set(n - 1, j, q * z + p * H.unsafe_get(n, j));
                        H.unsafe_set(n, j, q * H.unsafe_get(n, j) - p * z);
                    }

                    // Column modification

                    for (int i = 0; i <= n; i++) {
                        z = H.unsafe_get(i, n - 1);
                        H.unsafe_set(i, n - 1, q * z + p * H.unsafe_get(i, n));
                        H.unsafe_set(i, n, q * H.unsafe_get(i, n) - p * z);
                    }

                    // Accumulate transformations

                    for (int i = low; i <= high; i++) {
                        z = V.unsafe_get(i, n - 1);
                        V.unsafe_set(i, n - 1, q * z + p * V.unsafe_get(i, n));
                        V.unsafe_set(i, n, q * V.unsafe_get(i, n) - p * z);
                    }

                    // Complex pair

                } else {
                    d.unsafe_set(n - 1, x + p);
                    d.unsafe_set(n, x + p);
                    e.unsafe_set(n - 1, z);
                    e.unsafe_set(n, -z);
                }
                n = n - 2;
                iter = 0;

                // No convergence yet

            } else {

                // Form shift

                x = H.unsafe_get(n, n);
                y = 0.0;
                w = 0.0;
                if (l < n) {
                    y = H.unsafe_get(n - 1, n - 1);
                    w = H.unsafe_get(n, n - 1) * H.unsafe_get(n - 1, n);
                }

                // Wilkinson's original ad hoc shift

                if (iter == 10) {
                    exshift += x;
                    for (int i = low; i <= n; i++) {
                        H.unsafe_set(i, i, H.unsafe_get(i, i) - x);
                    }
                    s = Math.abs(H.unsafe_get(n, n - 1)) 
                        + Math.abs(H.unsafe_get(n - 1, n - 2));
                    x = y = 0.75 * s; // WTF ???
                    w = -0.4375 * s * s; // Are you kidding me???
                }

                // MATLAB's new ad hoc shift

                if (iter == 30) {
                    s = (y - x) / 2.0;
                    s = s * s + w;
                    if (s > 0) {
                        s = Math.sqrt(s);
                        if (y < x) {
                            s = -s;
                        }
                        s = x - w / ((y - x) / 2.0 + s);
                        for (int i = low; i <= n; i++) {
                            H.unsafe_set(i, i, H.unsafe_get(i, i) - s);
                        }
                        exshift += s;
                        x = y = w = 0.964;
                    }
                }

                iter = iter + 1; // (Could check iteration count here.)

                // Look for two consecutive small sub-diagonal elements

                int m = n - 2;
                while (m >= l) {
                    z = H.unsafe_get(m, m);
                    r = x - z;
                    s = y - z;
                    p = (r * s - w) / H.unsafe_get(m + 1, m) 
                                      + H.unsafe_get(m, m + 1);
                    q = H.unsafe_get(m + 1, m + 1) - z - r - s;
                    r = H.unsafe_get(m + 2, m + 1);
                    s = Math.abs(p) + Math.abs(q) + Math.abs(r);
                    p = p / s;
                    q = q / s;
                    r = r / s;
                    if (m == l) {
                        break;
                    }
                    if (Math.abs(H.unsafe_get(m, m - 1)) * (Math.abs(q) + Math.abs(r)) < eps
                            * (Math.abs(p) * (Math.abs(H.unsafe_get(m - 1, m - 1))
                                    + Math.abs(z) + Math.abs(H.unsafe_get(m + 1, m + 1))))) {
                        break;
                    }
                    m--;
                }

                for (int i = m + 2; i <= n; i++) {
                    H.unsafe_set(i, i - 2, 0.0);
                    if (i > m + 2) {
                        H.unsafe_set(i, i - 3, 0.0);
                    }
                }

                // Double QR step involving rows l:n and columns m:n

                for (int k = m; k <= n - 1; k++) {
                    boolean notlast = (k != n - 1);
                    if (k != m) {
                        p = H.unsafe_get(k, k - 1);
                        q = H.unsafe_get(k + 1, k - 1);
                        r = (notlast ? H.unsafe_get(k + 2, k - 1) : 0.0);
                        x = Math.abs(p) + Math.abs(q) + Math.abs(r);
                        if (x == 0.0) {
                            continue;
                        }
                        p = p / x;
                        q = q / x;
                        r = r / x;
                    }

                    s = Math.sqrt(p * p + q * q + r * r);
                    if (p < 0) {
                        s = -s;
                    }
                    if (s != 0) {
                        if (k != m) {
                            H.unsafe_set(k, k - 1, -s * x);
                        } else if (l != m) {
                            H.unsafe_set(k, k - 1, -H.unsafe_get(k, k - 1));
                        }
                        p = p + s;
                        x = p / s;
                        y = q / s;
                        z = r / s;
                        q = q / p;
                        r = r / p;

                        // Row modification

                        for (int j = k; j < nn; j++) {
                            p = H.unsafe_get(k, j) + q * H.unsafe_get(k + 1, j);
                            if (notlast) {
                                p = p + r * H.unsafe_get(k + 2, j);
                                H.unsafe_set(k + 2, j, H.unsafe_get(k + 2, j) 
                                             - p * z);
                            }
                            H.unsafe_set(k, j, H.unsafe_get(k, j) - p * x);
                            H.unsafe_set(k + 1, j, H.unsafe_get(k + 1, j) 
                                         - p * y);
                        }

                        // Column modification

                        for (int i = 0; i <= Math.min(n, k + 3); i++) {
                            p = x * H.unsafe_get(i, k) + y 
                                * H.unsafe_get(i, k + 1);
                            if (notlast) {
                                p = p + z * H.unsafe_get(i, k + 2);
                                H.unsafe_set(i, k + 2, H.unsafe_get(i, k + 2)
                                             - p * r);
                            }
                            H.unsafe_set(i, k, H.unsafe_get(i, k) - p);
                            H.unsafe_set(i, k + 1, H.unsafe_get(i, k + 1) 
                                         - p * q);
                        }

                        // Accumulate transformations

                        for (int i = low; i <= high; i++) {
                            p = x * V.unsafe_get(i, k) + y 
                                * V.unsafe_get(i, k + 1);
                            if (notlast) {
                                p = p + z * V.unsafe_get(i, k + 2);
                                V.unsafe_set(i, k + 2, V.unsafe_get(i, k + 2) 
                                             - p * r);
                            }
                            V.unsafe_set(i, k, V.unsafe_get(i, k) - p);
                            V.unsafe_set(i, k + 1, V.unsafe_get(i, k + 1) 
                                         - p * q);
                        }
                    } // (s != 0)
                } // k loop
            } // check convergence
        } // while (n >= low)

        // Backsubstitute to find vectors of upper triangular form

        if (norm == 0.0) {
            return;
        }

        for (n = nn - 1; n >= 0; n--) {
            p = d.unsafe_get(n);
            q = e.unsafe_get(n);

            // Real vector

            if (q == 0) {
                int l = n;
                H.unsafe_set(n, n, 1.0);
                for (int i = n - 1; i >= 0; i--) {
                    w = H.unsafe_get(i, i) - p;
                    r = 0.0;
                    for (int j = l; j <= n; j++) {
                        r = r + H.unsafe_get(i, j) * H.unsafe_get(j, n);
                    }
                    if (e.unsafe_get(i) < 0.0) {
                        z = w;
                        s = r;
                    } else {
                        l = i;
                        if (e.unsafe_get(i) == 0.0) {
                            if (w != 0.0) {
                                H.unsafe_set(i, n, -r / w);
                            } else {
                                H.unsafe_set(i, n, -r / (eps * norm));
                            }

                            // Solve real equations

                        } else {
                            x = H.unsafe_get(i, i + 1);
                            y = H.unsafe_get(i + 1, i);
                            q = (d.unsafe_get(i) - p) * (d.unsafe_get(i) - p) 
                                + e.unsafe_get(i) * e.unsafe_get(i);
                            t = (x * s - z * r) / q;
                            H.unsafe_set(i, n, t);
                            if (Math.abs(x) > Math.abs(z)) {
                                H.unsafe_set(i + 1, n, (-r - w * t) / x);
                            } else {
                                H.unsafe_set(i + 1, n, (-s - y * t) / z);
                            }
                        }

                        // Overflow control

                        t = Math.abs(H.unsafe_get(i, n));
                        if ((eps * t) * t > 1) {
                            for (int j = i; j <= n; j++) {
                                H.unsafe_set(j, n, H.unsafe_get(j, n) / t);
                            }
                        }
                    }
                }

                // Complex vector

            } else if (q < 0) {
                int l = n - 1;

                // Last vector component imaginary so matrix is triangular

                if (Math.abs(H.unsafe_get(n, n - 1)) 
                        > Math.abs(H.unsafe_get(n - 1, n))) {
                    H.unsafe_set(n - 1, n - 1, q / H.unsafe_get(n, n - 1));
                    H.unsafe_set(n - 1, n, -(H.unsafe_get(n, n) - p) 
                                            / H.unsafe_get(n, n - 1));
                } else {
                    double cdiv[] = cdiv(0.0, -H.unsafe_get(n - 1, n), 
                         H.unsafe_get(n - 1, n - 1) - p, q);
                    H.unsafe_set(n - 1, n - 1, cdiv[0]);
                    H.unsafe_set(n - 1, n, cdiv[1]);
                }
                H.unsafe_set(n, n - 1, 0.0);
                H.unsafe_set(n, n, 1.0);
                for (int i = n - 2; i >= 0; i--) {
                    double ra, sa, vr, vi;
                    ra = 0.0;
                    sa = 0.0;
                    for (int j = l; j <= n; j++) {
                        ra = ra + H.unsafe_get(i, j) * H.unsafe_get(j, n - 1);
                        sa = sa + H.unsafe_get(i, j) * H.unsafe_get(j, n);
                    }
                    w = H.unsafe_get(i, i) - p;

                    if (e.unsafe_get(i) < 0.0) {
                        z = w;
                        r = ra;
                        s = sa;
                    } else {
                        l = i;
                        if (e.unsafe_get(i) == 0) {
                            double cdiv[] = cdiv(-ra, -sa, w, q);
                            H.unsafe_set(i, n - 1, cdiv[0]);
                            H.unsafe_set(i, n, cdiv[1]);
                        } else {

                            // Solve complex equations

                            x = H.unsafe_get(i, i + 1);
                            y = H.unsafe_get(i + 1, i);
                            vr = (d.unsafe_get(i) - p) * (d.unsafe_get(i) - p) 
                                 + e.unsafe_get(i) * e.unsafe_get(i) - q * q;
                            vi = (d.unsafe_get(i) - p) * 2.0 * q;
                            if (vr == 0.0 & vi == 0.0) {
                                vr = eps
                                        * norm
                                        * (Math.abs(w) + Math.abs(q)
                                                + Math.abs(x) + Math.abs(y) + Math
                                                    .abs(z));
                            }
                            double cdiv[] = cdiv(x * r - z * ra + q * sa, 
                                                 x * s - z * sa - q * ra, vr, vi);
                            H.unsafe_set(i, n - 1, cdiv[0]);
                            H.unsafe_set(i, n, cdiv[1]);
                            if (Math.abs(x) > (Math.abs(z) + Math.abs(q))) {
                                H.unsafe_set(i + 1, n - 1, (-ra - w 
                                             * H.unsafe_get(i, n - 1) + q
                                             * H.unsafe_get(i, n)) / x);
                                H.unsafe_set(i + 1, n, (-sa - w 
                                             * H.unsafe_get(i, n) - q 
                                             * H.unsafe_get(i, n - 1)) / x);
                            } else {
                                cdiv = cdiv(-r - y 
                                     * H.unsafe_get(i, n - 1), -s - y 
                                     * H.unsafe_get(i, n), z, q);
                                H.unsafe_set(i + 1, n - 1, cdiv[0]);
                                H.unsafe_set(i + 1, n, cdiv[1]);
                            }
                        }

                        // Overflow control

                        t = Math.max(Math.abs(H.unsafe_get(i, n - 1)), 
                                     Math.abs(H.unsafe_get(i, n)));
                        if ((eps * t) * t > 1) {
                            for (int j = i; j <= n; j++) {
                                H.unsafe_set(j, n - 1, 
                                             H.unsafe_get(j, n - 1) / t);
                                H.unsafe_set(j, n, H.unsafe_get(j, n) / t);
                            }
                        }
                    }
                }
            }
        }

        // Vectors of isolated roots

        for (int i = 0; i < nn; i++) {
            if (i < low | i > high) {
                for (int j = i; j < nn; j++) {
                    V.unsafe_set(i, j, H.unsafe_get(i, j));
                }
            }
        }

        // Back transformation to get eigenvectors of original matrix

        for (int j = nn - 1; j >= low; j--) {
            for (int i = low; i <= high; i++) {
                z = 0.0;
                for (int k = low; k <= Math.min(j, high); k++) {
                    z = z + V.unsafe_get(i, k) * H.unsafe_get(k, j);
                }
                V.unsafe_set(i, j, z);
            }
        }
    }


   private double[] cdiv(double xr, double xi, double yr, double yi) {
        double cdivr, cdivi;
        double r, d;
        if (Math.abs(yr) > Math.abs(yi)) {
            r = yi / yr;
            d = yr + r * yi;
            cdivr = (xr + r * xi) / d;
            cdivi = (xi - r * xr) / d;
        } else {
            r = yr / yi;
            d = yi + r * yr;
            cdivr = (r * xr + xi) / d;
            cdivi = (r * xi - xr) / d;
        }

        return new double[] { cdivr, cdivi };
    }
  
}
