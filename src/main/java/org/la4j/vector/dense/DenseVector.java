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
import org.la4j.io.VectorIterator;
import org.la4j.io.VectorToBurningIterator;
import org.la4j.vector.AbstractVector;
import org.la4j.vector.Vector;
import org.la4j.vector.operation.VectorOperation;
import org.la4j.vector.operation.VectorVectorOperation;

public abstract class DenseVector extends AbstractVector {

    public DenseVector(int length) {
        super(LinearAlgebra.DENSE_FACTORY, length);
    }

    @Override
    public <T> T pipeTo(VectorOperation<T> operation) {
        return operation.apply(this);
    }

    @Override
    public <T> T pipeTo(VectorVectorOperation<T> operation, Vector that) {
        return that.pipeTo(operation.curry(this));
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

        for (int i = 0; i < length(); i++) {
            result.set(i, get(i) * value);
        }

        return result;
    }

    @Override
    public void multiplyInPlace(double value) {
        // TODO: multiply by 0 = clear()
        for (int i = 0; i < length; i++) {
            set(i, get(i) * value);
        }
    }

    @Override
    public VectorIterator burningIterator() {
        return new VectorToBurningIterator(iterator()) {
            @Override
            public void flush() {
                // fast flush
                for (int i = innerCursor() + 1; i < length; i++) {
                    DenseVector.this.set(i, 0.0);
                }
            }
        };
    }
}
