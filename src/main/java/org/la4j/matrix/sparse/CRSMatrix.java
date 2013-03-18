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

package org.la4j.matrix.sparse;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.la4j.factory.CRSFactory;
import org.la4j.factory.Factory;
import org.la4j.matrix.Matrices;
import org.la4j.matrix.Matrix;
import org.la4j.matrix.functor.MatrixProcedure;
import org.la4j.matrix.source.Array2DMatrixSource;
import org.la4j.matrix.source.MatrixSource;
import org.la4j.matrix.source.UnsafeMatrixSource;
import org.la4j.vector.Vector;
import org.la4j.vector.sparse.CompressedVector;

public class CRSMatrix extends AbstractCompressedMatrix implements SparseMatrix {

    private static final long serialVersionUID = 4071505L;

    private static final int MINIMUM_SIZE = 32;

    private double values[];
    private int columnIndices[];
    private int rowPointers[];

    public CRSMatrix() {
        this(0, 0);
    }

    public CRSMatrix(int rows, int columns) {
        this(rows, columns, 0);
    }

    public CRSMatrix(Matrix matrix) {
        this(new UnsafeMatrixSource(matrix));
    }

    public CRSMatrix(double array[][]) {
        this(new Array2DMatrixSource(array));
    }

    public CRSMatrix(MatrixSource source) {
        this(source.rows(), source.columns(), 0);

        for (int i = 0; i < rows; i++) {
            rowPointers[i] = cardinality;
            for (int j = 0; j < columns; j++) {
                double value = source.get(i, j);
                if (Math.abs(value) > Matrices.EPS) {

                    if (values.length < cardinality + 1) {
                        growup();
                    }

                    values[cardinality] = value;
                    columnIndices[cardinality] = j;
                    cardinality++;
                }
            }
        }

        rowPointers[rows] = cardinality;
    }

    public CRSMatrix(int rows, int columns, int cardinality) {
        super(new CRSFactory(), rows, columns);

        int alignedSize = align(rows, columns, cardinality);

        this.cardinality = 0;
        this.values = new double[alignedSize];
        this.columnIndices = new int[alignedSize];
        this.rowPointers = new int[rows + 1];
    }

    public CRSMatrix(int rows, int columns, int cardinality, double values[],
            int columnIndices[], int rowPointers[]) {

        super(new CRSFactory(), rows, columns);

        this.cardinality = cardinality;

        this.values = values;
        this.columnIndices = columnIndices;
        this.rowPointers = rowPointers;
    }

    @Override
    public double get(int i, int j) {

        for (int ii = rowPointers[i]; ii < rowPointers[i + 1]; ii++) {
            if (columnIndices[ii] == j) {
                return values[ii];
            }
        }

        return 0.0;
    }

    @Override
    public void set(int i, int j, double value) {

        for (int ii = rowPointers[i]; ii < rowPointers[i + 1]; ii++) {
            if (columnIndices[ii] == j) {

                // TODO: Issue 14
                // clear the value cell if the value is 0

                values[ii] = value;
                return;
            }
        }

        if (Math.abs(value) < Matrices.EPS) {
            return;
        }

        if (values.length < cardinality + 1) {
            growup();
        }

        int position = rowPointers[i];
        while (position < rowPointers[i + 1] && j >= columnIndices[position]) {
            position++;
        }

        for (int k = cardinality; k > position; k--) {
            values[k] = values[k - 1];
            columnIndices[k] = columnIndices[k - 1];
        }

        values[position] = value;
        columnIndices[position] = j;

        for (int k = i + 1; k < rows + 1; k++) {
            rowPointers[k]++;
        }

        cardinality++;
    }

    @Override
    public void resize(int rows, int columns) {

        if (rows < 0 || columns < 0) {
            throw new IllegalArgumentException("Wrong dimensions: " 
                    + rows + "x" + columns);
        }

        if (this.rows == rows && this.columns == columns) {
            return;
        }

        if (this.rows >= rows && this.columns >= columns) {

            int position = 0;
            for (int k = 0; k < rowPointers[rows]; k++) {
                if (columns > columnIndices[k]) {
                    values[position++] = values[k];
                }
            }

            cardinality = rowPointers[rows];

        } else if (this.rows < rows) {

            int newRowPointers[] = new int[rows + 1];
            System.arraycopy(rowPointers, 0, newRowPointers, 0,
                    rowPointers.length);

            for (int i = this.rows; i < rows + 1; i++) {
                newRowPointers[i] = cardinality;
            }

            this.rowPointers = newRowPointers;
        }

        this.rows = rows;
        this.columns = columns;
    }

    @Override
    public Matrix transpose(Factory factory) {
        ensureFactoryIsNotNull(factory);

        Matrix result = factory.createMatrix(columns, rows);

        int k = 0, i = 0;
        while (k < cardinality) {
            for (int j = rowPointers[i]; j < rowPointers[i + 1]; j++, k++) {
                result.set(columnIndices[j], i, values[j]);
            }
            i++;
        }

        return result;
    }

    @Override
    public Vector getRow(int i) {

        int rowCardinality = rowPointers[i + 1] - rowPointers[i]; 

        double rowValues[] = new double[rowCardinality];
        int rowIndices[] = new int[rowCardinality];

        System.arraycopy(values, rowPointers[i], rowValues, 0, rowCardinality);
        System.arraycopy(columnIndices, rowPointers[i], rowIndices, 
                         0, rowCardinality);

        return new CompressedVector(columns, rowCardinality, rowValues, 
                                    rowIndices);
    }

