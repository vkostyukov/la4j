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
 *                 Julia Kostyukova
 *                 Jakob Moellers
 *                 Maxim Samoylov
 *                 Anveshi Charuvaka
 *                 Todd Brunhoff
 *                 Pavel Kalaidin
 *                 Ewald Grusk
 *                 Yuriy Drozd
 */

package org.la4j;

import org.la4j.decomposition.MatrixDecompositor;
import org.la4j.inversion.MatrixInverter;
import org.la4j.iterator.ColumnMajorMatrixIterator;
import org.la4j.iterator.MatrixIterator;
import org.la4j.iterator.RowMajorMatrixIterator;
import org.la4j.iterator.VectorIterator;
import org.la4j.linear.LinearSystemSolver;
import org.la4j.matrix.MatrixFactory;
import org.la4j.matrix.DenseMatrix;
import org.la4j.matrix.dense.Basic1DMatrix;
import org.la4j.matrix.dense.Basic2DMatrix;
import org.la4j.matrix.functor.*;
import org.la4j.matrix.ColumnMajorSparseMatrix;
import org.la4j.matrix.RowMajorSparseMatrix;
import org.la4j.matrix.SparseMatrix;
import org.la4j.operation.MatrixMatrixOperation;
import org.la4j.operation.MatrixOperation;
import org.la4j.operation.MatrixVectorOperation;
import org.la4j.vector.functor.VectorAccumulator;
import org.la4j.vector.functor.VectorFunction;
import org.la4j.vector.functor.VectorProcedure;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.StringTokenizer;

/**
 * A real matrix.
 * <p>
 * <a href="http://mathworld.wolfram.com/Matrix.html">
 * http://mathworld.wolfram.com/Matrix.html </a>
 * </p>
 */
public abstract class Matrix implements Iterable<Double> {

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

    /**
     * Creates a zero-shape matrix.
     */
    public Matrix() {
        this(0, 0);
    }

    /**
     * Creates a matrix of given shape {@code rows} x {@code columns};
     */
    public Matrix(int rows, int columns) {
        ensureDimensionsAreCorrect(rows, columns);
        this.rows = rows;
        this.columns = columns;
    }

    /**
     * Creates a zero {@link Matrix} of the given shape:
     * {@code rows} x {@code columns}.
     */
    public static Matrix zero(int rows, int columns) {
        long size = (long) rows * columns;
        return size > 1000 ? SparseMatrix.zero(rows, columns) : DenseMatrix.zero(rows, columns);
    }

    /**
     * Creates a constant {@link Matrix} of the given shape and {@code value}.
     */
    public static Matrix constant(int rows, int columns, double constant) {
        return DenseMatrix.constant(rows, columns, constant);
    }

    /**
     * Creates a diagonal {@link Matrix} of the given {@code size} whose
     * diagonal elements are equal to {@code diagonal}.
     */
    public static Matrix diagonal(int size, double diagonal) {
        return SparseMatrix.diagonal(size, diagonal);
    }

    /**
     * Creates an unit {@link Matrix} of the given shape:
     * {@code rows} x {@code columns}.
     */
    public static Matrix unit(int rows, int columns) {
        return DenseMatrix.unit(rows, columns);
    }

    /**
     * Creates an identity {@link Matrix} of the given {@code size}.
     */
    public static Matrix identity(int size) {
        return SparseMatrix.identity(size);
    }

    /**
     * Creates a random {@link Matrix} of the given shape:
     * {@code rows} x {@code columns}.
     */
    public static Matrix random(int rows, int columns, Random random) {
        return DenseMatrix.random(rows, columns, random);
    }

    /**
     * Creates a random symmetric {@link Matrix} of the given {@code size}.
     */
    public static Matrix randomSymmetric(int size, Random random) {
        return DenseMatrix.randomSymmetric(size, random);
    }

    /**
     * Creates a {@link Matrix} of the given 1D {@code array} w/o
     * copying the underlying array.
     */
    public static Matrix from1DArray(int rows, int columns, double[] array) {
        return Basic1DMatrix.from1DArray(rows, columns, array);
    }

    /**
     * Creates a {@link Matrix} of the given 2D {@code array} w/o
     * copying the underlying array.
     */
    public static Matrix from2DArray(double[][] array) {
        return Basic2DMatrix.from2DArray(array);
    }

    /**
     * Creates a block {@link Matrix} of the given blocks {@code a},
     * {@code b}, {@code c} and {@code d}.
     */
    public static Matrix block(Matrix a, Matrix b, Matrix c, Matrix d) {
        return DenseMatrix.block(a, b, c, d);
    }

    /**
     * Parses {@link Matrix} from the given CSV string.
     *
     * @param csv the string in CSV format
     *
     * @return a parsed matrix
     */
    public static Matrix fromCSV(String csv) {
        StringTokenizer lines = new StringTokenizer(csv, "\n");
        Matrix result = DenseMatrix.zero(10, 10);
        int rows = 0;
        int columns = 0;

        while (lines.hasMoreTokens()) {
            if (result.rows() == rows) {
                result = result.copyOfRows((rows * 3) / 2 + 1);
            }

            StringTokenizer elements = new StringTokenizer(lines.nextToken(), ", ");
            int j = 0;
            while (elements.hasMoreElements()) {
                if (j == result.columns()) {
                    result = result.copyOfColumns((j * 3) / 2 + 1);
                }

                double x = Double.parseDouble(elements.nextToken());
                result.set(rows, j++, x);
            }

            rows++;
            columns = j > columns ? j : columns;
        }

        return result.copyOfShape(rows, columns);
    }

