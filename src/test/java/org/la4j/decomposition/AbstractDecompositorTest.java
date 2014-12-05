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
import org.la4j.factory.Factory;
import org.la4j.matrix.Matrix;
import org.la4j.matrix.MockMatrix;

import static org.junit.Assert.assertEquals;

public abstract class AbstractDecompositorTest {

    public abstract LinearAlgebra.DecompositorFactory decompositorFactory();

    @Test
    public void testDecompose_1x1_1() {

        double[][] input = new double[][] {
                { 71.0 }
        };

        double[][][] output = new double[][][] {
                {
                        { 1.0 }
                },
                {
                        { 71.0 }
                },
                {
                        { 1.0 }
                }
        };

        performTest(input, output);
    }

    @Test
    public void testDecompose_1x1_2() {

        double[][] input = new double[][] {
                { 56 }
        };

        double[][][] output = new double[][][] {
                {
                        { 7.483 }
                }
        };

        performTest(input, output);
    }

    @Test
    public void testDecompose_2x2_1() {

        double[][] input = new double[][] {
                { 14.0, 6.0 },
                { 7.0, 18.0 }
        };

        double[][][] output = new double[][][] {
                {
                        { 1.0, 0.0 },
                        { 0.5, 1.0 }
                },
                {
                        { 14.0, 6.0 },
                        { 0.0, 15.0 }
                },
                {
                        { 1.0, 0.0 },
                        { 0.0, 1.0 }
                }
        };

        performTest(input, output);
    }

    @Test
    public void testDecompose_2x2_2() {

        double[][] input = new double[][] {
                { 1.0, -2.0},
                { -2.0, 5.0}
        };

        double[][][] output = new double[][][] {
                {
                        { 1.0, 0.0},
                        {-2.0, 1.0}
                }
        };

        performTest(input, output);
    }

    @Test
    public void testDecompose_3x3_1() {

        double[][] input = new double[][] {
                { 1.0, 0.0, 2.0 },
                { 0.0, 10.0, 0.0 },
                { 2.0, 0.0, 9.0 }
        };

        double[][][] output = new double[][][] {
                {
                        { 1.0, 0.0, 0.0 },
                        { 0.0, 1.0, 0.0 },
                        { 0.5, 0.0, 1.0 }
                },
                {
                        { 2.0, 0.0, 9.0 },
                        { 0.0, 10.0, 0.0 },
                        { 0.0, 0.0, -2.5 }
                },
                {
                        { 0.0, 0.0, 1.0 },
                        { 0.0, 1.0, 0.0 },
                        { 1.0, 0.0, 0.0 },
                }
        };

        performTest(input, output);
    }

    @Test
    public void testDecompose_3x3_2() {

        double[][] input = new double[][] {
                { 77.0, 19.0, 1.0 },
                { -9.0, 17.0, 34.0 },
                { 11.0, 100.0, -2.0 }
        };

        double[][][] output = new double[][][] {
                {
                        { 1.0, 0.0, 0.0 },
                        { 0.143, 1.0, 0.0 },
                        { -0.117, 0.198, 1.0 }
                },
                {
                        { 77.0, 19.0, 1.0 },
                        { 0.0, 97.286, -2.143 },
                        { 0.0, 0.0, 34.540 }
                },
                {
                        { 1.0, 0.0, 0.0 },
                        { 0.0, 0.0, 1.0 },
                        { 0.0, 1.0, 0.0 }
                }
        };

        performTest(input, output);
    }

    @Test
    public void testDecompose_3x3_3() {

        double[][] input = new double[][] {
                { 1.0,-2.0, 0.0 },
                {-2.0, 5.0, 2.0 },
                { 0.0, 2.0, 5.0 }
        };

        double[][][] output = new double[][][] {
                {
                        { 1.0, 0.0, 0.0 },
                        {-2.0, 1.0, 0.0 },
                        { 0.0, 2.0, 1.0 }
                }
        };

        performTest(input, output);
    }

