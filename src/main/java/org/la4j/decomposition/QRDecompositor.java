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

/**
 * This class represents QR decomposition of matrices. More details
 * <p>
 * <a href="http://mathworld.wolfram.com/QRDecomposition.html"> here.</a>
 * </p>
 */
public class QRDecompositor extends RawQRDecompositor implements MatrixDecompositor {

    public QRDecompositor(Matrix matrix) {
        super(matrix);
    }

    /**
     * Returns the result of QR decomposition of given matrix
     * <p>
     * See <a href="http://mathworld.wolfram.com/QRDecomposition.html">
     * http://mathworld.wolfram.com/QRDecomposition.html</a> for more details.
     * </p>
     * 
     * @param factory
     * @return { Q, R }
     */
    @Override
    public Matrix[] decompose(Factory factory) {

        Matrix[] qrr = super.decompose(factory);
        Matrix qr = qrr[0];
        Matrix r = qrr[1];

        Matrix q = qr.blank(factory);

        for (int k = q.columns() - 1; k >= 0; k--) {

            q.set(k, k, 1.0);

            for (int j = k; j < q.columns(); j++) {

                if (Math.abs(qr.get(k, k)) > Matrices.EPS) {

                    double acc = 0.0;

                    for (int i = k; i < q.rows(); i++) {
                        acc += qr.get(i, k) * q.get(i, j);
                    }

                    acc = -acc / qr.get(k, k);

                    for (int i = k; i < q.rows(); i++) {
                        q.update(i, j, Matrices.asPlusFunction(acc *qr.get(i, k)));
                    }
                }
            }
        }

        for (int i = 0; i < r.rows(); i++) {
            for (int j = i + 1; j < r.columns(); j++) {
                r.set(i, j, qr.get(i, j));
            }
        }

        return new Matrix[] { q, r };
    }
}