    /**
     * Parses {@link Matrix} from the given Matrix Market string.
     *
     * @param mm the string in Matrix Market format
     *
     * @return a parsed matrix
     */
    public static Matrix fromMatrixMarket(String mm) {
        StringTokenizer body = new StringTokenizer(mm, "\n");

        String headerString = body.nextToken();
        StringTokenizer header = new StringTokenizer(headerString);

        if (!"%%MatrixMarket".equals(header.nextToken())) {
            throw new IllegalArgumentException("Wrong input file format: can not read header '%%MatrixMarket'.");
        }

        String object = header.nextToken();
        if (!"matrix".equals(object)) {
            throw new IllegalArgumentException("Unexpected object: " + object + ".");
        }

        String format = header.nextToken();
        if (!"coordinate".equals(format) && !"array".equals(format)) {
            throw new IllegalArgumentException("Unknown format: " + format + ".");
        }

        String field = header.nextToken();
        if (!"real".equals(field)) {
            throw new IllegalArgumentException("Unknown field type: " + field + ".");
        }

        String symmetry = header.nextToken();
        if (!symmetry.equals("general")) {
            throw new IllegalArgumentException("Unknown symmetry type: " + symmetry + ".");
        }

        String majority = (header.hasMoreTokens()) ? header.nextToken() : "row-major";

        String nextToken = body.nextToken();
        while (nextToken.startsWith("%")) {
            nextToken = body.nextToken();
        }

        if ("coordinate".equals(format)) {
            StringTokenizer lines = new StringTokenizer(nextToken);

            int rows = Integer.parseInt(lines.nextToken());
            int columns = Integer.parseInt(lines.nextToken());
            int cardinality = Integer.parseInt(lines.nextToken());

            Matrix result = "row-major".equals(majority) ?
                    RowMajorSparseMatrix.zero(rows, columns, cardinality) :
                    ColumnMajorSparseMatrix.zero(rows, columns, cardinality);

            for (int k = 0; k < cardinality; k++) {
                lines = new StringTokenizer(body.nextToken());

                int i = Integer.valueOf(lines.nextToken());
                int j = Integer.valueOf(lines.nextToken());
                double x = Double.valueOf(lines.nextToken());
                result.set(i - 1, j - 1, x);
            }

            return result;
        } else {
            StringTokenizer lines = new StringTokenizer(nextToken);

            int rows = Integer.valueOf(lines.nextToken());
            int columns = Integer.valueOf(lines.nextToken());
            Matrix result = DenseMatrix.zero(rows, columns);

            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < columns; j++) {
                    result.set(i, j, Double.valueOf(body.nextToken()));
                }
            }

