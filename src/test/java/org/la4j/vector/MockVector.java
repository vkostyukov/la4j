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

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import org.la4j.io.VectorIterator;
import org.la4j.vector.dense.DenseVector;

/**
 * A dense vector.
 * 
 * A dense data structure stores data in an underlying array. Even zero elements
 * take up memory space. If you want a data structure that will not have zero
 * elements take up memory space, try a sparse structure.
 * 
 * However, fetch/store operations on dense data structures only take O(1) time,
 * instead of the O(log n) time on sparse structures.
 * 
 * This class is used for testing.
 */
public class MockVector extends DenseVector {

    private final static double MOCK_EPS = 1e-3;
    private final Vector self;

    public MockVector(Vector vector) {
        super(vector.length());
        this.self = vector;
    }

    @Override
    public double get(int i) {
        return self.get(i);
    }

    @Override
    public void set(int i, double value) {
        self.set(i, value);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null) {
            return false;
        }

        if (!(object instanceof Vector)) {
            return false;
        }

        Vector vector = (Vector) object;

        if (length != vector.length()) {
            return false;
        }

        boolean result = true;

        for (int i = 0; result && i < length; i++) {
            double a = get(i);
            double b = vector.get(i);

            double diff = Math.abs(a - b);

            result = (a == b) || (diff < MOCK_EPS || diff / Math.max(Math.abs(a), Math.abs(b)) < MOCK_EPS);
        }

        return result;
    }

    @Override
    public double[] toArray() {
        return null;
    }

    @Override
    public VectorIterator iterator() {
        return null;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {

        self.readExternal(in);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        self.writeExternal(out);
    }
}
