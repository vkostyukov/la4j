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

import java.util.Random;

import org.la4j.matrix.Matrix;
import org.la4j.matrix.dense.Basic2DMatrix;
import org.la4j.matrix.source.MatrixSource;

@Deprecated
public class Basic2DFactory extends BasicFactory {

    private static final long serialVersionUID = 4071505L;

    @Override
    public Matrix createMatrix() {
        return Basic2DMatrix.zero(0, 0);
    }

    @Override
    public Matrix createMatrix(int rows, int columns) {
        return Basic2DMatrix.zero(rows, columns);
    }

    @Override
    public Matrix createMatrix(int rows, int columns, double[] array) {
        return Basic2DMatrix.from1DArray(rows, columns, array);
    }

    @Override
    public Matrix createMatrix(double array[][]) {
        return Basic2DMatrix.from2DArray(array);
    }

    @Override
    public Matrix createMatrix(Matrix matrix) {
        return new Basic2DMatrix(matrix);
    }

    @Override
    public Matrix createMatrix(MatrixSource source) {
        return new Basic2DMatrix(source);
    }

    @Override
    public Matrix createConstantMatrix(int rows, int columns, double value) {
        return Basic2DMatrix.constant(rows, columns, value);
    }

    @Override
    public Matrix createRandomMatrix(int rows, int columns, Random random) {
        return Basic2DMatrix.random(rows, columns, random);
    }

    @Override
    public Matrix createRandomSymmetricMatrix(int size, Random random) {
        return Basic2DMatrix.randomSymmetric(size, random);
    }

    @Override
    public Matrix createSquareMatrix(int size) {
        return new Basic2DMatrix(size, size);
    }

    @Override
    public Matrix createIdentityMatrix(int size) {
        return Basic2DMatrix.identity(size);
    }

    @Override
    public Matrix createBlockMatrix(Matrix a, Matrix b, Matrix c, Matrix d) {
        return Basic2DMatrix.block(a, b, c, d);
    }

    @Override
    public Matrix createDiagonalMatrix(double[] diagonal) {

        int size = diagonal.length;
        double array[][] = new double[size][size];

        for (int i = 0; i < size; i++) {
            array[i][i] = diagonal[i];
        }

        return new Basic2DMatrix(array);
    }
}
