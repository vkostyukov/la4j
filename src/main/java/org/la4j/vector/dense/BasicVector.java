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
import org.la4j.io.VectorIterator;
import org.la4j.vector.Vector;
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

    private double self[];

    public BasicVector() {
        this(0);
    }

    public BasicVector(Vector vector) {
        this(Vectors.asVectorSource(vector));
    }

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
    public void swap(int i, int j) {
        if (i != j) {
            double d = self[i];
            self[i] = self[j];
            self[j] = d;
        }
    }

    @Override
    public Vector copy() {
        return resize(length);
    }

    @Override
    public Vector resize(int length) {
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
                return self[i];
            }

            @Override
            public void set(double value) {
                self[i] = value;
            }

            @Override
            public boolean hasNext() {
                return i + 1 < length;
            }

            @Override
            public Double next() {
                return self[++i];
            }
        };
    }
}
