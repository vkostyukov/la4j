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
import org.la4j.matrix.Matrices;

public class SweepSolverTest extends AbstractSolverTest {

    @Override
    public LinearAlgebra.SolverFactory solverFactory() {
        return LinearAlgebra.SWEEP;
    }

    @Test
    public void testSolve_3x3() {

        double a[][] = new double[][] { 
            { 5.0, 1.0, 0.0 }, 
            { 1.0, 7.0, 3.0 },
            { 0.0, 2.0, 9.0 } 
        };

        double b[] = new double[] { 0.0, 2.0, 0.0 };

        performTest(a, b);
    }

    @Test
    public void testSolve_3x3_2() {

        double a[][] = new double[][] { 
            { -6.0, 11.0, 0.0 }, 
            { 17.0, 18.0, -20.0 },
            { 0.0, 20.0, -9.0 } 
        };

        double b[] = new double[] { -356.0, 497.0, -209.0 };

        performTest(a, b);
    }

    @Test
    public void testSolve_4x4() {

        double a[][] = new double[][] { 
            { 1.0, 18.0, 0.0, 0.0 }, 
            { -21.0, 2.0, -53.0, 0.0 },
            { 0.0, 19.0, 3.0, -6.0 },
            { 0.0, 0.0, 66.0, -4.0 }
        };

        double b[] = new double[] { -180.0, -394.7, -215.3, -10.6 };

        performTest(a, b);
    }

    @Test
    public void testSolve_5x5() {

        double a[][] = new double[][] { 
            { 7.0, 8.0, 0.0, 0.0, 0.0 }, 
            { -9.0, 100.0, -1.0, 0.0, 0.0 },
            { 0.0, 2.0, 3.0, 10.0, 0.0 },
            { 0.0, 0.0, -1.0, -77.0, 11.0 },
            { 0.0, 0.0, 0.0, 5.0, -2.0 }
        };

        double b[] = new double[] { -1.0, -111.0, -16.0, 185.0, -16.0 };

        performTest(a, b);
    }
}
