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

import java.util.Arrays;
import java.util.Random;

import org.la4j.matrix.Matrix;
import org.la4j.matrix.dense.Basic1DMatrix;
import org.la4j.matrix.source.MatrixSource;

public class Basic1DFactory extends BasicFactory implements Factory {

    private static final long serialVersionUID = 4071505L;

    @Override
    public Matrix createMatrix() {
        return new Basic1DMatrix();
    }

    @Override
    public Matrix createMatrix(int rows, int columns) {
        return new Basic1DMatrix(rows, columns);
    }

    @Override
    public Matrix createMatrix(double[][] array) {
        return new Basic1DMatrix(array);
    }

    @Override
    public Matrix createMatrix(Matrix matrix) {
        return new Basic1DMatrix(matrix);
    }

    public Matrix createMatrix(MatrixSource source) {
        return new Basic1DMatrix(source);
    }

    @Override
    public Matrix createConstantMatrix(int rows, int columns, double value) {

        double array[] = new double[rows * columns];
        Arrays.fill(array, value);

        return new Basic1DMatrix(rows, columns, array);
    }

    @Override
    public Matrix createRandomMatrix(int rows, int columns) {

        double array[] = new double[rows * columns];

        Random rnd = new Random();

        for (int i = 0; i < rows * columns; i++) {
            array[i] = rnd.nextDouble();
        }

        return new Basic1DMatrix(rows, columns, array);
    }

    @Override
    public Matrix createRandomSymmetricMatrix(int size) {

        double array[] = new double[size * size];

        Random rnd = new Random();

        for (int i = 0; i < size; i++) {
            for (int j = i; j < size; j++) {
                double value = rnd.nextDouble();
                array[i * size + j] = value;
                array[j * size + i] = value;
            }
        }

        return new Basic1DMatrix(size, size, array);
    }

    @Override
    public Matrix createSquareMatrix(int size) {
        return new Basic1DMatrix(size, size);
    }

    @Override
    public Matrix createIdentityMatrix(int size) {
 
        double array[] = new double[size * size];

        for (int i = 0; i < size; i++) {
            array[i * size + i] = (double) 1.0;
        }

        return new Basic1DMatrix(size, size, array);
    }
}
