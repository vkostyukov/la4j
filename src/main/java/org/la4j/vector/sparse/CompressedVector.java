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

package org.la4j.vector.sparse;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.la4j.factory.CRSFactory;
import org.la4j.vector.AbstractVector;
import org.la4j.vector.Vector;
import org.la4j.vector.Vectors;
import org.la4j.vector.functor.VectorProcedure;
import org.la4j.vector.source.ArrayVectorSource;
import org.la4j.vector.source.UnsafeVectorSource;
import org.la4j.vector.source.VectorSource;

public class CompressedVector extends AbstractVector implements SparseVector {

    private static final long serialVersionUID = 4071505L;

    private static final int MINIMUM_SIZE = 32;

    private double values[];
    private int indices[];

    private int cardinality;

    public CompressedVector() {
        this(0);
    }

    public CompressedVector(int length) {
        this(length, 0);
    }

    public CompressedVector(Vector vector) {
        this(new UnsafeVectorSource(vector));
    }

    public CompressedVector(double array[]) {
        this(new ArrayVectorSource(array));
    }

    public CompressedVector(VectorSource source) {
        this(source.length(), 0);

        for (int i = 0; i < length; i++) {
            double value = source.get(i);
            if (Math.abs(value) > Vectors.EPS) {

                if (values.length <= cardinality) {
                    growup();
                }

                values[cardinality] = value;
                indices[cardinality] = i;
                cardinality++;
            }
        }
    }

    public CompressedVector(int length, int cardinality) {
        this(length, cardinality, new double[align(length, cardinality)],
             new int[align(length, cardinality)]);
    }

    public CompressedVector(int length, int cardinality, double values[],
                            int indices[]) {

        super(new CRSFactory(), length);

        this.cardinality = cardinality;

        this.values = values;
        this.indices = indices;
    }

    @Override
    public double get(int i) {

        for (int k = 0; k < cardinality; k++) {
            if (indices[k] == i) {
                return values[k];
            }
        }

        return 0.0;
    }

    @Override
    public void set(int i, double value) {

        for (int k = 0; k < cardinality; k++) {
            if (indices[k] == i) {
                if (Math.abs(value) > Vectors.EPS) {
                    values[k] = value;
                    return;
                } else {
                    cardinality--;
                    for (int kk = k; kk < cardinality; kk++) {
                        values[kk] = values[kk + 1];
                        indices[kk] = indices[kk + 1];
                    }
                }
            }
        }

        if (Math.abs(value) < Vectors.EPS) {
            return;
        }

        if (values.length <= cardinality + 1) {
            growup();
        }

        values[cardinality] = value;
        indices[cardinality] = i;
        cardinality++;
    }

    @Override
    public int cardinality() {
        return cardinality;
    }

    @Override
    public double density() {
        return cardinality / length;
    }

    @Override
    public void resize(int length) {

        if (length < 0) {
            throw new IllegalArgumentException("Wrong dimension: " + length);
        }

        if (length == this.length) {
            return;
        }

        if (length < this.length) {
            for (int i = 0; i < cardinality; i++) {
                if (indices[i] > length) {
                    cardinality--;
                }
            }
        }

        this.length = length;
    }

    @Override
    public void swap(int i, int j) {

        if (i == j) {
            return;
        }

        int ii = -1, jj = -1;

        for (int k = 0; (ii == -1 || jj == -1) && k < cardinality; k++) {

            if (ii == -1 && indices[k] == i) {
                ii = k;
            }

            if (jj == -1 && indices[k] == j) {
                jj = k;
            }
        }

        if (ii == -1 && jj == -1) {
            return;
        }

        if (ii == -1) {
            indices[jj] = i;
            return;
        }

        if (jj == -1) {
            indices[ii] = j;
            return;
        }

        int s = indices[jj];
        indices[jj] = indices[ii];
        indices[ii] = s;
    }

    @Override
    public void each(VectorProcedure procedure) {
        for (int i = 0; i < cardinality; i++) {
            procedure.apply(indices[i], values[i]);
        }
    }

    @Override
    public Vector safe() {
        return new SparseSafeVector(this);
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

    private void growup() {

        int newSize = Math.min(length, (cardinality * 3) / 2 + 1);

        double newValues[] = new double[newSize];
        int newIndices[] = new int[newSize];

        System.arraycopy(values, 0, newValues, 0, cardinality);
        System.arraycopy(indices, 0, newIndices, 0, cardinality);

        this.values = newValues;
        this.indices = newIndices;
    }

    private static int align(int length, int cardinality) {
        return Math.min(length, ((cardinality % MINIMUM_SIZE) + 1)
                        * MINIMUM_SIZE);
    }
}
