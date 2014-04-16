package org.la4j.vector.operation;

import org.la4j.vector.Vector;
import org.la4j.vector.dense.BasicVector;
import org.la4j.vector.sparse.CompressedVector;

public abstract class CommonVectorVectorOperation<T> extends VectorVectorOperation<T> {
    @Override
    public T apply(final CompressedVector a, final CompressedVector b) {
        return applyCommon(a, b);
    }

    @Override
    public T apply(final CompressedVector a, final BasicVector b) {
        return applyCommon(a, b);
    }

    @Override
    public T apply(final BasicVector a, final BasicVector b) {
        return applyCommon(a, b);
    }

    @Override
    public T apply(final BasicVector a, final CompressedVector b) {
        return applyCommon(a, b);
    }

    public abstract T applyCommon(final Vector a, final Vector b);
}
