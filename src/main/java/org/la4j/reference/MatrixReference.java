/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.la4j.reference;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import org.la4j.LinearAlgebra;
import org.la4j.decomposition.MatrixDecompositor;
import org.la4j.factory.Factory;
import org.la4j.inversion.MatrixInverter;
import org.la4j.linear.LinearSystemSolver;
import org.la4j.matrix.AbstractMatrix;
import org.la4j.matrix.Matrix;
import org.la4j.matrix.dense.AbstractBasicMatrix;
import org.la4j.matrix.dense.Basic2DMatrix;
import org.la4j.matrix.functor.AdvancedMatrixPredicate;
import org.la4j.matrix.functor.MatrixAccumulator;
import org.la4j.matrix.functor.MatrixFunction;
import org.la4j.matrix.functor.MatrixPredicate;
import org.la4j.matrix.functor.MatrixProcedure;
import org.la4j.matrix.source.MatrixSource;
import org.la4j.vector.Vector;

/**
 *
 * @author toddb
 */
public class MatrixReference extends AbstractBasicMatrix implements MatrixSource {

    private final int row, col;
    private final Matrix mSrc;
    
    public MatrixReference(Matrix src) {
        super(new ReferenceFactory(src), src.rows(), src.columns());
        mSrc = src;
        row = col = 0;
    }

    public MatrixReference(Matrix src,
                           int fromRow, int fromCol,
                           int untilRow, int untilColumn) {
        super(new ReferenceFactory(src), untilRow - fromRow, untilColumn - fromCol);
        mSrc = src;
        row = fromRow;
        col = fromCol;
    }

    @Override
    public double get(int i, int j) {
        return mSrc.get(row + i, col + j);
    }

    @Override
    public void set(int i, int j, double value) {
        mSrc.set(row + i, col + j, value);
    }

    @Override
    public double[][] toArray() {
        Basic2DMatrix m = new Basic2DMatrix((MatrixSource)this);
        return m.toArray();
    }

    @Override
    public Vector getRow(int i) {
        return new VectorOfMatrixReference(this, i, VectorReferenceType.byRow);
    }

    @Override
    public Vector getColumn(int j) {
        return new VectorOfMatrixReference(this, j, VectorReferenceType.byColumn);
    }

    @Override
    public Vector toRowVector() {
        return new VectorOfMatrixReference(this, VectorReferenceType.byRow);
    }

    @Override
    public Vector toColumnVector() {
        return new VectorOfMatrixReference(this, VectorReferenceType.byRow);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {

        out.writeInt(rows);
        out.writeInt(columns);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                out.writeDouble(get(i, j));
            }
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {

        rows = in.readInt();
        columns = in.readInt();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                set(i, j, in.readDouble());
            }
        }
    }

    @Override
    public Matrix power(int n) {
        if (n < 0) {
            fail("The exponent should be positive: " + n + ".");
        }
        
        while (n > 0) {
            if (n % 2 == 1) {
                multiply(this);
            }

            n /= 2;
            multiply(this);
        }
        return this;
    }

    @Override
    public Matrix multiply(double value) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                set(i, j, get(i, j) * value);
            }
        }

        return this;
    }

    @Override
    public Matrix subtract(Matrix matrix) {
        ensureArgumentIsNotNull(matrix, "matrix");

        if (rows != matrix.rows() || columns != matrix.columns()) {
            fail("Wrong matrix dimensions: " + matrix.rows() + "x" + matrix.columns() +
                    ". Should be: " + rows + "x" + columns + ".");
        }

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                set(i, j, get(i, j) - matrix.get(i, j));
            }
        }

        return this;
    }

    @Override
    public Matrix add(double value) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                set(i, j, get(i, j) + value);
            }
        }

        return this;
    }

    @Override
    public Matrix add(Matrix matrix) {
        ensureArgumentIsNotNull(matrix, "matrix");

        if (rows != matrix.rows() || columns != matrix.columns()) {
            fail("Wrong matrix dimensions: " + matrix.rows() + "x" + matrix.columns() +
                 ". Should be: " + rows + "x" + columns + ".");
        }

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                set(i, j, get(i, j) + matrix.get(i, j));
            }
        }

        return this;
    }

    @Override
    public Matrix hadamardProduct(Matrix matrix) {
        ensureArgumentIsNotNull(matrix, "matrix");

        if ((columns != matrix.columns()) || (rows != matrix.rows())) {
            fail("Wrong matrix dimensions: " + matrix.rows() + "x" + matrix.columns() +
                 ". Should be: " + rows + "x" + columns + ".");
        }

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                set(i, j, matrix.get(i, j) * get(i, j));
            }
        }
        
        return this;
    }

    @Override
    public Matrix slice(int fromRow, int fromColumn,
                        int untilRow, int untilColumn) {
        return new MatrixReference(this, fromRow, fromColumn,
                                         untilRow, untilColumn);
    }

    @Override
    public Matrix sliceTopLeft(int untilRow, int untilColumn) {
        return new MatrixReference(this, 0, 0, untilRow, untilColumn);
    }

    @Override
    public Matrix sliceBottomRight(int fromRow, int fromColumn) {
        return new MatrixReference(this, fromRow, fromColumn, row, columns);
    }

    @Override
    public Matrix transform(MatrixFunction function) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                set(i, j, function.evaluate(i, j, get(i, j)));
            }
        }

        return this;
    }

    @Override
    public Matrix transform(int i, int j, MatrixFunction function) {
        set(i, j, function.evaluate(i, j, get(i, j)));
        return this;
    }
}
