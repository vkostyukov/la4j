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

/**
 * This class represents LU decomposition of matrices. More details
 * <p>
 * <a href="http://mathworld.wolfram.com/LUDecomposition.html"> here.</a>
 * </p>
 */
public class LUDecompositor extends RawLUDecompositor implements MatrixDecompositor {

    public LUDecompositor(Matrix matrix) {
        super(matrix);
    }

    /**
     * Returns the result of LU decomposition of given matrix
     * <p>
     * See <a href="http://mathworld.wolfram.com/LUDecomposition.html">
     * http://mathworld.wolfram.com/LUDecomposition.html</a> for more details.
     * </p>
     * 
     * @param factory
     * @return { L, U, P }
     */
    @Override
    public Matrix[] decompose(Factory factory) {

        Matrix[] lup = super.decompose(factory);
        Matrix lu = lup[0];
        Matrix p = lup[1];

        Matrix l = factory.createMatrix(lu.rows(), lu.columns());

        for (int i = 0; i < l.rows(); i++) {
            for (int j = 0; j <= i; j++) {
                if (i > j) {
                    l.set(i, j, lu.get(i, j));
                } else {
                    l.set(i, j, 1.0);
                }
            }
        }

        Matrix u = factory.createMatrix(lu.columns(), lu.columns());

        for (int i = 0; i < u.rows(); i++) {
            for (int j = i; j < u.columns(); j++) {
                u.set(i, j, lu.get(i, j));
            }
        }

        return new Matrix[] { l, u, p };
    }
}
