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

public class ForwardBackSubstitutionSolverTest extends AbstractSolverTest {

    @Override
    public LinearAlgebra.SolverFactory solverFactory() {
        return LinearAlgebra.FORWARD_BACK_SUBSTITUTION;
    }
    
    @Test
    public void testSolve_1x1() {

        double a[][] = new double[][] {
                { 62.0 }
        };

        double b[] = new double[] { -31.0 };

        performTest(a, b);
    }

    @Test
    public void testSolve_2x2() {

        double a[][] = new double[][] {
                { 18.0, 4.0 },
                { 10.0, -2.0 }
        };

        double b[] = new double[] { 11.0, 4.0 };

        performTest(a, b);
    }

    @Test
    public void testSolve_3x3() {

        double a[][] = new double[][] {
                { -19.0, 15.0, 4.0 },
                { 2.0, -2.0, 1.0 },
                { 9.0, 55.0, -1.0 }
        };

        double b[] = new double[] { -146.0, 16.5, 7.5 };

        performTest(a, b);
    }

    @Test
    public void testSolve_3x3_2() {

        double a[][] = new double[][] {
                { 1.0, 0.0, -10.0 },
                { 20.0, 5.0, -1.0},
                { 0.0, 0.0, -100.0 }
        };

        double b[] = new double[] { 8.0, 115.2, 20.0 };

        performTest(a, b);
    }

    @Test
    public void testSolve_4x4() {

        double a[][] = new double[][] {
                { 7.0, -42.0, 11.0, 2.0 },
                { 8.0, 1.0, -2.0, -54.0 },
                { 6.0, 0.0, 55.0, -15.0 },
                { 24.0, -12.0, 44.0, -1.0 }
        };

        double b[] = new double[] { 52.3, -32.6, -29.0, 37.1 };

        performTest(a, b);
    }

    @Test
    public void testSolve_4x4_2() {

        double a[][] = new double[][] {
                { -100.0, 1.0, 0.0, -1.0 },
                { 80.0, 6.0, -2.0, -6.0 },
                { 0.0, -14.0, -110.0, 0.0 },
                { 22.0, 0.0, 0.0, -16.0 }
        };

        double b[] = new double[] { -41.5, 63.0, -1533.0, 155.0 };

        performTest(a, b);
    }

    @Test
    public void testSolve_5x5() {

        double a[][] = new double[][] {
                { 4.0, 15.0, -0.5, 0.6, 0.0 },
                { 9.0, 16.0, -9.0, 0.0, 1.0 },
                { 77.0, -8.0, 16.0, -32.0, 0.0 },
                { 8.0, -12.0, 22.0, -2.0, 4.0 },
                { 0.0, 0.0, 9.0, -1.0, 60.0 }
        };

        double b[] = new double[] { -110.44, -79.2, 862.8, 221.0, -3.1 };

        performTest(a, b);
    }

}
