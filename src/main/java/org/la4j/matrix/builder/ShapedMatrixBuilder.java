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

public class ShapedMatrixBuilder extends NonTerminalMatrixBuilder {

    private int rows;
    private int columns;

    public ShapedMatrixBuilder(UnderlyingMatrixBuilder underlying, int rows, int columns) {
        super(underlying);
        this.rows = rows;
        this.columns = columns;
    }

    @Override
    public Matrix build() {
        return underlying.build(rows, columns);
    }

    @Override
    public Matrix buildSymmetric() {
        return underlying.buildSymmetric(rows, columns);
    }

    @Override
    public Matrix buildIdentity() {
        return underlying.buildIdentity(rows, columns);
    }

    @Override
    public Matrix buildDiagonal() {
        return underlying.buildDiagonal(rows, columns);
    }

    @Override
    public Matrix buildDiagonal(MatrixStream stream) {
        return underlying.buildDiagonal(rows, columns, stream);
    }

    @Override
    public Matrix buildIdentity(MatrixStream stream) {
        return underlying.buildIdentity(rows, columns, stream);
    }

    @Override
    public Matrix buildSymmetric(MatrixStream stream) {
        return underlying.buildSymmetric(rows, columns, stream);
    }

    @Override
    public Matrix build(MatrixStream stream) {
        return underlying.build(rows, columns, stream);
    }

    @Override
    public Matrix buildDiagonal(Random random) {
        return underlying.buildDiagonal(rows, columns, random);
    }

    @Override
    public Matrix buildIdentity(Random random) {
        return underlying.buildIdentity(rows, columns, random);
    }

    @Override
    public Matrix buildSymmetric(Random random) {
        return underlying.buildSymmetric(rows, columns, random);
    }

    @Override
    public Matrix build(Random random) {
        return underlying.build(rows, columns, random);
    }

    @Override
    public Matrix buildDiagonal(double[][] array) {
        return underlying.buildDiagonal(rows, columns, array);
    }

    @Override
    public Matrix buildIdentity(double[][] array) {
        return underlying.buildIdentity(rows, columns, array);
    }

    @Override
    public Matrix buildSymmetric(double[][] array) {
        return underlying.buildSymmetric(rows, columns, array);
    }

    @Override
    public Matrix build(double[][] array) {
        return underlying.build(rows, columns, array);
    }

    @Override
    public Matrix buildDiagonal(double[] array) {
        return underlying.buildDiagonal(rows, columns, array);
    }

    @Override
    public Matrix buildIdentity(double[] array) {
        return underlying.buildIdentity(rows, columns, array);
    }

    @Override
    public Matrix buildSymmetric(double[] array) {
        return underlying.buildSymmetric(rows, columns, array);
    }

    @Override
    public Matrix build(double[] array) {
        return underlying.build(rows, columns, array);
    }

    @Override
    public Matrix buildDiagonal(double value) {
        return underlying.buildDiagonal(rows, columns, value);
    }

    @Override
    public Matrix buildIdentity(double value) {
        return underlying.buildIdentity(rows, columns, value);
    }

    @Override
    public Matrix buildSymmetric(double value) {
        return underlying.buildSymmetric(rows, columns, value);
    }

    @Override
    public Matrix build(double value) {
        return underlying.build(rows, columns, value);
    }
}
