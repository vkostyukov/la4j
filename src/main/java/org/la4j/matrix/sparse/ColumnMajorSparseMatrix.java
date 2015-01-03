/*
 * Copyright 2011-2014, by Vladimir Kostyukov and Contributors.
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

import org.la4j.factory.Factory;
import org.la4j.iterator.ColumnMajorMatrixIterator;
import org.la4j.iterator.MatrixIterator;
import org.la4j.matrix.Matrix;
import org.la4j.operation.MatrixMatrixOperation;
import org.la4j.operation.MatrixOperation;
import org.la4j.operation.MatrixVectorOperation;
import org.la4j.vector.Vector;

import java.util.Iterator;
import java.util.Random;

public abstract class ColumnMajorSparseMatrix extends SparseMatrix {

    /**
     * Creates a zero {@link ColumnMajorSparseMatrix} of the given shape:
     * {@code rows} x {@code columns}.
     */
    public static ColumnMajorSparseMatrix zero(int rows, int columns) {
        return CCSMatrix.zero(rows, columns);
    }

    /**
     * Creates a diagonal {@link ColumnMajorSparseMatrix} of the given {@code size} whose
     * diagonal elements are equal to {@code diagonal}.
     */
    public static ColumnMajorSparseMatrix diagonal(int size, double diagonal) {
        return CCSMatrix.diagonal(size, diagonal);
    }

    /**
     * Creates an identity {@link ColumnMajorSparseMatrix} of the given {@code size}.
     */
    public static ColumnMajorSparseMatrix identity(int size) {
        return CCSMatrix.identity(size);
    }

    /**
     * Creates a random {@link ColumnMajorSparseMatrix} of the given shape:
     * {@code rows} x {@code columns}.
     */
    public static ColumnMajorSparseMatrix random(int rows, int columns, double density, Random random) {
        return CCSMatrix.random(rows, columns, density, random);
    }

    /**
     * Creates a random symmetric {@link ColumnMajorSparseMatrix} of the given {@code size}.
     */
    public static ColumnMajorSparseMatrix randomSymmetric(int size, double density, Random random) {
        return CCSMatrix.randomSymmetric(size, density, random);
    }

    /**
     * Creates a new {@link ColumnMajorSparseMatrix} from the given 1D {@code array} with
     * compressing (copying) the underlying array.
     */
    public static ColumnMajorSparseMatrix from1DArray(int rows, int columns, double[] array) {
        return CCSMatrix.from1DArray(rows, columns, array);
    }

    /**
     * Creates a new {@link ColumnMajorSparseMatrix} from the given 2D {@code array} with
     * compressing (copying) the underlying array.
     */
    public static ColumnMajorSparseMatrix from2DArray(double[][] array) {
        return CCSMatrix.from2DArray(array);
    }

    /**
     * Creates a block {@link ColumnMajorSparseMatrix} of the given blocks {@code a},
     * {@code b}, {@code c} and {@code d}.
     */
    public static ColumnMajorSparseMatrix block(Matrix a, Matrix b, Matrix c, Matrix d) {
        return CCSMatrix.block(a, b, c, d);
    }

    @Override
    public Matrix transpose() {
        Matrix result = RowMajorSparseMatrix.zero(columns, rows);
        MatrixIterator it = nonZeroColumnMajorIterator();

        while (it.hasNext()) {
            double x = it.next();
            int i = it.rowIndex();
            int j = it.columnIndex();
            result.set(j, i, x);
        }

        return result;
    }

    @Override
    public Matrix rotate() {
        // TODO: it can be a bit faster
        Matrix result = RowMajorSparseMatrix.zero(columns, rows);
        MatrixIterator it = nonZeroColumnMajorIterator();

        while (it.hasNext()) {
            double x = it.next();
            int i = it.rowIndex();
            int j = it.columnIndex();
            result.set(j, rows - 1 - i, x);
        }

        return result;
    }

    protected ColumnMajorSparseMatrix(Factory factory, int rows, int columns) {
        super(factory, rows, columns);
    }

    @Override
    public ColumnMajorMatrixIterator iterator() {
        return columnMajorIterator();
    }

    @Override
    public MatrixIterator nonZeroIterator() {
        return nonZeroColumnMajorIterator();
    }

    public abstract Iterator<Integer> iteratorOrNonZeroColumns();

    @Override
    public <T> T apply(MatrixOperation<T> operation) {
        operation.ensureApplicableTo(this);
        return operation.apply(this);
    }

    @Override
    public <T> T apply(MatrixMatrixOperation<T> operation, Matrix that) {
        return that.apply(operation.partiallyApply(this));
    }

    @Override
    public <T> T apply(MatrixVectorOperation<T> operation, Vector that) {
        return that.apply(operation.partiallyApply(this));
    }
}
