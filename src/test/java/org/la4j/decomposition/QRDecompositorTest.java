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

public class QRDecompositorTest extends AbstractDecompositorTest {

    @Override
    public LinearAlgebra.DecompositorFactory decompositorFactory() {
        return LinearAlgebra.QR;
    }

    @Test
    public void testDecompose_1x1() {
        double[][] input = new double[][]{
                {15.0}
        };
        double[][][] output = new double[][][]{
                {
                        {-1.0}
                },
                {
                        {-15.0}
                }
        };
        performTest(input, output);
    }

    @Test
    public void testDecompose_2x2() {
        double[][] input = new double[][]{
                {5.0, 10.0},
                {70.0, 11.0}
        };
        double[][][] output = new double[][][]{
                {
                        {-0.071, 0.997},
                        {-0.997, -0.071}
                },
                {
                        {-70.178, -11.685},
                        {0.0, 9.191}
                }
        };
        performTest(input, output);
    }

    @Test
    public void testDecompose_4x1() {
        double[][] input = new double[][]{
                {8.0},
                {2.0},
                {-10.0},
                {54.0}
        };
        double[][][] output = new double[][][]{
                {
                        {-0.144},
                        {-0.036},
                        {0.180},
                        {-0.972}
                },
                {
                        {-55.534}
                }
        };
        performTest(input, output);
    }

    @Test
    public void testDecompose_3x2() {
        double[][] input = new double[][]{
                {65.0, 4.0},
                {9.0, 12.0},
                {32.0, 42.0}
        };
        double[][][] output = new double[][][]{
                {
                        {-0.890, 0.455},
                        {-0.123, -0.246},
                        {-0.438, -0.856}
                },
                {
                        {-73.007, -23.450},
                        {0.000, -37.069}
                }
        };
        performTest(input, output);
    }

    @Test
    public void testDecompose_3x3() {
        double[][] input = new double[][]{
                {-8.0, 0.0, 0.0},
                {0.0, -4.0, -6.0},
                {0.0, 0.0, -2.0}
        };
        double[][][] output = new double[][][]{
                {
                        {-1.0, 0.0, 0.0},
                        {0.0, -1.0, 0.0},
                        {0.0, 0.0, -1.0}
                },
                {
                        {8.0, 0.0, 0.0},
                        {0.0, 4.0, 6.0},
                        {0.0, 0.0, 2.0}
                }
        };
        performTest(input, output);
    }

    @Test
    public void testDecompose_3x3_2() {
        double[][] input = new double[][]{
                {-2.0, 6.0, 14.0},
                {-10.0, -9.0, 6.0},
                {12.0, 16.0, 100.0}
        };
        double[][][] output = new double[][][]{
                {
                        {-0.127, 0.920, 0.371},
                        {-0.635, 0.212, -0.743},
                        {0.762, 0.330, -0.557}
                },
                {
                        {15.748, 17.145, 70.612},
                        {0.0, 8.891, 47.167},
                        {0.0, 0.0, -54.966}
                }
        };
        performTest(input, output);
    }

    @Test
    public void testDecompose_4x3() {
        double[][] input = new double[][]{
                {51.0, 4.0, 19.0},
                {7.0, 17.0, 77.0},
                {5.0, 6.0, 7.0},
                {100.0, 1.0, -10.0}
        };
        double[][][] output = new double[][][]{
                {
                        {-0.453, -0.121, 0.364},
                        {-0.062, -0.928, 0.236},
                        {-0.044, -0.323, -0.887},
                        {-0.888, 0.143, -0.158}
                },
                {
                        {-112.583, -4.024, -4.823},
                        {0.0, -18.050, -77.428},
                        {0.0, 0.0, 20.509}
                }
        };
        performTest(input, output);
    }

    @Test
    public void testDecompose_5x5() {
        double[][] input = new double[][]{
                {14.0, 66.0, 11.0, 4.0, 61.0},
                {10.0, 22.0, 54.0, -1.0, 1.0},
                {-19.0, 26.0, 4.0, 44.0, 14.0},
                {87.0, -1.0, 34.0, 29.0, -2.0},
                {18.0, 43.0, 51.0, 39.0, 16.0}
        };
        double[][][] output = new double[][][]{
                {
                        {-0.151, -0.754, -0.520, 0.358, 0.097},
                        {-0.108, -0.242, 0.739, 0.388, 0.483},
                        {0.205, -0.342, -0.001, -0.771, 0.496},
                        {-0.941, 0.173, -0.088, -0.211, 0.180},
                        {-0.195, -0.475, 0.419, -0.287, -0.692}
                },
                {
                        {-92.466, -14.459, -48.602, -26.334, -7.700},
                        {0.0, -84.599, -41.067, -31.363, -58.992},
                        {0.0, 0.0, 52.549, 10.907, -24.128},
                        {0.0, 0.0, 0.0, -50.189, 7.268},
                        {0.0, 0.0, 0.0, 0.0, 1.908}
                }
        };
        performTest(input, output);
    }
}