    @Override
    public Vector getRow(int i, Factory factory) {
        ensureFactoryIsNotNull(factory);

        Vector result = factory.createVector(columns);

        for (int ii = rowPointers[i]; ii < rowPointers[i + 1]; ii++) {
            result.set(columnIndices[ii], values[ii]);
        }

        return result;
    }

    @Override
    public Vector getColumn(int i, Factory factory) {
        ensureFactoryIsNotNull(factory);

        Vector result = factory.createVector(rows);

        int k = 0, ii = 0;
        while (k < cardinality) {
            for (int jj = rowPointers[ii]; jj < rowPointers[ii + 1]; 
                 jj++, k++) {

                if (columnIndices[jj] == i) {
                    result.set(ii, values[jj]);
                }
            }
            ii++;
        }

        return result;
    }

    @Override
    public void setColumn(int i, Vector column) {

        if (column == null) {
            throw new IllegalArgumentException("Column can't be null.");
        }

        if (rows != column.length()) {
            throw new IllegalArgumentException("Wrong column length: " 
                                               + column.length());
        }

        for (int ii = 0; ii < column.length(); ii++) {

            int position = rowPointers[ii], limit = rowPointers[ii + 1];
            while (position < limit && columnIndices[position] < i) {
                position++;
            }

            double value = column.get(ii); 

            if (Math.abs(value) > Matrices.EPS) {

                if (values.length < cardinality + 1) {
                    growup();
                }

                if (columnIndices[position] != i || position == limit) {

                    for (int k = cardinality; k > position; k--) {
                        values[k] = values[k - 1];
                        columnIndices[k] = columnIndices[k - 1];
                    }

                    for (int k = ii + 1; k < rows + 1; k++) {
                        rowPointers[k]++;
                    }

                    columnIndices[position] = i;

                    cardinality++;
                }

                values[position] = value;

            } else if (columnIndices[position] == i && position < limit) {

                for (int k = position; k < cardinality - 1; k++) {
                    values[k] = values[k + 1];
                    columnIndices[k] = columnIndices[k + 1];
                }

                for (int k = ii + 1; k < rows + 1; k++) {
                    rowPointers[k]--;
                }

                cardinality--;
            }
        }
    }

    @Override
    public void setRow(int i, Vector row) {

        if (row == null) {
            throw new IllegalArgumentException("Row can't be null.");
        }

        if (columns != row.length()) {
            throw new IllegalArgumentException("Wrong row length: " 
                                               + row.length());
        }

        int position = rowPointers[i], limit = rowPointers[i + 1];

        rowPointers[i] = limit;

        for (int ii = 0; ii < row.length(); ii++) {

            double value = row.get(ii);

            if (Math.abs(value) > Matrices.EPS) {

                if (position >= limit) {

                    if (values.length < cardinality + 1) {
                        growup();
                    }

                    for (int k = cardinality; k > position; k--) {
                        values[k] = values[k - 1];
                        columnIndices[k] = columnIndices[k - 1];
                    }

                    cardinality++;

                } else {
                    rowPointers[i]--;
                }

                values[position] = value;
                columnIndices[position] = ii;
                position++;
            }
        }

        if (limit > position) {

            cardinality -= (limit - position);

            for (int k = position; k < cardinality; k++) {
                values[k] = values[k + (limit - position)];
                columnIndices[k] = columnIndices[k + (limit - position)];
            }

            rowPointers[i] -= (limit - position);
        }

        for (int k = i + 1; k < rows + 1; k++) {
            rowPointers[k] += (position - limit);
        }
    }

    @Override
    public void each(MatrixProcedure procedure) {
        int k = 0, i = 0;
        while (k < cardinality) {
            for (int j = rowPointers[i]; j < rowPointers[i + 1]; j++, k++) {
                procedure.apply(i, columnIndices[j], values[j]);
            }
            i++;
        }
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {

        out.writeInt(rows);
        out.writeInt(columns);
        out.writeInt(cardinality);

        int k = 0, i = 0;
        while (k < cardinality) {
            for (int j = rowPointers[i]; j < rowPointers[i + 1]; j++, k++) {
                out.writeInt(i);
                out.writeInt(columnIndices[j]);
                out.writeDouble(values[j]);
            }
            i++;
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {

        rows = in.readInt();
        columns = in.readInt();
        cardinality = in.readInt();

        int alignedSize = align(rows, columns, cardinality);

        values = new double[alignedSize];
        columnIndices = new int[alignedSize];
        rowPointers = new int[rows + 1];

        for (int k = 0; k < cardinality; k++) {
            int i = in.readInt();
            columnIndices[k] = in.readInt();
            values[k] = in.readDouble();
            rowPointers[i + 1] = k + 1;
        }
    }

    private void growup() {

        int newSize = Math.min(rows * columns, (cardinality * 3) / 2 + 1);

        double newValues[] = new double[newSize];
        int newColumnIndices[] = new int[newSize];

        System.arraycopy(values, 0, newValues, 0, cardinality);
        System.arraycopy(columnIndices, 0, newColumnIndices, 0, cardinality);

        this.values = newValues;
        this.columnIndices = newColumnIndices;
    }

    private int align(int rows, int columns, int cardinality) {
        return Math.min(rows * columns, ((cardinality % MINIMUM_SIZE) + 1)
                * MINIMUM_SIZE);
    }
}
