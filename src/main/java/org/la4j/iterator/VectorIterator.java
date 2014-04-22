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

public abstract class VectorIterator extends CursorIterator<VectorIterator.VectorCursor> {

    public final class VectorCursor implements Comparable<VectorCursor> {

        public final int index;

        public VectorCursor(int index) {
            this.index = index;
        }

        @Override
        public int compareTo(VectorCursor that) {
            return this.index - that.index;
        }
    }

    /**
     * Returns an index of the current cell.
     *
     * @return an index of the current cell
     */
    public abstract int index();

    /**
     * Returns a value of the current cell
     *
     * @return a value of the current cell
     */
    public abstract double value();

    public VectorIterator andAlsoAdd(final VectorIterator those) {
        return new CursorToVectorIterator(super.andAlso(those, JoinFunction.ADD));
    }

    public VectorIterator orElseAdd(final VectorIterator those) {
        return new CursorToVectorIterator(super.orElse(those, JoinFunction.ADD));
    }

    public VectorIterator andAlsoSub(final VectorIterator those) {
        return new CursorToVectorIterator(super.andAlso(those, JoinFunction.SUB));
    }

    public VectorIterator orElseSub(final VectorIterator those) {
        return new CursorToVectorIterator(super.orElse(those, JoinFunction.SUB));
    }

    public VectorIterator andAlsoMul(final VectorIterator those) {
        return new CursorToVectorIterator(super.andAlso(those, JoinFunction.MUL));
    }

    public VectorIterator orElseMul(final VectorIterator those) {
        return new CursorToVectorIterator(super.orElse(those, JoinFunction.MUL));
    }

    @Override
    protected VectorCursor cursor() {
        // TODO: this is a bottleneck
        return new VectorCursor(index());
    }
}
