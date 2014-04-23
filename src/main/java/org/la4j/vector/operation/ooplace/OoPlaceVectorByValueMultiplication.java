package org.la4j.vector.operation.ooplace;

import org.la4j.factory.Factory;
import org.la4j.vector.Vector;
import org.la4j.iterator.VectorIterator;
import org.la4j.vector.dense.DenseVector;
import org.la4j.vector.operation.VectorOperation;
import org.la4j.vector.sparse.SparseVector;

public class OoPlaceVectorByValueMultiplication implements VectorOperation<Vector> {

    private double value;
    private Factory factory;

    public OoPlaceVectorByValueMultiplication(double value, Factory factory) {
        this.value = value;
        this.factory = factory;
    }

    @Override
    public Vector apply(SparseVector a) {
        Vector result = a.blank(factory);
        VectorIterator it = a.nonZeroIterator();
        while (it.hasNext()) {
            it.next();
            result.set(it.index(), it.value() * value);
        }
        return result;
    }

    @Override
    public Vector apply(DenseVector a) {
        Vector result = a.blank(factory);
        for (int i = 0; i < a.length(); i++) {
            result.set(i, a.get(i) * value);
        }
        return result;
    }
}
