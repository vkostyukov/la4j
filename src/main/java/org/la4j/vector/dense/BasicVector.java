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

package org.la4j.vector.dense;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Random;

import org.la4j.vector.Vector;
import org.la4j.vector.VectorFactory;
import org.la4j.vector.Vectors;
import org.la4j.vector.source.VectorSource;

/**
 * A basic dense vector implementation using an array.
 * 
 * A dense data structure stores data in an underlying array. Even zero elements
 * take up memory space. If you want a data structure that will not have zero
 * elements take up memory space, try a sparse structure.
 * 
 * However, fetch/store operations on dense data structures only take O(1) time,
 * instead of the O(log n) time on sparse structures.
 * 
 * {@code BasicVector} stores the underlying data in a standard array.
 */
public class BasicVector extends DenseVector {

    private static final long serialVersionUID = 4071505L;

    /**
     * Creates a zero {@link BasicVector} of the given {@code length}.
     */
    public static BasicVector zero(int length) {
        return new BasicVector(length);
    }

    /**
     * Creates a constant {@link BasicVector} of the given {@code length} with
     * the given {@code value}.
     */
    public static BasicVector constant(int length, double value) {
        double array[] = new double[length];
        Arrays.fill(array, value);

        return new BasicVector(array);
    }

    /**
     * Creates an unit {@link BasicVector} of the given {@code length}.
     */
    public static BasicVector unit(int length) {
        return BasicVector.constant(length, 1.0);
    }

    /**
     * Creates a random {@link BasicVector} of the given {@code length} with
     * the given {@code Random}.
     */
    public static BasicVector random(int length, Random random) {
        double array[] = new double[length];
        for (int i = 0; i < length; i++) {
            array[i] = random.nextDouble();
        }

        return new BasicVector(array);
    }

    /**
     * Creates a new {@link BasicVector} from the given {@code array} w/o
     * copying the underlying array.
     */
    public static BasicVector fromArray(double[] array) {
        return new BasicVector(array);
    }

    private double self[];

    public BasicVector() {
        this(0);
    }

    /**
     * This constructor is deprecated. Use {@link Vector#to(VectorFactory)}
     * instead.
     *
     * <p />
     *
     * Creates a new {@link BasicVector} as a copy of the given {@code vector}.
     *
     * @param vector the vector to copy
     */
    @Deprecated
    public BasicVector(Vector vector) {
        this(Vectors.asVectorSource(vector));
    }

    @Deprecated
    public BasicVector(VectorSource source) {
        this(source.length());

        for (int i = 0; i < length; i++) {
            self[i] = source.get(i);
        }
    }

    public BasicVector(int length) {
        this(new double[length]);
    }

    public BasicVector(double array[]) {
        super(array.length);
        this.self = array;
    }

    @Override
    public double get(int i) {
        return self[i];
    }

    @Override
    public void set(int i, double value) {
        self[i] = value;
    }

    @Override
    public void swapElements(int i, int j) {
        if (i != j) {
            double d = self[i];
            self[i] = self[j];
            self[j] = d;
        }
    }

    @Override
    public Vector copyOfLength(int length) {
      ensureLengthIsCorrect(length);

      double $self[] = new double[length];
      System.arraycopy(self, 0, $self, 0, Math.min($self.length, self.length));

      return new BasicVector($self);
    }

    @Override
    public double[] toArray() {
        double result[] = new double[length];
        System.arraycopy(self, 0, result, 0, length);
        return result;
    }

    @Override
    public <T extends Vector> T to(VectorFactory<T> factory) {
        if (factory.outputClass == BasicVector.class) {
            return factory.outputClass.cast(this);
        }

        return super.to(factory);
    }

    @Override
    public Vector blankOfLength(int length) {
        return BasicVector.zero(length);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(length);

        for (int i = 0; i < length; i++) {
            out.writeDouble(self[i]);
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {

        length = in.readInt();

        self = new double[length];

        for (int i = 0; i < length; i++) {
            self[i] = in.readDouble();
        }
    }
}