    @Test
    public void testDecompose_4x4_1() {

        double[][] input = new double[][] {
                { 99.0, 1.0, -10.0, 6.0 },
                { 14.0, 65.0, 7.0, 48.0 },
                { 39.0, 40.0, -2.0, 9.0 },
                { 11.0, 5.0, 43.0, 99.0 }
        };

        double[][][] output = new double[][][] {
                {
                        { 1.0, 0.0, 0.0, 0.0 },
                        { 0.141, 1.0, 0.0, 0.0 },
                        { 0.111, 0.075, 1.0, 0.0 },
                        { 0.394, 0.611, -0.074, 1.0 }
                },
                {
                        { 99.0, 1.0, -10.0, 6.0 },
                        { 0.0, 64.859, 8.414, 47.152 },
                        { 0.0, 0.0, 43.477, 94.779 },
                        { 0.0, 0.0, 0.000, -15.184 }
                },
                {
                        { 1.0, 0.0, 0.0, 0.0 },
                        { 0.0, 1.0, 0.0, 0.0 },
                        { 0.0, 0.0, 0.0, 1.0 },
                        { 0.0, 0.0, 1.0, 0.0 }
                }
        };

        performTest(input, output);
    }

    @Test
    public void testDecompose_4x4_2() {

        double[][] input = new double[][] {
                { 18.0, 22.0,  54.0,  42.0 },
                { 22.0, 70.0,  86.0,  62.0 },
                { 54.0, 86.0, 174.0, 134.0 },
                { 42.0, 62.0, 134.0, 106.0 }
        };

        double[][][] output = new double[][][] {
                {
                        {  4.243, 0.000, 0.000, 0.000 },
                        {  5.185, 6.566, 0.000, 0.000 },
                        { 12.728, 3.046, 1.650, 0.000 },
                        {  9.899, 1.625, 1.850, 1.393 }
                }
        };

        performTest(input, output);
    }

    @Test
    public void testDecompose_5x5_1() {

        double[][] input = new double[][] {
                { 6.0, 19.0, 81.0, 10.0, 65.0 },
                { 100.0, 1.0, -10.0, 16.0, 71.0 },
                { 58.0, -17.0, 88.0, 19.0, 29.0 },
                {-44.0, 4.0, 16.0, -100.0, 1.0 },
                { 5.00, 76.0, 93.0, 35.0, -24.0 }
        };

        double[][][] output = new double[][][] {
                {
                        { 1.0, 0.0, 0.0, 0.0, 0.0 },
                        { 0.05, 1.0, 0.0, 0.0, 0.0 },
                        { 0.58, -0.231, 1.0, 0.0, 0.0 },
                        { -0.44,  0.058, 0.053, 1.0, 0.0 },
                        { 0.06, 0.249, 0.505, 0.088, 1.0 }
                },
                {
                        { 100.0, 1.0, -10.0, 16.0, 71.0 },
                        { 0.0, 75.95, 93.5, 34.2, -27.55 },
                        { 0.0, 0.0, 115.442, 17.636, -18.557 },
                        { 0.0, 0.0, 0.0, -95.896, 34.837 },
                        { 0.0, 0.0, 0.0, 0.0, 73.930 }
                },
                {
                        { 0.0, 1.0, 0.0, 0.0, 0.0 },
                        { 0.0, 0.0, 0.0, 0.0, 1.0 },
                        { 0.0, 0.0, 1.0, 0.0, 0.0 },
                        { 0.0, 0.0, 0.0, 1.0, 0.0 },
                        { 1.0, 0.0, 0.0, 0.0, 0.0 }
                }
        };

        performTest(input, output);
    }

