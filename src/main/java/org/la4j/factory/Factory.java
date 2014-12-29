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
import java.util.Random;

import org.la4j.matrix.Matrices;
import org.la4j.matrix.Matrix;
import org.la4j.matrix.MatrixFactory;
import org.la4j.matrix.source.MatrixSource;
import org.la4j.vector.Vector;
import org.la4j.vector.VectorFactory;
import org.la4j.vector.Vectors;
import org.la4j.vector.source.VectorSource;

@Deprecated
public abstract class Factory implements Serializable {

    /**
     * Creates an empty matrix.
     *
     * @return an empty matrix
     */
    public abstract Matrix createMatrix();

    /**
     * Creates a matrix of specified shape.
     * 
     * @param rows the number of matrix rows
     * @param columns the number of matrix columns
     *
     * @return a new matrix of given shape
     */
    public abstract Matrix createMatrix(int rows, int columns);

    /**
     * Creates a matrix of specified shape.
     *
     * @param rows the number of matrix rows
     * @param columns the number of matrix columns
     * @param array the source 1D array
     *
     * @return a new matrix of given shape
     */
    public abstract Matrix createMatrix(int rows, int columns, double array[]);

    /**
     * Creates a matrix from given {@code array}.
     * 
     * @param array the source 2D array
     *
     * @return a new matrix of given array
     */
    public abstract Matrix createMatrix(double array[][]);

    /**
     * Creates a matrix from another {@code matrix}.
     * 
     * @param matrix the source matrix
     *
     * @return a new matrix
     */
    public abstract Matrix createMatrix(Matrix matrix);

    /**
     * Creates a matrix from given matrix {@code source}.
     * 
     * @param source the matrix source
     *
     * @return a new matrix
     */
    public abstract Matrix createMatrix(MatrixSource source);

    /**
     * Creates a constant matrix of given shape with {@code value} stored in
     * each matrix cell.
     * 
     * @param rows the number of matrix rows
     * @param columns the number of matrix columns
     *
     * @return a constant matrix
     */
    public abstract Matrix createConstantMatrix(int rows, int columns, double value);

    /**
     * Creates a random matrix of given shape.
     * 
     * @param rows the number of matrix rows
     * @param columns the number of matrix columns
     *
     * @return a random matrix
     */
    public Matrix createRandomMatrix(int rows, int columns) {
        return createRandomMatrix(rows, columns, new Random());
    }

    /**
     * Creates a random matrix of given shape.
     *
     * @param rows the number of matrix rows
     * @param columns the number of matrix columns
     * @param random the random object instance
     *
     * @return a random matrix
     */
    public abstract Matrix createRandomMatrix(int rows, int columns, Random random);

    /**
     * Creates a square random symmetric matrix of given {@code size}.
     * 
     * @param size the number of matrix rows/columns
     *
     * @return a square random symmetric matrix
     */
    Matrix createRandomSymmetricMatrix(int size) {
        return createRandomSymmetricMatrix(size, new Random());
    }

    /**
     * Creates a square random symmetric matrix of given {@code size}.
     *
     * @param size the number of matrix rows/columns
     * @param random the random object instance
     *
     * @return a square random symmetric matrix
     */
    public abstract Matrix createRandomSymmetricMatrix(int size, Random random);

    /**
     * Creates a square matrix of given {@code size}.
     * 
     * @param size the number of matrix rows/columns
     *
     * @return a square matrix
     */
    public abstract Matrix createSquareMatrix(int size);

    /**
     * Creates an identity matrix of given {@code size}. An identity matrix
     * contains {@code 1.0} at its main diagonal.
     *
     * @param size the number of matrix rows/columns
     *
     * @return an identity matrix
     */
    public abstract Matrix createIdentityMatrix(int size);

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
    public abstract Matrix createBlockMatrix(Matrix a, Matrix b, Matrix c, Matrix d);

    /**
     * Creates a diagonal matrix of given {@code diagonal}.
     *
     * @param diagonal the matrix diagonal
     *
     * @return a diagonal matrix
     */
    public abstract Matrix createDiagonalMatrix(double diagonal[]);

    /**
     * Creates an empty vector.
     * 
     * @return empty vector
     */
    public abstract Vector createVector();

    /**
     * Creates a vector of given {@code length}.
     * 
     * @param length the vector's length
     *
     * @return a new vector
     */
    public abstract Vector createVector(int length);

    /**
     * Creates a vector from given {@code array}.
     * 
     * @param array the source 1D array
     *
     * @return a new vector
     */
    public abstract Vector createVector(double array[]);

    /**
     * Creates a vector from another {@code vector}.
     * 
     * @param vector the source vector
     *
     * @return a new vector
     */
    public abstract Vector createVector(Vector vector);

    /**
     * Creates a vector of given {@code source}.
     * 
     * @param source the vector source
     *
     * @return a new vector
     */
    public abstract Vector createVector(VectorSource source);

    /**
     * Creates a constant vector of given {@code length} and constant
     * {@code value}.
     * 
     * @param length the vector's length
     * @param value the constant value
     *
     * @return a constant vector
     */
    public abstract Vector createConstantVector(int length, double value);

    /**
     * Creates a random vector of given {@code length}.
     * 
     * @param length the vector's length
     *
     * @return a random vector
     */
    public Vector createRandomVector(int length) {
        return createRandomVector(length, new Random());
    }

    /**
     * Creates a random vector of given {@code length}.
     *
     * @param length the vector's length
     * @param random the random object instance
     *
     * @return a random vector
     */
    public abstract Vector createRandomVector(int length, Random random);

    /**
     * A method for internal needs to make the factory deprecation as smooth
     * as possible.
     */
    @SuppressWarnings("unchecked cast")
    public static <T extends Vector> VectorFactory<T> asVectorFactory(Factory factory) {
        if (factory instanceof CompressedFactory) {
            return (VectorFactory<T>) Vectors.SPARSE;
        } else if (factory instanceof BasicFactory) {
            return (VectorFactory<T>) Vectors.DENSE;
        } else {
            throw new IllegalArgumentException("You have the factory I don't know about.");
        }
    }

    @SuppressWarnings("unchecked cast")
    public static <T extends Matrix> MatrixFactory<T> asMatrixFactory(Factory factory) {
        if (factory instanceof CRSFactory) {
            return (MatrixFactory<T>) Matrices.CRS;
        } else if (factory instanceof  CCSFactory) {
            return (MatrixFactory<T>) Matrices.CCS;
        } else if (factory instanceof Basic1DFactory) {
            return (MatrixFactory<T>) Matrices.BASIC_1D;
        } else if (factory instanceof Basic2DFactory) {
            return (MatrixFactory<T>) Matrices.BASIC_2D;
        } else {
            throw new IllegalArgumentException("You have the factory I don't know about.");
        }

    }
}
