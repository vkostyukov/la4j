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

package org.la4j.io;

public class VectorToBurningIterator extends VectorIterator {

    private VectorIterator underlying;
    private int currentIndex;
    private double currentValue;

    public VectorToBurningIterator(VectorIterator underlying) {
        super(underlying.length);
        this.underlying = underlying;
    }

    @Override
    public int index() {
        return currentIndex;
    }

    @Override
    public double get() {
        return currentValue;
    }

    @Override
    public void set(double value) {
        underlying.set(value);
        currentValue = value;
    }

    @Override
    public boolean hasNext() {
        return underlying.hasNext();
    }

    @Override
    public Double next() {
        double result = underlying.next();
        currentIndex = underlying.index();
        currentValue = underlying.get();
        underlying.set(0.0); // burn
        return result;
    }

    @Override
    public void flush() {
        underlying.flush();
    }
}