    @Test
    public void testDecompose_5x5_2() {

        double[][] input = new double[][] {
                { 88.0, 17.0, 6.0, 14.0, -1.0 },
                { 5.0, -5.0, 41.0, 16.0, -29.0 },
                { 7.0, -53.0, 19.0, 22.0, -99.0 },
                { 3.0, 101.0, -91.0, 8.0, 26.0 },
                { 71.0, -66.0, 46.0, 18.0, 23.0 }
        };

        double[][][] output = new double[][][] {
                {
                        { 1.0, 0.0, 0.0, 0.0, 0.0 },
                        { 0.034, 1.0, 0.0, 0.0, 0.0 },
                        { 0.057, -0.059, 1.0, 0.0, 0.0 },
                        { 0.080, -0.541, -0.875, 1.0, 0.0 },
                        { 0.807, -0.794, -0.887, 0.687, 1.0 }
                },
                {
                        { 88.0, 17.0, 6.0, 14.0, -1.0 },
                        { 0.0, 100.42, -91.205, 7.523, 26.034 },
                        { 0.0, 0.0, 35.241, 15.651, -27.397 },
                        { 0.0, 0.0, 0.0, 38.656, -108.806 },
                        { 0.0, 0.0, 0.0, 0.0, 94.922 }
                },
                {
                        { 1.0, 0.0, 0.0, 0.0, 0.0 },
                        { 0.0, 0.0, 0.0, 1.0, 0.0 },
                        { 0.0, 1.0, 0.0, 0.0, 0.0 },
                        { 0.0, 0.0, 1.0, 0.0, 0.0 },
                        { 0.0, 0.0, 0.0, 0.0, 1.0 }
                }
        };

        performTest(input, output);
    }

    @Test
    public void  testDecompose_5x5_3() {

        double[][] input = new double[][] {
                {   6.0,  15.0,   55.0,   72.0,  101.0 },
                {  15.0,  55.0,  225.0,  229.0,  256.0 },
                {  55.0, 225.0,  979.0, 1024.0, 1200.0 },
                {  72.0, 229.0, 1024.0, 2048.0, 2057.0 },
                { 101.0, 256.0, 1200.0, 2057.0, 6000.0 }
        };

        double[][][] output = new double[][][] {
                {
                        {  2.449,  0.000,  0.000,  0.000,  0.000 },
                        {  6.124,  4.183,  0.000,  0.000,  0.000 },
                        { 22.454, 20.917,  6.110,  0.000,  0.000 },
                        { 29.394, 11.713, 19.476, 25.836,  0.000 },
                        { 41.233,  0.837, 42.007,  0.661, 50.340 }
                }
        };

        performTest(input, output);
    }

    @Test
    public void testDecompose_1x1_symmetric_1() {
        double[][] input = new double[][] {
                { 76.0 }
        };

        double[][][] output = new double[][][] {
                {
                        { 1.0 }
                },
                {
                        { 76.0 }
                }
        };

        performTest(input, output);
    }

    @Test
    public void testDecompose_1x1_symmetric_2() {
        double[][] input = new double[][] {
                { -10.0 }
        };

        double[][][] output = new double[][][] {
                {
                        { 1.0 }
                },
                {
                        { -10.0 }
                }
        };

        performTest(input, output);
    }

    @Test
    public void testDecompose_2x2_symmetric_1() {
        double[][] input = new double[][] {
                { -5.0, 12.0 },
                { 12.0,  9.0 }
        };

        double[][][] output = new double[][][] {
                {
                        {  0.867, 0.498 },
                        { -0.498, 0.867 }
                },
                {
                        { -11.892, 0.0 },
                        { 0.0, 15.892 },
                }
        };

        performTest(input, output);
    }

    @Test
    public void testDecompose_2x2_symmetric_2() {
        double[][] input = new double[][] {
                { 25.0, 0 },
                { 0.0, 9.0 }
        };

        double[][][] output = new double[][][] {
                {
                        { 1.0, 0.0 },
                        { 0.0, 1.0 }
                },
                {
                        { 25.0, 0.0 },
                        { 0.0, 9.0 },
                }
        };

        performTest(input, output);
    }

    @Test
    public void testDecompose_3x3_symmetric_1() {
        double[][] input = new double[][] {
                {  1.5, 13.0, 9.4 },
                { 13.0,  2.8, 3.6 },
                {  9.4,  3.6, 4.4 }
        };

        double[][][] output = new double[][][] {
                {
                        { 0.1199,-0.7559, 0.6435 },
                        { 0.5753,  0.5812, 0.5754 },
                        {-0.8090,  0.3012, 0.5046 }
                },
                {
                        { 0.447, 0.0, 0.0 },
                        { 0.0, -12.242, 0.0 },
                        { 0.0, 0.0, 20.495 }
                }
        };

        performTest(input, output);
    }

