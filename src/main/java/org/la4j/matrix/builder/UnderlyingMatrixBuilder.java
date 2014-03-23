/*
 * Copyright 2011-2014, by Vladimir Kostyukov and Contributors.
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
 * Contributor(s): -
 */

package org.la4j.matrix.builder;

import org.la4j.io.MatrixStream;
import org.la4j.matrix.Matrix;

import java.util.Random;

public abstract class UnderlyingMatrixBuilder implements MatrixBuilder {

    @Override
    public Matrix buildSymmetric() {
        return build();
    }

    @Override
    public Matrix buildIdentity() {
        return build();
    }

    @Override
    public Matrix buildDiagonal() {
        return build();
    }

    // shape
    public abstract Matrix build(int rows, int columns);
    public abstract Matrix buildIdentity(int rows, int columns);

    public Matrix buildSymmetric(int rows, int columns) {
        return build(rows, columns);
    }

    public Matrix buildDiagonal(int rows, int columns) {
        return build(rows, columns);
    }

    // value source (ignored w/o shape)
    public Matrix build(double value) {
        return build();
    }

    public Matrix buildSymmetric(double value) {
        return build();
    }

    public Matrix buildIdentity(double value) {
        return build();
    }

    public Matrix buildDiagonal(double value) {
        return build();
    }

    // 1D array source
    public abstract Matrix buildDiagonal(double array[]);

    public Matrix build(double array[]) {
        return build();
    }

    public Matrix buildSymmetric(double array[]) {
        return build();
    }

    public Matrix buildIdentity(double array[]) {
        return build();
    }

    // 2D array source
    public abstract Matrix build(double array[][]);
    public abstract Matrix buildSymmetric(double array[][]);
    public abstract Matrix buildDiagonal(double array[][]);
    public abstract Matrix buildIdentity(double array[][]);

    // random source (ignored w/o shape)
    public Matrix build(Random random) {
        return build();
    }

    public Matrix buildSymmetric(Random random) {
        return build();
    }

    public Matrix buildIdentity(Random random) {
        return build();
    }

    public Matrix buildDiagonal(Random random) {
        return build();
    }

    // stream source
    public abstract Matrix build(MatrixStream stream);
    public abstract Matrix buildSymmetric(MatrixStream stream);
    public abstract Matrix buildIdentity(MatrixStream stream);
    public abstract Matrix buildDiagonal(MatrixStream stream);

    // matrix source
    public abstract Matrix build(Matrix matrix);
    public abstract Matrix buildSymmetric(Matrix matrix);
    public abstract Matrix buildIdentity(Matrix matrix);
    public abstract Matrix buildDiagonal(Matrix matrix);

    // shape + value source
    public abstract Matrix build(int rows, int columns, double value);
    public abstract Matrix buildDiagonal(int rows, int columns, double value);

    public Matrix buildIdentity(int rows, int columns, double value) {
        return buildIdentity(rows, columns);
    }

    public Matrix buildSymmetric(int rows, int columns, double value) {
        return build(rows, columns, value);
    }

    // shape + 1D array source
    public abstract Matrix build(int rows, int columns, double array[]);
    public abstract Matrix buildSymmetric(int rows, int columns, double array[]);
    public abstract Matrix buildDiagonal(int rows, int columns, double array[]);

    public Matrix buildIdentity(int rows, int columns, double array[]) {
        return buildIdentity(rows, columns);
    }

    // shape + 2D array source
    public abstract Matrix build(int rows, int columns, double array[][]);
    public abstract Matrix buildSymmetric(int rows, int columns, double array[][]);
    public abstract Matrix buildDiagonal(int rows, int columns, double array[][]);

    public Matrix buildIdentity(int rows, int columns, double array[][]) {
        return buildIdentity(rows, columns);
    }

    // shape + random source
    public abstract Matrix build(int rows, int columns, Random random);
    public abstract Matrix buildSymmetric(int rows, int columns, Random random);
    public abstract Matrix buildDiagonal(int rows, int columns, Random random);

    public Matrix buildIdentity(int rows, int columns, Random random) {
        return buildIdentity(rows, columns);
    }

    // shape + stream source
    public abstract Matrix build(int rows, int columns, MatrixStream stream);
    public abstract Matrix buildSymmetric(int rows, int columns, MatrixStream stream);
    public abstract Matrix buildDiagonal(int rows, int columns, MatrixStream stream);

    public Matrix buildIdentity(int rows, int columns, MatrixStream stream) {
        return buildIdentity(rows, columns);
    }

    // shape + matrix source
    public abstract Matrix build(int rows, int columns, Matrix matrix);
    public abstract Matrix buildSymmetric(int rows, int columns, Matrix matrix);
    public abstract Matrix buildDiagonal(int rows, int columns, Matrix matrix);

    public Matrix buildIdentity(int rows, int columns, Matrix matrix) {
        return buildIdentity(rows, columns);
    }

    @Override
    public MatrixBuilder shape(int rows, int columns) {
        return new ShapedMatrixBuilder(this, rows, columns);
    }

    @Override
    public MatrixBuilder source(double value) {
        return new ConstantSourcedMatrixBuilder(this, value);
    }

    @Override
    public MatrixBuilder source(double[][] array) {
        return new Array2DSourcedMatrixBuilder(this, array);
    }

    @Override
    public MatrixBuilder source(double[] array) {
        return new Array1DSourcedMatrixBuilder(this, array);
    }

    @Override
    public MatrixBuilder source(Random random) {
        return new RandomSourcedMatrixBuilder(this, random);
    }

    @Override
    public MatrixBuilder source(MatrixStream stream) {
        return new StreamSourcedMatrixBuilder(this, stream);
    }

    @Override
    public MatrixBuilder source(Matrix matrix) {
        return new MatrixSourcedMatrixBuilder(this, matrix);
    }
}
