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
import org.la4j.vector.Vectors;
import org.la4j.vector.dense.DenseVector;
import org.la4j.vector.functor.VectorFunction;
import org.la4j.vector.sparse.SparseVector;

public class OoPlaceInnerProduct extends SymmetricVectorVectorOperation<Double> {

    @Override
    public Double apply(final SparseVector a, final SparseVector b) {
        VectorIterator these = a.nonZeroIterator();
        VectorIterator those = b.nonZeroIterator();

        return these.innerProduct(those);
    }

    @Override
    public Double applySymmetric(DenseVector a, SparseVector b) {
        return b.foldNonZero(Vectors.asSumFunctionAccumulator(0.0, dot(a)));
    }

    @Override
    public Double apply(final DenseVector a, final DenseVector b) {
        double result = 0.0;

        for (int i = 0; i < a.length(); i++) {
            result += a.get(i) * b.get(i);
        }

        return result;
    }

    private VectorFunction dot(final Vector b) {
        return new VectorFunction() {
            @Override
            public double evaluate(int i, double value) {
                return b.get(i) * value;
            }
        };
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
