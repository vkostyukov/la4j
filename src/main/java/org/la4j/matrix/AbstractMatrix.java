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

package org.la4j.matrix;

import org.la4j.decomposition.MatrixDecompositor;
import org.la4j.factory.Factory;
import org.la4j.inversion.MatrixInvertor;
import org.la4j.matrix.functor.AdvancedMatrixPredicate;
import org.la4j.matrix.functor.MatrixFunction;
import org.la4j.matrix.functor.MatrixPredicate;
import org.la4j.matrix.functor.MatrixProcedure;
import org.la4j.vector.Vector;

public abstract class AbstractMatrix implements Matrix {

    protected int rows;
    protected int columns;

    protected Factory factory;

    protected AbstractMatrix(Factory factory) {
        this(factory, 0, 0);
    }

    protected AbstractMatrix(Factory factory, int rows, int columns) {
        this.factory = factory;

        if (rows < 0 || columns < 0) {
            throw new IllegalArgumentException("Wrong matrix dimensions: " 
                                               + rows + "x" + columns);
        }

        this.rows = rows;
        this.columns = columns;
    }

    @Override
    public void assign(double value) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                unsafe_set(i, j, value);
            }
        }
    }

    @Override
    public double get(int i, int j) {
        ensureIndexInRows(i);
        ensureIndexInColumns(j);

        return unsafe_get(i, j);
    }

    @Override
    public void set(int i, int j, double value) {
        ensureIndexInRows(i);
        ensureIndexInColumns(j);

        unsafe_set(i, j, value);
    }

    @Override
    public int rows() {
        return rows;
    }

    @Override
    public int columns() {
        return columns;
    }

    @Override
    public Vector getRow(int i) {
        return getRow(i, factory);
    }

    @Override
    public Vector getRow(int i, Factory factory) {
        ensureFactoryIsNotNull(factory);
        ensureIndexInRows(i);

        Vector result = factory.createVector(columns);

        for (int j = 0; j < columns; j++) {
            result.unsafe_set(j, unsafe_get(i, j));
        }

        return result;
    }

    @Override
    public Vector getColumn(int i) {
        return getColumn(i, factory);
    }

    @Override
    public Vector getColumn(int i, Factory factory) {
        ensureFactoryIsNotNull(factory);
        ensureIndexInColumns(i);

        Vector result = factory.createVector(rows);

        for (int j = 0; j < rows; j++) {
            result.unsafe_set(j, unsafe_get(j, i));
        }

        return result;
    }

    @Override
    public void setRow(int i, Vector row) {
        ensureIndexInRows(i);

        if (row == null) {
            throw new IllegalArgumentException("Row can't be null.");
        }

        if (columns != row.length()) {
            throw new IllegalArgumentException("Wrong row length: " 
                                               + row.length());
        }

        for (int j = 0; j < row.length(); j++) {
            unsafe_set(i, j, row.unsafe_get(j));
        }
    }

    @Override
    public void setColumn(int i, Vector column) {
        ensureIndexInColumns(i);

        if (column == null) {
            throw new IllegalArgumentException("Column can't be null.");
        }

        if (rows != column.length()) {
            throw new IllegalArgumentException("Wrong column length: " 
                                               + column.length());
        }

        for (int j = 0; j < column.length(); j++) {
            unsafe_set(j, i, column.unsafe_get(j));
        }
    }

    @Override
    public void swapRows(int i, int j) {
        ensureIndexInRows(i);
        ensureIndexInRows(j);

        if (i != j) {
            Vector ii = getRow(i);
            Vector jj = getRow(j);

            setRow(i, jj);
            setRow(j, ii);
        }
    }

    @Override
    public void swapColumns(int i, int j) {
        ensureIndexInColumns(i);
        ensureIndexInColumns(j);

        if (i != j) {
            Vector ii = getColumn(i);
            Vector jj = getColumn(j);

            setColumn(i, jj);
            setColumn(j, ii);
        }
    }

    @Override
    public Matrix transpose() {
        return transpose(factory);
    }

    @Override
    public Matrix transpose(Factory factory) {
        ensureFactoryIsNotNull(factory);

        Matrix result = blank(factory);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                result.unsafe_set(j, i, unsafe_get(i, j));
            }
        }

        return result;
    }

    @Override
    public double determinant() {
        return triangularize().product();
    }

    @Override
    public Matrix multiply(double value) {
        return multiply(value, factory);
    }

    @Override
    public Matrix multiply(double value, Factory factory) {
        ensureFactoryIsNotNull(factory);

        Matrix result = blank(factory);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                result.unsafe_set(i, j, unsafe_get(i, j) * value);
            }
        }

        return result;
    }

    @Override
    public Vector multiply(Vector vector) {
        return multiply(vector, factory);
    }

    @Override
    public Vector multiply(Vector vector, Factory factory) {
        ensureFactoryIsNotNull(factory);

        if (vector == null) {
            throw new IllegalArgumentException("Vector can't be null.");
        }

        if (columns != vector.length()) {
            throw new IllegalArgumentException("Wrong vector length: " 
                                                + vector.length());
        }

       return unsafe_multiply(vector, factory);
    }

    @Override
    public Vector unsafe_multiply(Vector vector) {
        return unsafe_multiply(vector, factory);
    }

    @Override
    public Vector unsafe_multiply(Vector vector, Factory factory) {

    	Vector result = factory.createVector(columns);

        for (int i = 0; i < rows; i++) {
            double summand = 0;
            for (int j = 0; j < columns; j++) {
                summand += unsafe_get(i, j) * vector.unsafe_get(j);
            }
            result.unsafe_set(i, summand);
        }

        return result;
    }

	@Override
    public Matrix multiply(Matrix matrix) {
        return multiply(matrix, factory);
    }

    @Override
    public Matrix multiply(Matrix matrix, Factory factory) {
        ensureFactoryIsNotNull(factory);

        if (matrix == null) {
            throw new IllegalArgumentException("Matrix can't be null.");
        }

        if (columns != matrix.rows()) {
            throw new IllegalArgumentException("Wrong matrix dimensions: " 
                                               + matrix.rows() + "x" 
                                               + matrix.columns());
        }

        return unsafe_multiply(matrix, factory);
    }

    @Override
    public Matrix unsafe_multiply(Matrix matrix) {
        return unsafe_multiply(matrix, factory);
    }

    @Override
    public Matrix unsafe_multiply(Matrix matrix, Factory factory) {

        Matrix result = factory.createMatrix(rows, matrix.columns());

        for (int j = 0; j < matrix.columns(); j++) {

            Vector thatColumn = matrix.getColumn(j);

            for (int i = 0; i < rows; i++) {

                Vector thisRow = getRow(i);
                double summand = 0;

                for (int k = 0; k < columns; k++) {
                    summand += thisRow.unsafe_get(k) 
                               * thatColumn.unsafe_get(k);
                }
                result.unsafe_set(i, j, summand);
            }
        }

        return result;
    }

    @Override
    public Matrix subtract(double value) {
        return subtract(value, factory);
    }

    @Override
    public Matrix subtract(double value, Factory factory) {
        return add(-value, factory);
    }

    @Override
    public Matrix subtract(Matrix matrix) {
        return subtract(matrix, factory);
    }

    @Override
    public Matrix subtract(Matrix matrix, Factory factory) {
        return add(matrix.multiply(-1.0), factory);
    }

    @Override
    public Matrix unsafe_subtract(Matrix matrix) {
        return unsafe_subtract(matrix, factory);
    }

    @Override
    public Matrix unsafe_subtract(Matrix matrix, Factory factory) {
        return unsafe_add(matrix.multiply(-1.0), factory);
    }

    @Override
    public Matrix add(double value) {
        return add(value, factory);
    }

    @Override
    public Matrix add(double value, Factory factory) {
        ensureFactoryIsNotNull(factory);

        Matrix result = blank(factory);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                result.unsafe_set(i, j, unsafe_get(i, j) + value);
            }
        }

        return result;
    }

    @Override
    public Matrix add(Matrix matrix) {
        return add(matrix, factory);
    }

    @Override
    public Matrix add(Matrix matrix, Factory factory) {
        ensureFactoryIsNotNull(factory);

        if (matrix == null) {
            throw new IllegalArgumentException("Matrix can't be null.");
        }

        if (rows != matrix.rows() || columns != matrix.columns()) {
            throw new IllegalArgumentException("Wrong matrix dimensions: "
                                               + matrix.rows() + "x" 
                                               + matrix.columns());
        }

        return unsafe_add(matrix, factory); 
    }

    @Override
    public Matrix unsafe_add(Matrix matrix) {
        return unsafe_add(matrix, factory);
    }

    @Override
    public Matrix unsafe_add(Matrix matrix, Factory factory) {

        Matrix result = blank(factory);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                result.unsafe_set(i, j, unsafe_get(i, j) 
                                  + matrix.unsafe_get(i, j));
            }
        }

        return result;
    }

    @Override
    public Matrix div(double value) {
        return div(value, factory);
    }

    @Override
    public Matrix div(double value, Factory factory) {
        return multiply(1.0 / value, factory);
    }

    @Override
    public double trace() {

        double result = 0;

        for (int i = 0; i < rows; i++) {
            result += get(i, i);
        }

        return result;
    }

    @Override
    public double product() {

        double result = 1;

        for (int i = 0; i < rows; i++) {
            result *= get(i, i);
        }

        return result;
    }

    @Override
    public Matrix triangularize() {
        return triangularize(factory);
    }

    @Override
    public Matrix triangularize(Factory factory) {
        ensureFactoryIsNotNull(factory);

        if (is(Matrices.UPPER_TRIANGULAR_MATRIX) 
                || is(Matrices.LOWER_TRIANGULAR_MARTIX)) {

            return copy(factory);
        }

        Matrix result = factory.createMatrix(rows, columns);

        for (int i = 0; i < rows; i++) {
            for (int j = i + 1; j < rows; j++) {

                double c = unsafe_get(j, i) / unsafe_get(i, i);

                for (int k = i; k < columns; k++) {
                    if (k == i) {
                        result.unsafe_set(j, k, 0.0);
                    } else {
                        result.unsafe_set(j, k, unsafe_get(j, k) 
                                          - (unsafe_get(i, k) * c));
                    }
                }
            }
        }

        return result;
    }

    @Override
    public Matrix[] decompose(MatrixDecompositor decompositor) {
        return decompose(decompositor, factory);
    }

    @Override
    public Matrix[] decompose(MatrixDecompositor decompositor, 
            Factory factory) {

        return decompositor.decompose(this, factory);
    }

    @Override
    public Matrix inverse(MatrixInvertor invertor) {
        return inverse(invertor, factory);
    }

    @Override
    public Matrix inverse(MatrixInvertor invertor, Factory factory) {
        return invertor.inverse(this, factory);
    }

    @Override
    public Matrix blank() {
        return blank(factory);
    }

    @Override
    public Matrix blank(Factory factory) {
        ensureFactoryIsNotNull(factory);

        return factory.createMatrix(rows, columns);
    }

    @Override
    public Matrix copy() {
        return copy(factory);
    }

    @Override
    public Matrix copy(Factory factory) {
        ensureFactoryIsNotNull(factory);

        return factory.createMatrix(this);
    }

    @Override
    public void each(MatrixProcedure procedure) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                procedure.apply(i, j, unsafe_get(i, j));
            }
        }
    }

    @Override
    public Matrix transform(MatrixFunction function) {
        return transform(function, factory);
    }

    @Override
    public Matrix transform(MatrixFunction function, Factory factory) {

        Matrix result = blank(factory);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                result.unsafe_set(i, j, function.evaluate(i, j, unsafe_get(i, j)));
            }
        }

        return result;
    }

    @Override
    public boolean is(MatrixPredicate predicate) {

        boolean result = (predicate instanceof AdvancedMatrixPredicate) 
                ? ((AdvancedMatrixPredicate) predicate).test(rows, columns)
                : rows > 0 && columns > 0;

        for (int i = 0; result && i < rows; i++) {
            for (int j = 0; result && j < columns; j++) {
                result = result && predicate.test(i, j, unsafe_get(i, j));
            }
        }

        return result;
    }

    @Override
    public int hashCode() {

        int result = 17;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                long value = (long) unsafe_get(i, j);
                result = 37 * result + (int) (value ^ (value >>> 32));
            }
        }

        return result;
    }

    @Override
    public boolean equals(Object object) {

        if (this == object)
            return true;
        if (object == null)
            return false;

        if (!(object instanceof Matrix)) {
            return false;
        }

        Matrix matrix = (Matrix) object;

        if (rows != matrix.rows() || columns != matrix.columns()) {
            return false;
        }

        boolean result = true;

        for (int i = 0; result && i < rows; i++) {
            for (int j = 0; result && j < columns; j++) {
                result = result
                        & (Math.abs(unsafe_get(i, j) 
                                    - matrix.unsafe_get(i, j)) < EPS);
            }
        }

        return result;
    }

    @Override
    public String toString() {

        int formats[] = new int[columns];

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                long value = (long) unsafe_get(i, j);
                int size = Long.toString(value).length() + PRECISION + 2;
                formats[j] = size > formats[j] ? size : formats[j];
            }
        }

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                sb.append(String.format("%" + Integer.toString(formats[j])
                        + "." + PRECISION + "f", unsafe_get(i, j)));
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    protected void ensureFactoryIsNotNull(Factory factory) {
        if (factory == null) {
            throw new IllegalArgumentException("Factory can't be null.");
        }
    }

    protected void ensureIndexInRows(int i) {
        if (i >= rows || i < 0) {
            throw new IllegalArgumentException("Row index out of bounds: " + i);
        }
    }

    protected void ensureIndexInColumns(int i) {
        if (i >= columns || i < 0) {
            throw new IllegalArgumentException("Column index out of bounds: " 
                                               + i);
        }
    }
}
