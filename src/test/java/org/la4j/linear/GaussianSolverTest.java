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

import org.la4j.matrix.Matrices;

public class GaussianSolverTest extends AbstractSolverTest {

    public void testSolve_2x2() {

        double a[][] = new double[][] {
            { 5.0, 10.0 },
            { 15.0, -20.0 }
        };

        double b[] = new double[] { 21.0, -37.0 };

        performTest(Matrices.GAUSSIAN_SOLVER, a, b);
    }

    public void testSolve_1x2() {

        double a[][] = new double[][] {
            { -4.0, 12.0 }
        };

        double b[] = new double[] { 2.0 };

        performTest(Matrices.GAUSSIAN_SOLVER, a, b);
    }

    public void testSolve_2x3() {

        double a[][] = new double[][] {
            { 7.0, -2.0, -22.0 },
            { -16.0, 8.8, -3.3 }
        };

        double b[] = new double[] { -63.0, -175.4 };

        performTest(Matrices.GAUSSIAN_SOLVER, a, b);
    }

    public void testSolve_3x3() {

        double a[][] = new double[][] {
            { -8.0, 4.0, 2.0 },
            { 10.0, 18.0, -70.0 },
            { 3.0, -54.0, 19.0 }
        };

        double b[] = new double[] { 7.0, -85.5, 0.5 };

        performTest(Matrices.GAUSSIAN_SOLVER, a, b);
    }

    public void testSolve_3x4() {

        double a[][] = new double[][] {
            { -1.0, 6.5, 2.4, 24 },
            { -10.0, 11.0, 28.0, -42.0 },
            { 100.0, 14.0, -20.0, 7.0 }
        };

        double b[] = new double[] { 23.0, 270.0, -100.0 };

        performTest(Matrices.GAUSSIAN_SOLVER, a, b);
    }

    public void testSolve_4x4() {

        double a[][] = new double[][] {
            { 9.0, 3.0, 0.0, 5.0 }, 
            { 1.0, 0.0, 3.0, 6.0 },
            { 7.0, 0.0, 2.0, 2.0 }, 
            { 0.0, 3.0, 0.0, 0.0 } 
        };

        double b[] = new double[] { 0.0, 2.0, 0.0, 3.0 };

        performTest(Matrices.GAUSSIAN_SOLVER, a, b);
    }
}
