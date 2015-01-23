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

import org.junit.Assert;
import org.la4j.LinearAlgebra;
import org.la4j.Matrix;
import org.la4j.Vector;
import static org.la4j.M.*;
import static org.la4j.V.*;

public abstract class AbstractSolverTest {

    public abstract LinearAlgebra.SolverFactory solverFactory();

    public void performTest(double coefficientMatrix[][], double rightHandVector[]) {
        for (Matrix a: ms(coefficientMatrix)) {
            for (Vector b: vs(rightHandVector)) {
                LinearSystemSolver solver = a.withSolver(solverFactory());
                Vector x = solver.solve(b);
                Vector ax = a.multiply(x);

                Assert.assertTrue(b.equals(ax, 1e-9));
            }
        }
    }
}
