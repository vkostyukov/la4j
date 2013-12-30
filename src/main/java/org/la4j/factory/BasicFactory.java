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

import java.util.Arrays;
import java.util.Random;

import org.la4j.vector.Vector;
import org.la4j.vector.dense.BasicVector;
import org.la4j.vector.source.VectorSource;


public abstract class BasicFactory implements Factory {

    private static final long serialVersionUID = 4071505L;

    @Override
    public Vector createVector() {
        return new BasicVector();
    }

    @Override
    public Vector createVector(int length) {
        return new BasicVector(length);
    }

    @Override
    public Vector createVector(double[] array) {
        return new BasicVector(array);
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

        double array[] = new double[length];
        Arrays.fill(array, value);

        return new BasicVector(array);
    }


    @Override
    public Vector createRandomVector(int length) {

        Random rnd = new Random();

        double array[] = new double[length];
        for (int i = 0; i < length; i++) {
            array[i] = rnd.nextDouble();
        }

        return new BasicVector(array);
    }
}
