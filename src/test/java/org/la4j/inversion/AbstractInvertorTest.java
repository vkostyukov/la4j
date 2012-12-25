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

import org.la4j.factory.Basic1DFactory;
import org.la4j.factory.Basic2DFactory;
import org.la4j.factory.CCSFactory;
import org.la4j.factory.CRSFactory;
import org.la4j.factory.Factory;
import org.la4j.matrix.Matrix;

public abstract class AbstractInvertorTest extends TestCase {

    public abstract MatrixInvertor invertor();

    public Factory[] factories() {
        return new Factory[] { 
                new Basic1DFactory(), 
                new Basic2DFactory(),
                new CRSFactory(), 
                new CCSFactory() 
        };
    }

    public void xtestInverse_4x4() {

        for (Factory factory : factories()) {

            Matrix a = factory.createMatrix(new double[][] {
                    { 9.0, 3.0, 4.0, 5.0 }, 
                    { 1.0, 2.0, 3.0, 6.0 },
                    { 7.0, 0.0, 2.0, 2.0 }, 
                    { 0.0, 3.0, 4.0, 0.0 }
            });

            Matrix b = a.inverse(invertor(), factory);
            assertEquals(factory.createIdentityMatrix(a.rows()), a.multiply(b));
        }
    }

    public void xtestInverse_7x7() {

        for (Factory factory : factories()) {

            Matrix a = factory.createMatrix(new double[][] {
                    { 9.0, 3.0, 4.0, 5.0, 11.0, 7.0, 17.0 }, 
                    { 1.0, 2.0, 3.0, 6.0, 14.0, 21.0, 54.0 },
                    { 7.0, 0.0, 2.0, 2.0, 54.0, 22.0, 16.0 }, 
                    { 0.0, 3.0, 4.0, 11.0, 12.0, 43.0, 21.0},
                    { 43.0, 33.0, 3.0, 3.0, 73.0, 11.0, 15.0},
                    { 19.0, 53.0, 9.0, 0.0, 47.0, 52.0, 75.0},
                    { 84.0, 85.0, 99.0, 2.0, 17.0, 13.0, 29.0},
            });

            Matrix b = a.inverse(invertor(), factory);
            assertEquals(factory.createIdentityMatrix(a.rows()), a.multiply(b));
        }
    }

    public void testInverse_64x64_random() {

        for (Factory factory : factories()) {

            Matrix a = factory.createRandomMatrix(56, 56);

            Matrix b = a.inverse(invertor(), factory);
            assertEquals(factory.createIdentityMatrix(a.rows()), a.multiply(b));
        }
    }
}
