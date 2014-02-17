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
 * Contributor(s): Maxim Samoylov
 * 
 */

package org.la4j.factory;

import java.io.Serializable;

import org.la4j.matrix.Matrix;
import org.la4j.matrix.source.MatrixSource;
import org.la4j.vector.Vector;
import org.la4j.vector.source.VectorSource;

public interface Factory extends Serializable {

    /**
     * Creates an empty matrix.
     *
     * @return an empty matrix
     */
    Matrix createMatrix();

    /**
     * Creates a matrix of specified shape.
     * 
     * @param rows the number of matrix rows
     * @param columns the number of matrix columns
     *
     * @return a new matrix of given shape
     */
    Matrix createMatrix(int rows, int columns);

    /**
     * Creates a matrix from given {@code array}.
     * 
     * @param array the source 2D array
     *
     * @return a new matrix of given array
     */
    Matrix createMatrix(double array[][]);

    /**
     * Creates a matrix from another {@code matrix}.
     * 
     * @param matrix the source matrix
     *
     * @return a new matrix
     */
    Matrix createMatrix(Matrix matrix);

    /**
     * Creates a matrix from given matrix {@code source}.
     * 
     * @param source the matrix source
     *
     * @return a new matrix
     */
    Matrix createMatrix(MatrixSource source);

    /**
     * Creates a constant matrix of given shape with {@code value} stored in
     * each matrix cell.
     * 
     * @param rows the number of matrix rows
     * @param columns the number of matrix columns
     *
     * @return a constant matrix
     */
    Matrix createConstantMatrix(int rows, int columns, double value);

    /**
     * Creates a random matrix of given shape.
     * 
     * @param rows the number of matrix rows
     * @param columns the number of matrix columns
     *
     * @return a random matrix
     */
    Matrix createRandomMatrix(int rows, int columns);

    /**
     * Creates a square random symmetric matrix of given {@code size}.
     * 
     * @param size the number of matrix rows/columns
     *
     * @return a square random symmetric matrix
     */
    Matrix createRandomSymmetricMatrix(int size);

    /**
     * Creates a square matrix of given {@code size}.
     * 
     * @param size the number of matrix rows/columns
     *
     * @return a square matrix
     */
    Matrix createSquareMatrix(int size);

    /**
     * Creates an identity matrix of given {@code size}. An identity matrix
     * contains {@code 1.0} at its main diagonal.
     *
     * @param size the number of matrix rows/columns
     *
     * @return an identity matrix
     */
    Matrix createIdentityMatrix(int size);

    /**
     * Creates a matrix from given blocks.
     * Throws IllegalArgumentException if sizes of blocks are incompatible.
     *
     * <p>
     * See <a href="http://mathworld.wolfram.com/BlockMatrix.html">
     * http://mathworld.wolfram.com/BlockMatrix.html</a> for more details.
     * </p>
     *
     * @param a the first block
     * @param b the second block
     * @param c the third block
     * @param d the forth block
     *
     * @return a block matrix
     */
    Matrix createBlockMatrix(Matrix a, Matrix b, Matrix c, Matrix d);

    /**
     * Creates a diagonal matrix of given {@code diagonal}.
     *
     * @param diagonal the matrix diagonal
     *
     * @return a diagonal matrix
     */
    Matrix createDiagonalMatrix(double diagonal[]);

    /**
     * Creates an empty vector.
     * 
     * @return empty vector
     */
    Vector createVector();

    /**
     * Creates a vector of given {@code length}.
     * 
     * @param length the vector's length
     *
     * @return a new vector
     */
    Vector createVector(int length);

    /**
     * Creates a vector from given {@code array}.
     * 
     * @param array the source 1D array
     *
     * @return a new vector
     */
    Vector createVector(double array[]);

    /**
     * Creates a vector from another {@code vector}.
     * 
     * @param vector the source vector
     *
     * @return a new vector
     */
    Vector createVector(Vector vector);

    /**
     * Creates a vector of given {@code source}.
     * 
     * @param source the vector source
     *
     * @return a new vector
     */
    Vector createVector(VectorSource source);

    /**
     * Creates a constant vector of given {@code length} and constant
     * {@code value}.
     * 
     * @param length the vector's length
     * @param value the constant value
     *
     * @return a constant vector
     */
    Vector createConstantVector(int length, double value);

    /**
     * Creates a random vector of given {@code length}.
     * 
     * @param length the vector's length
     *
     * @return a random vector
     */
    Vector createRandomVector(int length);
}
