/*
 * Copyright 2011-2013, by Vladimir Kostyukov and Contributors.
 * 
 * This file is part of la4j project (http://la4j.org)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * Contributor(s): Jakob Moellers
 * 
 */

package org.la4j.matrix;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.la4j.decomposition.MatrixDecompositor;
import org.la4j.factory.Factory;
import org.la4j.inversion.MatrixInvertor;
import org.la4j.matrix.functor.MatrixAccumulator;
import org.la4j.matrix.functor.MatrixFunction;
import org.la4j.matrix.functor.MatrixPredicate;
import org.la4j.matrix.functor.MatrixProcedure;
import org.la4j.vector.Vector;

public abstract class AbstractSafeMatrix implements Matrix {

    protected Matrix self;
    protected Factory factory;

    protected AbstractSafeMatrix(Matrix matrix) {
        this.self = matrix;
        this.factory = matrix.factory().safe();
    }

    @Override
    public double get(int i, int j) {
        ensureIndexInRows(i);
        ensureIndexInColumns(j);

        return self.get(i, j);
    }

    @Override
    public void set(int i, int j, double value) {
        ensureIndexInRows(i);
        ensureIndexInColumns(j);

        self.set(i, j, value);
    }

    @Override
    public void assign(double value) {
        self.assign(value);
    }

    @Override
    public void swapRows(int i, int j) {
        ensureIndexInRows(i);
        ensureIndexInRows(j);

        self.swapRows(i, j);
    }

    @Override
    public void swapColumns(int i, int j) {
        ensureIndexInColumns(i);
        ensureIndexInColumns(j);

        self.swapColumns(i, j);
    }

    @Override
    public int rows() {
        return self.rows();
    }

    @Override
    public int columns() {
        return self.columns();
    }

    @Override
    public Matrix transpose() {
        return self.transpose(factory);
    }
    
    @Override
    public Matrix shuffle() {
        return self.shuffle(factory);
    }

    @Override
    public Matrix shuffle(Factory factory) {
        return self.shuffle(factory);
    }

    @Override
    public Matrix rotate() {
        return self.rotate(factory);
    }

    @Override
    public Matrix rotate(Factory factory) {
        return self.rotate(factory);
    }

    @Override
    public Matrix transpose(Factory factory) {
        return self.transpose(factory);
    }

    @Override
    public Matrix multiply(double value) {
        return self.multiply(value, factory);
    }

    @Override
    public Matrix multiply(double value, Factory factory) {
        return self.multiply(value, factory);
    }

    @Override
    public Vector multiply(Vector vector) {
        return self.multiply(vector, factory);
    }

    @Override
    public Vector multiply(Vector vector, Factory factory) {
        return self.multiply(vector, factory);
    }

    @Override
    public Matrix multiply(Matrix matrix) {
        return self.multiply(matrix, factory);
    }

    @Override
    public Matrix multiply(Matrix matrix, Factory factory) {
        return self.multiply(matrix, factory);
    }

    @Override
    public Matrix subtract(double value) {
        return self.subtract(value, factory);
    }

    @Override
    public Matrix subtract(double value, Factory factory) {
        return self.subtract(value, factory);
    }

    @Override
    public Matrix subtract(Matrix matrix) {
        return self.subtract(matrix, factory);
    }

    @Override
    public Matrix subtract(Matrix matrix, Factory factory) {
        return self.subtract(matrix, factory);
    }

    @Override
    public Matrix add(double value) {
        return self.add(value, factory);
    }

    @Override
    public Matrix add(double value, Factory factory) {
        return self.add(value, factory);
    }

    @Override
    public Matrix add(Matrix matrix) {
        return self.add(matrix, factory);
    }

    @Override
    public Matrix add(Matrix matrix, Factory factory) {
        return self.add(matrix, factory);
    }

    @Override
    public Matrix divide(double value) {
        return self.divide(value, factory);
    }

    @Override
    public Matrix divide(double value, Factory factory) {
        return self.divide(value, factory);
    }

    @Override
    public Matrix kronecker(Matrix matrix) {
        return self.kronecker(matrix, factory);
    }

    @Override
    public Matrix kronecker(Matrix matrix, Factory factory) {
        return self.kronecker(matrix, factory);
    }

    @Override
    public double trace() {
        return self.trace();
    }

    @Override
    public double product() {
        return self.product();
    }

    @Override
    public double determinant() {
        return self.determinant();
    }

    @Override
    public int rank() {
        return self.rank();
    }

    @Override
    public Vector getRow(int i) {
        ensureIndexInRows(i);

        return self.getRow(i, factory);
    }

    @Override
    public Vector getRow(int i, Factory factory) {
        ensureIndexInRows(i);

        return self.getRow(i, factory);
    }

    @Override
    public Vector getColumn(int j) {
        ensureIndexInColumns(j);

        return self.getColumn(j, factory);
    }

    @Override
    public Vector getColumn(int j, Factory factory) {
        ensureIndexInColumns(j);

        return self.getColumn(j, factory);
    }

    @Override
    public void setRow(int i, Vector row) {
        ensureIndexInRows(i);

        self.setRow(i, row);
    }

    @Override
    public void setColumn(int j, Vector column) {
        ensureIndexInColumns(j);

        self.setColumn(j, column);
    }

