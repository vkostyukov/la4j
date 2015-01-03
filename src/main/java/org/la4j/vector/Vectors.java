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
import java.util.Random;
import org.la4j.LinearAlgebra;
import org.la4j.io.MatrixMarketStream;
import org.la4j.io.SymbolSeparatedStream;
import org.la4j.vector.dense.BasicVector;
import org.la4j.vector.functor.VectorAccumulator;
import org.la4j.vector.functor.VectorFunction;
import org.la4j.vector.functor.VectorPredicate;
import org.la4j.vector.functor.VectorProcedure;
import org.la4j.vector.source.ArrayVectorSource;
import org.la4j.vector.source.LoopbackVectorSource;
import org.la4j.vector.source.RandomVectorSource;
import org.la4j.vector.source.StreamVectorSource;
import org.la4j.vector.source.VectorSource;
import org.la4j.vector.sparse.CompressedVector;

public final class Vectors {

    public static final double EPS = LinearAlgebra.EPS;
    public static final int ROUND_FACTOR = LinearAlgebra.ROUND_FACTOR;

    public static final VectorFactory<BasicVector> DENSE = new VectorFactory<BasicVector>() {
        @Override
        public BasicVector apply(int length) {
            return BasicVector.zero(length);
        }
    };

    public static final VectorFactory<CompressedVector> SPARSE = new VectorFactory<CompressedVector>() {
        @Override
        public CompressedVector apply(int length) {
            return CompressedVector.zero(length);
        }
    };

    public static final VectorFactory[] FACTORIES = {
            DENSE, SPARSE
    };

    /**
     * Checks whether the vector is a
     * <a href="http://mathworld.wolfram.com/ZeroMatrix.html">zero
     * vector</a>.
     */
    public static final VectorPredicate ZERO_VECTOR = new VectorPredicate() {
        @Override
        public boolean test(int i, double value) {
            return Math.abs(value) < EPS;
        }
    };

    /**
     * Checks whether the vector is a
     * <a href="http://mathworld.wolfram.com/PositiveMatrix.html">positive
     * vector</a>.
     */
    public static final VectorPredicate POSITIVE_VECTOR = new VectorPredicate() {
        @Override
        public boolean test(int i, double value) {
            return value > 0.0;
        }
    };

    /**
     * Checks whether the vector is a
     * <a href="http://mathworld.wolfram.com/NegativeMatrix.html">negative
     * vector</a>.
     */
    public static final VectorPredicate NEGATIVE_VECTOR = new VectorPredicate() {
        @Override
        public boolean test(int i, double value) {
            return value < 0.0;
        }
    };

    /**
     * Increases each element of vector by <code>1</code>.
     */
    public static final VectorFunction INC_FUNCTION = new VectorFunction() {
        @Override
        public double evaluate(int i, double value) {
            return value + 1.0;
        }
    };

    /**
     * Decreases each element of vectors by <code>1</code>.
     */
    public static final VectorFunction DEC_FUNCTION = new VectorFunction() {
        @Override
        public double evaluate(int i, double value) {
            return value - 1.0;
        }
    };

