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
 * Contributor(s): Yuriy Drozd
 *                 Ewald Grusk
 *                 Maxim Samoylov
 *                 Miron Aseev
 *                 Todd Brunhoff
 *
 */

package org.la4j.matrix;

import org.la4j.LinearAlgebra;
import org.la4j.io.MatrixMarketStream;
import org.la4j.io.SymbolSeparatedStream;
import org.la4j.matrix.functor.AdvancedMatrixPredicate;
import org.la4j.matrix.functor.MatrixAccumulator;
import org.la4j.matrix.functor.MatrixFunction;
import org.la4j.matrix.functor.MatrixPredicate;
import org.la4j.matrix.source.Array1DMatrixSource;
import org.la4j.matrix.source.Array2DMatrixSource;
import org.la4j.matrix.source.IdentityMatrixSource;
import org.la4j.matrix.source.LoopbackMatrixSource;
import org.la4j.matrix.source.MatrixSource;
import org.la4j.matrix.source.RandomMatrixSource;
import org.la4j.matrix.source.RandomSymmetricMatrixSource;
import org.la4j.matrix.source.StreamMatrixSource;

import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;

public final class Matrices {

    /**
     * The machine epsilon, that is calculated at runtime.
     */
    public static final double EPS = LinearAlgebra.EPS;

    /**
     * Exponent of machine epsilon
     */
    public static final int ROUND_FACTOR = LinearAlgebra.ROUND_FACTOR;

    private static class DiagonalMatrixPredicate implements MatrixPredicate {
        @Override
        public boolean test(int rows, int columns) {
            return rows == columns;
        }

        @Override
        public boolean test(int i, int j, double value) {
            return (i == j) || Math.abs(value) < EPS;
        }
    }

    private static class IdentityMatrixPredicate implements MatrixPredicate {
        @Override
        public boolean test(int rows, int columns) {
            return rows == columns;
        }

        @Override
        public boolean test(int i, int j, double value) {
            return (i == j) ? Math.abs(1.0 - value) < EPS 
                            : Math.abs(value) < EPS; 
        }
    }

    private static class ZeroMatrixPredicate implements MatrixPredicate {
        @Override
        public boolean test(int rows, int columns) {
            return true;
        }

        @Override
        public boolean test(int i, int j, double value) {
            return Math.abs(value) < EPS;
        }
    }

    private static class TridiagonalMatrixPredicate implements MatrixPredicate {
        @Override
        public boolean test(int rows, int columns) {
            return rows == columns;
        }

        @Override
        public boolean test(int i, int j, double value) {
            return Math.abs(i - j) <= 1 || Math.abs(value) < EPS;
        }
    }

    private static class PositiveMatrixPredicate implements MatrixPredicate {
        @Override
        public boolean test(int rows, int columns) {
            return true;
        }

        @Override
        public boolean test(int i, int j, double value) {
            return value > 0.0;
        }
    }

    private static class NegativeMatrixPredicate implements MatrixPredicate {
        @Override
        public boolean test(int rows, int columns) {
            return true;
        }

        @Override
        public boolean test(int i, int j, double value) {
            return value < 0.0;
        }
    }

    private static class LowerBidiagonalMatrixPredicate 
            implements MatrixPredicate {

        @Override
        public boolean test(int rows, int columns) {
            return rows == columns;
        }

        @Override
        public boolean test(int i, int j, double value) {
            return !((i == j) || (i == j + 1)) || Math.abs(value) < EPS;
        }
    }

    private static class UpperBidiagonalMatrixPredicate 
            implements MatrixPredicate {

        @Override
        public boolean test(int rows, int columns) {
            return rows == columns;
        }

        @Override
        public boolean test(int i, int j, double value) {
            return !((i == j) || (i == j - 1)) || Math.abs(value) < EPS;
        }
    }

    private static class LowerTriangularMatrixPredicate 
            implements MatrixPredicate {

        @Override
        public boolean test(int rows, int columns) {
            return rows == columns;
        }

        @Override
        public boolean test(int i, int j, double value) {
            return (i <= j) || Math.abs(value) < EPS;
        }
    }

    private static class UpperTriangularMatrixPredicate 
            implements MatrixPredicate {

        @Override
        public boolean test(int rows, int columns) {
            return rows == columns;
        }

        @Override
        public boolean test(int i, int j, double value) {
            return (i >= j) || Math.abs(value) < EPS;
        }
    }

