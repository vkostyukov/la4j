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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import org.la4j.matrix.Matrices;
import org.la4j.matrix.Matrix;
import org.la4j.matrix.source.MatrixSource;
import org.la4j.matrix.sparse.CRSMatrix;

@Deprecated
public class CRSFactory extends CompressedFactory {

    private static final long serialVersionUID = 4071505L;

    @Override
    public Matrix createMatrix() {
        return CRSMatrix.zero(0, 0);
    }

    @Override
    public Matrix createMatrix(int rows, int columns) {
        return CRSMatrix.zero(rows, columns);
    }

    @Override
    public Matrix createMatrix(int rows, int columns, double[] array) {
        return CRSMatrix.from1DArray(rows, columns, array);
    }

    @Override
    public Matrix createMatrix(double[][] array) {
        return CRSMatrix.from2DArray(array);
    }

    @Override
    public Matrix createMatrix(Matrix matrix) {
        return new CRSMatrix(matrix);
    }

    @Override
    public Matrix createMatrix(MatrixSource source) {
        return new CRSMatrix(source);
    }

    @Override
    public Matrix createConstantMatrix(int rows, int columns, double value) {

        int size = rows * columns;

        double values[] = new double[size];
        int columnIndices[] = new int[size];
        int rowPointers[] = new int[rows + 1];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                values[i * columns + j] = value;
                columnIndices[i * columns + j] = j;
            }
            rowPointers[i] = columns * i;
        }

        rowPointers[rows] = size;

        return new CRSMatrix(rows, columns, size, values, 
                             columnIndices, rowPointers);
    }

    @Override
    public Matrix createRandomMatrix(int rows, int columns, Random random) {
        return CRSMatrix.random(rows, columns, 1.0 / DENSITY, random);
    }

    @Override
    public Matrix createRandomSymmetricMatrix(int size, Random random) {
        return CRSMatrix.randomSymmetric(size, 1.0 / DENSITY, random);
    }

    @Override
    public Matrix createSquareMatrix(int size) {
        return createMatrix(size, size);
    }

    @Override
    public Matrix createIdentityMatrix(int size) {
        return CRSMatrix.identity(size);
    }

    @Override
    public Matrix createBlockMatrix(Matrix a, Matrix b, Matrix c, Matrix d) {
        return CRSMatrix.block(a, b, c, d);
    }

    @Override
    public Matrix createDiagonalMatrix(double[] diagonal) {

        int size = diagonal.length;
        int columnIndices[] = new int[size];
        int rowPointers[] = new int[size + 1];

        for (int i = 0; i < size; i++) {
            columnIndices[i] = i;
            rowPointers[i] = i;
        }

        rowPointers[size] = size;

        return new CRSMatrix(size, size, size, diagonal, columnIndices, rowPointers);
    }
}
