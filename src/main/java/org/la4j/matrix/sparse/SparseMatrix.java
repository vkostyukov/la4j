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
import org.la4j.iterator.RowMajorMatrixIterator;
import org.la4j.iterator.VectorIterator;
import org.la4j.matrix.AbstractMatrix;
import org.la4j.matrix.Matrices;
import org.la4j.matrix.Matrix;
import org.la4j.matrix.dense.DenseMatrix;
import org.la4j.matrix.functor.MatrixAccumulator;
import org.la4j.matrix.functor.MatrixProcedure;
import org.la4j.vector.Vector;
import org.la4j.vector.Vectors;
import org.la4j.vector.functor.VectorAccumulator;
import org.la4j.vector.functor.VectorProcedure;
import org.la4j.vector.sparse.SparseVector;

import java.util.Random;

public abstract class SparseMatrix extends AbstractMatrix {

    /**
     * Creates a zero {@link SparseMatrix} of the given shape:
     * {@code rows} x {@code columns}.
     */
    public static SparseMatrix zero(int rows, int columns) {
        return CRSMatrix.zero(rows, columns);
    }

    /**
     * Creates a diagonal {@link SparseMatrix} of the given {@code size} whose
     * diagonal elements are equal to {@code diagonal}.
     */
    public static SparseMatrix diagonal(int size, double diagonal) {
        return CRSMatrix.diagonal(size, diagonal);
    }

    /**
     * Creates an identity {@link SparseMatrix} of the given {@code size}.
     */
    public static SparseMatrix identity(int size) {
        return CRSMatrix.identity(size);
    }

    /**
     * Creates a random {@link SparseMatrix} of the given shape:
     * {@code rows} x {@code columns}.
     */
    public static SparseMatrix random(int rows, int columns, double density, Random random) {
        return CRSMatrix.random(rows, columns, density, random);
    }

    /**
     * Creates a random symmetric {@link SparseMatrix} of the given {@code size}.
     */
    public static SparseMatrix randomSymmetric(int size, double density, Random random) {
        return CRSMatrix.randomSymmetric(size, density, random);
    }

    /**
     * Creates a new {@link SparseMatrix} from the given 1D {@code array} with
     * compressing (copying) the underlying array.
     */
    public static SparseMatrix from1DArray(int rows, int columns, double[] array) {
        return CRSMatrix.from1DArray(rows, columns, array);
    }

    /**
     * Creates a new {@link SparseMatrix} from the given 2D {@code array} with
     * compressing (copying) the underlying array.
     */
    public static SparseMatrix from2DArray(double[][] array) {
        return CRSMatrix.from2DArray(array);
    }

    /**
     * Creates a block {@link SparseMatrix} of the given blocks {@code a},
     * {@code b}, {@code c} and {@code d}.
     */
    public static SparseMatrix block(Matrix a, Matrix b, Matrix c, Matrix d) {
        return CRSMatrix.block(a, b, c, d);
    }

    protected int cardinality;

    protected SparseMatrix(Factory factory, int rows, int columns) {
        super(factory, rows, columns);
    }

    @Override
    public double get(int i, int j) {
        return getOrElse(i, j, 0.0);
    }

    /**
     * Gets the specified element, or a {@code defaultValue} if there
     * is no actual element at ({@code i}, {@code j}) in this sparse matrix.
     *
     * @param i the element's row index
     * @param j the element's column index
     * @param defaultValue the default value
     *
     * @return the element of this vector or a default value
     */
    public abstract double getOrElse(int i, int j, double defaultValue);

    /**
     * Returns the cardinality (the number of non-zero elements)
     * of this sparse matrix.
     * 
     * @return the cardinality of this matrix
     */
    public int cardinality() {
        return cardinality;
    }

    /**
     * Returns the density (non-zero elements divided by total elements)
     * of this sparse matrix.
     * 
     * @return the density of this matrix
     */
    public double density() {
        return cardinality / (double) (rows * columns);
    }

    /**
     * @return a capacity of this sparse matrix
     */
    protected long capacity() {
        return ((long) rows) * columns;
    }

    @Override
    public Vector getRow(int i) {
        Vector result = SparseVector.zero(columns);
        VectorIterator it = nonZeroIteratorOfRow(i);

        while (it.hasNext()) {
            double x = it.next();
            int j = it.index();
            result.set(j, x);
        }

        return result;
    }

    @Override
    public Vector getColumn(int j) {
        Vector result = SparseVector.zero(rows);
        VectorIterator it = nonZeroIteratorOfColumn(j);

        while (it.hasNext()) {
            double x = it.next();
            int i = it.index();
            result.set(i, x);
        }

        return result;
    }

    @Override
    public Matrix multiply(double value) {
        MatrixIterator it = nonZeroIterator();
        Matrix result = blank();

        while (it.hasNext()) {
            double x = it.next();
            int i = it.rowIndex();
            int j = it.columnIndex();
            result.set(i, j, x * value);
        }

        return result;
    }

