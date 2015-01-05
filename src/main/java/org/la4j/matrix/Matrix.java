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

import org.la4j.LinearAlgebra;
import org.la4j.decomposition.MatrixDecompositor;
import org.la4j.inversion.MatrixInverter;
import org.la4j.iterator.ColumnMajorMatrixIterator;
import org.la4j.iterator.MatrixIterator;
import org.la4j.iterator.RowMajorMatrixIterator;
import org.la4j.iterator.VectorIterator;
import org.la4j.linear.LinearSystemSolver;
import org.la4j.matrix.dense.DenseMatrix;
import org.la4j.matrix.functor.*;
import org.la4j.matrix.sparse.ColumnMajorSparseMatrix;
import org.la4j.matrix.sparse.RowMajorSparseMatrix;
import org.la4j.matrix.sparse.SparseMatrix;
import org.la4j.operation.MatrixMatrixOperation;
import org.la4j.operation.MatrixOperation;
import org.la4j.operation.MatrixVectorOperation;
import org.la4j.vector.Vector;
import org.la4j.vector.functor.VectorAccumulator;
import org.la4j.vector.functor.VectorFunction;
import org.la4j.vector.functor.VectorProcedure;

import java.io.Externalizable;
import java.text.NumberFormat;

/**
 * This interface represents real matrix.
 * <p>
 * <a href="http://mathworld.wolfram.com/Matrix.html">
 * http://mathworld.wolfram.com/Matrix.html </a>
 * </p>
 * 
 */
public interface Matrix extends Externalizable, Iterable<Double> {

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
     * Sets all elements of this matrix to the given {@code value}.
     *
     * @param value the element's new value
     */
    void setAll(double value);

    /**
     * <p>
     * Sets all elements of the specified row of this matrix to given {@code value}.
     * </p>
     *
     * @param i the row index
     * @param value the element's new value
     */
    void setRow(int i, double value);

    /**
     * <p>
     * Sets all elements of the specified column of this matrix to given {@code value}.
     * </p>
     *
     * @param j the column index
     * @param value the element's new value
     */
    void setColumn(int j, double value);

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
     * Rotates this matrix by 90 degrees to the right.
     * 
     * @return the rotated matrix
     */
    Matrix rotate();

    /**
     * Powers this matrix of given exponent {code n}.
     *
     * @param n the exponent
     *
     * @return the powered matrix
     */
    Matrix power(int n);

    /**
     * Scales this matrix by given {@code value} (v).
     * 
     * @param value the scale factor
     *
     * @return A * v
     */
    Matrix multiply(double value);

    /**
     * Multiplies this matrix (A) by given {@code that} vector (x).
     *
     * @param that the vector
     *
     * @return A * x
     */
    Vector multiply(Vector that);

    /**
     * Multiplies this matrix (A) by given {@code that} matrix (B).
     * 
     * @param that the right hand matrix for multiplication
     *
     * @return A * B
     */
    Matrix multiply(Matrix that);

    /**
     * Multiplies this matrix by its transpose.
     *
     * @return this matrix multiplied by its transpose
     */
    Matrix multiplyByItsTranspose();

    /**
     * Subtracts given {@code value} (v) from every element of this matrix (A).
     *
     * @param value the right hand value for subtraction
     *
     * @return A - v
     */
    Matrix subtract(double value);

    /**
     * Subtracts given {@code that} matrix (B) from this matrix (A).
     *
     * @param that the right hand matrix for subtraction
     *
     * @return A - B
     */
    Matrix subtract(Matrix that);

    /**
     * Adds given {@code value} (v) to every element of this matrix (A).
     * 
     * @param value the right hand value for addition
     *
     * @return A + v
     */
    Matrix add(double value);

    /**
     * Adds given {@code that} matrix (B) to this matrix (A).
     * 
     * @param that the right hand matrix for addition
     *
     * @return A + B
     */
    Matrix add(Matrix that);

