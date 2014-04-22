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

import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Iterator;

abstract class CursorIterator<T extends Comparable<T>> implements Iterator<Double> {

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

    public abstract double value();
    protected abstract T cursor();

    protected CursorIterator<T> or(final CursorIterator<T> those, final MergeFunction function) {
        final CursorIterator<T> these = this;
        return new CursorIterator<T>() {
            private EnumSet<IteratorState> state = EnumSet.copyOf(TAKEN_FROM_BOTH);

            @Override
            public T cursor() {
                if (state.contains(IteratorState.TAKEN_FROM_THESE)) {
                    return these.cursor();
                } else {
                    return those.cursor();
                }
            }

            @Override
            public double value() {
                if (state.contains(IteratorState.TAKEN_FROM_THESE) &&
                        state.contains(IteratorState.TAKEN_FROM_THOSE)) {

                    return function.evaluate(these.value(), those.value());
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
                    int compare = these.cursor().compareTo(those.cursor());

                    if (compare < 0) {
                        state.add(IteratorState.TAKEN_FROM_THESE);
                    } else if (compare > 0) {
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
        };
    }

    protected CursorIterator<T> and(final CursorIterator<T> those, final MergeFunction function) {
        final CursorIterator<T> these = this;
        return new CursorIterator<T>() {
            private boolean hasNext;
            private double prevValue, currValue;
            private T prevCursor, currCursor;

            {
                doNext();
            }

            @Override
            public T cursor() {
                return prevCursor;
            }

            private void doNext() {
                hasNext = false;

                prevValue = currValue;
                prevCursor = currCursor;

                if (these.hasNext() && those.hasNext()) {
                    these.next();
                    those.next();

                    T theseCursor = these.cursor();
                    T thoseCursor = those.cursor();
                    int compare = theseCursor.compareTo(thoseCursor);

                    while (compare != 0) {
                        if (these.hasNext() && compare < 0) {
                            these.next();
                            theseCursor = these.cursor();
                        } else if (those.hasNext() && compare > 0) {
                            those.next();
                            thoseCursor = those.cursor();
                        } else {
                            return;
                        }
                        compare = theseCursor.compareTo(thoseCursor);
                    }

                    hasNext = true;

                    currValue = function.evaluate(these.value(), those.value());
                    currCursor = theseCursor;
                }
            }

            @Override
            public double value() {
                return prevValue;
            }

            @Override
            public boolean hasNext() {
                return hasNext;
            }

            @Override
            public Double next() {
                doNext();
                return value();
            }
        };
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
