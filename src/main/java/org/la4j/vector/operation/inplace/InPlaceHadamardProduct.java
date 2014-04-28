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

package org.la4j.vector.operation.inplace;

import org.la4j.iterator.VectorIterator;
import org.la4j.vector.VectorSink;
import org.la4j.vector.dense.DenseVector;
import org.la4j.vector.operation.VectorVectorOperation;
import org.la4j.vector.sparse.SparseVector;

public class InPlaceHadamardProduct extends VectorVectorOperation<Void> {
    @Override
    public Void apply(SparseVector a, SparseVector b) {
        VectorIterator these = a.nonZeroIterator();
        VectorIterator those = b.nonZeroIterator();
        VectorIterator both = these.andAlsoMultiply(those);

        VectorSink recorder = a.sink();
        while (both.hasNext()) {
            both.next();
            recorder.set(both.index(), both.get());
        }
        recorder.flush();

        return null;
    }

    @Override
    public Void apply(SparseVector a, DenseVector b) {
        VectorIterator it = a.nonZeroIterator();

        VectorSink rec = a.sink();
        while (it.hasNext()) {
            it.next();
            rec.set(it.index(), it.get() * b.get(it.index()));
        }
        rec.flush();

        return null;
    }

    @Override
    public Void apply(DenseVector a, DenseVector b) {
        for (int i = 0; i < a.length(); i++) {
            a.set(i, a.get(i) * b.get(i));
        }
        return null;
    }

    @Override
    public Void apply(DenseVector a, SparseVector b) {
        VectorIterator it = b.nonZeroIterator();
        VectorSink recorder = a.sink();

        while (it.hasNext()) {
            it.next();
            recorder.set(it.index(), a.get(it.index()) * it.get());
        }
        return null;
    }
}
