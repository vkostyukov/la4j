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
import org.la4j.vector.dense.BasicVector;

/**
 *
 * @author toddb
 */
class VectorReference extends AbstractVector {
    private final Vector src;
    private final int zero;
    
    public VectorReference(Vector srcVector) {
        super(srcVector.factory(), srcVector.length());
        src = srcVector;
        zero = 0;
    }

    public VectorReference(Vector srcVector, int zero_, int length) {
        super(srcVector.factory(), length);
        src = srcVector;
        zero = zero_;
    }

    @Override
    public double get(int i) {
        return src.get(zero + i);
    }

    @Override
    public void set(int i, double value) {
        src.set(zero + i, value);
    }

    @Override
    public Vector add(double value) {
        for (int i = 0; i < length; i++) {
            set(i, get(i) + value);
        }
        return this;
    }

    @Override
    public Vector add(Vector vector) {
        ensureArgumentIsNotNull(vector, "vector");

        if (length != vector.length()) {
            fail("Wrong vector length: " + vector.length() + ". Should be: " + length + ".");
        }

        for (int i = 0; i < length; i++) {
            set(i, get(i) + vector.get(i));
        }

        return this;
    }

    @Override
    public Vector multiply(double value) {
        for (int i = 0; i < length; i++) {
            set(i, get(i) * value);
        }

        return this;
    }

    @Override
    public Vector hadamardProduct(Vector vector) {
        for (int i = 0; i < length; i++) {
            set(i, get(i) * vector.get(i));
        }
        return this;
    }

    @Override
    public Vector multiply(Matrix matrix) {
        ensureArgumentIsNotNull(matrix, "matrix");

        if (length != matrix.rows()) {
            fail("Wrong matrix dimensions: " + matrix.rows() + "x" + matrix.columns() +
                 ". Should be: " + length + "x_.");
        }

        for (int j = 0; j < matrix.columns(); j++) {

            double acc = 0.0;

            for (int i = 0; i < matrix.rows(); i++) {
                acc += get(i) * matrix.get(i, j);
            }

            set(j, acc);
        }

        return this;
    }

    @Override
    public Vector subtract(Vector vector) {
        ensureArgumentIsNotNull(vector, "vector");

        if (length != vector.length()) {
            fail("Wrong vector length: " + vector.length() + ". Should be: " + length + ".");
        }

        for (int i = 0; i < length; i++) {
            set(i, get(i) - vector.get(i));
        }

        return this;
    }

    @Override
    public Vector slice(int from, int until) {
        return new VectorReference(this, from, until - from);
    }

    @Override
    public Vector sliceLeft(int until) {
        return new VectorReference(this, 0, until);
    }

    @Override
    public Vector sliceRight(int from) {
        return new VectorReference(this, from, length - from);
    }

    @Override
    public Vector safe() {
        return new BasicVector(this);
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
        src.readExternal(in);
    }
}
