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
import org.la4j.matrix.source.MatrixSource;
import org.la4j.matrix.sparse.CCSMatrix;

public class CCSFactory extends CompressedFactory implements Factory {

    private static final long serialVersionUID = 4071505L;

    @Override
    public Matrix createMatrix() {
        return new CCSMatrix();
    }

    @Override
    public Matrix createMatrix(int rows, int columns) {
        return new CCSMatrix(rows, columns);
    }

    @Override
    public Matrix createMatrix(double[][] array) {
        return new CCSMatrix(array);
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
    public Matrix createRandomMatrix(int rows, int columns) {

        Random random = new Random();

        int cardinality = (rows * columns) / DENSITY;

        double values[] = new double[cardinality];
        int rowIndices[] = new int[cardinality];
        int columnPointers[] = new int[columns + 1];

        int kk = cardinality / columns;
        int indices[] = new int[kk];

        int k = 0;
        for (int j = 0; j < columns; j++) {

            columnPointers[j] = k;

            for (int jj = 0; jj < kk; jj++) {
                indices[jj] = random.nextInt(rows);
            }

            Arrays.sort(indices);

            int previous = -1;
            for (int jj = 0; jj < kk; jj++) {

                if (indices[jj] == previous) {
                    continue;
                }

                values[k] = random.nextDouble();
                rowIndices[k++] = indices[jj];
                previous = indices[jj];
            }
        }

        columnPointers[columns] = cardinality;

        return new CCSMatrix(rows, columns, cardinality, values, 
                             rowIndices, columnPointers);
    }

    @Override
    public Matrix createRandomSymmetricMatrix(int size) {

        // TODO: Issue 15

        int cardinality = (size * size) / DENSITY;

        Random random = new Random();

        Matrix matrix = new CCSMatrix(size, size, cardinality);

        for (int k = 0; k < cardinality / 2; k++) {
            int i = random.nextInt(size);
            int j = random.nextInt(size);
            double value = random.nextDouble();
            
            matrix.set(i, j, value);
            matrix.set(j, i, value);
        }

        return matrix;
    }

    @Override
    public Matrix createSquareMatrix(int size) {
        return new CCSMatrix(size, size);
    }

    @Override
    public Matrix createIdentityMatrix(int size) {

        double values[] = new double[size];
        int rowIndices[] = new int[size];
        int columnPointers[] = new int[size + 1];

        for (int i = 0; i < size; i++) {
            values[i] = 1.0;
            rowIndices[i] = i;
            columnPointers[i] = i;
        }
        columnPointers[size] = size;

        return new CCSMatrix(size, size, size, values, rowIndices,
                             columnPointers);
    }
}
