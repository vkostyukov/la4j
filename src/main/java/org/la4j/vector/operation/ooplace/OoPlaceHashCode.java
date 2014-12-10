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

package org.la4j.vector.operation.ooplace;

import org.la4j.iterator.VectorIterator;
import org.la4j.vector.dense.DenseVector;
import org.la4j.vector.operation.VectorOperation;
import org.la4j.vector.sparse.SparseVector;

public class OoPlaceHashCode implements VectorOperation<Integer> {

    @Override
    public Integer apply(SparseVector a) {
        int result = 17;
        VectorIterator it = a.nonZeroIterator();

        while (it.hasNext()) {
            it.next();
            long value = (long) it.get();
            long index = (long) it.index();
            result = 37 * result + (int) (value ^ (value >>> 32));
            result = 37 * result + (int) (index ^ (index >>> 32));
        }

        return result;
    }

    @Override
    public Integer apply(DenseVector a) {
        int result = 17;

        for (int i = 0; i < a.length(); i++) {
            long value = (long) a.get(i);
            result = 37 * result + (int) (value ^ (value >>> 32));
        }

        return result;
    }
}
