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
import org.la4j.matrix.functor.MatrixFunction;
import org.la4j.vector.Vector;

/**  
 * This class represents
 * <a href="http://mathworld.wolfram.com/Gauss-SeidelMethod.html"> Seidel method
 * </a> for solving linear systems.
 */
public class SeidelSolver implements LinearSystemSolver {

    private static final long serialVersionUID = 4071505L;

    private final static int MAX_ITERATIONS = 1000000;

    /**
     * Returns the solution for the given linear system
     * <p>
     * See <a href="http://mathworld.wolfram.com/Gauss-SeidelMethod.html">
     * http://mathworld.wolfram.com/Gauss-SeidelMethod.html</a> for more
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
            throw new IllegalArgumentException("This system can't be solved with Seidel method.");
        }

        Matrix a = linearSystem.coefficientsMatrix().copy();
        Vector b = linearSystem.rightHandVector();

        for (int i = 0; i < a.rows(); i++) {
            MatrixFunction divider = Matrices.asDivFunction(a.get(i, i));
            for (int j = 0; j < a.columns(); j++) {
                if (i != j) {
                    a.update(i, j, divider);
                }
            }
        }

        Vector current = factory.createVector(linearSystem.variables());

        // TODO: we can peel out the iterations
        int iteration = 0;

        while (iteration < MAX_ITERATIONS && !linearSystem.isSolution(current)) {

            for (int i = 0; i < a.rows(); i++) {

                double summand = b.get(i) / a.get(i, i);
                for (int j = 0; j < a.columns(); j++) {
                    if (i != j) {
                        summand -= a.get(i, j) * current.get(j);
                    }
                }

                current.set(i, summand);
            }

            iteration++;
        }

        return current;
    }

    /**
     * Checks whether this linear system can be solved by Seidel solver
     * @param linearSystem
     * @return <code>true</code> if given linear system can be solved by Seidel solver
     */
    @Override
    public boolean suitableFor(LinearSystem linearSystem) {
        return linearSystem.coefficientsMatrix().is(Matrices.DIAGONALLY_DOMINANT_MATRIX);
    }
}
