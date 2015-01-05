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
 * Contributor(s): Miron Aseev
 * 
 */

package org.la4j.decomposition;

import org.la4j.Matrices;
import org.la4j.Matrix;

/**
 * This class represents Cholesky decomposition of matrices. More details
 * <p>
 * <a href="http://mathworld.wolfram.com/CholeskyDecomposition.html"> here</a>
 * </p>
 */
public class CholeskyDecompositor extends AbstractDecompositor implements MatrixDecompositor {

    public CholeskyDecompositor(Matrix matrix) {
        super(matrix);
    }

    /**
     * Returns the result of Cholesky decomposition of given matrix
     * <p>
     * See <a href="http://mathworld.wolfram.com/CholeskyDecomposition.html">
     * http://mathworld.wolfram.com/CholeskyDecomposition.html</a> for more
     * details.
     * </p>
     *
     * @return { L }
     */
    @Override
    public Matrix[] decompose() {

        Matrix l = matrix.blankOfShape(matrix.rows(), matrix.rows());

        for (int j = 0; j < l.rows(); j++) {

            double d = 0.0;

            for (int k = 0; k < j; k++) {

                double s = 0.0;

                for (int i = 0; i < k; i++) {
                    s += l.get(k, i) * l.get(j, i);
                }

                s = (matrix.get(j, k) - s) / l.get(k, k);

                l.set(j, k, s);

                d = d + s * s;
            }

            d = matrix.get(j, j) - d;

            l.set(j, j, Math.sqrt(Math.max(d, 0.0)));

            for (int k = j + 1; k < l.rows(); k++) {
                l.set(j, k, 0.0);
            }
        }

        return new Matrix[] { l };
    }

    @Override
    public boolean applicableTo(Matrix matrix) {
        return matrix.rows() == matrix.columns() &&
                matrix.is(Matrices.SYMMETRIC_MATRIX) && matrix.is(Matrices.POSITIVE_DEFINITE_MATRIX);
    }
}
