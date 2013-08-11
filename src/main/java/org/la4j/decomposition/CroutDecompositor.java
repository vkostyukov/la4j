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
 * Contributor(s): Yuriy Drozd
 * 
 */

package org.la4j.decomposition;

import org.la4j.factory.Factory;
import org.la4j.matrix.Matrices;
import org.la4j.matrix.Matrix;

/**
 * This class represents LU decomposition of matrices. More details
 * <p>
 * <a href="http://math.fullerton.edu/mathews/n2003/CholeskyMod.html"> here.</a>
 * </p>
 */
public class CroutDecompositor implements MatrixDecompositor {
	
    /**
     * Returns the result of Crout decomposition of given matrix
     * <p>
     * See <a href="http://math.fullerton.edu/mathews/n2003/CholeskyMod.html">
     * http://math.fullerton.edu/mathews/n2003/CholeskyMod.html</a> for more details.
     * </p>
     * 
     * @param matrix
     * @param factory
     * @return { L, U }
     */
    @Override
    public Matrix[] decompose(Matrix matrix, Factory factory) {

        if (matrix.rows() != matrix.columns()) {
            throw new IllegalArgumentException("Wrong matrix size: " 
                    +  "rows != columns");
        }

        Matrix l = matrix.blank(factory);
        Matrix u = factory.createIdentityMatrix(matrix.rows());

        for (int j = 0; j < l.columns(); j++) {
            for (int i = j; i < l.rows(); i++) {

                double s = 0.0;

                for (int k = 0; k < j; k++) {
                    s += l.get(i, k) * u.get(k, j);
                }

                l.set(i, j, matrix.get(i, j) - s);
            }

            for (int i = j; i < l.rows(); i++) {

                double s = 0.0;

                for (int k = 0; k < j; k++) {
                    s += l.get(j, k) * u.get(k, i);
                }

                if (Math.abs(l.get(j, j)) < Matrices.EPS) {
                    throw new IllegalArgumentException("Singular matrix!");
                }

                u.set(j, i, (matrix.get(j, i) - s) / l.get(j, j));
            }
        }

        return new Matrix[] { l, u };
    }		
}
