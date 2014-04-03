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

package org.la4j.vector.sparse;

import org.la4j.vector.Vector;
import org.la4j.vector.functor.VectorAccumulator;
import org.la4j.vector.functor.VectorProcedure;

public interface SparseVector extends Vector {

    /**
     * Returns the cardinality (the number of non-zero elements)
     * of this sparse vector.
     *
     * @return the cardinality of this vector
     */
    int cardinality();

    /**
     * Returns the density (non-zero elements divided by total elements)
     * of this sparse vector.
     *
     * @return the density of this vector
     */
    double density();

    /**
     * Whether or not the specified element is zero.
     *
     * @param i element's index
     *
     * @return {@code true} if specified element is zero, {@code false} otherwise
     */
    boolean isZeroAt(int i);

    /**
     * * Whether or not the specified element is not zero.
     *
     * @param i element's index
     *
     * @return {@code true} if specified element is zero, {@code false} otherwise
     */
    boolean nonZeroAt(int i);

    /**
     * Folds non-zero elements of this vector with given {@code accumulator}.
     *
     * @param accumulator the vector accumulator
     *
     * @return the accumulated value
     */
    double foldNonZero(VectorAccumulator accumulator);

    /**
     * Applies given {@code procedure} to each non-zero element of this vector.
     *
     * @param procedure the vector procedure
     */
    void eachNonZero(VectorProcedure procedure);

    /**
     * Gets the specified element, or a {@code defaultValue} if there
     * is no actual element at index {@code i} in this sparse vector.
     *
     * @param i the element's index
     * @param defaultValue the default value
     *
     * @return the element of this vector or a default value
     */
    double getOrElse(int i, double defaultValue);
}
