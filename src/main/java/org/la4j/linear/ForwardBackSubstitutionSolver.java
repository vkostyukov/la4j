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

import org.la4j.LinearAlgebra;
import org.la4j.decomposition.MatrixDecompositor;
import org.la4j.factory.Factory;
import org.la4j.matrix.Matrices;
import org.la4j.matrix.Matrix;
import org.la4j.vector.Vector;
import org.la4j.vector.Vectors;

public class ForwardBackSubstitutionSolver extends AbstractSolver implements LinearSystemSolver {

    private static final long serialVersionUID = 4071505L;

    // Matrices from RAW_LU decomposition
    private final Matrix lu;
    private final Matrix p;

    public ForwardBackSubstitutionSolver(Matrix a) {
        super(a);

        // we use Raw LU for this
        MatrixDecompositor decompositor = a.withDecompositor(LinearAlgebra.RAW_LU);
        Matrix[] lup = decompositor.decompose();

        // TODO: it doesn't look safe.
        this.lu = lup[0];
        this.p = lup[1];
    }

    @Override
    public Vector solve(Vector b, Factory factory) {
        ensureRHSIsCorrect(b);

        int n = unknowns();

        // checks whether the lu matrix is singular or not
        for (int i = 0; i < n; i++) {
            if (lu.get(i, i) == 0.0) {
                fail("This system can not be solved: coefficient matrix is singular.");
            }
        }

        Vector x = factory.createVector(n);

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (p.get(i, j) != 0.0) {
                    x.set(i, b.get(j));
                    break;
                }
            }
        }

        for (int j = 0; j < n; j++) {
            for (int i = j + 1; i < n; i++) {
                x.update(i, Vectors.asMinusFunction(x.get(j) * lu.get(i, j)));
            }
        }

        for (int j = n - 1; j >= 0; j--) {
            x.update(j, Vectors.asDivFunction(lu.get(j, j)));

            for (int i = 0; i < j; i++) {
                x.update(i, Vectors.asMinusFunction(x.get(j) * lu.get(i, j)));
            }
        }

        return x;
    }

    @Override
    public boolean applicableTo(Matrix matrix) {
        return matrix.rows() == matrix.columns();
    }
}
