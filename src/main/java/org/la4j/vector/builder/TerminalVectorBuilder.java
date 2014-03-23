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

package org.la4j.vector.builder;

import org.la4j.factory.Factory;
import org.la4j.io.VectorStream;
import org.la4j.vector.Vector;

import java.io.IOException;
import java.util.Random;

public class TerminalVectorBuilder extends UnderlyingVectorBuilder {

    private Factory factory;

    public TerminalVectorBuilder(Factory factory) {
        this.factory = factory;
    }

    @Override
    public Vector build(int length) {
        return factory.createVector(length);
    }

    @Override
    public Vector build(double[] array) {
        return factory.createVector(array);
    }

    @Override
    public Vector build(VectorStream stream) {
        return streamedOrEmpty(stream);
    }

    @Override
    public Vector build(Vector vector) {
        return factory.createVector(vector);
    }

    @Override
    public Vector build(int length, double value) {
        return factory.createConstantVector(length, value);
    }

    @Override
    public Vector build(int length, double[] array) {
        return build(array).resize(length);
    }

    @Override
    public Vector build(int length, Random random) {
        return factory.createRandomVector(length, random);
    }

    @Override
    public Vector build(int length, VectorStream stream) {
        return build(stream).resize(length);
    }

    @Override
    public Vector build(int length, Vector vector) {
        return build(vector).resize(length);
    }

    @Override
    public Vector build() {
        return factory.createVector();
    }

    private Vector streamedOrEmpty(VectorStream stream) {
        try {
            return stream.readVector(factory);
        } catch (IOException ignored) {
            return factory.createVector();
        }
    }
}