    private static class SymmetricMatrixPredicate
            implements AdvancedMatrixPredicate {

        @Override
        public boolean test(Matrix matrix) {
            if (matrix.rows() != matrix.columns()) {
                return false;
            }

            for (int i = 0; i < matrix.rows(); i++) {
                for (int j = i + 1; j < matrix.columns(); j++) {
                    double a = matrix.get(i, j);
                    double b = matrix.get(j, i);
                    double diff = Math.abs(a - b);
                    if (diff / Math.max(Math.abs(a), Math.abs(b)) > EPS) {
                        return false;
                    }
                }
            }

            return true;
        }
    }

    private static class DiagonallyDominantPredicate
            implements AdvancedMatrixPredicate {

        @Override
        public boolean test(Matrix matrix) {
            if (matrix.rows() != matrix.columns()) {
                return false;
            }

            for (int i = 0; i < matrix.rows(); i++) {
                double sum = 0;
                for (int j = 0; j < matrix.columns(); j++) {
                    if (i != j) {
                        sum += Math.abs(matrix.get(i, j));
                    }
                }
                if (sum > Math.abs(matrix.get(i, i)) - EPS) {
                    return false;
                }
            }

            return true;
        }
    }

    private static class InvertiblePredicate implements AdvancedMatrixPredicate {

        @Override
        public boolean test(Matrix matrix) {
            return matrix.rows() == matrix.columns() && matrix.determinant() != 0.0;
        }
    }

    private static class SquareMatrixPredicate implements AdvancedMatrixPredicate {

        @Override
        public boolean test(Matrix matrix) {
            return matrix.rows() == matrix.columns();
        }
    }

    private static class PositiveDefiniteMatrixPredicate implements AdvancedMatrixPredicate {

        @Override
        public boolean test(Matrix matrix) {
            if (matrix.rows() != matrix.columns()) {
                return false;
            }

            int size = matrix.columns();
            int currentSize = 1;

            while (currentSize <= size) {
                Matrix topLeftMatrix = matrix.sliceTopLeft(currentSize, currentSize);

                if (topLeftMatrix.determinant() < 0) {
                    return false;
                }

                currentSize++;
            }

            return true;
        }
    }

    private static class IncMatrixFunction 
            implements MatrixFunction {
        @Override
        public double evaluate(int i, int j, double value) {
            return value + 1.0;
        }
    }

    private static class DecMatrixFunction 
            implements MatrixFunction {
        @Override
        public double evaluate(int i, int j, double value) {
            return value - 1.0;
        }
    }

    private static class InvMatrixFunction 
            implements MatrixFunction {
        @Override
        public double evaluate(int i, int j, double value) {
            return -value;
        }
    }

    private static class ConstMatrixFunction
            implements MatrixFunction {

        private double arg;

        public ConstMatrixFunction(double arg) {
            this.arg = arg;
        }

        @Override
        public double evaluate(int i, int j, double value) {
            return arg;
        }
    }

    private static class PlusMatrixFunction 
            implements MatrixFunction {

        private double arg;

        public PlusMatrixFunction(double arg) {
            this.arg = arg;
        }

        @Override
        public double evaluate(int i, int j, double value) {
            return value + arg; 
        }
    }

    private static class MinusMatrixFunction 
            implements MatrixFunction {

        private double arg;

        public MinusMatrixFunction(double arg) {
            this.arg = arg;
        }

        @Override
        public double evaluate(int i, int j, double value) {
            return value - arg; 
        }
    }

    private static class MulMatrixFunction 
            implements MatrixFunction {

        private double arg;

        public MulMatrixFunction(double arg) {
            this.arg = arg;
        }

        @Override
        public double evaluate(int i, int j, double value) {
            return value * arg; 
        }
    }

    private static class DivMatrixFunction 
            implements MatrixFunction {

        private double arg;

        public DivMatrixFunction(double arg) {
            this.arg = arg;
        }

        @Override
        public double evaluate(int i, int j, double value) {
            return value / arg; 
        }
    }

    private static class ModMatrixFunction
            implements MatrixFunction {

        private double arg;

        public ModMatrixFunction(double arg) {
            this.arg = arg;
        }

        @Override
        public double evaluate(int i, int j, double value) {
            return value % arg;
        }
    }

    private static class SumMatrixAccumulator
            implements MatrixAccumulator {

        private BigDecimal result;
        private final double neutral;

        public SumMatrixAccumulator(double neutral) {
            this.neutral = neutral;
            this.result = new BigDecimal(neutral);
        }

        @Override
        public void update(int i, int j, double value) {
            result = result.add(new BigDecimal(value));
        }

        @Override
        public double accumulate() {
            double value = result.setScale(Matrices.ROUND_FACTOR, RoundingMode.CEILING).doubleValue();
            result = new BigDecimal(neutral);
            return value;
        }
    }

