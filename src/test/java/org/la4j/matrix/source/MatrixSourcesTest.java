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

package org.la4j.matrix.source;

import org.junit.Test;
import org.la4j.factory.Basic1DFactory;
import org.la4j.factory.Basic2DFactory;
import org.la4j.factory.CCSFactory;
import org.la4j.factory.CRSFactory;
import org.la4j.factory.Factory;
import org.la4j.matrix.Matrices;
import org.la4j.matrix.Matrix;

import static org.junit.Assert.assertEquals;

public class MatrixSourcesTest {

    public static final Factory[] FACTORIES = { 
        new Basic1DFactory(), new Basic2DFactory(),
        new CRSFactory(), new CCSFactory()
    };

    @Test
    public void testRandomSymmetricSource() {
        for (Factory factory: FACTORIES) {
            Matrix a = factory.createMatrix(new RandomSymmetricMatrixSource(5));

            assertEquals(5, a.rows());
            assertEquals(5, a.columns());

            for (int i = 0; i < a.rows(); i++) {
                for (int j = i + 1; j < a.columns(); j++) {
                    assertEquals(a.get(i, j), a.get(j, i), Matrices.EPS);
                }
            }
        }
    }

    @Test
    public void testIdentityMatrixSource() {
        for (Factory factory: FACTORIES) {
            Matrix a = factory.createMatrix(new IdentityMatrixSource(5));

            assertEquals(5, a.rows());
            assertEquals(5, a.columns());

            for (int i = 0; i < a.rows(); i++) {
                for (int j = 0; j < a.columns(); j++) {
                    if (i == j) {
                        assertEquals(1.0, a.get(i, j), Matrices.EPS);
                    } else {
                        assertEquals(0.0, a.get(i, j), Matrices.EPS);
                    }
                }
            }
        }
    }
}
