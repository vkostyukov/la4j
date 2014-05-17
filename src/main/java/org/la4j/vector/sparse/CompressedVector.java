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
 * Contributor(s): Ewald Grusk
 *                 Yuriy Drozd
 *                 Maxim Samoylov
 * 
 */

package org.la4j.vector.sparse;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.la4j.io.VectorToBurningIterator;
import org.la4j.vector.Vector;
import org.la4j.io.VectorIterator;
import org.la4j.vector.Vectors;
import org.la4j.vector.functor.VectorFunction;
import org.la4j.vector.functor.VectorProcedure;
import org.la4j.vector.source.VectorSource;

public class CompressedVector extends SparseVector {

    private static final long serialVersionUID = 4071505L;

    private static final int MINIMUM_SIZE = 32;

    private double values[];
    private int indices[];

    public CompressedVector() {
        this(0);
    }

    public CompressedVector(int length) {
        this(length, 0);
    }

    public CompressedVector(Vector vector) {
        this(Vectors.asVectorSource(vector));
    }

    public CompressedVector(double array[]) {
        this(Vectors.asArraySource(array));
    }

    public CompressedVector(VectorSource source) {
        this(source.length(), 0);

        for (int i = 0; i < length; i++) {
            double value = source.get(i);
            //if (Math.abs(value) > Vectors.EPS || value < 0.0) {
            if (value != 0.0) {

                if (values.length < cardinality + 1) {
                    growup();
                }

                values[cardinality] = value;
                indices[cardinality] = i;
                cardinality++;
            }
        }
    }

    public CompressedVector(int length, int capacity) {
        super(length, 0);
        int alignedSize = align(length, capacity);
        this.values = new double[alignedSize];
        this.indices = new int[alignedSize];
    }

    public CompressedVector(int length, int cardinality, double values[], int indices[]) {
        super(length, cardinality);
        this.values = values;
        this.indices = indices;
    }

    @Override
    public double getOrElse(int i, double defaultValue) {

        int k = searchForIndex(i);

        if (k < cardinality && indices[k] == i) {
            return values[k];
        }

        return defaultValue;
    }

    @Override
    public void set(int i, double value) {

        int k = searchForIndex(i);

        if (k < cardinality && indices[k] == i) {
            // if (Math.abs(value) > Vectors.EPS || value < 0.0) {
            if (value != 0.0) {
                values[k] = value;
            } else {
                remove(k);
            }
        } else {
            insert(k, i, value);
        }
    }

    @Override
    public void swap(int i, int j) {

        if (i == j) {
            return;
        }

        int ii = searchForIndex(i);
        int jj = searchForIndex(j);

        boolean iiNotZero = ii < cardinality && i == indices[ii];
        boolean jjNotZero = jj < cardinality && j == indices[jj];

        if (iiNotZero && jjNotZero) {

            double sd = values[ii];
            values[ii] = values[jj];
            values[jj] = sd;

        } else {

            double notZero = values[iiNotZero ? ii : jj];

            int leftIndex = (ii < jj) ? ii : jj;
            int rightIndex = (ii > jj) ? ii : jj;

            if (((iiNotZero && (leftIndex == ii)) 
                 || (jjNotZero && (leftIndex == jj))) && (ii != jj)) {

                System.arraycopy(values, leftIndex + 1, values, leftIndex, 
                        cardinality - leftIndex);
                System.arraycopy(values, rightIndex - 1, values, rightIndex, 
                        cardinality - rightIndex);

                values[rightIndex - 1] = notZero;

                System.arraycopy(indices, leftIndex + 1, indices, leftIndex, 
                        cardinality - leftIndex);
                System.arraycopy(indices, rightIndex - 1, indices, rightIndex, 
                        cardinality - rightIndex);

                indices[rightIndex -1] = jjNotZero ? i : j;

            } else if((iiNotZero && (rightIndex == ii)) 
                      || (jjNotZero && (rightIndex == jj))) {

                System.arraycopy(values, rightIndex + 1, values, rightIndex, 
                        cardinality - rightIndex);
                System.arraycopy(values, leftIndex, values, leftIndex + 1, 
                        cardinality - leftIndex);

                values[leftIndex] = notZero;

                System.arraycopy(indices, rightIndex + 1, indices, rightIndex, 
                        cardinality - rightIndex);
                System.arraycopy(indices, leftIndex, indices, leftIndex + 1, 
                        cardinality - leftIndex);

                indices[leftIndex] = jjNotZero ? i : j;
            }
        }
    }

    @Override
    public Vector resize(int length) {
        ensureLengthIsCorrect(length);

        int $cardinality = (length > this.length) ?
                           cardinality : searchForIndex(length);

        double $values[] = new double[align(length, $cardinality)];
        int $indices[] = new int[align(length, $cardinality)];

        System.arraycopy(values, 0, $values, 0, $cardinality);
        System.arraycopy(indices, 0, $indices, 0, $cardinality);

        return new CompressedVector(length, $cardinality, $values, $indices);
    }

    @Override
    public void each(VectorProcedure procedure) {
        int k = 0;
        for (int i = 0; i < length; i++) {
            if (k < cardinality && indices[k] == i) {
                procedure.apply(i, values[k++]);
            } else {
                procedure.apply(i, 0.0);
            }
        }
    }

    @Override
    public void eachNonZero(VectorProcedure procedure) {
        for (int i = 0; i < cardinality; i++) {
            procedure.apply(indices[i], values[i]);
        }
    }

