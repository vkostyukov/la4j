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
 * Contributor(s): Daniel Renshaw
 *                 Ewald Grusk
 *                 Jakob Moellers
 *                 Maxim Samoylov
 * 
 */

package org.la4j.vector;

import java.util.Random;

import org.la4j.factory.Factory;
import org.la4j.matrix.Matrices;
import org.la4j.matrix.Matrix;
import org.la4j.vector.functor.VectorAccumulator;
import org.la4j.vector.functor.VectorFunction;
import org.la4j.vector.functor.VectorPredicate;
import org.la4j.vector.functor.VectorProcedure;

public abstract class AbstractVector implements Vector {

    protected int length;

    protected Factory factory;

    protected AbstractVector(Factory factory) {
        this(factory, 0);
    }

    protected AbstractVector(Factory factory, int length) {
        ensureLengthIsNotNegative(length);

        this.factory = factory;
        this.length = length;
    }

    @Override
    public void swap(int i, int j) {
        double s = get(i);
        set(i, get(j));
        set(j, s);
    }

    @Override
    public void assign(double value) {
        for (int i = 0; i < length; i++) {
            set(i, value);
        }
    }

    @Override
    public int length() {
        return length;
    }

    @Override
    public Vector add(double value) {
        return add(value, factory);
    }

    @Override
    public Vector add(double value, Factory factory) {
        ensureFactoryIsNotNull(factory);

        Vector result = blank(factory);

        for (int i = 0; i < length; i++) {
            result.set(i, get(i) + value);
        }

        return result;
    }

    @Override
    public Vector add(Vector vector) {
        return add(vector, factory);
    }

    @Override
    public Vector add(Vector vector, Factory factory) {
        ensureFactoryIsNotNull(factory);

        if (vector == null) {
            throw new IllegalArgumentException("Vector can't be null.");
        }

        if (length != vector.length()) {
            throw new IllegalArgumentException("Wrong vector length: " 
                                               + vector.length());
        }

        Vector result = blank(factory);

        for (int i = 0; i < length; i++) {
            result.set(i, get(i) + vector.get(i));
        }

        return result;
    }

    @Override
    public Vector multiply(double value) {
        return multiply(value, factory);
    }

    @Override
    public Vector multiply(double value, Factory factory) {
        ensureFactoryIsNotNull(factory);

        Vector result = blank(factory);

        for (int i = 0; i < length; i++) {
            result.set(i, get(i) * value);
        }

        return result;
    }

    @Override
    public Vector hadamardProduct(Vector vector) {
        return hadamardProduct(vector, factory);
    }

    @Override
    public Vector hadamardProduct(Vector vector, Factory factory) {
        ensureFactoryIsNotNull(factory);

        if (vector == null) {
            throw new IllegalArgumentException("Vector can't be null.");
        }

        if (length != vector.length()) {
            throw new IllegalArgumentException("Wrong vector length: " 
                                               + vector.length());
        }

        Vector result = blank(factory);

        for (int i = 0; i < length; i++) {
            result.set(i, get(i) * vector.get(i));
        }

        return result;
    }

    @Override
    public Vector multiply(Matrix matrix) {
        return multiply(matrix, factory);
    }

    @Override
    public Vector multiply(Matrix matrix, Factory factory) {
        ensureFactoryIsNotNull(factory);

        if (matrix == null) {
            throw new IllegalArgumentException("Matrix can't be null.");
        }

        if (length != matrix.rows()) {
            throw new IllegalArgumentException("Wrong matrix dimenstions: " 
                                + matrix.rows() + "x" + matrix.columns() + ".");
        }

        Vector result = factory.createVector(matrix.columns());

        for (int j = 0; j < matrix.columns(); j++) {

            double summand = 0.0;

            for (int i = 0; i < matrix.rows(); i++) {
                summand += get(i) * matrix.get(i, j);
            }

            result.set(j, summand);
        }

        return result;
    }

