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
import org.la4j.Matrix;
import org.la4j.matrix.SparseMatrix;

import static org.junit.Assert.assertEquals;

public class LargeSparseMatrixTest {
    @Test
    public void rowMajorOperationsDoNotOverflowCursors() {
        checkOperations(new CRSMatrix(65536, 65537), new CRSMatrix(65536, 65537));
    }

    @Test
    public void columnMajorOperationsDoNotOverflowCursors() {
        checkOperations(new CCSMatrix(65536, 65537), new CCSMatrix(65536, 65537));
    }

    private void checkOperations(SparseMatrix left, SparseMatrix right) {
        left.set(0, 0, 1.0);
        left.set(32768, 32768, 2.0);
        left.set(65535, 65536, 3.0);
        right.set(0, 1, 4.0);
        right.set(32768, 32768, 5.0);
        right.set(65535, 65536, 6.0);

        Matrix sum = left.add(right);
        assertEquals(1.0, sum.get(0, 0), 0.0);
        assertEquals(4.0, sum.get(0, 1), 0.0);
        assertEquals(7.0, sum.get(32768, 32768), 0.0);
        assertEquals(9.0, sum.get(65535, 65536), 0.0);
        assertEquals(4, ((SparseMatrix) sum).cardinality());

        Matrix difference = left.subtract(right);
        assertEquals(1.0, difference.get(0, 0), 0.0);
        assertEquals(-4.0, difference.get(0, 1), 0.0);
        assertEquals(-3.0, difference.get(32768, 32768), 0.0);
        assertEquals(-3.0, difference.get(65535, 65536), 0.0);
        assertEquals(4, ((SparseMatrix) difference).cardinality());

        Matrix product = left.hadamardProduct(right);
        assertEquals(10.0, product.get(32768, 32768), 0.0);
        assertEquals(18.0, product.get(65535, 65536), 0.0);
        assertEquals(2, ((SparseMatrix) product).cardinality());
    }
}
