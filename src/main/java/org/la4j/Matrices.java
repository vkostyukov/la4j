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

package org.la4j;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.la4j.matrix.MatrixFactory;
import org.la4j.matrix.dense.Basic1DMatrix;
import org.la4j.matrix.dense.Basic2DMatrix;
import org.la4j.matrix.functor.AdvancedMatrixPredicate;
import org.la4j.matrix.functor.MatrixAccumulator;
import org.la4j.matrix.functor.MatrixFunction;
import org.la4j.matrix.functor.MatrixPredicate;
import org.la4j.matrix.functor.MatrixProcedure;
import org.la4j.matrix.sparse.CCSMatrix;
import org.la4j.matrix.sparse.CRSMatrix;

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

    public static final MatrixFactory<?>[] CONVERTERS = {
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

    private Matrices() {}

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
     * Makes an Euclidean norm accumulator that allows to use
     * {@link org.la4j.Matrix#fold(org.la4j.vector.functor.MatrixAccumulator)}
     * method for norm calculation.
     *
     * @return an Euclidean norm accumulator
     */
    public static MatrixAccumulator mkEuclideanNormAccumulator() {
        return new MatrixAccumulator() {
            private BigDecimal result = BigDecimal.valueOf(0.0);

            @Override
            public void update(int i, int j, double value) {
                result = result.add(BigDecimal.valueOf(value * value));
            }

            @Override
            public double accumulate() {
                double value = result.setScale(Matrices.ROUND_FACTOR, RoundingMode.CEILING).doubleValue();
                result = BigDecimal.valueOf(0.0);
                return Math.sqrt(value);
            }
        };
    }
    
    /**
     * Makes an Manhattan norm accumulator that allows to use
     * {@link org.la4j.Matrix#fold(org.la4j.vector.functor.MatrixAccumulator)}
     * method for norm calculation.
     *
     * @return a Manhattan norm accumulator
     */
    public static MatrixAccumulator mkManhattanNormAccumulator() {
        return new MatrixAccumulator() {
            private double result = 0.0;

            @Override
            public void update(int i, int j, double value) {
                result += Math.abs(value);
            }

            @Override
            public double accumulate() {
                double value = result;
                result = 0.0;
                return value;
            }
        };
    }
    
    /**
     * Makes an Infinity norm accumulator that allows to use
     * {@link org.la4j.Matrix#fold(org.la4j.vector.functor.MatrixAccumulator)}
     * method for norm calculation.
     *
     * @return an Infinity norm accumulator
     */
    public static MatrixAccumulator mkInfinityNormAccumulator() {
        return new MatrixAccumulator() {
          private double result = Double.NEGATIVE_INFINITY;
          
          @Override
          public void update(int i, int j, double value) {
            result = Math.max(result, Math.abs(value));
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
            private BigDecimal result = BigDecimal.valueOf(neutral);

            @Override
            public void update(int i, int j, double value) {
                result = result.add(BigDecimal.valueOf(value));
            }

            @Override
            public double accumulate() {
                double value = result.setScale(Matrices.ROUND_FACTOR, RoundingMode.CEILING).doubleValue();
                result = BigDecimal.valueOf(neutral);
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
            private BigDecimal result = BigDecimal.valueOf(neutral);

            @Override
            public void update(int i, int j, double value) {
                result = result.multiply(BigDecimal.valueOf(value));
            }

            @Override
            public double accumulate() {
                double value = result.setScale(Matrices.ROUND_FACTOR, RoundingMode.CEILING).doubleValue();
                result = BigDecimal.valueOf(neutral);
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
}