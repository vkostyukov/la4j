/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.la4j.reference;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import org.la4j.matrix.Matrix;
import org.la4j.vector.AbstractVector;
import org.la4j.vector.Vector;
import org.la4j.vector.dense.DenseSafeVector;
import org.la4j.vector.dense.DenseVector;

/**
 *
 * @author toddb
 */
public class VectorOfMatrixReference extends AbstractVector implements DenseVector {
    private final Matrix src;
    private final VectorReferenceType type;
    private final int index;
    private final Deferencer dereferencer;
    
    private interface Deferencer {
        public double get(int i);
        public void set(int i, double value);
    }
    
    private class DereferenceByRow implements Deferencer {
        @Override public double get(int i)              { return src.get(index, i); }
        @Override public void set(int i, double value)  { src.set(index, i, value); }
    }
    
    private class DereferenceByColumn implements Deferencer {
        @Override public double get(int i)              { return src.get(i, index); }
        @Override public void set(int i, double value)  { src.set(i, index, value); }
    }
    
    private class DereferenceAllRows implements Deferencer {
        @Override public double get(int i)              { return src.get(i/length, i%length); }
        @Override public void set(int i, double value)  { src.set(i/length, i%length, value); }
    }
    
    private class DereferenceAllColumns implements Deferencer {
        @Override public double get(int i)              { return src.get(i%length, i/length); }
        @Override public void set(int i, double value)  { src.set(i%length, i/length, value); }
    }
    
    public VectorOfMatrixReference(Matrix matrix, int dimensionIndex, VectorReferenceType referenceType) {
        super(new ReferenceFactory(matrix),
              referenceType == VectorReferenceType.byRow ? matrix.columns() : matrix.rows());
        src = matrix;
        type = referenceType;
        index = dimensionIndex;
        switch (referenceType) {
        case byRow:     dereferencer = new DereferenceByRow(); break;
        case byColumn:  dereferencer = new DereferenceByColumn(); break;
        default:        throw new AssertionError(referenceType.name());
        }
    }
    
    public VectorOfMatrixReference(Matrix matrix, VectorReferenceType referenceType) {
        super(new ReferenceFactory(matrix), matrix.columns() * matrix.rows());
        src = matrix;
        type = referenceType;
        index = -1;
        switch (referenceType) {
        case byRow:     dereferencer = new DereferenceAllRows(); break;
        case byColumn:  dereferencer = new DereferenceAllColumns(); break;
        default:        throw new AssertionError(referenceType.name());
        }
    }
    
    @Override
    public double get(int i) {
        return dereferencer.get(i);
    }

    @Override
    public void set(int i, double value) {
        dereferencer.set(i, value);
    }

    @Override
    public Vector safe() {
        return new DenseSafeVector(this);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {

        out.writeInt(length);

        for (int i = 0; i < length; i++) {
            out.writeDouble(get(i));
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {

        length = in.readInt();

        for (int i = 0; i < length; i++) {
            set(i, in.readDouble());
        }
    }

    @Override
    public double[] toArray() {
        double result[] = new double[length];
        for (int i = 0; i < length; i++) {
            result[i] = get(i);
        }
        return result;
    }
}
