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

public class SweepSolver implements LinearSystemSolver {

    private static final long serialVersionUID = 4071505L;

    @Override
    public Vector solve(LinearSystem linearSystem, Factory factory) {

        if (!suitableFor(linearSystem)) {
            throw new IllegalArgumentException();
        }

        Matrix a = linearSystem.coefficientsMatrix();
        Vector b = linearSystem.rightHandVector();

        Vector x = factory.createVector(linearSystem.variables());

        for (int i = 0; i < linearSystem.variables() - 1; i++) {

            double maxItem = Math.abs(a.get(i, i));
            int maxIndex = i;

            for (int j = i + 1; j < linearSystem.variables(); j++) {
                if (Math.abs(a.get(j, i)) > maxItem) {
                    maxItem = Math.abs(a.get(j, i));
                    maxIndex = j;
                }
            }

            if (maxIndex != i) {
                for (int j = 0; j < linearSystem.variables(); j++) {
                    double t = a.get(i, j);
                    a.set(i, j, a.get(maxIndex, j));
                    a.set(maxIndex, j, t);
                }

                b.swap(i, maxIndex);
            }

            for (int j = i + 1; j < linearSystem.variables(); j++) {

                double c = a.get(j, i) / a.get(i, i);
                for (int k = i; k < a.columns(); k++) {
                    a.set(j, k, a.get(j, k) - a.get(i, k) 
                                 * c);
                }

                b.set(j, b.get(j) - b.get(i) * c);
            }
        }

        for (int i = linearSystem.variables() - 1; i >= 0; i--) {

            double summand = 0.0;

            for (int j = i + 1; j < a.columns(); j++) {
                summand += a.get(i, j) * x.get(j);
            }

            x.set(i, (b.get(i) - summand) / a.get(i, i));
        }

        return x;
    }

    @Override
    public boolean suitableFor(LinearSystem linearSystem) {
        return linearSystem.coefficientsMatrix()
                .is(Matrices.TRIDIAGONAL_MATRIX);
    }
}
