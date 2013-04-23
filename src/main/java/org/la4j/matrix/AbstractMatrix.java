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
 * Contributor(s): Evgenia Krivova
 *                 Pavel Kalaidin
 * 
 */

package org.la4j.matrix;

import org.la4j.decomposition.MatrixDecompositor;
import org.la4j.factory.Factory;
import org.la4j.inversion.MatrixInvertor;
import org.la4j.matrix.functor.AdvancedMatrixPredicate;
import org.la4j.matrix.functor.MatrixAccumulator;
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
        ensureDimensionsAreNotNegative(rows, columns);

        this.factory = factory;

        this.rows = rows;
        this.columns = columns;
    }

    @Override
    public void assign(double value) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                set(i, j, value);
            }
        }
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
    
        Vector result = factory.createVector(columns);

        for (int j = 0; j < columns; j++) {
            result.set(j, get(i, j));
        }

        return result;
    }

    @Override
    public Vector getColumn(int j) {
        return getColumn(j, factory);
    }

    @Override
    public Vector getColumn(int j, Factory factory) {
        ensureFactoryIsNotNull(factory);

        Vector result = factory.createVector(rows);

        for (int i = 0; i < rows; i++) {
            result.set(i, get(i, j));
        }

        return result;
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

        for (int j = 0; j < row.length(); j++) {
            set(i, j, row.get(j));
        }
    }

    @Override
    public void setColumn(int j, Vector column) {

        if (column == null) {
            throw new IllegalArgumentException("Column can't be null.");
        }

        if (rows != column.length()) {
            throw new IllegalArgumentException("Wrong column length: " 
                                               + column.length());
        }

        for (int i = 0; i < column.length(); i++) {
            set(i, j, column.get(i));
        }
    }

    @Override
    public void swapRows(int i, int j) {
        if (i != j) {
            Vector ii = getRow(i);
            Vector jj = getRow(j);

            setRow(i, jj);
            setRow(j, ii);
        }
    }

    @Override
    public void swapColumns(int i, int j) {
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

        Matrix result = factory.createMatrix(columns, rows);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                result.set(j, i, get(i, j));
            }
        }

        return result;
    }

    @Override
    public double determinant() {
        if (rows != columns) {
            throw new IllegalStateException("Can't compute determinant for " +
                                            "non-square matrix.");
        }

        if (rows == 0) {
            return 0.0;
        } else if (rows == 1) {
            return get(0, 0);
        } else if (rows == 2) {
            return get(0, 0) * get(1, 1) -
                   get(0, 1) * get(1, 0);
        } else if (rows == 3) {
            return get(0, 0) * get(1, 1) * get(2, 2) +
                   get(0, 1) * get(1, 2) * get(2, 0) +
                   get(0, 2) * get(1, 0) * get(2, 1) -
                   get(0, 2) * get(1, 1) * get(2, 0) -
                   get(0, 1) * get(1, 0) * get(2, 2) -
                   get(0, 0) * get(1, 2) * get(2, 1);
        }

        return triangularize().product();
    }

    @Override
    public int rank() {

        if ((columns == 0) || (rows == 0)) {
            return 0;
        }

        int x = 0; 
        int y = 0;

        int endi = (columns > rows)? rows : columns;

        for(int i = 0; i < endi; i++) {
            if (Math.abs(get(i, i)) <= Matrices.EPS) {
                boolean c = false;
                for (int k = i; k < rows; k++) {
                    for (int l = i; l < columns; l++) {
                        if (Math.abs(get(k, l)) > Matrices.EPS) {
                            y = k;
                            x = l;
                            c = true;
                            break;
                        }         
                    }

                    if (c) {
                        break;
                    }
                }

                if (!c) {
                    break;
                }

                if (i != y) {
                    swapRows(i, y);
                }

                if (i != x) {
                    swapColumns(i, x);
                }
            }

            for (x = i; x < columns; x++) {
                // TODO: use update() here
                set(i, x, get(i, x) / get(i, i));
            }

            for (y = i + 1; y < rows; y++) {
                for (x = i; x < columns; x++) {
                    // TODO: use update() here
                    set(y, x, get(y, x) - get(i, x) * get(y, i));
                }
            }

            for (x = i + 1; x < columns; x++) {
                for (y = i; y < rows; y++) {
                    // TODO: use update here
                    set(y, x, get(y, x) - get(y, i) * get(i, x));
                }
            }
        }

        int result = 0;

        for (int i = 0; i < endi; i++) {
            if (Math.abs(get(i, i)) <= Matrices.EPS) {
                break;
            }

            result++;
        }

        return result;
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
                result.set(i, j, get(i, j) * value);
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

        Vector result = factory.createVector(rows);

        for (int i = 0; i < rows; i++) {
            double summand = 0;
            for (int j = 0; j < columns; j++) {
                summand += get(i, j) * vector.get(j);
            }
            result.set(i, summand);
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

        Matrix result = factory.createMatrix(rows, matrix.columns());

        for (int j = 0; j < matrix.columns(); j++) {

            Vector thatColumn = matrix.getColumn(j);

            for (int i = 0; i < rows; i++) {

                Vector thisRow = getRow(i);
                double summand = 0;

                for (int k = 0; k < columns; k++) {
                    summand += thisRow.get(k) * thatColumn.get(k);
                }
                result.set(i, j, summand);
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
    public Matrix add(double value) {
        return add(value, factory);
    }

    @Override
    public Matrix add(double value, Factory factory) {
        ensureFactoryIsNotNull(factory);

        Matrix result = blank(factory);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                result.set(i, j, get(i, j) + value);
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

        Matrix result = blank(factory);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                result.set(i, j, get(i, j) + matrix.get(i, j));
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
    public Matrix kronecker(Matrix matrix) {
        return kronecker(matrix, factory);
    }

    @Override
    public Matrix kronecker(Matrix matrix, Factory factory) {
        ensureFactoryIsNotNull(factory);

        if (matrix == null) {
            throw new IllegalArgumentException("Matrix can't be null.");
        }

        int n = rows() * matrix.rows();
        int m = columns() * matrix.columns();

        Matrix result = factory.createMatrix(n, m);

        int p = matrix.rows();
        int q = matrix.columns();

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                result.set(i, j, get(i / p, j / q) * matrix.get(i % p, j % q));
            }
        }

        return result;
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

                double c = get(j, i) / get(i, i);

                for (int k = i; k < columns; k++) {
                    if (k == i) {
                        result.set(j, k, 0.0);
                    } else {
                        result.set(j, k, get(j, k) - (get(i, k) * c));
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
    public Matrix resize(int rows, int columns) {
        return resize(rows, columns, factory);
    }

    @Override
    public Matrix resizeRows(int rows) {
        return resize(rows, columns, factory);
    }

    @Override
    public Matrix resizeRows(int rows, Factory factory) {
        return resize(rows, columns, factory);
    }

    @Override
    public Matrix resizeColumns(int columns) {
        return resize(rows, columns, factory);
    }

    @Override
    public Matrix resizeColumns(int columns, Factory factory) {
        return resize(rows, columns, factory);
    }

    @Override
    public Matrix resize(int rows, int columns, Factory factory) {
        ensureFactoryIsNotNull(factory);

        Matrix result = factory.createMatrix(rows, columns);

        for (int i = 0; i < Math.min(rows, this.rows); i++) {
            for (int j = 0; j < Math.min(columns, this.columns); j++) {
                result.set(i, j, get(i, j));
            }
        }

        return result;
    }

    @Override
    public Matrix slice(int fromRow, int fromColumn, int untilRow,
            int untilColumn) {

        return slice(fromRow, fromColumn, untilRow, untilColumn, factory);
    }

    @Override
    public Matrix slice(int fromRow, int fromColumn, int untilRow,
            int untilColumn, Factory factory) {

        ensureFactoryIsNotNull(factory);

        Matrix result = factory.createMatrix(untilRow - fromRow, 
                                             untilColumn - fromColumn);

        for (int i = fromRow; i < untilRow; i++) {
            for (int j = fromColumn; j < untilColumn; j++) {
                result.set(i - fromRow, j - fromColumn, get(i, j));
            }
        }

        return result;
    }

    @Override
    public Matrix sliceTopLeft(int untilRow, int untilColumn) {
        return slice(0, 0, untilRow, untilColumn, factory);
    }

    @Override
    public Matrix sliceTopLeft(int untilRow, int untilColumn, Factory factory) {
        return slice(0, 0, untilRow, untilColumn, factory);
    }

    @Override
    public Matrix sliceBottomRight(int fromRow, int fromColumn) {
        return slice(fromRow, fromColumn, rows, columns, factory);
    }

    @Override
    public Matrix sliceBottomRight(int fromRow, int fromColumn, Factory fac) {
        return slice(fromRow, fromColumn, rows, columns, factory);
    }

    @Override
    public Factory factory() {
        return factory;
    }

    @Override
    public void each(MatrixProcedure procedure) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                procedure.apply(i, j, get(i, j));
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
                result.set(i, j, function.evaluate(i, j, get(i, j)));
            }
        }

        return result;
    }

    @Override
    public Matrix transform(int i, int j, MatrixFunction function) {
        return transform(i, j, function, factory);
    }

    @Override
    public Matrix transform(int i, int j, MatrixFunction function,
            Factory factory) {

        Matrix result = copy(factory);
        result.set(i, j, function.evaluate(i, j, result.get(i, j)));

        return result;
    }

    @Override
    public void update(MatrixFunction function) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                set(i, j, function.evaluate(i, j, get(i, j)));
            }
        }
    }

    @Override
    public void update(int i, int j, MatrixFunction function) {
        set(i, j, function.evaluate(i, j, get(i, j)));
    }

    @Override
    public double fold(MatrixAccumulator accumulator) {

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                accumulator.update(i, j, get(i, j));
            }
        }

        return accumulator.accumulate();
    }

    @Override
    public double foldRow(int i, MatrixAccumulator accumulator) {

        for (int j = 0; j < columns; j++) {
            accumulator.update(i, j, get(i, j));
        }

        return accumulator.accumulate();
    }

    @Override
    public double foldColumn(int j, MatrixAccumulator accumulator) {

        for (int i = 0; i < rows; i++) {
            accumulator.update(i, j, get(i, j));
        }

        return accumulator.accumulate();
    }

    @Override
    public boolean is(MatrixPredicate predicate) {

        boolean result = (predicate instanceof AdvancedMatrixPredicate) 
                ? ((AdvancedMatrixPredicate) predicate).test(rows, columns)
                : rows > 0 && columns > 0;

        for (int i = 0; result && i < rows; i++) {
            for (int j = 0; result && j < columns; j++) {
                result = result && predicate.test(i, j, get(i, j));
            }
        }

        return result;
    }

    @Override
    public Matrix unsafe() {
        return this;
    }

    @Override
    public int hashCode() {

        int result = 17;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                long value = (long) get(i, j);
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
                double a = get(i, j);
                double b = matrix.get(i, j);

                double diff = Math.abs(a - b);

                result = result && (a == b) ? true : 
                         diff < Matrices.EPS ? true :
                         diff / Math.max(Math.abs(a), Math.abs(b)) 
                         < Matrices.EPS;
            }
        }

        return result;
    }

    @Override
    public String toString() {

        final int precision = 3; 

        int formats[] = new int[columns];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                long value = (long) get(i, j);
                int size = Long.toString(value).length() + precision + 2;
                formats[j] = size > formats[j] ? size : formats[j];
            }
        }

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                sb.append(String.format("%" + Integer.toString(formats[j])
                        + "." + precision + "f", get(i, j)));
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

    protected void ensureDimensionsAreNotNegative(int rows, int columns) {
        if (rows < 0 || columns < 0) {
            throw new IllegalArgumentException("Wrong matrix dimensions: " 
                                               + rows + "x" + columns);
        }
    }
}