    /**
     * Inserts a given {@code matrix} (B) into this matrix (A). The original
     * values are overwritten by the new ones.
     * 
     * @param matrix the matrix to insert, from the first row and column
     * @return a matrix with the parameter inserted into it
     */
    Matrix insert(Matrix matrix);
    
    /**
     * Inserts a given {@code that} matrix (B) into this matrix (A). The original
     * values are overwritten by the new ones.
     * 
     * @param that the matrix to insert
     * @param numRows number of rows to insert
     * @param numColumns number of columns to insert
     * @return a matrix with the parameter inserted into it
     */
    Matrix insert(Matrix that, int numRows, int numColumns);

    /**
     * Inserts a given {@code that} matrix (B) into this matrix (A). The original
     * values are overwritten by the new ones.
     * 
     * @param that the matrix to insert
     * @param destRow the row to insert at in the destination matrix
     * @param destColumn the column to insert at in the destination matrix
     * @param numRows number of rows to insert
     * @param numColumns number of columns to insert
     * @return a matrix with the parameter inserted into it
     */
    Matrix insert(Matrix that, int destRow, int destColumn, int numRows, int numColumns);

    /**
     * Inserts a given {@code that} matrix (B) into this matrix (A). The original
     * values are overwritten by the new ones.
     * 
     * @param that the matrix to insert
     * @param srcRow the row to start at in the source matrix
     * @param srcColumn the column to start at in the source matrix
     * @param destRow the row to insert at in the destination matrix
     * @param destColumn the column to insert at in the destination matrix
     * @param numRows number of rows to insert
     * @param numCols number of columns to insert
     * @return a matrix with the parameter inserted into it
     */
    Matrix insert(Matrix that, int srcRow, int srcColumn, int destRow, int destColumn, int numRows, int numCols);

    /**
     * Divides every element of this matrix (A) by given {@code value} (v).
     * 
     * @param value the right hand value for division
     *
     * @return A / v
     */
    Matrix divide(double value);

    /**
     * Calculates the Kronecker product of this matrix (A) and given {@code that} matrix (B).
     * 
     * @param that the right hand matrix for Kronecker product
     *
     * @return A (+) B
     */
    Matrix kroneckerProduct(Matrix that);

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
     * Calculates the Hadamard (element-wise) product of this and given {@code that} matrix.
     *
     * @param that the right hand matrix for Hadamard product
     *
     * @return the Hadamard product of two matrices
     */
    Matrix hadamardProduct(Matrix that);

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
     * Copies the specified column of this matrix into the vector.
     *
     * @param j the column index
     *
     * @return the column represented as vector
     */
    Vector getColumn(int j);

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
     * Removes one row from matrix.
     * @param i the row index
     * @return matrix without row.
     */
    Matrix removeRow(int i);

    /**
     * Removes one column from matrix.
     * @param j
     * @return matrix without column.
     */
    Matrix removeColumn(int j);

    /**
     * Removes first row from matrix.
     * @return matrix without first row.
     */
    Matrix removeFirstRow();

    /**
     * Removes first column from matrix.
     * @return matrix without first column
     */
    Matrix removeFirstColumn();

    /**
     * Removes last row from matrix.
     * @return matrix without last row
     */
    Matrix removeLastRow();

    /**
     * Removes last column from matrix.
     * @return matrix without last column
     */
    Matrix removeLastColumn();

    /**
     * Creates the blank matrix (a zero matrix with same size) of this matrix.
     * 
     * @return blank matrix
     */
    Matrix blank();

    /**
     * Creates the blank matrix (a zero matrix with same size) of this matrix
     * of the given shape: {@code rows} x {@code columns}.
     *
     * @return blank matrix
     */
    Matrix blankOfShape(int rows, int columns);

