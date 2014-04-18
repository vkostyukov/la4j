package org.la4j.vector.operation.inplace;

import org.la4j.vector.VectorIterator;
import org.la4j.vector.Vectors;
import org.la4j.vector.dense.DenseVector;
import org.la4j.vector.operation.VectorVectorOperation;
import org.la4j.vector.sparse.SparseVector;

public class Addition extends VectorVectorOperation<Void> {
    @Override
    public Void apply(SparseVector a, SparseVector b) {
        VectorIterator it = b.nonZeroIterator();
        while (it.hasNext()) {
            it.next();
            a.update(it.index(), Vectors.asPlusFunction(it.value()));
        }
        return null;
    }

    @Override
    public Void apply(SparseVector a, DenseVector b) {
        for (int i = 0; i < b.length(); i++) {
            a.update(i, Vectors.asPlusFunction(b.get(i)));
        }
        return null;
    }

    @Override
    public Void apply(DenseVector a, DenseVector b) {
        for (int i = 0; i < a.length(); i++) {
            a.set(i, a.get(i) + b.get(i));
        }
        return null;
    }

    @Override
    public Void apply(DenseVector a, SparseVector b) {
        VectorIterator it = b.nonZeroIterator();
        while (it.hasNext()) {
            it.next();
            a.set(it.index(), a.get(it.index()) + it.value());
        }
        return null;
    }
}
