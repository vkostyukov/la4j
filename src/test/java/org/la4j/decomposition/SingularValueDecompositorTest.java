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

public class SingularValueDecompositorTest extends AbstractDecompositorTest {

    @Override
    public LinearAlgebra.DecompositorFactory decompositorFactory() {
        return LinearAlgebra.SVD;
    }

    @Test
    public void testDecompose_1x1() {
        double[][] input = new double[][]{
                {89.0}
        };
        double[][][] output = new double[][][]{
                {
                        {1.0}
                },
                {
                        {89.0}
                },
                {
                        {1.0}
                }
        };
        performTest(input, output);
    }

    @Test
    public void testDecompose_2x2() {
        double[][] input = new double[][]{
                {7.0, 19.0},
                {-1.0, 100.0}
        };
        double[][][] output = new double[][][]{
                {
                        {0.187, -0.982},
                        {0.982, 0.187}
                },
                {
                        {101.790, 0.0},
                        {0.0, 7.064}
                },
                {
                        {0.003, -1.0},
                        {1.0, 0.003}
                }
        };
        performTest(input, output);
    }

    @Test
    public void testDecompose_3x3() {
        double[][] input = new double[][]{
                {2.0, 0.0, 0.0},
                {0.0, 4.0, 0.0},
                {0.0, 0.0, 8.0}
        };
        double[][][] output = new double[][][]{
                {
                        {0.0, 0.0, -1.0},
                        {0.0, -1.0, 0.0},
                        {1.0, 0.0, 0.0}
                },
                {
                        {8.0, 0.0, 0.0},
                        {0.0, 4.0, 0.0},
                        {0.0, 0.0, 2.0}
                },
                {
                        {0.0, 0.0, -1.0},
                        {0.0, -1.0, 0.0},
                        {1.0, 0.0, 0.0}
                }
        };
        performTest(input, output);
    }

    @Test
    public void testDecompose_3x1() {
        double[][] input = new double[][]{
                {6.0},
                {19.0},
                {20.0}
        };
        double[][][] output = new double[][][]{
                {
                        {-0.213},
                        {-0.673},
                        {-0.708}
                },
                {
                        {28.231}
                },
                {
                        {-1.0}
                }
        };
        performTest(input, output);
    }

    @Test
    public void testDecompose_4x4() {
        double[][] input = new double[][]{
                {9.0, 18.0, -1.0, 43.0},
                {-9.0, 14.0, 85.0, -2.0},
                {1.0, 100.0, 53.0, -22.0},
                {28.0, 15.0, -28.0, 5.0}
        };
        double[][][] output = new double[][][]{
                {
                        {-0.041, -0.261, 0.928, -0.262},
                        {-0.538, 0.664, 0.285, 0.434},
                        {-0.838, -0.467, -0.219, -0.179},
                        {0.087, -0.522, 0.095, 0.843}
                },
                {
                        {130.97, 0.0, 0.0, 0.0},
                        {0.0, 72.357, 0.0, 0.0},
                        {0.0, 0.0, 46.283, 0.0},
                        {0.0, 0.0, 0.0, 18.496}
                },
                {
                        {0.046, -0.323, 0.178, 0.928},
                        {-0.693, -0.691, 0.005, -0.207},
                        {-0.706, 0.643, 0.196, 0.222},
                        {0.139, -0.067, 0.964, -0.215}
                }
        };
        performTest(input, output);
    }

    @Test
    public void testDecompose_5x5() {
        double[][] input = new double[][]{
                {-1.0, 8.0, 17.0, 100.0, 10.0},
                {4.0, -5.0, 44.0, 11.0, -66.0},
                {22.0, 54.0, 24.0, -14.0, 1.0},
                {99.0, 25.0, 77.0, -23.0, 4.0},
                {11.0, 7.0, -15.0, -4.0, 81.0}
        };
        double[][][] output = new double[][][]{
                {
                        {-0.072, 0.35, 0.927, 0.039, -0.105},
                        {0.252, 0.624, -0.132, -0.058, 0.725},
                        {0.337, -0.113, 0.036, 0.932, 0.06},
                        {0.901, -0.158, 0.124, -0.338, -0.182},
                        {-0.072, -0.671, 0.326, -0.110, 0.653}
                },
                {
                        {141.208, 0.0, 0.0, 0.0, 0.0},
                        {0.0, 112.592, 0.0, 0.0, 0.0},
                        {0.0, 0.0, 101.124, 0.0, 0.0},
                        {0.0, 0.0, 0.0, 44.635, 0.0},
                        {0.0, 0.0, 0.0, 0.0, 10.868}
                },
                {
                        {0.686, -0.207, 0.15, -0.324, -0.599},
                        {0.272, -0.134, 0.152, 0.934, -0.109},
                        {0.626, 0.254, 0.153, -0.087, 0.716},
                        {-0.210, 0.442, 0.856, -0.034, -0.161},
                        {-0.136, -0.824, 0.444, -0.115, 0.303}
                }
        };
        performTest(input, output);
    }
}
