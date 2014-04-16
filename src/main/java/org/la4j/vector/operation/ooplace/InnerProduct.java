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
import org.la4j.vector.Vectors;
import org.la4j.vector.dense.BasicVector;
import org.la4j.vector.functor.VectorFunction;
import org.la4j.vector.operation.VectorVectorOperation;
import org.la4j.vector.sparse.CompressedSpecific;
import org.la4j.vector.sparse.CompressedVector;

public class InnerProduct extends VectorVectorOperation<Double> {

    @Override
    public Double apply(final CompressedVector a, final CompressedVector b) {
        CompressedSpecific aa = a.specific();
        CompressedSpecific bb = b.specific();

        double result = 0.0;

        int i = 0, j = 0;
        while (i < aa.cardinality() && j < bb.cardinality()) {
            int ii = aa.indices()[i];
            int jj = bb.indices()[j];

            if (ii == jj) {
                result += aa.values()[i++] * bb.values()[j++];
            } else if (ii < jj) {
                i++;
            } else {
                j++;
            }
        }

        return result;
    }

    @Override
    public Double apply(final CompressedVector a, final BasicVector b) {
        return a.foldNonZero(Vectors.asSumFunctionAccumulator(0.0, dot(b)));
    }

    @Override
    public Double apply(final BasicVector a, final BasicVector b) {
        return a.fold(Vectors.asSumFunctionAccumulator(0.0, dot(b)));
    }

    @Override
    public Double apply(final BasicVector a, final CompressedVector b) {
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
