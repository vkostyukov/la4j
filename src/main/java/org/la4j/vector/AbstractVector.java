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
import org.la4j.vector.sparse.CompressedVector;
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
    public void swap(int i, int j) {
        swapElements(i, j);
    }

    @Override
    public void clear() {
        setAll(0.0);
    }

    @Override
    public void setAll(double value) {
        for (int i = 0; i < length; i++) {
            set(i, value);
        }
    }

    @Override
    public void assign(double value) {
        setAll(value);
    }

    @Override
    public int length() {
        return length;
    }

    @Override
    public Vector add(double value) {
        Vector result = blank();
        VectorIterator it = iterator();

        while (it.hasNext()) {
            it.next();
            result.set(it.index(), it.get() + value);
        }

        return result;
    }

    @Override
    public Vector add(double value, Factory factory) {
        ensureFactoryIsNotNull(factory);
        return add(value).to(Factory.asVectorFactory(factory));
    }

    @Override
    public Vector add(Vector that) {
        ensureArgumentIsNotNull(that, "vector");
        ensureVectorIsSimilar(that);

        return apply(LinearAlgebra.OO_PLACE_VECTORS_ADDITION, that);
    }

    @Override
    public Vector add(Vector that, Factory factory) {
        ensureFactoryIsNotNull(factory);
        return add(that).to(Factory.asVectorFactory(factory));
    }

    @Override
    public Vector multiply(double value) {
        Vector result = blank();

        for (int i = 0; i < length; i++) {
            result.set(i, get(i) * value);
        }

        return result;
    }

    @Override
    public Vector multiply(double value, Factory factory) {
        ensureFactoryIsNotNull(factory);
        return multiply(value).to(Factory.asVectorFactory(factory));
    }

    @Override
    public Vector hadamardProduct(Vector that) {
        ensureArgumentIsNotNull(that, "vector");
        ensureVectorIsSimilar(that);
        return apply(LinearAlgebra.OO_PLACE_HADAMARD_PRODUCT, that);
    }

    @Override
    public Vector hadamardProduct(Vector that, Factory factory) {
        ensureFactoryIsNotNull(factory);
        return hadamardProduct(that).to(Factory.asVectorFactory(factory));
    }

    @Override
    public Vector multiply(Matrix matrix) {
        return multiply(matrix, factory);
    }

    @Override
    public Vector multiply(Matrix matrix, Factory factory) {
        // TODO: export as operation (blocked by no-support of matrices)
        ensureFactoryIsNotNull(factory);
        ensureArgumentIsNotNull(matrix, "matrix");

        if (length != matrix.rows()) {
            fail("Wrong matrix dimensions: " + matrix.rows() + "x" + matrix.columns() +
                    ". Should be: " + length + "x_.");
        }

        Vector result = factory.createVector(matrix.columns());

        for (int j = 0; j < matrix.columns(); j++) {

            double acc = 0.0;

            for (int i = 0; i < matrix.rows(); i++) {
                acc += get(i) * matrix.get(i, j);
            }

            result.set(j, acc);
        }

        return result;
    }

    @Override
    public Vector subtract(double value) {
        return subtract(value, factory);
    }

    @Override
    public Vector subtract(double value, Factory factory) {
        return add(-value, factory);
    }

    @Override
    public Vector subtract(Vector that) {
        ensureArgumentIsNotNull(that, "vector");
        ensureVectorIsSimilar(that);

        return apply(LinearAlgebra.OO_PLACE_VECTORS_SUBTRACTION, that);
    }

    @Override
    public Vector subtract(Vector that, Factory factory) {
        ensureFactoryIsNotNull(factory);
        return subtract(that).to(Factory.asVectorFactory(factory));
    }

    @Override
    public Vector divide(double value) {
        return divide(value, factory);
    }

    @Override
    public Vector divide(double value, Factory factory) {
        return multiply(1.0 / value, factory);
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
        ensureArgumentIsNotNull(that, "vector");
        ensureVectorIsSimilar(that);

        return apply(LinearAlgebra.OO_PLACE_INNER_PRODUCT, that);
    }

    @Override
    public Matrix outerProduct(Vector that) {
        return outerProduct(that, factory);
    }

    @Override
    public Matrix outerProduct(Vector that, Factory factory) {
        // TODO: export as operation (blocked by no-support of matrices)
        ensureFactoryIsNotNull(factory);
        ensureArgumentIsNotNull(that, "vector");

        Matrix result = factory.createMatrix(length, that.length());

        for (int i = 0; i < length; i++) {
            for (int j = 0; j < that.length(); j++) {
                result.set(i, j, get(i) * that.get(j));
            }
        }

        return result;
    }

    @Override
    public Vector blank() {
        return blankOfLength(length);
    }

    @Override
    public Vector blank(Factory factory) {
        ensureFactoryIsNotNull(factory);

        return factory.createVector(length);
    }

    @Override
    public Vector copy() {
        return copyOfLength(length);
    }

    @Override
    public Vector copy(Factory factory) {
        ensureFactoryIsNotNull(factory);

        if (factory == this.factory) {
            return copy();
        }

        return factory.createVector(this);
    }

    @Override
    @Deprecated
    public Vector resize(int length) {
        return copyOfLength(length);
    }

    @Override
    @Deprecated
    public Vector resize(int length, Factory factory) {
        return copyOfLength(length).to(Factory.asVectorFactory(factory));
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

    /**
     * Shuffles this vector, using a Fisher-Yates shuffle.
     * <p/>
     * Copies this vector in the new vector that contains the same elements but with
     * the elements shuffled around (which might also result in the same vector,
     * since all outcomes are equally probable).
     *
     * @param factory the factory of result vector
     * @return the shuffled vector
     */
    @Override
    public Vector shuffle(Factory factory) {
        ensureFactoryIsNotNull(factory);
        return shuffle().to(Factory.asVectorFactory(factory));
    }

    @Override
    public Vector sliceLeft(int until) {
        return slice(0, until);
    }

    @Override
    public Vector sliceLeft(int until, Factory factory) {
        return slice(0, until, factory);
    }

    @Override
    public Vector sliceRight(int from) {
        return slice(from, length);
    }

    @Override
    public Vector sliceRight(int from, Factory factory) {
        return slice(from, length, factory);
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
    public Vector slice(int from, int until, Factory factory) {
        ensureFactoryIsNotNull(factory);
        return slice(from, until).to(Factory.asVectorFactory(factory));
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
    public Vector select(int[] indices, Factory factory) {
        return select(indices).to(Factory.asVectorFactory(factory));
    }

    @Override
    public void each(VectorProcedure procedure) {
        VectorIterator it = iterator();
        while (it.hasNext()) {
            it.next();
            procedure.apply(it.index(), it.get());
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
        Vector result = blank();

        for (int i = 0; i < length; i++) {
            result.set(i, function.evaluate(i, get(i)));
        }

        return result;
    }

    @Override
    public Vector transform(VectorFunction function, Factory factory) {
        return transform(function).to(Factory.asVectorFactory(factory));
    }

    @Override
    public Vector transform(int i, VectorFunction function) {
        return transform(i, function, factory);
    }

    @Override
    public Vector transform(int i, VectorFunction function, Factory factory) {

        Vector result = copy(factory);
        result.set(i, function.evaluate(i, get(i)));

        return result;
    }

    @Override
    public void update(VectorFunction function) {
        for (int i = 0; i < length; i++) {
            set(i, function.evaluate(i, get(i)));
        }
    }

    @Override
    public void update(int i, VectorFunction function) {
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
            it.next();
            result = result && predicate.test(it.index(), it.get());
        }

        return result;
    }

    @Override
    public boolean non(VectorPredicate predicate) {
        return !is(predicate);
    }

    @Override
    public Matrix toRowMatrix(Factory factory) {
        Matrix result = factory.createMatrix(1, length);
        result.setRow(0, this);
        return result;
    }

    @Override
    public Matrix toColumnMatrix(Factory factory) {
        Matrix result = factory.createMatrix(length, 1);
        result.setColumn(0, this);
        return result;
    }

    @Override
    public <T extends Vector> T to(VectorFactory<T> factory) {
        T result = factory.vectorOfLength(length);

        for (int i = 0; i < length; i++) {
            result.set(i, get(i));
        }

        return result;
    }

    @Override
    public Vector normalize() {
        return normalize(Vectors.mkEuclideanNormAccumulator());
    }

    @Override
    public Vector normalize(VectorAccumulator acc) {
        return divide(fold(acc));
    }

    @Override
    public int hashCode() {
        int result = 17;

        for (int i = 0; i < length; i++) {
            long value = (long) get(i);
            result = 37 * result + (int) (value ^ (value >>> 32));
        }

        return result;
    }

    @Override
    public boolean equals(Vector that, double precision) {
        return apply(Vectors.ooPlaceVectorToVectorComparison(precision), that);
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
            it.next();
            sb.append(formatter.format(it.get()));
            sb.append((it.index() < length - 1 ? delimiter : ""));
        }

        return sb.toString();
    }

    @Override
    public Factory factory() {
        return factory;
    }

    @Override
    public DenseVector toDenseVector() {
        return to(Vectors.DENSE);
    }

    @Override
    public SparseVector toSparseVector() {
        return to(Vectors.SPARSE);
    }

    protected void ensureFactoryIsNotNull(Factory factory) {
        ensureArgumentIsNotNull(factory, "factory");
    }

    protected void ensureLengthIsCorrect(int length) {
        if (length < 0) {
            fail("Wrong vector length: " + length);
        }
        if (length == Integer.MAX_VALUE) {
            fail("Wrong vector length: use 'Integer.MAX_VALUE - 1' instead.");
        }
    }

    protected void ensureVectorIsSimilar(Vector that) {
        if (length != that.length()) {
            fail("Wrong vector length: " + that.length() + ". Should be: " + length + ".");
        }
    }

    protected void ensureArgumentIsNotNull(Object argument, String name) {
        if (argument == null) {
            fail("Bad argument: \"" + name + "\" is 'null'.");
        }
    }

    protected void fail(String message) {
        throw new IllegalArgumentException(message);
    }
}
