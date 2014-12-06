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

public class EigenDecompositorTest extends AbstractDecompositorTest {


    @Override
    public LinearAlgebra.DecompositorFactory decompositorFactory() {
        return LinearAlgebra.EIGEN;
    }

    @Test
    public void testDecompose_1x1_symmetric_1() {
        double[][] input = new double[][]{
                {76.0}
        };
        double[][][] output = new double[][][]{
                {
                        {1.0}
                },
                {
                        {76.0}
                }
        };
        performTest(input, output);
    }

    @Test
    public void testDecompose_1x1_symmetric_2() {
        double[][] input = new double[][]{
                {-10.0}
        };
        double[][][] output = new double[][][]{
                {
                        {1.0}
                },
                {
                        {-10.0}
                }
        };
        performTest(input, output);
    }

    @Test
    public void testDecompose_2x2_symmetric_1() {
        double[][] input = new double[][]{
                {-5.0, 12.0},
                {12.0, 9.0}
        };
        double[][][] output = new double[][][]{
                {
                        {0.867, 0.498},
                        {-0.498, 0.867}
                },
                {
                        {-11.892, 0.0},
                        {0.0, 15.892},
                }
        };
        performTest(input, output);
    }

    @Test
    public void testDecompose_2x2_symmetric_2() {
        double[][] input = new double[][]{
                {25.0, 0},
                {0.0, 9.0}
        };
        double[][][] output = new double[][][]{
                {
                        {1.0, 0.0},
                        {0.0, 1.0}
                },
                {
                        {25.0, 0.0},
                        {0.0, 9.0},
                }
        };
        performTest(input, output);
    }

    @Test
    public void testDecompose_3x3_symmetric_1() {
        double[][] input = new double[][]{
                {1.5, 13.0, 9.4},
                {13.0, 2.8, 3.6},
                {9.4, 3.6, 4.4}
        };
        double[][][] output = new double[][][]{
                {
                        {0.1199, -0.7559, 0.6435},
                        {0.5753, 0.5812, 0.5754},
                        {-0.8090, 0.3012, 0.5046}
                },
                {
                        {0.447, 0.0, 0.0},
                        {0.0, -12.242, 0.0},
                        {0.0, 0.0, 20.495}
                }
        };
        performTest(input, output);
    }

    @Test
    public void testDecompose_3x3_symmetric_2() {
        double[][] input = new double[][]{
                {1.0, 0.0, 0.0},
                {0.0, 2.0, 0.0},
                {0.0, 0.0, 4.0}
        };
        double[][][] output = new double[][][]{
                {
                        {1.0, 0.0, 0.0},
                        {0.0, 1.0, 0.0},
                        {0.0, 0.0, 1.0}
                },
                {
                        {1.0, 0.0, 0.0},
                        {0.0, 2.0, 0.0},
                        {0.0, 0.0, 4.0}
                }
        };
        performTest(input, output);
    }

    @Test
    public void testDecompose_4x4_symmetric_1() {
        double[][] input = new double[][]{
                {1.0, 5.0, 7.0, 9.0},
                {5.0, 2.0, 8.0, 4.0},
                {7.0, 8.0, 4.0, 1.0},
                {9.0, 4.0, 1.0, 5.0}
        };
        double[][][] output = new double[][][]{
                {
                        {0.537, -0.444, -0.192, -0.691},
                        {0.478, 0.788, 0.318, -0.223},
                        {0.502, -0.404, 0.590, 0.486},
                        {0.481, 0.135, -0.717, 0.486}
                },
                {
                        {20.058, 0.0, 0.0, 0.0},
                        {0.0, -4.237, 0.0, 0.0},
                        {0.0, 0.0, 4.814, 0.0},
                        {0.0, 0.0, 0.0, -8.635}
                }
        };
        performTest(input, output);
    }

    @Test
    public void testDecompose_4x4_symmetric_2() {
        double[][] input = new double[][]{
                {-8.0, 7.0, 2.0, -3.0},
                {7.0, 20.0, -4.0, 5.0},
                {2.0, -4.0, 4.0, 2.0},
                {-3.0, 5.0, 2.0, -44.0}
        };
        double[][][] output = new double[][][]{
                {
                        {-0.947, 0.203, -0.226, 0.1},
                        {0.227, 0.961, -0.13, -0.09},
                        {0.187, -0.179, -0.965, -0.052},
                        {0.127, 0.058, -0.04, 0.989}
                },
                {
                        {-9.672, 0.0, 0.0, 0.0},
                        {0.0, 22.525, 0.0, 0.0},
                        {0.0, 0.0, 4.013, 0.0},
                        {0.0, 0.0, 0.0, -44.866}
                }
        };
        performTest(input, output);
    }

    @Test
    public void testDecompose_5x5_symmetric_1() {
        double[][] input = new double[][]{
                {1.0, 0.0, 0.0, 0.0, 0.0},
                {0.0, 2.0, 0.0, 0.0, 0.0},
                {0.0, 0.0, 3.0, 0.0, 0.0},
                {0.0, 0.0, 0.0, 4.0, 0.0},
                {0.0, 0.0, 0.0, 0.0, 5.0}
        };
        double[][][] output = new double[][][]{
                {
                        {1.0, 0.0, 0.0, 0.0, 0.0},
                        {0.0, 1.0, 0.0, 0.0, 0.0},
                        {0.0, 0.0, 1.0, 0.0, 0.0},
                        {0.0, 0.0, 0.0, 1.0, 0.0},
                        {0.0, 0.0, 0.0, 0.0, 1.0}
                },
                {
                        {1.0, 0.0, 0.0, 0.0, 0.0},
                        {0.0, 2.0, 0.0, 0.0, 0.0},
                        {0.0, 0.0, 3.0, 0.0, 0.0},
                        {0.0, 0.0, 0.0, 4.0, 0.0},
                        {0.0, 0.0, 0.0, 0.0, 5.0}
                }
        };
        performTest(input, output);
    }

