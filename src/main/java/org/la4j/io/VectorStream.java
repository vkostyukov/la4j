/*
 * Copyright 2011-2013, by Vladimir Kostyukov and Contributors.
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

import java.io.IOException;

import org.la4j.factory.Factory;
import org.la4j.vector.Vector;

public interface VectorStream {

    /**
     * Reads a vector from the stream. The new vector will be constructed with 
     * {@link Vectors#DEFAULT_DENSE_FASCTORY} 
     * or {@link Vectors#DEFAULT_SPARSE_FACTORY} 
     * if it is a dense or sparse vector respectively. 
     * 
     * @return
     * @throws IOException
     */
    Vector readVector() throws IOException;

    /**
     * Reads a vector from the stream with specified <code>factory</code>. 
     * 
     * @param factory
     * @return
     * @throws IOException
     */
    Vector readVector(Factory factory) throws IOException;

    /**
     * Writes the <code>vector</code> to the stream.
     * 
     * @param vector
     * @throws IOException
     */
    void writeVector(Vector vector) throws IOException;
}
