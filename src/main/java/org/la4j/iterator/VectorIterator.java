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
 *
 */

package org.la4j.iterator;

import org.la4j.factory.Factory;
import org.la4j.vector.Vector;

public abstract class VectorIterator extends CursorIterator {

    protected final int length;

    public VectorIterator(int length) {
        this.length = length;
    }

    /**
     * Returns an index of the current cell.
     *
     * @return an index of the current cell
     */
    public abstract int index();

    public VectorIterator orElseAdd(final VectorIterator those) {
        return new CursorToVectorIterator(super.orElse(those, JoinFunction.ADD), length);
    }

    public VectorIterator orElseSubtract(final VectorIterator those) {
        return new CursorToVectorIterator(super.orElse(those, JoinFunction.SUB), length);
    }

    public VectorIterator andAlsoMultiply(final VectorIterator those) {
        return new CursorToVectorIterator(super.andAlso(those, JoinFunction.MUL), length);
    }

    public VectorIterator andAlsoDivide(final VectorIterator those) {
        return new CursorToVectorIterator(super.andAlso(those, JoinFunction.DIV), length);
    }

    public double innerProduct(final VectorIterator those) {
        VectorIterator both = this.andAlsoMultiply(those);
        double acc = 0.0;
        while (both.hasNext()) {
            acc += both.next();
        }

        return acc;
    }

    @Override
    protected int cursor() {
        return index();
    }
}
