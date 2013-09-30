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
 * 
 */

package org.la4j.matrix;

import org.la4j.LinearAlgebra;
import org.la4j.decomposition.CholeskyDecompositor;
import org.la4j.decomposition.EigenDecompositor;
import org.la4j.decomposition.LUDecompositor;
import org.la4j.decomposition.MatrixDecompositor;
import org.la4j.decomposition.QRDecompositor;
import org.la4j.decomposition.RawLUDecompositor;
import org.la4j.decomposition.RawQRDecompositor;
import org.la4j.decomposition.SingularValueDecompositor;
import org.la4j.factory.Basic1DFactory;
import org.la4j.factory.Basic2DFactory;
import org.la4j.factory.CCSFactory;
import org.la4j.factory.CRSFactory;
import org.la4j.factory.Factory;
import org.la4j.inversion.GaussJordanInverter;
import org.la4j.inversion.MatrixInverter;
import org.la4j.io.MatrixMarketStream;
import org.la4j.io.SymbolSeparatedStream;
import org.la4j.linear.ForwardBackSubstitutionSolver;
import org.la4j.linear.GaussianSolver;
import org.la4j.linear.JacobiSolver;
import org.la4j.linear.LeastSquaresSolver;
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
import org.la4j.matrix.source.IdentityMatrixSource;
import org.la4j.matrix.source.MatrixSource;
import org.la4j.matrix.source.RandomMatrixSource;
import org.la4j.matrix.source.RandomSymmetricMatrixSource;
import org.la4j.matrix.source.SafeMatrixSource;
import org.la4j.matrix.source.StreamMatrixSource;
import org.la4j.matrix.source.UnsafeMatrixSource;
import org.la4j.vector.Vector;

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

        public SumMatrixAccumulator(double neutral) {
            this.result = new BigDecimal(neutral);
        }

        @Override
        public void update(int i, int j, double value) {
            result = result.add(new BigDecimal(value));
        }

        @Override
        public double accumulate() {
            return result.setScale(Matrices.ROUND_FACTOR, RoundingMode.CEILING).doubleValue();
        }
    }

    private static class ProductMatrixAccumulator
            implements MatrixAccumulator {

        private BigDecimal result;

        public ProductMatrixAccumulator(double neutral) {
            this.result = new BigDecimal(neutral);
        }

        @Override
        public void update(int i, int j, double value) {
            result = result.multiply(new BigDecimal(value));
        }

        @Override
        public double accumulate() {
            return result.setScale(Matrices.ROUND_FACTOR, RoundingMode.CEILING).doubleValue();

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
     * Creates a modulus function with specified <code>value</code>. The function 
     * evaluates like following:
     * <p>
     * <center><code>something %= value</code></center>
     * </p>
     *
     * @param value
     * @return
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
     * The {@link Basic1DFactory} singleton instance.
     *
     * Deprecated: use {@link LinearAlgebra#BASIC1D_FACTORY} instead.
     */
    @Deprecated
    public static final Factory BASIC1D_FACTORY = LinearAlgebra.BASIC1D_FACTORY;

    /**
     * The {@link Basic2DFactory} singleton instance.
     *
     * Deprecated: use {@link LinearAlgebra#BASIC2D_FACTORY} instead.
     */
    @Deprecated
    public static final Factory BASIC2D_FACTORY = LinearAlgebra.BASIC2D_FACTORY;

    /**
     * The {@link CRSFactory} singleton instance.
     *
     * Deprecated: use {@link LinearAlgebra#CRS_FACTORY} instead.
     */
    @Deprecated
    public static final Factory CRS_FACTORY = LinearAlgebra.CRS_FACTORY;

    /**
     * The {@link CCSFactory} singleton instance.
     *
     * Deprecated: use {@link LinearAlgebra#CCS_FACTORY} instead.
     */
    @Deprecated
    public static final Factory CCS_FACTORY = LinearAlgebra.CCS_FACTORY;

    /**
     * Safe version of {@link Matrices#BASIC1D_FACTORY}.
     * 
     * <p>
     * The safe factory creates matrices that is wrapped with safe accessors 
     * and modifiers. 
     * </p>
     *
     * Deprecated: use {@link LinearAlgebra#SAFE_BASIC1D_FACTORY} instead.
     */
    @Deprecated
    public static final Factory SAFE_BASIC1D_FACTORY = LinearAlgebra.SAFE_BASIC1D_FACTORY;

    /**
     * Safe version of {@link Matrices#BASIC2D_FACTORY}.
     * 
     * <p>
     * The safe factory creates matrices that is wrapped with safe accessors 
     * and modifiers. 
     * </p>
     *
     * Deprecated: use {@link LinearAlgebra#SAFE_BASIC2D_FACTORY} instead.
     */
    @Deprecated
    public static final Factory SAFE_BASIC2D_FACTORY = LinearAlgebra.SAFE_BASIC2D_FACTORY;

    /**
     * Safe version of {@link Matrices#CRS_FACTORY}.
     * 
     * <p>
     * The safe factory creates matrices that is wrapped with safe accessors 
     * and modifiers. 
     * </p>
     *
     * Deprecated: use {@link LinearAlgebra#SAFE_CRS_FACTORY} instead.
     */
    @Deprecated
    public static final Factory SAFE_CRS_FACTORY = LinearAlgebra.SAFE_CRS_FACTORY;

    /**
     * Safe version of {@link Matrices#CCS_FACTORY}.
     * 
     * <p>
     * The safe factory creates matrices that is wrapped with safe accessors 
     * and modifiers. 
     * </p>
     *
     * Deprecated: use {@link LinearAlgebra#SAFE_CCS_FACTORY} instead.
     */
    @Deprecated
    public static final Factory SAFE_CCS_FACTORY = LinearAlgebra.SAFE_CCS_FACTORY;

    /**
     * Reference to the {@link Matrices#BASIC1D_FACTORY}.
     *
     * Deprecated: use {@link LinearAlgebra#UNSAFE_BASIC1D_FACTORY} instead.
     */
    @Deprecated
    public static final Factory UNSAFE_BASIC1D_FACTORY = LinearAlgebra.UNSAFE_BASIC1D_FACTORY;

    /**
     * Reference to the {@link Matrices#BASIC2D_FACTORY}.
     *
     * Deprecated: use {@link LinearAlgebra#UNSAFE_BASIC2D_FACTORY} instead.
     */
    @Deprecated
    public static final Factory UNSAFE_BASIC2D_FACTORY = LinearAlgebra.UNSAFE_BASIC2D_FACTORY;

    /**
     * Reference to the {@link Matrices#CRS_FACTORY}.
     *
     * Deprecated: use {@link LinearAlgebra#UNSAFE_CRS_FACTORY} instead.
     */
    @Deprecated
    public static final Factory UNSAFE_CRS_FACTORY = LinearAlgebra.UNSAFE_CRS_FACTORY;

    /**
     * Reference to the {@link Matrices#CCS_FACTORY}.
     *
     * Deprecated: use {@link LinearAlgebra#UNSAFE_CCS_FACTORY} instead.
     */
    @Deprecated
    public static final Factory UNSAFE_CCS_FACTORY = LinearAlgebra.UNSAFE_CCS_FACTORY;

    /**
     * The default dense factory singleton instance. References the
     * {@link Matrices#BASIC2D_FACTORY}.
     *
     * Deprecated: use {@link LinearAlgebra#DENSE_FACTORY} instead.
     */
    @Deprecated
    public static final Factory DEFAULT_DENSE_FACTORY = LinearAlgebra.DENSE_FACTORY;

    /**
     * The default sparse factory singleton instance. References the
     * {@link Matrices#CRS_FACTORY}.
     *
     * Deprecated: use {@link LinearAlgebra#SPARSE_FACTORY} instead.
     */
    @Deprecated
    public static final Factory DEFAULT_SPARSE_FACTORY = LinearAlgebra.SPARSE_FACTORY;

    /**
     * The default matrix factory singleton instance. References the
     * {@link Matrices#BASIC2D_FACTORY}.
     *
     * Deprecated: use {@link LinearAlgebra#DEFAULT_FACTORY} instead.
     */
    @Deprecated
    public static final Factory DEFAULT_FACTORY = LinearAlgebra.DEFAULT_FACTORY;

    /**
     * The array with all factories available.
     *
     * Deprecated: use {@link LinearAlgebra#FACTORIES} instead.
     */
    @Deprecated
    public static final Factory FACTORIES[] = LinearAlgebra.FACTORIES;

    /**
     * The array with unsafe factories available.
     *
     * Deprecated: use {@link LinearAlgebra#UNSAFE_FACTORIES} instead.
     */
    @Deprecated
    public static final Factory UNSAFE_FACTORIES[] = LinearAlgebra.UNSAFE_FACTORIES;

    /**
     * The array with safe factories available.
     *
     * Deprecated: use {@link LinearAlgebra#SAFE_FACTORIES} instead.
     */
    @Deprecated
    public static final Factory SAFE_FACTORIES[] = LinearAlgebra.SAFE_FACTORIES;

    /**
     * The {@link CholeskyDecompositor} singleton instance.
     * <p>
     * This field is deprecated. Use {@link Matrix#withDecompositor(org.la4j.LinearAlgebra.DecompositorFactory)} instead.
     * </p>
     * Like this:
     * <br /><br />
     * <code>
     *     Matrix a = new Basic2DMatrix(...);
     *     <br />
     *     MatrixDecompositor decompositor = a.withDecompositor(LinearAlgebra.CHOLESKY);
     *     <br />
     *     Matrix[] l = decompositor.decompose(LinearAlgebra.DENSE_FACTORY);
     *     <br />
     * </code>
     */
    @Deprecated
    public static final MatrixDecompositor CHOLESKY_DECOMPOSITOR =
            new CholeskyDecompositor(DEFAULT_FACTORY.createMatrix());

    /**
     * The {@link EigenDecompositor} singleton instance.
     * <p>
     * This field is deprecated. Use {@link Matrix#withDecompositor(org.la4j.LinearAlgebra.DecompositorFactory)} instead.
     * </p>
     * Like this:
     * <br /><br />
     * <code>
     *     Matrix a = new Basic2DMatrix(...);
     *     <br />
     *     MatrixDecompositor decompositor = a.withDecompositor(LinearAlgebra.EIGEN);
     *     <br />
     *     Matrix[] vd = decompositor.decompose(LinearAlgebra.DENSE_FACTORY);
     *     <br />
     * </code>
     */
    @Deprecated
    public static final MatrixDecompositor EIGEN_DECOMPOSITOR = 
            new EigenDecompositor(DEFAULT_FACTORY.createMatrix());

    /**
     * The {@link LUDecompositor} singleton instance.
     * <p>
     * This field is deprecated. Use {@link Matrix#withDecompositor(org.la4j.LinearAlgebra.DecompositorFactory)} instead.
     * </p>
     * Like this:
     * <br /><br />
     * <code>
     *     Matrix a = new Basic2DMatrix(...);
     *     <br />
     *     MatrixDecompositor decompositor = a.withDecompositor(LinearAlgebra.LU);
     *     <br />
     *     Matrix[] lup = decompositor.decompose(LinearAlgebra.DENSE_FACTORY);
     *     <br />
     * </code>
     */
    @Deprecated
    public static final MatrixDecompositor LU_DECOMPOSITOR = 
            new LUDecompositor(DEFAULT_FACTORY.createMatrix());

    /**
     * The {@link RawLUDecompositor} singleton instance.
     * <p>
     * This field is deprecated. Use {@link Matrix#withDecompositor(org.la4j.LinearAlgebra.DecompositorFactory)} instead.
     * </p>
     * Like this:
     * <br /><br />
     * <code>
     *     Matrix a = new Basic2DMatrix(...);
     *     <br />
     *     MatrixDecompositor decompositor = a.withDecompositor(LinearAlgebra.RAW_LU);
     *     <br />
     *     Matrix[] lup = decompositor.decompose(LinearAlgebra.DENSE_FACTORY);
     *     <br />
     * </code>
     */
    @Deprecated
    public static final MatrixDecompositor RAW_LU_DECOMPOSITOR =
            new RawLUDecompositor(DEFAULT_FACTORY.createMatrix());

    /**
     * The {@link QRDecompositor} singleton instance.
     * <p>
     * This field is deprecated. Use {@link Matrix#withDecompositor(org.la4j.LinearAlgebra.DecompositorFactory)} instead.
     * </p>
     * Like this:
     * <br /><br />
     * <code>
     *     Matrix a = new Basic2DMatrix(...);
     *     <br />
     *     MatrixDecompositor decompositor = a.withDecompositor(LinearAlgebra.QR);
     *     <br />
     *     Matrix[] qr = decompositor.decompose(LinearAlgebra.DENSE_FACTORY);
     *     <br />
     * </code>
     */
    @Deprecated
    public static final MatrixDecompositor QR_DECOMPOSITOR = 
            new QRDecompositor(DEFAULT_FACTORY.createMatrix());

    /**
     * The {@link RawQRDecompositor} singleton instance.
     * <p>
     * This field is deprecated. Use {@link Matrix#withDecompositor(org.la4j.LinearAlgebra.DecompositorFactory)} instead.
     * </p>
     * Like this:
     * <br /><br />
     * <code>
     *     Matrix a = new Basic2DMatrix(...);
     *     <br />
     *     MatrixDecompositor decompositor = a.withDecompositor(LinearAlgebra.RAW_QR);
     *     <br />
     *     Matrix[] qrr = decompositor.decompose(LinearAlgebra.DENSE_FACTORY);
     *     <br />
     * </code>
     */
    @Deprecated
    public static final MatrixDecompositor RAW_QR_DECOMPOSITOR =
            new RawQRDecompositor(DEFAULT_FACTORY.createMatrix());

    /**
     * The {@link SingularValueDecompositor} singleton instance.
     * <p>
     * This field is deprecated. Use {@link Matrix#withDecompositor(org.la4j.LinearAlgebra.DecompositorFactory)} instead.
     * </p>
     * Like this:
     * <br /><br />
     * <code>
     *     Matrix a = new Basic2DMatrix(...);
     *     <br />
     *     MatrixDecompositor decompositor = a.withDecompositor(LinearAlgebra.SVD);
     *     <br />
     *     Matrix[] usv = decompositor.decompose(LinearAlgebra.DENSE_FACTORY);
     *     <br />
     * </code>
     */
    @Deprecated
    public static final MatrixDecompositor SINGULAR_VALUE_DECOMPOSITOR = 
            new SingularValueDecompositor(DEFAULT_FACTORY.createMatrix());

    /**
     * The {@link GaussJordanInverter} singleton instance.
     *
     * <p>
     * This field is deprecated. Use {@link Matrix#withInverter(org.la4j.LinearAlgebra.InverterFactory)} instead.
     * </p>
     * Like this:
     * <br /><br />
     * <code>
     *     Matrix a = new Basic2DMatrix(...);
     *     <br />
     *     MatrixInverter inverter = a.withInverter(LinearAlgebra.INVERTER);
     *     <br />
     *     Matrix b = inverter.inverse(LinearAlgebra.DENSE_FACTORY);
     *     <br />
     * </code>
     */
    @Deprecated
    public static final MatrixInverter GAUSSIAN_INVERTER =
            new GaussJordanInverter(DEFAULT_FACTORY.createMatrix());

    /**
     * The default matrix inverter singleton instance. References the 
     * {@link GaussJordanInverter}.
     *
     * <p>
     * This field is deprecated. Use {@link Matrix#withInverter(org.la4j.LinearAlgebra.InverterFactory)} instead.
     * </p>
     * Like this:
     * <br /><br />
     * <code>
     *     Matrix a = new Basic2DMatrix(...);
     *     <br />
     *     MatrixInverter inverter = a.withInverter(LinearAlgebra.INVERTER);
     *     <br />
     *     Matrix b = inverter.inverse(LinearAlgebra.DENSE_FACTORY);
     *     <br />
     * </code>
     *
     */
    @Deprecated
    public static final MatrixInverter DEFAULT_INVERTER = GAUSSIAN_INVERTER;

    /**
     * The {@link GaussianSolver} singleton instance.
     *
     * <p>
     * This field is deprecated. Use {@link Matrix#withSolver(org.la4j.LinearAlgebra.SolverFactory)} instead. Like this:
     * <br /><br />
     * <code>
     *     Matrix a = new Basic2DMatrix(...);
     *     <br />
     *     Vector b = new BasicVector(...);
     *     <br />
     *     LinearSystemSolver solver = a.withSolver(LinearAlgebra.GAUSSIAN);
     *     <br />
     *     Vector x = solver.solve(LinearAlgebra.DENSE_FACTORY);
     *     <br />
     * </code>
     * </p>
     */
    @Deprecated
    public static final LinearSystemSolver GAUSSIAN_SOLVER =
            new GaussianSolver(DEFAULT_FACTORY.createMatrix());

    /**
     * The {@link JacobiSolver} singleton instance.
     * <p>
     * This field is deprecated. Use {@link Matrix#withSolver(org.la4j.LinearAlgebra.SolverFactory)} instead. Like this:
     * <br /><br />
     * <code>
     *     Matrix a = new Basic2DMatrix(...);
     *     <br />
     *     Vector b = new BasicVector(...);
     *     <br />
     *     LinearSystemSolver solver = a.withSolver(LinearAlgebra.JACOBI);
     *     <br />
     *     Vector x = solver.solve(LinearAlgebra.DENSE_FACTORY);
     *     <br />
     * </code>
     * </p>
     */
    @Deprecated
    public static final LinearSystemSolver JACOBI_SOLVER = 
            new JacobiSolver(DEFAULT_FACTORY.createMatrix());

    /**
     * The {@link SeidelSolver} singleton instance.
     * <p>
     * This field is deprecated. Use {@link Matrix#withSolver(org.la4j.LinearAlgebra.SolverFactory)} instead. Like this:
     * <br /><br />
     * <code>
     *     Matrix a = new Basic2DMatrix(...);
     *     <br />
     *     Vector b = new BasicVector(...);
     *     <br />
     *     LinearSystemSolver solver = a.withSolver(LinearAlgebra.SEIDEL);
     *     <br />
     *     Vector x = solver.solve(LinearAlgebra.DENSE_FACTORY);
     *     <br />
     * </code>
     * </p>
     */
    @Deprecated
    public static final LinearSystemSolver SEIDEL_SOLVER = 
            new SeidelSolver(DEFAULT_FACTORY.createMatrix());

    /**
     * The {@link SquareRootSolver} singleton instance.
     * <p>
     * This field is deprecated. Use {@link Matrix#withSolver(org.la4j.LinearAlgebra.SolverFactory)} instead. Like this:
     * <br /><br />
     * <code>
     *     Matrix a = new Basic2DMatrix(...);
     *     <br />
     *     Vector b = new BasicVector(...);
     *     <br />
     *     LinearSystemSolver solver = a.withSolver(LinearAlgebra.SQUARE_ROOT);
     *     <br />
     *     Vector x = solver.solve(LinearAlgebra.DENSE_FACTORY);
     *     <br />
     * </code>
     * </p>
     */
    @Deprecated
    public static final LinearSystemSolver SQUARE_ROOT_SOLVER =
            new SquareRootSolver(DEFAULT_FACTORY.createMatrix());

    /**
     * The {@link SweepSolver} singleton instance.
     * <p>
     * This field is deprecated. Use {@link Matrix#withSolver(org.la4j.LinearAlgebra.SolverFactory)} instead. Like this:
     * <br /><br />
     * <code>
     *     Matrix a = new Basic2DMatrix(...);
     *     <br />
     *     Vector b = new BasicVector(...);
     *     <br />
     *     LinearSystemSolver solver = a.withSolver(LinearAlgebra.SWEEP);
     *     <br />
     *     Vector x = solver.solve(LinearAlgebra.DENSE_FACTORY);
     *     <br />
     * </code>
     * </p>
     */
    @Deprecated
    public static final LinearSystemSolver SWEEP_SOLVER = 
            new SweepSolver(DEFAULT_FACTORY.createMatrix());

    /**
     * The {@link LeastSquaresSolver} singleton instance.
     * <p>
     * This field is deprecated. Use {@link Matrix#withSolver(org.la4j.LinearAlgebra.SolverFactory)} instead. Like this:
     * <br /><br />
     * <code>
     *     Matrix a = new Basic2DMatrix(...);
     *     <br />
     *     Vector b = new BasicVector(...);
     *     <br />
     *     LinearSystemSolver solver = a.withSolver(LinearAlgebra.LEAST_SQUARES);
     *     <br />
     *     Vector x = solver.solve(LinearAlgebra.DENSE_FACTORY);
     *     <br />
     * </code>
     * </p>
     */
    @Deprecated
    public static final LinearSystemSolver LEAST_SQUARES_SOLVER =
            new LeastSquaresSolver(DEFAULT_FACTORY.createMatrix());

    /**
     * The {@link ForwardBackSubstitutionSolver} (simple square solver) singleton instance.
     * <p>
     * This field is deprecated. Use {@link Matrix#withSolver(org.la4j.LinearAlgebra.SolverFactory)} instead. Like this:
     * <br /><br />
     * <code>
     *     Matrix a = new Basic2DMatrix(...);
     *     <br />
     *     Vector b = new BasicVector(...);
     *     <br />
     *     LinearSystemSolver solver = a.withSolver(LinearAlgebra.FORWARD_BACK_SUBSTITUTION);
     *     <br />
     *     Vector x = solver.solve(LinearAlgebra.DENSE_FACTORY);
     *     <br />
     * </code>
     * </p>
     */
    @Deprecated
    public static final LinearSystemSolver FORWARD_BACK_SUBSTITUTION_SOLVER =
            new ForwardBackSubstitutionSolver(DEFAULT_FACTORY.createMatrix());

    /**
     * The default linear system solver singleton instance. References the 
     * {@link Matrices#GAUSSIAN_SOLVER}.
     * <p>
     * This field is deprecated. Use {@link org.la4j.matrix.Matrix#withSolver(org.la4j.LinearAlgebra.SolverFactory)} instead. Like this:
     * <br /><br />
     * <code>
     *     Matrix a = new Basic2DMatrix(...);
     *     <br />
     *     Vector b = new BasicVector(...);
     *     <br />
     *     LinearSystemSolver solver = a.withSolver(LinearAlgebra.SOLVER);
     *     <br />
     *     Vector x = solver.solve(LinearAlgebra.DENSE_FACTORY);
     *     <br />
     * </code>
     * </p>
     */
    @Deprecated
    public static final LinearSystemSolver DEFAULT_SOLVER = GAUSSIAN_SOLVER;

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
     * <p>
     * This method is deprecated. Use {@link Matrix#withSolver(org.la4j.LinearAlgebra.SolverFactory)} instead.
     * </p>
     * 
     * @param a
     * @param b
     * @return
     */
    @Deprecated
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
        return new IdentityMatrixSource(size);
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
