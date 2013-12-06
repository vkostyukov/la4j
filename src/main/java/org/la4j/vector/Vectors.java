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
 * Contributor(s): Maxim Samoylov
 *                 Miron Aseev
 * 
 */

package org.la4j.vector;

import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;

import org.la4j.LinearAlgebra;
import org.la4j.io.MatrixMarketStream;
import org.la4j.io.SymbolSeparatedStream;
import org.la4j.vector.functor.VectorAccumulator;
import org.la4j.vector.functor.VectorFunction;
import org.la4j.vector.functor.VectorPredicate;
import org.la4j.vector.source.ArrayVectorSource;
import org.la4j.vector.source.RandomVectorSource;
import org.la4j.vector.source.SafeVectorSource;
import org.la4j.vector.source.StreamVectorSource;
import org.la4j.vector.source.UnsafeVectorSource;
import org.la4j.vector.source.VectorSource;

public final class Vectors {

    public static final double EPS = LinearAlgebra.EPS;
    public static final int ROUND_FACTOR = LinearAlgebra.ROUND_FACTOR;

    private static class ZeroVectorPredicate implements VectorPredicate {
        @Override
        public boolean test(int i, double value) {
            return Math.abs(value) < EPS;
        }
    }

    private static class PositiveVectorPredicate implements VectorPredicate {
        @Override
        public boolean test(int i, double value) {
            return value > 0;
        }
    }

    private static class NegativeVectorPredicate implements VectorPredicate {
        @Override
        public boolean test(int i, double value) {
            return value < 0;
        }
    }

    private static class IncVectorFunction implements VectorFunction {
        @Override
        public double evaluate(int i, double value) {
            return value + 1.0;
        }
    }

    private static class DecVectorFunction implements VectorFunction {
        @Override
        public double evaluate(int i, double value) {
            return value - 1.0;
        }
    }

    private static class InvVectorFunction implements VectorFunction {
        @Override
        public double evaluate(int i, double value) {
            return -value;
        }
    }

    private static class PlusFunction implements VectorFunction {

        private double arg;

        public PlusFunction(double arg) {
            this.arg = arg;
        }

        @Override
        public double evaluate(int i, double value) {
            return value + arg;
        }
    }

    private static class MinusFunction implements VectorFunction {

        private double arg;

        public MinusFunction(double arg) {
            this.arg = arg;
        }

        @Override
        public double evaluate(int i, double value) {
            return value - arg;
        }
    }

    private static class MulFunction implements VectorFunction {

        private double arg;

        public MulFunction(double arg) {
            this.arg = arg;
        }

        @Override
        public double evaluate(int i, double value) {
            return value * arg;
        }
    }

    private static class DivFunction implements VectorFunction {

        private double arg;

        public DivFunction(double arg) {
            this.arg = arg;
        }

        @Override
        public double evaluate(int i, double value) {
            return value / arg;
        }
    }

    private static class ModFunction implements VectorFunction {

        private double arg;

        public ModFunction(double arg) {
            this.arg = arg;
        }

        @Override
        public double evaluate(int i, double value) {
            return value % arg;
        }
    }

    private static class SumVectorAccumulator implements VectorAccumulator {

        private BigDecimal result;

        public SumVectorAccumulator(double neutral) {
            this.result = new BigDecimal(neutral);
        }

        @Override
        public void update(int i, double value) {
            result = result.add(new BigDecimal(value));
        }

        @Override
        public double accumulate() {
            return result.setScale(Vectors.ROUND_FACTOR, RoundingMode.CEILING).doubleValue();
        }
    }

    private static class FunctionVectorAccumulator implements VectorAccumulator {

        private VectorAccumulator accumulator;
        private VectorFunction function;
        
        public FunctionVectorAccumulator(VectorAccumulator accumulator,
                VectorFunction function) {

            this.accumulator = accumulator;
            this.function = function;
        }

        @Override
        public void update(int i, double value) {
            accumulator.update(i, function.evaluate(i, value));
        }

        @Override
        public double accumulate() {
            return accumulator.accumulate();
        }
    }

    private static class ProductVectorAccumulator implements VectorAccumulator {

        private BigDecimal result;

        public ProductVectorAccumulator(double neutral) {
            this.result = new BigDecimal(neutral);
        }

        @Override
        public void update(int i, double value) {
            result = result.multiply(new BigDecimal(value));
        }

        @Override
        public double accumulate() {
            return result.setScale(Vectors.ROUND_FACTOR, RoundingMode.CEILING).doubleValue();
        }
    }

    public static interface NormFunction {

        double compute(Vector vector);
    }

    private static class EuclideanNormFunction implements NormFunction {

        @Override
        public double compute(Vector vector) {
            return Math.sqrt(vector.innerProduct(vector));
        }
    }

    private static class ManhattanNormFunction implements NormFunction {

        @Override
        public double compute(Vector vector) {
            double result = 0.0;

            for (int i = 0; i < vector.length(); i++) {
                result += Math.abs(vector.get(i));
            }

            return result;
        }
    }

    public static class InfinityNormFunction implements NormFunction {

        @Override
        public double compute(Vector vector) {
            double max = Math.abs(vector.get(0));

            for (int i = 1; i < vector.length(); i++) {
                double item = Math.abs(vector.get(i));
                if (item > max) {
                    max = item;
                }
            }

            return max;
        }
    }

