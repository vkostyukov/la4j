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

package org.la4j.vector.dense;

import org.la4j.LinearAlgebra;
import org.la4j.factory.Factory;
import org.la4j.vector.AbstractVector;
import org.la4j.vector.Vector;
import org.la4j.vector.operation.VectorOperation;
import org.la4j.vector.operation.VectorVectorOperation;
import org.la4j.vector.source.VectorSource;

import java.util.Random;

/**
 * A dense vector.
 * 
 * A vector represents an array of elements. It can be re-sized.
 * 
 * A dense data structure usually stores data in an underlying array. Zero elements
 * take up memory space. If you want a data structure that will not have zero
 * elements take up memory space, try a sparse structure.
 * 
 * However, fetch/store operations on dense data structures only take O(1) time,
 * instead of the O(log n) time on sparse structures.
 * 
 */
public abstract class DenseVector extends AbstractVector {

    /**
     * Creates an empty {@link DenseVector}.
     */
    public static DenseVector empty() {
        return BasicVector.empty();
    }

    /**
     * Creates a new {@link DenseVector} of the given {@code length}.
     */
    public static DenseVector ofLength(int length) {
        return BasicVector.ofLength(length);
    }

    /**
     * Creates a new {@link BasicVector} from the given {@code values}.
     */
    public static DenseVector of(double... values) {
        return BasicVector.of(values);
    }


    /**
     * Creates a new {@link DenseVector} from the given {@code array}.
     */
    public static DenseVector fromArray(double[] array) {
        return BasicVector.fromArray(array);
    }

    /**
     * Creates a new {@link DenseVector} from the given other {@code vector}.
     */
    public static DenseVector fromVector(Vector vector) {
        return BasicVector.fromVector(vector);
    }

    /**
     * Creates a constant {@link DenseVector} of the given {@code length} with
     * the given {@code value}.
     */
    public static DenseVector constant(int length, double value) {
        return BasicVector.constant(length, value);
    }

    /**
     * Creates a random {@link DenseVector} of the given {@code length} with
     * the given {@code Random}.
     */
    public static DenseVector random(int length, Random random) {
        return BasicVector.random(length, random);
    }

    public DenseVector(int length) {
        super(LinearAlgebra.DENSE_FACTORY, length);
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
     * Converts this dense vector to a double array.
     *
     * @return an array representation of this vector
     */
    public abstract double[] toArray();

    @Override
    public Vector multiply(double value, Factory factory) {
        ensureFactoryIsNotNull(factory);
        Vector result = blank(factory);

        for (int i = 0; i < length; i++) {
            result.set(i, get(i) * value);
        }

        return result;
    }

    @Override
    public void setAll(double value) {
        for (int i = 0; i < length; i++) {
            set(i, value);
        }
    }
}
