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

package org.la4j.inversion;

import org.la4j.LinearAlgebra;
import org.la4j.factory.Factory;
import org.la4j.linear.LinearSystemSolver;
import org.la4j.matrix.Matrix;
import org.la4j.vector.Vector;

public class GaussJordanInverter implements MatrixInverter {

    private final Matrix matrix;

    public GaussJordanInverter(Matrix matrix) {
        this.matrix = matrix;
    }

    @Override
    public Matrix inverse(Factory factory) {

        if (matrix.rows() != matrix.columns()) {
            throw new IllegalArgumentException("Wrong matrix size: "
                    + "rows != columns");
        }

        Matrix result = factory.createMatrix(matrix.rows(), matrix.columns());

        for (int i = 0; i < matrix.rows(); i++) {

            Vector b = factory.createVector(matrix.rows());
            b.set(i, 1.0);

            try {
                LinearSystemSolver solver = matrix.withSolver(LinearAlgebra.GAUSSIAN);
                Vector x = solver.solve(b, factory);
                result.setColumn(i, x);
            } catch (IllegalArgumentException ex) {
                throw new IllegalArgumentException("This matrix is not invertible.");
            }
        }

        return result;
    }

    @Override
    public Matrix inverse() {
        return inverse(matrix.factory());
    }

    @Override
    public Matrix self() {
        return matrix;
    }
}
