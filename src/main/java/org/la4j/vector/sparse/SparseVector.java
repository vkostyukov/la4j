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
 * Contributor(s): -
 * 
 */

package org.la4j.vector.sparse;

import java.util.Iterator;
import java.util.Random;

import org.la4j.LinearAlgebra;
import org.la4j.factory.Factory;
import org.la4j.iterator.VectorIterator;
import org.la4j.vector.AbstractVector;
import org.la4j.vector.Vector;
import org.la4j.vector.VectorFactory;
import org.la4j.vector.Vectors;
import org.la4j.vector.functor.VectorAccumulator;
import org.la4j.vector.functor.VectorProcedure;
import org.la4j.vector.operation.VectorOperation;
import org.la4j.vector.operation.VectorVectorOperation;

/**
 * A sparse vector.
 * 
 * A vector represents an array of elements. It can be re-sized.
 * 
 * A sparse data structure does not store blank elements, and instead just stores
 * elements with values. A sparse data structure can be initialized with a large
 * length but take up no storage until the space is filled with non-zero elements.
 * 
 * However, there is a performance cost. Fetch/store operations take O(log n)
 * time instead of the O(1) time of a dense data structure.
 * 
 */
public abstract class SparseVector extends AbstractVector {

    /**
     * Creates an empty {@link SparseVector}.
     */
    public static SparseVector empty() {
        return CompressedVector.empty();
    }

    /**
     * Creates a new {@link SparseVector} of the given {@code length}.
     */
    public static SparseVector ofLength(int length) {
        return CompressedVector.ofLength(length);
    }

    /**
     * Creates a new {@link SparseVector} from the given {@code values}.
     */
    public static SparseVector of(double... values) {
        return CompressedVector.of(values);
    }

    /**
     * Creates a new {@link SparseVector} from the given {@code array}.
     */
    public static SparseVector fromArray(double[] array) {
        return CompressedVector.fromArray(array);
    }

    /**
     * Creates a new {@link SparseVector} from the given other {@code vector}.
     */
    public static SparseVector fromVector(Vector vector) {
        return CompressedVector.fromVector(vector);
    }

    /**
     * Creates a constant {@link SparseVector} of the given {@code length} with
     * the given {@code value}.
     */
    public static SparseVector constant(int length, double value) {
        return CompressedVector.constant(length, value);
    }

    /**
     * Creates a constant {@link SparseVector} of the given {@code length} with
     * the given {@code value}.
     */
    public static SparseVector random(int length, double density, Random random) {
        return CompressedVector.random(length, density, random);
    }

    protected int cardinality;

    public SparseVector(int length, int cardinality) {
        super(LinearAlgebra.SPARSE_FACTORY, length);
        this.cardinality = cardinality;
    }

    /**
     * Returns the cardinality (the number of non-zero elements)
     * of this sparse vector.
     *
     * @return the cardinality of this vector
     */
    public int cardinality() {
        return cardinality;
    }

    /**
     * Returns the density (non-zero elements divided by total elements)
     * of this sparse vector.
     *
     * @return the density of this vector
     */
    public double density() {
        return cardinality / (double) length;
    }

    @Override
    public void assign(double value) {
        // fast clear
        if (value == 0.0) {
            cardinality = 0;
        } else {
            super.assign(value);
        }
    }

    @Override
    public double get(int i) {
        return getOrElse(i, 0.0);
    }

    /**
     * Gets the specified element, or a {@code defaultValue} if there
     * is no actual element at index {@code i} in this sparse vector.
     *
     * @param i the element's index
     * @param defaultValue the default value
     *
     * @return the element of this vector or a default value
     */
    public abstract double getOrElse(int i, double defaultValue);

    /**
     * Whether or not the specified element is zero.
     *
     * @param i element's index
     *
     * @return {@code true} if specified element is zero, {@code false} otherwise
     */
    public boolean isZeroAt(int i) {
        return !nonZeroAt(i);
    }

    /**
     * * Whether or not the specified element is not zero.
     *
     * @param i element's index
     *
     * @return {@code true} if specified element is zero, {@code false} otherwise
     */
    public abstract boolean nonZeroAt(int i);

    /**
     * Folds non-zero elements of this vector with given {@code accumulator}.
     *
     * @param accumulator the vector accumulator
     *
     * @return the accumulated value
     */
    public double foldNonZero(VectorAccumulator accumulator) {
        eachNonZero(Vectors.asAccumulatorProcedure(accumulator));
        return accumulator.accumulate();
    }

    /**
     * Applies given {@code procedure} to each non-zero element of this vector.
     *
     * @param procedure the vector procedure
     */
    public void eachNonZero(VectorProcedure procedure) {
        VectorIterator it = nonZeroIterator();
        while (it.hasNext()) {
            it.next();
            procedure.apply(it.index(), it.get());
        }
    }

    @Override
    public double max() {
        double max = foldNonZero(Vectors.mkMaxAccumulator());
        return (max > 0.0) ? max : 0.0;
    }

    @Override
    public double min() {
        double min = foldNonZero(Vectors.mkMinAccumulator());
        return (min < 0.0) ? min : 0.0;
    }

    @Override
    public Vector multiply(double value, Factory factory) {
        Vector result = blank(factory);
        VectorIterator it = nonZeroIterator();

        while (it.hasNext()) {
            it.next();
            result.set(it.index(), it.get() * value);
        }

        return result;
    }

    /**
     * Returns a non-zero iterator.
     *
     * @return a non-zero iterator
     */
    public abstract VectorIterator nonZeroIterator();

    /**
     * Returns a non-zero iterable instance. This method is useful in for-each loops.
     *
     * @return a non-zero iterable instance
     */
    public Iterable<Double> skipZeros() {
        return new Iterable<Double>() {
            @Override
            public Iterator<Double> iterator() {
                return nonZeroIterator();
            }
        };
    }

    @Override
    public Vector copy() {
        return copyOfLength(length);
    }

    @Override
    public <T extends Vector> T to(VectorFactory<T> factory) {
        T result = factory.vectorOfLength(length);
        VectorIterator it = nonZeroIterator();

        while (it.hasNext()) {
            it.next();
            result.set(it.index(), it.get());
        }

        return result;
    }

    @Override
    public <T> T apply(VectorOperation<T> operation) {
        return operation.apply(this);
    }

    @Override
    public <T> T apply(VectorVectorOperation<T> operation, Vector that) {
        return that.apply(operation.curry(this));
    }
    
    /**
     * Ensures the provided index is in the bounds of this {@link SparseVector}.
     * 
     * @param i The index to check.
     */
    protected void ensureIndexIsInBounds(int i) {
        if (i < 0 || i >= this.length) {
            throw new IndexOutOfBoundsException("Index '" + i + "' is invalid.");
        }
    }
}
