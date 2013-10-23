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
 *                 Julia Kostyukova
 *                 Jakob Moellers
 *                 Maxim Samoylov
 *                 Anveshi Charuvaka
 */

package org.la4j.matrix;

import java.io.Externalizable;

import org.la4j.LinearAlgebra;
import org.la4j.decomposition.MatrixDecompositor;
import org.la4j.factory.Factory;
import org.la4j.inversion.MatrixInverter;
import org.la4j.linear.LinearSystemSolver;
import org.la4j.matrix.functor.AdvancedMatrixPredicate;
import org.la4j.matrix.functor.MatrixAccumulator;
import org.la4j.matrix.functor.MatrixFunction;
import org.la4j.matrix.functor.MatrixPredicate;
import org.la4j.matrix.functor.MatrixProcedure;
import org.la4j.vector.Vector;

/**
 * This interface represents real matrix.
 * <p>
 * <a href="http://mathworld.wolfram.com/Matrix.html">
 * http://mathworld.wolfram.com/Matrix.html </a>
 * </p>
 * 
 */
public interface Matrix extends Externalizable {

    /**
     * Returns the (<code>i, j</code>) element of this matrix.
     * 
     * <p>
     * See <a href="http://mathworld.wolfram.com/MatrixElement.html">
     * http://mathworld.wolfram.com/MatrixElement.html</a> for more details.
     * </p>
     * 
     * @param i
     *            row of matrix
     * @param j
     *            column of matrix
     * @return the element at (i, j) of this matrix
     */
    double get(int i, int j);

    /**
     * Assigns the (<code>i, j</code>) element of this matrix to <code>value</code>.
     * 
     * 
     * @param i
     *            row of matrix
     * @param j
     *            column of matrix
     * @param value
     *            to be stored at (i, j) in matrix
     */
    void set(int i, int j, double value);

    /**
     * Assigns all elements of this matrix to <code>value</code>.
     * 
     * @param value
     */
    void assign(double value);

    /**
     * Swaps <code>i</code> and <code>j</code> rows of this matrix.
     * 
     * @param i
     * @param j
     */
    void swapRows(int i, int j);

    /**
     * Swaps <code>i</code> and <code>j</code> columns of this matrix.
     * 
     * @param i
     * @param j
     */
    void swapColumns(int i, int j);

    /**
     * Gets rows number of this matrix.
     * 
     * @return rows number
     */
    int rows();

    /**
     * Gets columns number of this matrix.
     * 
     * @return columns number
     */
    int columns();

    /**
     * Transposes this matrix.
     * 
     * @return transposed matrix
     */
    Matrix transpose();

    /**
     * Transposes this matrix with specified <code>factory</code>.
     * 
     * @param factory
     * @return
     */
    Matrix transpose(Factory factory);

    /**
     * Rotates a matrix by 90 degrees to the right
     * 
     * @return The rotated matrix
     */
    Matrix rotate();

    /**
     * Rotates a matrix by 90 degrees to the right
     * 
     * @return The rotated matrix
     */
    Matrix rotate(Factory factory);

    /**
     * Power operation for matrices. Matrix is returned to the power of n. This
     * function uses the Exponentiation by squaring method.
     * 
     * @param n
     *            The exponent
     * @return Exponentiated matrix
     */
    Matrix power(int n);

    /**
     * Power operation for matrices. Matrix is returned to the power of n. This
     * function uses the Exponentiation by squaring method.
     * 
     * @param n
     *            The exponent
     * @param factory
     *            Factory for this matrix
     * @return Exponentiated matrix
     */
    Matrix power(int n, Factory factory);

    /**
     * Scales this matrix by <code>value</code>.
     * 
     * @param value
     * @return scaled matrix
     */
    Matrix multiply(double value);

    /**
     * Scales this matrix to <code>value</code> with <code>factory</code>.
     * 
     * @param value
     * @param factory
     * @return
     */
    Matrix multiply(double value, Factory factory);

