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
 * Contributor(s): Evgenia Krivova
 * 
 */

package org.la4j.matrix;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import junit.framework.TestCase;

import org.la4j.factory.Factory;
import org.la4j.vector.Vector;

public abstract class AbstractMatrixTest extends TestCase {

    public abstract Factory factory();

    public void testAccess_3x3() {

        Matrix a = factory().createMatrix(new double[][] {
                { 1.0, 0.0, 3.0 },
                { 0.0, 5.0, 0.0 },
                { 7.0, 0.0, 9.0 }
        });

        a.set(0, 1, a.get(1, 1) * 2);
        assertEquals(10.0, a.get(0, 1));
    }

    public void testGetColumn_4x4() {

        Matrix a = factory().createMatrix(new double[][] {
                { 8.0, 3.0, 1.0, 9.0 },
                { 4.0, 9.0, 6.0, 6.0 },
                { 9.0, 1.0, 1.0, 4.0 },
                { 5.0, 7.0, 3.0, 0.0 }
        });

        Vector b = factory().createVector(new double[] { 8.0, 4.0, 9.0, 5.0 });

        Vector c = factory().createVector(new double[] { 1.0, 6.0, 1.0, 3.0 });

        assertEquals(b, a.getColumn(0));
        assertEquals(c, a.getColumn(2));
    }

    public void testGetRow_4x4() {

        Matrix a = factory().createMatrix(new double[][] {
                { 8.0, 3.0, 1.0, 9.0 },
                { 4.0, 9.0, 6.0, 6.0 },
                { 9.0, 1.0, 1.0, 4.0 },
                { 5.0, 7.0, 3.0, 0.0 }
        });

        Vector b = factory().createVector(new double[] { 8.0, 3.0, 1.0, 9.0 });

        Vector c = factory().createVector(new double[] { 9.0, 1.0, 1.0, 4.0 });

        assertEquals(b, a.getRow(0));
        assertEquals(c, a.getRow(2));
    }

    public void testAssign_3x3() {

        Matrix a = factory().createMatrix(new double[][] {
                { 5.0, 5.0, 5.0 },
                { 5.0, 5.0, 5.0 },
                { 5.0, 5.0, 5.0 }
        });

        Matrix b = factory().createMatrix(3, 3);

        b.assign(5.0);

        assertEquals(a, b);
    }

    public void testResize_3x3_to_4x4_to_2x2() {

        Matrix a = factory().createMatrix(new double[][] { 
                { 1.0, 0.0, 0.0 },
                { 0.0, 5.0, 0.0 }, 
                { 0.0, 0.0, 9.0 } 
        });

        Matrix b = factory().createMatrix(new double[][] { 
                { 1.0, 0.0, 0.0, 0.0 },
                { 0.0, 5.0, 0.0, 0.0 }, 
                { 0.0, 0.0, 9.0, 0.0 },
                { 0.0, 0.0, 0.0, 0.0 } 
        });

        Matrix c = factory().createMatrix(new double[][] { 
                { 1.0, 0.0 }, 
                { 0.0, 5.0 } 
        });

        a = a.resize(a.rows() + 1, a.columns() + 1);
        assertEquals(b, a);

        a = a.resize(a.rows() - 2, a.columns() - 2);
        assertEquals(c, a);
    }

    public void testResize_2x3_to_3x4_to_1x2() {

        Matrix a = factory().createMatrix(new double[][] { 
                { 1.0, 0.0, 0.0 },
                { 0.0, 5.0, 0.0 }, 
        });

        Matrix b = factory().createMatrix(new double[][] { 
                { 1.0, 0.0, 0.0, 0.0 },
                { 0.0, 5.0, 0.0, 0.0 }, 
                { 0.0, 0.0, 0.0, 0.0 },
        });

        Matrix c = factory().createMatrix(new double[][] { 
                { 1.0, 0.0 }, 
        });

        a = a.resize(a.rows() + 1, a.columns() + 1);
        assertEquals(b, a);

        a = a.resize(a.rows() - 2, a.columns() - 2);
        assertEquals(c, a);
    }

