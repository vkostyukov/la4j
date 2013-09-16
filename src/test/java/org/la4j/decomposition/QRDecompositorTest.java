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

import org.la4j.matrix.Matrices;

public class QRDecompositorTest extends AbstractDecompositorTest {

    public void testDecompose_2x2() {

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

        performTest(Matrices.QR_DECOMPOSITOR, input, output);
    }

    public void testDecompose_4x1() {

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

        performTest(Matrices.QR_DECOMPOSITOR, input, output);
    }

    public void testDecompose_3x2() {

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

        performTest(Matrices.QR_DECOMPOSITOR, input, output);
    }

    public void testDecompose_3x3() {

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

        performTest(Matrices.QR_DECOMPOSITOR, input, output);
    }

    public void testDecompose_3x3_2() {

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

        performTest(Matrices.QR_DECOMPOSITOR, input, output);
    }
}
