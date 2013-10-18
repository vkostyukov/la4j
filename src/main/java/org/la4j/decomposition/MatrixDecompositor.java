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

package org.la4j.decomposition;

import org.la4j.factory.Factory;
import org.la4j.matrix.Matrix;

import java.io.Serializable;

/**
 * Interface for implementing various matrix decompositions. More details
 * <p><a href="http://mathworld.wolfram.com/MatrixDecomposition.html">
 * here.</a>
 * </p>
 */
public interface MatrixDecompositor extends Serializable {

    /**
     * Decomposes the wrapped matrix.
     *
     * @return
     */
    Matrix[] decompose();

    /**
     *
     * @param factory
     * @return
     */
    Matrix[] decompose(Factory factory);

    /**
     * Returns the self matrix of this decompositor.
     *
     * @return
     */
    Matrix self();

    /**
     * Checks whether this decompositor is applicable to given matrix or not.
     *
     * @param matrix
     * @return
     */
    boolean applicableTo(Matrix matrix);
}
