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

public class SquareRootSolverTest extends AbstractSolverTest {

    @Override
    public LinearAlgebra.SolverFactory solverFactory() {
        return LinearAlgebra.SQUARE_ROOT;
    }

    @Test
    public void testSolve_1x1() {

        double a[][] = new double[][] { 
            { 44.0 } 
        };

        double b[] = new double[] { -22.0 };

        performTest(a, b);
    }

    @Test
    public void testSolve_2x2() {

        double a[][] = new double[][] { 
            { -7.0, 77.0 },
            { 77.0, 14.0 }
        };

        double b[] = new double[] { -77.0, 768.6 };

        performTest(a, b);
    }

    @Test
    public void testSolve_3x3() {

        double a[][] = new double[][] { 
            { 1.0, 0.0, 0.0 }, 
            { 0.0, 5.0, 0.0 },
            { 0.0, 0.0, 9.0 } 
        };

        double b[] = new double[] { 0.0, 2.0, 0.0 };

        performTest(a, b);
    }

    @Test
    public void testSolve_3x3_2() {

        double a[][] = new double[][] { 
            { 9.0, -1.0, -18.0 }, 
            { -1.0, 6.0, -3.0 },
            { -18.0, -3.0, 33.0 } 
        };

        double b[] = new double[] { -45.0, -10.0, 81.0 };

        performTest(a, b);
    }

    @Test
    public void testSolve_4x4() {

        double a[][] = new double[][] { 
            { -55.0, 11.0, 1.0, 0.0 }, 
            { 11.0, 66.0, 4.0, -1.0 },
            { 1.0, 4.0, -44.0, -9.0 },
            { 0.0, -1.0, -9.0, 33.0 }
        };

        double b[] = new double[] { -276.0, 29.0, -439.0, -89.0 };

        performTest(a, b);
    }

    @Test
    public void testSolve_5x5() {

        double a[][] = new double[][] { 
            { 14.0, 0.0, -10.0, 11.0, -4.0 },
            { 0.0, 21.0, -2.0, 0.0, 11.0 },
            { -10.0, -2.0, -7.0, 1.0, -3.0 },
            { 11.0, 0.0, 1.0, -28.0, 6.0  },
            { -4.0, 11.0, -3.0, 6.0, 0.7 },
        };

        double b[] = new double[] { 104.0, -256.0, -45.0, 54.0, -143.8 };

        performTest(a, b);
    }
}