    @Override
    public Matrix add(double value) {
        MatrixIterator it = nonZeroIterator();
        Matrix result = DenseMatrix.constant(rows, columns, value);

        while (it.hasNext()) {
            double x = it.next();
            int i = it.rowIndex();
            int j = it.columnIndex();
            result.set(i, j, x + value);
        }

        return result;
    }

    /**
     * Whether or not the specified element is zero.
     *
     * @param i element's row index
     * @param j element's column index
     *
     * @return {@code true} if specified element is zero, {@code false} otherwise
     */
    public boolean isZeroAt(int i, int j) {
        return !nonZeroAt(i, j);
    }

    /**
     * Whether or not the specified element is not zero.
     *
     * @param i element's row index
     * @param j element's column index
     *
     * @return {@code true} if specified element is not zero, {@code false} otherwise
     */
    public abstract boolean nonZeroAt(int i, int j);

    /**
     * Applies given {@code procedure} to each non-zero element of this matrix.
     *
     * @param procedure the matrix procedure
     */
    public void eachNonZero(MatrixProcedure procedure) {
        MatrixIterator it = nonZeroIterator();

        while (it.hasNext()) {
            double x = it.next();
            int i = it.rowIndex();
            int j = it.columnIndex();
            procedure.apply(i, j, x);
        }
    }

    /**
     * Deprecated. Use {@link SparseMatrix#eachNonZeroInRow(int, VectorProcedure)} instead.
     * <p/>
     * Applies given {@code procedure} to each non-zero element of specified row of this matrix.
     *
     * @param i the row index
     * @param procedure the matrix procedure
     */
    @Deprecated
    public void eachNonZeroInRow(int i, MatrixProcedure procedure) {
        final MatrixProcedure p = procedure;
        final int ii = i;
        eachNonZeroInRow(i, new VectorProcedure() {
            @Override
            public void apply(int j, double value) {
                p.apply(ii, j, value);
            }
        });
    }
    
    /**
     * Applies the given {@code procedure} to each non-zero element of the specified row of this matrix.
     * 
     * @param i the row index. 
     * @param procedure the {@link VectorProcedure}. 
     */
    public void eachNonZeroInRow(int i, VectorProcedure procedure) {
        VectorIterator it = nonZeroIteratorOfRow(i);

        while (it.hasNext()) {
            double x = it.next();
            int j = it.index();
            procedure.apply(j, x);
        }
    }

    /**
     * Deprecated. Use {@link SparseMatrix#eachNonZeroInColumn(int, VectorProcedure)} instead.
     * <p/>
     * Applies given {@code procedure} to each non-zero element of specified column of this matrix.
     *
     * @param j the column index
     * @param procedure the matrix procedure
     */
    @Deprecated
    public void eachNonZeroInColumn(int j, MatrixProcedure procedure) {
        final MatrixProcedure p = procedure;
        final int jj = j;
        eachNonZeroInColumn(j, new VectorProcedure() {
            @Override
            public void apply(int i, double value) {
                p.apply(i, jj, value);
            }
        });
    }
    
    /**
     * Applies the given {@code procedure} to each non-zero element of the specified column of this matrix.
     * 
     * @param j the column index.
     * @param procedure the {@link VectorProcedure}.
     */
    public void eachNonZeroInColumn(int j, VectorProcedure procedure) {
        VectorIterator it = nonZeroIteratorOfColumn(j);

        while (it.hasNext()) {
            double x = it.next();
            int i = it.index();
            procedure.apply(i, x);
        }
    }

    /**
     * Folds non-zero elements of this matrix with given {@code accumulator}.
     *
     * @param accumulator the matrix accumulator
     *
     * @return the accumulated value
     */
    public double foldNonZero(MatrixAccumulator accumulator) {
        eachNonZero(Matrices.asAccumulatorProcedure(accumulator));
        return accumulator.accumulate();
    }

    /**
     * Folds non-zero elements of the specified row in this matrix with the given {@code accumulator}.
     * 
     * @param i the row index.
     * @param accumulator the {@link VectorAccumulator}.
     * 
     * @return the accumulated value.
     */
    public double foldNonZeroInRow(int i, VectorAccumulator accumulator) {
        eachNonZeroInRow(i, Vectors.asAccumulatorProcedure(accumulator));
        return accumulator.accumulate();
    }

    /**
     * Folds non-zero elements of the specified column in this matrix with the given {@code accumulator}.
     * 
     * @param j the column index.
     * @param accumulator the {@link VectorAccumulator}.
     * 
     * @return the accumulated value.
     */
    public double foldNonZeroInColumn(int j, VectorAccumulator accumulator) {
        eachNonZeroInColumn(j, Vectors.asAccumulatorProcedure(accumulator));
        return accumulator.accumulate();
    }

