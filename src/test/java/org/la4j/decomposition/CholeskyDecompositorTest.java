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

package org.la4j.decomposition;

import org.junit.Test;
import org.la4j.LinearAlgebra;

public class CholeskyDecompositorTest extends AbstractDecompositorTest {

    @Override
    public LinearAlgebra.DecompositorFactory decompositorFactory() {
        return LinearAlgebra.CHOLESKY;
    }
    
    @Test
    public void testDecompose_1x1() {
        double[][] input = new double[][]{
                {56}
        };
        double[][][] output = new double[][][]{
                {
                        {7.483}
                }
        };
        performTest(input, output);
    }

    @Test
    public void testDecompose_2x2() {
        double[][] input = new double[][]{
                {1.0, -2.0},
                {-2.0, 5.0}
        };
        double[][][] output = new double[][][]{
                {
                        {1.0, 0.0},
                        {-2.0, 1.0}
                }
        };
        performTest(input, output);
    }

    @Test
    public void testDecompose_3x3() {
        double[][] input = new double[][]{
                {1.0, -2.0, 0.0},
                {-2.0, 5.0, 2.0},
                {0.0, 2.0, 5.0}
        };
        double[][][] output = new double[][][]{
                {
                        {1.0, 0.0, 0.0},
                        {-2.0, 1.0, 0.0},
                        {0.0, 2.0, 1.0}
                }
        };
        performTest(input, output);
    }

    @Test
    public void testDecompose_4x4() {
        double[][] input = new double[][]{
                {18.0, 22.0, 54.0, 42.0},
                {22.0, 70.0, 86.0, 62.0},
                {54.0, 86.0, 174.0, 134.0},
                {42.0, 62.0, 134.0, 106.0}
        };
        double[][][] output = new double[][][]{
                {
                        {4.243, 0.000, 0.000, 0.000},
                        {5.185, 6.566, 0.000, 0.000},
                        {12.728, 3.046, 1.650, 0.000},
                        {9.899, 1.625, 1.850, 1.393}
                }
        };
        performTest(input, output);
    }

    @Test
    public void testDecompose_5x5() {
        double[][] input = new double[][]{
                {6.0, 15.0, 55.0, 72.0, 101.0},
                {15.0, 55.0, 225.0, 229.0, 256.0},
                {55.0, 225.0, 979.0, 1024.0, 1200.0},
                {72.0, 229.0, 1024.0, 2048.0, 2057.0},
                {101.0, 256.0, 1200.0, 2057.0, 6000.0}
        };
        double[][][] output = new double[][][]{
                {
                        {2.449, 0.000, 0.000, 0.000, 0.000},
                        {6.124, 4.183, 0.000, 0.000, 0.000},
                        {22.454, 20.917, 6.110, 0.000, 0.000},
                        {29.394, 11.713, 19.476, 25.836, 0.000},
                        {41.233, 0.837, 42.007, 0.661, 50.340}
                }
        };
        performTest(input, output);
    }
}