            return result;
        }
    }

    //
    // ============ ABSTRACT METHODS ============
    //

    /**
     * Gets the specified element of this matrix.
     *
     * @param i element's row index
     * @param j element's column index
     *
     * @return the element of this matrix
     */
    public abstract double get(int i, int j);

    /**
     * Sets the specified element of this matrix to given {@code value}.
     * 
     * @param i element's row index
     * @param j element's column index
     * @param value element's new value
     */
    public abstract void set(int i, int j, double value);

    /**
     * Copies the specified row of this matrix into the vector.
     *
     * @param i the row index
     *
     * @return the row represented as vector
     */
    public abstract Vector getRow(int i);

    /**
     * Copies the specified column of this matrix into the vector.
     *
     * @param j the column index
     *
     * @return the column represented as vector
     */
    public abstract Vector getColumn(int j);

    /**
     * Creates the blank matrix (a zero matrix with same size) of this matrix
     * of the given shape: {@code rows} x {@code columns}.
     *
     * @return blank matrix
     */
    public abstract Matrix blankOfShape(int rows, int columns);

    /**
     * Copies this matrix into the new matrix with specified dimensions: {@code rows} and {@code columns}.
     *
     * @param rows the number of rows in new matrix
     * @param columns the number of columns in new matrix
     *
     * @return the copy of this matrix with new size
     */
    public abstract Matrix copyOfShape(int rows, int columns);

    /**
     * Pipes this matrix to a given {@code operation}.
     *
     * @param operation the matrix operation
     *                  (an operation that takes a matrix and returns {@code T})
     * @param <T> the result type
     *
     * @return the result of an operation applied to this matrix
     */
    public abstract <T> T apply(MatrixOperation<T> operation);

    /**
     * Pipes this matrix to a given {@code operation}.
     *
     * @param operation the matrix-matrix operation
     *                  (an operation that takes two matrices and returns {@code T})
     * @param that the right hand matrix of the given operation
     * @param <T> the result type
     *
     * @return the result of an operation applied to this matrix
     */
    public abstract <T> T apply(MatrixMatrixOperation<T> operation, Matrix that);

    /**
     * Pipes this matrix to a given {@code operation}.
     *
     * @param operation the matrix-vector operation
     *                  (an operation that takes matrix and vector and returns {@code T})
     * @param that the right hand vector of the given operation
     * @param <T> the result type
     *
     * @return the result of an operation applied to this matrix
     */
    public abstract <T> T apply(MatrixVectorOperation<T> operation, Vector that);

    /**
     * Encodes this matrix into a byte array.
     *
     * @return a byte array representing this matrix
     */
    public abstract byte[] toBinary();

    /**
     * Converts this matrix into the Matrix Market string using the
     * given number {@code formatter}.
     *
     * @return a string in Matrix Market format representing this matrix;
     */
    public abstract String toMatrixMarket(NumberFormat formatter);

    //
    // ============ CONCRETE METHODS ============
    //

    /**
     * Sets all elements of this matrix to the given {@code value}.
     *
     * @param value the element's new value
     */
    public void setAll(double value) {
        MatrixIterator it = iterator();

        while (it.hasNext()) {
            it.next();
            it.set(value);
        }
    }

    /**
     * <p>
     * Sets all elements of the specified row of this matrix to given {@code value}.
     * </p>
     *
     * @param i the row index
     * @param value the element's new value
     */
    public void setRow(int i, double value) {
        VectorIterator it = iteratorOfRow(i);

        while (it.hasNext()) {
            it.next();
            it.set(value);
        }
    }

    /**
     * <p>
     * Sets all elements of the specified column of this matrix to given {@code value}.
     * </p>
     *
     * @param j the column index
     * @param value the element's new value
     */
    public void setColumn(int j, double value) {
        VectorIterator it = iteratorOfColumn(j);

        while (it.hasNext()) {
            it.next();
            it.set(value);
        }
    }

    /**
     * Swaps the specified rows of this matrix.
     * 
     * @param i the row index
     * @param j the row index
     */
    public void swapRows(int i, int j) {
        if (i != j) {
            Vector ii = getRow(i);
            Vector jj = getRow(j);

            setRow(i, jj);
            setRow(j, ii);
        }
    }

    /**
     * Swaps the specified columns of this matrix.
     * 
     * @param i the column index
     * @param j the column index
     */
    public void swapColumns(int i, int j) {
        if (i != j) {
            Vector ii = getColumn(i);
            Vector jj = getColumn(j);

            setColumn(i, jj);
            setColumn(j, ii);
        }
    }

    /**
     * Returns the number of rows of this matrix.
     * 
     * @return the number of rows
     */
    public int rows() {
        return rows;
    }

    /**
     * Returns the number of columns of this matrix.
     * 
     * @return the number of columns
     */
    public int columns() {
        return columns;
    }

    /**
     * Transposes this matrix.
     * 
     * @return the transposed matrix
     */
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

    /**
     * Rotates this matrix by 90 degrees to the right.
     * 
     * @return the rotated matrix
     */
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

    /**
     * Powers this matrix of given exponent {code n}.
     *
     * @param n the exponent
     *
     * @return the powered matrix
     */
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

    /**
     * Scales this matrix by given {@code value} (v).
     * 
     * @param value the scale factor
     *
     * @return A * v
     */
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

    /**
     * Multiplies this matrix (A) by given {@code that} vector (x).
     *
     * @param that the vector
     *
     * @return A * x
     */
    public Vector multiply(Vector that) {
        return apply(LinearAlgebra.OO_PLACE_MATRIX_BY_VECTOR_MULTIPLICATION, that);
    }

    /**
     * Multiplies this matrix (A) by given {@code that} matrix (B).
     * 
     * @param that the right hand matrix for multiplication
     *
     * @return A * B
     */
    public Matrix multiply(Matrix that) {
        return apply(LinearAlgebra.OO_PLACE_MATRICES_MULTIPLICATION, that);
    }

    /**
     * Multiplies this matrix by its transpose.
     *
     * @return this matrix multiplied by its transpose
     */
    public Matrix multiplyByItsTranspose() {
        return apply(LinearAlgebra.OO_PLACE_MATRIX_BY_ITS_TRANSPOSE_MULTIPLICATION);
    }

    /**
     * Subtracts given {@code value} (v) from every element of this matrix (A).
     *
     * @param value the right hand value for subtraction
     *
     * @return A - v
     */
    public Matrix subtract(double value) {
        return add(-value);
    }

    /**
     * Subtracts given {@code that} matrix (B) from this matrix (A).
     *
     * @param that the right hand matrix for subtraction
     *
     * @return A - B
     */
    public Matrix subtract(Matrix that) {
        return apply(LinearAlgebra.OO_PLACE_MATRICES_SUBTRACTION, that);
    }

    /**
     * Adds given {@code value} (v) to every element of this matrix (A).
     * 
     * @param value the right hand value for addition
     *
     * @return A + v
     */
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

    /**
     * Adds given {@code that} matrix (B) to this matrix (A).
     * 
     * @param that the right hand matrix for addition
     *
     * @return A + B
     */
    public Matrix add(Matrix that) {
        return apply(LinearAlgebra.OO_PLACE_MATRIX_ADDITION, that);
    }

    /**
     * Inserts a given {@code that} (B) into this matrix (A). The original
     * values are overwritten by the new ones.
     * 
     * @param that the matrix to insert, from the first row and column
     * @return a matrix with the parameter inserted into it
     */
    public Matrix insert(Matrix that) {
        return insert(that, 0, 0, 0, 0, that.rows(), that.columns());
    }
    
    /**
     * Inserts a given {@code that} matrix (B) into this matrix (A). The original
     * values are overwritten by the new ones.
     * 
     * @param that the matrix to insert
     * @param rows number of rows to insert
     * @param columns number of columns to insert
     * @return a matrix with the parameter inserted into it
     */
    public Matrix insert(Matrix that, int rows, int columns) {
        return insert(that, 0, 0, 0, 0, rows, columns);
    }

    /**
     * Inserts a given {@code that} matrix (B) into this matrix (A). The original
     * values are overwritten by the new ones.
     * 
     * @param that the matrix to insert
     * @param destRow the row to insert at in the destination matrix
     * @param destColumn the column to insert at in the destination matrix
     * @param rows number of rows to insert
     * @param columns number of columns to insert
     * @return a matrix with the parameter inserted into it
     */
    public Matrix insert(Matrix that, int destRow, int destColumn, int rows, int columns) {
        return insert(that, 0, 0, destRow, destColumn, rows, columns);
    }

    /**
     * Inserts a given {@code that} matrix (B) into this matrix (A). The original
     * values are overwritten by the new ones.
     * 
     * @param that the matrix to insert
     * @param srcRow the row to start at in the source matrix
     * @param srcColumn the column to start at in the source matrix
     * @param destRow the row to insert at in the destination matrix
     * @param destColumn the column to insert at in the destination matrix
     * @param rows number of rows to insert
     * @param columns number of columns to insert
     * @return a matrix with the parameter inserted into it
     */
    public Matrix insert(Matrix that, int srcRow, int srcColumn, int destRow, int destColumn, int rows, int columns) {
        if (rows < 0 || columns < 0) {
            fail("Cannot have negative rows or columns: " + rows + "x" + columns);
        }

        if (destRow < 0 || destColumn < 0) {
            fail("Cannot have negative destination position: " + destRow + ", " + destColumn);
        }

        if (destRow > this.rows || destColumn > this.columns) {
            fail("Destination position out of bounds: " + destRow + ", " + destColumn);
        }

        if (srcRow < 0 || srcColumn < 0) {
            fail("Cannot have negative source position: " + destRow + ", " + destColumn);
        }

        if (srcRow > that.rows || srcColumn > that.columns) {
            fail("Destination position out of bounds: " + srcRow + ", " + srcColumn);
        }

        if (destRow + rows > this.rows || destColumn + columns > this.columns) {
            fail("Out of bounds: Cannot add " + rows + " rows and " + columns + " cols at "
                    + destRow + ", " + destColumn + " in a " + this.rows + "x" + this.columns + " matrix.");
        }

        if (srcRow + rows > that.rows || srcColumn + columns > that.columns) {
            fail("Out of bounds: Cannot get " + rows + " rows and " + columns + " cols at "
                    + srcRow + ", " + srcColumn + " from a " + that.rows + "x" + that.columns + " matrix.");
        }

        Matrix result = copy();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                result.set(i + destRow, j + destColumn, that.get(i + srcRow, j + srcColumn));
            }
        }

        return result;
    }

    /**
     * Divides every element of this matrix (A) by given {@code value} (v).
     * 
     * @param value the right hand value for division
     *
     * @return A / v
     */
    public Matrix divide(double value) {
        return multiply(1.0 / value);
    }

    /**
     * Calculates the Kronecker product of this matrix (A) and given {@code that} matrix (B).
     * 
     * @param that the right hand matrix for Kronecker product
     *
     * @return A (+) B
     */
    public Matrix kroneckerProduct(Matrix that) {
        return apply(LinearAlgebra.OO_PLACE_KRONECKER_PRODUCT, that);
    }

    /**
     * Calculates the trace of this matrix.
     *
     * <p>
     * See <a href="http://mathworld.wolfram.com/MatrixTrace.html">
     * http://mathworld.wolfram.com/MatrixTrace.html</a> for more details.
     * </p>
     * 
     * @return the trace of this matrix
     */
    public double trace() {
        double result = 0.0;

        for (int i = 0; i < rows; i++) {
            result += get(i, i);
        }

        return result;
    }

    /**
     * Calculates the product of diagonal elements of this matrix.
     * 
     * @return the product of diagonal elements of this matrix
     */
    public double diagonalProduct() {
        BigDecimal result = BigDecimal.ONE;

        for (int i = 0; i < rows; i++) {
            result = result.multiply(BigDecimal.valueOf(get(i, i)));
        }

        return result.setScale(Matrices.ROUND_FACTOR,
                RoundingMode.CEILING).doubleValue();

    }

    /**
     * Calculates an Euclidean norm of this matrix, a.k.a. frobenius norm
     *
     * @return an Euclidean norm
     */
    public double norm() {
        return euclideanNorm();
    }

    /**
     * Calculates an Euclidean norm of this matrix, a.k.a. frobenius norm
     *
     * @return an Euclidean norm
     */
    public double euclideanNorm() {
        return fold(Matrices.mkEuclideanNormAccumulator());
    }
    
    /**
     * Calculates a Manhattan norm of this matrix, a.k.a. taxicab norm
     *
     * @return a Manhattan norm
     */
    public double manhattanNorm() {
        return fold(Matrices.mkManhattanNormAccumulator());
    }
    
    /**
     * Calculates an Infinity norm of this matrix.
     *
     * @return an Infinity norm
     */
    public double infinityNorm() {
        return fold(Matrices.mkInfinityNormAccumulator());
    }
    
    /**
     * Multiplies up all elements of this matrix.
     * 
     * @return the product of all elements of this matrix
     */
    public double product() {
        return fold(Matrices.asProductAccumulator(1.0));
    }

    /**
     * Summarizes up all elements of this matrix.
     *
     * @return the sum of all elements of this matrix
     */
    public double sum() {
        return fold(Matrices.asSumAccumulator(0.0));
    }

    /**
     * Calculates the Hadamard (element-wise) product of this and given {@code that} matrix.
     *
     * @param that the right hand matrix for Hadamard product
     *
     * @return the Hadamard product of two matrices
     */
    public Matrix hadamardProduct(Matrix that) {
        return apply(LinearAlgebra.OO_PLACE_MATRIX_HADAMARD_PRODUCT, that);
    }

    /**
     * Calculates the determinant of this matrix.
     *
     * <p>
     * See <a href="http://mathworld.wolfram.com/Determinant.html">
     * http://mathworld.wolfram.com/Determinant.html</a> for more details.
     * </p>
     * 
     * @return the determinant of this matrix
     */
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
        Matrix[] lup = decompositor.decompose();
        // TODO: Why Java doesn't support pattern matching?
        Matrix u = lup[1];
        Matrix p = lup[2];

        double result = u.diagonalProduct();

        // TODO: we can do that in O(n log n)
        //       just google: "counting inversions divide and conqueror"
        int[] permutations = new int[p.rows()];
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

    /**
     * Calculates the rank of this matrix.
     *
     * <p>
     * See <a href="http://mathworld.wolfram.com/MatrixRank.html">
     * http://mathworld.wolfram.com/MatrixRank.html</a> for more details.
     * </p>
     *
     * @return the rank of this matrix
     */
    public int rank() {
        if (rows == 0 || columns == 0) {
            return 0;
        }

        // TODO:
        // handle small (1x1, 1xn, nx1, 2x2, 2xn, nx2, 3x3, 3xn, nx3)
        // matrices without SVD

        MatrixDecompositor decompositor = withDecompositor(LinearAlgebra.SVD);
        Matrix[] usv = decompositor.decompose();
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

    /**
     * Copies given {@code row} into the specified row of this matrix.
     *
     * @param i the row index
     * @param row the row represented as vector
     */
    public void setRow(int i, Vector row) {
        if (columns != row.length()) {
            fail("Wrong vector length: " + row.length() + ". Should be: " + columns + ".");
        }

        for (int j = 0; j < row.length(); j++) {
            set(i, j, row.get(j));
        }
    }

    /**
     * Copies given {@code column} into the specified column of this matrix.
     *
     * @param j the column index
     * @param column the column represented as vector
     */
    public void setColumn(int j, Vector column) {
        if (rows != column.length()) {
            fail("Wrong vector length: " + column.length() + ". Should be: " + rows + ".");
        }

        for (int i = 0; i < column.length(); i++) {
            set(i, j, column.get(i));
        }
    }

    /**
     * Adds one row to matrix.
     * @param i the row index
     * @return matrix with row.
     */
    public Matrix insertRow(int i, Vector row) {
        if (i > rows || i < 0) {
            throw new IndexOutOfBoundsException("Illegal row number, must be 0.." + rows);
        }

        Matrix result;
        if (columns == 0) {
            result = blankOfShape(rows + 1, row.length());
        } else {
            result = blankOfShape(rows + 1, columns);
        }

        for (int ii = 0; ii < i; ii++) {
            result.setRow(ii, getRow(ii));
        }

        result.setRow(i, row);

        for (int ii = i; ii < rows; ii++) {
            result.setRow(ii + 1, getRow(ii));
        }

        return result;
    }

    /**
     * Adds one column to matrix.
     * @param j the column index
     * @return matrix with column.
     */
    public Matrix insertColumn(int j, Vector column) {
        if (j > columns || j < 0) {
            throw new IndexOutOfBoundsException("Illegal column number, must be 0.." + columns);
        }

        Matrix result;
        if (rows == 0) {
            result = blankOfShape(column.length(), columns + 1);
        } else {
            result = blankOfShape(rows, columns + 1);
        }

        for (int jj = 0; jj < j; jj++) {
            result.setColumn(jj, getColumn(jj));
        }

        result.setColumn(j, column);

        for (int jj = j; jj < columns; jj++) {
            result.setColumn(jj + 1, getColumn(jj));
        }

        return result;
    }

    /**
     * Removes one row from matrix.
     * @param i the row index
     * @return matrix without row.
     */
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

    /**
     * Removes one column from matrix.
     * @param j the column index
     * @return matrix without column.
     */
    public Matrix removeColumn(int j) {
        if (j >= columns || j < 0) {
            throw new IndexOutOfBoundsException("Illegal column number, must be 0.." + (columns - 1));
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

    /**
     * Removes first row from matrix.
     * @return matrix without first row.
     */
    public Matrix removeFirstRow() {
        return removeRow(0);
    }

    /**
     * Removes first column from matrix.
     * @return matrix without first column
     */
    public Matrix removeFirstColumn() {
        return removeColumn(0);
    }

    /**
     * Removes last row from matrix.
     * @return matrix without last row
     */
    public Matrix removeLastRow() {
        return removeRow(rows - 1);
    }

    /**
     * Removes last column from matrix.
     * @return matrix without last column
     */
    public Matrix removeLastColumn() {
        return removeColumn(columns - 1);
    }

    /**
     * Creates the blank matrix (a zero matrix with same size) of this matrix.
     * 
     * @return blank matrix
     */
    public Matrix blank() {
        return blankOfShape(rows, columns);
    }

    /**
     * Creates the blank matrix (a zero matrix with same size) of this matrix
     * of the given shape: {@code rows}. The {@code columns} number remains the
     * same.
     *
     * @return blank matrix
     */
    public Matrix blankOfRows(int rows) {
        return blankOfShape(rows, columns);
    }

    /**
     * Creates the blank matrix (a zero matrix with same size) of this matrix
     * of the given shape: {@code columns}. The {@code rows} number remains the
     * same.
     *
     * @return blank matrix
     */
    public Matrix blankOfColumns(int columns) {
        return blankOfShape(rows, columns);
    }

    /**
     * Copies this matrix.
     *
     * @return the copy of this matrix
     */
    public Matrix copy() {
        return copyOfShape(rows, columns);
    }

    /**
     * Copies this matrix into the new matrix with specified row dimension: {@code rows}.
     *
     * @param rows the number of rows in new matrix
     *
     * @return the copy of this matrix with new size
     */
    public Matrix copyOfRows(int rows) {
        return copyOfShape(rows, columns);
    }

    /**
     * Copies this matrix into the new matrix with specified column dimension: {@code columns}.
     *
     * @param columns the number of columns in new matrix
     *
     * @return the copy of this matrix with new size
     */
    public Matrix copyOfColumns(int columns) {
        return copyOfShape(rows, columns);
    }

    /**
     * Shuffles this matrix.
     *
     * <p>
     * Copies this matrix into the matrix that contains the same elements but with the elements
     * shuffled around (which might also result in the same matrix (with a small likelihood)).
     * </p>
     *
     * @return the shuffled matrix
     */
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

    /**
     * Retrieves the specified sub-matrix of this matrix. The sub-matrix is specified by
     * intervals for row indices and column indices.
     *
     * @param fromRow the beginning of the row indices interval
     * @param fromColumn the beginning of the column indices interval
     * @param untilRow the ending of the row indices interval
     * @param untilColumn the ending of the column indices interval
     *
     * @return the sub-matrix of this matrix
     */
    public Matrix slice(int fromRow, int fromColumn, int untilRow, int untilColumn) {
        ensureIndexArgumentsAreInBounds(fromRow, fromColumn);
        ensureIndexArgumentsAreInBounds(untilRow - 1, untilColumn - 1);

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

    /**
     * Retrieves the specified sub-matrix of this matrix. The sub-matrix is specified by
     * intervals for row indices and column indices. The top left points of both intervals
     * are fixed to zero.
     *
     * @param untilRow the ending of the row indices interval
     * @param untilColumn the ending of the column indices interval
     *
     * @return the sub-matrix of this matrix
     */
    public Matrix sliceTopLeft(int untilRow, int untilColumn) {
        return slice(0, 0, untilRow, untilColumn);
    }

    /**
     * Retrieves the specified sub-matrix of this matrix. The sub-matrix is specified by
     * intervals for row indices and column indices. The bottom right points of both intervals
     * are fixed to matrix dimensions - it's rows and columns correspondingly.
     *
     * @param fromRow the beginning of the row indices interval
     * @param fromColumn the beginning of the column indices interval
     *
     * @return the sub-matrix of this matrix
     */
    public Matrix sliceBottomRight(int fromRow, int fromColumn) {
        return slice(fromRow, fromColumn, rows, columns);
    }

    /**
     * Returns a new matrix with the selected rows and columns. This method can
     * be used either return a specific subset of rows and/or columns or to
     * permute the indices in an arbitrary order. The list of indices are
     * allowed to contain duplicates indices. This is more general than slice()
     * which selects only contiguous blocks. However, where applicable slice()
     * is probably more efficient.
     * 
     * @param rowIndices the array of row indices
     * @param columnIndices the array of column indices
     *
     * @return the new matrix with the selected rows and columns
     *
     * @throws IllegalArgumentException if invalid row or column indices are provided
     */
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

    /**
     * Applies given {@code procedure} to each element of this matrix.
     *
     * @param procedure the matrix procedure
     */
    public void each(MatrixProcedure procedure) {
        MatrixIterator it = iterator();

        while (it.hasNext()) {
            double x = it.next();
            int i = it.rowIndex();
            int j = it.columnIndex();
            procedure.apply(i, j, x);
        }
    }

    /**
     * Applies given {@code procedure} to each element of specified row of this matrix.
     *
     * @param i the row index
     * @param procedure the vector procedure
     */
    public void eachInRow(int i, VectorProcedure procedure) {
        VectorIterator it = iteratorOfRow(i);

        while (it.hasNext()) {
            double x = it.next();
            int j = it.index();
            procedure.apply(j, x);
        }
    }

    /**
     * Applies given {@code procedure} to each element of specified column of this matrix.
     *
     * @param j the column index
     * @param procedure the vector procedure
     */
    public void eachInColumn(int j, VectorProcedure procedure) {
        VectorIterator it = iteratorOfColumn(j);

        while (it.hasNext()) {
            double x = it.next();
            int i = it.index();
            procedure.apply(i, x);
        }
    }

    /**
     * Searches for the maximum value of the elements of this matrix.
     *
     * @return maximum value of this matrix
     */
    public double max() {
        return fold(Matrices.mkMaxAccumulator());
    }

    /**
     * Searches for the minimum value of the elements of this matrix.
     *
     * @return minimum value of this matrix
     */
    public double min() {
        return fold(Matrices.mkMinAccumulator());
    }

    /**
     * Searches for the maximum value of specified row in this matrix.
     *
     * @param i the row index
     *
     * @return maximum value of specified row in this matrix
     */
    public double maxInRow(int i) {
        return foldRow(i, Vectors.mkMaxAccumulator());
    }

    /**
     * Searches for the minimum value of specified row in this matrix.
     *
     * @param i the row index
     *
     * @return minimum value of specified row in this matrix
     */
    public double minInRow(int i) {
        return foldRow(i, Vectors.mkMinAccumulator());
    }

    /**
     * Searches for the maximum value of specified column in this matrix.
     *
     * @param j the column index
     *
     * @return maximum value of specified column in this matrix
     */
    public double maxInColumn(int j) {
        return foldColumn(j, Vectors.mkMaxAccumulator());
    }

    /**
     * Searches for the minimum value of specified column in this matrix.
     *
     * @param j the column index
     *
     * @return minimum value of specified column in this matrix
     */
    public double minInColumn(int j) {
        return foldColumn(j, Vectors.mkMinAccumulator());
    }

    /**
     * Builds a new matrix by applying given {@code function} to each element of this matrix.
     *
     * @param function the matrix function
     *
     * @return the transformed matrix
     */
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

    /**
     * Builds a new matrix by applying given {@code function} to each element of specified
     * row in this matrix.
     *
     * @param i the row index
     * @param function the vector function
     *
     * @return the transformed matrix
     */
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

    /**
     * Builds a new matrix by applying given {@code function} to each element of specified
     * column in this matrix.
     *
     * @param j the column index
     * @param function the vector function
     *
     * @return the transformed matrix
     */
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

    /**
     * Updates all elements of this matrix by applying given {@code function}.
     *
     * @param function the matrix function
     */
    public void update(MatrixFunction function) {
        MatrixIterator it = iterator();

        while (it.hasNext()) {
            double x = it.next();
            int i = it.rowIndex();
            int j = it.columnIndex();
            it.set(function.evaluate(i, j, x));
        }
    }

    /**
     * Updates the specified element of this matrix by applying given {@code function}.
     *
     * @param i the row index
     * @param j the column index
     * @param function the matrix function
     */
    public void updateAt(int i, int j, MatrixFunction function) {
        set(i, j, function.evaluate(i, j, get(i, j)));
    }

    /**
     * Updates all elements of the specified row in this matrix by applying given {@code function}.
     *
     * @param i the row index
     * @param function the vector function
     */
    public void updateRow(int i, VectorFunction function) {
        VectorIterator it = iteratorOfRow(i);

        while (it.hasNext()) {
            double x = it.next();
            int j = it.index();
            it.set(function.evaluate(j, x));
        }
    }

    /**
     * Updates all elements of the specified column in this matrix by applying given {@code function}.
     *
     * @param j the column index
     * @param function the vector function
     */
    public void updateColumn(int j, VectorFunction function) {
        VectorIterator it = iteratorOfColumn(j);

        while (it.hasNext()) {
            double x = it.next();
            int i = it.index();
            it.set(function.evaluate(i, x));
        }
    }

    /**
     * Folds all elements of this matrix with given {@code accumulator}.
     * 
     * @param accumulator the matrix accumulator
     *
     * @return the accumulated value
     */
    public double fold(MatrixAccumulator accumulator) {
        each(Matrices.asAccumulatorProcedure(accumulator));
        return accumulator.accumulate();
    }

    /**
     * Folds all elements of specified row in this matrix with given {@code accumulator}.
     *
     * @param i the row index
     * @param accumulator the vector accumulator
     *
     * @return the accumulated value
     */
    public double foldRow(int i, VectorAccumulator accumulator) {
        eachInRow(i, Vectors.asAccumulatorProcedure(accumulator));
        return accumulator.accumulate();
    }

    /**
     * Folds all elements (in row-by-row manner) of this matrix with given {@code accumulator}.
     *
     * @param accumulator the vector accumulator
     *
     * @return the accumulated double array
     */
    public double[] foldRows(VectorAccumulator accumulator) {
        double[] result = new double[rows];

        for (int i = 0; i < rows; i++) {
            result[i] = foldRow(i, accumulator);
        }

        return result;
    }

    /**
     * Folds all elements of specified column in this matrix with given {@code accumulator}.
     *
     * @param j the column index
     * @param accumulator the vector accumulator
     *
     * @return the accumulated value
     */
    public double foldColumn(int j, VectorAccumulator accumulator) {
        eachInColumn(j, Vectors.asAccumulatorProcedure(accumulator));
        return accumulator.accumulate();
    }

    /**
     * Folds all elements (in a column-by-column manner) of this matrix with given {@code accumulator}.
     *
     * @param accumulator the vector accumulator
     *
     * @return the accumulated double array
     */
    public double[] foldColumns(VectorAccumulator accumulator) {
        double[] result = new double[columns];

        for (int i = 0; i < columns; i++) {
            result[i] = foldColumn(i, accumulator);
        }

        return result;
    }

    /**
     * Checks whether this matrix compiles with given {@code predicate} or not.
     * 
     * @param predicate the matrix predicate
     *
     * @return whether this matrix compiles with predicate
     */
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

    /**
     * Checks whether this matrix compiles with given {@code predicate} or not.
     *
     * @param predicate the advanced matrix predicate
     *
     * @return whether this matrix compiles with predicate
     */
    public boolean is(AdvancedMatrixPredicate predicate) {
        return predicate.test(this);
    }

    /**
     * Checks whether this matrix compiles with given {@code predicate} or not.
     *
     * @param predicate the matrix predicate
     *
     * @return whether this matrix compiles with predicate
     */
    public boolean non(MatrixPredicate predicate) {
        return !is(predicate);
    }

    /**
     * Checks whether this matrix compiles with given {@code predicate} or not.
     *
     * @param predicate the advanced matrix predicate
     *
     * @return whether this matrix compiles with predicate
     */
    public boolean non(AdvancedMatrixPredicate predicate) {
        return !is(predicate);
    }

    /**
     * Converts this matrix into the row vector.
     *
     * @return the row vector of this matrix
     */
    public Vector toRowVector() {
        return getRow(0);
    }

    /**
     * Converts this matrix into the column vector.
     *
     * @return the column vector of this matrix
     */
    public Vector toColumnVector() {
        return getColumn(0);
    }

    /**
     * Creates a new solver by given {@code factory} of this matrix.
     *
     * @param factory the solver factory
     *
     * @return the linear system solver of this matrix
     */
    public LinearSystemSolver withSolver(LinearAlgebra.SolverFactory factory) {
        return factory.create(this);
    }

    /**
     * Creates a new inverter by given {@code factory} of this matrix.
     *
     * @param factory the inverter factory
     *
     * @return the inverter of this matrix
     */
    public MatrixInverter withInverter(LinearAlgebra.InverterFactory factory) {
        return factory.create(this);
    }

    /**
     * Creates a new decompositor by given {@code factory} of this matrix.
     *
     * @param factory the decompositor factory
     *
     * @return the decompositor of this matrix
     */
    public MatrixDecompositor withDecompositor(LinearAlgebra.DecompositorFactory factory) {
        return factory.create(this);
    }

    /**
     * Returns true when matrix is equal to given {@code matrix} with given {@code precision}
     *
     * @param matrix matrix
     * @param precision given precision
     *
     * @return equals of this matrix to that
     */
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

    /**
     * Converts this matrix into the string representation.
     *
     * @param formatter the number formatter
     *
     * @return the matrix converted to a string
     */
    public String mkString(NumberFormat formatter) {
        return mkString(formatter, DEFAULT_ROWS_DELIMITER, DEFAULT_COLUMNS_DELIMITER);
    }

    /**
     * Converts this matrix into the string representation.
     *
     * @param rowsDelimiter the rows' delimiter
     * @param columnsDelimiter the columns' delimiter
     *
     * @return the matrix converted to a string
     */
    public String mkString(String rowsDelimiter, String columnsDelimiter) {
        return mkString(DEFAULT_FORMATTER, rowsDelimiter, columnsDelimiter);
    }

    /**
     * Converts this matrix into the string representation.
     *
     * @param formatter the number formatter
     * @param rowsDelimiter the rows' delimiter
     * @param columnsDelimiter the columns' delimiter
     *
     * @return the matrix converted to a string
     */
    public String mkString(NumberFormat formatter, String rowsDelimiter, String columnsDelimiter) {
        // TODO: rewrite using iterators
        int[] formats = new int[columns];

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
                        .append(j < columns - 1 ? columnsDelimiter : "");
            }
            sb.append(rowsDelimiter);
        }

        return sb.toString();
    }

    @Override
    public String toString() {
        return mkString(DEFAULT_FORMATTER, DEFAULT_ROWS_DELIMITER, DEFAULT_COLUMNS_DELIMITER);
    }

    /**
     * Returns a matrix iterator.
     *
     * @return a matrix iterator
     */
    @Override
    public MatrixIterator iterator() {
        return rowMajorIterator();
    }

    /**
     * Returns a row-major matrix iterator.
     *
     * @return a row-major matrix iterator.
     */
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
                return Matrix.this.get(rowIndex(), columnIndex());
            }

            @Override
            public void set(double value) {
                Matrix.this.set(rowIndex(), columnIndex(), value);
            }

            @Override
            public boolean hasNext() {
                return i + 1 < limit;
            }

            @Override
            public Double next() {
                if(!hasNext()) {
                    throw new NoSuchElementException();
                }

                i++;
                return get();
            }
        };
    }

    /**
     * Returns a column-major matrix iterator.
     *
     * @return a column-major matrix iterator.
     */
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
                return Matrix.this.get(rowIndex(), columnIndex());
            }

            @Override
            public void set(double value) {
                Matrix.this.set(rowIndex(), columnIndex(), value);
            }

            @Override
            public boolean hasNext() {
                return i + 1 < limit;
            }

            @Override
            public Double next() {
                if(!hasNext()) {
                    throw new NoSuchElementException();
                }
                i++;
                return get();
            }
        };
    }

    /**
     * Returns a vector iterator of the given row {code i}.
     *
     * @return a vector iterator
     */
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
                return Matrix.this.get(ii, j);
            }

            @Override
            public void set(double value) {
                Matrix.this.set(ii, j, value);
            }

            @Override
            public boolean hasNext() {
                return j + 1 < columns;
            }

            @Override
            public Double next() {
                if(!hasNext()) {
                    throw new NoSuchElementException();
                }
                j++;
                return get();
            }
        };
    }

    /**
     * Returns a vector iterator of the given column {code j}.
     *
     * @return a vector iterator
     */
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
                 return Matrix.this.get(i, jj);
             }

             @Override
             public void set(double value) {
                 Matrix.this.set(i, jj, value);
             }

             @Override
             public boolean hasNext() {
                 return i + 1 < rows;
             }

             @Override
             public Double next() {
                 if(!hasNext()) {
                     throw new NoSuchElementException();
                 }
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

    /**
     * Converts this matrix using the given {@code factory}.
     *
     * @param factory the factory that creates an output matrix
     * @param <T> type of the result matrix
     *
     * @return converted matrix
     */
    public <T extends Matrix> T to(MatrixFactory<T> factory) {
        T result = factory.apply(rows, columns);
        apply(LinearAlgebra.IN_PLACE_COPY_MATRIX_TO_MATRIX, result);
        return result;
    }

    /**
     * Converts this matrix into a sparse matrix.
     *
     * @return a sparse matrix
     */
    public SparseMatrix toSparseMatrix() {
        return to(Matrices.SPARSE);
    }

    /**
     * Converts this matrix into a dense matrix.
     *
     * @return a dense matrix
     */
    public DenseMatrix toDenseMatrix() {
        return to(Matrices.DENSE);
    }

    /**
     * Converts this matrix into a row-major sparse matrix.
     *
     * @return a row-major sparse matrix
     */
    public RowMajorSparseMatrix toRowMajorSparseMatrix() {
        return to(Matrices.SPARSE_ROW_MAJOR);
    }

    /**
     * Converts this matrix into a column-major sparse matrix.
     *
     * @return a row-major sparse matrix
     */
    public ColumnMajorSparseMatrix toColumnMajorSparseMatrix() {
        return to(Matrices.SPARSE_COLUMN_MAJOR);
    }

    /**
     * Converts this matrix into the CSV (Comma Separated Value) string.
     *
     * @return a CSV string representing this matrix
     */
    public String toCSV() {
        return toCSV(DEFAULT_FORMATTER);
    }

    /**
     * Converts this matrix into the Matrix Market string.
     *
     * @return a string in Matrix Market format representing this matrix;
     */
    public String toMatrixMarket() {
        return toMatrixMarket(DEFAULT_FORMATTER);
    }

    /**
     * Converts this matrix into the CSV (Comma Separated Value) string
     * using the given {@code formatter}.
     *
     * @param formatter the number formatter
     *
     * @return a CSV string representing this matrix
     */
    public String toCSV(NumberFormat formatter) {
        return mkString(formatter, "\n", ", ");
    }

    protected void ensureDimensionsAreCorrect(int rows, int columns) {
        if (rows < 0 || columns < 0) {
            fail("Wrong matrix dimensions: " + rows + "x" + columns);
        }
        if (rows == Integer.MAX_VALUE || columns == Integer.MAX_VALUE) {
            fail("Wrong matrix dimensions: use 'Integer.MAX_VALUE - 1' instead.");
        }
    }

    protected void ensureIndexArgumentsAreInBounds(int i, int j) {
        if (i < 0 || i >= rows) {
            fail(String.format("Bad row argument %d; out of bounds", i));
        }

        if (j < 0 || j >= columns) {
            fail(String.format("Bad column argument %d; out of bounds", j));
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
}
