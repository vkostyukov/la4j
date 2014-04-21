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

package org.la4j.vector;

import org.la4j.vector.functor.VectorAccumulator;

import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Iterator;

public abstract class VectorIterator implements Iterator<Double> {

    private enum IteratorState {
        TAKEN_FROM_THESE,
        TAKEN_FROM_THOSE,
        THESE_ARE_EMPTY,
        THOSE_ARE_EMPTY,
    }

    private static final Collection<IteratorState> TAKEN_FROM_BOTH = Arrays.asList(
            IteratorState.TAKEN_FROM_THESE,
            IteratorState.TAKEN_FROM_THOSE
    );

    /**
     * Returns an index of the current cell.
     *
     * @return an index
     */
    public abstract int index();

    /**
     * Returns a values of the current cell
     *
     * @return
     */
    public abstract double value();

    public VectorIterator and(final VectorIterator those, final VectorAccumulator accumulator) {
        final VectorIterator these = this;
        return null;
    }

    public VectorIterator or(final VectorIterator those, final VectorAccumulator accumulator) {
        final VectorIterator these = this;
        return new VectorIterator() {
            private EnumSet<IteratorState> state = EnumSet.copyOf(TAKEN_FROM_BOTH);

            @Override
            public int index() {
                if (state.contains(IteratorState.TAKEN_FROM_THESE)) {
                    return these.index();
                } else {
                    return those.index();
                }
            }

            @Override
            public double value() {
                if (state.contains(IteratorState.TAKEN_FROM_THESE) &&
                    state.contains(IteratorState.TAKEN_FROM_THOSE)) {

                    accumulator.update(these.index(), these.value());
                    accumulator.update(those.index(), those.value());
                    return accumulator.accumulate();
                } else if (state.contains(IteratorState.TAKEN_FROM_THESE)) {
                    return these.value();
                } else {
                    return those.value();
                }
            }

            @Override
            public boolean hasNext() {
                if (these.hasNext() || those.hasNext()) {
                    return true;
                }
                if (state.containsAll(TAKEN_FROM_BOTH)) {
                    return false;
                }
                return !state.contains(IteratorState.THESE_ARE_EMPTY) ||
                       !state.contains(IteratorState.THOSE_ARE_EMPTY);
            }

            @Override
            public Double next() {
                if (state.contains(IteratorState.TAKEN_FROM_THESE)) {
                    if (these.hasNext()) {
                        these.next();
                    } else {
                        state.add(IteratorState.THESE_ARE_EMPTY);
                    }
                }

                if (state.contains(IteratorState.TAKEN_FROM_THOSE)) {
                    if (those.hasNext()) {
                        those.next();
                    } else {
                        state.add(IteratorState.THOSE_ARE_EMPTY);
                    }
                }

                state.remove(IteratorState.TAKEN_FROM_THESE);
                state.remove(IteratorState.TAKEN_FROM_THOSE);

                if (!state.contains(IteratorState.THESE_ARE_EMPTY) &&
                    !state.contains(IteratorState.THOSE_ARE_EMPTY)) {
                    if (these.index() < those.index()) {
                        state.add(IteratorState.TAKEN_FROM_THESE);
                    } else if (these.index() > those.index()) {
                        state.add(IteratorState.TAKEN_FROM_THOSE);
                    } else {
                        state.add(IteratorState.TAKEN_FROM_THESE);
                        state.add(IteratorState.TAKEN_FROM_THOSE);
                    }
                } else if (state.contains(IteratorState.THESE_ARE_EMPTY)) {
                    state.add(IteratorState.TAKEN_FROM_THOSE);
                } else if (state.contains(IteratorState.THOSE_ARE_EMPTY)) {
                    state.add(IteratorState.TAKEN_FROM_THESE);
                }

                return value();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}