    private static class ProductMatrixAccumulator
            implements MatrixAccumulator {

        private BigDecimal result;
        private final double neutral;

        public ProductMatrixAccumulator(double neutral) {
            this.neutral = neutral;
            this.result = new BigDecimal(neutral);
        }

        @Override
        public void update(int i, int j, double value) {
            result = result.multiply(new BigDecimal(value));
        }

        @Override
        public double accumulate() {
            double value = result.setScale(Matrices.ROUND_FACTOR, RoundingMode.CEILING).doubleValue();
            result = new BigDecimal(neutral);
            return value;
        }
    }

    private static class FunctionMatrixAccumulator 
                implements MatrixAccumulator {

        private final MatrixAccumulator accumulator;
        private final MatrixFunction function;

        public FunctionMatrixAccumulator(MatrixAccumulator accumulator,
                MatrixFunction function) {

            this.accumulator = accumulator;
            this.function = function;
        }

        @Override
        public void update(int i, int j, double value) {
            accumulator.update(i, j, function.evaluate(i, j, value));
        }

        @Override
        public double accumulate() {
            return accumulator.accumulate();
        }
    }

    private static class MinMatrixAccumulator
            implements MatrixAccumulator {

        private double result = Double.POSITIVE_INFINITY;

        @Override
        public void update(int i, int j, double value) {
            result = Math.min(result, value);
        }

        @Override
        public double accumulate() {
            double value = result;
            result = Double.POSITIVE_INFINITY;
            return value;
        }
    }

    private static class MaxMatrixAccumulator
            implements MatrixAccumulator {

        private double result = Double.NEGATIVE_INFINITY;

        @Override
        public void update(int i, int j, double value) {
            result = Math.max(result, value);
        }

        @Override
        public double accumulate() {
            double value = result;
            result = Double.NEGATIVE_INFINITY;
            return value;
        }
    }

    /**
     * Creates a const function that evaluates it's argument to given {@code value}.
     *
     * @param value a const value
     *
     * @return a closure object that does {@code _}
     */
    public static MatrixFunction asConstFunction(double value) {
        return new ConstMatrixFunction(value);
    }

    /**
     * Creates a plus function that adds given {@code value} to it's argument.
     *
     * @param value a value to be added to function's argument
     *
     * @return a closure object that does {@code _ + _}
     */
    public static MatrixFunction asPlusFunction(double value) {
        return new PlusMatrixFunction(value);
    }

    /**
     * Creates a minus function that subtracts given {@code value} from it's argument.
     *
     * @param value a value to be subtracted from function's argument
     *
     * @return a closure that does {@code _ - _}
     */
    public static MatrixFunction asMinusFunction(double value) {
        return new MinusMatrixFunction(value);
    }

    /**
     * Creates a mul function that multiplies given {@code value} by it's argument.
     *
     * @param value a value to be multiplied by function's argument
     *
     * @return a closure that does {@code _ * _}
     */
    public static MatrixFunction asMulFunction(double value) {
        return new MulMatrixFunction(value);
    }

    /**
     * Creates a div function that divides it's argument by given {@code value}.
     *
     * @param value a divisor value
     *
     * @return a closure that does {@code _ / _}
     */
    public static MatrixFunction asDivFunction(double value) {
        return new DivMatrixFunction(value);
    }

    /**
     * Creates a mod function that calculates the modulus of it's argument and given {@code value}.
     *
     * @param value a divisor value
     *
     * @return a closure that does {@code _ % _}
     */
    public static MatrixFunction asModFunction(double value) {
        return new ModMatrixFunction(value);
    }

    /**
     * Checks whether the matrix is a
     * <a href="http://mathworld.wolfram.com/DiagonalMatrix.html">diagonal 
     * matrix</a>.
     */
    public static final MatrixPredicate DIAGONAL_MATRIX = 
            new DiagonalMatrixPredicate();

    /**
     * Checks whether the matrix is an 
     * <a href="http://mathworld.wolfram.com/IdentityMatrix.html">identity
     * matrix</a>.
     */
    public static final MatrixPredicate IDENTITY_MATRIX = 
            new IdentityMatrixPredicate();

    /**
     * Checks whether the matrix is a
     * <a href="http://mathworld.wolfram.com/ZeroMatrix.html">zero
     * matrix</a>.
     */
    public static final MatrixPredicate ZERO_MATRIX = 
            new ZeroMatrixPredicate();

