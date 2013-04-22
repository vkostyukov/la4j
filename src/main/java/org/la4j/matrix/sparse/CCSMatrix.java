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

import org.la4j.factory.Factory;
import org.la4j.matrix.Matrices;
import org.la4j.matrix.Matrix;
import org.la4j.matrix.functor.MatrixFunction;
import org.la4j.matrix.functor.MatrixProcedure;
import org.la4j.matrix.source.MatrixSource;
import org.la4j.vector.Vector;
import org.la4j.vector.sparse.CompressedVector;

public class CCSMatrix extends AbstractCompressedMatrix implements SparseMatrix {

    private static final long serialVersionUID = 4071505L;

    private static final int MINIMUM_SIZE = 32;

    private double values[];
    private int rowIndices[];
    private int columnPointers[];

    public CCSMatrix() {
        this(0, 0);
    }

    public CCSMatrix(int rows, int columns) {
        this (rows, columns, 0);
    }

    public CCSMatrix(Matrix matrix) {
        this(Matrices.asUnsafeSource(matrix));
    }

    public CCSMatrix(double array[][]) {
        this(Matrices.asArray2DSource(array));
    }

    public CCSMatrix(MatrixSource source) {
        this(source.rows(), source.columns(), 0);

        for (int j = 0; j < columns; j++) {
            columnPointers[j] = cardinality;
            for (int i = 0; i < rows; i++) {
                double value = source.get(i, j);
                if (Math.abs(value) > Matrices.EPS) {

                    if (values.length < cardinality + 1) {
                        growup();
                    }

                    values[cardinality] = value;
                    rowIndices[cardinality] = i;
                    cardinality++;
                }
            }
        }

        columnPointers[columns] = cardinality;
    }


    public CCSMatrix(int rows, int columns, int cardinality) {
        super(Matrices.CCS_FACTORY, rows, columns);

        int alignedSize = align(rows, columns, cardinality);

        this.cardinality = 0;
        this.values = new double[alignedSize];
        this.rowIndices = new int[alignedSize];

        this.columnPointers = new int[columns + 1];
    }

    public CCSMatrix(int rows, int columns, int cardinality, double values[],
                     int rowIndices[], int columnPointers[]) {

        super(Matrices.CCS_FACTORY, rows, columns);

        this.cardinality = cardinality;
        this.values = values;
        this.rowIndices = rowIndices;
        this.columnPointers = columnPointers;
    }

    @Override
    public double get(int i, int j) {

        for (int jj = columnPointers[j]; jj < columnPointers[j + 1]; jj++) {
            if (rowIndices[jj] == i) {
                return values[jj];
            }
        }

        return 0.0;
    }

    @Override
    public void set(int i, int j, double value) {

        for (int jj = columnPointers[j]; jj < columnPointers[j + 1]; jj++) {
            if (rowIndices[jj] == i) {

                if (Math.abs(value) < Matrices.EPS) {
                    remove(j, jj);
                    return;
                } else {
                    values[jj] = value;
                    return;
                }
            }
        }

        insert(i, j, value);
    }

    @Override
    public Vector getColumn(int j) {

        int columnCardinality = columnPointers[j + 1] - columnPointers[j];

        double columnValues[] = new double[columnCardinality];
        int columnIndices[] = new int[columnCardinality];

        System.arraycopy(values, columnPointers[j], columnValues, 0, 
                         columnCardinality);
        System.arraycopy(rowIndices, columnPointers[j], columnIndices, 0, 
                         columnCardinality);

        return new CompressedVector(rows, columnCardinality, columnValues, 
                                    columnIndices);
    }

    @Override
    public Vector getColumn(int j, Factory factory) {
        ensureFactoryIsNotNull(factory);

        Vector result = factory.createVector(rows);

        for (int jj = columnPointers[j]; jj < columnPointers[j + 1]; jj++) {
            result.set(rowIndices[jj], values[jj]);
        }

        return result;
    }

    @Override
    public Vector getRow(int i, Factory factory) {
        ensureFactoryIsNotNull(factory);

        Vector result = factory.createVector(columns);

        int k = 0, jj = 0;
        while (k < cardinality) {
            for (int ii = columnPointers[jj]; ii < columnPointers[jj + 1]; 
                 ii++, k++) {

                if (rowIndices[ii] == i) {
                    result.set(jj, values[ii]);
                }
            }
            jj++;
        }

        return result;
    }

    @Override
    public Matrix copy() {
        double $values[] = new double[align(rows, columns, cardinality)];
        int $rowIndices[] = new int[align(rows, columns, cardinality)];
        int $columnPointers[] = new int[columns + 1];

        System.arraycopy(values, 0, $values, 0, cardinality);
        System.arraycopy(rowIndices, 0, $rowIndices, 0, cardinality);
        System.arraycopy(columnPointers, 0, $columnPointers, 0, columns + 1);

        return new CCSMatrix(rows, columns, cardinality, $values, 
                             $rowIndices, $columnPointers);
    }

