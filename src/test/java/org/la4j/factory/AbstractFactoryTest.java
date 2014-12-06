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

package org.la4j.factory;

import org.junit.Test;
import org.la4j.matrix.Matrices;
import org.la4j.matrix.Matrix;
import org.la4j.vector.Vector;
import org.la4j.vector.Vectors;

import static org.junit.Assert.assertEquals;

public abstract class AbstractFactoryTest {

    public abstract Factory factory();

    @Test
    public void testCreateMatrix() {
        Matrix a = factory().createMatrix();
        Matrix b = factory().createMatrix(5, 5);
        Matrix c = factory().createRandomMatrix(5, 5);
        Matrix d = factory().createSquareMatrix(5);

        assertEquals(0, a.rows());
        assertEquals(0, a.columns());
        assertEquals(5, b.columns());
        assertEquals(5, b.rows());
        assertEquals(5, c.rows());
        assertEquals(5, c.columns());
        assertEquals(5, d.rows());
        assertEquals(5, d.columns());

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++)
            {
                assertEquals(0.0, b.get(i, j), Matrices.EPS);
                assertEquals(0.0, d.get(i, j), Matrices.EPS);
            }
        }
    }

    @Test
    public void testCreateMatrixFromArray() {
        double array[][] = new double[][] {
                { 1.0, 0.0, 3.0 },
                { 0.0, 5.0, 0.0 },
                { 7.0, 0.0, 9.0 }
        };

        Matrix a = factory().createMatrix(array);

        assertEquals(3, a.rows());
        assertEquals(3, a.columns());

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                assertEquals(array[i][j], a.get(i, j), Matrices.EPS);
            }
        }
    }

    @Test
    public void testCreateConstantMatrix_3x3() {

        Matrix a = factory().createConstantMatrix(3, 3, 10.0);

        assertEquals(3, a.rows());
        assertEquals(3, a.columns());

        for (int i = 0; i < a.rows(); i++) {
            for (int j = 0; j < a.columns(); j++) {
                assertEquals(10.0, a.get(i, j), Matrices.EPS);
            }
        }
    }

    @Test
    public void testCreateConstantMatrix_2x5() {

        Matrix a = factory().createConstantMatrix(2, 5, 20.0);

        assertEquals(2, a.rows());
        assertEquals(5, a.columns());

        for (int i = 0; i < a.rows(); i++) {
            for (int j = 0; j < a.columns(); j++) {
                assertEquals(20.0, a.get(i, j), Matrices.EPS);
            }
        }
    }

    @Test
    public void testCreateConstantMatrix_4x1() {

        Matrix a = factory().createConstantMatrix(4, 1, 30.0);

        assertEquals(4, a.rows());
        assertEquals(1, a.columns());

        for (int i = 0; i < a.rows(); i++) {
            for (int j = 0; j < a.columns(); j++) {
                assertEquals(30.0, a.get(i, j), Matrices.EPS);
            }
        }
    }

    @Test
    public void testCreateRandomSymmetricMatrix() {
        Matrix a = factory().createRandomSymmetricMatrix(5);

        for (int i = 0; i < a.rows(); i++) {
            for (int j = i; j < a.columns(); j++) {
                assertEquals(a.get(i, j), a.get(j, i), Matrices.EPS);
            }
        }
    }

    @Test
    public void testCreateIdentityMatrix() {

        Matrix a = factory().createIdentityMatrix(3);

        assertEquals(3, a.rows());
        assertEquals(3, a.columns());

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (i == j) {
                    assertEquals(1.0, a.get(i, j), Matrices.EPS);
                } else {
                    assertEquals(0.0, a.get(i, j), Matrices.EPS);
                }
            }
        }
    }

    @Test
    public void testCreateDiagonalMatrix_3x3() {

        double diagonal[] = new double[] { 1.0, 2.0, 3.0 };
        Matrix a = factory().createDiagonalMatrix(diagonal);

        assertEquals(3, a.rows());
        assertEquals(3, a.columns());

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (i == j) {
                    assertEquals(diagonal[i], a.get(i, j), Matrices.EPS);
                } else {
                    assertEquals(0.0, a.get(i, j), Matrices.EPS);
                }
            }
        }
    }

    @Test
    public void testCreateVector() {
        Vector a = factory().createVector();
        Vector b = factory().createVector(5);
        Vector c = factory().createRandomVector(5);

        assertEquals(0, a.length());
        assertEquals(5, b.length());
        assertEquals(5, c.length());

        for (int i = 0; i < b.length(); i++) {
            assertEquals(0.0, b.get(i), Matrices.EPS);
        }
    }

    @Test
    public void testCreateConstantVector_3() {
        Vector a = factory().createConstantVector(3, 3.14);
        Vector b = factory().createConstantVector(1, 3.14);

        assertEquals(3, a.length());
        assertEquals(1, b.length());

        assertEquals(b.get(0), 3.14, Vectors.EPS);

        for (int i = 0; i < 3; i++) {
            assertEquals(3.14, a.get(i), Vectors.EPS);
        }
    }

    @Test
    public void testCreateVectorFromArray() {
        double array[] = new double[] { 1.0, 0.0, 2.0, 0.0, 3.0 };
        Vector a = factory().createVector(array);

        assertEquals(5, a.length());

        for (int i = 0; i < 5; i++) {
            assertEquals(array[i], a.get(i), Vectors.EPS);
        }
    }
}
