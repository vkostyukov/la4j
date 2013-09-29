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
 * This class represents Jacobi method for solving linear systems. More details
 * <p>
 * <a href="http://mathworld.wolfram.com/JacobiMethod.html"> here.</a>
 * </p>
 */
public class JacobiSolver extends AbstractSolver implements LinearSystemSolver {

    private static final long serialVersionUID = 4071505L;

    private static final int MAX_ITERATIONS = 1000000;

    public JacobiSolver(Matrix a) {
        super(a);
    }

    @Override
    public Vector solve(Vector b, Factory factory) {
        ensureRHSIsCorrect(b);
        return solve(new LinearSystem(a, b, factory), factory);
    }

    /**
     * Returns the solution for the given linear system
     * <p>
     * See <a href="http://mathworld.wolfram.com/JacobiMethod.html">
     * http://mathworld.wolfram.com/JacobiMethod.html</a> for more details.
     * </p>
     * 
     * @param linearSystem
     * @param factory
     * @return vector
     */
    @Override
    @Deprecated
    public Vector solve(LinearSystem linearSystem, Factory factory) {

        if (!suitableFor(linearSystem)) {
            throw new IllegalArgumentException("Method Jacobi can't solve this system.");
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

        // TODO: we can peel out the iterations
        int iteration = 0;

        Vector current = factory.createVector(linearSystem.variables());

        while (iteration < MAX_ITERATIONS && !linearSystem.isSolution(current)) {

            Vector next = current.blank();

            for (int i = 0; i < a.rows(); i++) {

                double summand = b.get(i) / a.get(i, i);
                for (int j = 0; j < a.columns(); j++) {
                    if (i != j) {
                        summand -= a.get(i, j) * current.get(j);
                    }
                }

                next.set(i, summand);
            }

            current = next;
            iteration++;
        }

        return current;
    }

    /**
     * Checks whether this linear system can be solved by Jacobi solver
     *
     * @param linearSystem
     * @return <code>true</code> if given linear system can be solved by Jacobi solver
     */
    @Override
    @Deprecated
    public boolean suitableFor(LinearSystem linearSystem) {
        return applicableTo(linearSystem.coefficientsMatrix());
    }

    @Override
    public boolean applicableTo(Matrix matrix) {
        return matrix.is(Matrices.DIAGONALLY_DOMINANT_MATRIX);
    }
}
