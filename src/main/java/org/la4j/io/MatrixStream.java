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
import org.la4j.matrix.Matrix;

public interface MatrixStream {


    /**
     * Reads a matrix from the stream. The new matrix will be constructed with 
     * {@link Matrices#DEFAULT_DENSE_FASCTORY} 
     * or {@link Matrices#DEFAULT_SPARSE_FACTORY} 
     * if it is a dense or sparse matrix respectively. 
     * 
     * @return
     * @throws IOException
     */
    Matrix readMatrix() throws IOException;

    /**
     * Reads a matrix from the stream with specified <code>factory</code>.
     * 
     * @param factory
     * @return
     * @throws IOException
     */
    Matrix readMatrix(Factory factory) throws IOException;

    /**
     * Writes the <code>matrix</code> to the stream.
     * 
     * @param matrix
     * @throws IOException
     */
    void writeMatrix(Matrix matrix) throws IOException;
}