    /**
     * Multiplies this matrix by <code>vector</code>.
     * 
     * @param vector
     * @return multiplied matrix
     */
    Vector multiply(Vector vector);

    /**
     * Multiplies this matrix by <code>vector</code> with specified
     * <code>factory</code>.
     * 
     * @param vector
     * @param factory
     * @return multiplied matrix
     */
    Vector multiply(Vector vector, Factory factory);

    /**
     * Multiplies this matrix by other <code>matrix</code>.
     * 
     * @param matrix
     *            to be multiplied
     * @return multiplied matrix
     */
    Matrix multiply(Matrix matrix);

    /**
     * Multiplies this matrix to other <code>matrix</code> with specified
     * <code>factory</code>.
     * 
     * @param matrix
     * @param factory
     */
    Matrix multiply(Matrix matrix, Factory factory);

    /**
     * Subtracts the <code>value</code> from this matrix.
     * 
     * @param value
     *            to be subtracted
     * @return subtracted matrix
     */
    Matrix subtract(double value);

    /**
     * Subtracts <code>value</code> from this matrix with specified
     * <code>factory</code>.
     * 
     * @param value
     * @param factory
     */
    Matrix subtract(double value, Factory factory);

    /**
     * Subtracts other <code>matrix</code> by this matrix.
     * 
     * @param matrix
     *            to be subtracted
     * @return subtracted matrix
     */
    Matrix subtract(Matrix matrix);

    /**
     * Subtracts other <code>matrix</code> from this matrix with specified
     * <code>factory</code>.
     * 
     * @param matrix
     * @param factory
     */
    Matrix subtract(Matrix matrix, Factory factory);

    /**
     * Adds the <code>value</code> to this matrix.
     * 
     * @param value
     *            to be added
     * @return added matrix
     */
    Matrix add(double value);

    /**
     * Adds the <code>value</code> to this matrix with specified
     * <code>factory</code>
     * 
     * @param value
     * @param factory
     */
    Matrix add(double value, Factory factory);

    /**
     * Adds other <code>matrix</code> to this matrix.
     * 
     * @param matrix
     *            to be added
     * @return added matrix
     */
    Matrix add(Matrix matrix);

    /**
     * Adds other <code>matrix</code> to this matrix with specified
     * <code>factory</code>.
     * 
     * @param matrix
     * @param factory
     */
    Matrix add(Matrix matrix, Factory factory);

    /**
     * Divides this matrix by <code>value</code>.
     * 
     * @param value
     */
    Matrix divide(double value);

    /**
     * Divides this matrix by <code>value</code> with specified
     * <code>factory</code>.
     * 
     * @param value
     * @param factory
     */
    Matrix divide(double value, Factory factory);

    /**
     * Calculates the Kronecker product.
     * 
     * @param matrix
     * @return
     */
    Matrix kroneckerProduct(Matrix matrix);

    /**
     * Calculates the Kronecker product.
     * 
     * @param matrix
     * @return
     */
    Matrix kroneckerProduct(Matrix matrix, Factory factory);

    /**
     * Returns the "trace" of this matrix.
     * <p>
     * See <a href="http://mathworld.wolfram.com/MatrixTrace.html">
     * http://mathworld.wolfram.com/MatrixTrace.html</a> for more details.
     * </p>
     * 
     * @return the "trace" of this matrix
     */
    double trace();

    /**
     * Returns the product of diagonal elements of this matrix.
     * 
     * @return the product of diagonal elements of this matrix
     */
    double diagonalProduct();

    /**
     * Productizes up all elements of the matrix
     * 
     * @return the product of all elements of the matrix
     */
    double product();

    /**
     * Returns Hadamard product for two matrices
     * @param 
     *            matrix multiplier matrix
     * @return Hadamard product for two matrices
     */
    Matrix hadamardProduct(Matrix matrix);

    /**
     * Returns Hadamard product for two matrices
     * @param 
     *            matrix multiplier matrix
     * @param factory
     * @return Hadamard product for two matrices
     */
    Matrix hadamardProduct(Matrix matrix, Factory factory);

