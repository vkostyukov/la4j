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

public class LUDecompositorTest extends AbstractDecompositorTest {

    @Override
    public LinearAlgebra.DecompositorFactory decompositorFactory() {
        return LinearAlgebra.LU;
    }

    @Test
    public void testDecompose_1x1() {
        double[][] input = new double[][]{
                {71.0}
        };
        double[][][] output = new double[][][]{
                {
                        {1.0}
                },
                {
                        {71.0}
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
                {14.0, 6.0},
                {7.0, 18.0}
        };
        double[][][] output = new double[][][]{
                {
                        {1.0, 0.0},
                        {0.5, 1.0}
                },
                {
                        {14.0, 6.0},
                        {0.0, 15.0}
                },
                {
                        {1.0, 0.0},
                        {0.0, 1.0}
                }
        };
        performTest(input, output);
    }

    @Test
    public void testDecompose_3x3() {
        double[][] input = new double[][]{
                {1.0, 0.0, 2.0},
                {0.0, 10.0, 0.0},
                {2.0, 0.0, 9.0}
        };
        double[][][] output = new double[][][]{
                {
                        {1.0, 0.0, 0.0},
                        {0.0, 1.0, 0.0},
                        {0.5, 0.0, 1.0}
                },
                {
                        {2.0, 0.0, 9.0},
                        {0.0, 10.0, 0.0},
                        {0.0, 0.0, -2.5}
                },
                {
                        {0.0, 0.0, 1.0},
                        {0.0, 1.0, 0.0},
                        {1.0, 0.0, 0.0},
                }
        };
        performTest(input, output);
    }

    @Test
    public void testDecompose_3x3_2() {
        double[][] input = new double[][]{
                {77.0, 19.0, 1.0},
                {-9.0, 17.0, 34.0},
                {11.0, 100.0, -2.0}
        };
        double[][][] output = new double[][][]{
                {
                        {1.0, 0.0, 0.0},
                        {0.143, 1.0, 0.0},
                        {-0.117, 0.198, 1.0}
                },
                {
                        {77.0, 19.0, 1.0},
                        {0.0, 97.286, -2.143},
                        {0.0, 0.0, 34.540}
                },
                {
                        {1.0, 0.0, 0.0},
                        {0.0, 0.0, 1.0},
                        {0.0, 1.0, 0.0}
                }
        };
        performTest(input, output);
    }

    @Test
    public void testDecompose_4x4() {
        double[][] input = new double[][]{
                {99.0, 1.0, -10.0, 6.0},
                {14.0, 65.0, 7.0, 48.0},
                {39.0, 40.0, -2.0, 9.0},
                {11.0, 5.0, 43.0, 99.0}
        };
        double[][][] output = new double[][][]{
                {
                        {1.0, 0.0, 0.0, 0.0},
                        {0.141, 1.0, 0.0, 0.0},
                        {0.111, 0.075, 1.0, 0.0},
                        {0.394, 0.611, -0.074, 1.0}
                },
                {
                        {99.0, 1.0, -10.0, 6.0},
                        {0.0, 64.859, 8.414, 47.152},
                        {0.0, 0.0, 43.477, 94.779},
                        {0.0, 0.0, 0.000, -15.184}
                },
                {
                        {1.0, 0.0, 0.0, 0.0},
                        {0.0, 1.0, 0.0, 0.0},
                        {0.0, 0.0, 0.0, 1.0},
                        {0.0, 0.0, 1.0, 0.0}
                }
        };
        performTest(input, output);
    }

    @Test
    public void testDecompose_5x5() {
        double[][] input = new double[][]{
                {6.0, 19.0, 81.0, 10.0, 65.0},
                {100.0, 1.0, -10.0, 16.0, 71.0},
                {58.0, -17.0, 88.0, 19.0, 29.0},
                {-44.0, 4.0, 16.0, -100.0, 1.0},
                {5.00, 76.0, 93.0, 35.0, -24.0}
        };
        double[][][] output = new double[][][]{
                {
                        {1.0, 0.0, 0.0, 0.0, 0.0},
                        {0.05, 1.0, 0.0, 0.0, 0.0},
                        {0.58, -0.231, 1.0, 0.0, 0.0},
                        {-0.44, 0.058, 0.053, 1.0, 0.0},
                        {0.06, 0.249, 0.505, 0.088, 1.0}
                },
                {
                        {100.0, 1.0, -10.0, 16.0, 71.0},
                        {0.0, 75.95, 93.5, 34.2, -27.55},
                        {0.0, 0.0, 115.442, 17.636, -18.557},
                        {0.0, 0.0, 0.0, -95.896, 34.837},
                        {0.0, 0.0, 0.0, 0.0, 73.930}
                },
                {
                        {0.0, 1.0, 0.0, 0.0, 0.0},
                        {0.0, 0.0, 0.0, 0.0, 1.0},
                        {0.0, 0.0, 1.0, 0.0, 0.0},
                        {0.0, 0.0, 0.0, 1.0, 0.0},
                        {1.0, 0.0, 0.0, 0.0, 0.0}
                }
        };
        performTest(input, output);
    }

    @Test
    public void testDecompose_5x5_2() {
        double[][] input = new double[][]{
                {88.0, 17.0, 6.0, 14.0, -1.0},
                {5.0, -5.0, 41.0, 16.0, -29.0},
                {7.0, -53.0, 19.0, 22.0, -99.0},
                {3.0, 101.0, -91.0, 8.0, 26.0},
                {71.0, -66.0, 46.0, 18.0, 23.0}
        };
        double[][][] output = new double[][][]{
                {
                        {1.0, 0.0, 0.0, 0.0, 0.0},
                        {0.034, 1.0, 0.0, 0.0, 0.0},
                        {0.057, -0.059, 1.0, 0.0, 0.0},
                        {0.080, -0.541, -0.875, 1.0, 0.0},
                        {0.807, -0.794, -0.887, 0.687, 1.0}
                },
                {
                        {88.0, 17.0, 6.0, 14.0, -1.0},
                        {0.0, 100.42, -91.205, 7.523, 26.034},
                        {0.0, 0.0, 35.241, 15.651, -27.397},
                        {0.0, 0.0, 0.0, 38.656, -108.806},
                        {0.0, 0.0, 0.0, 0.0, 94.922}
                },
                {
                        {1.0, 0.0, 0.0, 0.0, 0.0},
                        {0.0, 0.0, 0.0, 1.0, 0.0},
                        {0.0, 1.0, 0.0, 0.0, 0.0},
                        {0.0, 0.0, 1.0, 0.0, 0.0},
                        {0.0, 0.0, 0.0, 0.0, 1.0}
                }
        };
        performTest(input, output);
    }
}
