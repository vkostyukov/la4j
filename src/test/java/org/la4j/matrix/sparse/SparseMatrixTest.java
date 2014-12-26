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

package org.la4j.matrix.sparse;

import org.junit.Test;
import org.la4j.matrix.AbstractMatrixTest;
import org.la4j.matrix.Matrices;
import org.la4j.matrix.functor.MatrixAccumulator;
import org.la4j.vector.Vector;
import org.la4j.vector.Vectors;
import org.la4j.vector.functor.VectorAccumulator;

import static org.junit.Assert.*;

public abstract class SparseMatrixTest extends AbstractMatrixTest {

    @Test
    public void testCardinality() {

        double array[][] = new double[][] {
                { 1.0, 0.0, 0.0 },
                { 0.0, 5.0, 0.0 }, 
                { 0.0, 0.0, 9.0 } 
        };

        SparseMatrix a = (SparseMatrix) factory().createMatrix(array);

        assertEquals(3, a.cardinality());
    }

    @Test
    public void testLargeMatrix()
    {
        int i = 1000000;
        int j = 2000000;

        SparseMatrix a = (SparseMatrix) factory().createMatrix(i, j);

        assertEquals(i, a.rows());
        assertEquals(j, a.columns());

        for(int x = 0; x < i; x += 100000) {
            for(int y = 0; y < j; y+= 500000) {
                a.set(x, y, x * y);
            }
        }

        for(int x = 0; x < i; x += 100000) {
            for(int y = 0; y < j; y+= 500000) {
                assertEquals(x * y, (long) a.get(x, y));
            }
        }
    }

    @Test
    public void testCapacityOverflow() {
        int i = 65536;
        int j = 65536;

        // Integer 65536 * 65536 overflows to 0
        assertEquals(0, i * j);

        SparseMatrix a = (SparseMatrix) factory().createMatrix(i, j);

        assertEquals(i, a.rows());
        assertEquals(j, a.columns());

        a.set(0, 0, 42.0);
        assertEquals(42.0, a.get(0, 0), Matrices.EPS);

        a.set(i-1, j-1, 7.0);
        assertEquals(7.0, a.get(i-1, j-1), Matrices.EPS);

        // Since values and Indices array sizes are align'd with CCSMatrix and
        //  CRSMatrix.MINIMUM_SIZE (=32), we need to set more than 32 values.
        for(int row = 0 ; row < 32 ; row++) {
            a.set(row, 1, 3.1415);
        }
    }

    @Test
    public void testIssue141() {
        int i = 5000000;
        int j = 7340;

        // Test overflow
        assertTrue(i * j < 0);

        SparseMatrix a = (SparseMatrix) factory().createMatrix(5000000, 7340);

        assertEquals(i, a.rows());
        assertEquals(j, a.columns());

        for(int row = 0 ; row < 32 ; row++) {
            a.set(row, 1, 3.1415);
        }
    }

    @Test
    public void testFoldNonZero_3x3() {

        SparseMatrix a = (SparseMatrix) factory().createMatrix(new double[][] {
                { 1.0, 0.0, 2.0 },
                { 4.0, 0.0, 5.0 },
                { 0.0, 0.0, 0.0 }
        });

        MatrixAccumulator sum = Matrices.asSumAccumulator(0.0);
        MatrixAccumulator product = Matrices.asProductAccumulator(1.0);

        assertEquals(12.0, a.foldNonZero(sum), Matrices.EPS);
        // check whether the accumulator were flushed or not
        assertEquals(12.0, a.foldNonZero(sum), Matrices.EPS);

        assertEquals(40.0, a.foldNonZero(product), Matrices.EPS);
        // check whether the accumulator were flushed or not
        assertEquals(40.0, a.foldNonZero(product), Matrices.EPS);

        assertEquals(20.0, a.foldNonZeroInRow(1, Vectors.asProductAccumulator(1.0)), Matrices.EPS);
        assertEquals(10.0, a.foldNonZeroInColumn(2, Vectors.asProductAccumulator(1.0)), Matrices.EPS);

        double[] nonZeroInColumns = a.foldNonZeroInColumns(Vectors.asProductAccumulator(1.0));
        assertArrayEquals(new double[] {4.0, 1.0, 10.0}, nonZeroInColumns, 1e-5);

        double[] nonZeroInRows = a.foldNonZeroInRows(Vectors.asProductAccumulator(1.0));
        assertArrayEquals(new double[] { 2.0, 20.0, 1.0}, nonZeroInRows, 1e-5);
    }

