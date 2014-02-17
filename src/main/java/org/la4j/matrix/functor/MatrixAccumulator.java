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

package org.la4j.matrix.functor;

/**
 * A matrix accumulator that accumulates elements across matrix.
 */
public interface MatrixAccumulator {

    /**
     * Updates a value that was accumulated so far with new matrix element.
     * 
     * @param i the row index
     * @param j the column index
     * @param value the element's value
     */
    void update(int i, int j, double value);

    /**
     * Return the accumulated value and resets this accumulator.
     *
     * @return an accumulated value
     */
    double accumulate();
}