    public void testResize_2x3_to_2x4_to_2x1() {

        Matrix a = factory().createMatrix(new double[][] { 
                { 1.0, 0.0, 0.0 },
                { 0.0, 5.0, 0.0 }, 
        });

        Matrix b = factory().createMatrix(new double[][] { 
                { 1.0, 0.0, 0.0, 0.0 },
                { 0.0, 5.0, 0.0, 0.0 }, 
        });

        Matrix c = factory().createMatrix(new double[][] { 
                { 1.0 },
                { 0.0 }
        });

        a = a.resize(a.rows(), a.columns() + 1);
        assertEquals(b, a);

        a = a.resize(a.rows(), a.columns() - 3);
        assertEquals(c, a);
    }

    public void testResize_3x5_to_4x5_to_2x5() {

        Matrix a = factory().createMatrix(new double[][] { 
                { 1.0, 0.0, 0.0, 0.0, 0.0 },
                { 0.0, 5.0, 0.0, 0.0, 0.0 },
                { 0.0, 0.0, 7.0, 0.0, 0.0 }
        });

        Matrix b = factory().createMatrix(new double[][] { 
                { 1.0, 0.0, 0.0, 0.0, 0.0 },
                { 0.0, 5.0, 0.0, 0.0, 0.0 },
                { 0.0, 0.0, 7.0, 0.0, 0.0 },
                { 0.0, 0.0, 0.0, 0.0, 0.0 },
        });

        Matrix c = factory().createMatrix(new double[][] { 
                { 1.0, 0.0, 0.0, 0.0, 0.0 },
                { 0.0, 5.0, 0.0, 0.0, 0.0 },
        });

        a = a.resize(a.rows() + 1, a.columns());
        assertEquals(b, a);

        a = a.resize(a.rows() - 2, a.columns());
        assertEquals(c, a);
    }

    public void testSlice_4x4_to_2x2() {

        Matrix a = factory().createMatrix(new double[][] { 
                { 1.0, 0.0, 0.0, 0.0 },
                { 0.0, 5.0, 0.0, 0.0 }, 
                { 0.0, 0.0, 9.0, 0.0 },
                { 0.0, 0.0, 0.0, 15.0 } 
        });

        Matrix b = factory().createMatrix(new double[][] { 
                { 5.0, 0.0 },
                { 0.0, 9.0 } 
        });

        Matrix c = a.slice(1, 1, 3, 3);
        assertEquals(b, c);
    }

    public void testSwap_3x3() {

        Matrix a = factory().createMatrix(new double[][] { 
                { 1.0, 0.0, 0.0 },
                { 0.0, 5.0, 0.0 }, 
                { 0.0, 0.0, 9.0 } 
        });

        Matrix b = factory().createMatrix(new double[][] { 
                { 0.0, 0.0, 9.0 },
                { 0.0, 5.0, 0.0 }, 
                { 1.0, 0.0, 0.0 } 
        });

        Matrix c = factory().createMatrix(new double[][] { 
                { 9.0, 0.0, 0.0 },
                { 0.0, 5.0, 0.0 }, 
                { 0.0, 0.0, 1.0 } 
        });

        a.swapRows(0, 2);
        assertEquals(b, a);

        b.swapColumns(0, 2);
        assertEquals(c, b);
    }

    public void testSwap_2x4() {

        Matrix a = factory().createMatrix(new double[][] { 
                { 1.0, 0.0, 0.0, 3.0 },
                { 0.0, 5.0, 4.0, 0.0 } 
        });

        Matrix b = factory().createMatrix(new double[][] { 
                { 0.0, 5.0, 4.0, 0.0 },
                { 1.0, 0.0, 0.0, 3.0 } 
        });

        Matrix c = factory().createMatrix(new double[][] { 
                { 0.0, 4.0, 5.0, 0.0 },
                { 1.0, 0.0, 0.0, 3.0 } 
        });

        a.swapRows(0, 1);
        assertEquals(b, a);

        b.swapColumns(1, 2);
        assertEquals(c, b);
    }

