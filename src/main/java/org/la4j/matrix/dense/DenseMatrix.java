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

package org.la4j.matrix.dense;

import org.la4j.factory.Factory;
import org.la4j.matrix.AbstractMatrix;
import org.la4j.matrix.Matrix;
import org.la4j.operation.MatrixMatrixOperation;
import org.la4j.operation.MatrixOperation;
import org.la4j.operation.MatrixVectorOperation;
import org.la4j.vector.Vector;
import org.la4j.vector.dense.DenseVector;

import java.util.Random;

public abstract class DenseMatrix extends AbstractMatrix {

    /**
     * Creates a zero {@link DenseMatrix} of the given shape:
     * {@code rows} x {@code columns}.
     */
    public static DenseMatrix zero(int rows, int columns) {
        return Basic2DMatrix.zero(rows, columns);
    }

    /**
     * Creates a constant {@link DenseMatrix} of the given shape and {@code value}.
     */
    public static DenseMatrix constant(int rows, int columns, double constant) {
        return Basic2DMatrix.constant(rows, columns, constant);
    }

    /**
     * Creates a diagonal {@link DenseMatrix} of the given {@code size} whose
     * diagonal elements are equal to {@code diagonal}.
     */
    public static DenseMatrix diagonal(int size, double diagonal) {
        return Basic2DMatrix.diagonal(size, diagonal);
    }

    /**
     * Creates an unit {@link DenseMatrix} of the given shape:
     * {@code rows} x {@code columns}.
     */
    public static DenseMatrix unit(int rows, int columns) {
        return Basic2DMatrix.unit(rows, columns);
    }

    /**
     * Creates an identity {@link DenseMatrix} of the given {@code size}.
     */
    public static DenseMatrix identity(int size) {
        return Basic2DMatrix.identity(size);
    }

    /**
     * Creates a random {@link DenseMatrix} of the given shape:
     * {@code rows} x {@code columns}.
     */
    public static DenseMatrix random(int rows, int columns, Random random) {
        return Basic2DMatrix.random(rows, columns, random);
    }

    /**
     * Creates a random symmetric {@link DenseMatrix} of the given {@code size}.
     */
    public static DenseMatrix randomSymmetric(int size, Random random) {
        return Basic2DMatrix.randomSymmetric(size, random);
    }

    /**
     * Creates a {@link DenseMatrix} of the given 1D {@code array} w/o
     * copying the underlying array.
     */
    public static DenseMatrix from1DArray(int rows, int columns, double[] array) {
        return Basic1DMatrix.from1DArray(rows, columns, array);
    }

    /**
     * Creates a {@link DenseMatrix} of the given 2D {@code array} w/o
     * copying the underlying array.
     */
    public static DenseMatrix from2DArray(double[][] array) {
        return Basic2DMatrix.from2DArray(array);
    }

    /**
     * Creates a block {@link DenseMatrix} of the given blocks {@code a},
     * {@code b}, {@code c} and {@code d}.
     */
    public static DenseMatrix block(Matrix a, Matrix b, Matrix c, Matrix d) {
        return Basic2DMatrix.block(a, b, c, d);
    }

    protected DenseMatrix(Factory factory, int rows, int columns) {
        super(factory, rows, columns);
    }

    /**
     * Converts this dense matrix to double array.
     * 
     * @return an array representation of this matrix
     */
    public abstract double[][] toArray();

    @Override
    public Vector getRow(int i) {
        Vector result = DenseVector.zero(columns);

        for (int j = 0; j < columns; j++) {
            result.set(j, get(i, j));
        }

        return result;
    }

    @Override
    public Vector getColumn(int j) {
        Vector result = DenseVector.zero(rows);

        for (int i = 0; i < rows; i++) {
            result.set(i, get(i, j));
        }

        return result;
    }

    @Override
    public <T> T apply(MatrixOperation<T> operation) {
        operation.ensureApplicableTo(this);
        return operation.apply(this);
    }

    @Override
    public <T> T apply(MatrixMatrixOperation<T> operation, Matrix that) {
        return that.apply(operation.partiallyApply(this));
    }

    @Override
    public <T> T apply(MatrixVectorOperation<T> operation, Vector that) {
        return that.apply(operation.partiallyApply(this));
    }
}
