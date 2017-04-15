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

import org.la4j.Matrices;
import org.la4j.Matrix;
import org.la4j.matrix.functor.MatrixFunction;
import org.la4j.Vector;

/**
 * This class represents Jacobi method for solving linear systems. More details
 * <p>
 * <a href="http://mathworld.wolfram.com/JacobiMethod.html"> here.</a>
 * </p>
 */
public class JacobiSolver extends AbstractSolver implements LinearSystemSolver {

    private static final long serialVersionUID = 4071505L;

    private final Matrix aa;

    public JacobiSolver(Matrix a) {
        super(a);

        // We need a copy here, since we don't want to change source matrix
        this.aa = a.copy();

        for (int i = 0; i < aa.rows(); i++) {
            MatrixFunction divider = Matrices.asDivFunction(aa.get(i, i));
            for (int j = 0; j < aa.columns(); j++) {
                if (i != j) {
                    aa.updateAt(i, j, divider);
                }
            }
        }
    }

    @Override
    public Vector solve(Vector b) {
        ensureRHSIsCorrect(b);

        Vector current =b.blankOfLength(unknowns());

        // while it is not a solution
        while (!matrix.multiply(current).equals(b)) {

            Vector next = current.blank();

            for (int i = 0; i < aa.rows(); i++) {

                double acc = b.get(i) / aa.get(i, i);
                for (int j = 0; j < aa.columns(); j++) {
                    if (i != j) {
                        acc -= aa.get(i, j) * current.get(j);
                    }
                }

                next.set(i, acc);
            }

            current = next;
        }

        return current;
    }

    @Override
    public boolean applicableTo(Matrix matrix) {
        return matrix.is(Matrices.DIAGONALLY_DOMINANT_MATRIX);
    }
}
