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

public class SingularValueDecompositor implements MatrixDecompositor {

    @Override
    public Matrix[] decompose(Matrix matrix, Factory factory) {

        // AHTIUNG: this code derived from Jama

        if (matrix.rows() < matrix.columns()) {
            throw new IllegalArgumentException("Wrong matrix size: " 
                    + "rows < columns");
        }

        Matrix a = matrix.copy();

        int n = Math.min(a.rows(), a.columns());

        Matrix u = factory.createMatrix(a.rows(), n);
        Matrix s = factory.createMatrix(a.columns(), a.columns());
        Matrix v = factory.createMatrix(a.columns(), a.columns());

        double[] e = new double[a.columns()];
        double[] work = new double[a.rows()];

        int nct = Math.min(a.rows() - 1, a.columns());
        int nrt = Math.max(0, Math.min(a.columns() - 2, a.rows()));

        for (int k = 0; k < Math.max(nct, nrt); k++) {

            if (k < nct) {

                for (int i = k; i < a.rows(); i++) {
                    s.unsafe_set(k, k, hypot(s.unsafe_get(k, k), 
                    		a.unsafe_get(i, k)));
                }

                if (Math.abs(s.unsafe_get(k, k)) > Matrices.EPS) {

                    if (a.unsafe_get(k, k) < 0.0) {
                        s.unsafe_set(k, k, -s.unsafe_get(k, k));
                    }

                    for (int i = k; i < a.rows(); i++) {
                        a.unsafe_set(i, k, a.unsafe_get(i, k) 
                        		/ s.unsafe_get(k, k));
                    }

                    a.unsafe_set(k, k, a.unsafe_get(k, k) + 1.0);
                }

                s.unsafe_set(k, k, -s.unsafe_get(k, k));
            }

            for (int j = k + 1; j < a.columns(); j++) {

                if ((k < nct) & (Math.abs(s.unsafe_get(k, k)) > Matrices.EPS)) {

                    double t = 0;

                    for (int i = k; i < a.rows(); i++) {
                        t += a.unsafe_get(i, k) * a.unsafe_get(i, j);
                    }

                    t = -t / a.unsafe_get(k, k);

                    for (int i = k; i < a.rows(); i++) {
                        a.unsafe_set(i, j, a.unsafe_get(i, j) 
                        		+ (t * a.unsafe_get(i, k)));
                    }
                }

                e[j] = a.unsafe_get(k, j);
            }

            if (k < nct) {

                for (int i = k; i < a.rows(); i++) {
                    u.unsafe_set(i, k, a.unsafe_get(i, k));
                }

            }

            if (k < nrt) {

                e[k] = 0;

                for (int i = k + 1; i < a.columns(); i++) {
                    e[k] = hypot(e[k], e[i]);
                }

                if (Math.abs(e[k]) > Matrices.EPS) {

                    if (e[k + 1] < 0.0) {

                        e[k] = -e[k];
                    }

                    for (int i = k + 1; i < a.columns(); i++) {

                        e[i] /= e[k];
                    }

                    e[k + 1] += 1.0;
                }

                e[k] = -e[k];

                if ((k + 1 < a.rows()) & (Math.abs(e[k]) > Matrices.EPS)) {

                    for (int j = k + 1; j < a.columns(); j++) {
                        for (int i = k + 1; i < a.rows(); i++) {
                            work[i] += e[j] * a.unsafe_get(i, j);
                        }
                    }

                    for (int j = k + 1; j < a.columns(); j++) {

                        double t = -e[j] / e[k + 1];

                        for (int i = k + 1; i < a.rows(); i++) {
                            a.unsafe_set(i, j, a.unsafe_get(i, j) 
                            		+ (t * work[i]));
                        }
                    }
                }

                for (int i = k + 1; i < a.columns(); i++) {
                    v.unsafe_set(i, k, e[i]);
                }
            }
        }

        int p = Math.min(a.columns(), a.rows() + 1);

        if (nct < a.columns()) {
            s.unsafe_set(nct, nct, a.unsafe_get(nct, nct));
        }

        if (a.rows() < p) {
            s.unsafe_set(p - 1, p - 1, 0.0);
        }

        if (nrt + 1 < p) {
            e[nrt] = a.unsafe_get(nrt, p - 1);
        }

        e[p - 1] = 0.0;

        for (int j = nct; j < n; j++) {

            for (int i = 0; i < a.rows(); i++) {
                u.unsafe_set(i, j, 0.0);
            }

            u.unsafe_set(j, j, 1.0);
        }

        for (int k = nct - 1; k >= 0; k--) {

            if (Math.abs(s.unsafe_get(k, k)) > Matrices.EPS) {

                for (int j = k + 1; j < n; j++) {

                    double t = 0;
                    for (int i = k; i < a.rows(); i++) {
                        t += u.unsafe_get(i, k) * u.unsafe_get(i, j);
                    }

                    t = -t / u.unsafe_get(k, k);

                    for (int i = k; i < a.rows(); i++) {
                        u.unsafe_set(i, j, u.unsafe_get(i, j) 
                        		+ (t * u.unsafe_get(i, k)));
                    }
                }

                for (int i = k; i < a.rows(); i++) {
                    u.unsafe_set(i, k, -u.unsafe_get(i, k));
                }

                u.unsafe_set(k, k, u.unsafe_get(k, k) + 1.0);

                for (int i = 0; i < k - 1; i++) {
                    u.unsafe_set(i, k, 0.0);
                }

            } else {

                for (int i = 0; i < a.rows(); i++) {
                    u.unsafe_set(i, k, 0.0);
                }

                u.unsafe_set(k, k, 1.0);
            }
        }

        for (int k = n - 1; k >= 0; k--) {

            if ((k < nrt) & (Math.abs(e[k]) > Matrices.EPS)) {

                for (int j = k + 1; j < n; j++) {

                    double t = 0;

                    for (int i = k + 1; i < a.columns(); i++) {
                        t += v.unsafe_get(i, k) * v.unsafe_get(i, j);
                    }

                    t = -t / v.unsafe_get(k + 1, k);

                    for (int i = k + 1; i < a.columns(); i++) {
                        v.unsafe_set(i, j, v.unsafe_get(i, j) 
                        		+ (t * v.unsafe_get(i, k)));
                    }
                }
            }

            for (int i = 0; i < a.columns(); i++) {
                v.unsafe_set(i, k, 0.0);
            }

            v.unsafe_set(k, k, 1.0);
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

                if (Math.abs(e[k]) <= tiny
                        + eps
                        * (Math.abs(s.unsafe_get(k, k)) + Math
                                .abs(s.unsafe_get(k + 1, k + 1)))) {
                    e[k] = 0.0;
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

                    double t = (ks != p ? Math.abs(e[ks]) : 0.)
                            + (ks != k + 1 ? Math.abs(e[ks - 1]) : 0.);

                    if (Math.abs(s.unsafe_get(ks, ks)) <= tiny + eps * t) {
                        s.unsafe_set(ks, ks, 0.0);
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
                double f = e[p - 2];
                e[p - 2] = 0.0;

                for (int j = p - 2; j >= k; j--) {

                    double t = hypot(s.unsafe_get(j, j), f);
                    double cs = s.unsafe_get(j, j) / t;
                    double sn = f / t;

                    s.unsafe_set(j, j, t);

                    if (j != k) {
                        f = -sn * e[j - 1];
                        e[j - 1] = cs * e[j - 1];
                    }

                    for (int i = 0; i < a.columns(); i++) {
                        t = cs * v.unsafe_get(i, j) + sn 
                        		* v.unsafe_get(i, p - 1);
                        v.unsafe_set(i, p - 1,
                                -sn * v.unsafe_get(i, j) 
                                + cs * v.unsafe_get(i, p - 1));
                        v.unsafe_set(i, j, t);
                    }
                }
            }
                break;

            case 2: {
                double f = e[k - 1];
                e[k - 1] = 0.0;

                for (int j = k; j < p; j++) {

                    double t = hypot(s.unsafe_get(j, j), f);
                    double cs = s.unsafe_get(j, j) / t;
                    double sn = f / t;

                    s.unsafe_set(j, j, t);
                    f = -sn * e[j];
                    e[j] = cs * e[j];

                    for (int i = 0; i < a.rows(); i++) {
                        t = cs * u.unsafe_get(i, j) 
                        		+ sn * u.unsafe_get(i, k - 1);
                        u.unsafe_set(i, k - 1,
                                -sn * u.unsafe_get(i, j) 
                                + cs * u.unsafe_get(i, k - 1));
                        u.unsafe_set(i, j, t);
                    }
                }
            }
                break;

            case 3: {

                double scale = Math.max(
                        Math.max(Math.max(
                                Math.max(Math.abs(s.unsafe_get(p - 1, p - 1)),
                                        Math.abs(s.unsafe_get(p - 2, p - 2))),
                                    Math.abs(e[p - 2])), 
                                Math.abs(s.unsafe_get(k, k))),
                        Math.abs(e[k]));

                double sp = s.unsafe_get(p - 1, p - 1) / scale;
                double spm1 = s.unsafe_get(p - 2, p - 2) / scale;
                double epm1 = e[p - 2] / scale;
                double sk = s.unsafe_get(k, k) / scale;
                double ek = e[k] / scale;
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
                        e[j - 1] = t;
                    }

                    f = cs * s.unsafe_get(j, j) + sn * e[j];
                    e[j] = cs * e[j] - sn * s.unsafe_get(j, j);
                    g = sn * s.unsafe_get(j + 1, j + 1);
                    s.unsafe_set(j + 1, j + 1, 
                    		cs * s.unsafe_get(j + 1, j + 1));

                    for (int i = 0; i < a.columns(); i++) {
                        t = cs * v.unsafe_get(i, j) 
                        		+ sn * v.unsafe_get(i, j + 1);
                        v.unsafe_set(i, j + 1,
                                -sn * v.unsafe_get(i, j) 
                                + cs * v.unsafe_get(i, j + 1));
                        v.unsafe_set(i, j, t);
                    }

                    t = hypot(f, g);
                    cs = f / t;
                    sn = g / t;
                    s.unsafe_set(j, j, t);
                    f = cs * e[j] + sn * s.unsafe_get(j + 1, j + 1);
                    s.unsafe_set(j + 1, j + 1, 
                    		-sn * e[j] + cs * s.unsafe_get(j + 1, j + 1));
                    g = sn * e[j + 1];
                    e[j + 1] = cs * e[j + 1];

                    if (j < a.rows() - 1) {
                        for (int i = 0; i < a.rows(); i++) {
                            t = cs * u.unsafe_get(i, j) 
                            		+ sn * u.unsafe_get(i, j + 1);
                            u.unsafe_set(i, j + 1,
                                    -sn * u.unsafe_get(i, j) 
                                    + cs * u.unsafe_get(i, j + 1));
                            u.unsafe_set(i, j, t);
                        }
                    }
                }

                e[p - 2] = f;
                iter = iter + 1;
            }
                break;

            case 4: {

                if (s.unsafe_get(k, k) <= 0.0) {
                    s.unsafe_set(k, k, 
                    		(s.unsafe_get(k, k) < 0.0 ? -s.unsafe_get(k, k) 
                    								  : 0.0));
                    for (int i = 0; i <= pp; i++) {
                        v.unsafe_set(i, k, -v.unsafe_get(i, k));
                    }
                }

                while (k < pp) {

                    if (s.unsafe_get(k, k) >= s.unsafe_get(k + 1, k + 1)) {
                        break;
                    }

                    double t = s.unsafe_get(k, k);
                    s.unsafe_set(k, k, s.unsafe_get(k + 1, k + 1));
                    s.unsafe_set(k + 1, k + 1, t);

                    if (k < a.columns() - 1) {
                        for (int i = 0; i < a.columns(); i++) {
                            t = v.unsafe_get(i, k + 1);
                            v.unsafe_set(i, k + 1, v.unsafe_get(i, k));
                            v.unsafe_set(i, k, t);
                        }
                    }

                    if (k < a.rows() - 1) {
                        for (int i = 0; i < a.rows(); i++) {
                            t = u.unsafe_get(i, k + 1);
                            u.unsafe_set(i, k + 1, u.unsafe_get(i, k));
                            u.unsafe_set(i, k, t);
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

        return new Matrix[] { u, s, v };
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
}