    /**
     * Summarizes up all elements of the matrix
     * 
     * @return the sum of all elements of the matrix
     */
    double sum();

    /**
     * Returns the "determinant" of this matrix.
     * <p>
     * See <a href="http://mathworld.wolfram.com/Determinant.html">
     * http://mathworld.wolfram.com/Determinant.html</a> for more details.
     * </p>
     * 
     * @return the "determinant" of this matrix
     */
    double determinant();

    /**
     * Returns the "rank" of this matrix.
     * <p>
     * See <a href="http://mathworld.wolfram.com/MatrixRank.html">
     * http://mathworld.wolfram.com/MatrixRank.html</a> for more details.
     * </p>
     * @return the "rank" of this matrix
     */
    int rank();

    /**
     * Gets the <code>i</code> row of this matrix.
     * 
     * @param i
     * @return the i-th row
     */
    Vector getRow(int i);

    /**
     * Gets the <code>i</code> row of this matrix.
     * 
     * @param i
     * @return the i-th row
     */
    Vector getRow(int i, Factory factory);

    /**
     * Gets the <code>i</code> column of this matrix.
     * 
     * @param j
     * @return the i-th column
     */
    Vector getColumn(int j);

    /**
     * Gets the <code>i</code> column of this matrix.
     * 
     * @param j
     * @return the i-th column
     */
    Vector getColumn(int j, Factory factory);

    /**
     * Sets the <code>i</code> row of this matrix.
     * 
     * @param i
     * @param row
     */
    void setRow(int i, Vector row);

    /**
     * Sets the <code>i</code> column of this matrix.
     * 
     * @param j
     * @param column
     */
    void setColumn(int j, Vector column);

    /**
     * Converts this matrix to triangle matrix.
     */
    Matrix triangularize();

    /**
     * Converts this matrix to triangle with <code>factory</code>.
     * 
     * @param factory
     */
    Matrix triangularize(Factory factory);

    /**
     * Gets blank matrix.
     * 
     * @return blanked matrix
     */
    Matrix blank();

    /**
     * Gets blank matrix with <code>factory</code>.
     * 
     * @param factory
     */
    Matrix blank(Factory factory);

    /**
     * Gets copy of this matrix.
     */
    Matrix copy();

    /**
     * Gets copy of this matrix with <code>factory</code>.
     * 
     * @param factory
     */
    Matrix copy(Factory factory);

    /**
     * Resizes this matrix to new size.
     * 
     * @param rows
     *            new rows size
     * @param columns
     *            new columns size
     */
    Matrix resize(int rows, int columns);

    /**
     * Resizes this matrix to new size.
     * 
     * @param rows
     *            new rows size
     * @param columns
     *            new columns size
     */
    Matrix resize(int rows, int columns, Factory factory);

    /**
     * Resizes this matrix to new rows size.
     * 
     * @param rows
     * @return
     */
    Matrix resizeRows(int rows);

    /**
     * Resizes this matrix to new rows size.
     * 
     * @param rows
     * @return
     */
    Matrix resizeRows(int rows, Factory factory);

    /**
     * Resizes this matrix to new columns size.
     * 
     * @param columns
     * @return
     */
    Matrix resizeColumns(int columns);

    /**
     * Resizes this matrix to new columns size.
     * 
     * @param columns
     * @return
     */
    Matrix resizeColumns(int columns, Factory factory);

    /**
     * Matrix that contains the same elements but with the elements shuffled
     * around (which might also result in the same matrix (with a small
     * likelihood)).
     * 
     * @return The shuffled matrix.
     */
    Matrix shuffle();

    /**
     * Matrix that contains the same elements but with the elements shuffled
     * around (which might also result in the same matrix (with a small
     * likelihood)).
     * 
     * @param factory
     *            The factory to use for this
     * @return The shuffled matrix.
     */
    Matrix shuffle(Factory factory);

