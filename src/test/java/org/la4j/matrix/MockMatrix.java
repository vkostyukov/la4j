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
 * Contributor(s): Julia Kostyukova
 * 
 */

package org.la4j.matrix;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class MockMatrix extends AbstractMatrix implements Matrix {

    private final static double MOCK_EPS = 1e-3;
    private Matrix self;

    public MockMatrix(Matrix matrix) {
        super(matrix.factory(), matrix.rows(), matrix.columns());
        this.self = matrix;
    }

    @Override
    public double get(int i, int j) {
         return self.get(i, j);
    }

    @Override
    public void set(int i, int j, double value) {
        self.set(i, j, value);
    }

    @Override
    public boolean equals(Object object) {

        if (this == object)
            return true;
        if (object == null)
            return false;

        if (!(object instanceof Matrix)) {
            return false;
        }

        Matrix matrix = (Matrix) object;

        if (rows != matrix.rows() || columns != matrix.columns()) {
            return false;
        }

        boolean result = true;

        for (int i = 0; result && i < rows; i++) {
            for (int j = 0; result && j < columns; j++) {
                double a = get(i, j);
                double b = matrix.get(i, j);

                double diff = Math.abs(a - b);

                result = (a == b) || (diff < MOCK_EPS || diff / Math.max(Math.abs(a), Math.abs(b)) < MOCK_EPS);
            }
        }

        return result;
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
