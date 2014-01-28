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
 * Contributor(s): Maxim Samoylov
 * 
 */

package org.la4j.decomposition;

import org.la4j.factory.Factory;
import org.la4j.matrix.Matrices;
import org.la4j.matrix.Matrix;
import org.la4j.vector.Vector;
import org.la4j.vector.Vectors;
import org.la4j.vector.functor.VectorAccumulator;

/**
 * This class represents Eigen decomposition of matrices. More details
 * <p>
 * <a href="http://mathworld.wolfram.com/EigenDecomposition.html"> here.</a>
 * </p>
 */
public class EigenDecompositor extends AbstractDecompositor implements MatrixDecompositor {

    public EigenDecompositor(Matrix matrix) {
        super(matrix);
    }

    /**
     * Returns the result of Eigen (EVD) decomposition of given matrix
     * <p>
     * See <a href="http://mathworld.wolfram.com/EigenDecomposition.html">
     * http://mathworld.wolfram.com/EigenDecomposition.html</a> for more
     * details.
     * </p>
     * 
     * @param factory
     * @return { V, D }
     */
    @Override
    public Matrix[] decompose(Factory factory) {

        if (matrix.is(Matrices.SYMMETRIC_MATRIX)) {
            return decomposeSymmetricMatrix(matrix, factory);
        } else if (matrix.rows() == matrix.columns()) {
            return decomposeNonSymmetricMatrix(matrix, factory);
        } else {
            throw new IllegalArgumentException("Can't decompose rectangle matrix");
        }
    }

    @Override
    public boolean applicableTo(Matrix matrix) {
        return matrix.rows() == matrix.columns();
    }

    /**
     * Returns the result of Eigen decomposition for <a
     * href="http://mathworld.wolfram.com/SymmetricMatrix.html"> symmetric</a>
     * matrix
     * <p>
     * See <a href="http://mathworld.wolfram.com/EigenDecomposition.html">
     * http://mathworld.wolfram.com/EigenDecomposition.html</a> for more
     * details.
     * </p>
     * 
     * @param matrix
     * @param factory
     * @return { V, D }
     */
    private Matrix[] decomposeSymmetricMatrix(Matrix matrix, Factory factory) {

        Matrix d = matrix.copy();
        Matrix v = factory.createIdentityMatrix(matrix.rows());

        Vector r = generateR(d, factory);
        Matrix u = factory.createIdentityMatrix(matrix.rows());

        VectorAccumulator normAccumulator = Vectors.mkEuclideanNormAccumulator();

        double n = Matrices.EPS;
        double nn = r.fold(normAccumulator);

        int kk = 0, ll = 0;

        while (Math.abs(n - nn) > Matrices.EPS) {

            int k = findMax(r);
            int l = findMax(d, k);

            regenerateU(u, d, k, l, kk, ll);

            kk = k;
            ll = l;

            v = v.multiply(u);
            d = u.transpose().multiply(d.multiply(u));

            r.set(k, generateRi(d, k));
            r.set(l, generateRi(d, l));

            n = nn;
            nn = r.fold(normAccumulator);
        }

        return new Matrix[] { v, d };
    }

    private int findMax(Vector vector) {

        double value = vector.get(0);
        int result = 0;

        for (int i = 1; i < vector.length(); i++) {
            double v = vector.get(i);
            if (Math.abs(value) < Math.abs(v)) {
                result = i;
                value = v;
            }
        }

        return result;
    }

    private int findMax(Matrix matrix, int i) {

        double value = i > 0 ? matrix.get(i, 0) : matrix.get(i, 1);
        int result = i > 0 ? 0 : 1;

        for (int j = 0; j < matrix.columns(); j++) {
            if (i != j) {
                double v = matrix.get(i, j);
                if (Math.abs(value) < Math.abs(v)) {
                    result = j;
                    value = v;
                }
            }
        }

        return result;
    }

    private Vector generateR(Matrix matrix, Factory factory) {

        Vector result = factory.createVector(matrix.rows());

        for (int i = 0; i < matrix.rows(); i++) {
            result.set(i, generateRi(matrix, i));
        }

        return result;
    }

