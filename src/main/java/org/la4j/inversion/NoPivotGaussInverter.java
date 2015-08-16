/*
 * Copyright 2011-2015, by Vladimir Kostyukov and Contributors.
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

package org.la4j.inversion;

import org.la4j.Matrix;

public class NoPivotGaussInverter implements MatrixInverter {

    private final Matrix matrix;

    public NoPivotGaussInverter(final Matrix matrix) {
        this.matrix = matrix;
    }

    @Override
    public Matrix inverse() {
        if (matrix.rows() != matrix.columns()) {
            throw new IllegalArgumentException("Wrong matrix size: " + "rows != columns");
        }

        double var;

        final Matrix result = matrix.copy();

        for (int k = 0; k < matrix.rows(); k++) {
            final double diagonalTerm = result.get(k, k);

            if (Math.abs(diagonalTerm) <= Double.MIN_VALUE) {
                throw new IllegalArgumentException(
                        "This matrix cannot be inverted with a non-pivoting Gauss elimination method.");
            }

            var = 1.0 / result.get(k, k);
            result.set(k, k, 1.0);

            for (int j = 0; j < matrix.rows(); j++) {
                result.set(k, j, result.get(k, j) * var);
            }

            for (int i = 0; i < matrix.rows(); i++) {
                if (i == k) {
                    continue;
                }

                var = result.get(i, k);
                result.set(i, k, 0.0);
                for (int j = 0; j < matrix.rows(); j++) {
                    result.set(i, j, result.get(i, j) - var * result.get(k, j));
                }
            }
        }

        return result;
    }

    @Override
    public Matrix self() {
        return matrix;
    }
}