    @Test
    public void testDecompose_3x3_symmetric_2() {
        double[][] input = new double[][] {
                { 1.0, 0.0, 0.0 },
                { 0.0, 2.0, 0.0 },
                { 0.0, 0.0, 4.0 }
        };

        double[][][] output = new double[][][] {
                {
                        { 1.0, 0.0, 0.0 },
                        { 0.0, 1.0, 0.0 },
                        { 0.0, 0.0, 1.0 }
                },
                {
                        { 1.0, 0.0, 0.0 },
                        { 0.0, 2.0, 0.0 },
                        { 0.0, 0.0, 4.0 }
                }
        };

        performTest(input, output);
    }

    @Test
    public void testDecompose_4x4_symmetric_1() {
        double[][] input = new double[][] {
                { 1.0, 5.0, 7.0, 9.0 },
                { 5.0, 2.0, 8.0, 4.0 },
                { 7.0, 8.0, 4.0, 1.0 },
                { 9.0, 4.0, 1.0, 5.0 }
        };

        double[][][] output = new double[][][] {
                {
                        { 0.537, -0.444, -0.192, -0.691 },
                        { 0.478,  0.788,  0.318, -0.223 },
                        { 0.502, -0.404,  0.590,  0.486 },
                        { 0.481,  0.135, -0.717,  0.486 }
                },
                {
                        { 20.058, 0.0, 0.0, 0.0 },
                        { 0.0, -4.237, 0.0, 0.0 },
                        { 0.0, 0.0, 4.814, 0.0 },
                        { 0.0, 0.0, 0.0, -8.635 }
                }
        };

        performTest(input, output);
    }

    @Test
    public void testDecompose_4x4_symmetric_2() {
        double[][] input = new double[][] {
                { -8.0, 7.0, 2.0, -3.0 },
                { 7.0, 20.0, -4.0, 5.0 },
                { 2.0, -4.0, 4.0, 2.0 },
                { -3.0, 5.0, 2.0, -44.0 }
        };

        double[][][] output = new double[][][] {
                {
                        { -0.947, 0.203, -0.226, 0.1 },
                        { 0.227, 0.961, -0.13, -0.09 },
                        { 0.187, -0.179, -0.965, -0.052 },
                        { 0.127, 0.058, -0.04, 0.989 }

                },
                {
                        { -9.672, 0.0, 0.0, 0.0 },
                        { 0.0, 22.525, 0.0, 0.0 },
                        { 0.0, 0.0, 4.013, 0.0 },
                        { 0.0, 0.0, 0.0, -44.866 }
                }
        };

        performTest(input, output);
    }

    @Test
    public void testDecompose_5x5_symmetric_1() {
        double[][] input = new double[][] {
                { 1.0, 0.0, 0.0, 0.0, 0.0 },
                { 0.0, 2.0, 0.0, 0.0, 0.0 },
                { 0.0, 0.0, 3.0, 0.0, 0.0 },
                { 0.0, 0.0, 0.0, 4.0, 0.0 },
                { 0.0, 0.0, 0.0, 0.0, 5.0 }
        };

        double[][][] output = new double[][][] {
                {
                        { 1.0, 0.0, 0.0, 0.0, 0.0 },
                        { 0.0, 1.0, 0.0, 0.0, 0.0 },
                        { 0.0, 0.0, 1.0, 0.0, 0.0 },
                        { 0.0, 0.0, 0.0, 1.0, 0.0 },
                        { 0.0, 0.0, 0.0, 0.0, 1.0 }
                },
                {
                        { 1.0, 0.0, 0.0, 0.0, 0.0 },
                        { 0.0, 2.0, 0.0, 0.0, 0.0 },
                        { 0.0, 0.0, 3.0, 0.0, 0.0 },
                        { 0.0, 0.0, 0.0, 4.0, 0.0 },
                        { 0.0, 0.0, 0.0, 0.0, 5.0 }
                }
        };

        performTest(input, output);
    }

