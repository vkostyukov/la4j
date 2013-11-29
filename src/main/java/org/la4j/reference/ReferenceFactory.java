/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.la4j.reference;

import org.la4j.factory.Basic2DFactory;
import org.la4j.factory.Factory;
import org.la4j.matrix.Matrix;
import org.la4j.vector.Vector;

/**
 *
 * @author toddb
 */
public class ReferenceFactory extends Basic2DFactory {
    private final Matrix reference;
    
    ReferenceFactory(Matrix reference_) {
        reference = reference_;
    }
    
    @Override
    public Matrix createMatrix() {
        return reference;
    }

    @Override
    public Matrix createMatrix(Matrix matrix) {
        return new MatrixReference(matrix);
    }

    @Override
    public Vector createVector(Vector vector) {
        return new VectorReference(vector);
    }

    @Override
    public Factory unsafe() {
        return this;
    }
    
}
