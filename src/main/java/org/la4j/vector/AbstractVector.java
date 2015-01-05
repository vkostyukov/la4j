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
 * Contributor(s): Daniel Renshaw
 *                 Ewald Grusk
 *                 Jakob Moellers
 *                 Maxim Samoylov
 *                 Miron Aseev
 * 
 */

package org.la4j.vector;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Random;

import org.la4j.LinearAlgebra;
import org.la4j.factory.Factory;
import org.la4j.iterator.VectorIterator;
import org.la4j.matrix.Matrix;
import org.la4j.vector.dense.DenseVector;
import org.la4j.vector.functor.VectorAccumulator;
import org.la4j.vector.functor.VectorFunction;
import org.la4j.vector.functor.VectorPredicate;
import org.la4j.vector.functor.VectorProcedure;
import org.la4j.vector.sparse.SparseVector;

/**
 * An abstract wrapper around {@code Vector} to make it easier to implement.
 * <p/>
 * A vector represents an array of elements. It can be re-sized.
 */
public abstract class AbstractVector implements Vector {

    private static final String DEFAULT_DELIMITER = ", ";
    private static final NumberFormat DEFAULT_FORMATTER = new DecimalFormat("0.000");

    protected int length;

    protected Factory factory;

    protected AbstractVector(Factory factory, int length) {
        ensureLengthIsCorrect(length);

        this.factory = factory;
        this.length = length;
    }

    @Override
    public void swapElements(int i, int j) {
        if (i != j) {
            double s = get(i);
            set(i, get(j));
            set(j, s);
        }
    }

    @Override
    public void setAll(double value) {
        VectorIterator it = iterator();

        while (it.hasNext()) {
            it.next();
            it.set(value);
        }
    }

    @Override
    public int length() {
        return length;
    }

    @Override
    public Vector add(double value) {
        VectorIterator it = iterator();
        Vector result = blank();

        while (it.hasNext()) {
            double x = it.next();
            int i = it.index();
            result.set(i, x + value);
        }

        return result;
    }

    @Override
    public Vector add(Vector that) {
        return apply(LinearAlgebra.OO_PLACE_VECTORS_ADDITION, that);
    }

    @Override
    public Vector multiply(double value) {
        VectorIterator it = iterator();
        Vector result = blank();

        while (it.hasNext()) {
            double x = it.next();
            int i = it.index();
            result.set(i, x * value);
        }

        return result;
    }

    @Override
    public Vector hadamardProduct(Vector that) {
        return apply(LinearAlgebra.OO_PLACE_VECTOR_HADAMARD_PRODUCT, that);
    }

    @Override
    public Vector multiply(Matrix matrix) {
        return apply(LinearAlgebra.OO_PLACE_VECTOR_BY_MATRIX_MULTIPLICATION, matrix);
    }

    @Override
    public Vector subtract(double value) {
        return add(-value);
    }

    @Override
    public Vector subtract(Vector that) {
        return apply(LinearAlgebra.OO_PLACE_VECTORS_SUBTRACTION, that);
    }

    @Override
    public Vector divide(double value) {
        return multiply(1.0 / value);
    }

    @Override
    public double product() {
        return fold(Vectors.asProductAccumulator(1.0));
    }

    @Override
    public double sum() {
        return fold(Vectors.asSumAccumulator(0.0));
    }

    @Override
    public double innerProduct(Vector that) {
        return apply(LinearAlgebra.OO_PLACE_INNER_PRODUCT, that);
    }

    @Override
    public Matrix outerProduct(Vector that) {
        return apply(LinearAlgebra.OO_PLACE_OUTER_PRODUCT, that);
    }

    @Override
    public Vector blank() {
        return blankOfLength(length);
    }

    @Override
    public Vector copy() {
        return copyOfLength(length);
    }

    @Override
    public Vector shuffle() {
        Vector result = copy();

        // Conduct Fisher-Yates shuffle
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            int j = random.nextInt(length - i) + i;
            swapElements(i, j);
        }

