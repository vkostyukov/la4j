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
import java.util.Arrays;
import java.util.Random;

import org.la4j.LinearAlgebra;
import org.la4j.matrix.Matrices;
import org.la4j.matrix.Matrix;
import org.la4j.matrix.MatrixFactory;
import org.la4j.matrix.source.MatrixSource;
import org.la4j.vector.Vector;
import org.la4j.vector.dense.BasicVector;

public class Basic1DMatrix extends DenseMatrix  {

    /**
     * Creates a zero {@link Basic1DMatrix} of the given shape:
     * {@code rows} x {@code columns}.
     */
    public static Basic1DMatrix zero(int rows, int columns) {
        return new Basic1DMatrix(rows, columns);
    }

    /**
     * Creates a constant {@link Basic1DMatrix} of the given shape and {@code value}.
     */
    public static Basic1DMatrix constant(int rows, int columns, double constant) {
        double array[] = new double[rows * columns];
        Arrays.fill(array, constant);

        return new Basic1DMatrix(rows, columns, array);
    }

    /**
     * Creates a diagonal {@link Basic1DMatrix} of the given {@code size} whose
     * diagonal elements are equal to {@code diagonal}.
     */
    public static Basic1DMatrix diagonal(int size, double diagonal) {
        double array[] = new double[size * size];

        for (int i = 0; i < size; i++) {
            array[i * size + i] = diagonal;
        }

        return new Basic1DMatrix(size, size, array);
    }

    /**
     * Creates an unit {@link Basic1DMatrix} of the given shape:
     * {@code rows} x {@code columns}.
     */
    public static Basic1DMatrix unit(int rows, int columns) {
        return Basic1DMatrix.constant(rows, columns, 1.0);
    }

    /**
     * Creates an identity {@link Basic1DMatrix} of the given {@code size}.
     */
    public static Basic1DMatrix identity(int size) {
        return Basic1DMatrix.diagonal(size, 1.0);
    }

    /**
     * Creates a random {@link Basic1DMatrix} of the given shape:
     * {@code rows} x {@code columns}.
     */
    public static Basic1DMatrix random(int rows, int columns, Random random) {
        double array[] = new double[rows * columns];

        for (int i = 0; i < rows * columns; i++) {
            array[i] = random.nextDouble();
        }

        return new Basic1DMatrix(rows, columns, array);
    }

    /**
     * Creates a random symmetric {@link Basic1DMatrix} of the given {@code size}.
     */
    public static Basic1DMatrix randomSymmetric(int size, Random random) {
        double array[] = new double[size * size];

        for (int i = 0; i < size; i++) {
            for (int j = i; j < size; j++) {
                double value = random.nextDouble();
                array[i * size + j] = value;
                array[j * size + i] = value;
            }
        }

        return new Basic1DMatrix(size, size, array);
    }

    /**
     * Creates a {@link Basic1DMatrix} of the given 1D {@code array} w/o
     * copying the underlying array.
     */
    public static Basic1DMatrix from1DArray(int rows, int columns, double[] array) {
        return new Basic1DMatrix(rows, columns, array);
    }

    /**
     * Creates a {@link Basic1DMatrix} of the given 2D {@code array} with
     * copying the underlying array.
     */
    public static Basic1DMatrix from2DArray(double[][] array) {
        int rows = array.length;
        int columns = array[0].length;
        double[] array1D = new double[rows * columns];

        int offset = 0;
        for (int i = 0; i < rows; i++) {
            System.arraycopy(array[i], 0, array1D, offset, columns);
            offset += columns;
        }

        return new Basic1DMatrix(rows, columns, array1D);
    }

    /**
     * Creates a block {@link Basic1DMatrix} of the given blocks {@code a},
     * {@code b}, {@code c} and {@code d}.
     */
    public static Basic1DMatrix block(Matrix a, Matrix b, Matrix c, Matrix d) {
        if ((a.rows() != b.rows()) || (a.columns() != c.columns()) ||
            (c.rows() != d.rows()) || (b.columns() != d.columns())) {
            throw new IllegalArgumentException("Sides of blocks are incompatible!");
        }

        int rows = a.rows() + c.rows(), columns = a.columns() + b.columns();
        double array[] = new double[rows * columns];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if ((i < a.rows()) && (j < a.columns())) {
                    array[i * rows + j] = a.get(i, j);
                }
                if ((i < a.rows()) && (j > a.columns())) {
                    array[i * rows + j] = b.get(i, j);
                }
                if ((i > a.rows()) && (j < a.columns())) {
                    array[i * rows + j] = c.get(i, j);
                }
                if ((i > a.rows()) && (j > a.columns())) {
                    array[i * rows + j] = d.get(i, j);
                }
            }
        }

        return new Basic1DMatrix(rows, columns, array);
    }

    private static final long serialVersionUID = 4071505L;

    private double self[];

    public Basic1DMatrix() {
        this(0, 0);
    }

    @Deprecated
    public Basic1DMatrix(Matrix matrix) {
        this(Matrices.asMatrixSource(matrix));
    }

    @Deprecated
    public Basic1DMatrix(MatrixSource source) {
        this(source.rows(), source.columns());

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                self[i * columns + j] = source.get(i, j);
            }
        }
    }

    @Deprecated
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
        ensureIndexesAreInBounds(i, j);
        return self[i * columns + j];
    }

    @Override
    public void set(int i, int j, double value) {
        ensureIndexesAreInBounds(i, j);
        self[i * columns + j] = value;
    }

    @Override
    public void setAll(double value) {
        Arrays.fill(self, value);
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
    public Matrix copyOfShape(int rows, int columns) {
        ensureDimensionsAreCorrect(rows, columns);

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

    @Override
    public <T extends Matrix> T to(MatrixFactory<T> factory) {
        if (factory.outputClass == Basic1DMatrix.class) {
            return factory.outputClass.cast(this);
        }

        return super.to(factory);
    }

    @Override
    public Matrix blankOfShape(int rows, int columns) {
        return Basic1DMatrix.zero(rows, columns);
    }
}