    @Override
    public Matrix triangularize() {
        return self.triangularize(factory);
    }

    @Override
    public Matrix triangularize(Factory factory) {
        return self.triangularize(factory);
    }

    @Override
    public Matrix[] decompose(MatrixDecompositor decompositor) {
        return self.decompose(decompositor, factory);
    }

    @Override
    public Matrix[] decompose(MatrixDecompositor decompositor, Factory factory) {
        return self.decompose(decompositor, factory);
    }

    @Override
    public Matrix inverse(MatrixInvertor invertor) {
        return self.inverse(invertor, factory);
    }

    @Override
    public Matrix inverse(MatrixInvertor invertor, Factory factory) {
        return self.inverse(invertor, factory);
    }

    @Override
    public Matrix blank() {
        return self.blank(factory);
    }

    @Override
    public Matrix blank(Factory factory) {
        return self.blank(factory);
    }

    @Override
    public Matrix copy() {
        return self.copy(factory);
    }

    @Override
    public Matrix copy(Factory factory) {
        return self.copy(factory);
    }

    @Override
    public Matrix resize(int rows, int columns) {
        return self.resize(rows, columns, factory);
    }

    @Override
    public Matrix resize(int rows, int columns, Factory factory) {
        return self.resize(rows, columns, factory);
    }

    @Override
    public Matrix resizeRows(int rows) {
        return self.resizeRows(rows, factory);
    }

    @Override
    public Matrix resizeRows(int rows, Factory factory) {
        return self.resizeRows(rows, factory);
    }

    @Override
    public Matrix resizeColumns(int columns) {
        return self.resizeColumns(columns, factory);
    }

    @Override
    public Matrix resizeColumns(int columns, Factory factory) {
        return self.resizeColumns(columns, factory);
    }

    @Override
    public Matrix slice(int fromRow, int fromColumn, int untilRow, 
            int untilColumn) {

        return self.slice(fromRow, fromColumn, untilRow, untilColumn, factory);
    }

    @Override
    public Matrix slice(int fromRow, int fromColumn, int untilRow,
            int untilColumn, Factory factory) {

        return self.slice(fromRow, fromColumn, untilRow, untilColumn, factory);
    }

    @Override
    public Matrix sliceTopLeft(int untilRow, int untilColumn) {
        return self.sliceTopLeft(untilRow, untilColumn, factory);
    }

    @Override
    public Matrix sliceTopLeft(int untilRow, int untilColumn, Factory factory) {
        return self.sliceTopLeft(untilRow, untilColumn, factory);
    }

    @Override
    public Matrix sliceBottomRight(int fromRow, int fromColumn) {
        return self.sliceBottomRight(fromRow, fromColumn, factory);
    }

    @Override
    public Matrix sliceBottomRight(int fromRow, int fromColumn, Factory fac) {
        return self.sliceBottomRight(fromRow, fromColumn, factory);
    }

    @Override
    public Factory factory() {
        return factory;
    }

    @Override
    public void each(MatrixProcedure procedure) {
        self.each(procedure);
    }

    @Override
    public Matrix transform(MatrixFunction function) {
        return self.transform(function, factory);
    }

    @Override
    public Matrix transform(MatrixFunction function, Factory factory) {
        return self.transform(function, factory);
    }

    @Override
    public Matrix transform(int i, int j, MatrixFunction function) {
        ensureIndexInRows(i);
        ensureIndexInColumns(j);

        return self.transform(i, j, function, factory);
    }

    @Override
    public Matrix transform(int i, int j, MatrixFunction function,
            Factory factory) {

        ensureIndexInRows(i);
        ensureIndexInColumns(j);

        return self.transform(i, j, function, factory);
    }

    @Override
    public void update(MatrixFunction function) {
        self.update(function);
    }

    @Override
    public void update(int i, int j, MatrixFunction function) {
        ensureIndexInRows(i);
        ensureIndexInColumns(j);

        self.update(i, j, function);
    }

    @Override
    public double fold(MatrixAccumulator accumulator) {
        return self.fold(accumulator);
    }

    @Override
    public double foldRow(int i, MatrixAccumulator accumulator) {
        ensureIndexInRows(i);

        return self.foldRow(i, accumulator);
    }

    @Override
    public double foldColumn(int j, MatrixAccumulator accumulator) {
        ensureIndexInColumns(j);

        return self.foldColumn(j, accumulator);
    }

    @Override
    public boolean is(MatrixPredicate predidate) {
        return self.is(predidate);
    }

    @Override
    public Matrix safe() {
        return this;
    }

    @Override
    public Matrix unsafe() {
        return self;
    }

    @Override
    public boolean equals(Object obj) {
        return self.equals(obj);
    }

    @Override
    public int hashCode() {
        return self.hashCode(); 
    }

    @Override
    public String toString() {
        return self.toString();
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {

        self.readExternal(in);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        self.writeExternal(out);
    }

    protected void ensureIndexInRows(int i) {
        if (i >= self.rows() || i < 0) {
            throw new IllegalArgumentException("Row index out of bounds: " + i);
        }
    }

    protected void ensureIndexInColumns(int i) {
        if (i >= self.columns() || i < 0) {
            throw new IllegalArgumentException("Column index out of bounds: " 
                                               + i);
        }
    }
}
