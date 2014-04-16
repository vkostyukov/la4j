package org.la4j.vector.operation.ooplace;

import org.la4j.vector.Vector;
import org.la4j.vector.Vectors;
import org.la4j.vector.dense.BasicVector;
import org.la4j.vector.functor.VectorFunction;
import org.la4j.vector.operation.VectorVectorOperation;
import org.la4j.vector.sparse.CompressedSpecific;
import org.la4j.vector.sparse.CompressedVector;

public class InnerProductVectorOperation extends VectorVectorOperation<Double> {

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
