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
 *                 Miron Aseev
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

    protected AbstractVector(Factory factory, int length) {
        ensureLengthIsCorrect(length);

        this.factory = factory;
        this.length = length;
    }

    @Override
    public void swap(int i, int j) {
        if (i != j) {
            double s = get(i);
            set(i, get(j));
            set(j, s);
        }
    }

    @Override
    public void assign(double value) {
        update(Vectors.asConstFunction(value));
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
        ensureArgumentIsNotNull(vector, "vector");

        if (length != vector.length()) {
            fail("Wrong vector length: " + vector.length() + ". Should be: " + length + ".");
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
        ensureArgumentIsNotNull(vector,  "vector");

        if (length != vector.length()) {
            fail("Wrong vector length: " + vector.length() + ". Should be: " + length + ".");
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
        ensureArgumentIsNotNull(matrix, "matrix");

        if (length != matrix.rows()) {
            fail("Wrong matrix dimensions: " + matrix.rows() + "x" + matrix.columns() +
                 ". Should be: " + length + "x_.");
        }

        Vector result = factory.createVector(matrix.columns());

        for (int j = 0; j < matrix.columns(); j++) {

            double acc = 0.0;

            for (int i = 0; i < matrix.rows(); i++) {
                acc += get(i) * matrix.get(i, j);
            }

            result.set(j, acc);
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
        ensureArgumentIsNotNull(vector, "vector");

        if (length != vector.length()) {
            fail("Wrong vector length: " + vector.length() + ". Should be: " + length + ".");
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
        return fold(Vectors.asProductAccumulator(1.0));
    }

    @Override
    public double sum() {
        return fold(Vectors.asSumAccumulator(0.0));
    }

    @Override
    public double innerProduct(Vector vector) {
        ensureArgumentIsNotNull(vector, "vector");

        if (length != vector.length()) {
            fail("Wong vector length: " + vector.length() + ". Should be: " + length + ".");
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
        ensureArgumentIsNotNull(vector, "vector");

        Matrix result = factory.createMatrix(length, vector.length());

        for (int i = 0; i < length; i++) {
            for (int j = 0; j < vector.length(); j++) {
                result.set(i, j, get(i) * vector.get(j));
            }
        }

        return result;
    }

    @Override
    @Deprecated
    public double norm() {
        return fold(Vectors.mkEuclideanNormAccumulator());
    }

    @Override
    @Deprecated
    public Vector normalize() {
        return normalize(factory);
    }

    @Override
    @Deprecated
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

        if (until - from < 0) {
            fail("Wrong slice range: [" + from + ".." + until + "].");
        }

        Vector result = factory.createVector(until - from);

        for (int i = from; i < until; i++) {
            result.set(i - from, get(i));
        }

        return result;
    }

    @Override
    public Vector select(int[] indices) {
        return select(indices, factory);
    }

    @Override
    public Vector select(int[] indices, Factory factory) {
        int newLength = indices.length;

        if (newLength == 0) {
            fail("No elements selected.");
        }

        Vector result = factory.createVector(newLength);

        for (int i = 0; i < newLength; i++) {
            result.set(i, get(indices[i]));
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
    public double max() {
        return fold(Vectors.mkMaxAccumulator());
    }

    @Override
    public double min() {
        return fold(Vectors.mkMinAccumulator());
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
    public boolean non(VectorPredicate predicate) {
        return !is(predicate);
    }

    @Override
    public Matrix toRowMatrix() {
        return toRowMatrix(factory);
    }

    @Override
    public Matrix toRowMatrix(Factory factory) {
        Matrix result = factory.createMatrix(1, length);
        result.setRow(0, this);
        return result;
    }

    @Override
    public Matrix toColumnMatrix() {
        return toColumnMatrix(factory);
    }

    @Override
    public Matrix toColumnMatrix(Factory factory) {
        Matrix result = factory.createMatrix(length, 1);
        result.setColumn(0, this);
        return result;
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

            result = (a == b) || (diff < Matrices.EPS || diff / Math.max(Math.abs(a), Math.abs(b)) < Vectors.EPS);
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
        ensureArgumentIsNotNull(factory, "factory");
    }

    protected void ensureLengthIsCorrect(int length) {
        if (length < 0) {
            fail("Wrong vector length: " + length);
        }
        if (length == Integer.MAX_VALUE) {
            fail("Wrong vector length: use 'Integer.MAX_VALUE - 1' instead.");
        }
    }

    protected void ensureArgumentIsNotNull(Object argument, String name) {
        if (argument == null) {
            fail("Bad argument: \"" + name + "\" is 'null'.");
        }
    }

    protected void fail(String message) {
        throw new IllegalArgumentException(message);
    }
}