    public void testSwap_5x3() {

        Matrix a = factory().createMatrix(new double[][] { 
                { 1.0, 0.0, 0.0 },
                { 0.0, 5.0, 4.0 },
                { 7.0, 0.0, 2.0 },
                { 0.0, 8.0, 0.0 },
                { 5.0, 0.0, 6.0 }
        });

        Matrix b = factory().createMatrix(new double[][] { 
                { 1.0, 0.0, 0.0 },
                { 0.0, 5.0, 4.0 },
                { 0.0, 8.0, 0.0 },
                { 7.0, 0.0, 2.0 },
                { 5.0, 0.0, 6.0 } 
        });

        Matrix c = factory().createMatrix(new double[][] { 
                { 1.0, 0.0, 0.0 },
                { 0.0, 4.0, 5.0 },
                { 0.0, 0.0, 8.0 },
                { 7.0, 2.0, 0.0 },
                { 5.0, 6.0, 0.0 } 
        });

        a.swapRows(2, 3);
        assertEquals(b, a);

        b.swapColumns(1, 2);
        assertEquals(c, b);
    }

    public void testTranspose_4x4() {

        double arrayA[][] = new double[][] { 
                { 0.0, 14.2, 0.0, 4.0 },
                { 0.0, 5.0, 10.0, 0.0 }, 
                { 0.0, 3.0, 0.0, 2.3 },
                { 11.0, 7.0, 0.0, 1.0 }
        };

        double arrayB[][] = new double[][] { 
                { 0.0, 0.0, 0.0, 11.0 },
                { 14.2, 5.0, 3.0, 7.0 }, 
                { 0.0, 10.0, 0.0, 0.0 },
                { 4.0, 0.0, 2.3, 1.0 }
        };

        Matrix a = factory().createMatrix(arrayA);
        Matrix b = factory().createMatrix(arrayB);

        Matrix c = a.transpose();
        assertEquals(b, c);

        Matrix d = c.transpose();
        assertEquals(a, d);
    }

    public void testTranspose_6x5() {

        double arrayA[][] = new double[][] {
                {8.93, 3.96, 7.37, 3.43, 7.05},
                {5.88, 8.26, 5.79, 9.08, 7.75},
                {6.57, 2.51, 8.8, 1.16, 8.11},
                {9.3, 9.61, 0.87, 2.3, 2.93},
                {3.65, 4.63, 7.83, 3.66, 9.04},
                {0.08, 6.12, 6.15, 4.93, 6.72}
        };

        double arrayB[][] = new double[][] {
                {8.93, 5.88, 6.57, 9.3, 3.65, 0.08},
                {3.96, 8.26, 2.51, 9.61, 4.63, 6.12},
                {7.37, 5.79, 8.8, 0.87, 7.83, 6.15},
                {3.43, 9.08, 1.16, 2.3, 3.66, 4.93},
                {7.05, 7.75, 8.11, 2.93, 9.04, 6.72}
        };

        Matrix a = factory().createMatrix(arrayA);
        Matrix b = factory().createMatrix(arrayB);

        Matrix c = a.transpose();
        assertEquals(b, c);

        Matrix d = c.transpose();
        assertEquals(a, d);
    }

    public void testAdd() {

        double arrayA[][] = new double[][] { 
                { 1.0, 0.0, 0.0 },
                { 0.0, 5.0, 0.0 }, 
                { 0.0, 0.0, 9.0 } 
        };

        double arrayB[][] = new double[][] { 
                { 11.0, 10.0, 10.0 },
                { 10.0, 15.0, 10.0 }, 
                { 10.0, 10.0, 19.0 } 
        };

        double arrayC[][] = new double[][] { 
                { 2.0, 0.0, 0.0 },
                { 0.0, 10.0, 0.0 }, 
                { 0.0, 0.0, 18.0 } 
        };

        Matrix a = factory().createMatrix(arrayA);
        Matrix c = factory().createMatrix(arrayB);
        Matrix d = factory().createMatrix(arrayC);

        assertEquals(c, a.add(10.0));
        assertEquals(d, a.add(a));
    }

    public void testSubtract() {

        double arrayA[][] = new double[][] { 
                { 1.0, 0.0, 0.0 },
                { 0.0, 5.0, 0.0 }, 
                { 0.0, 0.0, 9.0 } 
        };

        double arrayB[][] = new double[][] { 
                { -9.0, -10.0, -10.0 },
                { -10.0, -5.0, -10.0 }, 
                { -10.0, -10.0, -1.0 } 
        };

        double arrayC[][] = new double[][] { 
                { 0.0, 0.0, 0.0 },
                { 0.0, 0.0, 0.0 }, 
                { 0.0, 0.0, 0.0 } 
        };

        Matrix a = factory().createMatrix(arrayA);
        Matrix b = factory().createMatrix(arrayB);
        Matrix c = factory().createMatrix(arrayC);

        assertEquals(b, a.subtract(10.0));
        assertEquals(c, a.subtract(a));
    }

