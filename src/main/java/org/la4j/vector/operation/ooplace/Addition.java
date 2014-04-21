package org.la4j.vector.operation.ooplace;

import org.la4j.factory.Factory;
import org.la4j.vector.Vector;
import org.la4j.vector.VectorIterator;
import org.la4j.vector.Vectors;
import org.la4j.vector.dense.DenseVector;
import org.la4j.vector.operation.VectorVectorOperation;
import org.la4j.vector.sparse.SparseVector;

public class Addition extends VectorVectorOperation<Vector> {

    private Factory factory;

    public Addition(Factory factory) {
        this.factory = factory;
    }

    @Override
    public Vector apply(SparseVector a, SparseVector b) {
        VectorIterator these = a.nonZeroIterator();
        VectorIterator those = b.nonZeroIterator();

        Vector result = factory.createVector(a.length());

        // Accumulator is used for accumulating the contented values
        // (values with the same index).
        // TODO: revise accumulator performance
        VectorIterator both  = these.or(those, Vectors.asSumAccumulator(0.0));

        while (both.hasNext()) {
            both.next();
            result.set(both.index(), both.value());
        }

        return result;
    }

    @Override
    public Vector apply(SparseVector a, DenseVector b) {
        return apply(b, a);
    }

    @Override
    public Vector apply(DenseVector a, DenseVector b) {
        Vector result = factory.createVector(a.length());
        for (int i = 0; i < a.length(); i++) {
            result.set(i, a.get(i) + b.get(i));
        }
        return result;
    }

    @Override
    public Vector apply(DenseVector a, SparseVector b) {
        Vector result = a.copy(factory);
        VectorIterator it = b.nonZeroIterator();
        while (it.hasNext()) {
            it.next();
            result.update(it.index(), Vectors.asPlusFunction(it.value()));
        }
        return result;
    }
}
