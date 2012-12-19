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

import junit.framework.TestCase;

import org.la4j.factory.Basic1DFactory;
import org.la4j.factory.Basic2DFactory;
import org.la4j.factory.CCSFactory;
import org.la4j.factory.CRSFactory;
import org.la4j.factory.Factory;
import org.la4j.matrix.Matrix;
import org.la4j.vector.Vector;

public abstract class AbstractSolverTest extends TestCase {

    public abstract LinearSystemSolver solver();

    public abstract double[][] coefficientMatrix();

    public abstract double[] rightHandVector();

    public Factory[] factories() {
        return new Factory[] { 
                new Basic1DFactory(), 
                new Basic2DFactory(),
                new CRSFactory(), 
                new CCSFactory() 
        };
    }

    public void testSolve() {

        for (Factory xFactory : factories()) {
            for (Factory yFactory : factories()) {

                Matrix a = xFactory.createMatrix(coefficientMatrix());
                Vector b = yFactory.createVector(rightHandVector());

                LinearSystem system = new LinearSystem(a, b);

                Vector x = system.solve(solver());
                assertTrue(system.isSolution(x));

                Vector y = system.solve(solver(), yFactory);
                assertTrue(system.isSolution(y));
            }
        }
    }
}
