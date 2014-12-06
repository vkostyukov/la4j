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

class CursorToVectorIterator extends VectorIterator {

    private final CursorIterator underlying;

    public CursorToVectorIterator(CursorIterator underlying, int length) {
        super(length);
        this.underlying = underlying;
    }

    @Override
    public int index() {
        return underlying.cursor();
    }

    @Override
    public double get() {
        return underlying.get();
    }

    @Override
    public void set(double value) {
        underlying.set(value);
    }

    @Override
    public boolean hasNext() {
        return underlying.hasNext();
    }

    @Override
    public Double next() {
        return underlying.next();
    }
}
