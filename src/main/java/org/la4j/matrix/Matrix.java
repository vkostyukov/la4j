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
 *                 Todd Brunhoff
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
     * Gets the specified element of this matrix.
     *
     * @param i element's row index
     * @param j element's column index
     *
     * @return the element of this matrix
     */
    double get(int i, int j);

    /**
     * Sets the specified element of this matrix to given {@code value}.
     * 
     * @param i element's row index
     * @param j element's column index
     * @param value element's new value
     */
    void set(int i, int j, double value);

    /**
     * Assigns all elements of this matrix to given {@code value}.
     * 
     * @param value the elements' new value
     */
    void assign(double value);

    /**
     * Assigns all elements of the specified row of this matrix to given {@code value}.
     *
     * @param i the row index
     * @param value the elements' new value
     */
    void assignRow(int i, double value);

    /**
     * Assigns all elements of the specified column of this matrix to given {@code value}.
     *
     * @param j the column index
     * @param value the elements' new value
     */
    void assignColumn(int j, double value);

    /**
     * Swaps the specified rows of this matrix.
     * 
     * @param i the row index
     * @param j the row index
     */
    void swapRows(int i, int j);

    /**
     * Swaps the specified columns of this matrix.
     * 
     * @param i the column index
     * @param j the column index
     */
    void swapColumns(int i, int j);

    /**
     * Returns the number of rows of this matrix.
     * 
     * @return the number of rows
     */
    int rows();

    /**
     * Returns the number of columns of this matrix.
     * 
     * @return the number of columns
     */
    int columns();

    /**
     * Transposes this matrix.
     * 
     * @return the transposed matrix
     */
    Matrix transpose();

    /**
     * Transposes this matrix.
     * 
     * @param factory the factory of result matrix
     *
     * @return the transposed matrix
     */
    Matrix transpose(Factory factory);

    /**
     * Rotates this matrix by 90 degrees to the right.
     * 
     * @return the rotated matrix
     */
    Matrix rotate();

    /**
     * Rotates this matrix by 90 degrees to the right.
     *
     * @param factory the factory of result matrix
     *
     * @return the rotated matrix
     */
    Matrix rotate(Factory factory);

    /**
     * Powers this matrix of given exponent {code n}.
     *
     * @param n the exponent
     *
     * @return the powered matrix
     */
    Matrix power(int n);

    /**
     * Powers this matrix of given exponent {code n}.
     *
     * @param n the exponent
     * @param factory the factory of result matrix
     *
     * @return the powered matrix
     */
    Matrix power(int n, Factory factory);

    /**
     * Scales this matrix by given {@code value} (v).
     * 
     * @param value the scale factor
     *
     * @return A * v
     */
    Matrix multiply(double value);

    /**
     * Scales this matrix by given {@code value} (v).
     *
     * @param value the scale factor
     * @param factory the factory of result matrix
     *
     * @return A * v
     */
    Matrix multiply(double value, Factory factory);

    /**
     * Multiplies this matrix (A) by given {@code vector} (x).
     * 
     * @param vector the vector
     *
     * @return A * x
     */
    Vector multiply(Vector vector);

    /**
     * Multiplies this matrix (A) by given {@code vector} (x).
     *
     * @param vector the right hand vector for multiplication
     * @param factory the factory of result matrix
     *
     * @return A * x
     */
    Vector multiply(Vector vector, Factory factory);

    /**
     * Multiplies this matrix (A) by given {@code matrix} (B).
     * 
     * @param matrix the right hand matrix for multiplication
     *
     * @return A * B
     */
    Matrix multiply(Matrix matrix);

    /**
     * Multiplies this matrix (A) by given {@code matrix} (B).
     *
     * @param matrix the right hand matrix for multiplication
     * @param factory the factory of result matrix
     *
     * @return A * B
     */
    Matrix multiply(Matrix matrix, Factory factory);

    /**
     * Subtracts given {@code value} (v) from every element of this matrix (A).
     *
     * @param value the right hand value for subtraction
     *
     * @return A - v
     */
    Matrix subtract(double value);

    /**
     * Subtracts given {@code value} (v) from every element of this matrix (A).
     *
     * @param value the right hand value for subtraction
     * @param factory the factory of result matrix
     *
     * @return A - v
     */
    Matrix subtract(double value, Factory factory);

    /**
     * Subtracts given {@code matrix} (B) from this matrix (A).
     *
     * @param matrix the right hand matrix for subtraction
     *
     * @return A - B
     */
    Matrix subtract(Matrix matrix);

    /**
     * Subtracts given {@code matrix} (B) from this matrix (A).
     *
     * @param matrix the right hand matrix for subtraction
     * @param factory the factory of result matrix
     *
     * @return A - B
     */
    Matrix subtract(Matrix matrix, Factory factory);

    /**
     * Adds given {@code value} (v) to every element of this matrix (A).
     * 
     * @param value the right hand value for addition
     *
     * @return A + v
     */
    Matrix add(double value);

    /**
     * Adds given {@code value} (v) to every element of this matrix (A).
     *
     * @param value the right hand value for addition
     * @param factory the factory of result matrix
     *
     * @return A + v
     */
    Matrix add(double value, Factory factory);

    /**
     * Adds given {@code matrix} (B) to this matrix (A).
     * 
     * @param matrix the right hand matrix for addition
     *
     * @return A + B
     */
    Matrix add(Matrix matrix);

    /**
     * Adds given {@code matrix} (B) to this matrix (A).
     *
     * @param matrix the right hand matrix for addition
     * @param factory the factory of result matrix
     *
     * @return A + B
     */
    Matrix add(Matrix matrix, Factory factory);

    /**
     * Divides every element of this matrix (A) by given {@code value} (v).
     * 
     * @param value the right hand value for division
     *
     * @return A / v
     */
    Matrix divide(double value);

    /**
     * Divides every element of this matrix (A) by given {@code value} (v).
     *
     * @param value the right hand value for division
     * @param factory the factory of result matrix
     *
     * @return A / v
     */
    Matrix divide(double value, Factory factory);

    /**
     * Calculates the Kronecker product of this matrix (A) and given {@code matrix} (B).
     * 
     * @param matrix the right hand matrix for Kronecker product
     *
     * @return A (+) B
     */
    Matrix kroneckerProduct(Matrix matrix);

    /**
     * Calculates the Kronecker product of this matrix (A) and given {@code matrix} (B).
     *
     * @param matrix the right hand matrix for Kronecker product
     * @param factory the factory of result matrix
     *
     * @return A (+) B
     */
    Matrix kroneckerProduct(Matrix matrix, Factory factory);

    /**
     * Calculates the trace of this matrix.
     *
     * <p>
     * See <a href="http://mathworld.wolfram.com/MatrixTrace.html">
     * http://mathworld.wolfram.com/MatrixTrace.html</a> for more details.
     * </p>
     * 
     * @return the trace of this matrix
     */
    double trace();

    /**
     * Calculates the product of diagonal elements of this matrix.
     * 
     * @return the product of diagonal elements of this matrix
     */
    double diagonalProduct();

    /**
     * Multiplies up all elements of this matrix.
     * 
     * @return the product of all elements of this matrix
     */
    double product();

    /**
     * Summarizes up all elements of this matrix.
     *
     * @return the sum of all elements of this matrix
     */
    double sum();

    /**
     * Calculates the Hadamard (element-wise) product of this and given {@code matrix}.
     *
     * @param matrix the right hand matrix for Hadamard product
     *
     * @return the Hadamard product of two matrices
     */
    Matrix hadamardProduct(Matrix matrix);

    /**
     * Calculates the Hadamard (element-wise) product of this and given {@code matrix}.
     *
     * @param matrix the right hand matrix for Hadamard product
     * @param factory the factory of result matrix
     *
     * @return the Hadamard product of two matrices
     */
    Matrix hadamardProduct(Matrix matrix, Factory factory);

    /**
     * Calculates the determinant of this matrix.
     *
     * <p>
     * See <a href="http://mathworld.wolfram.com/Determinant.html">
     * http://mathworld.wolfram.com/Determinant.html</a> for more details.
     * </p>
     * 
     * @return the determinant of this matrix
     */
    double determinant();

    /**
     * Calculates the rank of this matrix.
     *
     * <p>
     * See <a href="http://mathworld.wolfram.com/MatrixRank.html">
     * http://mathworld.wolfram.com/MatrixRank.html</a> for more details.
     * </p>
     *
     * @return the rank of this matrix
     */
    int rank();

    /**
     * Copies the specified row of this matrix into the vector.
     *
     * @param i the row index
     *
     * @return the row represented as vector
     */
    Vector getRow(int i);

    /**
     * Copies the specified row of this matrix into the vector.
     *
     * @param i the row index
     * @param factory the factory of result vector
     *
     * @return the row represented as vector
     */
    Vector getRow(int i, Factory factory);

    /**
     * Copies the specified column of this matrix into the vector.
     *
     * @param j the column index
     *
     * @return the column represented as vector
     */
    Vector getColumn(int j);

    /**
     * Copies the specified column of this matrix into the vector.
     *
     * @param j the column index
     * @param factory the factory of result vector
     *
     * @return the column represented as vector
     */
    Vector getColumn(int j, Factory factory);

    /**
     * Copies given {@code vector} into the specified row of this matrix.
     *
     * @param i the row index
     * @param vector the row represented as vector
     */
    void setRow(int i, Vector vector);

    /**
     * Copies given {@code vector} into the specified column of this matrix.
     *
     * @param j the column index
     * @param vector the column represented as vector
     */
    void setColumn(int j, Vector vector);

    /**
     * Converts this matrix into the triangle matrix.
     *
     * @return tirangularized matrix
     */
    @Deprecated
    Matrix triangularize();

    /**
     * Converts this matrix into the triangle matrix.
     *
     * @param factory the factory of result matrix
     *
     * @return tirangularized matrix
     */
    @Deprecated
    Matrix triangularize(Factory factory);

    /**
     * Creates the blank (an empty matrix with same size) matrix of this matrix.
     * 
     * @return blank matrix
     */
    Matrix blank();

    /**
     * Creates the blank (an empty matrix with same size) matrix of this matrix.
     *
     * @param factory the factory of result matrix
     *
     * @return blank matrix
     */
    Matrix blank(Factory factory);

    /**
     * Copies this matrix.
     *
     * @return the copy of this matrix
     */
    Matrix copy();

    /**
     * Copies this matrix.
     *
     * @param factory the factory of result matrix
     *
     * @return the copy of this matrix
     */
    Matrix copy(Factory factory);

    /**
     * Copies this matrix into the new matrix with specified dimensions: {@code rows} and {@code columns}.
     *
     * @param rows the number of rows in new matrix
     * @param columns the number of columns in new matrix
     *
     * @return the copy of this matrix with new size
     */
    Matrix resize(int rows, int columns);

    /**
     * Copies this matrix into the new matrix with specified dimensions: {@code rows} and {@code columns}.
     *
     * @param rows the number of rows in new matrix
     * @param columns the number of columns in new matrix
     * @param factory the factory of result matrix
     *
     * @return the copy of this matrix with new size
     */
    Matrix resize(int rows, int columns, Factory factory);

    /**
     * Copies this matrix into the new matrix with specified row dimension: {@code rows}.
     *
     * @param rows the number of rows in new matrix
     *
     * @return the copy of this matrix with new size
     */
    Matrix resizeRows(int rows);

    /**
     * Copies this matrix into the new matrix with specified row dimension: {@code rows}.
     *
     * @param rows the number of rows in new matrix
     * @param factory the factory of result matrix
     *
     * @return the copy of this matrix with new size
     */
    Matrix resizeRows(int rows, Factory factory);

    /**
     * Copies this matrix into the new matrix with specified column dimension: {@code columns}.
     *
     * @param columns the number of columns in new matrix
     *
     * @return the copy of this matrix with new size
     */
    Matrix resizeColumns(int columns);

    /**
     * Copies this matrix into the new matrix with specified column dimension: {@code columns}.
     *
     * @param columns the number of columns in new matrix
     * @param factory the factory of result matrix
     *
     * @return the copy of this matrix with new size
     */
    Matrix resizeColumns(int columns, Factory factory);

    /**
     * Shuffles this matrix.
     *
     * <p>
     * Copies this matrix into the matrix that contains the same elements but with the elements
     * shuffled around (which might also result in the same matrix (with a small likelihood)).
     * </p>
     *
     * @return the shuffled matrix
     */
    Matrix shuffle();

    /**
     * Shuffles this matrix.
     *
     * <p>
     * Copies this matrix into the matrix that contains the same elements but with the elements
     * shuffled around (which might also result in the same matrix (with a small likelihood)).
     * </p>
     *
     * @param factory the factory of result matrix
     *
     * @return the shuffled matrix
     */
    Matrix shuffle(Factory factory);

    /**
     * Retrieves the specified sub-matrix of this matrix. The sub-matrix is specified by
     * intervals for row indices and column indices.
     *
     * @param fromRow the beginning of the row indices interval
     * @param fromColumn the beginning of the column indices interval
     * @param untilRow the ending of the row indices interval
     * @param untilColumn the ending of the column indices interval
     *
     * @return the sub-matrix of this matrix
     */
    Matrix slice(int fromRow, int fromColumn, int untilRow, int untilColumn);

    /**
     * Retrieves the specified sub-matrix of this matrix. The sub-matrix is specified by
     * intervals for row indices and column indices.
     *
     * @param fromRow the beginning of the row indices interval
     * @param fromColumn the beginning of the column indices interval
     * @param untilRow the ending of the row indices interval
     * @param untilColumn the ending of the column indices interval
     * @param factory the factory of result matrix
     *
     * @return the sub-matrix of this matrix
     */
    Matrix slice(int fromRow, int fromColumn, int untilRow, int untilColumn, Factory factory);

    /**
     * Retrieves the specified sub-matrix of this matrix. The sub-matrix is specified by
     * intervals for row indices and column indices. The top left points of both intervals
     * are fixed to zero.
     *
     * @param untilRow the ending of the row indices interval
     * @param untilColumn the ending of the column indices interval
     *
     * @return the sub-matrix of this matrix
     */
    Matrix sliceTopLeft(int untilRow, int untilColumn);

    /**
     * Retrieves the specified sub-matrix of this matrix. The sub-matrix is specified by
     * intervals for row indices and column indices. The top left points of both intervals
     * are fixed to zero.
     *
     * @param untilRow the ending of the row indices interval
     * @param untilColumn the ending of the column indices interval
     * @param factory the factory of result matrix
     *
     * @return the sub-matrix of this matrix
     */
    Matrix sliceTopLeft(int untilRow, int untilColumn, Factory factory);

    /**
     * Retrieves the specified sub-matrix of this matrix. The sub-matrix is specified by
     * intervals for row indices and column indices. The bottom right points of both intervals
     * are fixed to matrix dimensions - it's rows and columns correspondingly.
     *
     * @param fromRow the beginning of the row indices interval
     * @param fromColumn the beginning of the column indices interval
     *
     * @return the sub-matrix of this matrix
     */
    Matrix sliceBottomRight(int fromRow, int fromColumn);

    /**
     * Retrieves the specified sub-matrix of this matrix. The sub-matrix is specified by
     * intervals for row indices and column indices. The bottom right points of both intervals
     * are fixed to matrix dimensions - it's rows and columns correspondingly.
     *
     * @param fromRow the beginning of the row indices interval
     * @param fromColumn the beginning of the column indices interval
     * @param factory the factory of result matrix
     *
     * @return the sub-matrix of this matrix
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
     * @param rowIndices the array of row indices
     * @param columnIndices the array of column indices
     *
     * @return the new matrix with the selected rows and columns
     *
     * @throws IllegalArgumentException if invalid row or column indices are provided
     */
    public Matrix select(int[] rowIndices, int[] columnIndices);

    /**
     * Returns a new matrix with the selected rows and columns. This method can
     * be used either return a specific subset of rows and/or columns or to
     * permute the indices in an arbitrary order. The list of indices are
     * allowed to contain duplicates indices. This is more general than slice()
     * which selects only contiguous blocks. However, where applicable slice()
     * is probably more efficient.
     *
     * @param rowIndices the array of row indices
     * @param columnIndices the array of column indices
     * @param factory the factory of result matrix
     *
     * @return the new matrix with the selected rows and columns
     *
     * @throws IllegalArgumentException if invalid row or column indices are provided
     */
    public Matrix select(int[] rowIndices, int[] columnIndices, Factory factory);

    /**
     * Returns the factory of this matrix.
     * 
     * @return the factory of this matrix
     */
    Factory factory();

    /**
     * Applies given {@code procedure} to each element of this matrix.
     *
     * @param procedure the matrix procedure
     */
    void each(MatrixProcedure procedure);

    /**
     * Applies given {@code procedure} to each element of specified row of this matrix.
     *
     * @param i the row index
     * @param procedure the matrix procedure
     */
    void eachInRow(int i, MatrixProcedure procedure);

    /**
     * Applies given {@code procedure} to each element of specified column of this matrix.
     *
     * @param j the column index
     * @param procedure the matrix procedure
     */
    void eachInColumn(int j, MatrixProcedure procedure);

    /**
     * Applies given {@code procedure} to each non-zero element of this matrix.
     *
     * @param procedure the matrix procedure
     */
    void eachNonZero(MatrixProcedure procedure);

    /**
     * Applies given {@code procedure} to each non-zero element of specified row of this matrix.
     *
     * @param i the row index
     * @param procedure the matrix procedure
     */
    void eachNonZeroInRow(int i, MatrixProcedure procedure);

    /**
     * Applies given {@code procedure} to each non-zero element of specified column of this matrix.
     *
     * @param j the column index
     * @param procedure the matrix procedure
     */
    void eachNonZeroInColumn(int j, MatrixProcedure procedure);

    /**
     * Searches for the maximum value of the elements of this matrix.
     *
     * @return maximum value of this matrix
     */
    double max();

    /**
     * Searches for the minimum value of the elements of this matrix.
     *
     * @return minimum value of this matrix
     */
    double min();

    /**
     * Searches for the maximum value of specified row in this matrix.
     *
     * @param i the row index
     *
     * @return maximum value of specified row in this matrix
     */
    double maxInRow(int i);

    /**
     * Searches for the minimum value of specified row in this matrix.
     *
     * @param i the row index
     *
     * @return minimum value of specified row in this matrix
     */
    double minInRow(int i);

    /**
     * Searches for the maximum value of specified column in this matrix.
     *
     * @param j the column index
     *
     * @return maximum value of specified column in this matrix
     */
    double maxInColumn(int j);

    /**
     * Searches for the minimum value of specified column in this matrix.
     *
     * @param j the column index
     *
     * @return minimum value of specified column in this matrix
     */
    double minInColumn(int j);

    /**
     * Builds a new matrix by applying given {@code function} to each element of this matrix.
     *
     * @param function the matrix function
     *
     * @return the transformed matrix
     */
    Matrix transform(MatrixFunction function);

    /**
     * Builds a new matrix by applying given {@code function} to each element of this matrix.
     *
     * @param function the matrix function
     * @param factory the factory of result matrix
     *
     * @return the transformed matrix
     */
    Matrix transform(MatrixFunction function, Factory factory);

    /**
     * Builds a new matrix by applying given {@code function} to specified element of this matrix.
     *
     * @param i the row index
     * @param j the column index
     * @param function the matrix function
     *
     * @return the transformed matrix
     */
    Matrix transform(int i, int j, MatrixFunction function);

    /**
     * Builds a new matrix by applying given {@code function} to specified element of this matrix.
     *
     * @param i the row index
     * @param j the column index
     * @param function the matrix function
     * @param factory the factory of result matrix
     *
     * @return the transformed matrix
     */
    Matrix transform(int i, int j, MatrixFunction function, Factory factory);

    /**
     * Builds a new matrix by applying given {@code function} to each element of specified
     * row in this matrix.
     *
     * @param i the row index
     * @param function the matrix function
     *
     * @return the transformed matrix
     */
    Matrix transformRow(int i, MatrixFunction function);

    /**
     * Builds a new matrix by applying given {@code function} to each element of specified
     * row in this matrix.
     *
     * @param i the row index
     * @param function the matrix function
     * @param factory the factory of result matrix
     *
     * @return the transformed matrix
     */
    Matrix transformRow(int i, MatrixFunction function, Factory factory);

    /**
     * Builds a new matrix by applying given {@code function} to each element of specified
     * column in this matrix.
     *
     * @param j the column index
     * @param function the matrix function
     *
     * @return the transformed matrix
     */
    Matrix transformColumn(int j, MatrixFunction function);

    /**
     * Builds a new matrix by applying given {@code function} to each element of specified
     * column in this matrix.
     *
     * @param j the column index
     * @param function the matrix function
     * @param factory the factory of result matrix
     *
     * @return the transformed matrix
     */
    Matrix transformColumn(int j, MatrixFunction function, Factory factory);

    /**
     * Updates all elements of this matrix by applying given {@code function}.
     * 
     * @param function the matrix function
     */
    void update(MatrixFunction function);

    /**
     * Updates the specified element of this matrix by applying given {@code function}.
     *
     * @param i the row index
     * @param j the column index
     * @param function the matrix function
     */
    void update(int i, int j, MatrixFunction function);

    /**
     * Updates all elements of the specified row in this matrix by applying given {@code function}.
     *
     * @param i the row index
     * @param function the matrix function
     */
    void updateRow(int i, MatrixFunction function);

    /**
     * Updates all elements of the specified column in this matrix by applying given {@code function}.
     *
     * @param j the column index
     * @param function the matrix function
     */
    void updateColumn(int j, MatrixFunction function);

    /**
     * Folds all elements of this matrix with given {@code accumulator}.
     * 
     * @param accumulator the matrix accumulator
     *
     * @return the accumulated value
     */
    double fold(MatrixAccumulator accumulator);

    /**
     * Folds all elements of specified row in this matrix with given {@code accumulator}.
     *
     * @param i the row index
     * @param accumulator the matrix accumulator
     *
     * @return the accumulated value
     */
    double foldRow(int i, MatrixAccumulator accumulator);

    /**
     * Folds all elements (in row-by-row manner) of this matrix with given {@code accumulator}.
     *
     * @param accumulator the matrix accumulator
     *
     * @return the accumulated vector
     */
    Vector foldRows(MatrixAccumulator accumulator);

    /**
     * Folds all elements of specified column in this matrix with given {@code accumulator}.
     *
     * @param j the column index
     * @param accumulator the matrix accumulator
     *
     * @return the accumulated value
     */
    double foldColumn(int j, MatrixAccumulator accumulator);

    /**
     * Folds all elements (in column-by-column manner) of this matrix with given {@code accumulator}.
     *
     * @param accumulator the matrix accumulator
     *
     * @return the accumulated vector
     */
    Vector foldColumns(MatrixAccumulator accumulator);

    /**
     * Checks whether this matrix compiles with given {@code predicate} or not.
     * 
     * @param predicate the matrix predicate
     *
     * @return whether this matrix compiles with predicate
     */
    boolean is(MatrixPredicate predicate);

    /**
     * Checks whether this matrix compiles with given {@code predicate} or not.
     *
     * @param predicate the advanced matrix predicate
     *
     * @return whether this matrix compiles with predicate
     */
    boolean is(AdvancedMatrixPredicate predicate);

    /**
     * Checks whether this matrix compiles with given {@code predicate} or not.
     *
     * @param predicate the matrix predicate
     *
     * @return whether this matrix compiles with predicate
     */
    boolean non(MatrixPredicate predicate);

    /**
     * Checks whether this matrix compiles with given {@code predicate} or not.
     *
     * @param predicate the advanced matrix predicate
     *
     * @return whether this matrix compiles with predicate
     */
    boolean non(AdvancedMatrixPredicate predicate);

    /**
     * Converts this matrix into the row vector.
     *
     * @return the row vector of this matrix
     */
    Vector toRowVector();

    /**
     * Converts this matrix into the row vector.
     *
     * @param factory the factory of result vector
     *
     * @return the row vector of this matrix
     */
    Vector toRowVector(Factory factory);

    /**
     * Converts this matrix into the column vector.
     *
     * @return the column vector of this matrix
     */
    Vector toColumnVector();

    /**
     * Converts this matrix into the column vector.
     *
     * @param factory the factory of result vector
     *
     * @return the column vector of this matrix
     */
    Vector toColumnVector(Factory factory);

    /**
     * Creates a new solver by given {@code factory} of this matrix.
     *
     * @param factory the solver factory
     *
     * @return the linear system solver of this matrix
     */
    LinearSystemSolver withSolver(LinearAlgebra.SolverFactory factory);

    /**
     * Creates a new inverter by given {@code factory} of this matrix.
     *
     * @param factory the inverter factory
     *
     * @return the inverter of this matrix
     */
    MatrixInverter withInverter(LinearAlgebra.InverterFactory factory);

    /**
     * Creates a new decompositor by given {@code factory} of this matrix.
     *
     * @param factory the decompositor factory
     *
     * @return the decompositor of this matrix
     */
    MatrixDecompositor withDecompositor(LinearAlgebra.DecompositorFactory factory);
}
