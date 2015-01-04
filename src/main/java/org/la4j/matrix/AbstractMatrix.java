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
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Random;

import org.la4j.LinearAlgebra;
import org.la4j.decomposition.MatrixDecompositor;
import org.la4j.factory.Factory;
import org.la4j.inversion.MatrixInverter;
import org.la4j.iterator.ColumnMajorMatrixIterator;
import org.la4j.iterator.MatrixIterator;
import org.la4j.iterator.RowMajorMatrixIterator;
import org.la4j.iterator.VectorIterator;
import org.la4j.linear.LinearSystemSolver;
import org.la4j.matrix.dense.DenseMatrix;
import org.la4j.matrix.functor.AdvancedMatrixPredicate;
import org.la4j.matrix.functor.MatrixAccumulator;
import org.la4j.matrix.functor.MatrixFunction;
import org.la4j.matrix.functor.MatrixPredicate;
import org.la4j.matrix.functor.MatrixProcedure;
import org.la4j.matrix.sparse.ColumnMajorSparseMatrix;
import org.la4j.matrix.sparse.RowMajorSparseMatrix;
import org.la4j.matrix.sparse.SparseMatrix;
import org.la4j.vector.Vector;
import org.la4j.vector.Vectors;
import org.la4j.vector.functor.VectorAccumulator;
import org.la4j.vector.functor.VectorFunction;
import org.la4j.vector.functor.VectorProcedure;

public abstract class AbstractMatrix implements Matrix {