    public void testMultiply_1x1_1x1() {

        double arrayA[][] = new double[][] {
                {3.37}
        };

        double arrayB[][] = new double[][] {
                {8.48}
        };

        double arrayC[][] = new double[][] {
                {28.5776}
        };

        Matrix a = factory().createMatrix(arrayA);
        Matrix b = factory().createMatrix(arrayB);
        Matrix c = factory().createMatrix(arrayC);

        Matrix f = a.multiply(b);
        assertEquals(c, f);
    }

    public void testMultiply_2x2_2x2() {

        double arrayA[][] = new double[][] {
                {2.46, 1.68},
                {7.57, 2.47}
        };

        double arrayB[][] = new double[][] {
                {3.85, 8.28},
                {8.02, 8.39}
        };

        double arrayC[][] = new double[][] {
                {22.9446, 34.464},
                {48.9539, 83.4029}
        };

        Matrix a = factory().createMatrix(arrayA);
        Matrix b = factory().createMatrix(arrayB);
        Matrix c = factory().createMatrix(arrayC);

        Matrix f = a.multiply(b);
        assertEquals(c, f);
    }

    public void testMultiply_4x4_4x4() {

        double arrayA[][] = new double[][] {
                { 8.0, 3.0, 1.0, 9.0 },
                { 4.0, 9.0, 6.0, 6.0 },
                { 9.0, 1.0, 1.0, 4.0 },
                { 5.0, 7.0, 3.0, 0.0 }
        };

        double arrayB[][] = new double[][] {
                { 4.0, 9.0, 0.0, 3.0 },
                { 6.0, 7.0, 7.0, 6.0 },
                { 9.0, 4.0, 3.0, 3.0 },
                { 4.0, 4.0, 1.0, 6.0 }
        };

        double arrayC[][] = new double[][] {
                { 95.0, 133.0, 33.0, 99.0 },
                { 148.0, 147.0, 87.0, 120.0 },
                { 67.0, 108.0, 14.0, 60.0 },
                { 89.0, 106.0, 58.0, 66.0 }
        };

        Matrix a = factory().createMatrix(arrayA);
        Matrix b = factory().createMatrix(arrayB);
        Matrix c = factory().createMatrix(arrayC);

        Matrix f = a.multiply(b);
        assertEquals(c, f);
    }

    public void testMultiply_4x1_1x4() {

        double arrayA[][] = new double[][] {
                {6.31},
                {6.06},
                {4.94},
                {9.62}
        };

        double arrayB[][] = new double[][] {
                {5.19, 6.06, 6.12, 5.92}
        };

        double arrayC[][] = new double[][] {
                {32.7489, 38.2386, 38.6172, 37.3552},
                {31.4514, 36.7236, 37.0872, 35.8752},
                {25.6386, 29.9364, 30.2328, 29.2448},
                {49.9278, 58.2972, 58.8744, 56.9504}
        };

        Matrix a = factory().createMatrix(arrayA);
        Matrix b = factory().createMatrix(arrayB);
        Matrix c = factory().createMatrix(arrayC);

        Matrix f = a.multiply(b);
        assertEquals(c, f);
    }

    public void testMultiply_1x10_10x1() {

        double arrayA[][] = new double[][] {
                {0.28, 1.61, 5.11, 1.71, 2.21, 5.97, 2.61, 2.58, 0.07, 3.78}
        };

        double arrayB[][] = new double[][] {
                {9.81},
                {0.14},
                {8.91},
                {8.54},
                {0.97},
                {2.55},
                {1.11},
                {2.52},
                {7.71},
                {1.69}
        };

        double arrayC[][] = new double[][] {
                {96.7995}
        };

        Matrix a = factory().createMatrix(arrayA);
        Matrix b = factory().createMatrix(arrayB);
        Matrix c = factory().createMatrix(arrayC);

        Matrix f = a.multiply(b);
        assertEquals(c, f);
    }