    /**
     * Inverts each element of vector.
     */
    public static final VectorFunction INV_FUNCTION = new VectorFunction() {
        @Override
        public double evaluate(int i, double value) {
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
    public static VectorFunction asConstFunction(final double arg) {
        return new VectorFunction() {
            @Override
            public double evaluate(int i, double value) {
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
    public static VectorFunction asPlusFunction(final double arg) {
        return new VectorFunction() {
            @Override
            public double evaluate(int i, double value) {
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
    public static VectorFunction asMinusFunction(final double arg) {
        return new VectorFunction() {
            @Override
            public double evaluate(int i, double value) {
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
    public static VectorFunction asMulFunction(final double arg) {
        return new VectorFunction() {
            @Override
            public double evaluate(int i, double value) {
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
    public static VectorFunction asDivFunction(final double arg) {
        return new VectorFunction() {
            @Override
            public double evaluate(int i, double value) {
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
    public static VectorFunction asModFunction(final double arg) {
        return new VectorFunction() {
            @Override
            public double evaluate(int i, double value) {
                return value % arg;
            }
        };
    }

    /**
     * Creates a sum vector accumulator that calculates the sum of all elements in the vector.
     *
     * @param neutral the neutral value
     *
     * @return a sum accumulator
     */
    public static VectorAccumulator asSumAccumulator(final double neutral) {
        return new VectorAccumulator() {
            private BigDecimal result = new BigDecimal(neutral);

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
        };
    }

    /**
     * Creates a product vector accumulator that calculates the product of all elements in the vector.
     *
     * @param neutral the neutral value
     *
     * @return a product accumulator
     */
    public static VectorAccumulator asProductAccumulator(final double neutral) {
        return new VectorAccumulator() {
            private BigDecimal result = new BigDecimal(neutral);

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
        };
    }

    /**
     * Makes a minimum vector accumulator that accumulates the minimum across vector elements.
     *
     * @return a minimum vector accumulator
     */
    public static VectorAccumulator mkMinAccumulator() {
        return new VectorAccumulator() {
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
        };
    }

    /**
     * Makes a maximum vector accumulator that accumulates the maximum across vector elements.
     *
     * @return a maximum vector accumulator
     */
    public static VectorAccumulator mkMaxAccumulator() {
        return new VectorAccumulator() {
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
        };
    }

    /**
     * Makes an Euclidean norm accumulator that allows to use
     * {@link Vector#fold(org.la4j.vector.functor.VectorAccumulator)} method for norm calculation.
     *
     * @return an Euclidean norm accumulator
     */
    public static VectorAccumulator mkEuclideanNormAccumulator() {
        return new VectorAccumulator() {
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
        };
    }

    /**
     * Makes a Manhattan norm accumulator that allows to use
     * {@link Vector#fold(org.la4j.vector.functor.VectorAccumulator)} method for norm calculation.
     *
     * @return a Manhattan norm accumulator
     */
    public static VectorAccumulator mkManhattanNormAccumulator() {
        return new VectorAccumulator() {
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
        };
    }

    /**
     * Makes an Infinity norm accumulator that allows to use
     * {@link Vector#fold(org.la4j.vector.functor.VectorAccumulator)} method for norm calculation.
     *
     * @return an Infinity norm accumulator
     */
    public static VectorAccumulator mkInfinityNormAccumulator() {
        return new VectorAccumulator() {
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
        };
    }

    /**
     * Creates a sum function accumulator, that calculates the sum of all
     * elements in the vector after applying given {@code function} to each of them.
     *
     * @param neutral the neutral value
     * @param function the vector function
     *
     * @return a sum function accumulator
     */
    public static VectorAccumulator asSumFunctionAccumulator(final double neutral,
                                                             final VectorFunction function) {
        return new VectorAccumulator() {
            private final VectorAccumulator sumAccumulator = Vectors.asSumAccumulator(neutral);

            @Override
            public void update(int i, double value) {
                sumAccumulator.update(i, function.evaluate(i, value));
            }

            @Override
            public double accumulate() {
                return sumAccumulator.accumulate();
            }
        };
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
    public static VectorAccumulator asProductFunctionAccumulator(final double neutral,
                                                                 final VectorFunction function) {
        return new VectorAccumulator() {
            private final VectorAccumulator productAccumulator = Vectors.asProductAccumulator(neutral);

            @Override
            public void update(int i, double value) {
                productAccumulator.update(i, function.evaluate(i, value));
            }

            @Override
            public double accumulate() {
                return productAccumulator.accumulate();
            }
        };
    }

    /**
     * Creates an accumulator procedure that adapts a vector accumulator for procedure
     * interface. This is useful for reusing a single accumulator for multiple fold operations
     * in multiple vectors.
     *
     * @param accumulator the vector accumulator
     *
     * @return an accumulator procedure
     */
    public static VectorProcedure asAccumulatorProcedure(final VectorAccumulator accumulator) {
        return new VectorProcedure() {
            @Override
            public void apply(int i, double value) {
                accumulator.update(i, value);
            }
        };
    }

    /**
     * Creates a vector source of given {@code vector}.
     * 
     * @param vector the source vector
     *
     * @return a vector source
     */
    @Deprecated
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
    @Deprecated
    public static VectorSource asArraySource(double[] array) {
        return new ArrayVectorSource(array);
    }

    /**
     * Creates a random vector source of given {@code length}.
     *
     * This method is deprecated. Use {@link org.la4j.vector.dense.DenseVector#random(int, java.util.Random)} instead.
     * 
     * @param length the length of the source
     *
     * @return a random vector source
     */
    @Deprecated
    public static VectorSource asRandomSource(int length, Random random) {
        return new RandomVectorSource(length, random);
    }

    /**
     * Creates a MatrixMarket stream source of given input stream {@code in}.
     * 
     * @param in the input stream
     *
     * @return a MatrixMarket stream source
     */
    @Deprecated
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
    @Deprecated
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
    @Deprecated
    public static VectorSource asSymbolSeparatedSource(InputStream in, String separator) {
        return new StreamVectorSource(new SymbolSeparatedStream(in, separator));
    }

    /**
     * Creates a singleton 1-length vector of given {@code value}.
     *
     * This method is deprecated. Use {@link org.la4j.vector.dense.DenseVector#of(double...)} instead.
     *
     * @param value the vector's singleton value
     *
     * @return a singleton vector
     */
    @Deprecated
    public static Vector asSingletonVector(double value) {
        return LinearAlgebra.DEFAULT_FACTORY.createVector(new double[]{value});
    }

    /**
     * Creates a default vector from given vararg {@code values}.
     *
     * This method is deprecated. Use {@link org.la4j.vector.dense.DenseVector#of(double...)} instead.
     *
     * @param values of the vector
     *
     * @return a default vector
     */
    @Deprecated
    public static Vector asVector(double... values) {
        return LinearAlgebra.DEFAULT_FACTORY.createVector(values);
    }
}
