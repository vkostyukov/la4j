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

import org.la4j.factory.Factory;
import org.la4j.io.MatrixStream;
import org.la4j.matrix.Matrices;
import org.la4j.matrix.Matrix;
import org.la4j.matrix.source.MatrixSource;

import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

public class TerminalMatrixBuilder extends UnderlyingMatrixBuilder {

    private Factory factory;

    public TerminalMatrixBuilder(Factory factory) {
        this.factory = factory;
    }

    @Override
    public Matrix build() {
        return factory.createMatrix();
    }

    @Override
    public Matrix build(int rows, int columns) {
        return factory.createMatrix(rows, columns);
    }

    @Override
    public Matrix buildIdentity(int rows, int columns) {
        return factory.createIdentityMatrix(Math.min(rows, columns));
    }

    @Override
    public Matrix buildDiagonal(double[] array) {
        return factory.createDiagonalMatrix(array);
    }

    @Override
    public Matrix build(double[][] array) {
        return factory.createMatrix(array);
    }

    @Override
    public Matrix buildSymmetric(double[][] array) {
        return symmetrized(Matrices.asArray2DSource(array));
    }

    @Override
    public Matrix buildDiagonal(double[][] array) {
        return diagonalized(Matrices.asArray2DSource(array));
    }

    @Override
    public Matrix buildIdentity(double[][] array) {
        return buildIdentity(array.length, array[0].length);
    }

    @Override
    public Matrix build(MatrixStream stream) {
        return streamedOrEmpty(stream);
    }

    @Override
    public Matrix buildSymmetric(MatrixStream stream) {
        return symmetrized(Matrices.asMatrixSource(streamedOrEmpty(stream)));
    }

    @Override
    public Matrix buildIdentity(MatrixStream stream) {
        Matrix source = streamedOrEmpty(stream);
        int size = Math.min(source.rows(), source.columns());
        return buildIdentity(size, size);
    }

    @Override
    public Matrix buildDiagonal(MatrixStream stream) {
        Matrix source = streamedOrEmpty(stream);
        return diagonalized(Matrices.asMatrixSource(source));
    }

    @Override
    public Matrix build(int rows, int columns, double value) {
        return factory.createConstantMatrix(rows, columns, value);
    }

    @Override
    public Matrix buildDiagonal(int rows, int columns, double value) {
        int size = Math.min(rows, columns);
        double diagonal[] = new double[size];
        Arrays.fill(diagonal, value);
        return buildDiagonal(diagonal);
    }

    @Override
    public Matrix build(int rows, int columns, double[] array) {
        return factory.createMatrix(rows, columns, array);
    }

    @Override
    public Matrix buildSymmetric(int rows, int columns, double[] array) {
        return symmetrized(Matrices.asArray1DSource(rows, columns, array));
    }

    @Override
    public Matrix buildDiagonal(int rows, int columns, double[] array) {
        return diagonalized(Matrices.asArray1DSource(rows, columns, array));
    }

    @Override
    public Matrix build(int rows, int columns, double[][] array) {
        return factory.createMatrix(array).resize(rows, columns);
    }

    @Override
    public Matrix buildSymmetric(int rows, int columns, double[][] array) {
        int size = Math.min(rows, columns);
        return buildSymmetric(array).resize(size, size);
    }

    @Override
    public Matrix buildDiagonal(int rows, int columns, double[][] array) {
        int size = Math.min(rows, columns);
        return buildDiagonal(array).resize(size, size);
    }

    @Override
    public Matrix build(int rows, int columns, Random random) {
        return factory.createRandomMatrix(rows, columns, random);
    }

    @Override
    public Matrix buildSymmetric(int rows, int columns, Random random) {
        return factory.createRandomSymmetricMatrix(Math.min(rows, columns), random);
    }

    @Override
    public Matrix buildDiagonal(int rows, int columns, Random random) {
        return diagonalized(Matrices.asRandomSource(rows, columns, random));
    }

    @Override
    public Matrix build(int rows, int columns, MatrixStream stream) {
        return build(stream).resize(rows, columns);
    }

    @Override
    public Matrix buildSymmetric(int rows, int columns, MatrixStream stream) {
        int size = Math.min(rows, columns);
        return buildSymmetric(stream).resize(size, size);
    }

    @Override
    public Matrix buildDiagonal(int rows, int columns, MatrixStream stream) {
        int size = Math.min(rows, columns);
        return buildDiagonal(stream).resize(size, size);
    }

    @Override
    public Matrix build(Matrix matrix) {
        return factory.createMatrix(matrix);
    }

    @Override
    public Matrix buildSymmetric(Matrix matrix) {
        return symmetrized(Matrices.asMatrixSource(matrix));
    }

    @Override
    public Matrix buildIdentity(Matrix matrix) {
        int size = Math.min(matrix.rows(), matrix.columns());
        return buildIdentity(size, size);
    }

    @Override
    public Matrix buildDiagonal(Matrix matrix) {
        return diagonalized(Matrices.asMatrixSource(matrix));
    }

    @Override
    public Matrix build(int rows, int columns, Matrix matrix) {
        return build(matrix).resize(rows, columns);
    }

    @Override
    public Matrix buildSymmetric(int rows, int columns, Matrix matrix) {
        int size = Math.min(rows, columns);
        return buildSymmetric(matrix).resize(size, size);
    }

    @Override
    public Matrix buildDiagonal(int rows, int columns, Matrix matrix) {
        int size = Math.min(rows, columns);
        return buildDiagonal(matrix).resize(size, size);
    }

    private Matrix streamedOrEmpty(MatrixStream stream) {
        try {
            return stream.readMatrix(factory);
        } catch (IOException ignored) {
            return factory.createMatrix();
        }
    }

    private Matrix symmetrized(MatrixSource source) {

        Matrix result = factory.createMatrix(source.rows(), source.columns());

        for (int i = 0; i < source.rows(); i++) {
            result.set(i, i, source.get(i, i));
            for (int j = i + 1; j < source.columns(); j++) {
                double value = source.get(i, j);
                result.set(i, j, value);
                result.set(j, i, value);
            }
        }

        return result;
    }

    private Matrix diagonalized(MatrixSource source) {

        int size = Math.min(source.rows(), source.columns());
        double diagonal[] = new double[size];

        for (int i = 0; i < size; i++) {
            diagonal[i] = source.get(i, i);
        }

        return factory.createDiagonalMatrix(diagonal);
    }
}
