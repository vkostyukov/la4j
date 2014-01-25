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
import org.la4j.vector.source.LoopbackVectorSource;
import org.la4j.vector.source.RandomVectorSource;
import org.la4j.vector.source.StreamVectorSource;
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

    private static class ConstVectorFunction implements VectorFunction {

        private double arg;

        public ConstVectorFunction(double arg) {
            this.arg = arg;
        }

        @Override
        public double evaluate(int i, double value) {
            return arg;
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

        private double neutral;
        private BigDecimal result;

        public SumVectorAccumulator(double neutral) {
            this.neutral = neutral;
            this.result = new BigDecimal(neutral);
        }

        @Override
        public void update(int i, double value) {
            result = result.add(new BigDecimal(value));
        }

        @Override
        public double accumulate() {
            double value = result.setScale(Vectors.ROUND_FACTOR, RoundingMode.CEILING).doubleValue();
            result = new BigDecimal(neutral);
            return value;
        }
    }

    private static class ProductVectorAccumulator implements VectorAccumulator {

        private double neutral;
        private BigDecimal result;

        public ProductVectorAccumulator(double neutral) {
            this.neutral = neutral;
            this.result = new BigDecimal(neutral);
        }

        @Override
        public void update(int i, double value) {
            result = result.multiply(new BigDecimal(value));
        }

        @Override
        public double accumulate() {
            double value = result.setScale(Vectors.ROUND_FACTOR, RoundingMode.CEILING).doubleValue();
            result = new BigDecimal(neutral);
            return value;
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

    private static class MinVectorAccumulator implements VectorAccumulator {

        private double result = Double.POSITIVE_INFINITY;

        @Override
        public void update(int i, double value) {
            result = Math.min(result, value);
        }

        @Override
        public double accumulate() {
            double value = result;
            result = Double.POSITIVE_INFINITY;
            return value;
        }
    }

    private static class MaxVectorAccumulator implements VectorAccumulator {

        private double result = Double.NEGATIVE_INFINITY;

        @Override
        public void update(int i, double value) {
            result = Math.max(result, value);
        }

        @Override
        public double accumulate() {
            double value = result;
            result = Double.NEGATIVE_INFINITY;
            return value;
        }
    }

    private static class EuclideanNormAccumulator implements VectorAccumulator {

        private BigDecimal result = new BigDecimal(0.0);

        @Override
        public void update(int i, double value) {
            result = result.add(new BigDecimal(value * value));
        }

        @Override
        public double accumulate() {
            double value = result.setScale(Vectors.ROUND_FACTOR, RoundingMode.CEILING).doubleValue();
            result = new BigDecimal(0.0);
            return Math.sqrt(value);
        }
    }

    private static class ManhattanNormAccumulator implements VectorAccumulator {

        private double result = 0.0;

        @Override
        public void update(int i, double value) {
            result += Math.abs(value);
        }

        @Override
        public double accumulate() {
            double value = result;
            result = 0.0;
            return value;
        }
    }

    private static class InfinityNormAccumulator implements VectorAccumulator {

        private double result = Double.NEGATIVE_INFINITY;

        @Override
        public void update(int i, double value) {
            result = Math.max(result, Math.abs(value));
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
    public static VectorFunction asConstFunction(double value) {
        return new ConstVectorFunction(value);
    }

    /**
     * Creates a plus function that adds given {@code value} to it's argument.
     *
     * @param value a value to be added to function's argument
     *
     * @return a closure object that does {@code _ + _}
     */
    public static VectorFunction asPlusFunction(double value) {
        return new PlusFunction(value);
    }

    /**
     * Creates a minus function that subtracts given {@code value} from it's argument.
     *
     * @param value a value to be subtracted from function's argument
     *
     * @return a closure that does {@code _ - _}
     */
    public static VectorFunction asMinusFunction(double value) {
        return new MinusFunction(value);
    }

    /**
     * Creates a mul function that multiplies given {@code value} by it's argument.
     *
     * @param value a value to be multiplied by function's argument
     *
     * @return a closure that does {@code _ * _}
     */
    public static VectorFunction asMulFunction(double value) {
        return new MulFunction(value);
    }

    /**
     * Creates a div function that divides it's argument by given {@code value}.
     *
     * @param value a divisor value
     *
     * @return a closure that does {@code _ / _}
     */
    public static VectorFunction asDivFunction(double value) {
        return new DivFunction(value);
    }

    /**
     * Creates a mod function that calculates the modulus of it's argument and given {@code value}.
     *
     * @param value a divisor value
     *
     * @return a closure that does {@code _ % _}
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
     * Creates a singleton 1-length vector of given {@code value}.
     * 
     * @param value the vector's singleton value
     *
     * @return a singleton vector
     */
    public static Vector asSingletonVector(double value) {
        return LinearAlgebra.DEFAULT_FACTORY.createVector(new double[]{value});
    }

    /**
     * Creates a vector source of given {@code vector}.
     * 
     * @param vector the source vector
     *
     * @return a vector source
     */
    public static VectorSource asVectorSource(Vector vector) {
        return new LoopbackVectorSource(vector);
    }

    /**
     * Creates an array vector source of given array {@code reference}.
     * 
     * @param array the source array
     *
     * @return an array vector source
     */
    public static VectorSource asArraySource(double[] array) {
        return new ArrayVectorSource(array);
    }

    /**
     * Creates a random vector source of given {@code length}.
     * 
     * @param length the length of the source
     *
     * @return a random vector source
     */
    public static VectorSource asRandomSource(int length) {
        return new RandomVectorSource(length);
    }

    /**
     * Creates a MatrixMarket stream source of given input stream {@code in}.
     * 
     * @param in the input stream
     *
     * @return a MatrixMarket stream source
     */
    public static VectorSource asMatrixMarketSource(InputStream in) {
        return new StreamVectorSource(new MatrixMarketStream(in));
    }

    /**
     * Creates a symbol separated stream source (like CSV) of given input stream {@code in}.
     *
     * @param in the input stream
     *
     * @return a symbol separated stream source
     */
    public static VectorSource asSymbolSeparatedSource(InputStream in) {
        return new StreamVectorSource(new SymbolSeparatedStream(in));
    }

    /**
     * Creates a symbol separated stream source (like CSV) of given input stream {@code in}.
     *
     * @param in the input stream
     * @param separator the values' separator
     *
     * @return a symbol separated stream source
     */
    public static VectorSource asSymbolSeparatedSource(InputStream in, String separator) {
        return new StreamVectorSource(new SymbolSeparatedStream(in, separator));
    }

    /**
     * Creates a sum vector accumulator that calculates the sum of all elements in the vector.
     * 
     * @param neutral the neutral value
     *
     * @return a sum accumulator
     */
    public static VectorAccumulator asSumAccumulator(double neutral) {
        return new SumVectorAccumulator(neutral);
    }

    /**
     * Creates a product vector accumulator that calculates the product of all elements in the vector.
     * 
     * @param neutral the neutral value
     *
     * @return a product accumulator
     */
    public static VectorAccumulator asProductAccumulator(double neutral) {
        return new ProductVectorAccumulator(neutral);
    }

    /**
     * Makes a minimum vector accumulator that accumulates the minimum across vector elements.
     *
     * @return a minimum vector accumulator
     */
    public static VectorAccumulator mkMinAccumulator() {
        return new MinVectorAccumulator();
    }

    /**
     * Makes a maximum vector accumulator that accumulates the maximum across vector elements.
     *
     * @return a maximum vector accumulator
     */
    public static VectorAccumulator mkMaxAccumulator() {
        return new MaxVectorAccumulator();
    }

    /**
     * Makes an Euclidean norm accumulator that allows to use
     * {@link Vector#fold(org.la4j.vector.functor.VectorAccumulator)} method for norm calculation.
     *
     * @return an Euclidean norm accumulator
     */
    public static VectorAccumulator mkEuclideanNormAccumulator() {
        return new EuclideanNormAccumulator();
    }

    /**
     * Makes a Manhattan norm accumulator that allows to use
     * {@link Vector#fold(org.la4j.vector.functor.VectorAccumulator)} method for norm calculation.
     *
     * @return a Manhattan norm accumulator
     */
    public static VectorAccumulator mkManhattanNormAccumulator() {
        return new ManhattanNormAccumulator();
    }

    /**
     * Makes an Infinity norm accumulator that allows to use
     * {@link Vector#fold(org.la4j.vector.functor.VectorAccumulator)} method for norm calculation.
     *
     * @return an Infinity norm accumulator
     */
    public static VectorAccumulator mkInfinityNormAccumulator() {
        return new InfinityNormAccumulator();
    }

    /**
     * Creates a sum function accumulator, that calculates the sum of all 
     * elements in the vector after applying given {@code function} to each of them.
     * 
     * @param neutral the neutral value
     * @param function the vector function
     *
     * @returna a sum function accumulator
     */
    public static VectorAccumulator asSumFunctionAccumulator(double neutral, VectorFunction function) {
        return new FunctionVectorAccumulator(new SumVectorAccumulator(neutral), function);
    }

    /**
     * Creates a product function accumulator, that calculates the product of
     * all elements in the vector after applying given {@code function} to
     * each of them.
     * 
     * @param neutral the neutral value
     * @param function the vector function
     *
     * @return a product function accumulator
     */
    public static VectorAccumulator asProductFunctionAccumulator(double neutral, VectorFunction function) {
        return new FunctionVectorAccumulator(new ProductVectorAccumulator(neutral), function);
    }
}
