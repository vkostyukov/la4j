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
import org.la4j.matrix.sparse.CCSMatrix;
import org.la4j.matrix.sparse.CRSMatrix;

@Deprecated
public class CCSFactory extends CompressedFactory {

    private static final long serialVersionUID = 4071505L;

    @Override
    public Matrix createMatrix() {
        return CCSMatrix.zero(0, 0);
    }

    @Override
    public Matrix createMatrix(int rows, int columns) {
        return CCSMatrix.zero(rows, columns);
    }

    @Override
    public Matrix createMatrix(int rows, int columns, double array[]) {
        return CCSMatrix.from1DArray(rows, columns, array);
    }

    @Override
    public Matrix createMatrix(double[][] array) {
        return CCSMatrix.from2DArray(array);
    }

    @Override
    public Matrix createMatrix(Matrix matrix) {
        return new CCSMatrix(matrix);
    }

    @Override
    public Matrix createMatrix(MatrixSource source) {
        return new CCSMatrix(source);
    }

    @Override
    public Matrix createConstantMatrix(int rows, int columns, double value) {

        int size = rows * columns;

        double values[] = new double[size];
        int rowIndices[] = new int[size];
        int columnPointers[] = new int[columns + 1];

        for (int j = 0; j < columns; j++) {
            for (int i = 0; i < rows; i++) {
                values[j * rows + i] = value;
                rowIndices[j * rows + i] = i;
            }
            columnPointers[j] = rows * j;
        }

        columnPointers[columns] = size;

        return new CCSMatrix(rows, columns, size, values, rowIndices, 
                             columnPointers);
    }

    @Override
    public Matrix createRandomMatrix(int rows, int columns, Random random) {
        return CCSMatrix.random(rows, columns, 1.0 / DENSITY, random);
    }

    @Override
    public Matrix createRandomSymmetricMatrix(int size, Random random) {
        return CCSMatrix.randomSymmetric(size, 1.0 / DENSITY, random);
    }

    @Override
    public Matrix createSquareMatrix(int size) {
        return new CCSMatrix(size, size);
    }

    @Override
    public Matrix createIdentityMatrix(int size) {
        return CCSMatrix.identity(size);
    }

    @Override
    public Matrix createBlockMatrix(Matrix a, Matrix b, Matrix c, Matrix d) {
        return CCSMatrix.block(a, b, c, d);
    }

    @Override
    public Matrix createDiagonalMatrix(double[] diagonal) {

        int size = diagonal.length;
        int rowIndices[] = new int[size];
        int columnPointers[] = new int[size + 1];

        for (int i = 0; i < size; i++) {
            rowIndices[i] = i;
            columnPointers[i] = i;
        }
        columnPointers[size] = size;

        return new CCSMatrix(size, size, size, diagonal, rowIndices, columnPointers);
    }
}
