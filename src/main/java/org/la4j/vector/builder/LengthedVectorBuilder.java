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

public class LengthedVectorBuilder extends NonTerminalVectorBuilder {

    private int length;

    public LengthedVectorBuilder(UnderlyingVectorBuilder underlying, int length) {
        super(underlying);
        this.length = length;
    }

    @Override
    public Vector build() {
        return underlying.build(length);
    }

    @Override
    public Vector build(double value) {
        return underlying.build(length, value);
    }

    @Override
    public Vector build(double[] array) {
        return underlying.build(length, array);
    }

    @Override
    public Vector build(Random random) {
        return underlying.build(length, random);
    }

    @Override
    public Vector build(VectorStream stream) {
        return underlying.build(length, stream);
    }

    @Override
    public Vector build(Vector vector) {
        return underlying.build(length, vector);
    }
}
