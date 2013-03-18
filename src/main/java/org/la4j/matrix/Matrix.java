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
 * 
 */

package org.la4j.matrix;

import java.io.Externalizable;

import org.la4j.decomposition.MatrixDecompositor;
import org.la4j.factory.Factory;
import org.la4j.inversion.MatrixInvertor;
import org.la4j.matrix.functor.MatrixFunction;
import org.la4j.matrix.functor.MatrixPredicate;
import org.la4j.matrix.functor.MatrixProcedure;
import org.la4j.vector.Vector;

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
     * Sets the (<code>i, j</code>) element of this matrix to <code>value</code>.
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
     * Resizes this matrix to new size.
     * 
     * @param rows
     *            new rows size
     * @param columns
     *            new columns size
     */
    void resize(int rows, int columns);

    /**
     * Swaps i and j rows of this matrix.
     * 
     * @param i
     *            row
     * @param j
     *            row to be replaced!!!
     */
    void swapRows(int i, int j);

    /**
     * Swap i and i columns of matrix;
     * 
     * @param i
     *            column
     * @param j
     *            column
     */
    void swapColumns(int i, int j);

    /**
     * Get rows number of matrix;
     * 
     * @return rows number
     */
    int rows();

    /**
     * Get columns number of matrix;
     * 
     * @return columns number
     */
    int columns();

    /**
     * Transpose the matrix;
     * 
     * @return transposed matrix
     */
    Matrix transpose();

    /**
     * Transpose the matrix with specified factory;
     * 
     * @param factory
     * @return
     */
    Matrix transpose(Factory factory);

    /**
     * Scale matrix;
     * 
     * @param value
     *            matrix to be scaled
     * @return scaled matrix
     */
    Matrix multiply(double value);

    /**
     * Multiply matrix to value with factory;
     * 
     * @param value
     * @param factory
     * @return
     */
    Matrix multiply(double value, Factory factory);

    /**
     * Multiply matrix by vector;
     * 
     * @param vector
     *            to be multiplied
     * @return multiplied matrix
     */
    Vector multiply(Vector vector);

    /**
     * Multiply matrix by vector with specified factory;
     * 
     * @param vector
     *            to be multiplied
     * @param factory
     * @return multiplied matrix
     */
    Vector multiply(Vector vector, Factory factory);

    /**
     * Multiply matrix by matrix;
     * 
     * @param matrix
     *            to be multiplied
     * @return multiplied matrix
     */
    Matrix multiply(Matrix matrix);

    /**
     * Multiply matrix to matrix with specified factory;
     * 
     * @param matrix
     * @param factory
     * @return
     * @throws MatrixException
     */
    Matrix multiply(Matrix matrix, Factory factory);

    /**
     * Subtract value from matrix;
     * 
     * @param value
     *            to be subtracted
     * @return subtracted matrix
     */
    Matrix subtract(double value);

    /**
     * Subtract value from matrix with specified factory;
     * 
     * @param value
     * @param factory
     * @return
     */
    Matrix subtract(double value, Factory factory);

    /**
     * Subtract matrix by matrix;
     * 
     * @param matrix
     *            to be subtracted
     * @return subtracted matrix
     */
    Matrix subtract(Matrix matrix);

    /**
     * Subtract matrix from matrix with specified factory;
     * 
     * @param matrix
     * @param factory
     * @return
     */
    Matrix subtract(Matrix matrix, Factory factory);

    /**
     * Add value to matrix;
     * 
     * @param value
     *            to be added
     * @return added matrix
     */
    Matrix add(double value);

    /**
     * Add value to matrix with specified factory;
     * 
     * @param value
     * @param factory
     * @return
     */
    Matrix add(double value, Factory factory);

    /**
     * Add matrix by matrix;
     * 
     * @param matrix
     *            to be added
     * @return added matrix
     */
    Matrix add(Matrix matrix);

    /**
     * Add matrix to matrix with specified factory;
     * 
     * @param matrix
     * @param factory
     * @return
     * @throws MatrixException
     */
    Matrix add(Matrix matrix, Factory factory);

    /**
     * Dived matrix to value;
     * 
     * @param value
     * @return
     */
    Matrix div(double value);

    /**
     * Dived matrix to value with specified factory;
     * 
     * @param value
     * @param factory
     * @return
     */
    Matrix div(double value, Factory factory);

    /**
     * Returns the "trace" of this matrix.
     * 
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
     * 
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
     * @ return the "rank" of this matrix
     */
    int rank();

    /**
     * Get the i-th row of matrix;
     * 
     * @param i
     *            row
     * @return the i-th row
     */
    Vector getRow(int i);

    /**
     * Get the i-th row of matrix;
     * 
     * @param i
     *            row
     * @return the i-th row
     */
    Vector getRow(int i, Factory factory);

    /**
     * Get the i-th column of matrix;
     * 
     * @param i
     *            column
     * @return the i-th column
     */
    Vector getColumn(int i);

    /**
     * Get the i-th column of matrix;
     * 
     * @param i
     *            column
     * @return the i-th column
     */
    Vector getColumn(int i, Factory factory);

    /**
     * Set the i-th row of matrix;
     * 
     * @param i
     *            row
     * @param row
     */
    void setRow(int i, Vector row);

    /**
     * Set the i-th column of matrix;
     * 
     * @param i
     *            column
     * @param column
     */
    void setColumn(int i, Vector column);

    /**
     * Convert matrix to triangle;
     * 
     * @return
     */
    Matrix triangularize();

    /**
     * Convert matrix to triangle with factory;
     * 
     * @param factory
     * @return
     */
    Matrix triangularize(Factory factory);

    /**
     * Decompose matrix;
     * 
     * @param decompositor
     * @return
     */
    Matrix[] decompose(MatrixDecompositor decompositor);

    /**
     * Decompose matrix;
     * 
     * @param decompositor
     * @param factory
     * @return
     */
    Matrix[] decompose(MatrixDecompositor decompositor, Factory factory);

    /**
     * Invert matrix;
     * 
     * @param invertor
     * @return
     */
    Matrix inverse(MatrixInvertor invertor);

    /**
     * Invert matrix;
     * 
     * @param invertor
     * @param factory
     * @return
     */
    Matrix inverse(MatrixInvertor invertor, Factory factory);

    /**
     * Get blank matrix;
     * 
     * @return blanked matrix
     */
    Matrix blank();

    /**
     * Get blank matrix with factory;
     * 
     * @param factory
     * @return
     */
    Matrix blank(Factory factory);

    /**
     * Get copy of matrix;
     * 
     * @return
     */
    Matrix copy();

    /**
     * Get copy of matrix with factory;
     * 
     * @param factory
     * @return
     */
    Matrix copy(Factory factory);

    /**
     * Returns the factory associated with this matrix.
     * 
     * @return factory
     */
    Factory factory();
    /**
     * 
     * @param function
     */
    void each(MatrixProcedure procedure);

    /**
     * 
     * @param function
     */
    Matrix transform(MatrixFunction function);

    /**
     * 
     * @param function
     */
    Matrix transform(MatrixFunction function, Factory factory);

    /**
     * 
     * @param i
     * @param j
     * @param function
     * @return
     */
    Matrix transform(int i, int j, MatrixFunction function);

    /**
     * 
     * @param i
     * @param j
     * @param function
     * @param factory
     * @return
     */
    Matrix transform(int i, int j, MatrixFunction function, Factory factory);

    /**
     * 
     * @param function
     */
    void update(MatrixFunction function);

    /**
     * 
     * @param i
     * @param j
     * @param function
     */
    void update(int i, int j, MatrixFunction function);

    /**
     * 
     * @param predidate
     * @return
     */
    boolean is(MatrixPredicate predidate);

    /**
     * 
     * @return
     */
    Matrix safe();

    /**
     * 
     * @return
     */
    Matrix unsafe();
}
