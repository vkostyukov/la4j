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

import org.junit.Test;
import org.la4j.LinearAlgebra;
import org.la4j.factory.Factory;
import org.la4j.matrix.Matrix;

import static org.junit.Assert.assertTrue;

public abstract class AbstractInverterTest {

    public abstract LinearAlgebra.InverterFactory inverterFactory();

    protected void performTest(double input[][], LinearAlgebra.InverterFactory inverterFactory) {

        for (Factory factory: LinearAlgebra.FACTORIES) {

            Matrix a = factory.createMatrix(input);
            MatrixInverter inverter = a.withInverter(inverterFactory);
            Matrix b = inverter.inverse(factory);

            // a * a^-1 = e
            Matrix c = a.multiply(b);
            Matrix e = factory.createIdentityMatrix(a.rows());

            assertTrue(e.equals(c, 1e-9));
        }
    }

    @Test
    public void testInverse_1x1 () {

        double input[][] = new double[][] {
                { -0.5 }
        };

        performTest(input, inverterFactory());
    }

    @Test
    public void testInverse_2x2 () {

        double input[][] = new double[][] {
                { 1.0, 0.0 },
                { 0.0, 1.0 }
        };

        performTest(input, inverterFactory());
    }

    @Test
    public void testInverse_4x4() {

        double input[][] = new double[][] {
            { 8.0, 16.0, -7.0, 10.0, 11.0 },
            { 0.0, 1.0, 5.0, -4.0, 12.0 },
            { 100.0, 2.0, 9.0, -17.0, 1.0 },
            { -7.0, 2.0, 0.0, 11.0, 54.0 },
            { -42.0, 1.0, 6.0, 52.0, 22.0 }
        };

        performTest(input, inverterFactory());
    }

    @Test
    public void testInverseInverse_5x5 () {

        double input[][] = new double[][] {
            { 12.0, 3.0, 478.0, 235.0, 2.0, 6.0 },
            { 82.0, 1.0, 2.0, 835.0, 12.0, 11.0 },
            { 1.0, -37.0, 13.0, 8.0, 237.0, 63.0 },
            { 51.0, 84.0, 2.0, -35.0, 9.0, 2.0 },
            { 29.0, -4.0, -27.0, -46.0, 29.0, 4.0 },
            {-58.0, 939.0, 2.0, 59.0, 96.0, -5.0 }
        };

        performTest(input, inverterFactory());
    }

    @Test
    public void testInverseInverse_6x6 () {

        double input[][] = new double[][] {
            { 12.0, 3.0, 478.0, 235.0, 2.0, 6.0 },
            { 82.0, 1.0, 2.0, 835.0, 12.0, 11.0 },
            { 1.0, -37.0, 13.0, 8.0, 237.0, 63.0 },
            { 51.0, 84.0, 2.0, -35.0, 9.0, 2.0 },
            { 29.0, -4.0, -27.0, -46.0, 29.0, 4.0 },
            {-58.0, 939.0, 2.0, 59.0, 96.0, -5.0 }
        };

        performTest(input, inverterFactory());
    }
}
