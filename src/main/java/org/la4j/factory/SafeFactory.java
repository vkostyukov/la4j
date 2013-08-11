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
 * Contributor(s): Maxim Samoylov
 * 
 */

package org.la4j.factory;

import org.la4j.linear.LinearSystem;
import org.la4j.matrix.Matrix;
import org.la4j.matrix.source.MatrixSource;
import org.la4j.vector.Vector;
import org.la4j.vector.source.VectorSource;

public class SafeFactory implements Factory {

    private static final long serialVersionUID = 4071505L;

    private Factory self;
    
    public SafeFactory(Factory factory) {
        this.self = factory;
    }

    @Override
    public Matrix createMatrix() {
        return self.createMatrix().safe();
    }

    @Override
    public Matrix createMatrix(int rows, int columns) {
        return self.createMatrix(rows, columns).safe();
    }

    @Override
    public Matrix createMatrix(double[][] array) {
        return self.createMatrix(array).safe();
    }

    @Override
    public Matrix createMatrix(Matrix matrix) {
        return self.createMatrix(matrix).safe();
    }

    @Override
    public Matrix createMatrix(MatrixSource source) {
        return self.createMatrix(source).safe();
    }

    @Override
    public Matrix createConstantMatrix(int rows, int columns, double value) {
        return self.createConstantMatrix(rows, columns, value).safe();
    }

    @Override
    public Matrix createRandomMatrix(int rows, int columns) {
        return self.createRandomMatrix(rows, columns).safe();
    }

    @Override
    public Matrix createRandomSymmetricMatrix(int size) {
        return self.createRandomSymmetricMatrix(size).safe();
    }

    @Override
    public Matrix createSquareMatrix(int size) {
        return self.createSquareMatrix(size).safe();
    }

    @Override
    public Matrix createIdentityMatrix(int size) {
        return self.createIdentityMatrix(size).safe();
    }

    @Override
    public Matrix createBlockMatrix(Matrix a, Matrix b, Matrix c, Matrix d) {
        return self.createBlockMatrix(a, b, c, d).safe();
    }

    @Override
    public Vector createVector() {
        return self.createVector().safe();
    }

    @Override
    public Vector createVector(int length) {
        return self.createVector(length).safe();
    }

    @Override
    public Vector createVector(double[] array) {
        return self.createVector(array).safe();
    }

    @Override
    public Vector createVector(Vector vector) {
        return self.createVector(vector).safe();
    }

    @Override
    public Vector createVector(VectorSource source) {
        return self.createVector(source).safe();
    }

    @Override
    public Vector createConstantVector(int length, double value) {
        return self.createConstantVector(length, value).safe();
    }

    @Override
    public Vector createRandomVector(int length) {
        return self.createRandomVector(length).safe();
    }

    @Override
    public LinearSystem createLinearSystem(Matrix a, Vector b) {
        return new LinearSystem(a, b, this);
    }

    @Override
    public Factory safe() {
        return this;
    }

    @Override
    public Factory unsafe() {
        return self;
    }
}
