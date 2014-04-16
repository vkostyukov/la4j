package org.la4j.vector.operation;

import org.la4j.vector.dense.BasicVector;
import org.la4j.vector.sparse.CompressedVector;

public abstract class VectorVectorOperation<T> {

    public abstract T apply(final CompressedVector a, final CompressedVector b);
    public abstract T apply(final CompressedVector a, final BasicVector b);
    public abstract T apply(final BasicVector a, final BasicVector b);
    public abstract T apply(final BasicVector a, final CompressedVector b);

    public VectorOperation<T> curry(final CompressedVector a) {
        return new VectorOperation<T>() {
            @Override
            public T apply(CompressedVector b) {
                return VectorVectorOperation.this.apply(a, b);
            }

            @Override
            public T apply(BasicVector b) {
                return VectorVectorOperation.this.apply(a, b);
            }
        };
    }

    public VectorOperation<T> curry(final BasicVector a) {
        return new VectorOperation<T>() {
            @Override
            public T apply(CompressedVector b) {
                return VectorVectorOperation.this.apply(a, b);
            }

            @Override
            public T apply(BasicVector b) {
                return VectorVectorOperation.this.apply(a, b);
            }
        };
    }
}
