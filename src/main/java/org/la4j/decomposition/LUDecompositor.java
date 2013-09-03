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
import org.la4j.matrix.Matrices;
import org.la4j.matrix.Matrix;
import org.la4j.vector.Vector;
import org.la4j.vector.Vectors;

/**
 * This class represents LU decomposition of matrices. More details
 * <p>
 * <a href="http://mathworld.wolfram.com/LUDecomposition.html"> here.</a>
 * </p>
 */
public class LUDecompositor implements MatrixDecompositor {

    /**
     * Returns the result of LU decomposition of given matrix
     * <p>
     * See <a href="http://mathworld.wolfram.com/LUDecomposition.html">
     * http://mathworld.wolfram.com/LUDecomposition.html</a> for more details.
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

        Matrix lu = matrix.copy();
        Matrix p = factory.createIdentityMatrix(lu.rows());

        for (int j = 0; j < lu.columns(); j++) {

            Vector jcolumn = lu.getColumn(j);

            for (int i = 0; i < lu.rows(); i++) {

                int kmax = Math.min(i, j);

                double s = 0.0;
                for (int k = 0; k < kmax; k++) {
                    s += lu.get(i, k) * jcolumn.get(k);
                }

                jcolumn.update(i, Vectors.asMinusFunction(s));
                lu.set(i, j, jcolumn.get(i));
            }

            int pivot = j;

            for (int i = j + 1; i < lu.rows(); i++) {
                if (Math.abs(jcolumn.get(i)) > Math.abs(jcolumn.get(pivot))) {
                    pivot = i;
                }
            }

            if (pivot != j) {
                lu.swapRows(pivot, j);
                p.swapRows(pivot, j);
            }

            if (j < lu.rows() && Math.abs(lu.get(j, j)) > Matrices.EPS) {
                for (int i = j + 1; i < lu.rows(); i++) {
                    lu.update(i, j, Matrices.asDivFunction(lu.get(j, j)));
                }
            }
        }

        Matrix l = factory.createMatrix(lu.rows(), lu.columns());

        for (int i = 0; i < l.rows(); i++) {
            for (int j = 0; j <= i; j++) {
                if (i > j) {
                    l.set(i, j, lu.get(i, j));
                } else if (i == j) {
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
