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

import org.la4j.matrix.AbstractMatrixTest;

public abstract class SparseMatrixTest extends AbstractMatrixTest {

    public void testCardinality() {

        double array[][] = new double[][] {
                { 1.0, 0.0, 0.0 },
                { 0.0, 5.0, 0.0 }, 
                { 0.0, 0.0, 9.0 } 
        };

        SparseMatrix a = (SparseMatrix) factory().createMatrix(array);

        assertEquals(3, a.cardinality());
    }

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

    public void testCapacityOverflow() {
        int i = 65536;
        int j = 65536;

        // Integer 65536 * 65536 overflows to 0
        assertEquals(0, i * j);

        SparseMatrix a = (SparseMatrix) factory().createMatrix(i, j);

        assertEquals(i, a.rows());
        assertEquals(j, a.columns());

        a.set(0, 0, 42.0);
        assertEquals(42.0, a.get(0, 0));

        a.set(i-1, j-1, 7.0);
        assertEquals(7.0, a.get(i-1, j-1));

        // Since values and Indices array sizes are align'd with CCSMatrix and
        //  CRSMatrix.MINIMUM_SIZE (=32), we need to set more than 32 values.
        for(int row = 0 ; row < 32 ; row++) {
            a.set(row, 1, 3.1415);
        }
    }

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
}
