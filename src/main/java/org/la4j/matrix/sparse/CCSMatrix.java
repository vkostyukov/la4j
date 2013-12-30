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
 * Contributor(s): Chandler May
 *                 Maxim Samoylov
 *                 Anveshi Charuvaka
 *                 Clement Skau
 * 
 */

package org.la4j.matrix.sparse;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.la4j.LinearAlgebra;
import org.la4j.factory.Factory;
import org.la4j.matrix.Matrices;
import org.la4j.matrix.Matrix;
import org.la4j.matrix.functor.MatrixFunction;
import org.la4j.matrix.functor.MatrixProcedure;
import org.la4j.matrix.source.MatrixSource;
import org.la4j.vector.Vector;
import org.la4j.vector.sparse.CompressedVector;

/**
 * This is a CCS (Compressed Column Storage) matrix class.
 */
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
        this(Matrices.asMatrixSource(matrix));
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
                //if (Math.abs(value) > Matrices.EPS || value < 0.0) {
                if (value != 0.0) {

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
        super(LinearAlgebra.CCS_FACTORY, rows, columns);
        ensureCardinalityIsCorrect(rows, columns, cardinality);

        int alignedSize = align(cardinality);

        this.cardinality = 0;
        this.values = new double[alignedSize];
        this.rowIndices = new int[alignedSize];

        this.columnPointers = new int[columns + 1];
    }

    public CCSMatrix(int rows, int columns, int cardinality, double values[], int rowIndices[], int columnPointers[]) {
        super(LinearAlgebra.CCS_FACTORY, rows, columns);
        ensureCardinalityIsCorrect(rows, columns, cardinality);

        this.cardinality = cardinality;
        this.values = values;
        this.rowIndices = rowIndices;
        this.columnPointers = columnPointers;
    }

    @Override
    public double get(int i, int j) {

        int k = searchForRowIndex(i, columnPointers[j], columnPointers[j + 1]);

        if (k < columnPointers[j + 1] && rowIndices[k] == i) {
            return values[k];
        }

        return 0.0;
    }

    @Override
    public void set(int i, int j, double value) {

        int k = searchForRowIndex(i, columnPointers[j], columnPointers[j + 1]);

        if (k < columnPointers[j + 1] && rowIndices[k] == i) {
            // if (Math.abs(value) < Matrices.EPS && value >= 0.0) {
            if (value == 0.0) {
                remove(k, j);
            } else {
                values[k] = value;
            }
        } else {
            insert(k, i, j, value);
        }
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

        int j = 0;
        while (columnPointers[j] < cardinality) {

            int k = searchForRowIndex(i, columnPointers[j], 
                                      columnPointers[j + 1]);

            if (k < columnPointers[j + 1] && rowIndices[k] == i) {
                result.set(j, values[k]);
            }

            j++;
        }

        return result;
    }

    @Override
    public Matrix copy() {
        double $values[] = new double[align(cardinality)];
        int $rowIndices[] = new int[align(cardinality)];
        int $columnPointers[] = new int[columns + 1];

        System.arraycopy(values, 0, $values, 0, cardinality);
        System.arraycopy(rowIndices, 0, $rowIndices, 0, cardinality);
        System.arraycopy(columnPointers, 0, $columnPointers, 0, columns + 1);

        return new CCSMatrix(rows, columns, cardinality, $values, 
                             $rowIndices, $columnPointers);
    }

    @Override
    public Matrix resize(int rows, int columns) {
        ensureDimensionsAreCorrect(rows, columns);

        if (this.rows == rows && this.columns == columns) {
            return copy();
        }

        if (this.rows >= rows && this.columns >= columns) {

            // TODO: think about cardinality in align call
            double $values[] = new double[align(cardinality)];
            int $rowIndices[] = new int[align(cardinality)];
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

            double $values[] = new double[align(cardinality)];
            int $rowIndices[] = new int[align(cardinality)];
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
        double $values[] = new double[align(cardinality)];
        int $rowIndices[] = new int[align(cardinality)];
        int $columnPointers[] = new int[columns + 1];

        System.arraycopy(values, 0, $values, 0, cardinality);
        System.arraycopy(rowIndices, 0, $rowIndices, 0, cardinality);
        System.arraycopy(columnPointers, 0, $columnPointers, 0, 
                         this.columns + 1);

        return new CCSMatrix(rows, columns, cardinality, $values, $rowIndices, 
                             $columnPointers);
    }

    @Override
    public void eachNonZero(MatrixProcedure procedure) {
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
    public void each(MatrixProcedure procedure) {
        int k = 0;
        for (int i = 0; i < rows; i++) {
            int valuesSoFar = columnPointers[i + 1];
            for (int j = 0; j < columns; j++) {
                if (k < valuesSoFar && j == rowIndices[k]) {
                    procedure.apply(i, j, values[k++]);
                } else {
                    procedure.apply(i, j, 0.0);
                }
            }
        }

    }

    @Override
    public void eachInColumn(int j, MatrixProcedure procedure) {
        int k = columnPointers[j];
        int valuesSoFar = columnPointers[j + 1];
        for (int i = 0; i < columns; i++) {
            if (k < valuesSoFar && i == rowIndices[k]) {
                procedure.apply(i, j, values[k++]);
            } else {
                procedure.apply(i, j, 0.0);
            }
        }
    }

    @Override
    public void eachNonZeroInColumn(int j, MatrixProcedure procedure) {
        for (int i = columnPointers[j]; i < columnPointers[j + 1]; i++) {
            procedure.apply(rowIndices[i], j, values[i]);
        }
    }

    @Override
    public void update(int i, int j, MatrixFunction function) {

        int k = searchForRowIndex(i, columnPointers[j], columnPointers[j + 1]);

        if (k < columnPointers[j + 1] && rowIndices[k] == i) {

            double value = function.evaluate(i, j, values[k]);
            // if (Math.abs(value) < Matrices.EPS && value >= 0.0) {
            if (value == 0.0) {
                remove(k, j);
            } else {
                values[k] = value;
            }
        } else {
            insert(k, i, j, function.evaluate(i, j, 0.0));
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException,
                ClassNotFoundException {

        rows = in.readInt();
        columns = in.readInt();
        cardinality = in.readInt();

        int alignedSize = align(cardinality);

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

    private int searchForRowIndex(int i, int left, int right) {

        if (left == right) {
            return left;
        }

        if (right - left < 8) {

            int ii = left;
            while (ii < right && rowIndices[ii] < i) {
                ii++;
            }

            return ii;
        }

        int p = (left + right) / 2;

        if (rowIndices[p] > i) {
            return searchForRowIndex(i, left, p);
        } else if (rowIndices[p] < i) {
            return searchForRowIndex(i, p + 1, right);
        } else {
            return p;
        }
    }

    private void insert(int k, int i, int j, double value) {

        // if (Math.abs(value) < Matrices.EPS && value >= 0.0) {
        if (value == 0.0) {
            return;
        }

        if (values.length < cardinality + 1) {
            growup();
        }

        System.arraycopy(values, k, values, k + 1, cardinality - k);
        System.arraycopy(rowIndices, k, rowIndices, k + 1, cardinality - k);

//        for (int k = cardinality; k > position; k--) {
//            values[k] = values[k - 1];
//            rowIndices[k] = rowIndices[k - 1];
//        }

        values[k] = value;
        rowIndices[k] = i;

        for (int jj = j + 1; jj < columns + 1; jj++) {
            columnPointers[jj]++;
        }

        cardinality++;
    }

    private void remove(int k, int j) {

        cardinality--;

        System.arraycopy(values, k + 1, values, k, cardinality - k);
        System.arraycopy(rowIndices, k + 1, rowIndices, k, cardinality - k);
        
//        for (int kk = k; kk < cardinality; kk++) {
//            values[kk] = values[kk + 1];
//            rowIndices[kk] = rowIndices[kk + 1];
//        }

        for (int jj = j + 1; jj < columns + 1; jj++) {
            columnPointers[jj]--;
        }
    }

    private void growup() {

        if (values.length == capacity()) {
            // This should never happen
            throw new IllegalStateException("This matrix can't grow up.");
        }

        int min = (
            (rows != 0 && columns > Integer.MAX_VALUE / rows) ?
            Integer.MAX_VALUE :
            (rows * columns)
            );
        int capacity = Math.min(min, (cardinality * 3) / 2 + 1);

        double $values[] = new double[capacity];
        int $rowIndices[] = new int[capacity];

        System.arraycopy(values, 0, $values, 0, cardinality);
        System.arraycopy(rowIndices, 0, $rowIndices, 0, cardinality);

        values = $values;
        rowIndices = $rowIndices;
    }

    private int align(int cardinality) {
        return ((cardinality / MINIMUM_SIZE) + 1) * MINIMUM_SIZE;
    }

    @Override
    public double max() {

        double max = Double.NEGATIVE_INFINITY;

        for (int i = 0; i < cardinality; i++) {
            if (values[i] > max) {
                max = values[i];
            }
        }

        return (max > 0.0) ? max : 0.0;
    }

    @Override
    public double min() {

        double min = Double.POSITIVE_INFINITY;

        for (int i = 0; i < cardinality; i++) {
            if (values[i] < min) {
                min = values[i];
            }
        }

        return (min < 0.0) ? min : 0.0;
    }

    @Override
    public double maxInColumn(int j) {

        double max = Double.NEGATIVE_INFINITY;

        for (int k = columnPointers[j]; k < columnPointers[j + 1]; k++) {
            if (values[k] > max) {
                max = values[k];
            }
        }

        return (max > 0.0) ? max : 0.0;
    }

    @Override
    public double minInColumn(int j) {

        double min = Double.POSITIVE_INFINITY;

        for (int k = columnPointers[j]; k < columnPointers[j + 1]; k++) {
            if (values[k] < min) {
                min = values[k];
            }
        }

        return (min < 0.0) ? min : 0.0;
    }

    /**
     * Returns a CCSMatrix with the selected rows and columns.
     */
    @Override
    public Matrix select(int[] rowIndices, int[] columnIndices) {
        int newRows = rowIndices.length;
        int newCols = columnIndices.length;

        if (newRows == 0 || newCols == 0) {
            fail("No rows or columns selected.");
        }

        // determine number of non-zero values (cardinality)
        // before allocating space, this is perhaps more efficient
        // than single pass and calling grow() when required.
        int newCardinality = 0;
        for (int i = 0; i < newRows; i++) {
            for (int j = 0; j < newCols; j++) {
                if (get(rowIndices[i], columnIndices[j]) != 0.0) {
                    newCardinality++;
                }
            }
        }

        // Construct the raw structure for the sparse matrix
        double[] newValues = new double[newCardinality];
        int[] newRowIndices = new int[newCardinality];
        int[] newColumnPointers = new int[newCols + 1];

        newColumnPointers[0] = 0;
        int endPtr = 0;
        for (int j = 0; j < newCols; j++) {
            newColumnPointers[j + 1] = newColumnPointers[j];
            for (int i = 0; i < newRows; i++) {
                double val = get(rowIndices[i], columnIndices[j]);
                if (val != 0.0) {
                    newValues[endPtr] = val;
                    newRowIndices[endPtr] = i;
                    endPtr++;
                    newColumnPointers[j + 1]++;
                }
            }
        }

        return new CCSMatrix(newRows, newCols, newCardinality, newValues,
                             newRowIndices, newColumnPointers);
    }
}