    /**
     * Creates a plus function with specified <code>value</code>. The function 
     * evaluates like following: 
     * <p>
     * <center><code>something += value</code></center>
     * </p>
     * 
     * @param value
     * @return
     */
    public static VectorFunction asPlusFunction(double value) {
        return new PlusFunction(value);
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
    public static VectorFunction asMinusFunction(double value) {
        return new MinusFunction(value);
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
    public static VectorFunction asMulFunction(double value) {
        return new MulFunction(value);
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
    public static VectorFunction asDivFunction(double value) {
        return new DivFunction(value);
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
    public static VectorFunction asModFunction(double value) {
        return new ModFunction(value);
    }

    /**
     * Checks whether the vector is a
     * <a href="http://mathworld.wolfram.com/ZeroMatrix.html">zero
     * vector</a>.
     */
    public static final VectorPredicate ZERO_VECTOR =
            new ZeroVectorPredicate();

    /**
     * Checks whether the vector is a 
     * <a href="http://mathworld.wolfram.com/PositiveMatrix.html">positive 
     * vector</a>.
     */
    public static final VectorPredicate POSITIVE_VECTOR =
            new PositiveVectorPredicate();

    /**
     * Checks whether the vector is a 
     * <a href="http://mathworld.wolfram.com/NegativeMatrix.html">negative 
     * vector</a>.
     */
    public static final VectorPredicate NEGATIVE_VECTOR = 
            new NegativeVectorPredicate();

     /**
     * Increases each element of vector by <code>1</code>.
     */
    public static final VectorFunction INC_FUNCTION = new IncVectorFunction();

    /**
     * Decreases each element of vectors by <code>1</code>.
     */
    public static final VectorFunction DEC_FUNCTION = new DecVectorFunction();

    /**
     * Inverts each element of vector.
     */
    public static final VectorFunction INV_FUNCTION = new InvVectorFunction();

    /**
     * Calculates the Euclidean norm of a vector.
     */
    public static final NormFunction EUCLIDEAN_NORM = new EuclideanNormFunction();

    /**
     * Calculates the Manhattan norm of a vector.
     */
    public static final NormFunction MANHATTAN_NORM = new ManhattanNormFunction();

    /**
     * Calculates the Maximum norm of a vector.
     */
    public static final NormFunction INFINITY_NORM = new InfinityNormFunction();

    /**
     * Creates a singleton 1-length vector from <code>value</code>.
     * 
     * @param value
     * @return
     */
    public static Vector asSingletonVector(double value) {
        return LinearAlgebra.DEFAULT_FACTORY.createVector(new double[] { value });
    }

    /**
     * Wraps the <code>vector</code> with interface that provides safe accessors
     * and modifiers.
     * 
     * @param vector
     * @return
     */
    public static Vector asSafeVector(Vector vector) {
        return vector.safe();
    }

    /**
     * Unwraps the safe <code>vector</code>.
     * 
     * @param vector
     * @return
     */
    public static Vector asUnsafeVector(Vector vector) {
        return vector.unsafe();
    }

    /**
     * Creates a safe vector source with specified <code>vector</code>.
     * 
     * @param vector
     * @return
     */
    public static VectorSource asSafeSource(Vector vector) {
        return new SafeVectorSource(vector);
    }

    /**
     * Creates a unsafe vector source with specified <code>vector</code>.
     * 
     * @param vector
     * @return
     */
    public static VectorSource asUnsafeSource(Vector vector) {
        return new UnsafeVectorSource(vector);
    }

    /**
     * Creates an array vector source with specified <code>array</code> 
     * reference.
     * 
     * @param array
     * @return
     */
    public static VectorSource asArraySource(double[] array) {
        return new ArrayVectorSource(array);
    }

    /**
     * Creates a random vector source with specified <code>length</code>.
     * 
     * @param length
     * @return
     */
    public static VectorSource asRandomSource(int length) {
        return new RandomVectorSource(length);
    }

    /**
     * Creates a MatrixMarket stream source with specified input stream.
     * 
     * @param in
     * @return
     */
    public static VectorSource asMatrixMarketSource(InputStream in) {
        return new StreamVectorSource(new MatrixMarketStream(in));
    }

    /**
     * Creates a symbol separated stream source (like CSV) with specified
     * input stream.
     * 
     * @param in
     * @return
     */
    public static VectorSource asSymbolSeparatedSource(InputStream in) {
        return new StreamVectorSource(new SymbolSeparatedStream(in));
    }

    /**
     * Creates a symbol separated stream source (like CSV) with specified
     * input stream and <code>separator</code>.
     * 
     * @param in
     * @param separator
     * @return
     */
    public static VectorSource asSymbolSeparatedSource(InputStream in, 
            String separator) {

        return new StreamVectorSource(new SymbolSeparatedStream(in, separator));
    }

    /**
     * Creates a sum vector accumulator, that calculates the sum of all 
     * elements of vector.
     * 
     * @param neutral
     * @return
     */
    public static VectorAccumulator asSumAccumulator(double neutral) {
        return new SumVectorAccumulator(neutral);
    }

    /**
     * Creates a product vector accumulator, that calculates the product of all
     * elements of vector.
     * 
     * @param neutral
     * @return
     */
    public static VectorAccumulator asProductAccumulator(double neutral) {
        return new ProductVectorAccumulator(neutral);
    }

    /**
     * Creates a sum function accumulator, that calculates the sum of all 
     * elements of vector after applying a <code>function</code> to 
     * each of them.
     * 
     * @param neutral
     * @param function
     * @return
     */
    public static VectorAccumulator asSumFunctionAccumulator(double neutral, 
            VectorFunction function) {

        return new FunctionVectorAccumulator(new SumVectorAccumulator(neutral), 
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
    public static VectorAccumulator asProductFunctionAccumulator(double neutral, 
            VectorFunction function) {

        return new FunctionVectorAccumulator(new ProductVectorAccumulator(neutral),
                                             function);
    }
}
