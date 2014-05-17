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

import org.la4j.LinearAlgebra;
import org.la4j.factory.Factory;
import org.la4j.io.VectorToBurningIterator;
import org.la4j.vector.AbstractVector;
import org.la4j.vector.Vector;
import org.la4j.io.VectorIterator;
import org.la4j.vector.Vectors;
import org.la4j.vector.functor.VectorAccumulator;
import org.la4j.vector.functor.VectorProcedure;
import org.la4j.vector.operation.VectorOperation;
import org.la4j.vector.operation.VectorVectorOperation;

import java.util.Iterator;

public abstract class SparseVector extends AbstractVector {

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

    @Override
    public void multiplyInPlace(double value) {
        // TODO: multiply by 0 = clear()
        VectorIterator it = nonZeroIterator();

        while (it.hasNext()) {
            it.next();
            it.set(it.get() * value);
        }
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

    public VectorIterator nonZeroBurningIterator() {
        return iteratorToBurning(nonZeroIterator());
    }

    @Override
    public VectorIterator burningIterator() {
        return iteratorToBurning(iterator());
    }

    private VectorIterator iteratorToBurning(final VectorIterator iterator) {
        return new VectorToBurningIterator(iterator) {
            @Override
            public void flush() {
                // fast flush
                SparseVector.this.cardinality = innerCursor() + 1;
            }
        };
    }

    @Override
    public Vector copy() {
        return resize(length);
    }

    @Override
    public <T> T pipeTo(VectorOperation<T> operation) {
        return operation.apply(this);
    }

    @Override
    public <T> T pipeTo(VectorVectorOperation<T> operation, Vector that) {
        return that.pipeTo(operation.curry(this));
    }
}