    @Test
    public void testDecompose_5x5_symmetric_2() {
        double[][] input = new double[][] {
                { 6.2,  4.7,  6.2, 45.9,  8.7 },
                { 4.7, 12.6, 13.5,  0.6, 12.3 },
                { 6.2, 13.5, 25.8,  1.2, 10.0 },
                { 45.9, 0.6,  1.2, 34.1,  6.2 },
                { 8.7, 12.3, 10.0,  6.2, 10.8 }
        };

        double[][][] output = new double[][][] {
                {
                        { -0.108,  0.035,  0.028, 0.580, -0.806 },
                        {  0.507,  0.708, -0.469, 0.141, 0.048 },
                        {  0.711, -0.108,  0.667, 0.186, 0.057 },
                        { -0.296,  0.068,  0.068, 0.749, 0.585 },
                        {  0.372, -0.694, -0.574, 0.218, 0.057 }
                },
                {
                        { 39.207, 0.0, 0.0, 0.0, 0.0 },
                        { 0.0, -1.232, 0.0, 0.0, 0.0 },
                        { 0.0, 0.0, 8.082, 0.0, 0.0 },
                        { 0.0, 0.0, 0.0, 71.862, 0.0 },
                        { 0.0, 0.0, 0.0, 0.0, -28.420 }
                }
        };

        performTest(input, output);
    }

    @Test
    public void testDecompose_2x2_nonSymmetric_1() {
        double[][] input = new double[][] {
                { 1.0, 2.0},
                { 37.0, 24.0 }
        };

        double[][][] output = new double[][][] {
                {
                        { -0.573, -0.122 },
                        { 0.820, -1.572 }
                },
                {
                        { -1.861, 0.0 },
                        { 0.0, 26.861 }
                }
        };

        performTest(input, output);
    }

    @Test
    public void testDecompose_2x2_nonSymmetric_2() {
        double[][] input = new double[][] {
                { 6.0, 99.0},
                { -4.0, 12.0 }
        };

        double[][][] output = new double[][][] {
                {
                        { 4.918, 0.750 },
                        { 0.000, 1.0 }
                },
                {
                        { 9.0, 19.672 },
                        { -19.672, 9.0 }
                }
        };

        performTest(input, output);
    }

    @Test
    public void testDecompose_3x3_nonSymmetric() {
        double[][] input = new double[][] {
                { 16.0, -11.0, 99.0 },
                { 7.0, -2.0, -42.0 },
                { 8.0, -1.0, -7.0 }
        };

        double[][][] output = new double[][][] {
                {
                        { 0.982, 0.644, -0.353 },
                        { -0.026, -0.896, -2.320 },
                        { 0.187, -0.359, -0.186 }
                },
                {
                        { 35.149, 0.0, 0.0 },
                        { 0.0, -23.856, 0.0 },
                        { 0.0, 0.0, -4.293 }
                }
        };

        performTest(input, output);
    }

    @Test
    public void testDecompose_4x4_nonSymmetric() {
        double[][] input = new double[][] {
                { -1.0, -4.0, 4.0, 8.0 },
                { 4.0, 1.0, -8.0, 2.0 },
                { -2.0, -4.0, 5.0, 8.0 },
                { 4.0,-1.0, -8.0, 4.0 }
        };

        double[][][] output = new double[][][] {
                {
                        { -0.497, -0.503, -2.828, -0.0 },
                        { 0.503, -0.497, 0.0, -2.828 },
                        { -0.497, -0.503, -1.414, -0.0 },
                        { 0.503, -0.497, 0.0, -1.414 }
                },
                {
                        { 3.0, 4.0, 0.0, 0.0 },
                        { -4.0, 3.0, 0.0, 0.0 },
                        { 0.0, 0.0, 1.0, 0.0 },
                        { 0.0, 0.0, 0.0, 2.0 }
                }
        };

        performTest(input, output);
    }

