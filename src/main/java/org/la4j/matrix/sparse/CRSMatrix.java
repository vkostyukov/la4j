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
 * This is a CRS (Compressed Row Storage) matrix class.
 */
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
        this(Matrices.asMatrixSource(matrix));
    }

    public CRSMatrix(double array[][]) {
        this(Matrices.asArray2DSource(array));
    }

    public CRSMatrix(MatrixSource source) {
        this(source.rows(), source.columns(), 0);

        for (int i = 0; i < rows; i++) {
            rowPointers[i] = cardinality;
            for (int j = 0; j < columns; j++) {
                double value = source.get(i, j);
                // if (Math.abs(value) > Matrices.EPS || value < 0.0) {
                if (value != 0.0) {

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
        super(LinearAlgebra.CRS_FACTORY, rows, columns);
        ensureCardinalityIsCorrect(rows, columns, cardinality);

        int alignedSize = align(cardinality);

        this.cardinality = 0;
        this.values = new double[alignedSize];
        this.columnIndices = new int[alignedSize];
        this.rowPointers = new int[rows + 1];
    }

    public CRSMatrix(int rows, int columns, int cardinality, double values[], int columnIndices[], int rowPointers[]) {
        super(LinearAlgebra.CRS_FACTORY, rows, columns);
        ensureCardinalityIsCorrect(rows, columns, cardinality);

        this.cardinality = cardinality;

        this.values = values;
        this.columnIndices = columnIndices;
        this.rowPointers = rowPointers;
    }

    @Override
    public double get(int i, int j) {

        int k = searchForColumnIndex(j, rowPointers[i], rowPointers[i + 1]);

        if (k < rowPointers[i + 1] && columnIndices[k] == j) {
            return values[k];
        }

        return 0.0;
    }

    @Override
    public void set(int i, int j, double value) {

        int k = searchForColumnIndex(j, rowPointers[i], rowPointers[i + 1]);

        if (k < rowPointers[i + 1] && columnIndices[k] == j) {
            // if (Math.abs(value) < Matrices.EPS && value >= 0.0) {
            if (value == 0.0) {
                remove(k, i);
            } else {
                values[k] = value;
            }
        } else {
            insert(k, i, j, value);
        }
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
    public Vector getColumn(int j, Factory factory) {
        ensureFactoryIsNotNull(factory);

        Vector result = factory.createVector(rows);

        int i = 0;
        while (rowPointers[i] < cardinality) {

            int k = searchForColumnIndex(j, rowPointers[i], rowPointers[i + 1]);

            if (k < rowPointers[i + 1] && columnIndices[k] == j) {
                result.set(i, values[k]);
            }

            i++;
        }

        return result;
    }

    @Override
    public Matrix copy() {
        double $values[] = new double[align(cardinality)];
        int $columnIndices[] = new int[align(cardinality)];
        int $rowPointers[] = new int[rows + 1];

        System.arraycopy(values, 0, $values, 0, cardinality);
        System.arraycopy(columnIndices, 0, $columnIndices, 0, cardinality);
        System.arraycopy(rowPointers, 0, $rowPointers, 0, rows + 1);

        return new CRSMatrix(rows, columns, cardinality, $values, 
                             $columnIndices, $rowPointers);
    }

    @Override
    public Matrix resize(int rows, int columns) {
        ensureDimensionsAreCorrect(rows, columns);

        if (this.rows == rows && this.columns == columns) {
            return copy();
        }

        if (this.rows >= rows && this.columns >= columns) {

            double $values[] = new double[align(cardinality)];
            int $columnIndices[] = new int[align(cardinality)];
            int $rowPointers[] = new int[rows + 1];

            int $cardinality = 0;

            int k = 0, i = 0;
            while (k < cardinality && i < rows) {

                $rowPointers[i] = $cardinality;

                for (int j = rowPointers[i]; j < rowPointers[i + 1] 
                        && columnIndices[j] < columns; j++, k++) {

                    $values[$cardinality] = values[j];
                    $columnIndices[$cardinality] = columnIndices[j];
                    $cardinality++;
                }
                i++;
            }

            $rowPointers[rows] = $cardinality;

            return new CRSMatrix(rows, columns, $cardinality, $values,
                                 $columnIndices, $rowPointers);
        }

        if (this.rows < rows) {
            double $values[] = new double[align(cardinality)];
            int $columnIndices[] = new int[align(cardinality)];
            int $rowPointers[] = new int[rows + 1];

            System.arraycopy(values, 0, $values, 0, cardinality);
            System.arraycopy(columnIndices, 0, $columnIndices, 0, cardinality);
            System.arraycopy(rowPointers, 0, $rowPointers, 0, this.rows + 1);


            for (int i = this.rows; i < rows + 1; i++) {
                $rowPointers[i] = cardinality;
            }

            return new CRSMatrix(rows, columns, cardinality, $values, 
                                 $columnIndices, $rowPointers);
        }

        // TODO: think about cardinality in align call
        double $values[] = new double[align(cardinality)];
        int $columnIndices[] = new int[align(cardinality)];
        int $rowPointers[] = new int[rows + 1];

        System.arraycopy(values, 0, $values, 0, cardinality);
        System.arraycopy(columnIndices, 0, $columnIndices, 0, cardinality);
        System.arraycopy(rowPointers, 0, $rowPointers, 0, this.rows + 1);

        return new CRSMatrix(rows, columns, cardinality, $values, 
                $columnIndices, $rowPointers);
    }

    @Override
    public void eachNonZero(MatrixProcedure procedure) {
        int k = 0, i = 0;
        while (k < cardinality) {
            for (int j = rowPointers[i]; j < rowPointers[i + 1]; j++, k++) {
                procedure.apply(i, columnIndices[j], values[j]);
            }
            i++;
        }
    }

    @Override
    public void each(MatrixProcedure procedure) {
        int k = 0;
        for (int i = 0; i < rows; i++) {
            int valuesSoFar = rowPointers[i + 1];
            for (int j = 0; j < columns; j++) {
                if (k < valuesSoFar && j == columnIndices[k]) {
                    procedure.apply(i, j, values[k++]);
                } else {
                    procedure.apply(i, j, 0.0);
                }
            }
        }
    }

    @Override
    public void eachInRow(int i, MatrixProcedure procedure) {
        int k = rowPointers[i];
        int valuesSoFar = rowPointers[i + 1];
        for (int j = 0; j < columns; j++) {
            if (k < valuesSoFar && j == columnIndices[k]) {
                procedure.apply(i, j, values[k++]);
            } else {
                procedure.apply(i, j, 0.0);
            }
        }
    }

    @Override
    public void eachNonZeroInRow(int i, MatrixProcedure procedure) {
        for (int j = rowPointers[i]; j < rowPointers[i + 1]; j++) {
            procedure.apply(i, columnIndices[j], values[j]);
        }
    }

    @Override
    public void update(int i, int j, MatrixFunction function) {

        int k = searchForColumnIndex(j, rowPointers[i], rowPointers[i + 1]);

        if (k < rowPointers[i + 1] && columnIndices[k] == j) {

            double value = function.evaluate(i, j, values[k]);

            // if (Math.abs(value) < Matrices.EPS && value >= 0.0) {
            if (value == 0.0) {
                remove(k, i);
            } else {
                values[k] = value;
            }
        } else {
            insert(k, i, j, function.evaluate(i, j, 0));
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

        int alignedSize = align(cardinality);

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

    private int searchForColumnIndex(int j, int left, int right) {

        if (left == right) {
            return left;
        }

        if (right - left < 8) {

            int jj = left;
            while (jj < right && columnIndices[jj] < j) {
                jj++;
            }

            return jj;
        }

        int p = (left + right) / 2;

        if (columnIndices[p] > j) {
            return searchForColumnIndex(j, left, p);
        } else if (columnIndices[p] < j) {
            return searchForColumnIndex(j, p + 1, right);
        } else {
            return p;
        }
    }

    private void insert(int k, int i, int j, double value) {

        //if (Math.abs(value) < Matrices.EPS && value >= 0.0) {
        if (value == 0.0) {
            return;
        }

        if (values.length < cardinality + 1) {
            growup();
        }

        System.arraycopy(values, k, values, k + 1, cardinality - k);
        System.arraycopy(columnIndices, k, columnIndices, k + 1, 
                         cardinality - k);

//      for (int k = cardinality; k > position; k--) {
//          values[k] = values[k - 1];
//          columnIndices[k] = columnIndices[k - 1];
//      }

        values[k] = value;
        columnIndices[k] = j;

        for (int ii = i + 1; ii < rows + 1; ii++) {
            rowPointers[ii]++;
        }

        cardinality++;
    }

    private void remove(int k, int i) {

        cardinality--;

        System.arraycopy(values, k + 1, values, k, cardinality - k);
        System.arraycopy(columnIndices, k + 1, columnIndices, k, 
                         cardinality - k);

//        for (int kk = k; kk < cardinality; kk++) {
//            values[kk] = values[kk + 1];
//            columnIndices[kk] = columnIndices[kk + 1];
//        }

        for (int ii = i + 1; ii < rows + 1; ii++) {
            rowPointers[ii]--;
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
        int $columnIndices[] = new int[capacity];

        System.arraycopy(values, 0, $values, 0, cardinality);
        System.arraycopy(columnIndices, 0, $columnIndices, 0, cardinality);

        values = $values;
        columnIndices = $columnIndices;
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
    public double maxInRow(int i) {

        double max = Double.NEGATIVE_INFINITY;

        for (int k = rowPointers[i]; k < rowPointers[i + 1]; k++) {
            if (values[k] > max) {
                max = values[k];
            }
        }

        return (max > 0.0) ? max : 0.0;
    }

    @Override
    public double minInRow(int i) {

        double min = Double.POSITIVE_INFINITY;

        for (int k = rowPointers[i]; k < rowPointers[i + 1]; k++) {
            if (values[k] < min) {
                min = values[k];
            }
        }

        return (min < 0.0) ? min : 0.0;
    }
    
    /**
     * Returns a CRSMatrix with the selected rows and columns.
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
        int[] newColumnIndices = new int[newCardinality];
        int[] newRowPointers = new int[newRows + 1];

        newRowPointers[0] = 0;
        int endPtr = 0;
        for (int i = 0; i < newRows; i++) {
            newRowPointers[i + 1] = newRowPointers[i];
            for (int j = 0; j < newCols; j++) {
                double val = get(rowIndices[i], columnIndices[j]);
                if (val != 0.0) {
                    newValues[endPtr] = val;
                    newColumnIndices[endPtr] = j;
                    endPtr++;
                    newRowPointers[i + 1] += 1;

                }
            }
        }

        return new CRSMatrix(newRows, newCols, newCardinality, newValues,
                             newColumnIndices, newRowPointers);
    }
}
