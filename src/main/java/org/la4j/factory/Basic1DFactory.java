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
 * Contributor(s): Maxim Samoylov
 * 
 */

package org.la4j.factory;

import java.util.Arrays;
import java.util.Random;

import org.la4j.matrix.Matrix;
import org.la4j.matrix.dense.Basic1DMatrix;
import org.la4j.matrix.source.MatrixSource;

@Deprecated
public class Basic1DFactory extends BasicFactory {

    private static final long serialVersionUID = 4071505L;

    @Override
    public Matrix createMatrix() {
        return Basic1DMatrix.zero(0, 0);
    }

    @Override
    public Matrix createMatrix(int rows, int columns) {
        return Basic1DMatrix.zero(rows, columns);
    }

    @Override
    public Matrix createMatrix(int rows, int columns, double[] array) {
        return Basic1DMatrix.from1DArray(rows, columns, array);
    }

    @Override
    public Matrix createMatrix(double[][] array) {
        return Basic1DMatrix.from2DArray(array);
    }

    @Override
    public Matrix createMatrix(Matrix matrix) {
        return new Basic1DMatrix(matrix);
    }

    @Override
    public Matrix createMatrix(MatrixSource source) {
        return new Basic1DMatrix(source);
    }

    @Override
    public Matrix createConstantMatrix(int rows, int columns, double value) {
        return Basic1DMatrix.constant(rows, columns, value);
    }

    @Override
    public Matrix createRandomMatrix(int rows, int columns, Random random) {
        return Basic1DMatrix.random(rows, columns, random);
    }

    @Override
    public Matrix createRandomSymmetricMatrix(int size, Random random) {
        return Basic1DMatrix.randomSymmetric(size, random);
    }

    @Override
    public Matrix createSquareMatrix(int size) {
        return new Basic1DMatrix(size, size);
    }

    @Override
    public Matrix createIdentityMatrix(int size) {
         return Basic1DMatrix.identity(size);
    }

    @Override
    public Matrix createBlockMatrix(Matrix a, Matrix b, Matrix c, Matrix d) {
        return Basic1DMatrix.block(a, b, c, d);
    }

    @Override
    public Matrix createDiagonalMatrix(double[] diagonal) {

        int size = diagonal.length;
        double array[] = new double[size * size];

        for (int i = 0; i < size; i++) {
            array[i * size + i] = diagonal[i];
        }

        return new Basic1DMatrix(size, size, array);

    }
}
