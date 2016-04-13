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

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.la4j.Vector;
import org.la4j.Vectors;
import org.la4j.vector.DenseVector;
import org.la4j.vector.VectorFactory;

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

    private static final byte VECTOR_TAG = (byte) 0x00;

    private double[] self;

    public BasicVector() {
        this(0);
    }

    public BasicVector(int length) {
        this(new double[length]);
    }

    public BasicVector(double[] array) {
        super(array.length);
        this.self = array;
    }

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
        double[] array = new double[length];
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
        double[] array = new double[length];
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

    /**
     * Decodes {@link BasicVector} from the given byte {@code array}.
     *
     * @param array the byte array representing a vector
     *
     * @return a decoded vector
     */
    public static BasicVector fromBinary(byte[] array) {
        ByteBuffer buffer = ByteBuffer.wrap(array);

        if (buffer.get() != VECTOR_TAG) {
            throw new IllegalArgumentException("Can not decode BasicVector from the given byte array.");
        }

        double[] values = new double[buffer.getInt()];
        for (int i = 0; i < values.length; i++) {
            values[i] = buffer.getDouble();
        }

        return new BasicVector(values);
    }

    /**
     * Parses {@link BasicVector} from the given CSV string.
     *
     * @param csv the CSV string representing a vector
     *
     * @return a parsed vector
     */
    public static BasicVector fromCSV(String csv) {
        return Vector.fromCSV(csv).to(Vectors.BASIC);
    }

    /**
     * Parses {@link BasicVector} from the given Matrix Market string.
     *
     * @param mm the string in Matrix Market format
     *
     * @return a parsed vector
     */
    public static BasicVector fromMatrixMarket(String mm) {
        return Vector.fromMatrixMarket(mm).to(Vectors.BASIC);
    }

    /**
     * Creates new {@link BasicVector} from
     *
     * @param list list containing doubles
     *
     * @return new vector from given double list
     */
    public static BasicVector fromCollection(Collection<? extends Number> list) {
        //TODO goto lambdas
        double[] self = new double[list.size()];
        int i = 0;
        for (Number x : list) {
            self[i] = x.doubleValue();
            i++;
        }
        return fromArray(self);
    }

    /**
     * Creates new {@link BasicVector} from index-value map
     *
     * @param map index-value map
     *
     * @param length vector length
     *
     * @return created vector
     */
    public static BasicVector fromMap(Map<Integer, ? extends Number> map, int length) {
        return Vector.fromMap(map, length).to(Vectors.BASIC);
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

      double[] $self = new double[length];
      System.arraycopy(self, 0, $self, 0, Math.min($self.length, self.length));

      return new BasicVector($self);
    }

    @Override
    public double[] toArray() {
        double[] result = new double[length];
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
    public byte[] toBinary() {
        int size = 1 +          // 1 byte: class tag
                   4 +          // 4 bytes: length
                  (8 * length); // 8 * length bytes: values

        ByteBuffer buffer = ByteBuffer.allocate(size);

        buffer.put(VECTOR_TAG);
        buffer.putInt(length);
        for (double value: self) {
            buffer.putDouble(value);
        }

        return buffer.array();
    }
}