   public void testMultiply_3x2_2x3() {

       double arrayA[][] = new double[][] {
           { 1.0, 9.0 },
           { 9.0, 1.0 },
           { 8.0, 9.0 }
       };

       double arrayB[][] = new double[][] {
           { 0.0, 3.0, 0.0 },
           { 2.0, 0.0, 4.0 }
       };

       double arrayC[][] = new double[][] {
           { 18.0, 3.0, 36.0 },
           { 2.0, 27.0, 4.0 },
           { 18.0, 24.0, 36.0 }
       };

        Matrix a = factory().createMatrix(arrayA);
        Matrix b = factory().createMatrix(arrayB);
        Matrix c = factory().createMatrix(arrayC);

        Matrix f = a.multiply(b);
        assertEquals(c, f);
   }

   public void testMultiply_4x9_9x4() {

      double arrayA[][] = new double[][] {
          {5.98, 3.76, 9.01, 9.68, 2.12, 6.34, 0.64, 6.22, 1.16},
          {8.4, 9.65, 7.06, 2.56, 7.66, 4.69, 3.29, 8.6, 8.55},
          {4.99, 7.06, 6.07, 7.53, 0.08, 1.08, 9.69, 8.51, 6.61},
          {4.72, 7.06, 4.0, 0.75, 2.45, 4.4, 8.33, 5.81, 0.57}
      };

      double arrayB[][] = new double[][] {
          {9.28, 7.63, 4.1, 4.71},
          {4.68, 2.82, 9.18, 5.39},
          {4.54, 6.86, 1.29, 5.4},
          {8.72, 2.06, 4.28, 7.37},
          {2.43, 3.7, 7.52, 5.87},
          {8.21, 9.36, 4.85, 0.3},
          {9.87, 8.19, 5.03, 6.14},
          {9.47, 4.28, 3.86, 3.12},
          {5.29, 4.41, 5.23, 4.85}
      };

      double arrayC[][] = new double[][] {
          {326.9658, 242.1452, 192.0747, 211.7362},
          {393.7521, 318.7092, 317.9021, 283.44},
          {392.8255, 270.4737, 247.3277, 268.7303},
          {283.873, 230.76, 199.6044, 175.1515}
      };

       Matrix a = factory().createMatrix(arrayA);
       Matrix b = factory().createMatrix(arrayB);
       Matrix c = factory().createMatrix(arrayC);

       Matrix f = a.multiply(b);
       assertEquals(c, f);
   }

    public void testDiv() {

        double arrayA[][] = new double[][] {
                { 1.0, 0.0, 0.0 },
                { 0.0, 5.0, 0.0 },
                { 0.0, 0.0, 9.0 }
        };

        double arrayB[][] = new double[][] {
                { 0.1, 0.0, 0.0 },
                { 0.0, 0.5, 0.0 },
                { 0.0, 0.0, 0.9 }
        };

        Matrix a = factory().createMatrix(arrayA);
        Matrix b = factory().createMatrix(arrayB);

        Matrix c = a.div(10.0);

        assertEquals(b, c);
    }

    public void testKronecker() {
        Matrix a = factory().createMatrix(new double[][] {
                { 1.0, 0.0, 3.0 },
                { 0.0, 5.0, 0.0 }
        });

        Matrix b = factory().createMatrix(new double[][] {
                { 10.0, 0.0 },
                { 0.0, 20.0 }
        });

        Matrix c = factory().createMatrix(new double[][] {
                { 10.0, 0.0, 0.0, 0.0, 30.0, 0.0 },
                { 0.0, 20.0, 0.0, 0.0, 0.0, 60.0 },
                { 0.0, 0.0, 50.0, 0.0, 0.0, 0.0 },
                { 00.0, 0.0, 0.0, 100.0, 0.0, 0.0 }
        });

        assertEquals(c, a.kronecker(b));
    }

    public void testTrace() {

        double array[][] = new double[][] {
                { 1.0, 0.0, 0.0 },
                { 0.0, 5.0, 0.0 },
                { 0.0, 0.0, 9.0 }
        };

        Matrix a = factory().createMatrix(array);

        assertTrue(Math.abs(a.trace() - 15.0) < Matrices.EPS);
    }

    public void testProduct() {

        double array[][] = new double[][] {
                { 1.0, 0.0, 0.0 },
                { 0.0, 5.0, 0.0 },
                { 0.0, 0.0, 9.0 }
        };

        Matrix a = factory().createMatrix(array);

        assertTrue(Math.abs(a.product() - 45.0) < Matrices.EPS);
    }