    /**
     * Slices this matrix to ({@code fromRow:untilRow}, {@code fromColumn:untilColumn}).
     * 
     * @param fromRow
     * @return
     */
    Matrix slice(int fromRow, int fromColumn, int untilRow, int untilColumn);

    /**
     * Slices this matrix to ({@code fromRow:untilRow}, {@code fromColumn:untilColumn})
     * 
     * @param fromRow
     * @return
     */
    Matrix slice(int fromRow, int fromColumn, int untilRow, int untilColumn, Factory factory);

    /**
     * Slices this matrix.
     * 
     * @param untilRow
     * @param untilColumn
     * @return
     */
    Matrix sliceTopLeft(int untilRow, int untilColumn);

    /**
     * Slices this matrix.
     * 
     * @param untilRow
     * @param untilColumn
     * @return
     */
    Matrix sliceTopLeft(int untilRow, int untilColumn, Factory factory);

    /**
     * Slices this matrix.
     * 
     * @param fromRow
     * @param fromColumn
     * @return
     */
    Matrix sliceBottomRight(int fromRow, int fromColumn);

    /**
     * Slices this matrix.
     * 
     * @param fromRow
     * @param fromColumn
     * @return
     */
    Matrix sliceBottomRight(int fromRow, int fromColumn, Factory factory);

    /**
     * Returns a new matrix with the selected rows and columns. This method can
     * be used either return a specific subset of rows and/or columns or to
     * permute the indices in an arbitrary order. The list of indices are
     * allowed to contain duplicates indices. This is more general than slice()
     * which selects only contiguous blocks. However, where applicable slice()
     * is probably more efficient.
     * 
     * @param rowIndices
     *            list of row indexes, each index < rows()
     * @param columnIndices
     *            list of column indexes, each index < columns()
     * @return The new matrix with the selected rows and columns.
     * @throws IllegalArgumentException
     *             if invalid row or column indices are provided.
     */
    public Matrix select(int[] rowIndices, int[] columnIndices);

    /**
     * Returns a new matrix with the selected rows and columns.
     */
    public Matrix select(int[] rowIndices, int[] columnIndices, Factory factory);

    /**
     * Returns the factory associated with this matrix.
     * 
     * @return factory
     */
    Factory factory();

    /**
     * Applies the <code>procedure</code> to every element of this matrix.
     * @param procedure
     */
    void each(MatrixProcedure procedure);

    /**
     * Applies the <code>procedure</code> to every element in the
     * <code>i</code> row of this matrix.
     * @param procedure
     * @param i
     */
    void eachInRow(int i, MatrixProcedure procedure);

    /**
     * Applies the <code>procedure</code> to every element in the
     * <code>i</code> column of this matrix.
     * @param procedure
     * @param j
     */
    void eachInColumn(int j, MatrixProcedure procedure);

    /**
     * Applies the <code>procedure</code> to every non-zero element in the
     * <code>i</code> column of this matrix.
     * @param procedure
     */
    void eachNonZero(MatrixProcedure procedure);

    /**
     * Applies the <code>procedure</code> to every non-zero element in the
     * <code>i</code> row of this matrix.
     * @param procedure
     * @param i
     */
    void eachNonZeroInRow(int i, MatrixProcedure procedure);

    /**
     * Applies the <code>procedure</code> to every non-zero element in the
     * <code>i</code> column of this matrix.
     * @param procedure
     * @param j
     */
    void eachNonZeroInColumn(int j, MatrixProcedure procedure);

    /**
     * Finds max in whole matrix.
     *
     * @return max
     */
    double max();

    /**
     * Finds min in whole matrix.
     *
     * @return min
     */
    double min();

    /**
     * Finds max in <code>i</code> row.
     *
     * @return max
     */
    double maxInRow(int i);

    /**
     * Finds min in <code>i</code> row.
     *
     * @return min
     */
    double minInRow(int i);


    /**
     * Finds max in <code>j</code> column.
     *
     * @return max
     */
    double maxInColumn(int j);

    /**
     * Finds min in <code>j</code> column.
     *
     * @return min
     */
    double minInColumn(int j);