    @Override
    public void update(int i, VectorFunction function) {

        int k = searchForIndex(i);

        if (k < cardinality && indices[k] == i) {

            double value = function.evaluate(i, values[k]); 

            // if (Math.abs(value) > Vectors.EPS || value < 0.0) {
            if (value != 0.0) {
                values[k] = value;
            } else {
                remove(k);
            }
        } else {
            insert(k, i, function.evaluate(i, 0.0));
        }
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {

        out.writeInt(length);
        out.writeInt(cardinality);

        for (int i = 0; i < cardinality; i++) {
            out.writeInt(indices[i]);
            out.writeDouble(values[i]);
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {

        length = in.readInt();
        cardinality = in.readInt();

        int alignedSize = align(length, cardinality);

        values = new double[alignedSize];
        indices = new int[alignedSize];

        for (int i = 0; i < cardinality; i++) {
            indices[i] = in.readInt();
            values[i] = in.readDouble();
        }
    }

    @Override
    public boolean nonZeroAt(int i) {
        int k = searchForIndex(i);
        return k < cardinality && indices[k] == i;
    }

    private int searchForIndex(int i) {

        // TODO: add the same check for CRS/CCS matrices
        if (cardinality == 0 || i > indices[cardinality - 1]) {
            return cardinality;
        }

        int left = 0;
        int right = cardinality;

        while (left < right) {
            int p = (left + right) / 2;
            if (indices[p] > i) {
                right = p;
            } else if (indices[p] < i) {
                left = p + 1;
            } else {
                return p;
            }
        }

        return left;
    }

    private void insert(int k, int i, double value) {

        // if (Math.abs(value) < Vectors.EPS && value >= 0.0) {
        if (value == 0.0) {
            return;
        }

        if (values.length < cardinality + 1) {
            growup();
        }

        // TODO: revise other system.arraycopy() calls
        if (cardinality - k > 0) {
            System.arraycopy(values, k, values, k + 1, cardinality - k);
            System.arraycopy(indices, k, indices, k + 1, cardinality - k);
        }

//        for (int kk = cardinality; kk > k; kk--) {
//            values[kk] = values[kk - 1];
//            indices[kk] = indices[kk - 1];
//        }

        values[k] = value;
        indices[k] = i;

        cardinality++;
    }

    private void remove(int k) {
        // TODO: https://github.com/vkostyukov/la4j/issues/87
        cardinality--;

        System.arraycopy(values, k + 1, values, k, cardinality - k);
        System.arraycopy(indices, k + 1, indices, k, cardinality - k);

//        for (int kk = k; kk < cardinality; kk++) {
//            values[kk] = values[kk + 1];
//            indices[kk] = indices[kk + 1];
//        }
    }

    // TODO: better name
    private void growup() {

        if (values.length == length) {
            // This should never happen
            throw new IllegalStateException("This vector can't grow up.");
        }

        int capacity = Math.min(length, (cardinality * 3) / 2 + 1);

        double $values[] = new double[capacity];
        int $indices[] = new int[capacity];

        System.arraycopy(values, 0, $values, 0, cardinality);
        System.arraycopy(indices, 0, $indices, 0, cardinality);

        values = $values;
        indices = $indices;
    }

    private int align(int length, int capacity) {
        if (capacity < 0) {
            fail("Cardinality should be positive: " + capacity + ".");
        }
        if (capacity > length) {
            fail("Cardinality should be less then or equal to capacity: " + capacity + ".");
        }
        return Math.min(length, ((capacity / MINIMUM_SIZE) + 1) * MINIMUM_SIZE);
    }

    @Override
    public VectorIterator nonZeroIterator() {
        return new VectorIterator(length) {
            private boolean currentIsRemoved = false;
            private int k = -1;
            private int removedIndex = -1;

            @Override
            public int index() {
                return currentIsRemoved ? removedIndex : indices[k];
            }

            @Override
            public double get() {
                return currentIsRemoved ? 0.0 : values[k];
            }

            @Override
            public void set(double value) {
                if (value == 0.0 && !currentIsRemoved) {
                    currentIsRemoved = true;
                    removedIndex = indices[k];
                    CompressedVector.this.remove(k--);
                } else if (value != 0.0 && !currentIsRemoved) {
                    values[k] = value;
                } else {
                    currentIsRemoved = false;
                    CompressedVector.this.insert(++k, removedIndex, value);
                }
            }

            @Override
            public boolean hasNext() {
                return k + 1 < cardinality;
            }

            @Override
            public Double next() {
                currentIsRemoved = false;
                return values[++k];
            }

            @Override
            protected int innerCursor() {
                return k;
            }
        };
    }

    @Override
    public VectorIterator iterator() {
        return new VectorIterator(length) {
            private int k = 0;
            private int i = -1;

            @Override
            public int index() {
                return i;
            }

            @Override
            public double get() {
                if (k < cardinality && indices[k] == i) {
                    return values[k];
                }
                return 0.0;
            }

            @Override
            public void set(double value) {
                if (k < cardinality && indices[k] == i) {
                    if (value == 0.0) {
                        CompressedVector.this.remove(k);
                    } else {
                        values[k] = value;
                    }
                } else {
                    CompressedVector.this.insert(k, i, value);
                }
            }

            @Override
            public boolean hasNext() {
                return i + 1 < length;
            }

            @Override
            public Double next() {
                i++;
                if (k < cardinality && indices[k] == i - 1) {
                    k++;
                }
                return get();
            }

            @Override
            protected int innerCursor() {
                return k;
            }
        };
    }
}