    public void testDeterminant() {

        double array[][] = new double[][] {
                { 1.0, 0.0, 0.0 },
                { 0.0, 5.0, 0.0 },
                { 0.0, 0.0, 9.0 } 
        };

        Matrix a = factory().createMatrix(array);

        assertTrue(Math.abs(a.determinant() - 45.0) < Matrices.EPS);
    }

    public void testRank_3x3() {

        double array[][] = new double[][] {
                { 1.0, 0.0, 0.0 },
                { 0.0, 5.0, 0.0 },
                { 0.0, 0.0, 9.0 } 
        };

        Matrix a = factory().createMatrix(array);

        assertEquals(3, a.rank());
    }
    
    public void testRank_4x4() {

        double array[][] = new double[][] {
                { 1.0, 0.0, 0.0, 3.5 },
                { 0.0, 0.0, 0.0, 2.0 },
                { 0.0, 0.0, 9.0, 0.0 },
                { 1.0, 0.0, 0.0, 0.0 }
        };

        Matrix a = factory().createMatrix(array);

        assertEquals(3, a.rank());
    }

    public void testRank_2x4() {

        double array[][] = new double[][] {
                { 1.0, 0.0, 0.0, 3.5 },
                { 0.0, 1.3, 0.0, 2.0 }
        };

        Matrix a = factory().createMatrix(array);

        assertEquals(2, a.rank());
    }
    
    public void testRank_5x3() {

        double array[][] = new double[][] {
                { 1.0, 0.0, 0.0 },
                { 0.0, 0.0, 0.0 },
                { 1.0, 0.0, 9.0 },
                { 1.0, 0.0, 0.0 },
                { 1.0, 0.0, 0.0 }
        };

        Matrix a = factory().createMatrix(array);

        assertEquals(2, a.rank());
    }
    
    public void testRank_1x4() {

        double array[][] = new double[][] {
                { 0.0, 1.0, 0.0, 0.0 }
        };

        Matrix a = factory().createMatrix(array);

        assertEquals(1, a.rank());
    }
    
    public void testRank_nullMatrix() {

        double array[][] = new double[][] {
                { 0.0, 0.0, 0.0 },
                { 0.0, 0.0, 0.0 },
                { 0.0, 0.0, 0.0 } 
        };

        Matrix a = factory().createMatrix(array);

        assertEquals(0, a.rank());
    }
    
    public void testRank_emptyMatrix() {

        double array[][] = new double[][] {
                { }
        };

        Matrix a = factory().createMatrix(array);

        assertEquals(0, a.rank());
    }
    
    public void testRowAccess_3x3() {

        Matrix a = factory().createMatrix(new double[][] {
                { 1.0, 0.0, 0.0 },
                { 0.0, 5.0, 0.0 },
                { 0.0, 0.0, 9.0 }
        });

        Matrix b = factory().createMatrix(new double[][] {
                 { 1.0, 0.0, 0.0 },
                 { 0.0, 5.0, 0.0 },
                 { 1.0, 0.0, 0.0 }	
        });

        a.setRow(2, a.getRow(0));

        assertEquals(b, a);
    }

    public void testRowAccess_2x4() {

        Matrix a = factory().createMatrix(new double[][] {
                { 1.0, 0.0, 4.0, 0.0 },
                { 0.0, 5.0, 0.0, 7.0 },
        });

        Matrix b = factory().createMatrix(new double[][] {
                 { 1.0, 0.0, 4.0, 0.0 },
                 { 1.0, 0.0, 4.0, 0.0 },
        });

        a.setRow(1, a.getRow(0));

        assertEquals(b, a);
    }

    public void testRowAccess_5x3() {

        Matrix a = factory().createMatrix(new double[][] {
                { 1.0, 0.0, 4.0 },
                { 0.0, 5.0, 3.0 },
                { 9.0, 0.0, 0.0 },
                { 0.0, 1.0, 8.0 },
                { 2.0, 0.0, 0.0 }
        });

        Matrix b = factory().createMatrix(new double[][] {
                { 1.0, 0.0, 4.0 },
                { 0.0, 5.0, 3.0 },
                { 9.0, 0.0, 0.0 },
                { 9.0, 0.0, 0.0 },
                { 2.0, 0.0, 0.0 }
        });

        a.setRow(3, a.getRow(2));

        assertEquals(b, a);
    }

