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

import org.la4j.io.VectorStream;
import org.la4j.vector.Vector;

import java.util.Random;

public abstract class UnderlyingVectorBuilder implements VectorBuilder {

    // length
    public abstract Vector build(int length);

    // source
    public abstract Vector build(double array[]);
    public abstract Vector build(VectorStream stream);
    public abstract Vector build(Vector vector);

    public Vector build(double value) {
        return build();
    }

    public Vector build(Random random) {
        return build();
    }

    // length + source
    public abstract Vector build(int length, double value);
    public abstract Vector build(int length, double array[]);
    public abstract Vector build(int length, Random random);
    public abstract Vector build(int length, VectorStream stream);
    public abstract Vector build(int length, Vector vector);

    @Override
    public VectorBuilder length(int length) {
        return new LengthedVectorBuilder(this, length);
    }

    @Override
    public VectorBuilder source(double value) {
        return new ConstantSourcedVectorBuilder(this, value);
    }

    @Override
    public VectorBuilder source(double[] array) {
        return new ArraySourcedVectorBuilder(this, array);
    }

    @Override
    public VectorBuilder source(Random random) {
        return new RandomSourcedVectorBuilder(this, random);
    }

    @Override
    public VectorBuilder source(VectorStream stream) {
        return new StreamSourcedVectorBuilder(this, stream);
    }

    @Override
    public VectorBuilder source(Vector vector) {
        return new VectorSourcedVectorBuilder(this, vector);
    }
}
