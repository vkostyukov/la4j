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

import java.io.InputStream;
import java.util.Arrays;

import org.la4j.decomposition.CholeskyDecompositor;
import org.la4j.decomposition.EigenDecompositor;
import org.la4j.decomposition.LUDecompositor;
import org.la4j.decomposition.MatrixDecompositor;
import org.la4j.decomposition.QRDecompositor;
import org.la4j.decomposition.SingularValueDecompositor;
import org.la4j.factory.Basic1DFactory;
import org.la4j.factory.Basic2DFactory;
import org.la4j.factory.CCSFactory;
import org.la4j.factory.CRSFactory;
import org.la4j.factory.Factory;
import org.la4j.factory.SafeFactory;
import org.la4j.inversion.GaussianInvertor;
import org.la4j.inversion.MatrixInvertor;
import org.la4j.io.MatrixMarketStream;
import org.la4j.io.SymbolSeparatedStream;
import org.la4j.linear.GaussianSolver;
import org.la4j.linear.JacobiSolver;
import org.la4j.linear.LinearSystem;
import org.la4j.linear.LinearSystemSolver;
import org.la4j.linear.SeidelSolver;
import org.la4j.linear.SquareRootSolver;
import org.la4j.linear.SweepSolver;
import org.la4j.matrix.functor.AdvancedMatrixPredicate;
import org.la4j.matrix.functor.MatrixAccumulator;
import org.la4j.matrix.functor.MatrixFunction;
import org.la4j.matrix.functor.MatrixPredicate;
import org.la4j.matrix.source.Array1DMatrixSource;
import org.la4j.matrix.source.Array2DMatrixSource;
import org.la4j.matrix.source.IdentityMattixSource;
import org.la4j.matrix.source.MatrixSource;
import org.la4j.matrix.source.RandomMatrixSource;
import org.la4j.matrix.source.RandomSymmetricMatrixSource;
import org.la4j.matrix.source.SafeMatrixSource;
import org.la4j.matrix.source.StreamMatrixSource;
import org.la4j.matrix.source.UnsafeMatrixSource;
import org.la4j.vector.Vector;

public final class Matrices {

    public static final double EPS;

    // Determine the machine epsilon
    // Tolerance is 10e1
    static {
        double eps = 1.0;
        while (1 + eps > 1) {
            eps = eps / 2;
        }
        EPS = eps * 10e1;
    }

    private static class DiagonalMatrixPredicate implements MatrixPredicate {
        @Override
        public boolean test(int i, int j, double value) {
            return (i != j) ? Math.abs(value) < EPS : true;
        }
    }

    private static class IdentityMatrixPredicate implements MatrixPredicate {
        @Override
        public boolean test(int i, int j, double value) {
            return (i == j) ? Math.abs(1.0 - value) < EPS 
                            : Math.abs(value) < EPS; 
        }
    }

    private static class ZeroMatrixPredicate implements MatrixPredicate {
        @Override
        public boolean test(int i, int j, double value) {
            return Math.abs(value) < EPS;
        }
    }

    private static class TridiagonalMatrixPredicate implements MatrixPredicate {
        @Override
        public boolean test(int i, int j, double value) {
            return Math.abs(i - j) > 1 ?  Math.abs(value) < EPS : true; 
        }
    }

    private static class PositiveMatrixPredicate implements MatrixPredicate {
        @Override
        public boolean test(int i, int j, double value) {
            return value > 0;
        }
    }

    private static class NegativeMatrixPredicate implements MatrixPredicate {
        @Override
        public boolean test(int i, int j, double value) {
            return value < 0;
        }
    }

    private static class LowerBidiagonalMatrixPredicate 
            implements MatrixPredicate {
        @Override
        public boolean test(int i, int j, double value) {
            return (i == j) || (i == j + 1) 
                    ? Math.abs(value) < EPS
                    : true;
        }
    }

    private static class UpperBidiagonalMatrixPredicate 
            implements MatrixPredicate {
        @Override
        public boolean test(int i, int j, double value) {
            return (i == j) || (i == j - 1) 
                    ? Math.abs(value) < EPS : true;
        }
    }

    private static class LowerTriangularMatrixPredicate 
            implements MatrixPredicate {
        @Override
        public boolean test(int i, int j, double value) {
            return (i > j) ? Math.abs(value) < EPS : true;
        }
    }

    private static class UpperTriangularMatrixPredicate 
            implements MatrixPredicate {
        @Override
        public boolean test(int i, int j, double value) {
            return (i < j) ? Math.abs(value) < EPS : true;
        }
    }

