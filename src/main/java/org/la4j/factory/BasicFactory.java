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

package org.la4j.factory;

import java.util.Random;

import org.la4j.vector.Vector;
import org.la4j.vector.dense.BasicVector;
import org.la4j.vector.source.VectorSource;

@Deprecated
public abstract class BasicFactory extends Factory {

    private static final long serialVersionUID = 4071505L;

    @Override
    public Vector createVector() {
        return new BasicVector();
    }

    @Override
    public Vector createVector(int length) {
        return BasicVector.zero(length);
    }

    @Override
    public Vector createVector(double[] array) {
        return BasicVector.fromArray(array);
    }

    @Override
    public Vector createVector(Vector vector) {
        return new BasicVector(vector);
    }

    @Override
    public Vector createVector(VectorSource source) {
        return new BasicVector(source);
    }

    @Override
    public Vector createConstantVector(int length, double value) {
        return BasicVector.constant(length, value);
    }

    @Override
    public Vector createRandomVector(int length, Random random) {
        return BasicVector.random(length, random);
    }
}
