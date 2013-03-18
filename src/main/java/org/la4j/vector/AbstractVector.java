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
 * Contributor(s): -
 * 
 */

package org.la4j.vector;

import org.la4j.factory.Factory;
import org.la4j.matrix.Matrices;
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
        this.factory = factory;
        this.length = length;
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
            throw new IllegalArgumentException("Worong vector length: " 
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
    public Vector multiply(Vector vector) {
        return multiply(vector, factory);
    }

    @Override
    public Vector multiply(Vector vector, Factory factory) {
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
        return add(vector.multiply(-1.0), factory);
    }

    @Override
    public Vector div(double value) {
        return div(value, factory);
    }

    @Override
    public Vector div(double value, Factory factory) {
        return multiply(1.0 / value, factory);
    }

    @Override
    public double product(Vector vector) {

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
    public double norm() {
        return Math.sqrt(product(this));
    }

    @Override
    public Vector normalize() {
        return normalize(factory);
    }

    @Override
    public Vector normalize(Factory factory) {
        return div(norm(), factory);
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
        ensureFactoryIsNotNull(factory);

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
}