    /**
     * Checks whether the matrix is a 
     * <a href="http://mathworld.wolfram.com/TridiagonalMatrix.html">tridiagonal
     * matrix</a>.
     */
    public static final MatrixPredicate TRIDIAGONAL_MATRIX = 
            new TridiagonalMatrixPredicate();

    /**
     * Checks whether the matrix is a 
     * <a href="http://mathworld.wolfram.com/PositiveMatrix.html">positive 
     * matrix</a>.
     */
    public static final MatrixPredicate POSITIVE_MATRIX = 
            new PositiveMatrixPredicate();

    /**
     * Checks whether the matrix is a 
     * <a href="http://mathworld.wolfram.com/NegativeMatrix.html">negative 
     * matrix</a>.
     */
    public static final MatrixPredicate NEGATIVE_MATRIX = 
            new NegativeMatrixPredicate();

    /**
     * Checks whether the matrix is a lower bidiagonal matrix</a>.
     */
    public static final MatrixPredicate LOWER_BIDIAGONAL_MATRIX = 
            new LowerBidiagonalMatrixPredicate();

    /**
     * Checks whether the matrix is an upper bidiagonal matrix. 
     */
    public static final MatrixPredicate UPPER_BIDIAGONAL_MATRIX = 
            new UpperBidiagonalMatrixPredicate();

    /**
     * Checks whether the matrix is a
     * <a href="http://mathworld.wolfram.com/LowerTriangularMatrix.html">lower 
     * triangular matrix</a>.
     */
    public static final MatrixPredicate LOWER_TRIANGULAR_MARTIX = 
            new LowerTriangularMatrixPredicate();

    /**
     * Checks whether the matrix is an
     * <a href="http://mathworld.wolfram.com/UpperTriangularMatrix.html">upper 
     * triangular matrix</a>.
     */
    public static final MatrixPredicate UPPER_TRIANGULAR_MATRIX = 
            new UpperTriangularMatrixPredicate();

    /**
     * Checks whether the matrix is a 
     * <a href="http://mathworld.wolfram.com/SymmetricMatrix.html">symmetric 
     * matrix</a>. 
     */
    public static final AdvancedMatrixPredicate SYMMETRIC_MATRIX = 
            new SymmetricMatrixPredicate();

    /**
     * Checks whether the matrix is a
     * <a href="http://en.wikipedia.org/wiki/Diagonally_dominant_matrix">dioganally dominant matrix</a>.
     */
    public static final AdvancedMatrixPredicate DIAGONALLY_DOMINANT_MATRIX = 
            new DiagonallyDominantPredicate();

    /**
     * Checks whether the matrix is
     * <a href="http://en.wikipedia.org/wiki/Invertible_matrix">invertible</a>.
     */
    public static final AdvancedMatrixPredicate INVERTIBLE_MATRIX =
            new InvertiblePredicate();

    /**
     * Checks whether the matrix is positive definite.
     */
    public static final AdvancedMatrixPredicate POSITIVE_DEFINITE_MATRIX =
            new PositiveDefiniteMatrixPredicate();

    /**
     * Checks whether the matrix is
     * <a href="http://en.wikipedia.org/wiki/Square_matrix">square</a>.
     */
    public static final AdvancedMatrixPredicate SQUARE_MATRIX =
            new SquareMatrixPredicate();

    /**
     * Increases each element of matrix by <code>1</code>.
     */
    public static final MatrixFunction INC_FUNCTION = 
            new IncMatrixFunction();

    /**
     * Decreases each element of matrix by <code>1</code>.
     */
    public static final MatrixFunction DEC_FUNCTION = 
            new DecMatrixFunction();

    /**
     * Inverts each element of matrix.  
     */
    public static final MatrixFunction INV_FUNCTION = 
            new InvMatrixFunction();

    /**
     * Creates a singleton 1x1 matrix of given {@code value}.
     * 
     * @param value the singleton value
     *
     * @return a singleton matrix
     */
    public static Matrix asSingletonMatrix(double value) {
        return LinearAlgebra.DEFAULT_FACTORY.createMatrix(new double[][]{{ value }});
    }

    /**
     * Creates a matrix source of given {@code matrix}.
     * 
     * @param matrix the source matrix
     *
     * @return a matrix source
     */
    public static MatrixSource asMatrixSource(Matrix matrix) {
        return new LoopbackMatrixSource(matrix);
    }

