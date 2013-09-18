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
 * This class represents <a
 * href="http://mathworld.wolfram.com/SquareRootMethod.html"> Square Root method
 * </a> for solving linear systems.
 */
public class SquareRootSolver implements LinearSystemSolver {

    private static final long serialVersionUID = 4071505L;

    /**
     * Returns the solution for the given linear system
     * <p>
     * See <a href="http://mathworld.wolfram.com/SquareRootMethod.html">
     * http://mathworld.wolfram.com/SquareRootMethod.html</a> for more details.
     * </p>
     * 
     * @param linearSystem
     * @param factory
     * @return vector
     */
    @Override
    public Vector solve(LinearSystem linearSystem, Factory factory) {

        if (!suitableFor(linearSystem)) {
            throw new IllegalArgumentException("This system can't be solved with SQRT method.");
        }

        Matrix a = linearSystem.coefficientsMatrix();
        Vector b = linearSystem.rightHandVector();

        Matrix s = a.blank();
        Matrix d = a.blank();

        Vector x = factory.createVector(linearSystem.variables());
        Vector y = factory.createVector(linearSystem.variables());
        Vector z = factory.createVector(linearSystem.variables());

        for (int i = 0; i < a.rows(); i++) {

            double dd = 0.0;
            for (int l = 0; l < i; l++) {
                double sli = s.get(l, i);
                dd += sli * sli * d.get(l, l);
            }

            d.set(i, i, Math.signum(a.get(i, i) - dd));
            s.set(i, i, Math.sqrt(Math.abs(a.get(i, i) - dd)));

            if (s.get(i, i) == 0.0) {
                // TODO: we can try to rearrange the diagonal elements
                throw new IllegalArgumentException("This matrix is singular. We can't solve it.");
            }

            for (int j = i + 1; j < a.columns(); j++) {

                double summand = 0;
                for (int l = 0; l < i; l++) {
                    double sli = s.get(l, i);
                    double slj = s.get(l, j);
                    summand += sli * slj * d.get(l, l);
                }

                s.set(i, j, (a.get(i, j) - summand) / (s.get(i, i) * d.get(i, i)));
            }

            double zz = 0.0;
            for (int l = 0; l < i; l++) {
                zz += z.get(l) * s.get(l, i);
            }

            z.set(i, (b.get(i) - zz) / s.get(i, i));
            y.set(i, z.get(i) / d.get(i, i));
        }

        for (int i = a.rows() - 1; i >= 0; i--) {

            double summand = 0.0;
            for (int l = i + 1; l < a.columns(); l++) {
                summand += x.get(l) * s.get(i, l);
            }

            x.set(i, (y.get(i) - summand) / s.get(i, i));
        }

        return x;
    }

    /**
     * Checks whether this linear system can be solved by Square Root solver
     * 
     * @param linearSystem
     * @return <code>true</code> if given linear system can be solved by Square
     *         Root solver
     */
    @Override
    public boolean suitableFor(LinearSystem linearSystem) {
        return linearSystem.coefficientsMatrix().is(Matrices.SYMMETRIC_MATRIX);
    }
}
