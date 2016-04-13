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

package org.la4j.matrix.dense;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Random;

import org.la4j.Matrices;
import org.la4j.Matrix;
import org.la4j.matrix.DenseMatrix;
import org.la4j.matrix.MatrixFactory;
import org.la4j.Vector;
import org.la4j.vector.dense.BasicVector;

public class Basic2DMatrix extends DenseMatrix {

    private static final byte MATRIX_TAG = (byte) 0x10;

    private double[][] self;

    public Basic2DMatrix() {
        this(0, 0);
    }

    public Basic2DMatrix(int rows, int columns) {
        this(new double[rows][columns]);
    }

    public Basic2DMatrix(double[][] array) {
        super(array.length, array.length == 0 ? 0: array[0].length);
        this.self = array;
    }

    /**
     * Creates a zero {@link Basic2DMatrix} of the given shape:
     * {@code rows} x {@code columns}.
     */
    public static Basic2DMatrix zero(int rows, int columns) {
        return new Basic2DMatrix(rows, columns);
    }

    /**
     * Creates a constant {@link Basic2DMatrix} of the given shape and {@code value}.
     */
    public static Basic2DMatrix constant(int rows, int columns, double constant) {
        double[][] array = new double[rows][columns];

        for (int i = 0; i < rows; i++) {
            Arrays.fill(array[i], constant);
        }

        return new Basic2DMatrix(array);
    }

    /**
     * Creates a diagonal {@link Basic2DMatrix} of the given {@code size} whose
     * diagonal elements are equal to {@code diagonal}.
     */
    public static Basic2DMatrix diagonal(int size, double diagonal) {
        double[][] array = new double[size][size];

        for (int i = 0; i < size; i++) {
            array[i][i] = diagonal;
        }

        return new Basic2DMatrix(array);
    }

    /**
     * Creates an unit {@link Basic2DMatrix} of the given shape:
     * {@code rows} x {@code columns}.
     */
    public static Basic2DMatrix unit(int rows, int columns) {
        return Basic2DMatrix.constant(rows, columns, 1.0);
    }

    /**
     * Creates an identity {@link Basic2DMatrix} of the given {@code size}.
     */
    public static Basic2DMatrix identity(int size) {
        return Basic2DMatrix.diagonal(size, 1.0);
    }

