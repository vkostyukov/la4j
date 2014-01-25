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
 *                 Jakob Moellers
 *                 Ewald Grusk
 *                 Yuriy Drozd
 *                 Maxim Samoylov
 *                 Anveshi Charuvaka
 *                 Todd Brunhoff
 * 
 */

package org.la4j.matrix;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

import org.la4j.LinearAlgebra;
import org.la4j.decomposition.MatrixDecompositor;
import org.la4j.factory.Factory;
import org.la4j.inversion.MatrixInverter;
import org.la4j.linear.LinearSystemSolver;
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

    protected AbstractMatrix(Factory factory, int rows, int columns) {
        ensureDimensionsAreCorrect(rows, columns);

        this.factory = factory;

        this.rows = rows;
        this.columns = columns;
    }

    @Override
    public void assign(double value) {
        update(Matrices.asConstFunction(value));
    }

    @Override
    public void assignRow(int i, double value) {
        updateRow(i, Matrices.asConstFunction(value));
    }

    @Override
    public void assignColumn(int j, double value) {
        updateColumn(j, Matrices.asConstFunction(value));
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
        ensureArgumentIsNotNull(row, "vector");

        if (columns != row.length()) {
            fail("Wrong vector length: " + row.length() + ". Should be: " + columns + ".");
        }

        for (int j = 0; j < row.length(); j++) {
            set(i, j, row.get(j));
        }
    }

    @Override
    public void setColumn(int j, Vector column) {
        ensureArgumentIsNotNull(column, "vector");

        if (rows != column.length()) {
            fail("Wrong vector length: " + column.length() + ". Should be: " + rows + ".");
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

    public Matrix rotate() {
        return rotate(factory);
    }

    public Matrix rotate(Factory factory) {
        ensureFactoryIsNotNull(factory);

        Matrix result = factory.createMatrix(columns, rows);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                result.set(j, rows - 1 - i, get(i, j));
            }
        }

        return result;
    }

    @Override
    public double determinant() {
        if (rows != columns) {
            throw new IllegalStateException("Can not compute determinant of non-square matrix.");
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

        MatrixDecompositor decompositor = withDecompositor(LinearAlgebra.LU);
        Matrix lup[] = decompositor.decompose(factory);
        // TODO: Why Java doesn't support pattern matching?
        Matrix u = lup[1];
        Matrix p = lup[2];

        double result = u.diagonalProduct();

        // TODO: we can do that in O(n log n)
        //       just google: "counting inversions divide and conqueror"
        int permutations[] = new int[p.rows()];
        for (int i = 0; i < p.rows(); i++) {
            for (int j = 0; j < p.columns(); j++) {
                if (p.get(i, j) > 0.0) {
                    permutations[i] = j;
                    break;
                }
            }
        }

        int sign = 1;
        for (int i = 0; i < permutations.length; i++) {
            for (int j = i + 1; j < permutations.length; j++) {
                if (permutations[j] < permutations[i]) {
                    sign *= -1;
                }
            }
        }

        return sign * result;
    }

    @Override
    public int rank() {
        if (rows == 0 || columns == 0) {
            return 0;
        }

        // TODO: 
        // handle small (1x1, 1xn, nx1, 2x2, 2xn, nx2, 3x3, 3xn, nx3) 
        // matrices without SVD

        MatrixDecompositor decompositor = withDecompositor(LinearAlgebra.SVD);
        Matrix usv[] = decompositor.decompose(factory);
        // TODO: Where is my pattern matching?
        Matrix s = usv[1];
        double tolerance = Math.max(rows, columns) * s.get(0, 0) * Matrices.EPS;

        int result = 0;
        for (int i = 0; i < s.rows(); i++) {
          if (s.get(i, i) > tolerance) {
            result++;
          }
        }

       return result;
    }

    public Matrix power(int n) {
        return power(n, factory);
    }

    public Matrix power(int n, Factory factory) {
        if (n < 0) {
            fail("The exponent should be positive: " + n + ".");
        }

        Matrix result = factory.createIdentityMatrix(rows);
        Matrix that = this;

        while (n > 0) {
            if (n % 2 == 1) {
                result = result.multiply(that);
            }

            n /= 2;
            that = that.multiply(that);
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
        ensureArgumentIsNotNull(vector, "vector");

        if (columns != vector.length()) {
            fail("Wrong vector length: " + vector.length() + ". Should be: " + columns + ".");
        }

        Vector result = factory.createVector(rows);

        for (int i = 0; i < rows; i++) {

            double acc = 0.0;

            for (int j = 0; j < columns; j++) {
                acc += get(i, j) * vector.get(j);
            }

            result.set(i, acc);
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
        ensureArgumentIsNotNull(matrix, "matrix");

        if (columns != matrix.rows()) {
            fail("Wrong matrix dimensions: " + matrix.rows() + "x" + matrix.columns() +
                 ". Should be: " + columns + "x_.");
        }

        Matrix result = factory.createMatrix(rows, matrix.columns());

        for (int j = 0; j < matrix.columns(); j++) {

            Vector column = matrix.getColumn(j);

            for (int i = 0; i < rows; i++) {

                double acc = 0.0;

                for (int k = 0; k < columns; k++) {
                    acc += get(i, k) * column.get(k);
                }

                result.set(i, j, acc);
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
        ensureFactoryIsNotNull(factory);
        ensureArgumentIsNotNull(matrix, "matrix");

        if (rows != matrix.rows() || columns != matrix.columns()) {
            fail("Wrong matrix dimensions: " + matrix.rows() + "x" + matrix.columns() +
                    ". Should be: " + rows + "x" + columns + ".");
        }

        Matrix result = blank(factory);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                result.set(i, j, get(i, j) - matrix.get(i, j));
            }
        }

        return result;
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
        ensureArgumentIsNotNull(matrix, "matrix");

        if (rows != matrix.rows() || columns != matrix.columns()) {
            fail("Wrong matrix dimensions: " + matrix.rows() + "x" + matrix.columns() +
                 ". Should be: " + rows + "x" + columns + ".");
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
    public Matrix divide(double value) {
        return divide(value, factory);
    }

    @Override
    public Matrix divide(double value, Factory factory) {
        return multiply(1.0 / value, factory);
    }

    @Override
    public Matrix kroneckerProduct(Matrix matrix) {
        return kroneckerProduct(matrix, factory);
    }

    @Override
    public Matrix kroneckerProduct(Matrix matrix, Factory factory) {
        ensureFactoryIsNotNull(factory);
        ensureArgumentIsNotNull(matrix, "matrix");

        int n = rows * matrix.rows();
        int m = columns * matrix.columns();

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

        double result = 0.0;

        for (int i = 0; i < rows; i++) {
            result += get(i, i);
        }

        return result;
    }

    @Override
    public double diagonalProduct() {

        BigDecimal result = BigDecimal.ONE;

        for (int i = 0; i < rows; i++) {
            result = result.multiply(BigDecimal.valueOf(get(i, i)));
        }

        return result.setScale(Matrices.ROUND_FACTOR, 
                               RoundingMode.CEILING).doubleValue();
    }

    @Override
    public double product() {
        return fold(Matrices.asProductAccumulator(1));
    }

    @Override
    public Matrix hadamardProduct(Matrix matrix) {
        return hadamardProduct(matrix, factory);
    }

    @Override
    public Matrix hadamardProduct(Matrix matrix, Factory factory) {
        ensureFactoryIsNotNull(factory);
        ensureArgumentIsNotNull(matrix, "matrix");

        if ((columns != matrix.columns()) || (rows != matrix.rows())) {
            fail("Wrong matrix dimensions: " + matrix.rows() + "x" + matrix.columns() +
                 ". Should be: " + rows + "x" + columns + ".");
        }

        Matrix result = factory.createMatrix(rows, columns);
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                result.set(i, j, matrix.get(i, j) * get(i, j));
            }
        }
        
        return result;
    }

    @Override
    public double sum() {
        return fold(Matrices.asSumAccumulator(0));
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
    
    public Matrix shuffle() {
        return shuffle(factory);
    }

    public Matrix shuffle(Factory factory) {
        ensureFactoryIsNotNull(factory);

        Matrix result = copy(factory);

        // Conduct Fisher-Yates shuffle
        Random rnd = new Random();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                int ii = rnd.nextInt(rows - i) + i;
                int jj = rnd.nextInt(columns - j) + j;

                double a = result.get(ii, jj);
                result.set(ii, jj, result.get(i, j));
                result.set(i, j, a);
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

        if (untilRow - fromRow < 0 || untilColumn - fromColumn < 0) {
            fail("Wrong slice range: [" + fromRow + ".." + untilRow + "][" + fromColumn + ".." + untilColumn + "].");
        }

        Matrix result = factory.createMatrix(untilRow - fromRow, untilColumn - fromColumn);

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
    public Matrix sliceBottomRight(int fromRow, int fromColumn, Factory factory) {
        return slice(fromRow, fromColumn, rows, columns, factory);
    }
    
    @Override
    public Matrix select(int[] rowIndices, int[] columnIndices) {
        return select(rowIndices, columnIndices, factory);
    }

    @Override
    public Matrix select(int[] rowIndices, int[] columnIndices, Factory factory) {
        int newRows = rowIndices.length;
        int newCols = columnIndices.length;

        if (newRows == 0 || newCols == 0) {
            fail("No rows or columns selected.");
        }

        Matrix result = factory.createMatrix(newRows, newCols);

        for (int i = 0; i < newRows; i++) {
            for (int j = 0; j < newCols; j++) {
                result.set(i, j, get(rowIndices[i], columnIndices[j]));
            }
        }

        return result;
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
    public void eachInRow(int i,MatrixProcedure procedure) {
        for (int j = 0; j < columns; j++) {
            procedure.apply(i, j, get(i, j));
        }
    }

    @Override
    public void eachInColumn(int j,MatrixProcedure procedure) {
        for (int i = 0; i < rows; i++) {
            procedure.apply(i, j, get(i, j));
        }
    }

    @Override
    public void eachNonZero(MatrixProcedure procedure) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (Math.abs(get(i,j)) > Matrices.EPS) {
                    procedure.apply(i, j, get(i, j));
                }
            }
        }
    }

    @Override
    public void eachNonZeroInRow(int i, MatrixProcedure procedure) {
        for (int j = 0; j < columns; j++) {
            if (Math.abs(get(i, j)) > Matrices.EPS) {
                procedure.apply(i, j, get(i, j));
            }
        }
    }

    @Override
    public void eachNonZeroInColumn(int j, MatrixProcedure procedure) {
        for (int i = 0; i < rows; i++) {
            if (Math.abs(get(i, j)) > Matrices.EPS) {
                procedure.apply(i, j, get(i, j));
            }
        }
    }

    @Override
    public double max() {
        return fold(Matrices.mkMaxAccumulator());
    }

    @Override
    public double min() {
        return fold(Matrices.mkMinAccumulator());
    }

    @Override
    public double maxInRow(int i) {
        return foldRow(i, Matrices.mkMaxAccumulator());
    }

    @Override
    public double minInRow(int i) {
        return foldRow(i, Matrices.mkMinAccumulator());
    }

    @Override
    public double maxInColumn(int j) {
        return foldColumn(j, Matrices.mkMaxAccumulator());
    }

    @Override
    public double minInColumn(int j) {
        return foldColumn(j, Matrices.mkMinAccumulator());
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
    public Matrix transform(int i, int j, MatrixFunction function, Factory factory) {

        Matrix result = copy(factory);
        result.set(i, j, function.evaluate(i, j, result.get(i, j)));

        return result;
    }

    @Override
    public Matrix transformRow(int i, MatrixFunction function) {
        return transformRow(i, function, factory);
    }

    @Override
    public Matrix transformRow(int i, MatrixFunction function, Factory factory) {

        Matrix result = copy(factory);

        for (int j = 0; j < columns; j++) {
            result.set(i, j, function.evaluate(i, j, result.get(i, j)));
        }

        return result;
    }

    @Override
    public Matrix transformColumn(int j, MatrixFunction function) {
        return transformColumn(j, function, factory);
    }

    @Override
    public Matrix transformColumn(int j, MatrixFunction function, Factory factory) {

        Matrix result = copy(factory);

        for (int i = 0; i < rows; i++) {
            result.set(i, j, function.evaluate(i, j, result.get(i, j)));
        }

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
    public void updateRow(int i, MatrixFunction function) {
        for (int j = 0; j < columns; j++) {
            update(i, j, function);
        }
    }

    @Override
    public void updateColumn(int j, MatrixFunction function) {
        for (int i = 0; i < rows; i++) {
            update(i, j, function);
        }
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
    public Vector foldRows(MatrixAccumulator accumulator) {

        Vector result = factory.createVector(rows);

        for (int i = 0; i < rows; i++) {
          result.set(i, foldRow(i, accumulator));
        }

        return result;
    }

    @Override
    public double foldColumn(int j, MatrixAccumulator accumulator) {

        for (int i = 0; i < rows; i++) {
            accumulator.update(i, j, get(i, j));
        }

        return accumulator.accumulate();
    }

    @Override
    public Vector foldColumns(MatrixAccumulator accumulator) {

        Vector result = factory.createVector(columns);

        for (int i = 0; i < columns; i++) {
          result.set(i, foldColumn(i, accumulator));
        }

        return result;
    }

    @Override
    public boolean is(MatrixPredicate predicate) {

        boolean result = predicate.test(rows, columns);

        for (int i = 0; result && i < rows; i++) {
            for (int j = 0; result && j < columns; j++) {
                result = predicate.test(i, j, get(i, j));
            }
        }

        return result;
    }

    @Override
    public boolean is(AdvancedMatrixPredicate predicate) {
        return predicate.test(this);
    }

    @Override
    public boolean non(MatrixPredicate predicate) {
        return !is(predicate);
    }

    @Override
    public boolean non(AdvancedMatrixPredicate predicate) {
        return !is(predicate);
    }

    @Override
    public Vector toRowVector() {
        return toRowVector(factory);
    }

    @Override
    public Vector toRowVector(Factory factory) {
        return getRow(0, factory);
    }

    @Override
    public Vector toColumnVector() {
        return toColumnVector(factory);
    }

    @Override
    public Vector toColumnVector(Factory factory) {
        return getColumn(0, factory);
    }

    @Override
    public LinearSystemSolver withSolver(LinearAlgebra.SolverFactory factory) {
        return factory.create(this);
    }

    @Override
    public MatrixInverter withInverter(LinearAlgebra.InverterFactory factory) {
        return factory.create(this);
    }

    @Override
    public MatrixDecompositor withDecompositor(LinearAlgebra.DecompositorFactory factory) {
        return factory.create(this);
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

        if (this == object) {
            return true;
        }
        if (object == null) {
            return false;
        }

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

                result = (a == b) || (diff < Matrices.EPS || diff / Math.max(Math.abs(a), Math.abs(b)) < Matrices.EPS);
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
                double value = get(i, j);
                int size = String.valueOf((long) value).length() 
                           + precision + (value < 0 && value > -1.0 ? 1 : 0) + 2;
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

    protected  void ensureFactoryIsNotNull(Factory factory) {
        ensureArgumentIsNotNull(factory, "factory");
    }

    protected void ensureArgumentIsNotNull(Object argument, String name) {
        if (argument == null) {
            fail("Bad argument: \"" + name + "\" is 'null'.");
        }
    }

    protected void ensureDimensionsAreCorrect(int rows, int columns) {
        if (rows < 0 || columns < 0) {
            fail("Wrong matrix dimensions: " + rows + "x" + columns);
        }
        if (rows == Integer.MAX_VALUE || columns == Integer.MAX_VALUE) {
            fail("Wrong matrix dimensions: use 'Integer.MAX_VALUE - 1' instead.");
        }
    }

    protected void fail(String message) {
        throw new IllegalArgumentException(message);
    }
}