    @Test
    public void testDecompose_5x5_symmetric_2() {
        double[][] input = new double[][]{
                {6.2, 4.7, 6.2, 45.9, 8.7},
                {4.7, 12.6, 13.5, 0.6, 12.3},
                {6.2, 13.5, 25.8, 1.2, 10.0},
                {45.9, 0.6, 1.2, 34.1, 6.2},
                {8.7, 12.3, 10.0, 6.2, 10.8}
        };
        double[][][] output = new double[][][]{
                {
                        {-0.108, 0.035, 0.028, 0.580, -0.806},
                        {0.507, 0.708, -0.469, 0.141, 0.048},
                        {0.711, -0.108, 0.667, 0.186, 0.057},
                        {-0.296, 0.068, 0.068, 0.749, 0.585},
                        {0.372, -0.694, -0.574, 0.218, 0.057}
                },
                {
                        {39.207, 0.0, 0.0, 0.0, 0.0},
                        {0.0, -1.232, 0.0, 0.0, 0.0},
                        {0.0, 0.0, 8.082, 0.0, 0.0},
                        {0.0, 0.0, 0.0, 71.862, 0.0},
                        {0.0, 0.0, 0.0, 0.0, -28.420}
                }
        };
        performTest(input, output);
    }

    @Test
    public void testDecompose_2x2_nonSymmetric_1() {
        double[][] input = new double[][]{
                {1.0, 2.0},
                {37.0, 24.0}
        };
        double[][][] output = new double[][][]{
                {
                        {-0.573, -0.122},
                        {0.820, -1.572}
                },
                {
                        {-1.861, 0.0},
                        {0.0, 26.861}
                }
        };
        performTest(input, output);
    }

    @Test
    public void testDecompose_2x2_nonSymmetric_2() {
        double[][] input = new double[][]{
                {6.0, 99.0},
                {-4.0, 12.0}
        };
        double[][][] output = new double[][][]{
                {
                        {4.918, 0.750},
                        {0.000, 1.0}
                },
                {
                        {9.0, 19.672},
                        {-19.672, 9.0}
                }
        };
        performTest(input, output);
    }

    @Test
    public void testDecompose_3x3_nonSymmetric() {
        double[][] input = new double[][]{
                {16.0, -11.0, 99.0},
                {7.0, -2.0, -42.0},
                {8.0, -1.0, -7.0}
        };
        double[][][] output = new double[][][]{
                {
                        {0.982, 0.644, -0.353},
                        {-0.026, -0.896, -2.320},
                        {0.187, -0.359, -0.186}
                },
                {
                        {35.149, 0.0, 0.0},
                        {0.0, -23.856, 0.0},
                        {0.0, 0.0, -4.293}
                }
        };
        performTest(input, output);
    }

    @Test
    public void testDecompose_4x4_nonSymmetric() {
        double[][] input = new double[][]{
                {-1.0, -4.0, 4.0, 8.0},
                {4.0, 1.0, -8.0, 2.0},
                {-2.0, -4.0, 5.0, 8.0},
                {4.0, -1.0, -8.0, 4.0}
        };
        double[][][] output = new double[][][]{
                {
                        {-0.497, -0.503, -2.828, -0.0},
                        {0.503, -0.497, 0.0, -2.828},
                        {-0.497, -0.503, -1.414, -0.0},
                        {0.503, -0.497, 0.0, -1.414}
                },
                {
                        {3.0, 4.0, 0.0, 0.0},
                        {-4.0, 3.0, 0.0, 0.0},
                        {0.0, 0.0, 1.0, 0.0},
                        {0.0, 0.0, 0.0, 2.0}
                }
        };
        performTest(input, output);
    }

    @Test
    public void testDecompose_5x5_nonSymmetric() {
        double[][] input = new double[][]{
                {26.0, -1.0, 2.0, 15.0, -3.0},
                {-19.0, -11.0, 21.0, -4.0, 0.0},
                {88.0, -22.0, -3.0, -5.0, -17.0},
                {15.0, 45.0, 22.0, 42.0, 54.0},
                {-17.0, 55.0, -9.0, 6.0, 2.0}
        };
        double[][][] output = new double[][][]{
                {
                        {-0.109, 0.159, -0.525, -0.274, 0.091},
                        {-0.338, -0.250, -0.015, 0.244, 0.364},
                        {0.341, -0.082, -0.713, -0.389, 0.883},
                        {0.138, -0.492, -0.996, 0.032, -0.760},
                        {0.165, 0.840, 0.153, 0.457, -0.397}
                },
                {
                        {-23.664, 17.563, 0.0, 0.0, 0.0},
                        {-17.563, -23.664, 0.0, 0.0, 0.0},
                        {0.0, 0.0, 58.024, 0.0, 0.0},
                        {0.0, 0.0, 0.0, 22.652, 31.064},
                        {0.0, 0.0, 0.0, -31.064, 22.652}
                }
        };
        performTest(input, output);
    }
}