    private double generateRi(Matrix matrix, int i) {
        double acc = 0;
        for (int j = 0; j < matrix.columns(); j++) {
            if (j != i) {
                double value = matrix.get(i, j);
                acc += value * value;
            }
        }

        return acc;
    }

    private void regenerateU(Matrix u, Matrix matrix, int k, int l, int kk, int ll) {

        u.set(kk, kk, 1.0);
        u.set(ll, ll, 1.0);
        u.set(kk, ll, 0.0);
        u.set(ll, kk, 0.0);

        double alpha = 0.0, beta = 0.0;

        if (Math.abs(matrix.get(k, k) - matrix.get(l, l)) < Matrices.EPS) {
            alpha = beta = Math.sqrt(0.5);
        } else {
            double mu = 2 * matrix.get(k, l) / (matrix.get(k, k) - matrix.get(l, l));
            mu = 1.0 / Math.sqrt(1.0 + mu * mu);
            alpha = Math.sqrt(0.5 * (1.0 + mu));
            beta = Math.signum(mu) * Math.sqrt(0.5 * (1.0 - mu));
        }

        u.set(k, k, alpha);
        u.set(l, l, alpha);
        u.set(k, l, -beta);
        u.set(l, k, beta);
    }

    /**
     * Returns the result of Eigen decomposition for non-<a
     * href="http://mathworld.wolfram.com/SymmetricMatrix.html">symmetric</a>
     * matrix
     * <p>
     * See <a href="http://mathworld.wolfram.com/EigenDecomposition.html">
     * http://mathworld.wolfram.com/EigenDecomposition.html</a> for more
     * details.
     * </p>
     * 
     * @param matrix
     * @param factory
     * @return { P, D }
     */
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
            dd.set(i, i, d.get(i));

            if (e.get(i) > 0) {
                dd.set(i, i + 1, e.get(i));
            } else if (e.get(i) < 0) {
                dd.set(i, i - 1, e.get(i));
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
                scale = scale + Math.abs(h.get(i, m - 1));
            }

