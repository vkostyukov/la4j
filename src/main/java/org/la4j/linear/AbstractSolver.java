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

package org.la4j.linear;

import org.la4j.Matrix;
import org.la4j.Vector;

public abstract class AbstractSolver implements LinearSystemSolver {

    // TODO: rename a to matrix
    protected Matrix a;
    protected int unknowns;
    protected int equations;

    protected AbstractSolver(Matrix a) {
        if (!applicableTo(a)) {
            fail("Given coefficient matrix can not be used with this solver.");
        }

        this.a = a;
        this.unknowns = a.columns();
        this.equations = a.rows();
    }

    @Override
    public Matrix self() {
        return a;
    }

    @Override
    public int unknowns() {
        return unknowns;
    }

    @Override
    public int equations() {
        return equations;
    }

    protected void ensureRHSIsCorrect(Vector vector) {
        if (vector.length() != equations) {
            fail("Wrong length of RHS vector: " + vector.length() + ".");
        }
    }

    protected void fail(String message) {
        throw new IllegalArgumentException(message);
    }
}
