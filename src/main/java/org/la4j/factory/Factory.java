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
 * Contributor(s): -
 * 
 */

package org.la4j.factory;

import java.io.Serializable;

import org.la4j.linear.LinearSystem;
import org.la4j.matrix.Matrix;
import org.la4j.matrix.source.MatrixSource;
import org.la4j.vector.Vector;
import org.la4j.vector.source.VectorSource;

public interface Factory extends Serializable {

    /**
     * Creates an empty matrix.
     * 
     * <p>
     * See <a href="http://mathworld.wolfram.com/Matrix.html">
     * http://mathworld.wolfram.com/Matrix.html</a> for more details.
     * </p>
     * 
     * @return empty matrix
     */
    Matrix createMatrix();

    /**
     * Creates a matrix with specified size.
     * 
     * <p>
     * See <a href="http://mathworld.wolfram.com/Matrix.html">
     * http://mathworld.wolfram.com/Matrix.html</a> for more details.
     * </p>
     * @param rows
     * @param columns
     * @return
     */
    Matrix createMatrix(int rows, int columns);

    /**
     * Creates a matrix from array.
     * 
     * @param array
     * @return
     */
    Matrix createMatrix(double array[][]);

    /**
     * Creates a matrix from another matrix.
     * 
     * @param matrix
     * @return
     */
    Matrix createMatrix(Matrix matrix);

    /**
     * Creates a matrix from matrix proxy.
     * 
     * @param matrix
     * @return
     */
    Matrix createMatrix(MatrixSource source);

    /**
     * Creates random matrix.
     * 
     * @param rows
     * @param columns
     * @return
     */
    Matrix createRandomMatrix(int rows, int columns);

    /**
     * Creates random symmetric matrix.
     * 
     * @param size
     * @return
     */
    Matrix createRandomSymmetricMatrix(int size);

    /**
     * Creates square matrix with specified size.
     * 
     * <p>
     * See <a href="http://mathworld.wolfram.com/SquareMatrix.html">
     * http://mathworld.wolfram.com/SquareMatrix.html</a> for more details.
     * </p>
     * 
     * @param size
     * @return
     */
    Matrix createSquareMatrix(int size);

    /**
     * Creates identity matrix.
     * 
     * <p>
     * See <a href="http://mathworld.wolfram.com/IdentityMatrix.html">
     * http://mathworld.wolfram.com/IdentityMatrix.html</a> for more details.
     * </p>
     * 
     * @param size
     * @return
     */
    Matrix createIdentityMatrix(int size);

    /**
     * Creates an empty vector.
     * 
     * See <a href="http://mathworld.wolfram.com/Vector.html">
     * http://mathworld.wolfram.com/Vector.html</a> for more details.
     * </p>
     * 
     * @return empty vector
     */
    Vector createVector();

    /**
     * Creates vector with specified length.
     * 
     * @param length
     * @return
     */
    Vector createVector(int length);

    /**
     * Creates vector from array.
     * 
     * @param array
     * @return
     */
    Vector createVector(double array[]);

    /**
     * 
     * @param vector
     * @return
     */
    Vector createVector(Vector vector);

    /**
     * 
     * @param proxy
     * @return
     */
    Vector createVector(VectorSource source);

    /**
     * Creates random vector.
     * 
     * @param length
     * @return
     */
    Vector createRandomVector(int length);

    /**
     * 
     * @return
     */
    LinearSystem createLinearSystem(Matrix a, Vector b);

    /**
     * 
     * @return
     */
    Factory safe();

    /**
     * 
     * @return
     */
    Factory unsafe();
}