    @Override
    public Vector subtract(double value) {
        return subtract(value, factory);
    }

    @Override
    public Vector subtract(double value, Factory factory) {
        return add(-value, factory);
    }

    @Override
    public Vector subtract(Vector vector) {
        return subtract(vector, factory);
    }

    @Override
    public Vector subtract(Vector vector, Factory factory) {
        ensureFactoryIsNotNull(factory);

        if (vector == null) {
            throw new IllegalArgumentException("Vector can't be null.");
        }

        if (length != vector.length()) {
            throw new IllegalArgumentException("Wrong vector length: " 
                                               + vector.length());
        }

        Vector result = blank(factory);

        for (int i = 0; i < length; i++) {
            result.set(i, get(i) - vector.get(i));
        }

        return result;
    }

    @Override
    public Vector divide(double value) {
        return divide(value, factory);
    }

    @Override
    public Vector divide(double value, Factory factory) {
        return multiply(1.0 / value, factory);
    }

    @Override
    public double product() {
        return fold(Vectors.asProductAccumulator(1));
    }

    @Override
    public double sum() {
        return fold(Vectors.asSumAccumulator(0));
    }

    @Override
    public double innerProduct(Vector vector) {

        if (vector == null) {
            throw new IllegalArgumentException("Vector can't be null.");
        }

        if (length != vector.length()) {
            throw new IllegalArgumentException("Wrong vector length: " 
                                               + vector.length());
        }

        double result = 0.0;

        for (int i = 0; i < length; i++) {
            result += get(i) * vector.get(i);
        }

        return result;
    }

    @Override
    public Matrix outerProduct(Vector vector) {
        return outerProduct(vector, factory);
    }

    @Override
    public Matrix outerProduct(Vector vector, Factory factory) {
        ensureFactoryIsNotNull(factory);

        if (vector == null) {
            throw new IllegalArgumentException("Vector can't be null.");
        }
        
        Matrix result = factory.createMatrix(length, vector.length());

        for (int i = 0; i < length; i++) {
            for (int j = 0; j < vector.length(); j++) {
                result.set(i, j, get(i) * vector.get(j));
            }
        }

        return result;
    }

    @Override
    public double norm() {
        return Math.sqrt(innerProduct(this));
    }

    @Override
    public Vector normalize() {
        return normalize(factory);
    }

    @Override
    public Vector normalize(Factory factory) {
        return divide(norm(), factory);
    }

    @Override
    public Vector blank() {
        return blank(factory);
    }

    @Override
    public Vector blank(Factory factory) {
        ensureFactoryIsNotNull(factory);

        return factory.createVector(length);
    }

    @Override
    public Vector copy() {
        return copy(factory);
    }

    @Override
    public Vector copy(Factory factory) {
        ensureFactoryIsNotNull(factory);

        return factory.createVector(this);
    }

    @Override
    public Vector resize(int length) {
        return resize(length, factory);
    }

    @Override
    public Vector resize(int length, Factory factory) {
        ensureFactoryIsNotNull(factory);

        Vector result = factory.createVector(length);

        for (int i = 0; i < Math.min(length, this.length); i++) {
            result.set(i, get(i));
        }

        return result;
    }

    @Override
    public Vector shuffle() {
        return shuffle(factory);
    }

    @Override
    public Vector shuffle(Factory factory) {
        ensureFactoryIsNotNull(factory);

        Vector result = copy(factory);

        // Conduct Fisher-Yates shuffle
        Random rnd = new Random();

        for (int i = 0; i < length; i++) {
            int ii = rnd.nextInt(length - i) + i;

            double a = result.get(ii);
            result.set(ii, result.get(i));
            result.set(i, a);
        }

        return result;
    }

    @Override
    public Vector sliceLeft(int until) {
        return slice(0, until, factory);
    }

    @Override
    public Vector sliceLeft(int until, Factory factory) {
        return slice(0, until, factory);
    }

