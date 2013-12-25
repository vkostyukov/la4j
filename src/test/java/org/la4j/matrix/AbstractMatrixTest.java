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
 *                 Jakob Moellers
 *                 Maxim Samoylov
 *                 Anveshi Charuvaka
 *                 Todd Brunhoff
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
import java.util.ArrayList;

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

    public void testSlice_3x4_to_1x4() {

        Matrix a = factory().createMatrix(new double[][] { 
                { 1.0, 0.0, 3.0, 0.0 },
                { 0.0, 5.0, 0.0, 7.0 }, 
                { 4.0, 0.0, 9.0, 0.0 }
        });

        Matrix b = factory().createMatrix(new double[][] { 
                { 4.0, 0.0, 9.0, 0.0 }
        });

        Matrix c = a.slice(2, 0, 3, 4);
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

        Matrix a = factory().createMatrix(new double[][] { 
                { 0.0, 14.2, 0.0, 4.0 },
                { 0.0, 5.0, 10.0, 0.0 }, 
                { 0.0, 3.0, 0.0, 2.3 },
                { 11.0, 7.0, 0.0, 1.0 }
        });

        Matrix b = factory().createMatrix(new double[][] { 
                { 0.0, 0.0, 0.0, 11.0 },
                { 14.2, 5.0, 3.0, 7.0 }, 
                { 0.0, 10.0, 0.0, 0.0 },
                { 4.0, 0.0, 2.3, 1.0 }
        });

        Matrix c = a.transpose();
        assertEquals(b, c);

        Matrix d = c.transpose();
        assertEquals(a, d);
    }

    public void testTranspose_5x3() {

        Matrix a = factory().createMatrix(new double[][] { 
                { 0.0, 14.2, 0.0 },
                { 0.0, 5.0, 10.0 }, 
                { 0.0, 3.0, 0.0 },
                { 11.0, 7.0, 0.0 },
                { 12.0, 7.5, 0.0 }
        });

        Matrix b = factory().createMatrix(new double[][] { 
                { 0.0, 0.0, 0.0, 11.0, 12.0 },
                { 14.2, 5.0, 3.0, 7.0, 7.5 }, 
                { 0.0, 10.0, 0.0, 0.0, 0 }
        });

        Matrix c = a.transpose();
        assertEquals(b, c);

        Matrix d = c.transpose();
        assertEquals(a, d);
    }

    public void testTranspose_6x5() {

        Matrix a = factory().createMatrix(new double[][] {
                { 8.93, 3.96, 7.37, 3.43, 7.05 },
                { 5.88, 8.26, 5.79, 9.08, 7.75 },
                { 6.57, 2.51, 8.8, 1.16, 8.11 },
                { 9.3, 9.61, 0.87, 2.3, 2.93 },
                { 3.65, 4.63, 7.83, 3.66, 9.04 },
                { 0.08, 6.12, 6.15, 4.93, 6.72 }
        });

        Matrix b = factory().createMatrix(new double[][] {
                { 8.93, 5.88, 6.57, 9.3, 3.65, 0.08 },
                { 3.96, 8.26, 2.51, 9.61, 4.63, 6.12 },
                { 7.37, 5.79, 8.8, 0.87, 7.83, 6.15 },
                { 3.43, 9.08, 1.16, 2.3, 3.66, 4.93 },
                { 7.05, 7.75, 8.11, 2.93, 9.04, 6.72 }
        });

        Matrix c = a.transpose();
        assertEquals(b, c);

        Matrix d = c.transpose();
        assertEquals(a, d);
    }

    public void testAdd_3x3() {

        Matrix a = factory().createMatrix(new double[][] { 
                { 1.0, 0.0, 0.0 },
                { 0.0, 5.0, 0.0 }, 
                { 0.0, 0.0, 9.0 } 
        });

        Matrix b = factory().createMatrix(new double[][] { 
                { 11.0, 10.0, 10.0 },
                { 10.0, 15.0, 10.0 }, 
                { 10.0, 10.0, 19.0 } 
        });

        Matrix c = factory().createMatrix(new double[][] { 
                { 2.0, 0.0, 0.0 },
                { 0.0, 10.0, 0.0 }, 
                { 0.0, 0.0, 18.0 } 
        });

        assertEquals(b, a.add(10.0));
        assertEquals(c, a.add(a));
    }

    public void testAdd_4x2() {

        Matrix a = factory().createMatrix(new double[][] { 
                { 1.0, 0.0 },
                { 0.0, 5.0 }, 
                { 7.0, 0.0 }, 
                { 0.0, 9.0 }
        });

        Matrix b = factory().createMatrix(new double[][] { 
                { 11.0, 10.0 },
                { 10.0, 15.0 }, 
                { 17.0, 10.0 },
                { 10.0, 19.0 }
        });

        Matrix c = factory().createMatrix(new double[][] { 
                { 2.0, 0.0 },
                { 0.0, 10.0 }, 
                { 14.0, 0.0 }, 
                { 0.0, 18.0 }
        });

        assertEquals(b, a.add(10.0));
        assertEquals(c, a.add(a));
    }

    public void testSubtract_3x3() {

        Matrix a = factory().createMatrix(new double[][] { 
                { 1.0, 0.0, 0.0 },
                { 0.0, 5.0, 0.0 }, 
                { 0.0, 0.0, 9.0 } 
        });

        Matrix b = factory().createMatrix(new double[][] { 
                { -9.0, -10.0, -10.0 },
                { -10.0, -5.0, -10.0 }, 
                { -10.0, -10.0, -1.0 } 
        });

        Matrix c = factory().createMatrix(new double[][] { 
                { 0.0, 0.0, 0.0 },
                { 0.0, 0.0, 0.0 }, 
                { 0.0, 0.0, 0.0 } 
        });

        assertEquals(b, a.subtract(10.0));
        assertEquals(c, a.subtract(a));
    }

    public void testSubtract_2x4() {

        Matrix a = factory().createMatrix(new double[][] { 
                { 1.0, 0.0, 7.0, 0.0 },
                { 0.0, 5.0, 0.0, 9.0 } 
        });

        Matrix b = factory().createMatrix(new double[][] { 
                { -9.0, -10.0, -3.0, -10.0 },
                { -10.0, -5.0, -10.0, -1.0 } 
        });

        Matrix c = factory().createMatrix(new double[][] { 
                { 0.0, 0.0, 0.0, 0.0 },
                { 0.0, 0.0, 0.0, 0.0 } 
        });

        assertEquals(b, a.subtract(10.0));
        assertEquals(c, a.subtract(a));
    }

    public void testMultiply_2x3() {

        Matrix a = factory().createMatrix(new double[][] {
                { 1.0, 0.0, 3.0 },
                { 0.0, 5.0, 0.0 }
        });

        Matrix b = factory().createMatrix(new double[][] {
                { 2.0, 0.0, 6.0 },
                { 0.0, 10.0, 0.0 }
        });

        assertEquals(b, a.multiply(2.0));
    }

    public void testMultiply_2x3_3() {

        Matrix a = factory().createMatrix(new double[][] {
                { 1.0, 0.0, 3.0 },
                { 0.0, 5.0, 0.0 }
        });

        Vector b = factory().createVector(new double[]
                { 10.0, 0.0, 30.0 }
        );

        Vector c = factory().createVector(new double[]
                { 100.0, 0.0 }
        );

        assertEquals(c, a.multiply(b));
    }

    public void testMultiply_5x2_2() {

        Matrix a = factory().createMatrix(new double[][] {
                { 1.0, 0.0 },
                { 0.0, 5.0 },
                { 7.0, 0.0 },
                { 3.0, 0.0 },
                { 0.0, 1.0 }
        });

        Vector b = factory().createVector(new double[]
                { 0.0, 10.0 }
        );

        Vector c = factory().createVector(new double[]
                { 0.0, 50.0, 0.0, 0.0, 10.0 }
        );

        assertEquals(c, a.multiply(b));
    }

    public void testMultiply_1x1_1x1() {

        Matrix a = factory().createMatrix(new double[][] {
                { 3.37 }
        });

        Matrix b = factory().createMatrix(new double[][] {
                { 8.48 }
        });

        Matrix c = factory().createMatrix(new double[][] {
                { 28.5776 }
        });

        assertEquals(c, a.multiply(b));
    }

    public void testMultiply_2x2_2x2() {

        Matrix a = factory().createMatrix(new double[][] {
                { 2.46, 1.68 },
                { 7.57, 2.47 }
        });


        Matrix b = factory().createMatrix(new double[][] {
                { 3.85, 8.28 },
                { 8.02, 8.39 }
        });

        Matrix c = factory().createMatrix(new double[][] {
                { 22.9446, 34.464 },
                { 48.9539, 83.4029 }
        }); 

        assertEquals(c, a.multiply(b));
    }

    public void testMultiply_4x4_4x4() {

        Matrix a = factory().createMatrix(new double[][] {
                { 8.0, 3.0, 1.0, 9.0 },
                { 4.0, 9.0, 6.0, 6.0 },
                { 9.0, 1.0, 1.0, 4.0 },
                { 5.0, 7.0, 3.0, 0.0 }
        });

        Matrix b = factory().createMatrix(new double[][] {
                { 4.0, 9.0, 0.0, 3.0 },
                { 6.0, 7.0, 7.0, 6.0 },
                { 9.0, 4.0, 3.0, 3.0 },
                { 4.0, 4.0, 1.0, 6.0 }
        });

        Matrix c = factory().createMatrix(new double[][] {
                { 95.0, 133.0, 33.0, 99.0 },
                { 148.0, 147.0, 87.0, 120.0 },
                { 67.0, 108.0, 14.0, 60.0 },
                { 89.0, 106.0, 58.0, 66.0 }
        });

        assertEquals(c, a.multiply(b));
    }

    public void testMultiply_4x1_1x4() {

        Matrix a = factory().createMatrix(new double[][] {
                { 6.31 },
                { 6.06 },
                { 4.94 },
                { 9.62 }
        });

        Matrix b = factory().createMatrix(new double[][] {
                { 5.19, 6.06, 6.12, 5.92 }
        });

        Matrix c = factory().createMatrix(new double[][] {
                { 32.7489, 38.2386, 38.6172, 37.3552 },
                { 31.4514, 36.7236, 37.0872, 35.8752 },
                { 25.6386, 29.9364, 30.2328, 29.2448 },
                { 49.9278, 58.2972, 58.8744, 56.9504 }
        });

        assertEquals(c, a.multiply(b));
    }

    public void testMultiply_1x10_10x1() {

        Matrix a = factory().createMatrix(new double[][] {
                { 0.28, 1.61, 5.11, 1.71, 2.21, 5.97, 2.61, 2.58, 0.07, 3.78 }
        });

        Matrix b = factory().createMatrix(new double[][] {
                { 9.81 },
                { 0.14 },
                { 8.91 },
                { 8.54 },
                { 0.97 },
                { 2.55 },
                { 1.11 },
                { 2.52 },
                { 7.71 },
                { 1.69 }
        }); 

        Matrix c = factory().createMatrix(new double[][] {
                { 96.7995 }
        });

        assertEquals(c, a.multiply(b));
    }

    public void testMultiply_3x2_2x3() {

        Matrix a = factory().createMatrix(new double[][] { 
                { 1.0, 9.0 }, 
                { 9.0, 1.0 },
                { 8.0, 9.0 } 
        });

        Matrix b = factory().createMatrix(new double[][] { 
                { 0.0, 3.0, 0.0 },
                { 2.0, 0.0, 4.0 } 
        });

        Matrix c = factory().createMatrix(new double[][] { 
                { 18.0, 3.0, 36.0 },
                { 2.0, 27.0, 4.0 }, 
                { 18.0, 24.0, 36.0 } 
        });

        assertEquals(c, a.multiply(b));
    }

    public void testMultiply_4x9_9x4() {

        Matrix a = factory().createMatrix(new double[][] {
                { 5.98, 3.76, 9.01, 9.68, 2.12, 6.34, 0.64, 6.22, 1.16 },
                { 8.4, 9.65, 7.06, 2.56, 7.66, 4.69, 3.29, 8.6, 8.55 },
                { 4.99, 7.06, 6.07, 7.53, 0.08, 1.08, 9.69, 8.51, 6.61 },
                { 4.72, 7.06, 4.0, 0.75, 2.45, 4.4, 8.33, 5.81, 0.57 }
        });

        Matrix b = factory().createMatrix(new double[][] {
                { 9.28, 7.63, 4.1, 4.71 },
                { 4.68, 2.82, 9.18, 5.39 },
                { 4.54, 6.86, 1.29, 5.4 },
                { 8.72, 2.06, 4.28, 7.37 },
                { 2.43, 3.7, 7.52, 5.87 },
                { 8.21, 9.36, 4.85, 0.3 },
                { 9.87, 8.19, 5.03, 6.14 },
                { 9.47, 4.28, 3.86, 3.12 },
                { 5.29, 4.41, 5.23, 4.85 }
        });

        Matrix c = factory().createMatrix(new double[][] {
                { 326.9658, 242.1452, 192.0747, 211.7362 },
                { 393.7521, 318.7092, 317.9021, 283.44 },
                { 392.8255, 270.4737, 247.3277, 268.7303 },
                { 283.873, 230.76, 199.6044, 175.1515 }
        });

        assertEquals(c, a.multiply(b));
    }

    public void testDivide_3x3() {

        Matrix a = factory().createMatrix(new double[][] {
                { 1.0, 0.0, 0.0 },
                { 0.0, 5.0, 0.0 },
                { 0.0, 0.0, 9.0 }
        });

        Matrix b = factory().createMatrix(new double[][] {
                { 0.1, 0.0, 0.0 },
                { 0.0, 0.5, 0.0 },
                { 0.0, 0.0, 0.9 }
        });

        assertEquals(b, a.divide(10.0));
    }

    public void testKronecker_3x3_2x2() {
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

        assertEquals(c, a.kroneckerProduct(b));
    }

    public void testTrace_3x3() {

        Matrix a = factory().createMatrix(new double[][] {
                { 1.0, 0.0, 0.0 },
                { 0.0, 5.0, 0.0 },
                { 0.0, 0.0, 9.0 }
        });

        assertTrue(Math.abs(a.trace() - 15.0) < Matrices.EPS);
    }

    public void testDiagonalProduct_3x3() {

        Matrix a = factory().createMatrix(new double[][] {
                { 1.0, 0.0, 0.0 },
                { 0.0, 5.0, 0.0 },
                { 0.0, 0.0, 9.0 }
        });

        assertTrue(Math.abs(a.diagonalProduct() - 45.0) < Matrices.EPS);
    }

    public void testProduct_3x3() {

        Matrix a = factory().createMatrix(new double[][] {
                {1.0, 1.0, 1.0},
                {1.0, 5.0, 1.0},
                {1.0, 1.0, 9.0}
        });

        assertTrue(Math.abs(a.product() - 45.0) < Matrices.EPS);
    }

    public void testSum_3x3() {

        Matrix a = factory().createMatrix(new double[][] {
                { 1.0, 0.0, 0.0 },
                { 0.0, 5.0, 0.0 },
                { 0.0, 0.0, 9.0 } 
        });

        assertTrue(Math.abs(a.sum() - 15.0) < Matrices.EPS);
    }

    public void testHadamardProduct_3x3() {

        Matrix a = factory().createMatrix(new double[][] {
                { 1.0, 2.0, 3.0 },
                { 4.0, 5.0, 6.0 },
                { 7.0, 8.0, 9.0 } 
        });

        Matrix b = factory().createMatrix(new double[][] {
                { 9.0, 8.0, 7.0 },
                { 6.0, 5.0, 4.0 },
                { 3.0, 2.0, 1.0 } 
        });

        Matrix c = factory().createMatrix(new double[][] {
                {  9.0, 16.0, 21.0 },
                { 24.0, 25.0, 24.0 },
                { 21.0, 16.0,  
                    9.0 } 
        });

        assertEquals(c, a.hadamardProduct(b));
    }
    
    public void testHadamardProduct_5x2() {
        Matrix a = factory().createMatrix(new double[][] {
                {1.0, 2.0},
                {2.0, 3.0},
                {3.0, 4.0},
                {4.0, 5.0},
                {5.0, 6.0}
        });
        
        Matrix b = factory().createMatrix(new double[][] {
                {6.0, 5.0},
                {5.0, 4.0},
                {4.0, 3.0},
                {3.0, 2.0},
                {2.0, 1.0}
        });
        
        Matrix c = factory().createMatrix(new double[][] {
                {6.0, 10.0},
                {10.0, 12.0},
                {12.0, 12.0},
                {12.0, 10.0},
                {10.0, 6.0}
        });
        
        assertEquals(c, a.hadamardProduct(b));
    }
    
    public void testHadamardProduct_3x4() {
        Matrix a = factory().createMatrix(new double[][] {
                {1.0, 2.0, 3.0, 4.0},
                {2.0, 3.0, 4.0, 5.0},
                {3.0, 4.0, 5.0, 6.0},
        });
        
        Matrix b = factory().createMatrix(new double[][] {
                {6.0, 5.0, 4.0, 3.0},
                {5.0, 4.0, 3.0, 2.0},
                {4.0, 3.0, 2.0, 1.0},
        });
        
        Matrix c = factory().createMatrix(new double[][] {
                {6.0, 10.0, 12.0, 12.0},
                {10.0, 12.0, 12.0, 10.0},
                {12.0, 12.0, 10.0, 6.0},
        });
        
        assertEquals(c, a.hadamardProduct(b));
    }
    
    public void testHadamardProduct_1x3() {
        Matrix a = factory().createMatrix(new double[][] {
                {1.0, 2.0, 3.0, 4.0}
        });
        
        Matrix b = factory().createMatrix(new double[][] {
                {6.0, 5.0, 4.0, 3.0}
        });
        
        Matrix c = factory().createMatrix(new double[][] {
                {6.0, 10.0, 12.0, 12.0}
        });
        
        assertEquals(c, a.hadamardProduct(b));
    }

    public void testDeterminant_3x3() {

        Matrix a = factory().createMatrix(new double[][] {
                { 1.0, 0.0, 0.0 },
                { 0.0, 5.0, 0.0 },
                { 0.0, 0.0, 9.0 } 
        });

        assertEquals(45.0, a.determinant(), 1e-5);
    }

    public void testDeterminant_4x4() {

        Matrix a = factory().createMatrix(new double[][] { 
                { 3.0, 3.0, 4.0, 43.0 }, 
                { 5.0, 5.0, 6.0, 6.0 }, 
                { 70.0, 7.0, 8.0, 8.0 }, 
                { 9.0, 9.0, 10.0, 11.0 }
        });

        assertEquals(-9954.0, a.determinant(), 1e-5);
    }

    public void testDeterminant_4x4_2() {

        Matrix a = factory().createMatrix(new double[][] {
                { 1.0, 2.0, 3.0, 4.0 },
                { 1.0, 1.0, 1.0, 1.0 },
                { 2.0, 3.0, 4.0, 5.0 },
                { 2.0, 2.0, 2.0, 2.0 }
        });

        assertEquals(0.0, a.determinant(), 1e-5);
    }

    public void testDeterminant_7x7() {

        Matrix a = factory().createMatrix(new double[][] {
                { 1.0,  6.0,  -8.0,  5.0,  -3.0,  41.0, -2.0 },
                {-8.0, -5.0,   7.0, 23.0,  -7.0,  12.0, -2.0 },
                { 8.0, 77.0, -65.0, 13.0,  -8.0,  55.0, -47.0 },
                {26.0, 27.0, -81.0, -1.0,  10.0, -48.0, -3.0 },
                { 0.0, 34.0, -79.0,  4.0,  -1.0,  28.0,  6.0 },
                {-5.0,  8.0, -20.0, 36.0, -12.0,  -7.0, -10.0 },
                {-6.0, 13.0,   9.0, -4.0,  95.0,   2.0,  46.0 }
        });

        assertEquals(-9134649369.0, a.determinant(), 1e-5);
    }

    public void testDeterminant_6x6() {

        Matrix a = factory().createMatrix(new double[][] {
                { 5.0, 89.0, 6.0, 23.0, 6.0, 4.0, },
                { 4.0, 0.0, 27.0, 90.0, 42.0, 12.0 },
                { 6.0, 0.0, 24.0, 9.0, 41.0, 15.0 },
                { 31.0, 0.0, 3.0, 22.0, 2.0, 1.0 },
                { 2.0, 0.0, 37.0, 4.0, 0.0, 21.0 },
                { 23.0, 8.0, 7.0, 0.0, 12.0, 10.0 }
        });

        assertEquals(-1695428964.0, a.determinant(), 1e-5);
    }

    public void testDeterminant_5x5() {

        Matrix a = factory().createMatrix(new double[][] {
                { -10.0, 89.0, -6.0, 23.0, 6.0, 4.0 },
                { 4.0, 5.0, 27.0, 90.0, 0.0, 12.0 },
                { 6.0, 0.0, 0.0, 9.0, 1.0, 15.0 },
                { 31.0, 11.0, 0.0, 12.0, 2.0, 1.0 },
                { 2.0, 0.0, 5.0, 4.0, -1.0, -21.0 },
                { 3.0, -18.0, 7.0, 0.0, 12.0, 10.0 }
        });

        assertEquals(3180462.0, a.determinant(), 1e-5);
    }

    public void testRank_3x3() {

        Matrix a = factory().createMatrix(new double[][] {
                { 1.0, 0.0, 0.0 },
                { 0.0, 5.0, 0.0 },
                { 0.0, 0.0, 9.0 } 
        });

        assertEquals(3, a.rank());
    }

    public void testRank_4x4() {

        Matrix a = factory().createMatrix(new double[][] {
                { 1.0, 0.0, 0.0, 3.5 },
                { 0.0, 0.0, 0.0, 2.0 },
                { 0.0, 0.0, 9.0, 0.0 },
                { 1.0, 0.0, 0.0, 0.0 }
        });

        assertEquals(3, a.rank());
    }

    public void testRank_4x4_2() {

        Matrix a = factory().createMatrix(new double[][] {
                { 1.0, 2.0, 3.0, 4.0 },
                { 1.0, 1.0, 1.0, 1.0 },
                { 2.0, 3.0, 4.0, 5.0 },
                { 2.0, 2.0, 2.0, 2.0 }
        });

        assertEquals(2, a.rank());
    }

    public void testRank_2x4() {

        Matrix a = factory().createMatrix(new double[][] {
                { 1.0, 0.0, 0.0, 3.5 },
                { 0.0, 1.3, 0.0, 2.0 }
        });

        assertEquals(2, a.rank());
    }

    public void testRank_5x3() {

        Matrix a = factory().createMatrix(new double[][] {
                { 1.0, 0.0, 0.0 },
                { 0.0, 0.0, 0.0 },
                { 1.0, 0.0, 9.0 },
                { 1.0, 0.0, 0.0 },
                { 1.0, 0.0, 0.0 }
        });

        assertEquals(2, a.rank());
    }

    public void testRank_10x10() {

        Matrix a = factory().createMatrix(new double[][] {
                { 15, 6, 84, 67, 4, 11, 81, 47, 57, 41 },
                { 21, 94, 86, 55, 16, 31, 60, 62, 33, 61 },
                { 3, 32, 57, 56, 55, 66, 70, 87, 6, 37 },
                { 71, 48, 8, 24, 43, 61, 56, 24, 37, 63 },
                { 79, 45, 36, 20, 13, 96, 31, 77, 67, 54 },
                { 20, 63, 41, 79, 46, 100, 90, 23, 42, 94 },
                { 15, 10, 36, 18, 25, 81, 76, 29, 23, 53 },
                { 43, 79, 60, 94, 26, 24, 50, 38, 53, 12 },
                { 100, 59, 26, 99, 72, 17, 29, 3, 76, 14 },
                { 18, 38, 141, 123, 59, 77, 151, 134, 63, 78 }
            });

        assertEquals(9, a.rank());
    }

    public void testRank_3x6() {

        Matrix a = factory().createMatrix(new double[][] {
                { 5.0, 7.0, 10.0, 3.0, 5.0, 8.0 },
                { 5.0, 2.0, 3.0, 10.0, 11.0, 9.0 },
                { 4.0, 3.0, 9.0, 12.0, 8.0, 9.0 }
        });

        assertEquals(3, a.rank());
    }

    public void testRank_7x11() {

        Matrix a = factory().createMatrix(new double[][] {
                { 0.0, 13.0, 25.0, 43.0, 81.0, 0.0, 39.0, 60.0, 70.0, 21.0, 44.0, 0.0 },
                { 44.0, 0.0, 13.0, 67.0, 35.0, 0.0, 84.0, 35.0, 23.0, 88.0, 11.0, 0.0 },
                { 5.0, 34.0, 0.0, 143.0, 35.0, 0.0, 65.0, 99.0, 22.0, 13.0, 26.0, 0.0 },
                { 89.0, 23.0, 13.0, 0.0, 78.0, 0.0, 13.0, 24.0, 98.0, 65.0, 0.0, 0.0 },
                { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 },
                { 56.0,  4.0, 24.0, 56.0, 78.0, 0.0, 13.0, 0.0, 24.0, 57.0, 8.0, 1.0 },
                { 0.0, 0.0, 46.0, 666.0, 34.0, 13.0, 67.0, 9.0, 12.0, 45.0, 38.0, 0.0 }
        });

        assertEquals(6, a.rank());
    }

    public void testRank_1x4() {

        Matrix a = factory().createMatrix(new double[][] {
                { 0.0, 1.0, 0.0, 0.0 }
        });

        assertEquals(1, a.rank());
    }

    public void testRank_3x3_empty() {

        Matrix a = factory().createMatrix(3, 3);

        assertEquals(0, a.rank());
    }

    public void testRank_0x0() {

        Matrix a = factory().createMatrix();

        assertEquals(0, a.rank());
    }

    public void testRank_7x3() {
        Matrix a = factory().createMatrix(new double[][] {
            {1, 2, 0},
            {0, 0, 0},
            {0, 0, 0},
            {0, 0, 0},
            {0, 0, 1},
            {0, 0, -1},
            {1, 2, 1}
        });

        assertEquals(2, a.rank());
    }

    public void testRank_3x1() {
        Matrix a = factory().createMatrix(new double[][] {
            {1},
            {2},
            {0}
        });

        assertEquals(1, a.rank());
    }

    public void testRowAccess_2x1() {
        Matrix a = factory().createMatrix(new double[][] {
                { 99.0 },
                { 88.0 }
        });

        Matrix b = factory().createMatrix(new double[][] {
                { 99.0 },
                { 99.0 }
        });

        a.setRow(1, a.getRow(0));

        assertEquals(b, a);
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

    public void testRowAccess_6x4() {

        Matrix a = factory().createMatrix(new double[][] {
                { 0.0, 18.0, 15.0, 0.0 },
                { 1.0, 0.0, -55.0, 9.0 },
                { 0.0, 0.0, 71.0, 19.0 },
                { -1.0, -8.0, 54.0, 0.0 },
                { 25.0, 18.0, 0.0, 0.0 },
                { 78.0, 28.0, 0.0, -8.0 }
        });

        Matrix b = factory().createMatrix(new double[][] {
                { 0.0, 18.0, 15.0, 0.0 },
                { 1.0, 0.0, -55.0, 9.0 },
                { 0.0, 0.0, 71.0, 19.0 },
                { -1.0, -8.0, 54.0, 0.0 },
                { 25.0, 18.0, 0.0, 0.0 },
                { 25.0, 18.0, 0.0, 0.0 }
        });

        a.setRow(5, a.getRow(4));

        assertEquals(b, a);
    }

    /**
     * Returns true if both matrices contain the same elements and have equal
     * dimensions.
     * 
     * @param matrix1
     *            Matrix 1
     * @param matrix2
     *            Matrix 2
     * @return True if both matrices contain the same elements and have equal
     *         dimensions.
     */
    public boolean testWhetherMatricesContainSameElements(Matrix matrix1, 
            Matrix matrix2) {

        // Test for equal columns and rows
        if (matrix1.rows() != matrix2.rows()) {
            return false;
        }
        if (matrix1.columns() != matrix2.columns()) {
            return false;
        }

        // Test for same elements
        double[] a = new double[matrix1.columns() * matrix1.rows()];
        int k = 0;
        for (int i = 0; i < matrix1.rows(); i++) {
            for (int j = 0; j < matrix1.columns(); j++) {
                a[k] = matrix1.get(i, j);
                k++;
            }
        }
        ArrayList<Double> b = new ArrayList<Double>();
        for (int i = 0; i < matrix2.rows(); i++) {
            for (int j = 0; j < matrix2.columns(); j++) {
                b.add(matrix2.get(i, j));
            }
        }
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < b.size(); j++) {
                if (a[i] == b.get(j)) {
                    b.remove(j); // If match found, remove it from ArrayList to
                                 // decrease complexity
                    break;
                }
            }
        }

        return b.size() == 0;
    }

    public void testTestWhetherMatricesContainSameElements() {
        Matrix m1 = factory().createMatrix(new double[][] { 
                { 1.0, 1.0, 3.0 }, 
                { 4.0, 5.0, 6.0 },
                { 7.0, 8.0, 9.0 } 
        });

        Matrix m2 = factory().createMatrix(new double[][] { 
                { 1.0, 1.0, 4.0 }, 
                { 5.0, 6.0, 9.0 },
                { 7.0, 3.0, 8.0 } 
        });

        assertTrue(testWhetherMatricesContainSameElements(m1, m2));

        Matrix m3 = factory().createMatrix(new double[][] { 
                { 1.0, 1.0, 3.0 }, 
                { 4.0, 52.0, 6.0 },
                { 7.0, 8.0, 9.0 } 
        });

        assertFalse(testWhetherMatricesContainSameElements(m1, m3));
    }
    
    public void testShuffle_3x2() {
        Matrix m1 = factory().createMatrix(new double[][] { 
                { 1.0, 2.0 }, 
                { 4.0, 5.0 }, 
                { 7.0, 8.0 } 
        });

        Matrix m2 = m1.shuffle();

        assertTrue(testWhetherMatricesContainSameElements(m1, m2));
    }
    
    public void testShuffle_5x3() {

        Matrix m1 = factory().createMatrix(new double[][] { 
                { 1.0, 2.0, 3.0 }, 
                { 4.0, 5.0, 6.0 },
                { 7.0, 8.0, 9.0 }, 
                { 10.0, 11.0, 12.0 },
                { 13.0, 14.0, 15.0 }
        });

        Matrix m2 = m1.shuffle();

        assertTrue(testWhetherMatricesContainSameElements(m1, m2));
    }

    public void testRotate_3x1() {

        Matrix m1 = factory().createMatrix(new double[][] { 
                { 1.0 }, 
                { 3.0 }, 
                { 5.0 } 
        });

        Matrix m3 = factory().createMatrix(new double[][] { 
                { 5.0, 3.0, 1.0 } 
        });

        Matrix m2 = m1.rotate();

        assertTrue(m2.equals(m3));
    }

    public void testRotate_2x2() {

        Matrix m1 = factory().createMatrix(new double[][] { 
                { 1.0, 2.0 }, 
                { 3.0, 4.0 } 
        });

        Matrix m3 = factory().createMatrix(new double[][] { 
                { 3.0, 1.0 }, 
                { 4.0, 2.0 }
        });

        Matrix m2 = m1.rotate();

        assertTrue(m2.equals(m3));
    }

    public void testRotate_2x4() {

        Matrix m1 = factory().createMatrix(new double[][] { 
                { 1.0, 2.0, 3.0, 4.0 },
                { 5.0, 6.0, 7.0, 8.0 } 
        });

        Matrix m3 = factory().createMatrix(new double[][] { 
                { 5.0, 1.0 }, 
                { 6.0, 2.0 }, 
                { 7.0, 3.0 },
                { 8.0, 4.0 } 
        });

        Matrix m2 = m1.rotate();

        assertTrue(m2.equals(m3));
    }

    public void testRotate_5x3() {

        Matrix m1 = factory().createMatrix(new double[][] { 
                { 1.0, 2.0, 3.0 }, 
                { 4.0, 5.0, 6.0 },
                { 7.0, 8.0, 9.0 }, 
                { 10.0, 11.0, 12.0 },
                { 13.0, 14.0, 15.0 } 
        });

        Matrix m3 = factory().createMatrix(new double[][] { 
                { 13.0, 10.0, 7.0, 4.0, 1.0 },
                { 14.0, 11.0, 8.0, 5.0, 2.0 },
                { 15.0, 12.0, 9.0, 6.0, 3.0 } 
        });

        Matrix m2 = m1.rotate();

        assertTrue(m2.equals(m3));
    }

    public void testColumnAccess_2x1() {

        Matrix a = factory().createMatrix(new double[][] {
                { 11.0, 22.0 },
        });

        Matrix b = factory().createMatrix(new double[][] { 
                { 22.0, 22.0 },
        });

        a.setColumn(0, a.getColumn(1));

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

    public void testColumnAccess_6x4() {

        Matrix a = factory().createMatrix(new double[][] {
                { 0.0, 18.0, 15.0, 0.0 },
                { 1.0, 0.0, -55.0, 9.0 },
                { 0.0, 0.0, 71.0, 19.0 },
                { -1.0, -8.0, 54.0, 0.0 },
                { 25.0, 18.0, 0.0, 0.0 },
                { 78.0, 28.0, 0.0, -8.0 }
        });

        Matrix b = factory().createMatrix(new double[][] {
                { 0.0, 18.0, 15.0, 0.0 },
                { 1.0, 0.0, -55.0, 1.0 },
                { 0.0, 0.0, 71.0, 0.0 },
                { -1.0, -8.0, 54.0, -1.0 },
                { 25.0, 18.0, 0.0, 25.0 },
                { 78.0, 28.0, 0.0, 78.0 }
        });

        a.setColumn(3, a.getColumn(0));

        assertEquals(b, a);
    }

    public void testTriangularize_3x3() {

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

    public void testCopy_3x3() {

        Matrix a = factory().createMatrix(new double[][] { 
                { 1.0, 2.0, 3.0 },
                { 4.0, 5.0, 6.0 }, 
                { 7.0, 8.0, 9.0 } 
        });

        assertEquals(a, a.copy());
    }

    public void testBlank_3x3() {

        Matrix a = factory().createMatrix(new double[][] { 
                { 0.0, 0.0, 3.0 },
                { 0.0, 0.0, 6.0 }, 
                { 0.0, 0.0, 9.0 } 
        });

        Matrix b = factory().createMatrix(new double[][] { 
                { 0.0, 0.0, 0.0 },
                { 0.0, 0.0, 0.0 }, 
                { 0.0, 0.0, 0.0 }
        });

        assertEquals(b, a.blank());
    }

    public void testSerialization() throws IOException, ClassNotFoundException {

        Matrix a = factory().createMatrix(new double[][] {
                { 0.0, 0.0, 3.0 },
                { 0.0, 0.0, 6.0 },
                { 0.0, 0.0, 9.0 }
        });

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

    public void testPower_2x2() {
        Matrix a = factory().createMatrix(new double[][] { 
                { 1.0, 2.0 }, 
                { 3.0, 4.0 } 
        });
        
        Matrix b = factory().createMatrix(new double[][] { 
                { 7.0, 10.0 }, 
                { 15.0, 22.0 } 
        });
        
        Matrix c = a.power(2);
        assertEquals(b, c);

        Matrix d = factory().createMatrix(new double[][] { 
                { 37.0, 54.0 }, 
                { 81.0, 118.0 } 
        });
        
        Matrix e = a.power(3);
        assertEquals(d, e);

        Matrix f = factory().createMatrix(new double[][] { 
                { 5743.0, 8370.0 }, 
                { 12555.0, 18298.0 } 
        });
        
        Matrix g = a.power(6);
        assertEquals(f, g);
    }

    public void testPower_3x3(){
        Matrix h = factory().createMatrix(new double[][] { 
                { 1.0, 0.0, 0.0 }, 
                { 4.0, 3.0, 6.0 },
                { 0.0, 0.0, 9.0 } 
        });

        Matrix i = factory().createMatrix(new double[][] { 
                { 1.0, 0.0, 0.0 }, 
                { 160.0, 81.0, 6480.0 },
                { 0.0, 0.0, 6561.0 } 
        });
        
        Matrix j = h.power(4);
        assertEquals(i, j);
        
        Matrix k = h.power(1);
        assertEquals(h, k);
    }

    public void testMax() {
        Matrix a = factory().createMatrix(new double[][]{
            { 0, 0, -1 },
            { 0, -3, 0 },
            { 6, -7, -2}
        });

        assertEquals(6.0, a.max());
    }

    public void testMaxCompressed() {
        Matrix a = factory().createMatrix(new double[][]{
            { 0, 0, -1 },
            { 0, -3, 0 },
            { 0, -7, -2}
        });

        assertEquals(0.0, a.max());
    }

    public void testMinCompressed() {
        Matrix a = factory().createMatrix(new double[][]{
            { 0, 0, 1 },
            { 0, 3, 0 },
            { 0, 7, 2 }
        });

        assertEquals(0.0, a.min());
    }

    public void testMin() {
        Matrix a = factory().createMatrix(new double[][]{
            { 0, 0, -1 },
            { 0, -3, 0 },
            { 0, -7, -2}
        });

        assertEquals(-7.0, a.min());
    }

    public void testMaxInRow() {
        Matrix a = factory().createMatrix(new double[][]{
            { 0, 0, 1, 0},
            { -3, 2, 0, 1},
            {-2, 0, 0, -1}
        });

        assertEquals(0.0, a.maxInRow(2));
    }

    public void testMinInRow() {
        Matrix a = factory().createMatrix(new double[][]{
            { 0, 0, 1, 0},
            { -3, 2, 0, 1},
            {2, 0, 0, 1}
        });

        assertEquals(0.0, a.minInRow(2));
    }

    public void testMaxInColumn() {
        Matrix a = factory().createMatrix(new double[][]{
            { 0, 0, 1, 0},
            { -3, 2, 0, 1},
            {-2, 0, 0, -1}
        });

        assertEquals(0.0, a.maxInColumn(0));
    }

    public void testMinInColumn() {
        Matrix a = factory().createMatrix(new double[][]{
            { 0, 0, 1, 0},
            { -3, 2, 0, 1},
            {-2, 0, 0, -1}
        });

        assertEquals(-1.0, a.minInColumn(3));
    }

    private Matrix matrixA() {
        return factory().createMatrix(new double[][] {
                // 0 1 2 3 4 5
                { 8.93, 5.88, 6.57, 9.3, 3.65, 0.08 },// 0
                { 3.96, 8.26, 2.51, 9.61, 4.63, 6.12 },// 1
                { 7.37, 5.79, 8.8, 0.87, 7.83, 6.15 },// 2
                { 3.43, 9.08, 1.16, 2.3, 3.66, 4.93 },// 3
                { 7.05, 7.75, 8.11, 2.93, 9.04, 6.72 } // 4
                });
    }

    public void xtestSelect1() {
        // Throw exception when row indices are invalid
        Matrix a = matrixA();
        int[] rowInd = new int[] { 3, 4, 10 };
        int[] colInd = new int[] { 0, 1, 2 }; // all columns
        try {
            a.select(rowInd, colInd);
            fail();
        } catch (IndexOutOfBoundsException ex) {
            // Do nothing
        }
    }

    public void xtestSelect2() {
        // Throw exception when column indices are invalid
        Matrix a = matrixA();
        int[] rowInd = new int[] { 0, 1, 2 };
        int[] colInd = new int[] { -1, 1, 2 }; // all columns
        try {
            a.select(rowInd, colInd);
            fail();
        } catch (IndexOutOfBoundsException ex) {
            // Do nothing
        }
    }

    public void testSelect3() {
        // All columns and a subset of rows selected.
        Matrix a = matrixA();
        int[] rowInd = new int[] { 1, 3, 4 };
        int[] colInd = new int[] { 0, 1, 2, 3, 4, 5 }; // all columns
        Matrix b = factory().createMatrix(new double[][] {
                // 0 1 2 3 4 5
                { 3.96, 8.26, 2.51, 9.61, 4.63, 6.12 },// 1
                { 3.43, 9.08, 1.16, 2.3, 3.66, 4.93 },// 3
                { 7.05, 7.75, 8.11, 2.93, 9.04, 6.72 } // 4
                });
        assertEquals(b, a.select(rowInd, colInd));
    }

    public void testSelect4() {
        // All rows and a subset of columns selected.
        Matrix a = matrixA();
        int[] rowInd = new int[] { 0, 1, 2, 3, 4 };
        int[] colInd = new int[] { 0, 2, 4, 5 }; // all columns
        Matrix c = factory().createMatrix(new double[][] {
                // 0 2 4 5
                { 8.93, 6.57, 3.65, 0.08 },// 0
                { 3.96, 2.51, 4.63, 6.12 },// 1
                { 7.37, 8.8, 7.83, 6.15 },// 2
                { 3.43, 1.16, 3.66, 4.93 },// 3
                { 7.05, 8.11, 9.04, 6.72 } // 4
                });
        assertEquals(c, a.select(rowInd, colInd));
    }

    public void testSelect5() {
        // A subset of rows and columns is selected.
        Matrix a = matrixA();
        int[] rowInd = new int[] { 1, 3, 4 };
        int[] colInd = new int[] { 2, 4, 5 };
        Matrix d = factory().createMatrix(new double[][] {
                // 2 4 5
                { 2.51, 4.63, 6.12 },// 1
                { 1.16, 3.66, 4.93 },// 3
                { 8.11, 9.04, 6.72 } // 4
                });
        assertEquals(d, a.select(rowInd, colInd));
    }

    public void testSelect6() {
        // Duplication of rows and columns.
        Matrix a = matrixA();
        int[] rowInd = new int[] { 1, 3, 3, 4 };
        int[] colInd = new int[] { 2, 2, 4, 5, 5 };
        Matrix d = factory().createMatrix(new double[][] {
                // 2 2 4 5 5
                { 2.51, 2.51, 4.63, 6.12, 6.12 },// 1
                { 1.16, 1.16, 3.66, 4.93, 4.93 },// 3
                { 1.16, 1.16, 3.66, 4.93, 4.93 },// 3
                { 8.11, 8.11, 9.04, 6.72, 6.72 } // 4
                });
        assertEquals(d, a.select(rowInd, colInd));
    }
    
    public void testFoldSum() {
        Matrix d = factory().createMatrix(new double[][] {
            { 6.436, 4.808, 3.923, 5.866 },
            { 7.072, 5.899, 4.771, 3.537 },
            { 7.282, 0.636, 0.010, 3.673 },
            { 5.833, 0.201, 6.941, 5.914 },
            { 5.757, 6.807, 7.830, 2.601 },
            { 0.434, 1.996, 9.329, 1.115 },
        });
        
        Vector columnSums = factory().createVector(new double[] {
            32.814, 20.347, 32.804, 22.706
        });
        
        for (int col = 0; col < d.columns(); col++) {
            double sum = d.foldColumn(col, Matrices.asSumAccumulator(0.0));
            assertEquals(sum, columnSums.get(col));
        }
        
        Vector s = d.foldColumns(Matrices.asSumAccumulator(0.0));
        assertEquals(s, columnSums);

        Vector rowSums = factory().createVector(new double[] {
            21.033, 21.279, 11.601, 18.889, 22.995, 12.874
        });

        for (int row = 0; row < d.columns(); row++) {
            double sum = d.foldRow(row, Matrices.asSumAccumulator(0.0));
            assertEquals(sum, rowSums.get(row));
        }
        
        s = d.foldRows(Matrices.asSumAccumulator(0.0));
        assertEquals(s, rowSums);
    }
}
