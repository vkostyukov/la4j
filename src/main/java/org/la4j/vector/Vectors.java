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

package org.la4j.vector;

import java.io.InputStream;

import org.la4j.factory.Basic1DFactory;
import org.la4j.factory.CRSFactory;
import org.la4j.factory.Factory;
import org.la4j.factory.SafeFactory;
import org.la4j.io.MatrixMarketStream;
import org.la4j.io.SymbolSeparatedStream;
import org.la4j.matrix.Matrices;
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

    public static final double EPS = Matrices.EPS;

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

    private static class IncVecorFunction implements VectorFunction {
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

    private static class SumVectorAccumulator implements VectorAccumulator {

        private double result;

        public SumVectorAccumulator(double neutral) {
            this.result = neutral;
        }

        @Override
        public void update(int i, double value) {
            result += value;
        }

        @Override
        public double accumulate() {
            return result;
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

        private double result;

        public ProductVectorAccumulator(double neutral) {
            this.result = neutral;
        }

        @Override
        public void update(int i, double value) {
            result *= value;
        }

        @Override
        public double accumulate() {
            return result;
        }
    }

    public static VectorFunction asPlusFunction(double value) {
        return new PlusFunction(value);
    }

    public static VectorFunction asMinusFunction(double value) {
        return new MinusFunction(value);
    }

    public static VectorFunction asMulFunction(double value) {
        return new MulFunction(value);
    }

    public static VectorFunction asDivFunction(double value) {
        return new DivFunction(value);
    }

    public static final VectorPredicate ZERO_VECTOR =
            new ZeroVectorPredicate();

    public static final VectorPredicate POSITIVE_VECTOR =
            new PositiveVectorPredicate();

    public static final VectorPredicate NEGATIVE_VECTOR = 
            new NegativeVectorPredicate();

    public static final Factory BASIC_FACTORY = new Basic1DFactory();

    public static final Factory COMPRESSED_FACTORY = new CRSFactory();

    public static final Factory SAFE_BASIC_FACTORY = 
            new SafeFactory(BASIC_FACTORY);

    public static final Factory SAFE_COMPRESSED_FACTORY = 
            new SafeFactory(COMPRESSED_FACTORY);

    public static final Factory UNSAFE_BASIC_FACTORY = BASIC_FACTORY;

    public static final Factory UNSAFE_COMPRESSED_FACTORY = COMPRESSED_FACTORY;

    public static final Factory DEFAULT_FACTORY = BASIC_FACTORY;

    public static final Factory DEFAULT_DENSE_FACTORY = BASIC_FACTORY;

    public static final Factory DEFAULT_SPARSE_FACTORY = COMPRESSED_FACTORY;

    public static final VectorFunction INC_VECTOR = new IncVecorFunction();

    public static final VectorFunction DEC_VECTOR = new DecVectorFunction();

    public static Vector asSingletonVector(double value) {
        return DEFAULT_FACTORY.createVector(new double[] { value });
    }

    public static Vector asSafeVector(Vector vector) {
        return vector.safe();
    }

    public static Vector asUnsafeVector(Vector vector) {
        return vector.unsafe();
    }

    public static VectorSource asSafeSource(Vector vector) {
        return new SafeVectorSource(vector);
    }

    public static VectorSource asUnsafeSource(Vector vector) {
        return new UnsafeVectorSource(vector);
    }

    public static VectorSource asArraySource(double[] array) {
        return new ArrayVectorSource(array);
    }

    public static VectorSource asRandomSource(int length) {
        return new RandomVectorSource(length);
    }

    public static VectorSource asMatrixMarketSource(InputStream in) {
        return new StreamVectorSource(new MatrixMarketStream(in));
    }

    public static VectorSource asSymbolSeparatedSource(InputStream in) {
        return new StreamVectorSource(new SymbolSeparatedStream(in));
    }

    public static VectorSource asSymbolSeparatedSource(InputStream in, 
            String separator) {

        return new StreamVectorSource(new SymbolSeparatedStream(in, separator));
    }

    public static VectorAccumulator asSumAccumulator(double neutral) {
        return new SumVectorAccumulator(neutral);
    }

    public static VectorAccumulator asProductAccumulator(double neutral) {
        return new ProductVectorAccumulator(neutral);
    }

    public static VectorAccumulator asSumFunctionAccumulator(double neutral, 
            VectorFunction function) {

        return new FunctionVectorAccumulator(new SumVectorAccumulator(neutral), 
                                             function);
    }

    public static VectorAccumulator asProductFunctionAccumulator(double neutral, 
            VectorFunction function) {

        return new FunctionVectorAccumulator(new ProductVectorAccumulator(neutral),
                                             function);
    }
}