    @Override
    public Vector sliceRight(int from) {
        return slice(from, length, factory);
    }

    @Override
    public Vector sliceRight(int from, Factory factory) {
        return slice(from, length, factory);
    }

    @Override
    public Vector slice(int from, int until) {
        return slice(from, until, factory);
    }

    @Override
    public Vector slice(int from, int until, Factory factory) {
        ensureFactoryIsNotNull(factory);

        Vector result = factory.createVector(until - from);

        for (int i = from; i < until; i++) {
            result.set(i - from, get(i));
        }

        return result;
    }

    @Override
    public Factory factory() {
        return factory;
    }

    @Override
    public void each(VectorProcedure procedure) {
        for (int i = 0; i < length; i++) {
            procedure.apply(i, get(i));
        }
    }

    @Override
    public void eachNonZero(VectorProcedure procedure) {
        for (int i = 0; i < length; i++) {
            if (Math.abs(get(i)) > Vectors.EPS) {
                procedure.apply(i, get(i));
            }
        }
    }

    @Override
    public Vector transform(VectorFunction function) {
        return transform(function, factory);
    }

    @Override
    public Vector transform(VectorFunction function, Factory factory) {

        Vector result = blank(factory);

        for (int i = 0; i < length; i++) {
            result.set(i, function.evaluate(i, get(i)));
        }

        return result;
    }

    @Override
    public Vector transform(int i, VectorFunction function) {
        return transform(i, function, factory);
    }

    @Override
    public Vector transform(int i, VectorFunction function, Factory factory) {

        Vector result = copy(factory);
        result.set(i, function.evaluate(i, get(i)));

        return result;
    }

    @Override
    public void update(VectorFunction function) {
        for (int i = 0; i < length; i++) {
            set(i, function.evaluate(i, get(i)));
        }
    }

    @Override
    public void update(int i, VectorFunction function) {
        set(i, function.evaluate(i, get(i)));
    }

    @Override
    public double fold(VectorAccumulator accumulator) {

        for (int i = 0; i < length; i++) {
            accumulator.update(i, get(i));
        }

        return accumulator.accumulate();
    }

    @Override
    public boolean is(VectorPredicate predicate) {

        boolean result = true;

        for (int i = 0; i < length; i++) {
            result = result && predicate.test(i, get(i)); 
        }

        return result;
    }

    @Override
    public Vector unsafe() {
        return this;
    }

    @Override
    public int hashCode() {

        int result = 17;

        for (int i = 0; i < length; i++) {
            long value = (long) get(i);
            result = 37 * result + (int) (value ^ (value >>> 32));
        }

        return result;
    }

    @Override
    public boolean equals(Object object) {

        if (this == object) {
            return true;
        }
        if (object == null) {
            return false;
        }

        if (!(object instanceof Vector)) {
            return false;
        }

        Vector vector = (Vector) object;

        if (length != vector.length()) {
            return false;
        }

        boolean result = true;

        for (int i = 0; result && i < length; i++) {
            double a = get(i);
            double b = vector.get(i);

            double diff = Math.abs(a - b);

            result = result && (a == b) ? true : 
                     diff < Matrices.EPS ? true :
                     diff / Math.max(Math.abs(a), Math.abs(b)) < Vectors.EPS;
        }

        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("[");
        for (int i = 0; i < length; i++) {
            sb.append(String.format("%6.3f", get(i)));
            sb.append((i < length - 1 ? ", " : " "));
        }
        sb.append("]");

        return sb.toString();
    }

    protected void ensureFactoryIsNotNull(Factory factory) {
        if (factory == null) {
            throw new IllegalArgumentException("Factory can't be null.");
        }
    }

    protected void ensureLengthIsNotNegative(int length) {
        if (length < 0) {
            throw new IllegalArgumentException("Wrong vector length: " + length);
        }
    }
}
