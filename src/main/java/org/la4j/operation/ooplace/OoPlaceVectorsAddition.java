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

package org.la4j.operation.ooplace;

import org.la4j.operation.SymmetricVectorVectorOperation;
import org.la4j.vector.Vector;
import org.la4j.iterator.VectorIterator;
import org.la4j.vector.dense.DenseVector;
import org.la4j.vector.sparse.SparseVector;

public class OoPlaceVectorsAddition extends SymmetricVectorVectorOperation<Vector> {

    @Override
    public Vector apply(SparseVector a, SparseVector b) {
        VectorIterator these = a.nonZeroIterator();
        VectorIterator those = b.nonZeroIterator();
        VectorIterator both  = these.orElseAdd(those);
        Vector result = a.blank();

        while (both.hasNext()) {
            double x = both.next();
            int i = both.index();
            result.set(i, x);
        }

        return result;
    }

    @Override
    public Vector apply(DenseVector a, DenseVector b) {
        Vector result = a.blank();

        for (int i = 0; i < a.length(); i++) {
            result.set(i, a.get(i) + b.get(i));
        }

        return result;
    }

    @Override
    public Vector applySymmetric(DenseVector a, SparseVector b) {
        Vector result = a.copy();
        VectorIterator it = b.nonZeroIterator();

        while (it.hasNext()) {
            double x = it.next();
            int i = it.index();
            result.set(i, result.get(i) + x);
        }

        return result;
    }

    @Override
    public void ensureApplicableTo(Vector a, Vector b) {
        if (a.length() != b.length()) {
            throw new IllegalArgumentException(
                "Given vectors should have the same length: " +
                a.length() + " does not equal to " + b.length() + "."
            );
        }
    }
}
