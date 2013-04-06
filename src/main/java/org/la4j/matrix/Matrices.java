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

    /**
     * The machine epsilon, that is calculated at runtime.
     */
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

    private static class InvMatrixFunction 
            implements MatrixFunction {
        @Override
        public double evaluate(int i, int j, double value) {
            return -value;
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
     * Creates a plus function with specified <code>value</code>. The function 
     * evaluates like following: 
     * <p>
     * <center><code>something += value</code></center>
     * </p>

     * @param value
     */
    public static MatrixFunction asPlusFunction(double value) {
        return new PlusMatrixFunction(value);
    }

    /**
     * Creates a minus function with specified <code>value</code>. The function 
     * evaluates like following: 
     * <p>
     * <center><code>something -= value</code></center>
     * </p> 
     * 
     * @param value
     * @return
     */
    public static MatrixFunction asMinusFunction(double value) {
        return new MinusMatrixFunction(value);
    }

    /**
     * Creates a multiply function with specified <code>value</code>. The 
     * function evaluates like following: 
     * <p>
     * <center><code>something *= value</code></center>
     * </p>
     * 
     * @param value
     * @return
     */
    public static MatrixFunction asMulFunction(double value) {
        return new MulMatrixFunction(value);
    }

    /**
     * Creates a divide function with specified <code>value</code>. The function 
     * evaluates like following: 
     * <p>
     * <center><code>something /= value</code></center>
     * </p>
     * 
     * @param value
     * @return
     */
    public static MatrixFunction asDivFunction(double value) {
        return new DivMatrixFunction(value);
    }

    /**
     * Checks whether the matrix is a
     * <a href="http://mathworld.wolfram.com/DiagonalMatrix.html">diagonal 
     * matrix</a>.
     */
    public final static MatrixPredicate DIAGONAL_MATRIX = 
            new DiagonalMatrixPredicate();

    /**
     * Checks whether the matrix is an 
     * <a href="http://mathworld.wolfram.com/IdentityMatrix.html">identity
     * matrix</a>.
     */
    public final static MatrixPredicate IDENTITY_MATRIX = 
            new IdentityMatrixPredicate();

    /**
     * Checks whether the matrix is a
     * <a href="http://mathworld.wolfram.com/ZeroMatrix.html">zero
     * matrix</a>.
     */
    public final static MatrixPredicate ZERO_MATRIX = 
            new ZeroMatrixPredicate();

    /**
     * Checks whether the matrix is a 
     * <a href="http://mathworld.wolfram.com/TridiagonalMatrix.html">tridiagonal
     * matrix</a>.
     */
    public final static MatrixPredicate TRIDIAGONAL_MATRIX = 
            new TridiagonalMatrixPredicate();

    /**
     * Checks whether the matrix is a 
     * <a href="http://mathworld.wolfram.com/PositiveMatrix.html">positive 
     * matrix</a>.
     */
    public final static MatrixPredicate POSITIVE_MATRIX = 
            new PositiveMatrixPredicate();

    /**
     * Checks whether the matrix is a 
     * <a href="http://mathworld.wolfram.com/NegativeMatrix.html">negative 
     * matrix</a>.
     */
    public final static MatrixPredicate NEGATIVE_MATRIX = 
            new NegativeMatrixPredicate();

    /**
     * Checks whether the matrix is a lower bidiagonal matrix</a>.
     */
    public final static MatrixPredicate LOWER_BIDIAGONAL_MATRIX = 
            new LowerBidiagonalMatrixPredicate();

    /**
     * Checks whether the matrix is an upper bidiagonal matrix. 
     */
    public final static MatrixPredicate UPPER_BIDIAGONAL_MATRIX = 
            new UpperBidiagonalMatrixPredicate();

    /**
     * Checks whether the matrix is a
     * <a href="http://mathworld.wolfram.com/LowerTriangularMatrix.html">lower 
     * triangular matrix</a>.
     */
    public final static MatrixPredicate LOWER_TRIANGULAR_MARTIX = 
            new LowerTriangularMatrixPredicate();

    /**
     * Checks whether the matrix is an
     * <a href="http://mathworld.wolfram.com/UpperTriangularMatrix.html">upper 
     * triangular matrix</a>.
     */
    public final static MatrixPredicate UPPER_TRIANGULAR_MATRIX = 
            new UpperTriangularMatrixPredicate();

    /**
     * Checks whether the matrix is a 
     * <a href="http://mathworld.wolfram.com/SymmetricMatrix.html">symmetric 
     * matrix</a>. 
     */
    public final static MatrixPredicate SYMMETRIC_MATRIX = 
            new SymmetricMatrixPredicate();

    /**
     * Increases each element of matrix by <code>1</code>.
     */
    public final static MatrixFunction INC_FUNCTION = 
            new IncMatrixFunction();

    /**
     * Decreases each element of matrix by <code>1</code>.
     */
    public final static MatrixFunction DEC_FUNCTION = 
            new DecMatrixFunction();

    /**
     * Inverts each element of matrix.  
     */
    public final static MatrixFunction INV_FUNCTION = 
            new InvMatrixFunction();
    /**
     * The {@link Basic1DFactory} singleton instance.
     */
    public final static Factory BASIC1D_FACTORY = new Basic1DFactory();

    /**
     * The {@link Basic2DFactory} singleton instance.
     */
    public final static Factory BASIC2D_FACTORY = new Basic2DFactory();

    /**
     * The {@link CRSFactory} singleton instance.
     */
    public final static Factory CRS_FACTORY = new CRSFactory();

    /**
     * The {@link CCSFactory} singleton instance.
     */
    public final static Factory CCS_FACTORY = new CCSFactory();

    /**
     * Safe version of {@link Matrices#BASIC1D_FACTORY}.
     * 
     * <p>
     * The safe factory creates matrices that is wrapped with safe accessors 
     * and modifiers. 
     * </p>
     */
    public final static Factory SAFE_BASIC1D_FACTORY = 
            new SafeFactory(BASIC1D_FACTORY);

    /**
     * Safe version of {@link Matrices#BASIC2D_FACTORY}.
     * 
     * <p>
     * The safe factory creates matrices that is wrapped with safe accessors 
     * and modifiers. 
     * </p>
     */
    public final static Factory SAFE_BASIC2D_FACTORY = 
            new SafeFactory(BASIC2D_FACTORY);

    /**
     * Safe version of {@link Matrices#CRS_FACTORY}.
     * 
     * <p>
     * The safe factory creates matrices that is wrapped with safe accessors 
     * and modifiers. 
     * </p>
     */
    public final static Factory SAFE_CRS_FACTORY = 
            new SafeFactory(CRS_FACTORY);

    /**
     * Safe version of {@link Matrices#CCS_FACTORY}.
     * 
     * <p>
     * The safe factory creates matrices that is wrapped with safe accessors 
     * and modifiers. 
     * </p>
     */
    public final static Factory SAFE_CCS_FACTORY = 
            new SafeFactory(CCS_FACTORY);

    /**
     * Reference to the {@link Matrices#BASIC1D_FACTORY}.
     */
    public final static Factory UNSAFE_BASIC1D_FACTORY = BASIC1D_FACTORY;

    /**
     * Reference to the {@link Matrices#BASIC2D_FACTORY}.
     */
    public final static Factory UNSAFE_BASIC2D_FACTORY = BASIC2D_FACTORY;

    /**
     * Reference to the {@link Matrices#CRS_FACTORY}.
     */
    public final static Factory UNSAFE_CRS_FACTORY = CRS_FACTORY;

    /**
     * Reference to the {@link Matrices#CCS_FACTORY}.
     */
    public final static Factory UNSAFE_CCS_FACTORY = CCS_FACTORY;

    /**
     * The default dense factory singleton instance. References the
     * {@link Matrices#BASIC2D_FACTORY}.
     */
    public final static Factory DEFAULT_DENSE_FACTORY = BASIC2D_FACTORY;

    /**
     * The default sparse factory singleton instance. References the
     * {@link Matrices#CRS_FACTORY}.
     */
    public final static Factory DEFAULT_SPARSE_FACTORY = CRS_FACTORY;

    /**
     * The default matrix factory singleton instance. References the
     * {@link Matrices#BASIC2D_FACTORY}.
     */
    public final static Factory DEFAULT_FACTORY = BASIC2D_FACTORY;

    /**
     * The {@link CholeskyDecompositor} singleton instance.
     */
    public final static MatrixDecompositor CHOLESKY_DECOMPOSITOR = 
            new CholeskyDecompositor();

    /**
     * The {@link EigenDecompositor} singleton instance.
     */
    public final static MatrixDecompositor EIGEN_DECOMPOSITOR = 
            new EigenDecompositor();

    /**
     * The {@link LUDecompositor} singleton instance.
     */
    public final static MatrixDecompositor LU_DECOMPOSITOR = 
            new LUDecompositor();

    /**
     * The {@link QRDecompositor} singleton instance.
     */
    public final static MatrixDecompositor QR_DECOMPOSITOR = 
            new QRDecompositor();

    /**
     * The {@link SingularValueDecompositor} singleton instance.
     */
    public final static MatrixDecompositor SINGULAR_VALUE_DECOMPOSITOR = 
            new SingularValueDecompositor();

    /**
     * The {@link GaussianInvertor} singleton instance.
     */
    public final static MatrixInvertor GAUSSIAN_INVERTOR =
            new GaussianInvertor();

    /**
     * The default matrix invertor singleton instance. References the 
     * {@link GaussianInvertor}. 
     */
    public final static MatrixInvertor DEFAULT_INVERTOR = GAUSSIAN_INVERTOR;

    /**
     * The {@link GaussianSolver} singleton instance.
     */
    public final static LinearSystemSolver GAUSSIAN_SOLVER = 
            new GaussianSolver();

    /**
     * The {@link JacobiSolver} singleton instance.
     */
    public final static LinearSystemSolver JACOBI_SOLVER = 
            new JacobiSolver();

    /**
     * The {@link SeidelSolver} singleton instance.
     */
    public final static LinearSystemSolver SEIDEL_SOLVER = 
            new SeidelSolver();

    /**
     * The {@link SquareRootSolver} singleton instance.
     */
    public final static LinearSystemSolver SQUARE_ROOT_SOLVER = 
            new SquareRootSolver();

    /**
     * The {@link SweepSolver} singleton instance.
     */
    public final static LinearSystemSolver SWEEP_SOLVER = 
            new SweepSolver();

    /**
     * The default linear system solver singleton instance. References the 
     * {@link Matrices#GAUSSIAN_SOLVER}.
     */
    public final static LinearSystemSolver DEFAULT_SOLVER = GAUSSIAN_SOLVER;

    /**
     * Creates a singleton <code>1x1</code> matrix from <code>value</code>.
     * 
     * @param value
     * @return
     */
    public static Matrix asSingletonMatrix(double value) {
        return DEFAULT_FACTORY.createMatrix(new double[][]{{ value }});
    }

    /**
     * Creates {@link LinearSystem} instance from matrix <code>a</code> and 
     * vector <code>b</code>. The {@link Matrices#DEFAULT_FACTORY} is using
     * for matrix construction.
     * 
     * @param a
     * @param b
     * @return
     */
    public static LinearSystem asLinearSystem(Matrix a, Vector b) {
        return DEFAULT_FACTORY.createLinearSystem(a, b);
    }

    /**
     * Wraps the <code>matrix</code> with interface that provides safe accessors
     * and modifiers.
     * 
     * @param matrix
     * @return
     */
    public static Matrix asSafeMatrix(Matrix matrix) {
        return matrix.safe();
    }

    /**
     * Unwraps the safe <code>matrix</code>.
     * 
     * @param matrix
     * @return
     */
    public static Matrix asUnsafeMatrix(Matrix matrix) {
        return matrix.unsafe();
    }

    /**
     * Creates a safe matrix source with specified <code>matrix</code>.
     * 
     * @param matrix
     * @return
     */
    public static MatrixSource asSafeSource(Matrix matrix) {
        return new SafeMatrixSource(matrix);
    }

    /**
     * Creates a unsafe matrix source with specified <code>matrix</code>.
     * 
     * @param matrix
     * @return
     */
    public static MatrixSource asUnsafeSource(Matrix matrix) {
        return new UnsafeMatrixSource(matrix);
    }

    /**
     * Creates a 1D array matrix source with specified dimensions and 
     * <code>array</code> reference. 
     * 
     * @param rows
     * @param columns
     * @param array
     * @return
     */
    public static MatrixSource asArray1DSource(int rows, int columns, 
            double[] array) {

        return new Array1DMatrixSource(rows, columns, array);
    }

    /**
     * Creates a 2D array matrix source with specified <code>array</code> 
     * reference.
     * 
     * @param array
     * @return
     */
    public static MatrixSource asArray2DSource(double[][] array) {
        return new Array2DMatrixSource(array);
    }

    /**
     * Creates an identity matrix source with specified <code>size</code>.
     * 
     * @param size
     * @return
     */
    public static MatrixSource asIdentitySource(int size) {
        return new IdentityMattixSource(size);
    }

    /**
     * Creates a random matrix source with specified dimensions.
     * 
     * @param rows
     * @param columns
     * @return
     */
    public static MatrixSource asRandomSource(int rows, int columns) {
        return new RandomMatrixSource(rows, columns);
    }

    /**
     * Creates a random symmetric matrix source with specified 
     * <code>size</code>.
     * 
     * @param size
     * @return
     */
    public static MatrixSource asRandomSymmetricSource(int size) {
        return new RandomSymmetricMatrixSource(size);
    }

    /**
     * Creates a MatrixMarket stream source with specified input stream.
     * 
     * @param in
     * @return
     */
    public static MatrixSource asMatrixMarketSource(InputStream in) {
        return new StreamMatrixSource(new MatrixMarketStream(in));
    }

    /**
     * Creates a symbol separated stream source (like CSV) with specified
     * input stream.
     * 
     * @param in
     * @return
     */
    public static MatrixSource asSymbolSeparatedSource(InputStream in) {
        return new StreamMatrixSource(new SymbolSeparatedStream(in));
    }

    /**
     * Creates a symbol separated stream source (like CSV) with specified
     * input stream and <code>separator</code>.
     * 
     * @param in
     * @param separator
     * @return
     */
    public static MatrixSource asSymbolSeparatedSource(InputStream in, 
            String separator) {

        return new StreamMatrixSource(new SymbolSeparatedStream(in, separator));
    }

    /**
     * Creates a sum matrix accumulator, that calculates the sum of all 
     * elements of matrix. 
     * 
     * @param neutral
     * @return
     */
    public static MatrixAccumulator asSumAccumulator(double neutral) {
        return new SumMatrixAccumulator(neutral);
    }

    /**
     * Creates a product matrix accumulator, that calculates the product of all
     * elements of matrix.
     * 
     * @param neutral
     * @return
     */
    public static MatrixAccumulator asProductAccumulator(double neutral) {
        return new ProductMatrixAccumulator(neutral);
    }

    /**
     * Creates a sum function accumulator, that calculates the sum of all 
     * elements of matrix after applying a <code>function</code> to 
     * each of them.
     * 
     * @param neutral
     * @param function
     * @return
     */
    public static MatrixAccumulator asSumFunctionAccumulator(double neutral, 
            MatrixFunction function) {

        return new FunctionMatrixAccumulator(new SumMatrixAccumulator(neutral), 
                                             function);
    }

    /**
     * Creates a produce function accumulator, that calculates the produce of
     * all elements of matrix after applying a <code>function</code> to
     * each of them.
     * 
     * @param neutral
     * @param function
     * @return
     */
    public static MatrixAccumulator asProductFunctionAccumulator(double neutral, 
            MatrixFunction function) {

        return new FunctionMatrixAccumulator(new ProductMatrixAccumulator(
                                             neutral), function);
    }
}
