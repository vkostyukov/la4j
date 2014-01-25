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
 * Contributor(s): Daniel Renshaw
 *                 Jakob Moellers
 *                 Yuriy Drozd
 * 
 */

package org.la4j.vector;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import junit.framework.TestCase;

import org.la4j.factory.Factory;
import org.la4j.matrix.Matrix;

public abstract class AbstractVectorTest extends TestCase {

    public abstract Factory factory();

    public void testAccess_4() {

        Vector a = factory().createVector(new double[] 
                { 0.0, 0.0, 3.0, 0.0, 0.0 }
        );

        assertEquals(5, a.length());

        a.set(0, a.get(2) + 10);
        assertEquals(13.0, a.get(0));

        assertEquals(0.0, a.get(1));
    }

    public void testAssign_4() {

        Vector a = factory().createVector(4);
        Vector b = factory().createVector(new double[] {
                10.0, 10.0, 10.0, 10.0
        });

        a.assign(10.0);

        assertEquals(b, a);
    }

    public void testResize_3_to_5_to_2() {

        Vector a = factory().createVector(new double[] 
                { 0.0, 0.0, 1.0 }
        );

        Vector b = factory().createVector(new double[] 
                { 0.0, 0.0, 1.0, 0.0, 0.0 }
        );

        Vector c = factory().createVector(new double[] 
                { 0.0, 0.0 }
        );

        a = a.resize(5);
        assertEquals(b, a);

        a = a.resize(2);
        assertEquals(c, a);
    }

    public void testResize_5_to_0_to_4() {

        Vector a = factory().createVector(new double[] 
                { 0.0, 1.0, 2.0, 3.0, 0.0 }
        );

        Vector b = factory().createVector(new double[0]);

        Vector c = factory().createVector(new double[] 
                { 0.0, 0.0, 0.0, 0.0 }
        );

        a = a.resize(0);
        assertEquals(b, a);

        a = a.resize(4);
        assertEquals(c, a);
    }

    public void testSlice_5_to_2_and_3() {

        Vector a = factory().createVector(new double[] 
                { 1.0, 2.0, 3.0, 4.0, 5.0 }
        );

        Vector b = factory().createVector(new double[] 
                { 2.0, 3.0 }
        );

        Vector c = factory().createVector(new double[] 
                { 3.0, 4.0, 5.0 }
        );

        assertEquals(b, a.slice(1, 3));
        assertEquals(c, a.slice(2, 5));
    }

    public void testSliceLeftRight_5_to_1_and_4() {

        Vector a = factory().createVector(new double[] 
                { 0.0, 2.0, 0.0, 4.0, 0.0 }
        );

        Vector b = factory().createVector(new double[] 
                { 0.0 }
        );

        Vector c = factory().createVector(new double[] 
                { 2.0, 0.0, 4.0, 0.0 }
        );

        assertEquals(b, a.sliceLeft(1));
        assertEquals(c, a.sliceRight(1));
    }

    public void testSelect_4() {
        Vector a = factory().createVector(new double[]
                { 0.0, 3.0, 7.0, 0.0 }
        );

        Vector b = factory().createVector(new double[]
                { 3.0, 0.0, 0.0, 7.0 }
        );

        Vector c = factory().createVector(new double[]
                { 7.0, 7.0, 0.0, 0.0 }
        );

        assertEquals(b, a.select(new int[]{ 1, 0, 3, 2 }));
        assertEquals(c, a.select(new int[]{ 2, 2, 0, 3 }));
    }

    public void testSelect_5() {
        Vector a = factory().createVector(new double[]
                { 1.0, 6.0, 0.0, 0.0, 8.0 }
        );

        Vector b = factory().createVector(new double[]
                { 1.0, 1.0, 1.0 }
        );

        Vector c = factory().createVector(new double[]
                { 0.0, 0.0, 8.0, 8.0, 1.0, 0.0 }
        );

        assertEquals(b, a.select(new int[]{ 0, 0, 0 }));
        assertEquals(c, a.select(new int[]{ 2, 3, 4, 4, 0, 3 }));
    }

