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

import org.junit.Test;
import org.la4j.LinearAlgebra;

public class LeastSquaresSolverTest extends AbstractSolverTest {
    
    @Override
    public LinearAlgebra.SolverFactory solverFactory() {
        return LinearAlgebra.LEAST_SQUARES;
    }
    
    @Test
    public void testSolve_1x1() {

        double a[][] = new double[][] {
                { 55.0 }
        };

        double b[] = new double[] { -5.0 };

        performTest(a, b);
    }

    @Test
    public void testSolve_2x2() {

        double a[][] = new double[][] {
                { 6.0, 3.0 },
                { 9.0, 18.0 }
        };

        double b[] = new double[] { 5.0, 21.0 };

        performTest(a, b);
    }

    @Test
    public void testSolve_2x1() {

        double a[][] = new double[][] {
                { 20.0 },
                { -24.0 }
        };

        double b[] = new double[] { -10.0, 12.0 };

        performTest(a, b);
    }

    @Test
    public void testSolve_3x3() {

        double a[][] = new double[][] {
                { 99.0, -1.0, 0.0 },
                { 9.0, 50.0, -2.0 },
                { 10.0, 60.0, -4.0 }
        };

        double b[] = new double[] { -98.9, -34.0, -56.0 };

        performTest(a, b);
    }

    @Test
    public void testSolve_3x1() {

        double a[][] = new double[][] {
                { 44.0 },
                { -4.0 },
                { 444.0 }
        };

        double b[] = new double[] { 4.4, -0.4, 44.4 };

        performTest(a, b);
    }

    @Test
    public void testSolve_3x2() {

        double a[][] = new double[][] {
                { 10.0, 1.0 },
                { 0.0, 2.0 },
                { 4.0, -8.0 }
        };

        double b[] = new double[] { 90.0, -20.0, 120.0 };

        performTest(a, b);
    }

    @Test
    public void testSolve_4x4() {

        double a[][] = new double[][] {
                { 77.0, -5.0, 10.0, 0.0 },
                { 44.0, 1.0, 0.0, -2.0 },
                { 33.0, 20.0, 1.0, 0.0 },
                { 0.0, 10.0, -12.0, 54.0 }
        };

        double b[] = new double[] { 708.0, 405.0, 319.0, -230.0 };

        performTest(a, b);
    }

    @Test
    public void testSolve_4x1() {

        double a[][] = new double[][] {
                { 2.0 },
                { -16.0 },
                { 32.0 },
                { 8.0  }
        };

        double b[] = new double[] { 1.0, -8.0, 16.0, 4.0 };

        performTest(a, b);
    }

    @Test
    public void testSolve_4x2() {

        double a[][] = new double[][] {
                { 100.0, -88.0 },
                { 40.0, -20.0 },
                { -0.5, 0.5 },
                { 1.0, 0.1 }
        };

        double b[] = new double[] { -870.0, -196.0, 4.95, 1.1 };

        performTest(a, b);
    }

    @Test
    public void testSolve_4x3() {

        double a[][] = new double[][] {
                { 64.0, -10.0, 1.0 },
                { -0.6, 11.0, -15.0 },
                { 29.0, 160.0, -9.0 },
                { 11.0, -54.0, 22.0 }
        };

        double b[] = new double[] { -36.5, 116.9, 1633.5, -540.0 };

        performTest(a, b);
    }

    @Test
    public void testSolve_5x5() {

        double a[][] = new double[][] {
                { -5.0, 0.0, 1.0, 20.0, -16.0 },
                { 0.0, -10.0, 2.0, -22.0, 14.0 },
                { 0.0, -2.0, 0.5, 54.0, 17.0 },
                { -0.2, 20.0, -10.0, 45.0, -1.0 },
                { 8.0, 9.0, 11.0, -2.0, 15.0 }
        };

        double b[] = new double[] { 42.5, -71.0, 117.75, 107.5, 41.0 };

        performTest(a, b);
    }
}