    @Test
    public void testIsZeroAt_5x3() {

        SparseMatrix a = (SparseMatrix) factory().createMatrix(new double[][] {
                { 1.0, 0.0, 0.0 },
                { 0.0, 0.0, 2.0 },
                { 0.0, 0.0, 0.0 },
                { 0.0, 3.0, 0.0 },
                { 0.0, 0.0, 0.0 }
        });

        assertTrue(a.isZeroAt(2, 2));
        assertFalse(a.isZeroAt(3, 1));
    }

    @Test
    public void testNonZeroAt_3x4() {

        SparseMatrix a = (SparseMatrix) factory().createMatrix(new double[][] {
                { 0.0, 0.0, 2.0, 0.0 },
                { 0.0, 0.0, 0.0, 0.0 },
                { 0.0, 1.0, 0.0, 0.0 }
        });

        assertTrue(a.nonZeroAt(2, 1));
        assertFalse(a.nonZeroAt(0, 3));
    }

    @Test
    public void testGetOrElse_2x5() {

        SparseMatrix a = (SparseMatrix) factory().createMatrix(new double[][] {
                { 0.0, 0.0, 0.0, 1.0, 0.0 },
                { 0.0, 0.0, 3.0, 0.0, 0.0 },
        });

        assertEquals(0.0, a.getOrElse(0, 2, 0.0), Matrices.EPS);
        assertEquals(3.0, a.getOrElse(1, 2, 3.14), Matrices.EPS);
        assertEquals(4.2, a.getOrElse(1, 3, 4.2), Matrices.EPS);
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetOrElse_IndexCheck_RowNegative() {
    	 SparseMatrix a = (SparseMatrix) factory().createMatrix(new double[][] {
                 { 0.0, 0.0, 0.0, 1.0, 0.0 },
                 { 0.0, 0.0, 3.0, 0.0, 0.0 },
         });
    	 
    	 a.getOrElse(-1, 1, 0.0);
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetOrElse_IndexCheck_ColumnNegative() {
    	 SparseMatrix a = (SparseMatrix) factory().createMatrix(new double[][] {
                 { 0.0, 0.0, 0.0, 1.0, 0.0 },
                 { 0.0, 0.0, 3.0, 0.0, 0.0 },
         });
    	 
    	 a.getOrElse(1, -1, 0.0);
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetOrElse_IndexCheck_RowTooLarge() {
    	 SparseMatrix a = (SparseMatrix) factory().createMatrix(new double[][] {
                 { 0.0, 0.0, 0.0, 1.0, 0.0 },
                 { 0.0, 0.0, 3.0, 0.0, 0.0 },
         });
    	 
    	 a.getOrElse(a.rows(), 1, 0.0);
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetOrElse_IndexCheck_ColumnTooLarge() {
    	 SparseMatrix a = (SparseMatrix) factory().createMatrix(new double[][] {
                 { 0.0, 0.0, 0.0, 1.0, 0.0 },
                 { 0.0, 0.0, 3.0, 0.0, 0.0 },
         });
    	 
    	 a.getOrElse(1, a.columns(), 0.0);
    }
    
    @Test
    public void testGetOrElse_IndexCheck_Valid() {
    	SparseMatrix a = (SparseMatrix) factory().createMatrix(new double[][] {
                 { 0.0, 0.0, 0.0, 1.0, 0.0 },
                 { 0.0, 0.0, 3.0, 0.0, 0.0 },
         });
    	 
    	 assertEquals(0.0, a.getOrElse(1, 1, 0.0), 0.0);
    }
}
