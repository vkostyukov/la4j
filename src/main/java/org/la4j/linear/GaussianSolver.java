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

package org.la4j.linear;

import org.la4j.factory.Factory;
import org.la4j.matrix.Matrices;
import org.la4j.matrix.Matrix;
import org.la4j.vector.Vector;

public class GaussianSolver implements LinearSystemSolver {

    private static final long serialVersionUID = 4071505L;

    @Override
    public Vector solve(LinearSystem linearSystem, Factory factory) {

        int rows = linearSystem.equations();
        int columns = linearSystem.variables();

        Matrix a = linearSystem.coefficientsMatrix().copy();
        Vector b = linearSystem.rightHandVector().copy();

        a.resize(rows, columns + 1);
        a.setColumn(columns, b);

        Matrix triangle = createExtendTriangleMatrix(a);

        return retraceGaus(triangle, factory);
    }

    private Matrix createExtendTriangleMatrix(Matrix matrix) {

        Matrix result = matrix.copy();

        for (int i = 0; i < result.rows(); i++) {

            int maxIndex = 0;
            double maxItem = result.get(i, i);

            for (int k = i + 1; k < result.rows(); k++) {

                if (Math.abs(result.get(k, i)) > maxItem) {
                    maxItem = Math.abs(result.get(k, i));
                    maxIndex = k;
                }
            }

            if (Math.abs(maxItem) < Matrices.EPS) {
                throw new IllegalArgumentException("This system can't be solved.");
            }

            if (maxIndex > i) {
                result.swapRows(maxIndex, i);
            }

            for (int j = i + 1; j < result.rows(); j++) {

                double C = result.get(j, i) / result.get(i, i);
                result.set(j, i, C);

                for (int k = i + 1; k < result.columns(); k++) {
                    result.set(j, k, result.get(j, k) 
                                      - result.get(i, k) * C);
                }
            }
        }

        return result;
    }

    private Vector retraceGaus(Matrix matrix, Factory factory) {

        if (Math.abs(matrix.product()) < Matrices.EPS) {
            throw new IllegalArgumentException("This system hasn't solution.");
        }

        Vector result = factory.createVector(matrix.columns() - 1);

        for (int i = result.length() - 1; i >= 0; i--) {

            double summand = 0;
            for (int j = i + 1; j < result.length(); j++) {
                summand += result.get(j) * matrix.get(i, j);
            }

            result.set(i, (matrix.get(i, matrix.columns() - 1) 
                                                    - summand)
                              / matrix.get(i, i));
        }

        return result;
    }

    @Override
    public boolean suitableFor(LinearSystem linearSystem) {
        return true;
    }
}
