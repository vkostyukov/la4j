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
 */

package org.la4j.vector.builder;

import org.la4j.io.VectorStream;
import org.la4j.vector.Vector;

import java.util.Random;

/**
 * A vector builder.
 */
public interface VectorBuilder {

    /**
     * Specifies the length of result vector.
     *
     * @param length the vector's length
     *
     * @return a lengthed vector builder
     */
    VectorBuilder length(int length);

    /**
     * Specifies the source of result vector.
     *
     * @param value the constant value source
     *
     * @return a sourced vector builder
     */
    VectorBuilder source(double value);

    /**
     * Specifies the source of result vector.
     *
     * @param array the array source
     *
     * @return a sourced vector builder
     */
    VectorBuilder source(double array[]);

    /**
     * Specifies the source of result vector.
     *
     * @param random the random generator instance
     *
     * @return a sourced vector builder
     */
    VectorBuilder source(Random random);

    /**
     * Specifies the source of result vector.
     *
     * @param stream the vector stream instance
     *
     * @return a sourced vector builder
     */
    VectorBuilder source(VectorStream stream);

    /**
     * Specifies the source of result vector.
     *
     * @param vector the source vector
     *
     * @return a sourced vector builder
     */
    VectorBuilder source(Vector vector);

    /**
     * Commits the building process and returns a vector.
     *
     * @return a freshly built vector
     */
    Vector build();
}
