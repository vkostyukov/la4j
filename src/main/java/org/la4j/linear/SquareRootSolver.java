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

public class SquareRootSolver implements LinearSystemSolver {

    private static final long serialVersionUID = 4071505L;

    @Override
    public Vector solve(LinearSystem linearSystem, Factory factory) {

        if (!suitableFor(linearSystem)) {
            throw new IllegalArgumentException();
        }

        Matrix a = linearSystem.coefficientsMatrix();
        Vector b = linearSystem.rightHandVector();

        Matrix s = a.blank();
        Matrix d = a.blank();

        Vector x = factory.createVector(linearSystem.variables());
        Vector y = factory.createVector(linearSystem.variables());
        Vector z = factory.createVector(linearSystem.variables());

        for (int i = 0; i < a.rows(); i++) {

            double dSumand = 0;
            for (int l = 0; l < i; l++) {
                dSumand += Math.pow(s.get(l, i), 2) * d.get(l, l);
            }

            d.set(i, i, Math.signum(a.get(i, i) - dSumand));

            double sSummand = 0;
            for (int l = 0; l < i; l++) {
                sSummand += s.get(l, i) * s.get(l, i) 
                            * d.get(l, l);
            }

            s.set(i, i, Math.sqrt(Math.abs(a.get(i, i) - sSummand)));

            if (Math.abs(s.get(i, i)) < Matrices.EPS) {
                throw new IllegalArgumentException(
                        "matrix s contains '0' at main diagonal");
            }

            for (int j = i + 1; j < a.columns(); j++) {

                double summand = 0;
                for (int l = 0; l < i; l++) {
                    summand += s.get(l, i) * s.get(l, i) 
                               * d.get(l, l);
                }

                s.set(i, j,
                        (a.get(i, j) - summand) / (s.get(i, i) 
                         * d.get(i, i)));
            }

            double zSummand = 0;
            for (int l = 0; l < i; l++) {
                zSummand += z.get(l) * s.get(l, i);
            }

            z.set(i, (b.get(i) - zSummand) / s.get(i, i));

            y.set(i, z.get(i) / d.get(i, i));
        }

        for (int i = a.rows() - 1; i >= 0; i--) {

            double summand = 0;
            for (int l = i + 1; l < a.columns(); l++) {
                summand += x.get(l) * s.get(i, l);
            }

            x.set(i, (y.get(i) - summand) / s.get(i, i));
        }

        return x;
    }

    @Override
    public boolean suitableFor(LinearSystem linearSystem) {
        return linearSystem.coefficientsMatrix().is(Matrices.SYMMETRIC_MATRIX);
    }
}