        return result;
    }

    @Override
    public Vector sliceLeft(int until) {
        return slice(0, until);
    }

    @Override
    public Vector sliceRight(int from) {
        return slice(from, length);
    }

    @Override
    public Vector slice(int from, int until) {
        if (until - from < 0) {
            fail("Wrong slice range: [" + from + ".." + until + "].");
        }

        Vector result = blankOfLength(until - from);

        for (int i = from; i < until; i++) {
            result.set(i - from, get(i));
        }

        return result;
    }

    @Override
    public Vector select(int[] indices) {
        int newLength = indices.length;

        if (newLength == 0) {
            fail("No elements selected.");
        }

        Vector result = blankOfLength(newLength);

        for (int i = 0; i < newLength; i++) {
            result.set(i, get(indices[i]));
        }

        return result;
    }

    @Override
    public void each(VectorProcedure procedure) {
        VectorIterator it = iterator();

        while (it.hasNext()) {
            double x = it.next();
            int i = it.index();
            procedure.apply(i, x);
        }
    }

    @Override
    public double max() {
        return fold(Vectors.mkMaxAccumulator());
    }

    @Override
    public double min() {
        return fold(Vectors.mkMinAccumulator());
    }

    @Override
    public Vector transform(VectorFunction function) {
        VectorIterator it = iterator();
        Vector result = blank();

        while (it.hasNext()) {
            double x = it.next();
            int i = it.index();
            result.set(i, function.evaluate(i, x));
        }

        return result;
    }

    @Override
    public void update(VectorFunction function) {
        VectorIterator it = iterator();

        while (it.hasNext()) {
            double x = it.next();
            int i = it.index();
            it.set(function.evaluate(i, x));
        }
    }

    @Override
    public void updateAt(int i, VectorFunction function) {
        set(i, function.evaluate(i, get(i)));
    }

    @Override
    public double fold(VectorAccumulator accumulator) {
        each(Vectors.asAccumulatorProcedure(accumulator));
        return accumulator.accumulate();
    }

    @Override
    public boolean is(VectorPredicate predicate) {
        boolean result = true;
        VectorIterator it = iterator();

        while (it.hasNext()) {
            double x = it.next();
            int i = it.index();
            result = result && predicate.test(i, x);
        }

        return result;
    }

    @Override
    public boolean non(VectorPredicate predicate) {
        return !is(predicate);
    }

    @Override
    public <T extends Vector> T to(VectorFactory<T> factory) {
        VectorIterator it = iterator();
        T result = factory.apply(length);

        while (it.hasNext()) {
            double x = it.next();
            int i = it.index();
            result.set(i, x);
        }

        return result;
    }

    @Override
    public double norm() {
        return euclideanNorm();
    }

    @Override
    public double euclideanNorm() {
        return fold(Vectors.mkEuclideanNormAccumulator());
    }

    @Override
    public double manhattanNorm() {
        return fold(Vectors.mkManhattanNormAccumulator());
    }

    @Override
    public double infinityNorm() {
        return fold(Vectors.mkInfinityNormAccumulator());
    }

    @Override
    public VectorIterator iterator() {
        return new VectorIterator(length) {
            private int i = -1;

            @Override
            public int index() {
                return i;
            }

            @Override
            public double get() {
                return AbstractVector.this.get(i);
            }

            @Override
            public void set(double value) {
                AbstractVector.this.set(i, value);
            }

            @Override
            public boolean hasNext() {
                return i + 1 < length;
            }

            @Override
            public Double next() {
                i++;
                return get();
            }
        };
    }

    @Override
    public int hashCode() {
        VectorIterator it = iterator();
        int result = 17;

        while (it.hasNext()) {
            long value = it.next().longValue();
            result = 37 * result + (int) (value ^ (value >>> 32));
        }

        return result;
    }

    @Override
    public boolean equals(Vector that, double precision) {
        if (this == that) {
            return true;
        }

        if (this.length != that.length()) {
            return false;
        }

        boolean result = true;

        for (int i = 0; result && i < length; i++) {
            double a = get(i);
            double b = that.get(i);
            double diff = Math.abs(a - b);
            result = (a == b) ||
                     (diff < precision || diff / Math.max(Math.abs(a), Math.abs(b)) < precision);
        }

        return result;
    }

    @Override
    public boolean equals(Object o) {
        return o != null && (o instanceof Vector) && equals((Vector) o, Vectors.EPS);
    }

    @Override
    public String toString() {
        return mkString(DEFAULT_FORMATTER,
                DEFAULT_DELIMITER);
    }

    @Override
    public String mkString(NumberFormat formatter) {
        return mkString(formatter,
                DEFAULT_DELIMITER);
    }

    @Override
    public String mkString(NumberFormat formatter, String delimiter) {
        StringBuilder sb = new StringBuilder();
        VectorIterator it = iterator();

        while (it.hasNext()) {
            double x = it.next();
            int i = it.index();
            sb.append(formatter.format(x))
              .append((i < length - 1 ? delimiter : ""));
        }

        return sb.toString();
    }

    @Override
    public DenseVector toDenseVector() {
        return to(Vectors.DENSE);
    }

    @Override
    public SparseVector toSparseVector() {
        return to(Vectors.SPARSE);
    }

    protected void ensureLengthIsCorrect(int length) {
        if (length < 0) {
            fail("Wrong vector length: " + length);
        }
        if (length == Integer.MAX_VALUE) {
            fail("Wrong vector length: use 'Integer.MAX_VALUE - 1' instead.");
        }
    }

    protected void fail(String message) {
        throw new IllegalArgumentException(message);
    }
}
