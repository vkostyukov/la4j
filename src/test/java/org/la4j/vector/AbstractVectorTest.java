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

import org.junit.Test;
import org.la4j.factory.Factory;
import org.la4j.matrix.Matrix;
import org.la4j.vector.functor.VectorAccumulator;
import org.la4j.vector.functor.VectorPredicate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public abstract class AbstractVectorTest {

    public abstract Factory factory();

    @Test
    public void testAccess_4() {

        Vector a = factory().createVector(new double[]
                {0.0, 0.0, 3.0, 0.0, 0.0}
        );

        assertEquals(5, a.length());

        a.set(0, a.get(2) + 10);
        assertEquals(13.0, a.get(0), Vectors.EPS);

        assertEquals(0.0, a.get(1), Vectors.EPS);
    }

    @Test
    public void testAssign_4() {

        Vector a = factory().createVector(4);
        Vector b = factory().createVector(new double[]{
                10.0, 10.0, 10.0, 10.0
        });

        a.assign(10.0);

        assertEquals(b, a);
    }

    @Test
    public void testResize_3_to_5_to_2() {

        Vector a = factory().createVector(new double[]
                {0.0, 0.0, 1.0}
        );

        Vector b = factory().createVector(new double[]
                {0.0, 0.0, 1.0, 0.0, 0.0}
        );

        Vector c = factory().createVector(new double[]
                {0.0, 0.0}
        );

        a = a.resize(5);
        assertEquals(b, a);

        a = a.resize(2);
        assertEquals(c, a);
    }

    @Test
    public void testResize_5_to_0_to_4() {

        Vector a = factory().createVector(new double[]
                {0.0, 1.0, 2.0, 3.0, 0.0}
        );

        Vector b = factory().createVector(new double[0]);

        Vector c = factory().createVector(new double[]
                {0.0, 0.0, 0.0, 0.0}
        );

        a = a.resize(0);
        assertEquals(b, a);

        a = a.resize(4);
        assertEquals(c, a);
    }

    @Test
    public void testSlice_5_to_2_and_3() {

        Vector a = factory().createVector(new double[]
                {1.0, 2.0, 3.0, 4.0, 5.0}
        );

        Vector b = factory().createVector(new double[]
                {2.0, 3.0}
        );

        Vector c = factory().createVector(new double[]
                {3.0, 4.0, 5.0}
        );

        assertEquals(b, a.slice(1, 3));
        assertEquals(c, a.slice(2, 5));
    }

    @Test
    public void testSliceLeftRight_5_to_1_and_4() {

        Vector a = factory().createVector(new double[]
                {0.0, 2.0, 0.0, 4.0, 0.0}
        );

        Vector b = factory().createVector(new double[]
                {0.0}
        );

        Vector c = factory().createVector(new double[]
                {2.0, 0.0, 4.0, 0.0}
        );

        assertEquals(b, a.sliceLeft(1));
        assertEquals(c, a.sliceRight(1));
    }

    @Test
    public void testSelect_4() {
        Vector a = factory().createVector(new double[]
                {0.0, 3.0, 7.0, 0.0}
        );

        Vector b = factory().createVector(new double[]
                {3.0, 0.0, 0.0, 7.0}
        );

        Vector c = factory().createVector(new double[]
                {7.0, 7.0, 0.0, 0.0}
        );

        assertEquals(b, a.select(new int[]{1, 0, 3, 2}));
        assertEquals(c, a.select(new int[]{2, 2, 0, 3}));
    }

    @Test
    public void testSelect_5() {
        Vector a = factory().createVector(new double[]
                {1.0, 6.0, 0.0, 0.0, 8.0}
        );

        Vector b = factory().createVector(new double[]
                {1.0, 1.0, 1.0}
        );

        Vector c = factory().createVector(new double[]
                {0.0, 0.0, 8.0, 8.0, 1.0, 0.0}
        );

        assertEquals(b, a.select(new int[]{0, 0, 0}));
        assertEquals(c, a.select(new int[]{2, 3, 4, 4, 0, 3}));
    }

    @Test
    public void testSelect_3() {
        Vector a = factory().createVector(new double[]
                {1.0, 0.0, 0.0}
        );

        Vector b = factory().createVector(new double[]
                {0.0, 0.0, 0.0, 0.0}
        );

        Vector c = factory().createVector(new double[]
                {1.0}
        );

        assertEquals(b, a.select(new int[]{1, 2, 2, 1}));
        assertEquals(c, a.select(new int[]{0}));
    }

    @Test
    public void testSwap_5() {

        Vector a = factory().createVector(new double[]
                {1.0, 0.0, 0.0, 0.0, 3.0}
        );

        Vector b = factory().createVector(new double[]
                {3.0, 0.0, 0.0, 0.0, 1.0}
        );

        a.swap(0, 4);
        assertEquals(b, a);
    }

    @Test
    public void testSwap_4() {

        Vector a = factory().createVector(new double[]
                {0.0, 1.0, 0.0, 0.0}
        );

        Vector b = factory().createVector(new double[]
                {0.0, 0.0, 1.0, 0.0}
        );

        a.swap(1, 2);
        assertEquals(b, a);
    }

    @Test
    public void testSwap_4_2() {

        Vector a = factory().createVector(new double[]
                {0.0, 1.0, 0.0, 2.0}
        );

        Vector b = factory().createVector(new double[]
                {0.0, 0.0, 1.0, 2.0}
        );

        a.swap(1, 2);
        assertEquals(a, b);
    }

    @Test
    public void testSwap_6() {

        Vector a = factory().createVector(new double[]
                {0.0, 0.0, 0.0, 0.0, 0.0, -5.0}
        );

        Vector b = factory().createVector(new double[]
                {0.0, 0.0, 0.0, -5.0, 0.0, 0.0}
        );

        a.swap(3, 5);
        assertEquals(a, b);
    }

    @Test
    public void testSwap_2() {

        Vector a = factory().createVector(new double[]
                {1.0, 2.0}
        );

        Vector b = factory().createVector(new double[]
                {2.0, 1.0}
        );

        a.swap(0, 1);
        assertEquals(b, a);
    }

    @Test
    public void testEuclideanNorm_3() {
        Vector a = factory().createVector(new double[]{1.0, 2.0, 3.0});
        assertEquals(3.74165, a.fold(Vectors.mkEuclideanNormAccumulator()), 1e-5);
    }

    @Test
    public void testEuclideanNorm_5() {
        Vector a = factory().createVector(new double[]{1.0, 0.0, 3.0, 0.0, -5.0});
        assertEquals(5.91607, a.fold(Vectors.mkEuclideanNormAccumulator()), 1e-5);
    }

    @Test
    public void testManhattanNorm_3() {
        Vector a = factory().createVector(new double[]{1.0, 2.0, 3.0});
        assertEquals(6.0, a.fold(Vectors.mkManhattanNormAccumulator()), 1e-5);
    }

    @Test
    public void testManhattanNorm_5() {
        Vector a = factory().createVector(new double[]{1.0, 0.0, 3.0, 0.0, -5.0});
        assertEquals(9.0, a.fold(Vectors.mkManhattanNormAccumulator()), 1e-5);
    }

    @Test
    public void testInfinityNorm_3() {
        Vector a = factory().createVector(new double[]{1.0, 2.0, 3.0});
        assertEquals(3.0, a.fold(Vectors.mkInfinityNormAccumulator()), 1e-5);
    }

    @Test
    public void testInfinityNorm_5() {
        Vector a = factory().createVector(new double[]{1.0, 0.0, 3.0, 0.0, -5.0});
        assertEquals(5.0, a.fold(Vectors.mkInfinityNormAccumulator()), 1e-5);
    }

    @Test
    public void testAdd_3() {

        Vector a = factory().createVector(new double[]
                {0.0, 0.0, 3.0}
        );

        Vector b = factory().createVector(new double[]
                {0.0, 5.0, 0.0}
        );

        Vector c = factory().createVector(new double[]
                {7.0, 7.0, 10.0}
        );

        Vector d = factory().createVector(new double[]
                {0.0, 5.0, 3.0}
        );

        assertEquals(c, a.add(7.0));
        assertEquals(d, a.add(b));
    }

    @Test
    public void testAdd_4() {

        Vector a = factory().createVector(new double[]{1.0, 0.0, 0.0, 3.0});
        Vector b = factory().createVector(new double[]{0.0, 1.0, 0.0, 6.0});
        Vector c = factory().createVector(new double[]{1.0, 1.0, 0.0, 9.0});

        assertEquals(c, a.add(b));
    }

    @Test
    public void testSubtract_3() {

        Vector a = factory().createVector(new double[]
                {0.0, 0.0, 3.0}
        );

        Vector b = factory().createVector(new double[]
                {4.0, 0.0, 0.0}
        );

        Vector c = factory().createVector(new double[]
                {-7.0, -7.0, -4.0}
        );

        Vector d = factory().createVector(new double[]
                {-4.0, 0.0, 3.0}
        );

        assertEquals(c, a.subtract(7.0));
        assertEquals(d, a.subtract(b));
    }

    @Test
    public void testMultiply_3() {

        Vector a = factory().createVector(new double[]
                {0.0, 0.0, 1.0}
        );

        Vector b = factory().createVector(new double[]
                {0.0, 5.0, 0.0}
        );

        Vector c = factory().createVector(new double[]
                {0.0, 0.0, 10.0}
        );

        Vector d = factory().createVector(new double[]
                {0.0, 0.0, 0.0}
        );

        assertEquals(c, a.multiply(10.0));
        assertEquals(d, a.hadamardProduct(b));
    }

    @Test
    public void testHadamardProduct_3() {

        Vector a = factory().createVector(new double[]
                {1.0, 0.0, 2.0}
        );

        Vector b = factory().createVector(new double[]
                {3.0, 0.0, 0.0}
        );

        Vector c = factory().createVector(new double[]
                {3.0, 0.0, 0.0}
        );

        assertEquals(c, a.hadamardProduct(b));
    }

    @Test
    public void testMultiply_2_2x4() {

        Vector a = factory().createVector(new double[]
                {1.0, 2.0}
        );

        Matrix b = factory().createMatrix(new double[][]{
                {0.0, 5.0, 0.0, 6.0},
                {1.0, 0.0, 8.0, 0.0}
        });

        Vector c = factory().createVector(new double[]
                {2.0, 5.0, 16.0, 6.0}
        );

        assertEquals(c, a.multiply(b));
    }

    @Test
    public void testMultiply_3_3x1() {

        Vector a = factory().createVector(new double[]
                {0.0, 2.0, 0.0}
        );

        Matrix b = factory().createMatrix(new double[][]{
                {0.0},
                {3.0},
                {0.0},
        });

        Vector c = factory().createVector(new double[]
                {6.0}
        );

        assertEquals(c, a.multiply(b));
    }

    @Test
    public void testProduct_3() {

        Vector a = factory().createVector(new double[]
                {2.0, 4.0, 6.0}
        );

        assertEquals(a.product(), 48.0, Vectors.EPS);
    }

    @Test
    public void testSum_3() {

        Vector a = factory().createVector(new double[]
                {2.0, 4.0, 6.0}
        );

        assertEquals(a.sum(), 12.0, Vectors.EPS);
    }

    @Test
    public void testDivide_3() {

        Vector a = factory().createVector(new double[]
                {0.0, 0.0, 3.0}
        );

        Vector b = factory().createVector(new double[]
                {0.0, 0.0, 0.3}
        );

        assertEquals(b, a.divide(10));
    }

    @Test
    public void testCopy_5() {

        Vector a = factory().createVector(new double[]
                {0.0, 0.0, 0.0, 0.0, 1.0}
        );

        assertEquals(a, a.copy());
    }

    @Test
    public void testBlank_5() {

        Vector a = factory().createVector(new double[]
                {0.0, 0.0, 0.0, 0.0, 1.0}
        );

        Vector b = factory().createVector(new double[]
                {0.0, 0.0, 0.0, 0.0, 0.0}
        );

        assertEquals(b, a.blank());
    }

    @Test
    public void testSerialization() throws IOException,
            ClassNotFoundException {

        Vector a = factory().createVector(
                new double[]{0.0, 0.0, 0.0, 0.0, 5.0});

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

    @Test
    public void testInnerProduct_1() {

        Vector a = factory().createVector(new double[]{18.0});
        Vector b = factory().createVector(new double[]{10.0});

        assertEquals(180.0, a.innerProduct(b), Vectors.EPS);
    }

    @Test
    public void testInnerProduct_3() {

        Vector a = factory().createVector(new double[]{1.0, 2.0, 3.0});
        Vector b = factory().createVector(new double[]{10.0, 0.0, 10.0});

        assertEquals(40.0, a.innerProduct(b), Vectors.EPS);
    }

    @Test
    public void testInnerProduct_4() {

        Vector a = factory().createVector(new double[]{2, 3, 5, 7});
        Vector b = factory().createVector(new double[]{11, 13, 17, 19});

        // 2 * 11 + 3 * 13 + 5 * 17 + 7 * 19 = 279
        assertEquals(279.0, a.innerProduct(b), Vectors.EPS);
    }

    @Test
    public void testOuterProduct_3_4() {

        Vector a = factory().createVector(new double[]{2, 3, 5});
        Vector b = factory().createVector(new double[]{7, 11, 13, 17});

        Matrix c = factory().createMatrix(new double[][]{
                {14, 22, 26, 34},
                {21, 33, 39, 51},
                {35, 55, 65, 85}
        });

        assertEquals(c, a.outerProduct(b));
    }

    @Test
    public void testOuterProduct_1_2() {

        Vector a = factory().createVector(new double[]{2.0});
        Vector b = factory().createVector(new double[]{24.0, 1.0});

        Matrix c = factory().createMatrix(new double[][]{
                {48.0, 2.0}
        });

        assertEquals(c, a.outerProduct(b));
    }

    @Test
    public void testOuterProduct_4_2() {

        Vector a = factory().createVector(new double[]{2.0, 0.0, -1.0, 41.0});
        Vector b = factory().createVector(new double[]{4.0, -10.0});

        Matrix c = factory().createMatrix(new double[][]{
                {8.0, -20.0},
                {0.0, 0.0},
                {-4.0, 10.0},
                {164.0, -410.0}
        });

        assertEquals(c, a.outerProduct(b));
    }

    /**
     * Tests whether two vectors contain the same elements
     *
     * @param vector1 Vector1
     * @param vector2 Vector2
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

    @Test
    public void testTestWhetherVectorsContainSameElements() {

        Vector a = factory().createVector(new double[]{1.0, 1.0, 3.0, 4.0});
        Vector b = factory().createVector(new double[]{4.0, 1.0, 1.0, 3.0});
        assertTrue(testWhetherVectorsContainSameElements(a, b));

        Vector c = factory().createVector(new double[]{4.0, 2.0, 1.0, 3.0});
        assertFalse(testWhetherVectorsContainSameElements(a, c));
    }

    @Test
    public void testShuffle() {

        Vector a = factory().createVector(new double[]{1.0, 1.0, 3.0, 4.0});
        Vector b = a.shuffle();

        assertTrue(testWhetherVectorsContainSameElements(a, b));
    }

    @Test
    public void testMax() {
        Vector a = factory().createVector(new double[]{1.0, 0.0, 0.0, -1.0, 0.0, 0.0, 0.0, 0.0, -5.0, 0.0, 0.0, 5.0});
        assertEquals(5.0, a.max(), Vectors.EPS);
    }

    @Test
    public void testMaxCompressed() {
        Vector a = factory().createVector(new double[]{0.0, 0.0, -1.0, 0.0, 0.0, 0.0, 0.0, -5.0, 0.0, 0.0});
        assertEquals(0.0, a.max(), Vectors.EPS);
    }

    @Test
    public void testMin() {
        Vector a = factory().createVector(new double[]{1.0, 0.0, 0.0, -1.0, 0.0, 0.0, 0.0, 0.0, -5.0, 0.0, 0.0, 5.0});
        assertEquals(-5.0, a.min(), Vectors.EPS);
    }

    @Test
    public void testMinCompressed() {
        Vector a = factory().createVector(new double[]{1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 5.0});
        assertEquals(0.0, a.min(), Vectors.EPS);
    }

    @Test
    public void testFold_6() {
        Vector a = factory().createVector(new double[]{0.0, 0.0, 5.0, 0.0, 2.0, 1.0});

        VectorAccumulator sum = Vectors.asSumAccumulator(0.0);
        VectorAccumulator product = Vectors.asProductAccumulator(1.0);

        assertEquals(8.0, a.fold(sum), Vectors.EPS);
        // check whether the accumulator were flushed
        assertEquals(8.0, a.fold(sum), Vectors.EPS);

        assertEquals(0.0, a.fold(product), Vectors.EPS);
        // check whether the accumulator were flushed
        assertEquals(0.0, a.fold(product), Vectors.EPS);
    }

    @Test
    public void testIssue162_0() {

        VectorPredicate pi = new VectorPredicate() {
            @Override
            public boolean test(int i, double value) {
                return value == 3.14;
            }
        };

        Vector a = factory().createVector();
        Vector b = a.resize(31);

        assertEquals(0, a.length());
        assertEquals(31, b.length());

        b.assign(3.14);
        assertTrue(b.is(pi));

        Vector c = b.resize(42);
        c.assign(3.14);
        assertTrue(c.is(pi));

        Vector d = c.resize(54);
        d.assign(3.14);
        assertTrue(d.is(pi));
    }

    @Test
    public void testResize_32_to_110_to_1076_to_31() {

        VectorPredicate fortyTwo = new VectorPredicate() {
            @Override
            public boolean test(int i, double value) {
                return value == 42.0;
            }
        };

        Vector a = factory().createVector();
        Vector b = a.resize(32);

        assertEquals(32, b.length());

        b.assign(42.0);
        assertTrue(b.is(fortyTwo));

        Vector c = b.resize(110);
        c.assign(42.0);
        assertTrue(c.is(fortyTwo));

        Vector d = c.resize(1076);
        d.assign(42.0);
        assertTrue(d.is(fortyTwo));

        Vector e = d.resize(31);
        e.assign(42.0);
        assertTrue(e.is(fortyTwo));
    }
    
    public void testNormalize_Default() {
    	Vector a = factory().createVector(new double[] {3, 0, -4});
    	Vector b = a.normalize();
    	
    	assertEquals(3, b.length());
    	double epsilon = 0.00001;
    	assertEquals(0.6, b.get(0), epsilon);
    	assertEquals(0.0, b.get(1), epsilon);
    	assertEquals(-0.8, b.get(2), epsilon);
    	// Verify b is a unit vector
    	// The default normalize() uses Euclidean as the accumulator
    	assertEquals(1.0, b.fold(Vectors.mkEuclideanNormAccumulator()));
    }
    
    public void testNormalize_EuclideanNormAccumulator() {
    	VectorAccumulator acc = Vectors.mkEuclideanNormAccumulator();
    	
    	Vector a = factory().createVector(new double[] {3, 0, -4});
    	Vector b = a.normalize(acc);
    	
    	assertEquals(3, b.length());
    	double epsilon = 0.00001;
    	assertEquals(0.6, b.get(0), epsilon);
    	assertEquals(0.0, b.get(1), epsilon);
    	assertEquals(-0.8, b.get(2), epsilon);
    	// Verify b is a unit vector
    	assertEquals(1.0, b.fold(acc));
    }
    
    public void testNormalize_ManhattanNormAccumulator() {
    	VectorAccumulator acc = Vectors.mkManhattanNormAccumulator();
    	
    	Vector a = factory().createVector(new double[] {3, 0, -4});
    	Vector b = a.normalize(acc);
    	
    	assertEquals(3, b.length());
    	double epsilon = 0.00001;
    	assertEquals(0.42857, b.get(0), epsilon);
    	assertEquals(0.0, b.get(1), epsilon);
    	assertEquals(-0.57142, b.get(2), epsilon);
    	// Verify b is a unit vector
    	assertEquals(1.0, b.fold(acc));
    }
    
    public void testNormalize_InfinityNormAccumulator() {
    	VectorAccumulator acc = Vectors.mkInfinityNormAccumulator();
    	
    	Vector a = factory().createVector(new double[] {3, 0, -4});
    	Vector b = a.normalize(acc);
    	
    	assertEquals(3, b.length());
    	double epsilon = 0.00001;
    	assertEquals(0.75, b.get(0), epsilon);
    	assertEquals(0.0, b.get(1), epsilon);
    	assertEquals(-1.0, b.get(2), epsilon);
    	// Verify b is a unit vector
    	assertEquals(1.0, b.fold(acc));
    }
}