    public void testColumnAccess_3x3() {

        Matrix a = factory().createMatrix(new double[][] {
                { 1.0, 0.0, 0.0 },
                { 0.0, 5.0, 0.0 },
                { 0.0, 0.0, 9.0 }
        });

        Matrix b = factory().createMatrix(new double[][] { 
                { 1.0, 0.0, 1.0 },
                { 0.0, 5.0, 0.0 }, 
                { 0.0, 0.0, 0.0 } 
        });

        a.setColumn(2, a.getColumn(0));

        assertEquals(b, a);
    }

    public void testColumnAccess_2x4() {

        Matrix a = factory().createMatrix(new double[][] {
                { 1.0, 0.0, 0.0, 4.0 },
                { 0.0, 5.0, 0.0, 9.0 }
        });

        Matrix b = factory().createMatrix(new double[][] { 
                { 1.0, 0.0, 0.0, 1.0 },
                { 0.0, 5.0, 0.0, 0.0 }, 
        });

        a.setColumn(3, a.getColumn(0));

        assertEquals(b, a);
    }

    public void testColumnAccess_5x3() {

        Matrix a = factory().createMatrix(new double[][] {
                { 1.0, 0.0, 0.0 },
                { 0.0, 5.0, 6.0 },
                { 3.0, 0.0, 4.0 },
                { 0.0, 0.0, 0.0 },
                { 2.0, 7.0, 0.0 }
        });

        Matrix b = factory().createMatrix(new double[][] { 
                { 1.0, 1.0, 0.0 },
                { 0.0, 0.0, 6.0 },
                { 3.0, 3.0, 4.0 },
                { 0.0, 0.0, 0.0 },
                { 2.0, 2.0, 0.0 }
        });

        a.setColumn(1, a.getColumn(0));

        assertEquals(b, a);
    }

    public void testTriangle() {

        Matrix a = factory().createMatrix(new double[][] { 
                { 1.0, 0.0, 0.0 },
                { 4.0, 3.0, 6.0 }, 
                { 0.0, 0.0, 9.0 } 
        });

        Matrix b = factory().createMatrix(new double[][] {
                { 1.0, 0.0, 3.0 },
                { 0.0, 5.0, 0.0 }, 
                { 0.0, 0.0, 9.0 }
        });

        Matrix c = a.triangularize();
        Matrix d = b.triangularize();

        Matrix e = factory().createMatrix(new double[][] {
                { 0.0, 0.0, 0.0 },
                { 0.0, 3.0, 6.0 },
                { 0.0, 0.0, 9.0 }
        });

        assertEquals(e, c);
        assertEquals(b, d);
    }

    public void testCopy() {

        double array[][] = new double[][] { 
                { 1.0, 2.0, 3.0 },
                { 4.0, 5.0, 6.0 }, 
                { 7.0, 8.0, 9.0 } 
        };

        Matrix a = factory().createMatrix(array);

        assertEquals(a, a.copy());
    }

    public void testBlank() {

        double arrayA[][] = new double[][] { 
                { 0.0, 0.0, 3.0 },
                { 0.0, 0.0, 6.0 }, 
                { 0.0, 0.0, 9.0 } 
        };

        double arrayB[][] = new double[][] { 
                { 0.0, 0.0, 0.0 },
                { 0.0, 0.0, 0.0 }, 
                { 0.0, 0.0, 0.0 }
        };

        Matrix a = factory().createMatrix(arrayA);
        Matrix b = factory().createMatrix(arrayB);

        assertEquals(b, a.blank());
    }

    public void testSerialization() throws IOException, ClassNotFoundException {

        double array[][] = new double[][] {
                { 0.0, 0.0, 3.0 },
                { 0.0, 0.0, 6.0 },
                { 0.0, 0.0, 9.0 }
        };

        Matrix a = factory().createMatrix(array);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = new ObjectOutputStream(bos);
        out.writeObject(a);
        out.close();

        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        ObjectInput in = new ObjectInputStream(bis);
        Matrix b = (Matrix) in.readObject();
        in.close();

        assertEquals(a, b);
    }
}