    @Test
    public void testDecompose_5x5_nonSymmetric() {
        double[][] input = new double[][] {
                { 26.0, -1.0, 2.0, 15.0, -3.0 },
                { -19.0, -11.0, 21.0, -4.0, 0.0 },
                { 88.0, -22.0, -3.0, -5.0, -17.0 },
                { 15.0, 45.0, 22.0, 42.0, 54.0 },
                { -17.0, 55.0, -9.0, 6.0, 2.0 }
        };

        double[][][] output = new double[][][] {
                {
                        { -0.109, 0.159, -0.525, -0.274, 0.091 },
                        { -0.338, -0.250, -0.015, 0.244, 0.364 },
                        { 0.341, -0.082, -0.713, -0.389, 0.883 },
                        { 0.138, -0.492, -0.996, 0.032, -0.760 },
                        { 0.165, 0.840, 0.153, 0.457, -0.397 }
                },
                {
                        { -23.664, 17.563, 0.0, 0.0, 0.0 },
                        { -17.563, -23.664, 0.0, 0.0, 0.0 },
                        { 0.0, 0.0, 58.024, 0.0, 0.0 },
                        { 0.0, 0.0, 0.0, 22.652, 31.064 },
                        { 0.0, 0.0, 0.0, -31.064, 22.652 }
                }
        };

        performTest(input, output);
    }

    @Test
    public void testDecompose_1x1_3() {

        double[][] input = new double[][] {
                { 15.0 }
        };

        double[][][] output = new double[][][] {
                {
                        { -1.0}
                },
                {
                        { -15.0 }
                }
        };

        performTest(input, output);
    }

    @Test
    public void testDecompose_2x2_3() {

        double[][] input = new double[][] {
                { 5.0, 10.0 },
                { 70.0, 11.0 }
        };

        double[][][] output = new double[][][] {
                {
                        { -0.071, 0.997 },
                        { -0.997, -0.071 }
                },
                {
                        { -70.178, -11.685 },
                        { 0.0, 9.191 }
                }
        };

        performTest(input, output);
    }

    @Test
    public void testDecompose_4x1_1() {

        double[][] input = new double[][] {
                { 8.0 },
                { 2.0 },
                { -10.0 },
                { 54.0 }
        };

        double[][][] output = new double[][][] {
                {
                        { -0.144 },
                        { -0.036 },
                        { 0.180 },
                        { -0.972 }
                },
                {
                        { -55.534 }
                }
        };

        performTest(input, output);
    }

    @Test
    public void testDecompose_3x2_1() {

        double[][] input = new double[][] {
                { 65.0, 4.0 },
                { 9.0, 12.0 },
                { 32.0, 42.0 }
        };

        double[][][] output = new double[][][] {
                {
                        { -0.890, 0.455 },
                        { -0.123, -0.246 },
                        { -0.438, -0.856 }
                },
                {
                        { -73.007, -23.450 },
                        { 0.000, -37.069 }
                }
        };

        performTest(input, output);
    }

    @Test
    public void testDecompose_3x3_4() {

        double[][] input = new double[][] {
                { -8.0, 0.0, 0.0 },
                { 0.0, -4.0, -6.0 },
                { 0.0, 0.0, -2.0 }
        };

        double[][][] output = new double[][][] {
                {
                        { -1.0, 0.0, 0.0 },
                        { 0.0, -1.0, 0.0 },
                        { 0.0, 0.0, -1.0 }
                },
                {
                        { 8.0, 0.0, 0.0 },
                        { 0.0, 4.0, 6.0 },
                        { 0.0, 0.0, 2.0 }
                }
        };

        performTest(input, output);
    }