    /**
     * Folds non-zero elements (in a column-by-column manner) of this matrix with given {@code accumulator}.
     *
     * @param accumulator the matrix accumulator
     *
     * @return the accumulated vector
     */
    public double[] foldNonZeroInColumns(VectorAccumulator accumulator) {
        double[] result = new double[columns];

        for (int j = 0; j < columns; j++) {
            result[j] = foldNonZeroInColumn(j, accumulator);
        }

        return result;
    }

    /**
     * Folds non-zero elements (in a row-by-row manner) of this matrix with given {@code accumulator}.
     *
     * @param accumulator the matrix accumulator
     *
     * @return the accumulated vector
     */
    public double[] foldNonZeroInRows(VectorAccumulator accumulator) {
        double[] result = new double[rows];

        for (int i = 0; i < rows; i++) {
            result[i] = foldNonZeroInRow(i, accumulator);
        }

        return result;
    }

    /**
     * Returns a non-zero matrix iterator.
     *
     * @return a non-zero matrix iterator
     */
    public MatrixIterator nonZeroIterator() {
        return nonZeroRowMajorIterator();
    }

    /**
     * Returns a non-zero row-major matrix iterator.
     *
     * @return a non-zero row-major matrix iterator.
     */
    public RowMajorMatrixIterator nonZeroRowMajorIterator() {
        return new RowMajorMatrixIterator(rows, columns) {
            private long limit = (long) rows * columns;
            private int i = -1;

            @Override
            public int rowIndex() {
                return i / columns;
            }

            @Override
            public int columnIndex() {
                return i - rowIndex() * columns;
            }

            @Override
            public double get() {
                return SparseMatrix.this.get(rowIndex(), columnIndex());
            }

            @Override
            public void set(double value) {
                SparseMatrix.this.set(rowIndex(), columnIndex(), value);
            }

            @Override
            public boolean hasNext() {
                while (i + 1 < limit) {
                    i++;
                    if (SparseMatrix.this.nonZeroAt(rowIndex(), columnIndex())) {
                        i--;
                        break;
                    }
                }

                return i + 1 < limit;
            }

            @Override
            public Double next() {
                i++;
                return get();
            }
        };
    }

    /**
     * Returns a non-zero column-major matrix iterator.
     *
     * @return a non-zero column major matrix iterator.
     */
    public ColumnMajorMatrixIterator nonZeroColumnMajorIterator() {
        return new ColumnMajorMatrixIterator(rows, columns) {
            private long limit = (long) rows * columns;
            private int i = -1;

            @Override
            public int rowIndex() {
                return i - columnIndex() * rows;
            }

            @Override
            public int columnIndex() {
                return i / rows;
            }

            @Override
            public double get() {
                return SparseMatrix.this.get(rowIndex(), columnIndex());
            }

            @Override
            public void set(double value) {
                SparseMatrix.this.set(rowIndex(), columnIndex(), value);
            }

            @Override
            public boolean hasNext() {
                while (i + 1 < limit) {
                    i++;
                    if (SparseMatrix.this.nonZeroAt(rowIndex(), columnIndex())) {
                        i--;
                        break;
                    }
                }

                return i + 1 < limit;
            }

            @Override
            public Double next() {
                i++;
                return get();
            }
        };
    }

    /**
     * Returns a non-zero vector iterator of the given row {@code i}.
     *
     * @return a non-zero vector iterator
     */
    public VectorIterator nonZeroIteratorOfRow(int i) {
        final int ii = i;
        return new VectorIterator(columns) {
            private int j = -1;

            @Override
            public int index() {
                return j;
            }

            @Override
            public double get() {
                return SparseMatrix.this.get(ii, j);
            }

            @Override
            public void set(double value) {
                SparseMatrix.this.set(ii, j, value);
            }

            @Override
            public boolean hasNext() {
                while (j + 1 < columns && SparseMatrix.this.isZeroAt(ii, j + 1)) {
                    j++;
                }

                return j + 1 < columns;
            }

            @Override
            public Double next() {
                j++;
                return get();
            }
        };
    }

    /**
     * Returns a non-zero vector iterator of the given column {@code j}.
     *
     * @return a non-zero vector iterator
     */
    public VectorIterator nonZeroIteratorOfColumn(int j) {
        final int jj = j;
        return new VectorIterator(rows) {
            private int i = -1;

            @Override
            public int index() {
                return i;
            }

            @Override
            public double get() {
                return SparseMatrix.this.get(i, jj);
            }

            @Override
            public void set(double value) {
                SparseMatrix.this.set(i, jj, value);
            }

            @Override
            public boolean hasNext() {
                while (i + 1 < rows && SparseMatrix.this.isZeroAt(i + 1, jj)) {
                    i++;
                }

                return i + 1 < rows;
            }

            @Override
            public Double next() {
                i++;
                return get();
            }
        };
    }

    protected void ensureCardinalityIsCorrect(long rows, long columns, long cardinality) {
        if (cardinality < 0) {
            fail("Cardinality should be positive: " + cardinality + ".");
        }

        long capacity = capacity();

        if (cardinality > capacity) {
            fail("Cardinality should be less then or equal to capacity: " + capacity + ".");
        }
    }
}
