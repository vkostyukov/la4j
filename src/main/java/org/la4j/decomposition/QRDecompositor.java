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

/**
 * This class represents QR decomposition of matrices. More details
 * <p>
 * <a href="http://mathworld.wolfram.com/QRDecomposition.html"> here.</a>
 * </p>
 */
public class QRDecompositor implements MatrixDecompositor {

    /**
     * Returns the result of QR decomposition of given matrix
     * <p>
     * See <a href="http://mathworld.wolfram.com/QRDecomposition.html">
     * http://mathworld.wolfram.com/QRDecomposition.html</a> for more details.
     * </p>
     * 
     * @param matrix
     * @param factory
     * @return { Q, R }
     */
    @Override
    public Matrix[] decompose(Matrix matrix, Factory factory) {

        if (matrix.rows() < matrix.columns()) {
            throw new IllegalArgumentException("Wrong matrix size: " 
                    +  "rows < columns");
        }

        Matrix qr = matrix.copy();

        Vector rdiag = factory.createVector(qr.columns());

        for (int k = 0; k < qr.columns(); k++) {

            double norm = 0.0;

            for (int i = k; i < qr.rows(); i++) {
                norm = hypot(norm, qr.get(i, k));
            }

            if (Math.abs(norm) > Matrices.EPS) {

                if (qr.get(k, k) < 0.0) {
                    norm = -norm;
                }

                for (int i = k; i < qr.rows(); i++) {
                    qr.update(i, k, Matrices.asDivFunction(norm));
                }

                qr.update(k, k, Matrices.INC_FUNCTION);

                for (int j = k + 1; j < qr.columns(); j++) {

                    double summand = 0.0;

                    for (int i = k; i < qr.rows(); i++) {
                        summand += qr.get(i, k) * qr.get(i, j);
                    }

                    summand = -summand / qr.get(k, k);

                    for (int i = k; i < qr.rows(); i++) {
                        qr.update(i, j, Matrices.asPlusFunction(summand * 
                                  qr.get(i, k)));
                    }
                }
            }

            rdiag.set(k, norm);
        }

        Matrix q = qr.blank(factory);

        for (int k = q.columns() - 1; k >= 0; k--) {

            q.set(k, k, 1.0);

            for (int j = k; j < q.columns(); j++) {

                if (Math.abs(qr.get(k, k)) > Matrices.EPS) {

                    double summand = 0.0;

                    for (int i = k; i < q.rows(); i++) {
                        summand += qr.get(i, k) * q.get(i, j);
                    }

                    summand = -summand / qr.get(k, k);

                    for (int i = k; i < q.rows(); i++) {
                        q.update(i, j, Matrices.asPlusFunction(summand * 
                                 qr.get(i, k)));
                    }
                }
            }
        }

        Matrix r = qr.blank(factory);

        for (int i = 0; i < r.columns(); i++) {
            for (int j = i; j < r.columns(); j++) {
                if (i < j) {
                    r.set(i, j, -qr.get(i, j));
                } else if (i == j) {
                    r.set(i, j, rdiag.get(i));
                }
            }
        }

        // TODO: Issue 13

        return new Matrix[] { q.multiply(-1), r };
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