    /**
     * Creates the blank matrix (a zero matrix with same size) of this matrix
     * of the given shape: {@code rows}. The {@code columns} number remains the
     * same.
     *
     * @return blank matrix
     */
    Matrix blankOfRows(int rows);

    /**
     * Creates the blank matrix (a zero matrix with same size) of this matrix
     * of the given shape: {@code columns}. The {@code rows} number remains the
     * same.
     *
     * @return blank matrix
     */
    Matrix blankOfColumns(int columns);

    /**
     * Copies this matrix.
     *
     * @return the copy of this matrix
     */
    Matrix copy();

    /**
     * Copies this matrix into the new matrix with specified dimensions: {@code rows} and {@code columns}.
     *
     * @param rows the number of rows in new matrix
     * @param columns the number of columns in new matrix
     *
     * @return the copy of this matrix with new size
     */
    Matrix copyOfShape(int rows, int columns);

    /**
     * Copies this matrix into the new matrix with specified row dimension: {@code rows}.
     *
     * @param rows the number of rows in new matrix
     *
     * @return the copy of this matrix with new size
     */
    Matrix copyOfRows(int rows);

    /**
     * Copies this matrix into the new matrix with specified column dimension: {@code columns}.
     *
     * @param columns the number of columns in new matrix
     *
     * @return the copy of this matrix with new size
     */
    Matrix copyOfColumns(int columns);

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
     * Applies given {@code procedure} to each element of this matrix.
     *
     * @param procedure the matrix procedure
     */
    void each(MatrixProcedure procedure);

    /**
     * Applies given {@code procedure} to each element of specified row of this matrix.
     *
     * @param i the row index
     * @param procedure the vector procedure
     */
    void eachInRow(int i, VectorProcedure procedure);

    /**
     * Applies given {@code procedure} to each element of specified column of this matrix.
     *
     * @param j the column index
     * @param procedure the vector procedure
     */
    void eachInColumn(int j, VectorProcedure procedure);

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
     * Builds a new matrix by applying given {@code function} to each element of specified
     * row in this matrix.
     *
     * @param i the row index
     * @param function the vector function
     *
     * @return the transformed matrix
     */
    Matrix transformRow(int i, VectorFunction function);

    /**
     * Builds a new matrix by applying given {@code function} to each element of specified
     * column in this matrix.
     *
     * @param j the column index
     * @param function the vector function
     *
     * @return the transformed matrix
     */
    Matrix transformColumn(int j, VectorFunction function);

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
    void updateAt(int i, int j, MatrixFunction function);

    /**
     * Updates all elements of the specified row in this matrix by applying given {@code function}.
     *
     * @param i the row index
     * @param function the vector function
     */
    void updateRow(int i, VectorFunction function);

    /**
     * Updates all elements of the specified column in this matrix by applying given {@code function}.
     *
     * @param j the column index
     * @param function the vector function
     */
    void updateColumn(int j, VectorFunction function);

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
     * @param accumulator the vector accumulator
     *
     * @return the accumulated value
     */
    double foldRow(int i, VectorAccumulator accumulator);

    /**
     * Folds all elements (in row-by-row manner) of this matrix with given {@code accumulator}.
     *
     * @param accumulator the vector accumulator
     *
     * @return the accumulated double array
     */
    double[] foldRows(VectorAccumulator accumulator);

    /**
     * Folds all elements of specified column in this matrix with given {@code accumulator}.
     *
     * @param j the column index
     * @param accumulator the vector accumulator
     *
     * @return the accumulated value
     */
    double foldColumn(int j, VectorAccumulator accumulator);

    /**
     * Folds all elements (in a column-by-column manner) of this matrix with given {@code accumulator}.
     *
     * @param accumulator the vector accumulator
     *
     * @return the accumulated double array
     */
    double[] foldColumns(VectorAccumulator accumulator);

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
     * Converts this matrix into the column vector.
     *
     * @return the column vector of this matrix
     */
    Vector toColumnVector();

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