    /**
     * Builds a new matrix by applying a <code>function</code> to all elements
     * of this matrix.
     * 
     * @param function
     */
    Matrix transform(MatrixFunction function);

    /**
     * Builds a new matrix by applying a <code>function</code> to all elements
     * of this matrix with using of specified <code>factory</code>.
     * 
     * @param function
     */
    Matrix transform(MatrixFunction function, Factory factory);

    /**
     * Builds a new matrix by applying a <code>function</code> to (
     * <code>i</code>, <code>j</code>) element of this matrix.
     * 
     * @param i
     * @param j
     * @param function
     */
    Matrix transform(int i, int j, MatrixFunction function);

    /**
     * Builds a new matrix by applying a <code>function</code> to (
     * <code>i</code>, <code>j</code>) element of this matrix with using of
     * specified <code>factory</code>.
     * 
     * @param i
     * @param j
     * @param function
     * @param factory
     */
    Matrix transform(int i, int j, MatrixFunction function, Factory factory);

    /**
     * Updates all elements of this matrix by applying <code>function</code>.
     * 
     * @param function
     */
    void update(MatrixFunction function);

    /**
     * Updates (<code>i</code>, <code>j</code>) element of this matrix by
     * applying <code>function</code>.
     * 
     * @param i
     * @param j
     * @param function
     */
    void update(int i, int j, MatrixFunction function);

    /**
     * 
     * @param accumulator
     * @return
     */
    double fold(MatrixAccumulator accumulator);

    /**
     * 
     * @param i
     * @param accumulator
     * @return
     */
    double foldRow(int i, MatrixAccumulator accumulator);

    /**
     * 
     * @param j
     * @param accumulator
     * @return
     */
    double foldColumn(int j, MatrixAccumulator accumulator);

    /**
     * Checks whether this matrix compiles with <code>predicate</code>.
     * 
     * @param predicate
     * @return <code>true</code> if this matrix compiles with
     *         <code>predicate</code>.
     */
    boolean is(MatrixPredicate predicate);

    /**
     * Checks whether this matrix compiles with <code>predicate</code>.
     * 
     * @return <code>true</code> if this matrix compiles with
     *         <code>predicate</code>.
     */
    boolean is(AdvancedMatrixPredicate predicate);

    /**
     * Checks whether this matrix compiles with <code>predicate</code>.
     *
     * @param predicate
     * @return <code>true</code> if this matrix doesn't compiles with predicate.
     */
    boolean non(MatrixPredicate predicate);

    /**
     * Checks whether this matrix compiles with <code>predicate</code>.
     *
     * @return <code>true</code> if this matrix doesn't compiles with  predicate.
     */
    boolean non(AdvancedMatrixPredicate predicate);

    /**
     * Wraps this matrix with safe interface
     */
    Matrix safe();

    /**
     * Wraps this matrix with unsafe interface
     */
    Matrix unsafe();

    /**
     * Converts this matrix to row vector.
     *
     * @return
     */
    Vector toRowVector();

    /**
     * Converts this matrix to row vector.
     *
     * @return
     */
    Vector toRowVector(Factory factory);

    /**
     * Converts this matrix to column vector.
     *
     * @return
     */
    Vector toColumnVector();

    /**
     * Converts this matrix to column vector.
     *
     * @return
     */
    Vector toColumnVector(Factory factory);

    /**
     * Creates a new solver by given {@code factory} of this matrix.
     *
     * @param factory
     * @return
     */
    LinearSystemSolver withSolver(LinearAlgebra.SolverFactory factory);

    /**
     * Creates a new inverter by given {@code factory} of this matrix.
     *
     * @param factory
     * @return
     */
    MatrixInverter withInverter(LinearAlgebra.InverterFactory factory);

    /**
     * Creates a new decompositor by given {@code factory} of this matrix.
     *
     * @param factory
     * @return
     */
    MatrixDecompositor withDecompositor(LinearAlgebra.DecompositorFactory factory);
}
