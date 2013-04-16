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

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.la4j.factory.Factory;
import org.la4j.vector.functor.VectorAccumulator;
import org.la4j.vector.functor.VectorFunction;
import org.la4j.vector.functor.VectorPredicate;
import org.la4j.vector.functor.VectorProcedure;

public abstract class AbstractSafeVector implements Vector {

    protected Vector self;
    protected Factory factory;

    public AbstractSafeVector(Vector vector) {
        this.self = vector;
        this.factory = vector.factory().safe();
    }

    @Override
    public double get(int i) {
        ensureIndexInLength(i);

        return self.get(i);
    }

    @Override
    public void set(int i, double value) {
        ensureIndexInLength(i);

        self.set(i, value);
    }

    @Override
    public void assign(double value) {
        self.assign(value);
    }

    @Override
    public int length() {
        return self.length();
    }

    @Override
    public Vector add(double value) {
        return self.add(value, factory);
    }

    @Override
    public Vector add(double value, Factory factory) {
        return self.add(value, factory);
    }

    @Override
    public Vector add(Vector vector) {
        return self.add(vector, factory);
    }

    @Override
    public Vector add(Vector vector, Factory factory) {
        return self.add(vector, factory);
    }

    @Override
    public Vector multiply(double value) {
        return self.multiply(value, factory);
    }

    @Override
    public Vector multiply(double value, Factory factory) {
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
    public Vector subtract(double value) {
        return self.subtract(value, factory);
    }

    @Override
    public Vector subtract(double value, Factory factory) {
        return self.subtract(value, factory);
    }

    @Override
    public Vector subtract(Vector vector) {
        return self.subtract(vector, factory);
    }

    @Override
    public Vector subtract(Vector vector, Factory factory) {
        return self.subtract(vector, factory);
    }

    @Override
    public Vector div(double value) {
        return self.div(value, factory);
    }

    @Override
    public Vector div(double value, Factory factory) {
        return self.div(value, factory);
    }

    @Override
    public double product(Vector vector) {
        return self.product(vector);
    }

    @Override
    public double norm() {
        return self.norm();
    }

    @Override
    public Vector normalize() {
        return self.normalize(factory);
    }

    @Override
    public Vector normalize(Factory factory) {
        return self.normalize(factory);
    }

    @Override
    public void swap(int i, int j) {
        ensureIndexInLength(i);
        ensureIndexInLength(j);

        self.swap(i, j);
    }

    @Override
    public Vector blank() {
        return self.blank(factory);
    }

    @Override
    public Vector blank(Factory factory) {
        return self.blank(factory);
    }

    @Override
    public Vector copy() {
        return self.copy(factory);
    }

    @Override
    public Vector copy(Factory factory) {
        return self.copy(factory);
    }

    @Override
    public Vector resize(int length) {
        return self.resize(length, factory);
    }

    @Override
    public Vector resize(int length, Factory factory) {
        return self.resize(length, factory);
    }

    @Override
    public Vector sliceLeft(int until) {
        return self.sliceLeft(until, factory);
    }

    @Override
    public Vector sliceLeft(int until, Factory factory) {
        return self.sliceLeft(until, factory);
    }

    @Override
    public Vector sliceRight(int from) {
        return self.sliceRight(from, factory);
    }

    @Override
    public Vector sliceRight(int from, Factory factory) {
        return self.sliceRight(from, factory);
    }

    @Override
    public Vector slice(int from, int until) {
        return self.slice(from, until, factory);
    }

    @Override
    public Vector slice(int from, int until, Factory factory) {
        return self.slice(from, until, factory);
    }

    @Override
    public Factory factory() {
        return factory;
    }

    @Override
    public void each(VectorProcedure procedure) {
        self.each(procedure);
    }

    @Override
    public Vector transform(VectorFunction function) {
        return self.transform(function, factory);
    }

    @Override
    public Vector transform(VectorFunction function, Factory factory) {
        return self.transform(function, factory);
    }

    @Override
    public Vector transform(int i, VectorFunction function) {
        return self.transform(i, function, factory);
    }

    @Override
    public Vector transform(int i, VectorFunction function, Factory factory) {
        return self.transform(i, function, factory);
    }

    @Override
    public void update(VectorFunction function) {
        self.update(function);
    }

    @Override
    public void update(int i, VectorFunction function) {
        self.update(i, function);
        
    }

    @Override
    public double fold(VectorAccumulator accumulator) {
        return self.fold(accumulator);
    }

    @Override
    public boolean is(VectorPredicate predicate) {
        return self.is(predicate);
    }

    @Override
    public Vector safe() {
        return this;
    }

    @Override
    public Vector unsafe() {
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

    protected void ensureIndexInLength(int i) {
        if (i >= self.length() || i < 0) {
            throw new IllegalArgumentException("Index out of bounds: " + i);
        }
    }
}
