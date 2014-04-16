package org.la4j.vector.operation;

import org.la4j.vector.Vector;
import org.la4j.vector.dense.BasicVector;
import org.la4j.vector.sparse.CompressedVector;

public abstract class CommonVectorOperation<T> implements VectorOperation<T> {
    @Override
    public T apply(final CompressedVector a) {
        return applyCommon(a);
    }

    @Override
    public T apply(final BasicVector a) {
        return applyCommon(a);
    }

    abstract T applyCommon(final Vector a);
}
