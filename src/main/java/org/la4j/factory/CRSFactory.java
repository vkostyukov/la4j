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

import java.util.Random;

import org.la4j.matrix.Matrix;
import org.la4j.matrix.source.MatrixSource;
import org.la4j.matrix.sparse.CRSMatrix;

public class CRSFactory extends CompressedFactory implements Factory {

    private static final long serialVersionUID = 4071505L;

    @Override
    public Matrix createMatrix() {
        return new CRSMatrix();
    }

    @Override
    public Matrix createMatrix(int rows, int columns) {
        return new CRSMatrix(rows, columns);
    }

    @Override
    public Matrix createMatrix(double[][] array) {
        return new CRSMatrix(array);
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
    public Matrix createRandomMatrix(int rows, int columns) {

        // TODO: Issue 15

        int cardinality = (rows * columns) / DENSITY;

        Random random = new Random();

        Matrix matrix = new CRSMatrix(rows, columns, cardinality);

        for (int k = 0; k < cardinality; k++) {
            int i = random.nextInt(rows);
            int j = random.nextInt(columns);

            matrix.set(i, j, random.nextDouble());
        }

        return matrix;
    }

    @Override
    public Matrix createRandomSymmetricMatrix(int size) {

        // TODO: Issue 13

        int cardinality = (size * size) / DENSITY;

        Random random = new Random();

        Matrix matrix = new CRSMatrix(size, size, cardinality);

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
        return createMatrix(size, size);
    }

    @Override
    public Matrix createIdentityMatrix(int size) {

        double values[] = new double[size];
        int columnIndices[] = new int[size];
        int rowPointers[] = new int[size + 1];

        for (int i = 0; i < size; i++) {
            values[i] = (double) 1.0;
            columnIndices[i] = i;
            rowPointers[i] = i;
        }
        rowPointers[size] = size;

        return new CRSMatrix(size, size, size, values, columnIndices,
                             rowPointers);
    }
}