            if (scale != 0.0) {

                // Compute Householder transformation.

                double hh = 0.0;
                for (int i = high; i >= m; i--) {
                    ort.set(i, h.get(i, m - 1) / scale);
                    hh += ort.get(i) * ort.get(i);
                }

                double g = Math.sqrt(hh);

                if (ort.get(m) > Matrices.EPS) {
                    g = -g;
                }

                hh = hh - ort.get(m) * g;
                ort.update(m, Vectors.asMinusFunction(g));

                // Apply Householder similarity transformation
                // H = (I-u*u'/h)*H*(I-u*u')/h)

                for (int j = m; j < n; j++) {
                    double f = 0.0;
                    for (int i = high; i >= m; i--) {
                        f += ort.get(i) * h.get(i, j);
                    }
                    f = f / hh;
                    for (int i = m; i <= high; i++) {
                        h.update(i, j, Matrices.asMinusFunction(f * ort.get(i)));
                    }
                }

                for (int i = 0; i <= high; i++) {
                    double f = 0.0;
                    for (int j = high; j >= m; j--) {
                        f += ort.get(j) * h.get(i, j);
                    }
                    f = f / hh;
                    for (int j = m; j <= high; j++) {
                        h.update(i, j, Matrices.asMinusFunction(f * ort.get(j)));
                    }
                }
                ort.set(m, scale * ort.get(m));
                h.set(m,  m - 1, scale * g);
            }
        }

        // Accumulate transformations (Algol's ortran).

        for (int m = high - 1; m >= low + 1; m--) {
            if (Math.abs(h.get(m, m - 1)) > Matrices.EPS) {
                for (int i = m + 1; i <= high; i++) {
                    ort.set(i, h.get(i, m - 1));
                }
                for (int j = m; j <= high; j++) {
                    double g = 0.0;
                    for (int i = m; i <= high; i++) {
                        g += ort.get(i) * v.get(i, j);
                    }
                    // Double division avoids possible underflow
                    g = (g / ort.get(m)) / h.get(m, m - 1);
                    for (int i = m; i <= high; i++) {
                        v.update(i, j, Matrices.asPlusFunction(g * ort.get(i)));
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
                d.set(i, H.get(i, i));
                e.set(i, 0.0);
            }
            for (int j = Math.max(i - 1, 0); j < nn; j++) {
                norm = norm + Math.abs(H.get(i, j));
            }
        }

        // Outer loop over eigenvalue index

        int iter = 0;
        while (n >= low) {

            // Look for single small sub-diagonal element

            int l = n;
            while (l > low) {
                s = Math.abs(H.get(l - 1, l - 1)) 
                    + Math.abs(H.get(l, l));

                if (s == 0.0) {
                    s = norm;
                }
                if (Math.abs(H.get(l, l - 1)) < eps * s) {
                    break;
                }
                l--;
            }

            // Check for convergence
            // One root found

            if (l == n) {
                H.update(n, n, Matrices.asPlusFunction(exshift));
                d.set(n, H.get(n, n));
                e.set(n, 0.0);
                n--;
                iter = 0;

                // Two roots found

            } else if (l == n - 1) {
                w = H.get(n, n - 1) * H.get(n - 1, n);
                p = (H.get(n - 1, n - 1) - H.get(n, n)) / 2.0;
                q = p * p + w;
                z = Math.sqrt(Math.abs(q));
                H.update(n, n, Matrices.asPlusFunction(exshift));
                H.update(n - 1, n - 1, Matrices.asPlusFunction(exshift));
                x = H.get(n, n);

                // Real pair

                if (q >= 0) {
                    if (p >= 0) {
                        z = p + z;
                    } else {
                        z = p - z;
                    }
                    d.set(n - 1, x + z);
                    d.set(n, d.get(n - 1));
                    if (z != 0.0) {
                        d.set(n, x - w / z);
                    }
                    e.set(n - 1, 0.0);
                    e.set(n, 0.0);
                    x = H.get(n, n - 1);
                    s = Math.abs(x) + Math.abs(z);
                    p = x / s;
                    q = z / s;
                    r = Math.sqrt(p * p + q * q);
                    p = p / r;
                    q = q / r;

                    // Row modification

                    for (int j = n - 1; j < nn; j++) {
                        z = H.get(n - 1, j);
                        H.set(n - 1, j, q * z + p * H.get(n, j));
                        H.set(n, j, q * H.get(n, j) - p * z);
                    }

                    // Column modification

                    for (int i = 0; i <= n; i++) {
                        z = H.get(i, n - 1);
                        H.set(i, n - 1, q * z + p * H.get(i, n));
                        H.set(i, n, q * H.get(i, n) - p * z);
                    }

                    // Accumulate transformations

                    for (int i = low; i <= high; i++) {
                        z = V.get(i, n - 1);
                        V.set(i, n - 1, q * z + p * V.get(i, n));
                        V.set(i, n, q * V.get(i, n) - p * z);
                    }

                    // Complex pair

                } else {
                    d.set(n - 1, x + p);
                    d.set(n, x + p);
                    e.set(n - 1, z);
                    e.set(n, -z);
                }
                n = n - 2;
                iter = 0;

                // No convergence yet

            } else {

                // Form shift

                x = H.get(n, n);
                y = 0.0;
                w = 0.0;
                if (l < n) {
                    y = H.get(n - 1, n - 1);
                    w = H.get(n, n - 1) * H.get(n - 1, n);
                }

                // Wilkinson's original ad hoc shift

                if (iter == 10) {
                    exshift += x;
                    for (int i = low; i <= n; i++) {
                        H.update(i, i, Matrices.asMinusFunction(x));
                    }
                    s = Math.abs(H.get(n, n - 1)) 
                        + Math.abs(H.get(n - 1, n - 2));
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
                            H.update(i, i, Matrices.asMinusFunction(s));
                        }
                        exshift += s;
                        x = y = w = 0.964;
                    }
                }

                iter = iter + 1; // (Could check iteration count here.)

                // Look for two consecutive small sub-diagonal elements

                int m = n - 2;
                while (m >= l) {
                    z = H.get(m, m);
                    r = x - z;
                    s = y - z;
                    p = (r * s - w) / H.get(m + 1, m) 
                                      + H.get(m, m + 1);
                    q = H.get(m + 1, m + 1) - z - r - s;
                    r = H.get(m + 2, m + 1);
                    s = Math.abs(p) + Math.abs(q) + Math.abs(r);
                    p = p / s;
                    q = q / s;
                    r = r / s;
                    if (m == l) {
                        break;
                    }
                    if (Math.abs(H.get(m, m - 1)) * (Math.abs(q) + Math.abs(r)) < eps
                            * (Math.abs(p) * (Math.abs(H.get(m - 1, m - 1))
                                    + Math.abs(z) + Math.abs(H.get(m + 1, m + 1))))) {
                        break;
                    }
                    m--;
                }

                for (int i = m + 2; i <= n; i++) {
                    H.set(i, i - 2, 0.0);
                    if (i > m + 2) {
                        H.set(i, i - 3, 0.0);
                    }
                }

                // Double QR step involving rows l:n and columns m:n

                for (int k = m; k <= n - 1; k++) {
                    boolean notlast = (k != n - 1);
                    if (k != m) {
                        p = H.get(k, k - 1);
                        q = H.get(k + 1, k - 1);
                        r = (notlast ? H.get(k + 2, k - 1) : 0.0);
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
                            H.set(k, k - 1, -s * x);
                        } else if (l != m) {
                            H.update(k, k - 1, Matrices.INV_FUNCTION);
                        }
                        p = p + s;
                        x = p / s;
                        y = q / s;
                        z = r / s;
                        q = q / p;
                        r = r / p;

                        // Row modification

                        for (int j = k; j < nn; j++) {
                            p = H.get(k, j) + q * H.get(k + 1, j);
                            if (notlast) {
                                p = p + r * H.get(k + 2, j);
                                H.update(k + 2, j, 
                                         Matrices.asMinusFunction(p * z));
                            }
                            H.update(k, j, Matrices.asMinusFunction(p * x));
                            H.update(k + 1, j, Matrices.asMinusFunction(p * y));
                        }

                        // Column modification

                        for (int i = 0; i <= Math.min(n, k + 3); i++) {
                            p = x * H.get(i, k) + y 
                                * H.get(i, k + 1);
                            if (notlast) {
                                p = p + z * H.get(i, k + 2);
                                H.update(i, k + 2, 
                                         Matrices.asMinusFunction(p * r));
                            }
                            H.update(i, k, Matrices.asMinusFunction(p));
                            H.update(i, k + 1, Matrices.asMinusFunction(p * q));
                        }

                        // Accumulate transformations

                        for (int i = low; i <= high; i++) {
                            p = x * V.get(i, k) + y 
                                * V.get(i, k + 1);
                            if (notlast) {
                                p = p + z * V.get(i, k + 2);
                                V.update(i, k + 2, 
                                         Matrices.asMinusFunction(p * r));
                            }
                            V.update(i, k, Matrices.asMinusFunction(p));
                            V.update(i, k + 1, Matrices.asMinusFunction(p * q));
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
            p = d.get(n);
            q = e.get(n);

            // Real vector

            if (q == 0) {
                int l = n;
                H.set(n, n, 1.0);
                for (int i = n - 1; i >= 0; i--) {
                    w = H.get(i, i) - p;
                    r = 0.0;
                    for (int j = l; j <= n; j++) {
                        r = r + H.get(i, j) * H.get(j, n);
                    }
                    if (e.get(i) < 0.0) {
                        z = w;
                        s = r;
                    } else {
                        l = i;
                        if (e.get(i) == 0.0) {
                            if (w != 0.0) {
                                H.set(i, n, -r / w);
                            } else {
                                H.set(i, n, -r / (eps * norm));
                            }

                            // Solve real equations

                        } else {
                            x = H.get(i, i + 1);
                            y = H.get(i + 1, i);
                            q = (d.get(i) - p) * (d.get(i) - p) 
                                + e.get(i) * e.get(i);
                            t = (x * s - z * r) / q;
                            H.set(i, n, t);
                            if (Math.abs(x) > Math.abs(z)) {
                                H.set(i + 1, n, (-r - w * t) / x);
                            } else {
                                H.set(i + 1, n, (-s - y * t) / z);
                            }
                        }

                        // Overflow control

                        t = Math.abs(H.get(i, n));
                        if ((eps * t) * t > 1) {
                            for (int j = i; j <= n; j++) {
                                H.update(j, n, Matrices.asDivFunction(t));
                            }
                        }
                    }
                }

                // Complex vector

            } else if (q < 0) {
                int l = n - 1;

                // Last vector component imaginary so matrix is triangular

                if (Math.abs(H.get(n, n - 1)) 
                        > Math.abs(H.get(n - 1, n))) {
                    H.set(n - 1, n - 1, q / H.get(n, n - 1));
                    H.set(n - 1, n, -(H.get(n, n) - p) 
                                            / H.get(n, n - 1));
                } else {
                    double cdiv[] = cdiv(0.0, -H.get(n - 1, n), 
                         H.get(n - 1, n - 1) - p, q);
                    H.set(n - 1, n - 1, cdiv[0]);
                    H.set(n - 1, n, cdiv[1]);
                }
                H.set(n, n - 1, 0.0);
                H.set(n, n, 1.0);
                for (int i = n - 2; i >= 0; i--) {
                    double ra, sa, vr, vi;
                    ra = 0.0;
                    sa = 0.0;
                    for (int j = l; j <= n; j++) {
                        ra = ra + H.get(i, j) * H.get(j, n - 1);
                        sa = sa + H.get(i, j) * H.get(j, n);
                    }
                    w = H.get(i, i) - p;

                    if (e.get(i) < 0.0) {
                        z = w;
                        r = ra;
                        s = sa;
                    } else {
                        l = i;
                        if (e.get(i) == 0) {
                            double cdiv[] = cdiv(-ra, -sa, w, q);
                            H.set(i, n - 1, cdiv[0]);
                            H.set(i, n, cdiv[1]);
                        } else {

                            // Solve complex equations

                            x = H.get(i, i + 1);
                            y = H.get(i + 1, i);
                            vr = (d.get(i) - p) * (d.get(i) - p) 
                                 + e.get(i) * e.get(i) - q * q;
                            vi = (d.get(i) - p) * 2.0 * q;
                            if (vr == 0.0 & vi == 0.0) {
                                vr = eps
                                        * norm
                                        * (Math.abs(w) + Math.abs(q)
                                                + Math.abs(x) + Math.abs(y) + Math
                                                    .abs(z));
                            }
                            double cdiv[] = cdiv(x * r - z * ra + q * sa, 
                                                 x * s - z * sa - q * ra, vr, vi);
                            H.set(i, n - 1, cdiv[0]);
                            H.set(i, n, cdiv[1]);
                            if (Math.abs(x) > (Math.abs(z) + Math.abs(q))) {
                                H.set(i + 1, n - 1, (-ra - w 
                                             * H.get(i, n - 1) + q
                                             * H.get(i, n)) / x);
                                H.set(i + 1, n, (-sa - w 
                                             * H.get(i, n) - q 
                                             * H.get(i, n - 1)) / x);
                            } else {
                                cdiv = cdiv(-r - y 
                                     * H.get(i, n - 1), -s - y 
                                     * H.get(i, n), z, q);
                                H.set(i + 1, n - 1, cdiv[0]);
                                H.set(i + 1, n, cdiv[1]);
                            }
                        }

                        // Overflow control

                        t = Math.max(Math.abs(H.get(i, n - 1)), 
                                     Math.abs(H.get(i, n)));
                        if ((eps * t) * t > 1) {
                            for (int j = i; j <= n; j++) {
                                H.update(j, n - 1, Matrices.asDivFunction(t));
                                H.update(j, n, Matrices.asDivFunction(t));
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
                    V.set(i, j, H.get(i, j));
                }
            }
        }

        // Back transformation to get eigenvectors of original matrix

        for (int j = nn - 1; j >= low; j--) {
            for (int i = low; i <= high; i++) {
                z = 0.0;
                for (int k = low; k <= Math.min(j, high); k++) {
                    z = z + V.get(i, k) * H.get(k, j);
                }
                V.set(i, j, z);
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