    //
    // TODO: Issue 5
    //
    // Please, send me a pull-request, if you know how to write it better
    // 
    private static class SymmetricMatrixPredicate
            implements AdvancedMatrixPredicate {

        private double values[];
        private int size;

        @Override
        public boolean test(int rows, int columns) {
            if (rows != columns) {
                return false;
            } else {
                size = rows;
                values = new double[(size * size - size) / 2 ];
                Arrays.fill(values, Double.NaN);

                return true;
            }
        }

        @Override
        public boolean test(int i, int j, double value) {
            int offset = -1;

            if (i < j) {
                offset = j - (i + 1) + (int)((((size - 1) + (size - i)) / 2.0) 
                         * i);
            } else if (i > j) {
                offset = i - (j + 1) + (int)((((size - 1) + (size - j)) / 2.0) 
                         * j);
            } else return true;

            if (Double.isNaN(values[offset])) {
                values[offset] = value;

                return true;
            } else {
                double diff = Math.abs(value - values[offset]);

                return (value == values[offset]) ? true :
                       diff < EPS ? true :
                       diff / Math.max(Math.abs(value), Math.abs(values[offset])) 
                       < EPS;
            }
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

    private static class SumMatrixAccumulator
            implements MatrixAccumulator {

        private double result;

        public SumMatrixAccumulator(double neutral) {
            this.result = neutral;
        }

        @Override
        public void update(int i, int j, double value) {
            result += value;
        }

        @Override
        public double accumulate() {
            return result;
        }
    }

    private static class ProductMatrixAccumulator
            implements MatrixAccumulator {

        private double result;

        public ProductMatrixAccumulator(double neutral) {
            this.result = neutral;
        }

        @Override
        public void update(int i, int j, double value) {
            result *= value;
        }

        @Override
        public double accumulate() {
            return result;
        }
    }

    private static class FunctionMatrixAccumulator 
                implements MatrixAccumulator {

        private MatrixAccumulator accumulator;
        private MatrixFunction function;

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

    /**
     * Creates a plus function with specified <code>value</code>.
     * @param value
     */
    public static MatrixFunction asPlusFunction(double value) {
        return new PlusMatrixFunction(value);
    }

    public static MatrixFunction asMinusFunction(double value) {
        return new MinusMatrixFunction(value);
    }

    public static MatrixFunction asMulFunction(double value) {
        return new MulMatrixFunction(value);
    }

    public static MatrixFunction asDivFunction(double value) {
        return new DivMatrixFunction(value);
    }

    public final static MatrixPredicate DIAGONAL_MATRIX = 
            new DiagonalMatrixPredicate();

    public final static MatrixPredicate IDENTITY_MATRIX = 
            new IdentityMatrixPredicate();

    public final static MatrixPredicate ZERO_MATRIX = 
            new ZeroMatrixPredicate();

    public final static MatrixPredicate TRIDIAGONAL_MATRIX = 
            new TridiagonalMatrixPredicate();

    public final static MatrixPredicate POSITIVE_MATRIX = 
            new PositiveMatrixPredicate();

    public final static MatrixPredicate NEGATIVE_MATRIX = 
            new NegativeMatrixPredicate();

    public final static MatrixPredicate LOWER_BIDIAGONAL_MATRIX = 
            new LowerBidiagonalMatrixPredicate();

    public final static MatrixPredicate UPPER_BIDIAGONAL_MATRIX = 
            new UpperBidiagonalMatrixPredicate();

    public final static MatrixPredicate LOWER_TRIANGULAR_MARTIX = 
            new LowerTriangularMatrixPredicate();

    public final static MatrixPredicate UPPER_TRIANGULAR_MATRIX = 
            new UpperTriangularMatrixPredicate();

    public final static MatrixPredicate SYMMETRIC_MATRIX = 
            new SymmetricMatrixPredicate();

    public final static MatrixFunction INC_MATRIX = 
            new IncMatrixFunction();

    public final static MatrixFunction DEC_MATRIX = 
            new DecMatrixFunction();

    /**
     * The {@link Basic1DFactory} singleton instance.
     */
    public final static Factory BASIC1D_FACTORY = new Basic1DFactory();

    /**
     * The {@link Basic2DFactory} singleton instance.
     */
    public final static Factory BASIC2D_FACTORY = new Basic2DFactory();

    public final static Factory CRS_FACTORY = new CRSFactory();

    public final static Factory CCS_FACTORY = new CCSFactory();

    public final static Factory SAFE_BASIC1D_FACTORY = 
            new SafeFactory(BASIC1D_FACTORY);

    public final static Factory SAFE_BASIC2D_FACTORY = 
            new SafeFactory(BASIC2D_FACTORY);

    public final static Factory SAFE_CRS_FACTORY = 
            new SafeFactory(CRS_FACTORY);

    public final static Factory SAFE_CCS_FACTORY = 
            new SafeFactory(CCS_FACTORY);

    public final static Factory UNSAFE_BASIC1D_FACTORY = BASIC1D_FACTORY;

    public final static Factory UNSAFE_BASIC2D_FACTORY = BASIC2D_FACTORY;

    public final static Factory UNSAFE_CRS_FACTORY = CRS_FACTORY;

    public final static Factory UNSAFE_CCS_FACTORY = CCS_FACTORY;

    public final static Factory DEFAULT_DENSE_FACTORY = BASIC2D_FACTORY;

    public final static Factory DEFAULT_SPARSE_FACTORY = CRS_FACTORY;

    public final static Factory DEFAULT_FACTORY = BASIC2D_FACTORY;

    public final static MatrixDecompositor CHOLESKY_DECOMPOSITOR = 
            new CholeskyDecompositor();

    public final static MatrixDecompositor EIGEN_DECOMPOSITOR = 
            new EigenDecompositor();

    public final static MatrixDecompositor LU_DECOMPOSITOR = 
            new LUDecompositor();

    public final static MatrixDecompositor QR_DECOMPOSITOR = 
            new QRDecompositor();

    public final static MatrixDecompositor SINGULAR_VALUE_DECOMPOSITOR = 
            new SingularValueDecompositor();

    public final static MatrixInvertor GAUSSIAN_INVERTOR =
            new GaussianInvertor();

    public final static MatrixInvertor DEFAULT_INVERTOR = GAUSSIAN_INVERTOR;

    public final static LinearSystemSolver GAUSSIAN_SOLVER = 
            new GaussianSolver();

    public final static LinearSystemSolver JACOBI_SOLVER = 
            new JacobiSolver();

    public final static LinearSystemSolver SEIDEL_SOLVER = 
            new SeidelSolver();

    public final static LinearSystemSolver SQUARE_ROOT_SOLVER = 
            new SquareRootSolver();

    public final static LinearSystemSolver SWEEP_SOLVER = 
            new SweepSolver();

    public final static LinearSystemSolver DEFAULT_SOLVER = GAUSSIAN_SOLVER;

    public static Matrix asSingletonMatrix(double value) {
        return DEFAULT_FACTORY.createMatrix(new double[][]{{ value }});
    }

    public static LinearSystem asLinearSystem(Matrix a, Vector b) {
        return DEFAULT_FACTORY.createLinearSystem(a, b);
    }

    public static Matrix asSafeMatrix(Matrix matrix) {
        return matrix.safe();
    }

    public static Matrix asUnsafeMatrix(Matrix matrix) {
        return matrix.unsafe();
    }

    public static MatrixSource asSafeSource(Matrix matrix) {
        return new SafeMatrixSource(matrix);
    }

    public static MatrixSource asUnsafeSource(Matrix matrix) {
        return new UnsafeMatrixSource(matrix);
    }

    public static MatrixSource asArray1DSource(int rows, int columns, 
            double[] array) {

        return new Array1DMatrixSource(rows, columns, array);
    }

    public static MatrixSource asArray2DSource(double[][] array) {
        return new Array2DMatrixSource(array);
    }

    public static MatrixSource asIdentitySource(int size) {
        return new IdentityMattixSource(size);
    }

    public static MatrixSource asRandomSource(int rows, int columns) {
        return new RandomMatrixSource(rows, columns);
    }

    public static MatrixSource asRandomSymmetricSource(int size) {
        return new RandomSymmetricMatrixSource(size);
    }

    public static MatrixSource asMatrixMarketSource(InputStream in) {
        return new StreamMatrixSource(new MatrixMarketStream(in));
    }

    public static MatrixSource asSymbolSeparatedSource(InputStream in) {
        return new StreamMatrixSource(new SymbolSeparatedStream(in));
    }

    public static MatrixSource asSymbolSeparatedSource(InputStream in, 
            String separator) {

        return new StreamMatrixSource(new SymbolSeparatedStream(in, separator));
    }

    public static MatrixAccumulator asSumAccumulator(double neutral) {
        return new SumMatrixAccumulator(neutral);
    }

    public static MatrixAccumulator asProductAccumulator(double neutral) {
        return new ProductMatrixAccumulator(neutral);
    }

    public static MatrixAccumulator asSumFunctionAccumulator(double neutral, 
            MatrixFunction function) {

        return new FunctionMatrixAccumulator(new SumMatrixAccumulator(neutral), 
                                             function);
    }

    public static MatrixAccumulator asProductFunctionAccumulator(double neutral, 
            MatrixFunction function) {

        return new FunctionMatrixAccumulator(new ProductMatrixAccumulator(
                                             neutral), function);
    }
}
