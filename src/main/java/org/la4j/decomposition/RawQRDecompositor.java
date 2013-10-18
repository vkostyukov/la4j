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

public class RawQRDecompositor extends AbstractDecompositor implements MatrixDecompositor {

    public RawQRDecompositor(Matrix matrix) {
        super(matrix);
    }

    @Override
    public Matrix[] decompose(Factory factory) {

        Matrix qr = matrix.copy();
        Matrix r = factory.createSquareMatrix(qr.columns());

        for (int k = 0; k < qr.columns(); k++) {

            double norm = 0.0;

            for (int i = k; i < qr.rows(); i++) {
                norm = Math.hypot(norm, qr.get(i, k));
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

                    double acc = 0.0;

                    for (int i = k; i < qr.rows(); i++) {
                        acc += qr.get(i, k) * qr.get(i, j);
                    }

                    acc = -acc / qr.get(k, k);

                    for (int i = k; i < qr.rows(); i++) {
                        qr.update(i, j, Matrices.asPlusFunction(acc * qr.get(i, k)));
                    }
                }
            }

            r.set(k, k, -norm);
        }

        return new Matrix[] { qr, r };
    }

    @Override
    public boolean applicableTo(Matrix matrix) {
        return matrix.rows() >= matrix.columns();
    }
}
