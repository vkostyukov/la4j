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

public class RawLUDecompositor extends AbstractDecompositor implements MatrixDecompositor {

    public RawLUDecompositor(Matrix matrix) {
        super(matrix);
    }

    @Override
    public Matrix[] decompose(Factory factory) {

        Matrix lu = matrix.copy();
        Matrix p = factory.createIdentityMatrix(lu.rows());

        for (int j = 0; j < lu.columns(); j++) {
            for (int i = 0; i < lu.rows(); i++) {

                int kmax = Math.min(i, j);

                double s = 0.0;
                for (int k = 0; k < kmax; k++) {
                    s += lu.get(i, k) * lu.get(k, j);
                }

                lu.update(i, j, Matrices.asMinusFunction(s));
            }

            int pivot = j;

            for (int i = j + 1; i < lu.rows(); i++) {
                if (Math.abs(lu.get(i, j)) > Math.abs(lu.get(pivot, j))) {
                    pivot = i;
                }
            }

            if (pivot != j) {
                lu.swapRows(pivot, j);
                p.swapRows(pivot, j);
            }

            if (j < lu.rows() && Math.abs(lu.get(j, j)) > Matrices.EPS) {
                for (int i = j + 1; i < lu.rows(); i++) {
                    lu.update(i, j, Matrices.asDivFunction(lu.get(j, j)));
                }
            }
        }

        return new Matrix[] { lu, p };
    }

    @Override
    public boolean applicableTo(Matrix matrix) {
        return matrix.rows() == matrix.columns();
    }
}
