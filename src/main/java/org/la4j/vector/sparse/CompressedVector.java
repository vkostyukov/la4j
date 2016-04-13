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

import java.nio.ByteBuffer;
import java.util.*;

import org.la4j.Vectors;
import org.la4j.iterator.VectorIterator;
import org.la4j.Vector;
import org.la4j.vector.SparseVector;
import org.la4j.vector.VectorFactory;
import org.la4j.vector.functor.VectorFunction;
import org.la4j.vector.functor.VectorProcedure;

/**
 * A basic sparse vector implementation using underlying value and index arrays.
 * 
 * A sparse data structure does not store blank elements, and instead just stores
 * elements with values. A sparse data structure can be initialized with a large
 * length but take up no storage until the space is filled with non-zero elements.
 * 
 * However, there is a performance cost. Fetch/store operations take O(log n)
 * time instead of the O(1) time of a dense data structure.
 * 
 * {@code CompressedVector} stores the underlying data in a two arrays: A values
 * array and a indices array. The values array matches the indices array, and
 * they're both sorted by the indices array. To get a value at an index, the
 * index is found in the indices array with a binary search and the respective
 * value is obtained from the values array.
 * 
 */
public class CompressedVector extends SparseVector {

    private static final byte VECTOR_TAG = (byte) 0x10;

    private static final int MINIMUM_SIZE = 32;

    private double[] values;
    private int[] indices;

    public CompressedVector() {
        this(0);
    }

    public CompressedVector(int length) {
        this(length, 0);
    }

    public CompressedVector(int length, int capacity) {
        super(length);
        int alignedSize = align(length, capacity);
        this.values = new double[alignedSize];
        this.indices = new int[alignedSize];
    }

    public CompressedVector(int length, int cardinality, double[] values, int[] indices) {
        super(length, cardinality);
        this.values = values;
        this.indices = indices;
    }

    /**
     * Creates a zero {@link CompressedVector} of the given {@code length}.
     */
    public static CompressedVector zero(int length) {
        return new CompressedVector(length);
    }

    /**
     * Creates a zero {@link CompressedVector} of the given {@code length} with
     * the given {@code capacity}.
     */
    public static CompressedVector zero(int length, int capacity) {
        return new CompressedVector(length, capacity);
    }

    /**
     * Creates a random {@link CompressedVector} of the given {@code length} with
     * the given {@code density} and {@code Random}.
     */
    public static CompressedVector random(int length, double density, Random random) {
        if (density < 0.0 || density > 1.0) {
            throw new IllegalArgumentException("The density value should be between 0 and 1.0");
        }

        int cardinality = (int) (length * density);
        double[] values = new double[cardinality];
        int[] indices = new int[cardinality];

        for (int i = 0; i < cardinality; i++) {
            values[i] = random.nextDouble();
            indices[i] = random.nextInt(length);
        }

        Arrays.sort(indices);

        return new CompressedVector(length, cardinality, values, indices);
    }

    /**
     * Creates a new {@link CompressedVector} from the given {@code array} with
     * compressing (copying) the underlying array.
     */
    public static CompressedVector fromArray(double[] array) {
        int length  = array.length;
        CompressedVector result = CompressedVector.zero(length);

        for (int i = 0; i < length; i++) {
            if (array[i] != 0.0) {
                result.set(i, array[i]);
            }
        }

        return result;
    }

    /**
     * Decodes {@link CompressedVector} from the given byte {@code array}.
     *
     * @param array the byte array representing a vector
     *
     * @return a decoded vector
     */
    public static CompressedVector fromBinary(byte[] array) {
        ByteBuffer buffer = ByteBuffer.wrap(array);

        if (buffer.get() != VECTOR_TAG) {
            throw new IllegalArgumentException("Can not decode CompressedVector from the given byte array.");
        }

        int length = buffer.getInt();
        int cardinality = buffer.getInt();
        double[] values = new double[cardinality];
        int[] indices = new int[cardinality];

        for (int i = 0; i < cardinality; i++) {
            indices[i] = buffer.getInt();
            values[i] = buffer.getDouble();
        }

        return new CompressedVector(length, cardinality, values, indices);
    }

    /**
     * Parses {@link CompressedVector} from the given CSV string.
     *
     * @param csv the CSV string representing a vector
     *
     * @return a parsed vector
     */
    public static CompressedVector fromCSV(String csv) {
        return Vector.fromCSV(csv).to(Vectors.COMPRESSED);
    }

    /**
     * Parses {@link CompressedVector} from the given Matrix Market string.
     *
     * @param mm the string in Matrix Market format
     *
     * @return a parsed vector
     */
    public static CompressedVector fromMatrixMarket(String mm) {
        return Vector.fromMatrixMarket(mm).to(Vectors.COMPRESSED);
    }

    /**
     * Creates new {@link CompressedVector} from collection
     *
     * @param list value list
     *
     * @return created vector
     */
    public static CompressedVector fromCollection(Collection<? extends Number> list) {
        return Vector.fromCollection(list).to(Vectors.COMPRESSED);
    }

