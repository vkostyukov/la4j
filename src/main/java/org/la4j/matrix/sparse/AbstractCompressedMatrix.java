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
 * Contributor(s): Ewald Grusk
 * 
 */

package org.la4j.matrix.sparse;

import org.la4j.factory.Factory;
import org.la4j.matrix.AbstractMatrix;
import org.la4j.matrix.Matrix;

public abstract class AbstractCompressedMatrix extends AbstractMatrix 
    implements SparseMatrix {

    protected int cardinality;

    public AbstractCompressedMatrix(Factory factory, int rows, int columns) {
        super(factory, rows, columns);
    }

    @Override
    public int cardinality() {
        return cardinality;
    }

    @Override
    public double density() {
        return cardinality / (double) (rows * columns);
    }

    protected long capacity() {
        return ((long) rows) * columns;
    }

    protected void ensureCardinalityIsCorrect(long rows, long columns, long cardinality) {
        if (cardinality < 0) {
            fail("Cardinality should be positive: " + cardinality + ".");
        }
    
        long capacity = rows * columns;

        if (cardinality > capacity) {
            fail("Cardinality should be less then or equal to capacity: " + cardinality + ".");
        }
    }
}
