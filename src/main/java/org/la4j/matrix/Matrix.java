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
 */

package org.la4j.matrix;

import java.io.Externalizable;

import org.la4j.decomposition.MatrixDecompositor;
import org.la4j.factory.Factory;
import org.la4j.inversion.MatrixInvertor;
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
    Matrix div(double value);

    /**
     * Divides this matrix by <code>value</code> with specified
     * <code>factory</code>.
     * 
     * @param value
     * @param factory
     */
    Matrix div(double value, Factory factory);

    /**
     * Calculates the Kronecker product.
     * 
     * @param matrix
     * @return
     */
    Matrix kronecker(Matrix matrix);

    /**
     * Calculates the Kronecker product.
     * 
     * @param matrix
     * @return
     */
    Matrix kronecker(Matrix matrix, Factory factory);

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
    double product();

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
     * @param h
     * @return the i-th column
     */
    Vector getColumn(int j);

    /**
     * Gets the <code>i</code> column of this matrix.
     * 
     * @param h
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
     * Decomposes this matrix.
     * 
     * @param decompositor
     */
    Matrix[] decompose(MatrixDecompositor decompositor);

    /**
	 * Decomposes this matrix with <code>decompositor</code> and
	 * <code>factory</code>.
	 * 
	 * @param decompositor
	 * @param factory
	 */
    Matrix[] decompose(MatrixDecompositor decompositor, Factory factory);

    /**
     * Inverts this matrix.
     * 
     * @param invertor
     */
    Matrix inverse(MatrixInvertor invertor);

    /**
     * Inverts this matrix.
     * 
     * @param invertor
     * @param factory
     */
    Matrix inverse(MatrixInvertor invertor, Factory factory);

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
     * 
     * 
     * @param fromRow
     * @return
     */
    Matrix slice(int fromRow, int fromColumn, int untilRow, int untilColumn);

    /**
     * 
     * 
     * @param fromRow
     * @return
     */
    Matrix slice(int fromRow, int fromColumn, int untilRow, int untilColumn, 
                 Factory factory);

    /**
     * 
     * @param untilRow
     * @param untilColumn
     * @return
     */
    Matrix sliceTopLeft(int untilRow, int untilColumn);

    /**
     * 
     * @param untilRow
     * @param untilColumn
     * @return
     */
    Matrix sliceTopLeft(int untilRow, int untilColumn, Factory factory);

    /**
     * 
     * @param fromRow
     * @param fromColumn
     * @return
     */
    Matrix sliceBottomRight(int fromRow, int fromColumn);

    /**
     * 
     * @param fromRow
     * @param fromColumn
     * @return
     */
    Matrix sliceBottomRight(int fromRow, int fromColumn, Factory factory);

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
     * @param predidate
     * @return <code>true</code> if this matrix compiles with
     *         <code>predicate</code>.
     */
    boolean is(MatrixPredicate predidate);

    /**
     * Wraps this matrix with safe interface
     */
    Matrix safe();

    /**
     * Wraps this matrix with unsafe interface
     */
    Matrix unsafe();
}
