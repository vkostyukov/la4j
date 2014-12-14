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

import org.junit.Ignore;
import org.junit.Test;
import org.la4j.LinearAlgebra;

public class GaussianSolverTest extends AbstractSolverTest {

    @Override
    public LinearAlgebra.SolverFactory solverFactory() {
        return LinearAlgebra.GAUSSIAN;
    }
    
    @Test
    public void testSolve_1x1() {

        double a[][] = new double[][] {
            { 99.0 }
        };

        double b[] = new double[] { -33.0 };

        performTest(a, b);
    }

    @Test
    public void testSolve_2x2() {

        double a[][] = new double[][] {
            { 5.0, 10.0 },
            { 15.0, -20.0 }
        };

        double b[] = new double[] { 21.0, -37.0 };

        performTest(a, b);
    }

    @Test
    public void testSolve_3x3() {

        double a[][] = new double[][] {
            { -8.0, 4.0, 2.0 },
            { 10.0, 18.0, -70.0 },
            { 3.0, -54.0, 19.0 }
        };

        double b[] = new double[] { 7.0, -85.5, 0.5 };

        performTest(a, b);
    }

    @Test
    public void testSolve_4x4() {

        double a[][] = new double[][] {
            { 9.0, 3.0, 0.0, 5.0 }, 
            { 1.0, 0.0, 3.0, 6.0 },
            { 7.0, 0.0, 2.0, 2.0 }, 
            { 0.0, 3.0, 0.0, 0.0 } 
        };

        double b[] = new double[] { 0.0, 2.0, 0.0, 3.0 };

        performTest(a, b);
    }

    @Test
    public void testSolve_5x5() {

        double a[][] = new double[][] {
            { 19.0, -1.0, 6.0, 18.0, -22.0 },
            { 77.0, 4.0, 100.0, 16.0, 54.0 },
            { -0.1, 19.0, 24.0, 3.0, 1.0 },
            { 11.0, 8.0, 33.0, -10.0, 0.0 },
            { -99.0, 7.0, 42.0, 42.0, 25.0 }
        };

        double b[] = new double[] { 146.0, 486.0, 77.0, 282.0, -157.0 };

        performTest(a, b);
    }
}
