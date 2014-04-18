package org.la4j.vector.operation.ooplace;

import org.la4j.factory.Factory;
import org.la4j.vector.Vector;
import org.la4j.vector.VectorIterator;
import org.la4j.vector.Vectors;
import org.la4j.vector.dense.DenseVector;
import org.la4j.vector.functor.VectorFunction;
import org.la4j.vector.operation.VectorVectorOperation;
import org.la4j.vector.sparse.SparseVector;

public class Addition extends VectorVectorOperation<Vector> {

    private Factory factory;

    public Addition(Factory factory) {
        this.factory = factory;
    }

    @Override
    public Vector apply(SparseVector a, SparseVector b) {

        Vector result = factory.createVector(a.length());

        VectorIterator ait = a.nonZeroIterator();
        VectorIterator bit = b.nonZeroIterator();

        //VectorIterator cit = ait.and(bit, function); // intersection
        //VectorIterator cit = ait.or(bit, function);  // union

        boolean takenFromA = true;
        boolean takenFromB = true;

        while ((!takenFromA || ait.hasNext()) && (!takenFromB || bit.hasNext())) {
            if (takenFromA) {
                ait.next();
            }
            if (takenFromB) {
                bit.next();
            }

            if (ait.index() < bit.index()) {
                result.set(ait.index(), ait.value());
                takenFromA = true;
                takenFromB = false;
            } else if (bit.index() < ait.index()) {
                result.set(bit.index(), bit.value());
                takenFromB = true;
                takenFromA = false;
            } else {
                result.set(ait.index(), ait.value() + bit.value());
                takenFromB = true;
                takenFromA = true;
            }
        }

        if (takenFromA && !takenFromB) {
            result.set(bit.index(), bit.value());
        } else if (!takenFromA) {
            result.set(ait.index(), ait.value());
        }

        while (ait.hasNext()) {
            ait.next();
            result.set(ait.index(), ait.value());
        }

        while (bit.hasNext()) {
            bit.next();
            result.set(bit.index(), bit.value());
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