    @Test
    public void testDecompose_3x3_5() {

        double[][] input = new double[][] {
                { -2.0, 6.0, 14.0 },
                { -10.0, -9.0, 6.0 },
                { 12.0, 16.0, 100.0 }
        };

        double[][][] output = new double[][][] {
                {
                        { -0.127, 0.920, 0.371 },
                        { -0.635, 0.212, -0.743 },
                        { 0.762, 0.330, -0.557 }
                },
                {
                        { 15.748, 17.145, 70.612 },
                        { 0.0, 8.891, 47.167 },
                        { 0.0, 0.0, -54.966 }
                }
        };

        performTest(input, output);
    }

    @Test
    public void testDecompose_4x3_1() {

        double[][] input = new double[][] {
                { 51.0, 4.0, 19.0 },
                { 7.0, 17.0, 77.0 },
                { 5.0, 6.0, 7.0 },
                { 100.0, 1.0, -10.0}
        };

        double[][][] output = new double[][][] {
                {
                        { -0.453, -0.121,  0.364 },
                        { -0.062, -0.928,  0.236 },
                        { -0.044, -0.323, -0.887 },
                        { -0.888, 0.143, -0.158 }
                },
                {
                        { -112.583, -4.024, -4.823 },
                        { 0.0, -18.050, -77.428 },
                        { 0.0, 0.0, 20.509 }
                }
        };

        performTest(input, output);
    }

    @Test
    public void testDecompose_5x5_4() {

        double[][] input = new double[][] {
                { 14.0, 66.0, 11.0, 4.0, 61.0 },
                { 10.0, 22.0, 54.0, -1.0, 1.0 },
                { -19.0, 26.0, 4.0, 44.0, 14.0 },
                { 87.0, -1.0, 34.0, 29.0, -2.0 },
                { 18.0, 43.0, 51.0, 39.0, 16.0 }
        };

        double[][][] output = new double[][][] {
                {
                        { -0.151, -0.754, -0.520, 0.358, 0.097 },
                        { -0.108, -0.242, 0.739, 0.388, 0.483 },
                        { 0.205, -0.342, -0.001, -0.771, 0.496 },
                        { -0.941, 0.173, -0.088, -0.211, 0.180 },
                        { -0.195, -0.475, 0.419, -0.287, -0.692 }
                },
                {
                        { -92.466, -14.459, -48.602, -26.334, -7.700 },
                        { 0.0, -84.599, -41.067, -31.363, -58.992 },
                        { 0.0, 0.0, 52.549, 10.907, -24.128 },
                        { 0.0, 0.0, 0.0, -50.189, 7.268 },
                        { 0.0, 0.0, 0.0, 0.0, 1.908 }
                }
        };

        performTest(input, output);
    }

    @Test
    public void testDecompose_1x1_4() {

        double[][] input = new double[][] {
                { 89.0 }
        };

        double[][][] output = new double[][][] {
                {
                        { 1.0 }
                },
                {
                        { 89.0 }
                },
                {
                        { 1.0 }
                }
        };

        performTest(input, output);
    }

    @Test
    public void testDecompose_2x2_4() {

        double[][] input = new double[][] {
                { 7.0, 19.0 },
                { -1.0, 100.0 }
        };

        double[][][] output = new double[][][] {
                {
                        { 0.187, -0.982 },
                        { 0.982, 0.187 }
                },
                {
                        { 101.790, 0.0 },
                        { 0.0, 7.064 }
                },
                {
                        { 0.003, -1.0 },
                        { 1.0, 0.003 }
                }
        };

        performTest(input, output);
    }

    @Test
    public void testDecompose_3x3_6() {

        double[][] input = new double[][] {
                { 2.0, 0.0, 0.0 },
                { 0.0, 4.0, 0.0 },
                { 0.0, 0.0, 8.0 }
        };

        double[][][] output = new double[][][] {
                {
                        { 0.0, 0.0, -1.0 },
                        { 0.0, -1.0, 0.0 },
                        { 1.0, 0.0, 0.0 }
                },
                {
                        { 8.0, 0.0, 0.0 },
                        { 0.0, 4.0, 0.0 },
                        { 0.0, 0.0, 2.0 }
                },
                {
                        { 0.0, 0.0, -1.0 },
                        { 0.0, -1.0, 0.0 },
                        { 1.0, 0.0, 0.0 }
                }
        };

        performTest(input, output);
    }

