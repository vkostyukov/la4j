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

package org.la4j.matrix.source;

/**
 * A matrix source abstraction that is primary used as a constructor
 * parameter for matrices.gi
 */
@Deprecated
public interface MatrixSource {

    /**
     * Gets a specified element of this source.
     * 
     * @param i the row index
     * @param j the column index
     *
     * @return an element of this source
     */
    double get(int i, int j);

    /**
     * Returns the number of columns in this source.
     * 
     * @return a number of columns
     */
    int columns();

    /**
     * Returns the number of rows in this source.
     * 
     * @return a number of rows
     */
    int rows();
}
