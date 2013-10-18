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
 * Contributor(s): Julia Kostyukova
 * 
 */

package org.la4j.decomposition;

import org.la4j.factory.Factory;
import org.la4j.matrix.Matrices;
import org.la4j.matrix.Matrix;
import org.la4j.vector.Vector;
import org.la4j.vector.Vectors;

/**
 * This class represents singular value decomposition of matrices. More details
 * <p><a href="http://mathworld.wolfram.com/SingularValueDecomposition.html">
 * here.</a> </p>
 */
public class SingularValueDecompositor extends AbstractDecompositor implements MatrixDecompositor {

    public SingularValueDecompositor(Matrix matrix) {
        super(matrix);
    }

    /**
     * Returns the result of Singular Value decomposition of given matrix
     * <p>
     * See <a
     * href="http://mathworld.wolfram.com/SingularValueDecomposition.html">
     * http://mathworld.wolfram.com/SingularValueDecomposition.html</a> for more
     * details.
     * </p>
     * 
     * @param factory
     * @return { U, D, V }
     */
    @Override
    public Matrix[] decompose(Factory factory) {

        // AHTIUNG: this code derived from Jama

        Matrix a = matrix.copy();

        int n = Math.min(a.rows(), a.columns());

        Matrix u = factory.createMatrix(a.rows(), n);
        Matrix s = factory.createMatrix(a.columns(), a.columns());
        Matrix v = factory.createMatrix(a.columns(), a.columns());

        Vector e = factory.createVector(a.columns());
        Vector work = factory.createVector(a.rows());

        int nct = Math.min(a.rows() - 1, a.columns());
        int nrt = Math.max(0, Math.min(a.columns() - 2, a.rows()));

        for (int k = 0; k < Math.max(nct, nrt); k++) {

            if (k < nct) {

                s.set(k, k, 0.0);

                for (int i = k; i < a.rows(); i++) {
                    s.set(k, k, hypot(s.get(k, k), a.get(i, k)));
                }

                if (Math.abs(s.get(k, k)) > Matrices.EPS) {

                    if (a.get(k, k) < 0.0) {
                        s.update(k, k, Matrices.INV_FUNCTION);
                    }

                    double skk = s.get(k, k);
                    for (int i = k; i < a.rows(); i++) {
                        a.update(i, k, Matrices.asDivFunction(skk));
                    }

                    a.update(k, k, Matrices.INC_FUNCTION);
                }

                s.update(k, k, Matrices.INV_FUNCTION);
            }

            for (int j = k + 1; j < a.columns(); j++) {

                if ((k < nct) & (Math.abs(s.get(k, k)) > Matrices.EPS)) {

                    double t = 0;

                    for (int i = k; i < a.rows(); i++) {
                        t += a.get(i, k) * a.get(i, j);
                    }

                    t = -t / a.get(k, k);

                    for (int i = k; i < a.rows(); i++) {
                        a.update(i, j, Matrices.asPlusFunction(t * a.get(i, k)));
                    }
                }

                e.set(j, a.get(k, j));
            }

            if (k < nct) {

                for (int i = k; i < a.rows(); i++) {
                    u.set(i, k, a.get(i, k));
                }

            }

            if (k < nrt) {

                e.set(k, 0);

                for (int i = k + 1; i < a.columns(); i++) {
                    e.set(k, hypot(e.get(k), e.get(i)));
                }

                if (Math.abs(e.get(k)) > Matrices.EPS) {

                    if (e.get(k + 1) < 0.0) {

                        e.update(k, Vectors.INV_FUNCTION);
                    }

                    double ek = e.get(k);
                    for (int i = k + 1; i < a.columns(); i++) {
                        e.update(i, Vectors.asDivFunction(ek));
                    }

                    e.update(k + 1, Vectors.INC_FUNCTION);
                }

                e.update(k, Vectors.INV_FUNCTION);

                if ((k + 1 < a.rows()) && (Math.abs(e.get(k)) > Matrices.EPS)) {

                    for (int i = k + 1; i < a.rows(); i++) {
                        work.set(i, 0.0);
                    }

                    for (int j = k + 1; j < a.columns(); j++) {
                        for (int i = k + 1; i < a.rows(); i++) {
                            work.update(i, Vectors.asPlusFunction(e.get(j) * 
                                        a.get(i, j)));
                        }
                    }

                    for (int j = k + 1; j < a.columns(); j++) {

                        double t = -e.get(j) / e.get(k + 1);

                        for (int i = k + 1; i < a.rows(); i++) {
                            a.update(i, j, Matrices.asPlusFunction(t * 
                                     work.get(i)));
                        }
                    }
                }

                for (int i = k + 1; i < a.columns(); i++) {
                    v.set(i, k, e.get(i));
                }
            }
        }

        int p = Math.min(a.columns(), a.rows() + 1);

        if (nct < a.columns()) {
            s.set(nct, nct, a.get(nct, nct));
        }

        if (a.rows() < p) {
            s.set(p - 1, p - 1, 0.0);
        }

        if (nrt + 1 < p) {
            e.set(nrt, a.get(nrt, p - 1));
        }

        e.set(p - 1, 0.0);

        for (int j = nct; j < n; j++) {

            for (int i = 0; i < a.rows(); i++) {
                u.set(i, j, 0.0);
            }

            u.set(j, j, 1.0);
        }

        for (int k = nct - 1; k >= 0; k--) {

            if (Math.abs(s.get(k, k)) > Matrices.EPS) {

                for (int j = k + 1; j < n; j++) {

                    double t = 0;
                    for (int i = k; i < a.rows(); i++) {
                        t += u.get(i, k) * u.get(i, j);
                    }

                    t = -t / u.get(k, k);

                    for (int i = k; i < a.rows(); i++) {
                        u.update(i, j, Matrices.asPlusFunction(t * u.get(i, k)));
                    }
                }

                for (int i = k; i < a.rows(); i++) {
                    u.update(i, k, Matrices.INV_FUNCTION);
                }

                u.update(k, k, Matrices.INC_FUNCTION);

                for (int i = 0; i < k - 1; i++) {
                    u.set(i, k, 0.0);
                }

            } else {

                for (int i = 0; i < a.rows(); i++) {
                    u.set(i, k, 0.0);
                }

                u.set(k, k, 1.0);
            }
        }

        for (int k = n - 1; k >= 0; k--) {

            if ((k < nrt) & (Math.abs(e.get(k)) > Matrices.EPS)) {

                for (int j = k + 1; j < n; j++) {

                    double t = 0;

                    for (int i = k + 1; i < a.columns(); i++) {
                        t += v.get(i, k) * v.get(i, j);
                    }

                    t = -t / v.get(k + 1, k);

                    for (int i = k + 1; i < a.columns(); i++) {
                        v.update(i, j, Matrices.asPlusFunction(t * v.get(i, k)));
                    }
                }
            }

            for (int i = 0; i < a.columns(); i++) {
                v.set(i, k, 0.0);
            }

            v.set(k, k, 1.0);
        }

        int pp = p - 1;
        int iter = 0;
        double eps = Math.pow(2.0, -52.0);
        double tiny = Math.pow(2.0, -966.0);

        while (p > 0) {

            int k, kase;

            for (k = p - 2; k >= -1; k--) {
                if (k == -1)
                    break;

                if (Math.abs(e.get(k)) <= tiny + eps * 
                        (Math.abs(s.get(k, k)) + Math.abs(s.get(k + 1, k + 1)))) {

                    e.set(k, 0.0);
                    break;
                }
            }

            if (k == p - 2) {

                kase = 4;

            } else {

                int ks;

                for (ks = p - 1; ks >= k; ks--) {

                    if (ks == k)
                        break;

                    double t = (ks != p ? Math.abs(e.get(ks)) : 0.)
                            + (ks != k + 1 ? Math.abs(e.get(ks - 1)) 
                            			   : 0.);

                    if (Math.abs(s.get(ks, ks)) <= tiny + eps * t) {
                        s.set(ks, ks, 0.0);
                        break;
                    }
                }

                if (ks == k) {
                    kase = 3;
                } else if (ks == p - 1) {
                    kase = 1;
                } else {
                    kase = 2;
                    k = ks;
                }
            }

            k++;

            switch (kase) {

            case 1: {
                double f = e.get(p - 2);
                e.set(p - 2, 0.0);

                for (int j = p - 2; j >= k; j--) {

                    double t = hypot(s.get(j, j), f);
                    double cs = s.get(j, j) / t;
                    double sn = f / t;

                    s.set(j, j, t);

                    if (j != k) {
                        f = -sn * e.get(j - 1);
                        e.set(j - 1, cs * e.get(j - 1));
                    }

                    for (int i = 0; i < a.columns(); i++) {
                        t = cs * v.get(i, j) + sn 
                        		* v.get(i, p - 1);
                        v.set(i, p - 1,
                                -sn * v.get(i, j) 
                                + cs * v.get(i, p - 1));
                        v.set(i, j, t);
                    }
                }
            }
                break;

            case 2: {
                double f = e.get(k - 1);
                e.set(k - 1, 0.0);

                for (int j = k; j < p; j++) {

                    double t = hypot(s.get(j, j), f);
                    double cs = s.get(j, j) / t;
                    double sn = f / t;

                    s.set(j, j, t);
                    f = -sn * e.get(j);
                    e.set(j, cs * e.get(j));

                    for (int i = 0; i < a.rows(); i++) {
                        t = cs * u.get(i, j) 
                        		+ sn * u.get(i, k - 1);
                        u.set(i, k - 1,
                                -sn * u.get(i, j) 
                                + cs * u.get(i, k - 1));
                        u.set(i, j, t);
                    }
                }
            }
                break;

            case 3: {

                double scale = Math.max(
                        Math.max(Math.max(
                                Math.max(Math.abs(s.get(p - 1, p - 1)),
                                        Math.abs(s.get(p - 2, p - 2))),
                                    Math.abs(e.get(p - 2))), 
                                Math.abs(s.get(k, k))),
                        Math.abs(e.get(k)));

                double sp = s.get(p - 1, p - 1) / scale;
                double spm1 = s.get(p - 2, p - 2) / scale;
                double epm1 = e.get(p - 2) / scale;
                double sk = s.get(k, k) / scale;
                double ek = e.get(k) / scale;
                double b = ((spm1 + sp) * (spm1 - sp) + epm1 * epm1) / 2.0;
                double c = (sp * epm1) * (sp * epm1);
                double shift = 0.0;

                if ((b != 0.0) | (c != 0.0)) {
                    shift = Math.sqrt(b * b + c);
                    if (b < 0.0) {
                        shift = -shift;
                    }
                    shift = c / (b + shift);
                }

                double f = (sk + sp) * (sk - sp) + shift;
                double g = sk * ek;

                for (int j = k; j < p - 1; j++) {
                    double t = hypot(f, g);
                    double cs = f / t;
                    double sn = g / t;

                    if (j != k) {
                        e.set(j - 1, t);
                    }

                    f = cs * s.get(j, j) + sn * e.get(j);
                    e.set(j, 
                    		cs * e.get(j) - sn * s.get(j, j));
                    g = sn * s.get(j + 1, j + 1);
                    s.set(j + 1, j + 1, 
                    		cs * s.get(j + 1, j + 1));

                    for (int i = 0; i < a.columns(); i++) {
                        t = cs * v.get(i, j) 
                        		+ sn * v.get(i, j + 1);
                        v.set(i, j + 1,
                                -sn * v.get(i, j) 
                                + cs * v.get(i, j + 1));
                        v.set(i, j, t);
                    }

                    t = hypot(f, g);
                    cs = f / t;
                    sn = g / t;
                    s.set(j, j, t);
                    f = cs * e.get(j) + sn * s.get(j + 1, j + 1);
                    s.set(j + 1, j + 1, 
                            -sn * e.get(j) 
                            + cs * s.get(j + 1, j + 1));
                    g = sn * e.get(j + 1);
                    e.update(j + 1, Vectors.asMulFunction(cs));

                    if (j < a.rows() - 1) {
                        for (int i = 0; i < a.rows(); i++) {
                            t = cs * u.get(i, j) 
                                    + sn * u.get(i, j + 1);
                            u.set(i, j + 1,
                                    -sn * u.get(i, j) 
                                    + cs * u.get(i, j + 1));
                            u.set(i, j, t);
                        }
                    }
                }

                e.set(p - 2, f);
                iter = iter + 1;
            }
                break;

            case 4: {

                if (s.get(k, k) <= 0.0) {
                    s.set(k, k, s.get(k, k) < 0.0 ? -s.get(k, k) : 0.0);
                    for (int i = 0; i <= pp; i++) {
                        v.update(i,  k, Matrices.INV_FUNCTION);
                    }
                }

                while (k < pp) {

                    if (s.get(k, k) >= s.get(k + 1, k + 1)) {
                        break;
                    }

                    double t = s.get(k, k);
                    s.set(k, k, s.get(k + 1, k + 1));
                    s.set(k + 1, k + 1, t);

                    if (k < a.columns() - 1) {
                        for (int i = 0; i < a.columns(); i++) {
                            t = v.get(i, k + 1);
                            v.set(i, k + 1, v.get(i, k));
                            v.set(i, k, t);
                        }
                    }

                    if (k < a.rows() - 1) {
                        for (int i = 0; i < a.rows(); i++) {
                            t = u.get(i, k + 1);
                            u.set(i, k + 1, u.get(i, k));
                            u.set(i, k, t);
                        }
                    }

                    k++;
                }

                iter = 0;
                p--;
            }
                break;
            }
        }

        // TODO:
        // We need to change the logic of this code in order do not use resize
        // It is also not a good idea to use SVN for [m < n] matrices
        // We need to change it in further releases
        return new Matrix[] { u, s.resize(n, a.columns(), factory), v };
    }

    private double hypot(double a, double b) {

        double result;

        if (Math.abs(a) > Math.abs(b)) {
            result = b / a;
            result = Math.abs(a) * Math.sqrt(1 + result * result);
        } else if (b != 0) {
            result = a / b;
            result = Math.abs(b) * Math.sqrt(1 + result * result);
        } else {
            result = 0.0;
        }

        return result;
    }

    @Override
    public boolean applicableTo(Matrix matrix) {
        return true;
    }
}
