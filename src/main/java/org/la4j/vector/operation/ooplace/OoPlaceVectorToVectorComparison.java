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

import org.la4j.iterator.JoinFunction;
import org.la4j.iterator.VectorIterator;
import org.la4j.vector.Vector;
import org.la4j.vector.dense.DenseVector;
import org.la4j.vector.operation.VectorVectorOperation;
import org.la4j.vector.sparse.SparseVector;

public class OoPlaceVectorToVectorComparison extends VectorVectorOperation<Boolean> {

    private final double precision;

    public OoPlaceVectorToVectorComparison(double precision) {
        this.precision = precision;
    }

    @Override
    public Boolean apply(SparseVector a, SparseVector b) {
        if (a == b) {
            return true;
        }

        if (!isSimilar(a, b)) {
            return false;
        }

        VectorIterator these = a.nonZeroIterator();
        VectorIterator those = b.nonZeroIterator();
        VectorIterator both = these.orElse(those, new JoinFunction() {
            @Override
            public double apply(double a, double b) {
                return isSimilar(a, b) ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY;
            }
        });

        boolean result = true;

        while (result && both.hasNext()) {
            both.next();
            result = both.get() > 0.0;
        }

        return result;
    }

    @Override
    public Boolean apply(SparseVector a, DenseVector b) {
        return apply(b, a);
    }

    @Override
    public Boolean apply(DenseVector a, DenseVector b) {
        if (a == b) {
            return true;
        }

        if (!isSimilar(a, b)) {
            return false;
        }

        boolean result = true;

        for (int i = 0; result && i < a.length(); i++) {
            result = isSimilar(a.get(i), b.get(i));
        }

        return result;
    }

    @Override
    public Boolean apply(DenseVector a, SparseVector b) {
        if (!isSimilar(a, b)) {
            return false;
        }

        VectorIterator it = b.iterator();
        boolean result = true;

        while (result && it.hasNext()) {
            it.next();
            result = isSimilar(it.get(), a.get(it.index()));
        }

        return result;
    }

    private boolean isSimilar(Vector a, Vector b) {
        return a.length() == b.length();
    }

    private boolean isSimilar(double a, double b) {
        double diff = Math.abs(a - b);
        return  (a == b) ||
                (diff < precision || diff / Math.max(Math.abs(a), Math.abs(b)) < precision);
    }
}