    /**
     * Creates new {@link CompressedVector} from index-value map
     *
     * @param map index-value map
     *
     * @param length vector length
     *
     * @return created vector
     */
    public static CompressedVector fromMap(Map<Integer, ? extends Number> map, int length) {
        //TODO goto lambdas
        int cardinality = map.size();
        int[] indices = new int[cardinality];
        double[] values = new double[cardinality];
        int i = 0;
        for (Map.Entry<Integer, ? extends Number> entry : map.entrySet()) {
            int index = entry.getKey();
            if (index < 0 || index >= length) {
                throw new IllegalArgumentException("Check your map: Index must be 0..n-1");
            }
            indices[i] = index;
            values[i] = entry.getValue().doubleValue();
            i++;
        }
        return new CompressedVector(length, cardinality, values, indices);
    }

    @Override
    public double getOrElse(int i, double defaultValue) {
        ensureIndexIsInBounds(i);
        int k = searchForIndex(i);

        if (k < cardinality && indices[k] == i) {
            return values[k];
        }

        return defaultValue;
    }

    @Override
    public void set(int i, double value) {
        ensureIndexIsInBounds(i);
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
    public void setAll(double value) {
        if (value == 0.0) {
            cardinality = 0;
        } else {
            if (values.length < length) {
                values = new double[length];
                indices = new int[length];
            }

            for (int i = 0; i < length; i++) {
                indices[i] = i;
                values[i] = value;
            }

            cardinality = length;
        }
    }

    @Override
    public void swapElements(int i, int j) {
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
    public Vector copyOfLength(int length) {
        ensureLengthIsCorrect(length);

        int $cardinality = (length >= this.length) ?
            cardinality : searchForIndex(length);
        int capacity = align(length, $cardinality);

        double[] $values = new double[capacity];
        int[] $indices = new int[capacity];

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
    public void updateAt(int i, VectorFunction function) {
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
    public boolean nonZeroAt(int i) {
        int k = searchForIndex(i);
        return k < cardinality && indices[k] == i;
    }

    @Override
    public <T extends Vector> T to(VectorFactory<T> factory) {
        if (factory.outputClass == CompressedVector.class) {
            return factory.outputClass.cast(this);
        }

        return super.to(factory);
    }

    @Override
    public Vector blankOfLength(int length) {
        return CompressedVector.zero(length);
    }

    @Override
    public byte[] toBinary() {
        int size = 1 +                // 1 byte: class tag
                   4 +                // 4 bytes: length
                   4 +                // 4 bytes: cardinality
                  (8 * cardinality) + // 8 * cardinality bytes: values
                  (8 * cardinality);  // 8 * cardinality bytes: indices

        ByteBuffer buffer = ByteBuffer.allocate(size);

        buffer.put(VECTOR_TAG);
        buffer.putInt(length);
        buffer.putInt(cardinality);

        for (int i = 0; i < cardinality; i++) {
            buffer.putInt(indices[i]);
            buffer.putDouble(values[i]);
        }

        return buffer.array();
    }

    /**
     * Does the binary searching to find the position in the value array given
     * it's index.
     * 
     * @param i the index to search for
     * @return the position in the value array
     */
    private int searchForIndex(int i) {
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
            growUp();
        }

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

        if (cardinality - k > 0) {
            System.arraycopy(values, k + 1, values, k, cardinality - k);
            System.arraycopy(indices, k + 1, indices, k, cardinality - k);
        }

//        for (int kk = k; kk < cardinality; kk++) {
//            values[kk] = values[kk + 1];
//            indices[kk] = indices[kk + 1];
//        }
    }

    private void growUp() {
        if (values.length == length) {
            // This should never happen
            throw new IllegalStateException("This vector can't grow up.");
        }

        int capacity = Math.min(length, (cardinality * 3) / 2 + 1);

        double[] $values = new double[capacity];
        int[] $indices = new int[capacity];

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
                if(!hasNext()) {
                    throw new NoSuchElementException();
                }
                currentIsRemoved = false;
                return values[++k];
            }
        };
    }

    @Override
    public VectorIterator iterator() {
        return new VectorIterator(length) {
            private int k = 0;
            private int i = -1;
            private boolean currentNonZero = false;

            @Override
            public int index() {
                return i;
            }

            @Override
            public double get() {
                return currentNonZero ? values[k] : 0.0;
            }

            @Override
            public void set(double value) {
                if (currentNonZero) {
                    if (value == 0.0) {
                        CompressedVector.this.remove(k);
                        currentNonZero = false;
                    } else {
                        values[k] = value;
                    }
                } else {
                    CompressedVector.this.insert(k, i, value);
                    currentNonZero = true;
                }
            }

            @Override
            public boolean hasNext() {
                return i + 1 < length;
            }

            @Override
            public Double next() {
                if(!hasNext()) {
                    throw new NoSuchElementException();
                }
                if (currentNonZero) {
                    k++;
                }

                i++;
                currentNonZero = k < cardinality && indices[k] == i;

                return get();
            }
        };
    }
}