    @Test
    public void testDecompose_3x1_1() {

        double[][] input = new double[][] {
                { 6.0 },
                { 19.0 },
                { 20.0 }
        };

        double[][][] output = new double[][][] {
                {
                        { -0.213 },
                        { -0.673 },
                        { -0.708 }
                },
                {
                        { 28.231 }
                },
                {
                        { -1.0 }
                }
        };

        performTest(input, output);
    }

    @Test
    public void testDecompose_4x4_3() {

        double[][] input = new double[][] {
                { 9.0, 18.0, -1.0, 43.0 },
                { -9.0, 14.0, 85.0, -2.0 },
                { 1.0, 100.0, 53.0, -22.0 },
                { 28.0, 15.0, -28.0, 5.0 }
        };

        double[][][] output = new double[][][] {
                {
                        { -0.041, -0.261, 0.928, -0.262 },
                        { -0.538, 0.664, 0.285, 0.434 },
                        { -0.838, -0.467, -0.219, -0.179 },
                        { 0.087, -0.522, 0.095, 0.843 }
                },
                {
                        { 130.97, 0.0, 0.0, 0.0 },
                        { 0.0, 72.357, 0.0, 0.0 },
                        { 0.0, 0.0, 46.283, 0.0 },
                        { 0.0, 0.0, 0.0, 18.496 }
                },
                {
                        { 0.046, -0.323, 0.178, 0.928 },
                        { -0.693, -0.691, 0.005, -0.207 },
                        { -0.706, 0.643, 0.196, 0.222 },
                        { 0.139, -0.067, 0.964, -0.215 }
                }
        };

        performTest(input, output);
    }

    @Test
    public void testDecompose_5x5_5() {

        double[][] input = new double[][] {
                { -1.0, 8.0, 17.0, 100.0, 10.0 },
                { 4.0, -5.0, 44.0, 11.0, -66.0 },
                { 22.0, 54.0, 24.0, -14.0, 1.0 },
                { 99.0, 25.0, 77.0, -23.0, 4.0 },
                { 11.0, 7.0, -15.0, -4.0, 81.0 }
        };

        double[][][] output = new double[][][] {
                {
                        { -0.072, 0.35, 0.927, 0.039, -0.105 },
                        { 0.252, 0.624, -0.132, -0.058, 0.725 },
                        { 0.337, -0.113, 0.036, 0.932, 0.06 },
                        { 0.901, -0.158, 0.124, -0.338, -0.182 },
                        { -0.072, -0.671, 0.326, -0.110, 0.653 }
                },
                {
                        { 141.208, 0.0, 0.0, 0.0, 0.0 },
                        { 0.0, 112.592, 0.0, 0.0, 0.0 },
                        { 0.0, 0.0, 101.124, 0.0, 0.0 },
                        { 0.0, 0.0, 0.0, 44.635, 0.0 },
                        { 0.0, 0.0, 0.0, 0.0, 10.868 }
                },
                {
                        { 0.686, -0.207, 0.15, -0.324, -0.599 },
                        { 0.272, -0.134, 0.152, 0.934, -0.109 },
                        { 0.626, 0.254, 0.153, -0.087, 0.716 },
                        { -0.210, 0.442, 0.856, -0.034, -0.161 },
                        { -0.136, -0.824, 0.444, -0.115,  0.303 }
                }
        };

        performTest(input, output);
    }

    private void performTest(double[][] input, double[][][] output) {

        for (Factory factory: LinearAlgebra.FACTORIES) {

            Matrix a = factory.createMatrix(input);
            MatrixDecompositor decompositor = a.withDecompositor(decompositorFactory());
            Matrix[] decomposition = decompositor.decompose(factory);

            assertEquals(output.length, decomposition.length);

            for (int i = 0; i < decomposition.length; i++) {
                assertEquals(new MockMatrix(factory.createMatrix(output[i])),
                             new MockMatrix(decomposition[i]));
            }
        }
    }
}