    private static final String DEFAULT_ROWS_DELIMITER = "\n";
    private static final String DEFAULT_COLUMNS_DELIMITER = " ";
    private static final NumberFormat DEFAULT_FORMATTER = new DecimalFormat("0.000");
    private static final String[] INDENTS = { // 9 predefined indents for alignment
            " ",
            "  ",
            "   ",
            "    ",
            "     ",
            "      ",
            "       ",
            "        ",
            "         ",
            "          "
    };

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
        setAll(value);
    }

    @Override
    public void setAll(double value) {
        MatrixIterator it = iterator();

        while (it.hasNext()) {
            it.next();
            it.set(value);
        }
    }

    @Override
    public void assignRow(int i, double value) {
       setRow(i, value);
    }

    @Override
    public void assignColumn(int j, double value) {
        setColumn(j, value);
    }

    @Override
    public void setRow(int i, double value) {
        VectorIterator it = iteratorOfRow(i);

        while (it.hasNext()) {
            it.next();
            it.set(value);
        }
    }

    @Override
    public void setColumn(int j, double value) {
        VectorIterator it = iteratorOfColumn(j);

        while (it.hasNext()) {
            it.next();
            it.set(value);
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
    public Vector getRow(int i, Factory factory) {
        return getRow(i).to(Factory.asVectorFactory(factory));
    }

    @Override
    public Vector getColumn(int j, Factory factory) {
        return getColumn(j).to(Factory.asVectorFactory(factory));
    }

    @Override
    public void setRow(int i, Vector row) {
        if (columns != row.length()) {
            fail("Wrong vector length: " + row.length() + ". Should be: " + columns + ".");
        }

        for (int j = 0; j < row.length(); j++) {
            set(i, j, row.get(j));
        }
    }

    @Override
    public void setColumn(int j, Vector column) {
        if (rows != column.length()) {
            fail("Wrong vector length: " + column.length() + ". Should be: " + rows + ".");
        }

        for (int i = 0; i < column.length(); i++) {
            set(i, j, column.get(i));
        }
    }

    @Override
    public Matrix removeRow(int i) {
        if (i >= rows || i < 0) {
            throw new IndexOutOfBoundsException("Illegal row number, must be 0.." + (rows - 1));
        }

        Matrix result = blankOfShape(rows - 1, columns);

        for (int ii = 0; ii < i; ii++) {
            result.setRow(ii, getRow(ii));
        }

        for (int ii = i + 1; ii < rows; ii++) {
            result.setRow(ii - 1, getRow(ii));
        }

        return result;
    }

    @Override
    public Matrix removeColumn(int j) {
        if (j >= columns || j < 0) {
            throw new IndexOutOfBoundsException("Illegal row number, must be 0.." + (columns - 1));
        }

        Matrix result = blankOfShape(rows, columns - 1);

        for (int jj = 0; jj < j; jj++) {
            result.setColumn(jj, getColumn(jj));
        }

        for (int jj = j + 1; jj < columns; jj++) {
            result.setColumn(jj - 1, getColumn(jj));
        }

        return result;
    }

    @Override
    public Matrix removeFirstRow() {
        return removeRow(0);
    }

    @Override
    public Matrix removeFirstColumn() {
        return removeColumn(0);
    }

    @Override
    public Matrix removeLastRow() {
        return removeRow(rows - 1);
    }

    @Override
    public Matrix removeLastColumn() {
        return removeColumn(columns - 1);
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
        Matrix result = blankOfShape(columns, rows);
        MatrixIterator it = result.iterator();

        while (it.hasNext()) {
            it.next();
            int i = it.rowIndex();
            int j = it.columnIndex();
            it.set(get(j, i));
        }

        return result;
    }

    @Override
    public Matrix transpose(Factory factory) {
        return transpose().to(Factory.asMatrixFactory(factory));
    }

    @Override
    public Matrix rotate() {
        Matrix result = blankOfShape(columns, rows);
        MatrixIterator it = result.iterator();

        while (it.hasNext()) {
            it.next();
            int i = it.rowIndex();
            int j = it.columnIndex();
            it.set(get(rows - 1 - j, i));
        }

        return result;
    }

    @Override
    public Matrix rotate(Factory factory) {
        return rotate().to(Factory.asMatrixFactory(factory));
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

    @Override
    public Matrix power(int n) {
        if (n < 0) {
            fail("The exponent should be positive: " + n + ".");
        }

        Matrix result = blankOfShape(rows, rows);
        Matrix that = this;

        for (int i = 0; i < rows; i++) {
            result.set(i, i, 1.0);
        }

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
    public Matrix power(int n, Factory factory) {
        return power(n).to(Factory.asMatrixFactory(factory));
    }

    @Override
    public Matrix multiply(double value) {
        Matrix result = blank();
        MatrixIterator it = iterator();

        while (it.hasNext()) {
            double x = it.next();
            int i = it.rowIndex();
            int j = it.columnIndex();
            result.set(i, j, x * value);
        }

        return result;
    }

    @Override
    public Matrix multiply(double value, Factory factory) {
        return multiply(value).to(Factory.asMatrixFactory(factory));
    }

    @Override
    public Vector multiply(Vector that) {
        return apply(LinearAlgebra.OO_PLACE_MATRIX_BY_VECTOR_MULTIPLICATION, that);
    }

    @Override
    public Vector multiply(Vector that, Factory factory) {
        return multiply(that).to(Factory.asVectorFactory(factory));
    }

    @Override
    public Matrix multiply(Matrix that) {
        return apply(LinearAlgebra.OO_PLACE_MATRICES_MULTIPLICATION, that);
    }

    @Override
    public Matrix multiply(Matrix that, Factory factory) {
        return multiply(that).to(Factory.asMatrixFactory(factory));
    }

    @Override
    public Matrix multiplyByItsTranspose() {
        return apply(LinearAlgebra.OO_PLACE_MATRIX_BY_ITS_TRANSPOSE_MULTIPLICATION);
    }

    @Override
    public Matrix subtract(double value) {
        return add(-value);
    }

    @Override
    public Matrix subtract(double value, Factory factory) {
        return subtract(value).to(Factory.asMatrixFactory(factory));
    }

    @Override
    public Matrix subtract(Matrix that) {
        return apply(LinearAlgebra.OO_PLACE_MATRICES_SUBTRACTION, that);
    }

    @Override
    public Matrix subtract(Matrix that, Factory factory) {
        return subtract(that).to(Factory.asMatrixFactory(factory));
    }

    @Override
    public Matrix add(double value) {
        MatrixIterator it = iterator();
        Matrix result = blank();

        while (it.hasNext()) {
            double x = it.next();
            int i = it.rowIndex();
            int j = it.columnIndex();
            result.set(i, j, x + value);
        }

        return result;
    }

    @Override
    public Matrix add(double value, Factory factory) {
        return add(value).to(Factory.asMatrixFactory(factory));
    }

    @Override
    public Matrix add(Matrix that) {
        return apply(LinearAlgebra.OO_PLACE_MATRIX_ADDITION, that);
    }

    @Override
    public Matrix add(Matrix that, Factory factory) {
        return add(that).to(Factory.asMatrixFactory(factory));
    }
    
    @Override
    public Matrix insert(Matrix that) {
        return insert(that, 0, 0, 0, 0, that.rows(), that.columns());
    }
    
    @Override
    public Matrix insert(Matrix that, int rows, int columns) {
        return insert(that, 0, 0, 0, 0, rows, columns);
    }
    
    @Override
    public Matrix insert(Matrix that, int destRow, int destColumn, int rows, int columns) {
        return insert(that, 0, 0, destRow, destColumn, rows, columns);
    }
    
    @Override
    public Matrix insert(Matrix that, int srcRow, int srcColumn, int destRow, int destColumn, int rows, int columns) {
        if (rows < 0 || columns < 0) {
            fail("Cannot have negative rows or columns: " + rows + "x" + columns);
        }
        
        if (destRow < 0 || destColumn < 0) {
            fail("Cannot have negative destination position: " + destRow + ", " + destColumn);
        }
        
        if (destRow > that.rows() || destColumn> that.columns()) {
            fail("Destination position out of bounds: " + destRow + ", " + destColumn);
        }
        
        if (srcRow < 0 || srcColumn < 0) {
            fail("Cannot have negative source position: " + destRow + ", " + destColumn);
        }
        
        if (srcRow > this.rows || srcColumn > this.columns) {
            fail("Destination position out of bounds: " + srcRow + ", " + srcColumn);
        }
        
        if (destRow + rows > this.columns || destColumn + columns > this.rows) {
            fail("Out of bounds: Cannot add " + rows + " rows and " + columns + " cols at " 
                    + destRow + ", " + destColumn + " in a " + this.rows + "x" + this.columns + " matrix.");
        }
        
        if (srcRow + rows > that.rows() || srcColumn + columns > that.columns()) {
            fail("Out of bounds: Cannot get " + rows + " rows and " + columns + " cols at " 
                    + srcRow + ", " + srcColumn + " from a " + that.rows() + "x" + that.columns() + " matrix.");
        }
        
        Matrix result = copy();
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                result.set(i + destRow, j + destColumn, that.get(i + srcRow, j + srcColumn));
            }
        }
        
        return result;
    }
    
    @Override
    public Matrix divide(double value) {
        return multiply(1.0 / value);
    }

    @Override
    public Matrix divide(double value, Factory factory) {
        return divide(value).to(Factory.asMatrixFactory(factory));
    }

    @Override
    public Matrix kroneckerProduct(Matrix that) {
        return apply(LinearAlgebra.OO_PLACE_KRONECKER_PRODUCT, that);
    }

    @Override
    public Matrix kroneckerProduct(Matrix matrix, Factory factory) {
        return kroneckerProduct(matrix).to(Factory.asMatrixFactory(factory));
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
        return fold(Matrices.asProductAccumulator(1.0));
    }

    @Override
    public Matrix hadamardProduct(Matrix that) {
        return apply(LinearAlgebra.OO_PLACE_MATRIX_HADAMARD_PRODUCT, that);
    }

    @Override
    public Matrix hadamardProduct(Matrix that, Factory factory) {
        return hadamardProduct(that).to(Factory.asMatrixFactory(factory));
    }

    @Override
    public double sum() {
        return fold(Matrices.asSumAccumulator(0.0));
    }

    @Override
    public Matrix blank() {
        return blankOfShape(rows, columns);
    }

    @Override
    public Matrix blankOfRows(int rows) {
        return blankOfShape(rows, columns);
    }

    @Override
    public Matrix blankOfColumns(int columns) {
        return blankOfShape(rows, columns);
    }

    @Override
    public Matrix blank(Factory factory) {
        return factory.createMatrix(rows, columns);
    }

    @Override
    public Matrix copy() {
        return copyOfShape(rows, columns);
    }

    @Override
    public Matrix copy(Factory factory) {
        return factory.createMatrix(this);
    }

    @Override
    public Matrix resize(int rows, int columns) {
        return copyOfShape(rows, columns);
    }

    @Override
    public Matrix resizeRows(int rows) {
        return copyOfRows(rows);
    }

    @Override
    public Matrix resizeRows(int rows, Factory factory) {
        return resize(rows, columns, factory);
    }

    @Override
    public Matrix resizeColumns(int columns) {
        return copyOfColumns(columns);
    }

    @Override
    public Matrix resizeColumns(int columns, Factory factory) {
        return resize(rows, columns, factory);
    }

    @Override
    public Matrix resize(int rows, int columns, Factory factory) {
        return copyOfShape(rows, columns).to(Factory.asMatrixFactory(factory));
    }

    @Override
    public Matrix copyOfRows(int rows) {
        return copyOfShape(rows, columns);
    }

    @Override
    public Matrix copyOfColumns(int columns) {
        return copyOfShape(rows, columns);
    }

    @Override
    public Matrix shuffle() {
        Matrix result = copy();

        // Conduct Fisher-Yates shuffle
        Random random = new Random();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                int ii = random.nextInt(rows - i) + i;
                int jj = random.nextInt(columns - j) + j;

                double a = result.get(ii, jj);
                result.set(ii, jj, result.get(i, j));
                result.set(i, j, a);
            }
        }

        return result;
    }

    @Override
    public Matrix shuffle(Factory factory) {
        return shuffle().to(Factory.asMatrixFactory(factory));
    }

    @Override
    public Matrix slice(int fromRow, int fromColumn, int untilRow, int untilColumn) {
        if (untilRow - fromRow < 0 || untilColumn - fromColumn < 0) {
            fail("Wrong slice range: [" + fromRow + ".." + untilRow + "][" + fromColumn + ".." + untilColumn + "].");
        }

        Matrix result = blankOfShape(untilRow - fromRow, untilColumn - fromColumn);

        for (int i = fromRow; i < untilRow; i++) {
            for (int j = fromColumn; j < untilColumn; j++) {
                result.set(i - fromRow, j - fromColumn, get(i, j));
            }
        }

        return result;
    }

    @Override
    public Matrix slice(int fromRow, int fromColumn, int untilRow,
                        int untilColumn, Factory factory) {

        return slice(fromRow, fromColumn, untilRow, untilColumn).to(Factory.asMatrixFactory(factory));
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
        int m = rowIndices.length;
        int n = columnIndices.length;

        if (m == 0 || n == 0) {
            fail("No rows or columns selected.");
        }

        Matrix result = blankOfShape(m, n);

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                result.set(i, j, get(rowIndices[i], columnIndices[j]));
            }
        }

        return result;
    }

    @Override
    public Matrix select(int[] rowIndices, int[] columnIndices, Factory factory) {
        return select(rowIndices, columnIndices).to(Factory.asMatrixFactory(factory));
    }

    @Override
    public Factory factory() {
        return factory;
    }

    @Override
    public void each(MatrixProcedure procedure) {
        MatrixIterator it = iterator();

        while (it.hasNext()) {
            double x = it.next();
            int i = it.rowIndex();
            int j = it.columnIndex();
            procedure.apply(i, j, x);
        }
    }

    @Override
    public void eachInRow(int i, MatrixProcedure procedure) {
        final MatrixProcedure p = procedure;
        final int ii = i;
        eachInRow(i, new VectorProcedure() {
            @Override
            public void apply(int j, double value) {
                p.apply(ii, j, value);
            }
        });
    }
    
    @Override
    public void eachInRow(int i, VectorProcedure procedure) {
        VectorIterator it = iteratorOfRow(i);

        while (it.hasNext()) {
            double x = it.next();
            int j = it.index();
            procedure.apply(j, x);
        }
    }

    @Override
    public void eachInColumn(int j, MatrixProcedure procedure) {
        final MatrixProcedure p = procedure;
        final int jj = j;
        eachInColumn(j, new VectorProcedure() {
            @Override
            public void apply(int i, double value) {
                p.apply(i, jj, value);
            }
        });
    }
    
    @Override
    public void eachInColumn(int j, VectorProcedure procedure) {
        VectorIterator it = iteratorOfColumn(j);

        while (it.hasNext()) {
            double x = it.next();
            int i = it.index();
            procedure.apply(i, x);
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
        Matrix result = blank();
        MatrixIterator it = iterator();

        while (it.hasNext()) {
            double x  = it.next();
            int i = it.rowIndex();
            int j = it.columnIndex();
            result.set(i, j, function.evaluate(i, j, x));
        }

        return result;
    }

    @Override
    public Matrix transform(MatrixFunction function, Factory factory) {
        return transform(function).to(Factory.asMatrixFactory(factory));
    }

    @Override
    public Matrix transformRow(int i, MatrixFunction function) {
        return transformRow(i, function, factory);
    }
    
    @Override
    public Matrix transformRow(int i, VectorFunction function) {
        Matrix result = copy();
        VectorIterator it = result.iteratorOfRow(i);

        while (it.hasNext()) {
            double x = it.next();
            int j = it.index();
            it.set(function.evaluate(j, x));
        }

        return result;
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
    public Matrix transformColumn(int j, VectorFunction function) {
        Matrix result = copy();
        VectorIterator it = result.iteratorOfColumn(j);

        while (it.hasNext()) {
            double x = it.next();
            int i = it.index();
            it.set(function.evaluate(i, x));
        }

        return result;
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
        MatrixIterator it = iterator();

        while (it.hasNext()) {
            double x = it.next();
            int i = it.rowIndex();
            int j = it.columnIndex();
            it.set(function.evaluate(i, j, x));
        }
    }

    @Override
    public void updateAt(int i, int j, MatrixFunction function) {
        set(i, j, function.evaluate(i, j, get(i, j)));
    }

    @Override
    public void update(int i, int j, MatrixFunction function) {
        updateAt(i, j, function);
    }

    @Override
    public void updateRow(int i, MatrixFunction function) {
        final int ii = i;
        final MatrixFunction f = function;
        updateRow(i, new VectorFunction() {
            @Override
            public double evaluate(int j, double value) {
                return f.evaluate(ii, j, value);
            }
        });
    }

    @Override
    public void updateRow(int i, VectorFunction function) {
        VectorIterator it = iteratorOfRow(i);

        while (it.hasNext()) {
            double x = it.next();
            int j = it.index();
            it.set(function.evaluate(j, x));
        }
    }
    
    @Override
    public void updateColumn(int j, MatrixFunction function) {
        final int jj = j;
        final MatrixFunction f = function;
        updateColumn(j, new VectorFunction() {
            @Override
            public double evaluate(int i, double value) {
                return f.evaluate(i, jj, value);
            }
        });
    }
    
    @Override
    public void updateColumn(int j, VectorFunction function) {
        VectorIterator it = iteratorOfColumn(j);

        while (it.hasNext()) {
            double x = it.next();
            int i = it.index();
            it.set(function.evaluate(i, x));
        }
    }

    @Override
    public double fold(MatrixAccumulator accumulator) {
        each(Matrices.asAccumulatorProcedure(accumulator));
        return accumulator.accumulate();
    }

    @Override
    public double foldRow(int i, MatrixAccumulator accumulator) {
        eachInRow(i, Matrices.asAccumulatorProcedure(accumulator));
        return accumulator.accumulate();
    }
    
    @Override
    public double foldRow(int i, VectorAccumulator accumulator) {
        eachInRow(i, Vectors.asAccumulatorProcedure(accumulator));
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
    public double[] foldRows(VectorAccumulator accumulator) {
        double[] result = new double[rows];

        for (int i = 0; i < rows; i++) {
            result[i] = foldRow(i, accumulator);
        }

        return result;
    }

    @Override
    public double foldColumn(int j, MatrixAccumulator accumulator) {
        eachInColumn(j, Matrices.asAccumulatorProcedure(accumulator));
        return accumulator.accumulate();
    }

    @Override
    public double foldColumn(int j, VectorAccumulator accumulator) {
        eachInColumn(j, Vectors.asAccumulatorProcedure(accumulator));
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
    public double[] foldColumns(VectorAccumulator accumulator) {
        double[] result = new double[columns];

        for (int i = 0; i < columns; i++) {
            result[i] = foldColumn(i, accumulator);
        }

        return result;
    }

    @Override
    public boolean is(MatrixPredicate predicate) {
        MatrixIterator it = iterator();
        boolean result = predicate.test(rows, columns);

        while (it.hasNext() && result) {
            double x = it.next();
            int i = it.rowIndex();
            int j = it.columnIndex();
            result = predicate.test(i, j, x);
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
    public Vector toRowVector(Factory factory) {
        return toRowVector().to(Factory.asVectorFactory(factory));
    }

    @Override
    public Vector toColumnVector(Factory factory) {
        return toColumnVector().to(Factory.asVectorFactory(factory));
    }

    @Override
    public Vector toRowVector() {
        return getRow(0);
    }

    @Override
    public Vector toColumnVector() {
        return getColumn(0);
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
    public MatrixIterator iterator() {
        return rowMajorIterator();
    }

    @Override
    public RowMajorMatrixIterator rowMajorIterator() {
        return new RowMajorMatrixIterator(rows, columns) {
            private long limit = (long) rows * columns;
            private int i = - 1;

            @Override
            public int rowIndex() {
                return i / columns;
            }

            @Override
            public int columnIndex() {
                return i - rowIndex() * columns;
            }

            @Override
            public double get() {
                return AbstractMatrix.this.get(rowIndex(), columnIndex());
            }

            @Override
            public void set(double value) {
                AbstractMatrix.this.set(rowIndex(), columnIndex(), value);
            }

            @Override
            public boolean hasNext() {
                return i + 1 < limit;
            }

            @Override
            public Double next() {
                i++;
                return get();
            }
        };
    }

    @Override
    public ColumnMajorMatrixIterator columnMajorIterator() {
        return new ColumnMajorMatrixIterator(rows, columns) {
            private long limit = (long) rows * columns;
            private int i = -1;

            @Override
            public int rowIndex() {
                return i - columnIndex() * rows;
            }

            @Override
            public int columnIndex() {
                return i / rows;
            }

            @Override
            public double get() {
                return AbstractMatrix.this.get(rowIndex(), columnIndex());
            }

            @Override
            public void set(double value) {
                AbstractMatrix.this.set(rowIndex(), columnIndex(), value);
            }

            @Override
            public boolean hasNext() {
                return i + 1 < limit;
            }

            @Override
            public Double next() {
                i++;
                return get();
            }
        };
    }

    @Override
    public VectorIterator iteratorOfRow(int i) {
        final int ii = i;
        return new VectorIterator(columns) {
            private int j = -1;

            @Override
            public int index() {
                return j;
            }

            @Override
            public double get() {
                return AbstractMatrix.this.get(ii, j);
            }

            @Override
            public void set(double value) {
                AbstractMatrix.this.set(ii, j, value);
            }

            @Override
            public boolean hasNext() {
                return j + 1 < columns;
            }

            @Override
            public Double next() {
                j++;
                return get();
            }
        };
    }

    @Override
    public VectorIterator iteratorOfColumn(int j) {
        final int jj = j;
        return new VectorIterator(rows) {
            private int i = -1;
            @Override
            public int index() {
                return i;
            }

            @Override
            public double get() {
                return AbstractMatrix.this.get(i, jj);
            }

            @Override
            public void set(double value) {
                AbstractMatrix.this.set(i, jj, value);
            }

            @Override
            public boolean hasNext() {
                return i + 1 < rows;
            }

            @Override
            public Double next() {
                i++;
                return get();
            }
        };
    }

    @Override
    public int hashCode() {
        MatrixIterator it = iterator();
        int result = 17;

        while (it.hasNext()) {
            long value = it.next().longValue();
            result = 37 * result + (int) (value ^ (value >>> 32));
        }

        return result;
    }

    @Override
    public boolean equals(Matrix matrix, double precision) {
        if (rows != matrix.rows() || columns != matrix.columns()) {
            return false;
        }

        boolean result = true;

        for (int i = 0; result && i < rows; i++) {
            for (int j = 0; result && j < columns; j++) {
                double a = get(i, j);
                double b = matrix.get(i, j);
                double diff = Math.abs(a - b);

                result = (a == b) || (diff < precision || diff / Math.max(Math.abs(a), Math.abs(b)) < precision);
            }
        }

        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }

        if (!(o instanceof Matrix)) {
            return false;
        }

        Matrix matrix = (Matrix) o;

        return equals(matrix, Matrices.EPS);
    }

    @Override
    public String toString() {
        return mkString(DEFAULT_FORMATTER,
                        DEFAULT_ROWS_DELIMITER,
                        DEFAULT_COLUMNS_DELIMITER);
    }

    @Override
    public String mkString(NumberFormat formatter) {
        return mkString(formatter,
                        DEFAULT_ROWS_DELIMITER,
                        DEFAULT_COLUMNS_DELIMITER);
    }

    @Override
    public String mkString(String rowsDelimiter, String columnsDelimiter) {
        return mkString(DEFAULT_FORMATTER,
                        rowsDelimiter,
                        columnsDelimiter);
    }

    @Override
    public String mkString(NumberFormat formatter, String rowsDelimiter, String columnsDelimiter) {
        int formats[] = new int[columns];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                double value = get(i, j);
                String output = formatter.format(value);
                int size = output.length();
                formats[j] = size > formats[j] ? size : formats[j];
            }
        }

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                String output = formatter.format(get(i, j));
                int outputLength = output.length();

                if (outputLength < formats[j]) {
                    int align = formats[j] - outputLength;
                    if (align > INDENTS.length - 1) {
                        indent(sb, align);
                    } else {
                        sb.append(INDENTS[align - 1]);
                    }
                }

                sb.append(output)
                  .append(columnsDelimiter);
            }
            sb.append(rowsDelimiter);
        }

        return sb.toString();
    }

    protected void ensureDimensionsAreCorrect(int rows, int columns) {
        if (rows < 0 || columns < 0) {
            fail("Wrong matrix dimensions: " + rows + "x" + columns);
        }
        if (rows == Integer.MAX_VALUE || columns == Integer.MAX_VALUE) {
            fail("Wrong matrix dimensions: use 'Integer.MAX_VALUE - 1' instead.");
        }
    }
    
    protected void ensureIndexesAreInBounds(int i, int j) {
        if (i < 0 || i >= rows) {
            throw new IndexOutOfBoundsException("Row '" + i + "' is invalid.");
        }

        if (j < 0 || j >= columns) {
            throw new IndexOutOfBoundsException("Column '" + j + "' is invalid.");
        }
    }

    protected void fail(String message) {
        throw new IllegalArgumentException(message);
    }

    private void indent(StringBuilder sb, int howMany) {
        while (howMany > 0) {
            sb.append(" ");
            howMany--;
        }
    }

    @Override
    public <T extends Matrix> T to(MatrixFactory<T> factory) {
        T result = factory.apply(rows, columns);
        apply(LinearAlgebra.IN_PLACE_COPY_MATRIX_TO_MATRIX, result);
        return result;
    }

    @Override
    public SparseMatrix toSparseMatrix() {
        return to(Matrices.SPARSE);
    }

    @Override
    public DenseMatrix toDenseMatrix() {
        return to(Matrices.DENSE);
    }

    @Override
    public RowMajorSparseMatrix toRowMajorSparseMatrix() {
        return to(Matrices.SPARSE_ROW_MAJOR);
    }

    @Override
    public ColumnMajorSparseMatrix toColumnMajorSparseMatrix() {
        return to(Matrices.SPARSE_COLUMN_MAJOR);
    }
}
