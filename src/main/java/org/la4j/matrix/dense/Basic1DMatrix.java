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
 * Contributor(s): Wajdy Essam
 * 
 */

package org.la4j.matrix.dense;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.la4j.LinearAlgebra;
import org.la4j.matrix.Matrices;
import org.la4j.matrix.Matrix;
import org.la4j.matrix.source.MatrixSource;
import org.la4j.vector.Vector;
import org.la4j.vector.dense.BasicVector;

public class Basic1DMatrix extends AbstractBasicMatrix implements DenseMatrix {

    private static final long serialVersionUID = 4071505L;

    private double self[];

    public Basic1DMatrix() {
        this(0, 0);
    }

    public Basic1DMatrix(Matrix matrix) {
        this(Matrices.asMatrixSource(matrix));
    }

    public Basic1DMatrix(MatrixSource source) {
        this(source.rows(), source.columns());

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                self[i * columns + j] = source.get(i, j);
            }
        }
    }

    public Basic1DMatrix(double array[][]) {
        this(array.length, array[0].length);

        int offset = 0;
        for (int i = 0; i < rows; i++) {
            System.arraycopy(array[i], 0, self, offset, columns);
            offset += columns;
        }
    }

    public Basic1DMatrix(int rows, int columns) {
        this(rows, columns, new double[rows * columns]);
    }

    public Basic1DMatrix(int rows, int columns, double array[]) {
        super(LinearAlgebra.BASIC1D_FACTORY, rows, columns);

        this.self = array;
    }

    @Override
    public double get(int i, int j) {
        return self[i * columns + j];
    }

    @Override
    public void set(int i, int j, double value) {
        self[i * columns + j] = value;
    }

    @Override
    public void swapRows(int i, int j) {
        if (i != j) {
            for (int k = 0; k < columns; k++) {
                double tmp = self[i * columns + k];
                self[i * columns + k] = self[j * columns + k];
                self[j * columns + k] = tmp;
            }
        }
    }

    @Override
    public void swapColumns(int i, int j) {
        if (i != j) {
            for (int k = 0; k < rows; k++) {
                double tmp  = self[k * columns + i];
                self[k * columns + i] = self[k * columns + j];
                self[k * columns + j] = tmp;
            }
        }
    }

    @Override
    public Vector getRow(int i) {
        double result[] = new double[columns];
        System.arraycopy(self, i * columns , result, 0, columns);

        return new BasicVector(result);
    }

    @Override
    public Matrix copy() {
        double $self[] = new double[rows * columns];
        System.arraycopy(self, 0, $self, 0, rows * columns);
        return new Basic1DMatrix(rows, columns, $self);
    }

    @Override
    public Matrix resize(int rows, int columns) {
        ensureDimensionsAreCorrect(rows, columns);

        if (this.rows == rows && this.columns == columns) {
            return copy();
        } 

        if (this.rows < rows && this.columns == columns) {
            double $self[] = new double[rows * columns];
            System.arraycopy(self, 0, $self, 0, this.rows * columns);

            return new Basic1DMatrix(rows, columns, $self);
        }

        double[] $self = new double[rows * columns];

        int columnSize = columns < this.columns ? columns : this.columns;
        int rowSize =  rows < this.rows ? rows : this.rows;

        for (int i = 0; i < rowSize; i++) {
            System.arraycopy(self, i * this.columns, $self, i * columns, 
                             columnSize);
        }

        return new Basic1DMatrix(rows, columns, $self);
    }

    @Override
    public double[][] toArray() {

        double result[][] = new double[rows][columns];

        int offset = 0;
        for (int i = 0; i < rows; i++) {
            System.arraycopy(self, offset, result[i], 0, columns);
            offset += columns;
        }

        return result;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {

        out.writeInt(rows);
        out.writeInt(columns);

        for (int i = 0; i < rows * columns; i++) {
            out.writeDouble(self[i]);
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {

        rows = in.readInt();
        columns = in.readInt();

        self = new double[rows * columns];

        for (int i = 0; i < rows * columns; i++) {
            self[i] = in.readDouble();
        }
    }
}
