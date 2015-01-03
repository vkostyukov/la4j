/*
 * Copyright 2011-2014, by Vladimir Kostyukov and Contributors.
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
import org.la4j.matrix.dense.Basic1DMatrix;
import org.la4j.matrix.dense.Basic2DMatrix;
import org.la4j.matrix.functor.AdvancedMatrixPredicate;
import org.la4j.matrix.functor.MatrixAccumulator;
import org.la4j.matrix.functor.MatrixFunction;
import org.la4j.matrix.functor.MatrixPredicate;
import org.la4j.matrix.functor.MatrixProcedure;
import org.la4j.matrix.source.Array1DMatrixSource;
import org.la4j.matrix.source.Array2DMatrixSource;
import org.la4j.matrix.source.IdentityMatrixSource;
import org.la4j.matrix.source.LoopbackMatrixSource;
import org.la4j.matrix.source.MatrixSource;
import org.la4j.matrix.source.RandomMatrixSource;
import org.la4j.matrix.source.RandomSymmetricMatrixSource;
import org.la4j.matrix.source.StreamMatrixSource;
import org.la4j.matrix.sparse.CCSMatrix;
import org.la4j.matrix.sparse.CRSMatrix;

import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

public final class Matrices {

    /**
     * The machine epsilon, that is calculated at runtime.
     */
    public static final double EPS = LinearAlgebra.EPS;

    /**
     * Exponent of machine epsilon
     */
    public static final int ROUND_FACTOR = LinearAlgebra.ROUND_FACTOR;

    /**
     * Checks whether the matrix is a
     * <a href="http://mathworld.wolfram.com/DiagonalMatrix.html">diagonal
     * matrix</a>.
     */
    public static final MatrixPredicate DIAGONAL_MATRIX = new MatrixPredicate() {
        @Override
        public boolean test(int rows, int columns) {
            return rows == columns;
        }

        @Override
        public boolean test(int i, int j, double value) {
            return (i == j) || Math.abs(value) < EPS;
        }
    };

    /**
     * Checks whether the matrix is an
     * <a href="http://mathworld.wolfram.com/IdentityMatrix.html">identity
     * matrix</a>.
     */
    public static final MatrixPredicate IDENTITY_MATRIX = new MatrixPredicate() {
        @Override
        public boolean test(int rows, int columns) {
            return rows == columns;
        }

        @Override
        public boolean test(int i, int j, double value) {
            return (i == j) ? Math.abs(1.0 - value) < EPS
                    : Math.abs(value) < EPS;
        }
    };

    /**
     * Checks whether the matrix is a
     * <a href="http://mathworld.wolfram.com/ZeroMatrix.html">zero
     * matrix</a>.
     */
    public static final MatrixPredicate ZERO_MATRIX = new MatrixPredicate() {
        @Override
        public boolean test(int rows, int columns) {
            return true;
        }

        @Override
        public boolean test(int i, int j, double value) {
            return Math.abs(value) < EPS;
        }
    };

    /**
     * Checks whether the matrix is a
     * <a href="http://mathworld.wolfram.com/TridiagonalMatrix.html">tridiagonal
     * matrix</a>.
     */
    public static final MatrixPredicate TRIDIAGONAL_MATRIX = new MatrixPredicate() {
        @Override
        public boolean test(int rows, int columns) {
            return rows == columns;
        }

        @Override
        public boolean test(int i, int j, double value) {
            return Math.abs(i - j) <= 1 || Math.abs(value) < EPS;
        }
    };

    /**
     * Checks whether the matrix is a
     * <a href="http://mathworld.wolfram.com/PositiveMatrix.html">positive
     * matrix</a>.
     */
    public static final MatrixPredicate POSITIVE_MATRIX = new MatrixPredicate() {
        @Override
        public boolean test(int rows, int columns) {
            return true;
        }

        @Override
        public boolean test(int i, int j, double value) {
            return value > 0.0;
        }
    };

    /**
     * Checks whether the matrix is a
     * <a href="http://mathworld.wolfram.com/NegativeMatrix.html">negative
     * matrix</a>.
     */
    public static final MatrixPredicate NEGATIVE_MATRIX = new MatrixPredicate() {
        @Override
        public boolean test(int rows, int columns) {
            return true;
        }

        @Override
        public boolean test(int i, int j, double value) {
            return value < 0.0;
        }
    };

    /**
     * Checks whether the matrix is a lower bi-diagonal matrix</a>.
     */
    public static final MatrixPredicate LOWER_BIDIAGONAL_MATRIX = new MatrixPredicate() {
        @Override
        public boolean test(int rows, int columns) {
            return rows == columns;
        }

        @Override
        public boolean test(int i, int j, double value) {
            return !((i == j) || (i == j + 1)) || Math.abs(value) < EPS;
        }
    };

    /**
     * Checks whether the matrix is an upper bidiagonal matrix.
     */
    public static final MatrixPredicate UPPER_BIDIAGONAL_MATRIX = new MatrixPredicate() {
        @Override
        public boolean test(int rows, int columns) {
            return rows == columns;
        }

        @Override
        public boolean test(int i, int j, double value) {
            return !((i == j) || (i == j - 1)) || Math.abs(value) < EPS;
        }
    };

    /**
     * Checks whether the matrix is a
     * <a href="http://mathworld.wolfram.com/LowerTriangularMatrix.html">lower
     * triangular matrix</a>.
     */
    public static final MatrixPredicate LOWER_TRIANGULAR_MATRIX = new MatrixPredicate() {
        @Override
        public boolean test(int rows, int columns) {
            return rows == columns;
        }

        @Override
        public boolean test(int i, int j, double value) {
            return (i <= j) || Math.abs(value) < EPS;
        }
    };

    /**
     * Checks whether the matrix is an
     * <a href="http://mathworld.wolfram.com/UpperTriangularMatrix.html">upper
     * triangular matrix</a>.
     */
    public static final MatrixPredicate UPPER_TRIANGULAR_MATRIX = new MatrixPredicate() {
        @Override
        public boolean test(int rows, int columns) {
            return rows == columns;
        }

        @Override
        public boolean test(int i, int j, double value) {
            return (i >= j) || Math.abs(value) < EPS;
        }
    };

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

    /**
     * Checks whether the matrix is a
     * <a href="http://mathworld.wolfram.com/SymmetricMatrix.html">symmetric
     * matrix</a>.
     */
    public static final AdvancedMatrixPredicate SYMMETRIC_MATRIX =
            new SymmetricMatrixPredicate();

    /**
     * Checks whether the matrix is a
     * <a href="http://en.wikipedia.org/wiki/Diagonally_dominant_matrix">diagonally dominant matrix</a>.
     */
    public static final AdvancedMatrixPredicate DIAGONALLY_DOMINANT_MATRIX =
            new DiagonallyDominantPredicate();

    /**
     * Checks whether the matrix is positive definite.
     */
    public static final AdvancedMatrixPredicate POSITIVE_DEFINITE_MATRIX =
            new PositiveDefiniteMatrixPredicate();

    /**
     * A matrix factory that produces zero {@link Basic2DMatrix}.
     */
    public static final MatrixFactory<Basic2DMatrix> BASIC_2D =
        new MatrixFactory<Basic2DMatrix>() {
            @Override
            public Basic2DMatrix apply(int rows, int columns) {
                return Basic2DMatrix.zero(rows, columns);
            }
        };

    /**
     * A matrix factory that produces zero {@link Basic1DMatrix}.
     */
    public static final MatrixFactory<Basic1DMatrix> BASIC_1D =
        new MatrixFactory<Basic1DMatrix>() {
            @Override
            public Basic1DMatrix apply(int rows, int columns) {
                return Basic1DMatrix.zero(rows, columns);
            }
        };

    /**
     * A default matrix factory for dense matrices.
     */
    public static final MatrixFactory<Basic2DMatrix> DENSE = BASIC_2D;

    /**
     * A matrix factory that produces zero {@link CCSMatrix}.
     */
    public static final MatrixFactory<CCSMatrix> CCS =
        new MatrixFactory<CCSMatrix>() {
            @Override
            public CCSMatrix apply(int rows, int columns) {
                return CCSMatrix.zero(rows, columns);
            }
        };

    /**
     * A matrix factory that produces zero {@link CRSMatrix}.
     */
    public static final MatrixFactory<CRSMatrix> CRS =
        new MatrixFactory<CRSMatrix>() {
            @Override
            public CRSMatrix apply(int rows, int columns) {
                return CRSMatrix.zero(rows, columns);
            }
        };

    /**
     * A default factory for sparse matrices.
     */
    public static final MatrixFactory<CRSMatrix> SPARSE = CRS;

    /**
     * A default factory for sparse row-major matrices.
     */
    public static final MatrixFactory<CRSMatrix> SPARSE_ROW_MAJOR = CRS;

    /**
     * A default factory for sparse column-major matrices.
     */
    public static final MatrixFactory<CCSMatrix> SPARSE_COLUMN_MAJOR = CCS;

    public static final MatrixFactory[] CONVERTERS = {
            BASIC_2D, BASIC_1D, CRS, CCS
    };

    /**
     * Increases each element of matrix by <code>1</code>.
     */
    public static final MatrixFunction INC_FUNCTION = new MatrixFunction() {
        @Override
        public double evaluate(int i, int j, double value) {
            return value + 1.0;
        }
    };

    /**
     * Decreases each element of matrix by <code>1</code>.
     */
    public static final MatrixFunction DEC_FUNCTION = new MatrixFunction() {
        @Override
        public double evaluate(int i, int j, double value) {
            return value - 1.0;
        }
    };

    /**
     * Inverts each element of matrix.
     */
    public static final MatrixFunction INV_FUNCTION = new MatrixFunction() {
        @Override
        public double evaluate(int i, int j, double value) {
            return -value;
        }
    };

    /**
     * Creates a const function that evaluates it's argument to given {@code value}.
     *
     * @param arg a const value
     *
     * @return a closure object that does {@code _}
     */
    public static MatrixFunction asConstFunction(final double arg) {
        return new MatrixFunction() {
            @Override
            public double evaluate(int i, int j, double value) {
                return arg;
            }
        };
    }

    /**
     * Creates a plus function that adds given {@code value} to it's argument.
     *
     * @param arg a value to be added to function's argument
     *
     * @return a closure object that does {@code _ + _}
     */
    public static MatrixFunction asPlusFunction(final double arg) {
        return new MatrixFunction() {
            @Override
            public double evaluate(int i, int j, double value) {
                return value + arg;
            }
        };
    }

    /**
     * Creates a minus function that subtracts given {@code value} from it's argument.
     *
     * @param arg a value to be subtracted from function's argument
     *
     * @return a closure that does {@code _ - _}
     */
    public static MatrixFunction asMinusFunction(final double arg) {
        return new MatrixFunction() {
            @Override
            public double evaluate(int i, int j, double value) {
                return value - arg;
            }
        };
    }

    /**
     * Creates a mul function that multiplies given {@code value} by it's argument.
     *
     * @param arg a value to be multiplied by function's argument
     *
     * @return a closure that does {@code _ * _}
     */
    public static MatrixFunction asMulFunction(final double arg) {
        return new MatrixFunction() {
            @Override
            public double evaluate(int i, int j, double value) {
                return value * arg;
            }
        };
    }

    /**
     * Creates a div function that divides it's argument by given {@code value}.
     *
     * @param arg a divisor value
     *
     * @return a closure that does {@code _ / _}
     */
    public static MatrixFunction asDivFunction(final double arg) {
        return new MatrixFunction() {
            @Override
            public double evaluate(int i, int j, double value) {
                return value / arg;
            }
        };
    }

    /**
     * Creates a mod function that calculates the modulus of it's argument and given {@code value}.
     *
     * @param arg a divisor value
     *
     * @return a closure that does {@code _ % _}
     */
    public static MatrixFunction asModFunction(final double arg) {
        return new MatrixFunction() {
            @Override
            public double evaluate(int i, int j, double value) {
                return value % arg;
            }
        };
    }

    /**
     * Makes a minimum matrix accumulator that accumulates the minimum of matrix elements.
     *
     * @return a minimum vector accumulator
     */
    public static MatrixAccumulator mkMinAccumulator() {
        return new MatrixAccumulator() {
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
        };
    }

    /**
     * Makes a maximum matrix accumulator that accumulates the maximum of matrix elements.
     *
     * @return a maximum vector accumulator
     */
    public static MatrixAccumulator mkMaxAccumulator() {
        return new MatrixAccumulator() {
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
        };
    }

    /**
     * Creates a sum matrix accumulator that calculates the sum of all elements in the matrix.
     *
     * @param neutral the neutral value
     *
     * @return a sum accumulator
     */
    public static MatrixAccumulator asSumAccumulator(final double neutral) {
        return new MatrixAccumulator() {
            private BigDecimal result = new BigDecimal(neutral);

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
        };
    }

    /**
     * Creates a product matrix accumulator that calculates the product of all elements in the matrix.
     *
     * @param neutral the neutral value
     *
     * @return a product accumulator
     */
    public static MatrixAccumulator asProductAccumulator(final double neutral) {
        return new MatrixAccumulator() {
            private BigDecimal result = new BigDecimal(neutral);

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
        };
    }

    /**
     * Creates a sum function accumulator, that calculates the sum of all
     * elements in the matrix after applying given {@code function} to each of them.
     *
     * @param neutral the neutral value
     * @param function the matrix function
     *
     * @return a sum function accumulator
     */
    public static MatrixAccumulator asSumFunctionAccumulator(final double neutral, final MatrixFunction function) {
        return new MatrixAccumulator() {
            private final MatrixAccumulator sumAccumulator = Matrices.asSumAccumulator(neutral);

            @Override
            public void update(int i, int j, double value) {
                sumAccumulator.update(i, j, function.evaluate(i, j, value));
            }

            @Override
            public double accumulate() {
                return sumAccumulator.accumulate();
            }
        };
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
    public static MatrixAccumulator asProductFunctionAccumulator(final double neutral,
                                                                 final MatrixFunction function) {
        return new MatrixAccumulator() {
            private final MatrixAccumulator productAccumulator = Matrices.asProductAccumulator(neutral);

            @Override
            public void update(int i, int j, double value) {
                productAccumulator.update(i, j, function.evaluate(i, j, value));
            }

            @Override
            public double accumulate() {
                return productAccumulator.accumulate();
            }
        };
    }

    /**
     * Creates an accumulator procedure that adapts a matrix accumulator for procedure
     * interface. This is useful for reusing a single accumulator for multiple fold operations
     * in multiple matrices.
     *
     * @param accumulator the matrix accumulator
     *
     * @return an accumulator procedure
     */
    public static MatrixProcedure asAccumulatorProcedure(final MatrixAccumulator accumulator) {
        return new MatrixProcedure() {
            @Override
            public void apply(int i, int j, double value) {
                accumulator.update(i, j, value);
            }
        };
    }

    /**
     * Creates a singleton 1x1 matrix of given {@code value}.
     *
     * @param value the singleton value
     *
     * @return a singleton matrix
     */
    @Deprecated
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
    @Deprecated
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
    @Deprecated
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
    @Deprecated
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
    @Deprecated
    public static MatrixSource asIdentitySource(int size) {
        return new IdentityMatrixSource(size);
    }

    /**
     * Creates a random matrix source of specified dimensions.
     *
     * @param rows the number of rows in the source
     * @param columns the number of columns in the source
     * @param random the random generator instance
     *
     * @return a random matrix source
     */
    @Deprecated
    public static MatrixSource asRandomSource(int rows, int columns, Random random) {
        return new RandomMatrixSource(rows, columns, random);
    }

    /**
     * Creates a random symmetric matrix source of given {@code size}.
     *
     * @param size the size of the source
     *
     * @return a random symmetric matrix source
     */
    @Deprecated
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
    @Deprecated
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
    @Deprecated
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
    @Deprecated
    public static MatrixSource asSymbolSeparatedSource(InputStream in, String separator) {
        return new StreamMatrixSource(new SymbolSeparatedStream(in, separator));
    }

    /**
     * Creates a default 1x1 matrix from given {@code value}.
     *
     * @param value of the matrix
     *
     * @return a default 1x1 matrix
     */
    @Deprecated
    public static Matrix asMatrix1x1(double value) {
        return LinearAlgebra.DEFAULT_FACTORY.createMatrix(new double[][]{{ value }});
    }

    /**
     * Creates a default 2x2 matrix from given {@code value}.
     *
     * @param values of the matrix
     *
     * @return a default 2x2 matrix
     */
    @Deprecated
    public static Matrix asMatrix2x2(double... values) {
        return LinearAlgebra.DEFAULT_FACTORY.createMatrix(unflatten(values, 2));
    }

    /**
     * Creates a default 3x3 matrix from given {@code value}.
     *
     * @param values of the matrix
     *
     * @return a default 3x3 matrix
     */
    @Deprecated
    public static Matrix asMatrix3x3(double... values) {
        return LinearAlgebra.DEFAULT_FACTORY.createMatrix(unflatten(values, 3));
    }

    /**
     * Creates a default 4x4 matrix from given {@code value}.
     *
     * @param values of the matrix
     *
     * @return a default 4x4 matrix
     */
    @Deprecated
    public static Matrix asMatrix4x4(double... values) {
        return LinearAlgebra.DEFAULT_FACTORY.createMatrix(unflatten(values, 4));
    }

    /**
     * TODO: It might be a good idea to put internal routines into a special utility class.
     *
     * An internal routine that un-flats given 1D {@code array} to square 2D array with size {@code n}.
     *
     * @param array the 1D array
     * @param n the size of square 2D array
     *
     * @return the square 2D array
     */
    private static double[][] unflatten(double array[], int n) {
        double result[][] = new double[n][n];

        int m = Math.min(array.length, n * n);

        for (int i = 0; i < m; i++) {
            result[i / n][i % n] = array[i];
        }

        return result;
    }
}
