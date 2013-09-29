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

package org.la4j.decomposition;

import org.la4j.matrix.Matrix;

public abstract class AbstractDecompositor implements MatrixDecompositor {

    protected Matrix matrix;

    public AbstractDecompositor(Matrix matrix) {
        if (!applicableTo(matrix)) {
            fail("Given matrix can not be used with this decompositor.");
        }

        this.matrix = matrix;
    }

    @Override
    public Matrix[] decompose() {
        return decompose(matrix.factory());
    }

    @Override
    public Matrix self() {
        return matrix;
    }

    protected void fail(String message) {
        throw new IllegalArgumentException(message);
    }
}
