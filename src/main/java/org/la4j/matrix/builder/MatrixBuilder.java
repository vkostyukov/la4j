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
 */

package org.la4j.matrix.builder;

import org.la4j.io.MatrixStream;
import org.la4j.matrix.Matrix;

import java.util.Random;

/**
 * A matrix builder.
 */
public interface MatrixBuilder {

    /**
     * Specifies the shape of a result matrix.
     *
     * @param rows the number of matrix rows
     * @param columns the number of matrix columns
     *
     * @return a shaped matrix builder
     */
    MatrixBuilder shape(int rows, int columns);

    /**
     * Specifies the source of a result matrix.
     *
     * @param value the constant value source
     *
     * @return a sourced matrix builder
     */
    MatrixBuilder source(double value);

    /**
     * Specifies the source of a result matrix.
     *
     * @param array the 2D source array
     *
     * @return a sourced matrix builder
     */
    MatrixBuilder source(double array[][]);

    /**
     * Specifies the source of a result matrix.
     *
     * @param array the 1D source array
     *
     * @return a sourced matrix builder
     */
    MatrixBuilder source(double array[]);

    /**
     * Specifies the source of a result matrix.
     *
     * @param random the random generator instance
     *
     * @return a sourced matrix builder
     */
    MatrixBuilder source(Random random);

    /**
     * Specifies the source of a result matrix.
     *
     * @param stream the matrix stream instance
     *
     * @return a sourced matrix builder
     */
    MatrixBuilder source(MatrixStream stream);

    /**
     * Specifies the source of a result matrix;
     *
     * @param matrix the source matrix
     *
     * @return a sourced matrix build
     */
    MatrixBuilder source(Matrix matrix);

    /**
     * Commits the building process and returns a simple matrix.
     *
     * @return a freshly built matrix
     */
    Matrix build();

    /**
     * Commits the building process and returns a symmetric matrix.
     *
     * @return a freshly built matrix
     */
    Matrix buildSymmetric();

    /**
     * Commits the building process and returns an identity matrix.
     *
     * @return a freshly built matrix
     */
    Matrix buildIdentity();

    /**
     * Commits the building process and returns a diagonal matrix.
     *
     * @return a freshly built matrix
     */
    Matrix buildDiagonal();
}
