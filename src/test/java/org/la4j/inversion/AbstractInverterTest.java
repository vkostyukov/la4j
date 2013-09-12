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

package org.la4j.inversion;

import junit.framework.TestCase;

import org.la4j.factory.Factory;
import org.la4j.matrix.Matrices;
import org.la4j.matrix.Matrix;
import org.la4j.matrix.MockMatrix;

public abstract class AbstractInverterTest extends TestCase {

    public abstract MatrixInverter inverter();

    protected void performTest(double input[][], MatrixInverter inverter) {

        for (Factory factory: Matrices.FACTORIES) {

            Matrix a = factory.createMatrix(input);

            Matrix b = a.inverse(inverter, factory);

            // a * a^-1 = e
            Matrix c = a.multiply(b);
            Matrix e = factory.createIdentityMatrix(a.rows());

            assertEquals(new MockMatrix(e), new MockMatrix(c));
        }
    }

    public void testInverse_4x4() {

        double input[][] = new double[][] {
                { 9.0, 3.0, 4.0, 5.0 }, 
                { 1.0, 2.0, 3.0, 6.0 },
                { 7.0, 0.0, 2.0, 2.0 }, 
                { 0.0, 3.0, 4.0, 0.0 }
        };

        performTest(input, inverter());
    }
}
