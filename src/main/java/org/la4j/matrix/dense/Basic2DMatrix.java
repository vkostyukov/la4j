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

public class Basic2DMatrix extends DenseMatrix {

    private static final long serialVersionUID = 4071505L;

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
        double array[][] = new double[rows][columns];

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
        double array[][] = new double[size][size];

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
        double array[][] = new double[rows][columns];

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
        double array[][] = new double[size][size];

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

        int rows = a.rows() + c.rows(), columns = a.columns() + b.columns();
        double array[][] = new double[rows][columns];

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

    private double self[][];

    public Basic2DMatrix() {
        this(0, 0);
    }

    @Deprecated
    public Basic2DMatrix(Matrix matrix) {
        this(Matrices.asMatrixSource(matrix));
    }

    @Deprecated
    public Basic2DMatrix(MatrixSource source) {
        this(source.rows(), source.columns());

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
               self[i][j] = source.get(i, j);
            }
        }
    }

    public Basic2DMatrix(int rows, int columns) {
        this(new double[rows][columns]);
    }

    @Deprecated
    public Basic2DMatrix(int rows, int columns, double array[]) {
        this(rows, columns);

        // TODO:
        // We suppose that 'array.length = rows * columns' for now.
        // Probably, we should check this explicitly.

        for (int i = 0; i < rows; i++) {
            System.arraycopy(array, i * columns, self[i], 0, columns);
        }
    }

    public Basic2DMatrix(double array[][]) {
        super(LinearAlgebra.BASIC2D_FACTORY, array.length, array.length == 0 ? 0: array[0].length);
        this.self = array;
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
            double tmp[] = self[i];
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
        double result[] = new double[columns];
        System.arraycopy(self[i], 0, result, 0, columns);

        return new BasicVector(result);
    }

    @Override
    public Matrix copyOfShape(int rows, int columns) {
        ensureDimensionsAreCorrect(rows, columns);

        double $self[][] = new double[rows][columns];
        for (int i = 0; i < Math.min(this.rows, rows); i++) {
            System.arraycopy(self[i], 0, $self[i], 0, Math.min(this.columns, columns));
        }

        return new Basic2DMatrix($self);
    }

    @Override
    public double[][] toArray() {
        double result[][] = new double[rows][columns];

        for (int i = 0; i < rows; i++) {
            System.arraycopy(self[i], 0, result[i], 0, columns);
        }

        return result;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(rows);
        out.writeInt(columns);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                out.writeDouble(self[i][j]);
            }
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {

        rows = in.readInt();
        columns = in.readInt();

        self = new double[rows][columns];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                self[i][j] = in.readDouble();
            }
        }
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
}
