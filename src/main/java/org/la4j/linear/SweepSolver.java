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
import org.la4j.vector.Vectors;

/**
 * This class represents <a
 * href="http://en.wikipedia.org/wiki/Tridiagonal_matrix_algorithm"> Sweep (or
 * Tridiagonal matrix, or Thomas) method </a> for solving linear systems.
 */
public class SweepSolver implements LinearSystemSolver {

    private static final long serialVersionUID = 4071505L;

    /**
     * Returns the solution for the given linear system
     * <p>
     * See <a href="http://en.wikipedia.org/wiki/Tridiagonal_matrix_algorithm">
     * http://en.wikipedia.org/wiki/Tridiagonal_matrix_algorithm</a> for more
     * details.
     * </p>
     * 
     * @param linearSystem
     * @param factory
     * @return vector
     */
    @Override
    public Vector solve(LinearSystem linearSystem, Factory factory) {

        if (!suitableFor(linearSystem)) {
            throw new IllegalArgumentException("This system can't be solved with Sweep solver.");
        }

        Matrix a = linearSystem.coefficientsMatrix().copy();
        Vector b = linearSystem.rightHandVector().copy();

        Vector x = factory.createVector(a.columns());

        for (int i = 0; i < a.rows() - 1; i++) {

            double maxItem = Math.abs(a.get(i, i));
            int maxIndex = i;

            for (int j = i + 1; j < a.columns(); j++) {
                double value = Math.abs(a.get(j, i));
                if (value > maxItem) {
                    maxItem = value;
                    maxIndex = j;
                }
            }

            if (maxIndex != i) {
                a.swapRows(maxIndex, i);
                b.swap(i, maxIndex);
            }

            for (int j = i + 1; j < a.columns(); j++) {

                double c = a.get(j, i) / a.get(i, i);
                for (int k = i; k < a.columns(); k++) {
                    a.update(j, k, Matrices.asMinusFunction(a.get(i, k) * c));
                }

                b.update(j, Vectors.asMinusFunction(b.get(i) * c));
            }
        }

        for (int i = a.rows() - 1; i >= 0; i--) {

            double summand = 0.0;

            for (int j = i + 1; j < a.columns(); j++) {
                summand += a.get(i, j) * x.get(j);
            }

            x.set(i, (b.get(i) - summand) / a.get(i, i));
        }

        return x;
    }

    /**
     * Checks whether this linear system can be solved by Sweep solver
     * @param linearSystem
     * @return <code>true</code> if given linear system can be solved by Sweep solver
     */
    @Override
    public boolean suitableFor(LinearSystem linearSystem) {
        return linearSystem.coefficientsMatrix().is(Matrices.TRIDIAGONAL_MATRIX);
    }
}