    @Override
    public Matrix resize(int rows, int columns) {
        ensureDimensionsAreNotNegative(rows, columns);

        if (this.rows == rows && this.columns == columns) {
            return copy();
        }

        if (this.rows >= rows && this.columns >= columns) {

            // TODO: think about cardinality in align call
            double $values[] = new double[align(rows, columns, cardinality)];
            int $rowIndices[] = new int[align(rows, columns, cardinality)];
            int $columnPointers[] = new int[columns + 1];

            int $cardinality = 0;

            int k = 0, j = 0;
            while (k < cardinality && j < columns) {

                $columnPointers[j] = $cardinality;

                for (int i = columnPointers[j]; i < columnPointers[j + 1] 
                        && rowIndices[i] < rows; i++, k++) {

                    $values[$cardinality] = values[i];
                    $rowIndices[$cardinality] = rowIndices[i];
                    $cardinality++;
                }
                j++;
            }

            $columnPointers[columns] = $cardinality;

            return new CCSMatrix(rows, columns, $cardinality, $values,
                    $rowIndices, $columnPointers);
        }

        if (this.columns < columns) {

            double $values[] = new double[align(rows, columns, cardinality)];
            int $rowIndices[] = new int[align(rows, columns, cardinality)];
            int $columnPointers[] = new int[columns + 1];

            System.arraycopy(values, 0, $values, 0, cardinality);
            System.arraycopy(rowIndices, 0, $rowIndices, 0, cardinality);
            System.arraycopy(columnPointers, 0, $columnPointers, 0, 
                             this.columns + 1);

            for (int i = this.columns; i < columns + 1; i++) {
                $columnPointers[i] = cardinality;
            }

            return new CCSMatrix(rows, columns, cardinality, $values, 
                                 $rowIndices, $columnPointers);
        }

        // TODO: think about cardinality in align call
        double $values[] = new double[align(rows, columns, cardinality)];
        int $rowIndices[] = new int[align(rows, columns, cardinality)];
        int $columnPointers[] = new int[columns + 1];

        System.arraycopy(values, 0, $values, 0, cardinality);
        System.arraycopy(rowIndices, 0, $rowIndices, 0, cardinality);
        System.arraycopy(columnPointers, 0, $columnPointers, 0, 
                         this.columns + 1);

        return new CCSMatrix(rows, columns, cardinality, $values, $rowIndices, 
                             $columnPointers);
    }

    @Override
    public void each(MatrixProcedure procedure) {
        int k = 0, j = 0;
        while (k < cardinality) {
            for (int i = columnPointers[j]; i < columnPointers[j + 1]; 
                 i++, k++) {

                procedure.apply(rowIndices[i], j, values[i]);
            }
            j++;
        }
    }

    @Override
    public void update(int i, int j, MatrixFunction function) {

        for (int jj = columnPointers[j]; jj < columnPointers[j + 1]; jj++) {
            if (rowIndices[jj] == i) {

                double value = function.evaluate(i, j, values[jj]); 

                if (Math.abs(value) < Matrices.EPS) {
                    remove(j, jj);
                    return;
                } else {
                    values[jj] = value;
                    return;
                }
            }
        }

        insert(i, j, function.evaluate(i, j, 0));
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException,
                ClassNotFoundException {

        rows = in.readInt();
        columns = in.readInt();
        cardinality = in.readInt();

        int alignedSize = align(rows, columns, cardinality);

        values = new double[alignedSize];
        rowIndices = new int[alignedSize];
        columnPointers = new int[columns + 1];

        for (int k = 0; k < cardinality; k++) {
            rowIndices[k] = in.readInt();
            int j = in.readInt();
            values[k] = in.readDouble();
            columnPointers[j + 1] = k + 1;
        }
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {

        out.writeInt(rows);
        out.writeInt(columns);
        out.writeInt(cardinality);

        int k = 0, j = 0;
        while (k < cardinality) {
            for (int i = columnPointers[j]; i < columnPointers[j + 1]; 
                 i++, k++) {

                out.writeInt(rowIndices[i]);
                out.writeInt(j);
                out.writeDouble(values[i]);
            }
            j++;
        }
    }

    private void insert(int i, int j, double value) {

        if (Math.abs(value) < Matrices.EPS) {
            return;
        }

        if (values.length < cardinality + 1) {
            growup();
        }

        int position = columnPointers[j];
        while (position < columnPointers[j + 1] && rowIndices[position] < i) {
            position++;
        }

        for (int k = cardinality; k > position; k--) {
            values[k] = values[k - 1];
            rowIndices[k] = rowIndices[k - 1];
        }

        values[position] = value;
        rowIndices[position] = i;

        for (int k = j + 1; k < columns + 1; k++) {
            columnPointers[k]++;
        }

        cardinality++;
    }

    private void remove(int j, int k) {

        cardinality--;

        for (int kk = k; kk < cardinality; kk++) {
            values[kk] = values[kk + 1];
            rowIndices[kk] = rowIndices[kk + 1];
        }

        for (int jj = j + 1; jj < columns + 1; jj++) {
            columnPointers[jj]--;
        }
    }

    private void growup() {

        int newSize = Math.min(rows * columns, (cardinality * 3) / 2 + 1);

        double newValues[] = new double[newSize];
        int newRowIndices[] = new int[newSize];

        System.arraycopy(values, 0, newValues, 0, cardinality);
        System.arraycopy(rowIndices, 0, newRowIndices, 0, cardinality);

        this.values = newValues;
        this.rowIndices = newRowIndices;
    }

    private int align(int rows, int columns, int cardinality) {
        return Math.min(rows * columns, ((cardinality % MINIMUM_SIZE) + 1)
                        * MINIMUM_SIZE);
    }
}
