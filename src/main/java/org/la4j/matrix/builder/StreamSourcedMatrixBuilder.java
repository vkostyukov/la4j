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

package org.la4j.matrix.builder;

import org.la4j.io.MatrixStream;
import org.la4j.matrix.Matrix;

public class StreamSourcedMatrixBuilder extends NonTerminalMatrixBuilder {

    private MatrixStream stream;

    public StreamSourcedMatrixBuilder(UnderlyingMatrixBuilder underlying, MatrixStream stream) {
        super(underlying);
        this.stream = stream;
    }

    @Override
    public Matrix build() {
        return underlying.build(stream);
    }

    @Override
    public Matrix buildSymmetric() {
        return underlying.buildSymmetric(stream);
    }

    @Override
    public Matrix buildIdentity() {
        return underlying.buildIdentity(stream);
    }

    @Override
    public Matrix buildDiagonal() {
        return underlying.buildDiagonal(stream);
    }

    @Override
    public Matrix buildDiagonal(int rows, int columns) {
        return underlying.buildDiagonal(rows, columns, stream);
    }

    @Override
    public Matrix buildIdentity(int rows, int columns) {
        return underlying.buildIdentity(rows, columns, stream);
    }

    @Override
    public Matrix buildSymmetric(int rows, int columns) {
        return underlying.buildSymmetric(rows, columns, stream);
    }

    @Override
    public Matrix build(int rows, int columns) {
        return underlying.build(rows, columns, stream);
    }
}
