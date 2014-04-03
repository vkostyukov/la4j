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
import org.la4j.matrix.Matrices;
import org.la4j.matrix.Matrix;
import org.la4j.matrix.functor.MatrixAccumulator;
import org.la4j.matrix.functor.MatrixProcedure;
import org.la4j.vector.Vector;

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

    @Override
    public double get(int i, int j) {
        return getOrElse(i, j, 0.0);
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

    @Override
    public boolean isZeroAt(int i, int j) {
        return !nonZeroAt(i, j);
    }

    @Override
    public void eachNonZero(MatrixProcedure procedure) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (Math.abs(get(i,j)) > Matrices.EPS) {
                    procedure.apply(i, j, get(i, j));
                }
            }
        }
    }

    @Override
    public void eachNonZeroInRow(int i, MatrixProcedure procedure) {
        for (int j = 0; j < columns; j++) {
            if (Math.abs(get(i, j)) > Matrices.EPS) {
                procedure.apply(i, j, get(i, j));
            }
        }
    }

    @Override
    public void eachNonZeroInColumn(int j, MatrixProcedure procedure) {
        for (int i = 0; i < rows; i++) {
            if (Math.abs(get(i, j)) > Matrices.EPS) {
                procedure.apply(i, j, get(i, j));
            }
        }
    }

    @Override
    public double foldNonZero(MatrixAccumulator accumulator) {
        eachNonZero(Matrices.asAccumulatorProcedure(accumulator));
        return accumulator.accumulate();
    }

    @Override
    public double foldNonZeroInRow(int i, MatrixAccumulator accumulator) {
        eachNonZeroInRow(i, Matrices.asAccumulatorProcedure(accumulator));
        return accumulator.accumulate();
    }

    @Override
    public double foldNonZeroInColumn(int j, MatrixAccumulator accumulator) {
        eachNonZeroInColumn(j, Matrices.asAccumulatorProcedure(accumulator));
        return accumulator.accumulate();
    }

    @Override
    public Vector foldNonZeroInColumns(MatrixAccumulator accumulator) {

        Vector result = factory.createVector(columns);

        for (int i = 0; i < columns; i++) {
            result.set(i, foldNonZeroInColumn(i, accumulator));
        }

        return result;
    }

    @Override
    public Vector foldNonZeroInRows(MatrixAccumulator accumulator) {

        Vector result = factory.createVector(rows);

        for (int i = 0; i < rows; i++) {
            result.set(i, foldNonZeroInRow(i, accumulator));
        }

        return result;
    }
}
