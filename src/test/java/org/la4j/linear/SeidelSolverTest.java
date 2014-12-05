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

public class SeidelSolverTest extends AbstractSolverTest {

    @Override
    public LinearAlgebra.SolverFactory solverFactory() {
        return LinearAlgebra.SEIDEL;
    }
    
    @Test
    public void testSolve_1x1() {

        double a[][] = new double[][] { 
            { -77.0 } 
        };

        double b[] = new double[] { 11.0 };

        performTest(a, b);
    }

    @Test
    public void testSolve_2x2() {

        double a[][] = new double[][] { 
            { 10.0, 1.0 },
            { 5.0, -22.0 }
        };

        double b[] = new double[] { -20.0, -10.0 };

        performTest(a, b);
    }

    @Test
    public void testSolve_3x3() {

        double a[][] = new double[][] { 
            { 5.0, 0.0, 0.0 }, 
            { 1.0, 7.0, 0.0 },
            { 4.0, 0.0, 9.0 }
        };

        double b[] = new double[] { 0.0, 2.0, 0.0 };

        performTest(a, b);
    }

    @Test
    public void testSolve_3x3_2() {

        double a[][] = new double[][] { 
            { 10.0, 1.0, -2.0 },
            { 3.0, -8.0, 1.0 },
            { 11.0, -2.0, 15.0 }
        };

        double b[] = new double[] { 6.8, -10.1, -36.9 };

        performTest(a, b);
    }

    @Test
    public void testSolve_4x4() {

        double a[][] = new double[][] { 
            { 32.0, 4.0, 1.0, -3.0 },
            { -2.0, -10.0, -6.0, 1.0 },
            { 5.0, -2.0, -12.0, -1.0 },
            { 0.0, -14.0, -6.0, 21.0 }
        };

        double b[] = new double[] { -3.9, -5.4, 2.1, 18.4 };

        performTest(a, b);
    }

    @Test
    public void testSolve_5x5() {

        double a[][] = new double[][] { 
            { 10.0, 1.0, 0.0, -2.0, -3.0 },
            { -1.0, -20.0, 0.0, 4.0, -3.0 },
            { 2.0, -3.0, 30.0, 5.0, 0.0 },
            { 5.0, 4.0, -1.0, 40.0, -6.0 },
            { -2.0, 1.0, 4.0, 10.0, -50.0 }
        };

        double b[] = new double[] { 0.07, -13.29, -12.45, -41.8, -32.8 };

        performTest(a, b);
    }
}