    public void testSelect_3() {
        Vector a = factory().createVector(new double[]
                { 1.0, 0.0, 0.0 }
        );

        Vector b = factory().createVector(new double[]
                { 0.0, 0.0, 0.0, 0.0 }
        );

        Vector c = factory().createVector(new double[]
                { 1.0 }
        );

        assertEquals(b, a.select(new int[]{ 1, 2, 2, 1 }));
        assertEquals(c, a.select(new int[]{ 0 }));
    }

    public void testSwap_5() {

        Vector a = factory().createVector(new double[] 
                { 1.0, 0.0, 0.0, 0.0, 3.0 }
        );

        Vector b = factory().createVector(new double[] 
                { 3.0, 0.0, 0.0, 0.0, 1.0 }
        );

        a.swap(0, 4);
        assertEquals(b, a);
    }

    public void testSwap_4() {

        Vector a = factory().createVector(new double[] 
                { 0.0, 1.0, 0.0, 0.0 }
        );

        Vector b = factory().createVector(new double[] 
                { 0.0, 0.0, 1.0, 0.0 }
        );

        a.swap(1, 2);
        assertEquals(b, a);
    }

    public void testSwap_4_2() {

        Vector a = factory().createVector(new double[] 
                { 0.0, 1.0, 0.0, 2.0 }
        );

        Vector b = factory().createVector(new double[] 
                { 0.0, 0.0, 1.0, 2.0 }
        );

        a.swap(1, 2);
        assertEquals(a,b);
    }

    public void testSwap_6() {

        Vector a = factory().createVector(new double[] 
                { 0.0, 0.0, 0.0, 0.0, 0.0, -5.0 }
        );

        Vector b = factory().createVector(new double[] 
                { 0.0, 0.0, 0.0, -5.0, 0.0, 0.0 }
        );

        a.swap(3, 5);
        assertEquals(a,b);
    }

    public void testSwap_2() {

        Vector a = factory().createVector(new double[] 
                { 1.0, 2.0 }
        );

        Vector b = factory().createVector(new double[] 
                { 2.0, 1.0 }
        );

        a.swap(0, 1);
        assertEquals(b, a);
    }

    public void testEuclideanNorm_3() {
        Vector a = factory().createVector(new double[] { 1.0, 2.0, 3.0 });
        assertEquals(3.74165, a.fold(Vectors.mkEuclideanNormAccumulator()), 1e-5);
    }

    public void testEuclideanNorm_5() {
        Vector a = factory().createVector(new double[] { 1.0, 0.0, 3.0, 0.0, -5.0 });
        assertEquals(5.91607, a.fold(Vectors.mkEuclideanNormAccumulator()), 1e-5);
    }

    public void testManhattanNorm_3() {
        Vector a = factory().createVector(new double[] { 1.0, 2.0, 3.0 });
        assertEquals(6.0, a.fold(Vectors.mkManhattanNormAccumulator()), 1e-5);
    }

    public void testManhattanNorm_5() {
        Vector a = factory().createVector(new double[] { 1.0, 0.0, 3.0, 0.0, -5.0 });
        assertEquals(9.0, a.fold(Vectors.mkManhattanNormAccumulator()), 1e-5);
    }

    public void testInfinityNorm_3() {
        Vector a = factory().createVector(new double[] { 1.0, 2.0, 3.0 });
        assertEquals(3.0, a.fold(Vectors.mkInfinityNormAccumulator()), 1e-5);
    }

    public void testInfinityNorm_5() {
        Vector a = factory().createVector(new double[] { 1.0, 0.0, 3.0, 0.0, -5.0 });
        assertEquals(5.0, a.fold(Vectors.mkInfinityNormAccumulator()), 1e-5);
    }

    public void testAdd_3() {

        Vector a = factory().createVector(new double[] 
                { 0.0, 0.0, 3.0 }
        );

        Vector b = factory().createVector(new double[] 
                { 0.0, 5.0, 0.0 }
        );

        Vector c = factory().createVector(new double[] 
                { 7.0, 7.0, 10.0 }
        );

        Vector d = factory().createVector(new double[] 
                { 0.0, 5.0, 3.0 }
        );

        assertEquals(c, a.add(7.0));
        assertEquals(d, a.add(b));
    }

