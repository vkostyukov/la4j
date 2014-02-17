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
 * A matrix predicate that tests each matrix element.
 */
public interface MatrixPredicate {

    /**
     * Tests number of rows and columns in the matrix.
     * 
     * @param rows the number of rows
     * @param columns the number of columns
     *
     * @return whether the shape meets this predicate
     */
    boolean test(int rows, int columns);

    /**
     * Tests matrix element.
     * 
     * @param i the row index
     * @param j the column index
     * @param value the element's value
     *
     * @return whether the element meets this predicate
     */
    boolean test(int i, int j, double value); 
}