    /**
     * Creates a 1D-array matrix source of given {@code array} reference.
     * 
     * @param rows the number of rows in the source
     * @param columns the number of columns in the source
     * @param array the array reference
     *
     * @return a 1D-array matrix source
     */
    public static MatrixSource asArray1DSource(int rows, int columns, double[] array) {
        return new Array1DMatrixSource(rows, columns, array);
    }

    /**
     * Creates a 2D-array matrix source of given {@code array} reference.
     * 
     * @param array the array reference
     *
     * @return a 2D-array matrix source
     */
    public static MatrixSource asArray2DSource(double[][] array) {
        return new Array2DMatrixSource(array);
    }

    /**
     * Creates an identity matrix source of given {@code size}.
     * 
     * @param size the source size
     *
     * @return an identity matrix source
     */
    public static MatrixSource asIdentitySource(int size) {
        return new IdentityMatrixSource(size);
    }

    /**
     * Creates a random matrix source of specified dimensions.
     * 
     * @param rows the number of rows in the source
     * @param columns the number of columns in the source
     *
     * @return a random matrix source
     */
    public static MatrixSource asRandomSource(int rows, int columns) {
        return new RandomMatrixSource(rows, columns);
    }

    /**
     * Creates a random symmetric matrix source of given {@code size}.
     * 
     * @param size the size of the source
     *
     * @return a random symmetric matrix source
     */
    public static MatrixSource asRandomSymmetricSource(int size) {
        return new RandomSymmetricMatrixSource(size);
    }

    /**
     * Creates a MatrixMarket stream source of given input stream {@code in}.
     *
     * @param in the input stream
     *
     * @return a MatrixMarket stream source
     */
    public static MatrixSource asMatrixMarketSource(InputStream in) {
        return new StreamMatrixSource(new MatrixMarketStream(in));
    }

    /**
     * Creates a symbol separated stream source (like CSV) of given input stream {@code in}.
     *
     * @param in the input stream
     *
     * @return a symbol separated stream source
     */
    public static MatrixSource asSymbolSeparatedSource(InputStream in) {
        return new StreamMatrixSource(new SymbolSeparatedStream(in));
    }

    /**
     * Creates a symbol separated stream source (like CSV) of given input stream {@code in}.
     *
     * @param in the input stream
     * @param separator the values' separator
     *
     * @return a symbol separated stream source
     */
    public static MatrixSource asSymbolSeparatedSource(InputStream in, String separator) {
        return new StreamMatrixSource(new SymbolSeparatedStream(in, separator));
    }

    /**
     * Makes a minimum matrix accumulator that accumulates the minimum of matrix elements.
     *
     * @return a minimum vector accumulator
     */
    public static MatrixAccumulator mkMinAccumulator() {
        return new MinMatrixAccumulator();
    }

    /**
     * Makes a maximum matrix accumulator that accumulates the maximum of matrix elements.
     *
     * @return a maximum vector accumulator
     */
    public static MatrixAccumulator mkMaxAccumulator() {
        return new MaxMatrixAccumulator();
    }

    /**
     * Creates a sum matrix accumulator that calculates the sum of all elements in the matrix.
     *
     * @param neutral the neutral value
     *
     * @return a sum accumulator
     */
    public static MatrixAccumulator asSumAccumulator(double neutral) {
        return new SumMatrixAccumulator(neutral);
    }

    /**
     * Creates a product matrix accumulator that calculates the product of all elements in the matrix.
     *
     * @param neutral the neutral value
     *
     * @return a product accumulator
     */
    public static MatrixAccumulator asProductAccumulator(double neutral) {
        return new ProductMatrixAccumulator(neutral);
    }

    /**
     * Creates a sum function accumulator, that calculates the sum of all
     * elements in the matrix after applying given {@code function} to each of them.
     *
     * @param neutral the neutral value
     * @param function the matrix function
     *
     * @returna a sum function accumulator
     */
    public static MatrixAccumulator asSumFunctionAccumulator(double neutral, MatrixFunction function) {
        return new FunctionMatrixAccumulator(new SumMatrixAccumulator(neutral), function);
    }

    /**
     * Creates a product function accumulator, that calculates the product of
     * all elements in the matrix after applying given {@code function} to
     * each of them.
     *
     * @param neutral the neutral value
     * @param function the matrix function
     *
     * @return a product function accumulator
     */
    public static MatrixAccumulator asProductFunctionAccumulator(double neutral, MatrixFunction function) {
        return new FunctionMatrixAccumulator(new ProductMatrixAccumulator(neutral), function);
    }
}