    public void testSubtract_3() {

        Vector a = factory().createVector(new double[] 
                { 0.0, 0.0, 3.0 }
        );

        Vector b = factory().createVector(new double[] 
                { 4.0, 0.0, 0.0 }
        );

        Vector c = factory().createVector(new double[] 
                { -7.0, -7.0, -4.0 }
        );

        Vector d = factory().createVector(new double[] 
                { -4.0, 0.0, 3.0 }
        );

        assertEquals(c, a.subtract(7.0));
        assertEquals(d, a.subtract(b));
    }

    public void testMultiply_3() {

        Vector a = factory().createVector(new double[] 
                { 0.0, 0.0, 1.0 }
        );

        Vector b = factory().createVector(new double[] 
                { 0.0, 5.0, 0.0 }
        );

        Vector c = factory().createVector(new double[] 
                { 0.0, 0.0, 10.0 }
        );

        Vector d = factory().createVector(new double[] 
                { 0.0, 0.0, 0.0 }
        );

        assertEquals(c, a.multiply(10.0));
        assertEquals(d, a.hadamardProduct(b));
    }

    public void testHadamardProduct_3() {

        Vector a = factory().createVector(new double[] 
                { 1.0, 0.0, 2.0 }
        );

        Vector b = factory().createVector(new double[] 
                { 3.0, 0.0, 0.0 }
        );

        Vector c = factory().createVector(new double[] 
                { 3.0, 0.0, 0.0 }
        );

        assertEquals(c, a.hadamardProduct(b));
    }


    public void testMultiply_2_2x4() {

        Vector a = factory().createVector(new double[] 
                { 1.0, 2.0 }
        );

        Matrix b = factory().createMatrix(new double[][] { 
                { 0.0, 5.0, 0.0, 6.0 },
                { 1.0, 0.0, 8.0, 0.0 }
        });

        Vector c = factory().createVector(new double[] 
                { 2.0, 5.0, 16.0, 6.0 }
        );

        assertEquals(c, a.multiply(b));
    }

    public void testMultiply_3_3x1() {

        Vector a = factory().createVector(new double[] 
                { 0.0, 2.0, 0.0 }
        );

        Matrix b = factory().createMatrix(new double[][] { 
                { 0.0 },
                { 3.0 },
                { 0.0 },
        });

        Vector c = factory().createVector(new double[] 
                { 6.0 }
        );

        assertEquals(c, a.multiply(b));
    }

    public void testProduct_3() {

        Vector a = factory().createVector(new double[]
                {2.0, 4.0, 6.0}
        );

        assertTrue(Math.abs(a.product() - 48.0) < Vectors.EPS);
    }

    public void testSum_3() {

        Vector a = factory().createVector(new double[]
                {2.0, 4.0, 6.0}
        );

        assertTrue(Math.abs(a.sum() - 12.0) < Vectors.EPS);
    }

    public void testDivide_3() {

        Vector a = factory().createVector(new double[] 
                { 0.0, 0.0, 3.0 }
        );

        Vector b = factory().createVector(new double[] 
                { 0.0, 0.0, 0.3 }
        );

        assertEquals(b, a.divide(10));
    }

    public void testCopy_5() {

        Vector a = factory().createVector(new double[] 
                { 0.0, 0.0, 0.0, 0.0, 1.0 }
        );

        assertEquals(a, a.copy());
    }

    public void testBlank_5() {

        Vector a = factory().createVector(new double[] 
                { 0.0, 0.0, 0.0, 0.0, 1.0 }
        );

        Vector b = factory().createVector(new double[] 
                { 0.0, 0.0, 0.0, 0.0, 0.0 }
        );

        assertEquals(b, a.blank());
    }

    public void testSerialization() throws IOException,
            ClassNotFoundException {

        Vector a = factory().createVector(
                new double[] { 0.0, 0.0, 0.0, 0.0, 5.0 });

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = new ObjectOutputStream(bos);
        out.writeObject(a);
        out.close();

        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        ObjectInput in = new ObjectInputStream(bis);
        Vector b = (Vector) in.readObject();
        in.close();

        assertEquals(a, b);
    }

    public void testInnerProduct_1() {

        Vector a = factory().createVector(new double[] { 18.0 });
        Vector b = factory().createVector(new double[] { 10.0 });

        assertEquals(180.0, a.innerProduct(b));
    }

    public void testInnerProduct_3() {

        Vector a = factory().createVector(new double[] { 1.0, 2.0, 3.0 });
        Vector b = factory().createVector(new double[] { 10.0, 0.0, 10.0 });

        assertEquals(40.0, a.innerProduct(b));
    }

