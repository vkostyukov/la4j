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
import org.la4j.vector.Vectors;

/**
 * This class represents <a
 * href="http://en.wikipedia.org/wiki/Tridiagonal_matrix_algorithm"> Sweep (or
 * Tridiagonal matrix, or Thomas) method </a> for solving linear systems.
 */
public class SweepSolver extends AbstractSolver implements LinearSystemSolver {

    private static final long serialVersionUID = 4071505L;

    public SweepSolver(Matrix a) {
        super(a);
    }

    @Override
    public Vector solve(Vector b, Factory factory) {
        ensureRHSIsCorrect(b);

        // We need a copy, since the algorithm changes data
        Matrix aa = a.copy();
        Vector bb = b.copy();

        Vector x = factory.createVector(aa.columns());

        for (int i = 0; i < aa.rows() - 1; i++) {

            double maxItem = Math.abs(aa.get(i, i));
            int maxIndex = i;

            for (int j = i + 1; j < aa.columns(); j++) {
                double value = Math.abs(aa.get(j, i));
                if (value > maxItem) {
                    maxItem = value;
                    maxIndex = j;
                }
            }

            if (maxIndex != i) {
                aa.swapRows(maxIndex, i);
                bb.swapElements(i, maxIndex);
            }

            for (int j = i + 1; j < aa.columns(); j++) {

                double c = aa.get(j, i) / aa.get(i, i);
                for (int k = i; k < aa.columns(); k++) {
                    aa.update(j, k, Matrices.asMinusFunction(aa.get(i, k) * c));
                }

                bb.update(j, Vectors.asMinusFunction(bb.get(i) * c));
            }
        }

        for (int i = aa.rows() - 1; i >= 0; i--) {

            double acc = 0.0;

            for (int j = i + 1; j < aa.columns(); j++) {
                acc += aa.get(i, j) * x.get(j);
            }

            x.set(i, (bb.get(i) - acc) / aa.get(i, i));
        }

        return x;
    }

    @Override
    public boolean applicableTo(Matrix matrix) {
        return matrix.is(Matrices.TRIDIAGONAL_MATRIX);
    }
}
