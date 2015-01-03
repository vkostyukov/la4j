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

package org.la4j.operation;

import org.la4j.vector.Vector;
import org.la4j.vector.dense.DenseVector;
import org.la4j.vector.sparse.SparseVector;

public abstract class VectorVectorOperation<R> {

    public abstract R apply(final SparseVector a, final SparseVector b);
    public abstract R apply(final SparseVector a, final DenseVector b);
    public abstract R apply(final DenseVector a, final DenseVector b);
    public abstract R apply(final DenseVector a, final SparseVector b);

    public void ensureApplicableTo(final Vector a, final Vector b) { }

    public VectorOperation<R> partiallyApply(final SparseVector a) {
        return new VectorOperation<R>() {
            @Override
            public R apply(final SparseVector b) {
                return VectorVectorOperation.this.apply(a, b);
            }

            @Override
            public R apply(final DenseVector b) {
                return VectorVectorOperation.this.apply(a, b);
            }

            @Override
            public void ensureApplicableTo(final Vector b) {
                VectorVectorOperation.this.ensureApplicableTo(a, b);
            }
        };
    }

    public VectorOperation<R> partiallyApply(final DenseVector a) {
        return new VectorOperation<R>() {
            @Override
            public R apply(final SparseVector b) {
                return VectorVectorOperation.this.apply(a, b);
            }

            @Override
            public R apply(final DenseVector b) {
                return VectorVectorOperation.this.apply(a, b);
            }

            @Override
            public void ensureApplicableTo(final Vector b) {
                VectorVectorOperation.this.ensureApplicableTo(a, b);
            }
        };
    }
}
