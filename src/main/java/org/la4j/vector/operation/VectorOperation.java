package org.la4j.vector.operation;

import org.la4j.vector.dense.BasicVector;
import org.la4j.vector.sparse.CompressedVector;

public interface VectorOperation<T> {
    T apply(final CompressedVector a);
    T apply(final BasicVector a);
}