    /**
     * Creates a random {@link Basic2DMatrix} of the given shape:
     * {@code rows} x {@code columns}.
     */
    public static Basic2DMatrix random(int rows, int columns, Random random) {
        double[][] array = new double[rows][columns];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                array[i][j] = random.nextDouble();
            }
        }

        return new Basic2DMatrix(array);
    }

    /**
     * Creates a random symmetric {@link Basic2DMatrix} of the given {@code size}.
     */
    public static Basic2DMatrix randomSymmetric(int size, Random random) {
        double[][] array = new double[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = i; j < size; j++) {
                double value = random.nextDouble();
                array[i][j] = value;
                array[j][i] = value;
            }
        }

        return new Basic2DMatrix(array);
    }

    /**
     * Creates a {@link Basic2DMatrix} of the given 1D {@code array} with
     * copying the underlying array.
     */
    public static Basic2DMatrix from1DArray(int rows, int columns, double[] array) {
        double[][] array2D = new double[rows][columns];

        for (int i = 0; i < rows; i++) {
            System.arraycopy(array, i * columns, array2D[i], 0, columns);
        }

        return new Basic2DMatrix(array2D);
    }

    /**
     * Creates a {@link Basic2DMatrix} of the given 2D {@code array} w/o
     * copying the underlying array.
     */
    public static Basic2DMatrix from2DArray(double[][] array) {
        return new Basic2DMatrix(array);
    }

    /**
     * Creates a block {@link Basic2DMatrix} of the given blocks {@code a},
     * {@code b}, {@code c} and {@code d}.
     */
    public static Basic2DMatrix block(Matrix a, Matrix b, Matrix c, Matrix d) {
        if ((a.rows() != b.rows()) || (a.columns() != c.columns()) ||
            (c.rows() != d.rows()) || (b.columns() != d.columns())) {
            throw new IllegalArgumentException("Sides of blocks are incompatible!");
        }

        int rows = a.rows() + c.rows();
        int columns = a.columns() + b.columns();
        double[][] array = new double[rows][columns];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if ((i < a.rows()) && (j < a.columns())) {
                    array[i][j] = a.get(i, j);
                }
                if ((i < a.rows()) && (j > a.columns())) {
                    array[i][j] = b.get(i, j);
                }
                if ((i > a.rows()) && (j < a.columns())) {
                    array[i][j] = c.get(i, j);
                }
                if ((i > a.rows()) && (j > a.columns())) {
                    array[i][j] = d.get(i, j);
                }
            }
        }

        return new Basic2DMatrix(array);
    }

    /**
     * Decodes {@link Basic2DMatrix} from the given byte {@code array}.
     *
     * @param array the byte array representing a matrix
     *
     * @return a decoded matrix
     */
    public static Basic2DMatrix fromBinary(byte[] array) {
        ByteBuffer buffer = ByteBuffer.wrap(array);

        if (buffer.get() != MATRIX_TAG) {
            throw new IllegalArgumentException("Can not decode Basic2DMatrix from the given byte array.");
        }

        int rows = buffer.getInt();
        int columns = buffer.getInt();
        double[][] values = new double[rows][columns];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                values[i][j] = buffer.getDouble();
            }
        }

        return new Basic2DMatrix(values);
    }

    /**
     * Parses {@link Basic2DMatrix} from the given CSV string.
     *
     * @param csv the CSV string representing a matrix
     *
     * @return a parsed matrix
     */
    public static Basic2DMatrix fromCSV(String csv) {
        return Matrix.fromCSV(csv).to(Matrices.BASIC_2D);
    }

    /**
     * Parses {@link Basic2DMatrix} from the given Matrix Market string.
     *
     * @param mm the string in Matrix Market format
     *
     * @return a parsed matrix
     */
    public static Basic2DMatrix fromMatrixMarket(String mm) {
        return Matrix.fromMatrixMarket(mm).to(Matrices.BASIC_2D);
    }

    @Override
    public double get(int i, int j) {
        return self[i][j];
    }

    @Override
    public void set(int i, int j, double value) {
        self[i][j] = value;
    }

    @Override
    public void setAll(double value) {
        for (int i = 0; i < rows; i++) {
            Arrays.fill(self[i], value);
        }
    }

    @Override
    public void swapRows(int i, int j) {
        if (i != j) {
            double[] tmp = self[i];
            self[i] = self[j];
            self[j] = tmp;
        }
    }

    @Override
    public void swapColumns(int i, int j) {
        if (i != j) {
            for (int ii = 0; ii < rows; ii++) {
                double tmp = self[ii][i];
                self[ii][i] = self[ii][j];
                self[ii][j] = tmp;
            }
        }
    }

    @Override
    public Vector getRow(int i) {
        double[] result = new double[columns];
        System.arraycopy(self[i], 0, result, 0, columns);

        return new BasicVector(result);
    }

    @Override
    public Matrix copyOfShape(int rows, int columns) {
        ensureDimensionsAreCorrect(rows, columns);

        double[][] $self = new double[rows][columns];
        for (int i = 0; i < Math.min(this.rows, rows); i++) {
            System.arraycopy(self[i], 0, $self[i], 0, Math.min(this.columns, columns));
        }

        return new Basic2DMatrix($self);
    }

    @Override
    public double[][] toArray() {
        double[][] result = new double[rows][columns];

        for (int i = 0; i < rows; i++) {
            System.arraycopy(self[i], 0, result[i], 0, columns);
        }

        return result;
    }

    @Override
    public <T extends Matrix> T to(MatrixFactory<T> factory) {
        if (factory.outputClass == Basic2DMatrix.class) {
            return factory.outputClass.cast(this);
        }

        return super.to(factory);
    }

    @Override
    public Matrix blankOfShape(int rows, int columns) {
        return Basic2DMatrix.zero(rows, columns);
    }

    @Override
    public byte[] toBinary() {
        int size = 1 +                  // 1 byte: class tag
                   4 +                  // 4 bytes: rows
                   4 +                  // 4 bytes: columns
                  (8 * rows * columns); // 8 * rows * columns bytes: values

        ByteBuffer buffer = ByteBuffer.allocate(size);

        buffer.put(MATRIX_TAG);
        buffer.putInt(rows);
        buffer.putInt(columns);
        for (int i = 0; i < rows; i++) {
            for (double value : self[i]) {
                buffer.putDouble(value);
            }
        }

        return buffer.array();
    }
}
