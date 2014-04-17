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

import org.la4j.vector.Vector;
import org.la4j.vector.VectorIterator;
import org.la4j.vector.Vectors;
import org.la4j.vector.dense.DenseVector;
import org.la4j.vector.functor.VectorFunction;
import org.la4j.vector.operation.VectorVectorOperation;
import org.la4j.vector.sparse.SparseVector;

public class InnerProduct extends VectorVectorOperation<Double> {

    @Override
    public Double apply(final SparseVector a, final SparseVector b) {

        VectorIterator aa = a.everyNonZero();
        VectorIterator bb = b.everyNonZero();

        double result = 0.0;

        while (aa.hasNext() && bb.hasNext()) {
            aa.next();
            bb.next();

            while (aa.index() != bb.index()) {
                if (aa.hasNext() && aa.index() < bb.index()) {
                    aa.next();
                } else if (bb.hasNext() && bb.index() < aa.index()) {
                    bb.next();
                } else {
                    return result;
                }
            }

            result += aa.value() * bb.value();
        }

        return result;
    }

    @Override
    public Double apply(final SparseVector a, final DenseVector b) {
        return a.foldNonZero(Vectors.asSumFunctionAccumulator(0.0, dot(b)));
    }

    @Override
    public Double apply(final DenseVector a, final DenseVector b) {
        return a.fold(Vectors.asSumFunctionAccumulator(0.0, dot(b)));
    }

    @Override
    public Double apply(final DenseVector a, final SparseVector b) {
        return b.foldNonZero(Vectors.asSumFunctionAccumulator(0.0, dot(a)));
    }

    private VectorFunction dot(final Vector b) {
        return new VectorFunction() {
            @Override
            public double evaluate(int i, double value) {
                return b.get(i) * value;
            }
        };
    }
}