    public void testInnerProduct_4() {

        Vector a = factory().createVector(new double[] { 2, 3, 5, 7 });
        Vector b = factory().createVector(new double[] { 11, 13, 17, 19 });

        // 2 * 11 + 3 * 13 + 5 * 17 + 7 * 19 = 279
        assertEquals(279.0, a.innerProduct(b));
    }

    public void testOuterProduct_3_4() {

        Vector a = factory().createVector(new double[] { 2, 3, 5 });
        Vector b = factory().createVector(new double[] { 7, 11, 13, 17 });

        Matrix c = factory().createMatrix(new double[][] { 
                { 14, 22, 26, 34 },
                { 21, 33, 39, 51 }, 
                { 35, 55, 65, 85 } 
        });

        assertEquals(c, a.outerProduct(b));
    }

    public void testOuterProduct_1_2() {

        Vector a = factory().createVector(new double[] { 2.0 });
        Vector b = factory().createVector(new double[] { 24.0, 1.0 });

        Matrix c = factory().createMatrix(new double[][] {
                { 48.0, 2.0 }
        });

        assertEquals(c, a.outerProduct(b));
    }

    public void testOuterProduct_4_2() {

        Vector a = factory().createVector(new double[] { 2.0, 0.0, -1.0, 41.0 });
        Vector b = factory().createVector(new double[] { 4.0, -10.0 });

        Matrix c = factory().createMatrix(new double[][] {
                { 8.0, -20.0 },
                { 0.0, 0.0 },
                { -4.0, 10.0 },
                { 164.0, -410.0 }
        });

        assertEquals(c, a.outerProduct(b));
    }

    /**
     * Tests whether two vectors contain the same elements
     * 
     * @param vector1
     *            Vector1
     * @param vector2
     *            Vector2
     * @return True if both vectors contain the same elements
     */
    public boolean testWhetherVectorsContainSameElements(Vector vector1,
            Vector vector2) {

        if (vector1.length() == vector2.length()) {

            boolean[] checkList = new boolean[vector1.length()];

            for (int i = 0; i < vector1.length(); i++) {
                for (int j = 0; j < vector2.length(); j++) {
                    if (vector1.get(i) == vector2.get(j)) {
                        if (!checkList[j]) {
                            checkList[j] = true;
                            break;
                        }
                    }
                }
            }

            boolean result = true;
            for (int i = 0; i < checkList.length; i++) {
                if (!checkList[i]) {
                    result = false;
                }
            }
            return result;
        } else {
            return false;
        }
    }

    public void testTestWhetherVectorsContainSameElements() {

        Vector a = factory().createVector(new double[] { 1.0, 1.0, 3.0, 4.0 });
        Vector b = factory().createVector(new double[] { 4.0, 1.0, 1.0, 3.0 });
        assertTrue(testWhetherVectorsContainSameElements(a, b));

        Vector c = factory().createVector(new double[] { 4.0, 2.0, 1.0, 3.0 });
        assertFalse(testWhetherVectorsContainSameElements(a, c));
    }

    public void testShuffle() {

        Vector a = factory().createVector(new double[] { 1.0, 1.0, 3.0, 4.0 });
        Vector b = a.shuffle();

        assertTrue(testWhetherVectorsContainSameElements(a, b));
    }

    public void testMax() {
        Vector a = factory().createVector(new double[]{ 1.0, 0.0, 0.0, -1.0, 0.0, 0.0, 0.0, 0.0, -5.0, 0.0, 0.0, 5.0 });
        assertEquals(5.0, a.max());
    }

    public void testMaxCompressed() {
        Vector a = factory().createVector(new double[]{ 0.0, 0.0, -1.0, 0.0, 0.0, 0.0, 0.0, -5.0, 0.0, 0.0 });
        assertEquals(0.0, a.max());
    }

    public void testMin() {
        Vector a = factory().createVector(new double[]{ 1.0, 0.0, 0.0, -1.0, 0.0, 0.0, 0.0, 0.0, -5.0, 0.0, 0.0, 5.0 });
        assertEquals(-5.0, a.min());
    }

    public void testMinCompressed() {
        Vector a = factory().createVector(new double[]{ 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 5.0 });
        assertEquals(0.0, a.min());
    }
}
