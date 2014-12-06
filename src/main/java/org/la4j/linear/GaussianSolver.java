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

package org.la4j.linear;

import org.la4j.factory.Factory;
import org.la4j.matrix.Matrices;
import org.la4j.matrix.Matrix;
import org.la4j.vector.Vector;

/**
 * This class represents Gaussian method for solving linear systems. More details
 * <p>
 * <a href="http://mathworld.wolfram.com/GaussianElimination.html"> here.</a>
 * </p>
 */
public class GaussianSolver extends AbstractSolver implements LinearSystemSolver {

    private static final long serialVersionUID = 4071505L;

    // augmented matrix
    private final Matrix aa;

    public GaussianSolver(Matrix a) {
        super(a);

        this.aa = a.resizeColumns(unknowns() + 1);
    }

    @Override
    public Vector solve(Vector b, Factory factory) {
        ensureRHSIsCorrect(b);

        // extend augmented matrix
        aa.setColumn(unknowns(), b);

        // the 1st phase
        triangularizeWithPivoting(aa);

        if (Math.abs(aa.diagonalProduct()) < Matrices.EPS) {
            fail("This system is singular.");
        }

        // the 2nd phase
        Vector x = factory.createVector(aa.columns() - 1);
        backSubstitution(aa, x);

        return x;
    }

    private void triangularizeWithPivoting(Matrix matrix) {

        for (int i = 0; i + 1 < matrix.rows(); i++) {

            int maxIndex = i;
            double maxItem = Math.abs(matrix.get(i, i));

            for (int k = i + 1; k < matrix.rows(); k++) {
                double value = Math.abs(matrix.get(k, i));
                if (value > maxItem) {
                    maxItem = value;
                    maxIndex = k;
                }
            }

            if (maxItem == 0.0) {
                throw new IllegalArgumentException("This system can't be solved.");
            }

            if (maxIndex > i) {
                matrix.swapRows(maxIndex, i);
            }

            for (int j = i + 1; j < matrix.rows(); j++) {

                double c = matrix.get(j, i) / matrix.get(i, i);
                matrix.set(j, i, 0.0);

                for (int k = i + 1; k < matrix.columns(); k++) {
                    matrix.update(j, k, Matrices.asMinusFunction(matrix.get(i, k) * c));
                }
            }
        }
    }

    private void backSubstitution(Matrix matrix, Vector result) {

        for (int i = matrix.rows() - 1; i >= 0; i--) {

            double acc = 0.0;
            for (int j = i + 1; j < matrix.columns() - 1; j++) {
                acc += result.get(j) * matrix.get(i, j);
            }

            result.set(i, (matrix.get(i, matrix.columns() - 1) - acc)
                       / matrix.get(i, i));
        }
    }

    @Override
    public boolean applicableTo(Matrix matrix) {
        return matrix.rows() == matrix.columns();
    }
}