    /**
     * Returns true when matrix is equal to given {@code matrix} with given {@code precision}
     *
     * @param matrix matrix
     * @param precision given precision
     *
     * @return equals of this matrix to that
     */
    boolean equals(Matrix matrix, double precision);

    /**
     * Converts this matrix into the string representation.
     *
     * @param formatter the number formatter
     *
     * @return the matrix converted to a string
     */
    String mkString(NumberFormat formatter);

    /**
     * Converts this matrix into the string representation.
     *
     * @param rowsDelimiter the rows' delimiter
     * @param columnsDelimiter the columns' delimiter
     *
     * @return the matrix converted to a string
     */
    String mkString(String rowsDelimiter, String columnsDelimiter);

    /**
     * Converts this matrix into the string representation.
     *
     * @param formatter the number formatter
     * @param rowsDelimiter the rows' delimiter
     * @param columnsDelimiter the columns' delimiter
     *
     * @return the matrix converted to a string
     */
    String mkString(NumberFormat formatter, String rowsDelimiter, String columnsDelimiter);

    /**
     * Returns a matrix iterator.
     *
     * @return a matrix iterator
     */
    @Override
    MatrixIterator iterator();

    /**
     * Returns a row-major matrix iterator.
     *
     * @return a row-major matrix iterator.
     */
    RowMajorMatrixIterator rowMajorIterator();

    /**
     * Returns a column-major matrix iterator.
     *
     * @return a column-major matrix iterator.
     */
    ColumnMajorMatrixIterator columnMajorIterator();

    /**
     * Returns a vector iterator of the given row {code i}.
     *
     * @return a vector iterator
     */
    VectorIterator iteratorOfRow(int i);

    /**
     * Returns a vector iterator of the given column {code j}.
     *
     * @return a vector iterator
     */
     VectorIterator iteratorOfColumn(int j);

    /**
     * Converts this matrix using the given {@code factory}.
     *
     * @param factory the factory that creates an output matrix
     * @param <T> type of the result matrix
     *
     * @return converted matrix
     */
    <T extends Matrix> T to(MatrixFactory<T> factory);

    /**
     * Converts this matrix into a sparse matrix.
     *
     * @return a sparse matrix
     */
    SparseMatrix toSparseMatrix();

    /**
     * Converts this matrix into a dense matrix.
     *
     * @return a dense matrix
     */
    DenseMatrix toDenseMatrix();

    /**
     * Converts this matrix into a row-major sparse matrix.
     *
     * @return a row-major sparse matrix
     */
    RowMajorSparseMatrix toRowMajorSparseMatrix();

    /**
     * Converts this matrix into a column-major sparse matrix.
     *
     * @return a row-major sparse matrix
     */
    ColumnMajorSparseMatrix toColumnMajorSparseMatrix();

    /**
     * Pipes this matrix to a given {@code operation}.
     *
     * @param operation the matrix operation
     *                  (an operation that takes a matrix and returns {@code T})
     * @param <T> the result type
     *
     * @return the result of an operation applied to this matrix
     */
    <T> T apply(MatrixOperation<T> operation);

    /**
     * Pipes this matrix to a given {@code operation}.
     *
     * @param operation the matrix-matrix operation
     *                  (an operation that takes two matrices and returns {@code T})
     * @param that the right hand matrix of the given operation
     * @param <T> the result type
     *
     * @return the result of an operation applied to this matrix
     */
    <T> T apply(MatrixMatrixOperation<T> operation, Matrix that);

    /**
     * Pipes this matrix to a given {@code operation}.
     *
     * @param operation the matrix-vector operation
     *                  (an operation that takes matrix and vector and returns {@code T})
     * @param that the right hand vector of the given operation
     * @param <T> the result type
     *
     * @return the result of an operation applied to this matrix
     */
    <T> T apply(MatrixVectorOperation<T> operation, Vector that);
}
