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

package org.la4j.vector.sparse;

import org.la4j.vector.AbstractVectorTest;
import org.la4j.vector.Vectors;
import org.la4j.vector.functor.VectorAccumulator;

public abstract class SparseVectorTest extends AbstractVectorTest {

    public void testCardinality() {

        SparseVector a = (SparseVector) factory().createVector(
                new double[] { 0.0, 0.0, 0.0, 0.0, 1.0 }
        );

        assertEquals(1, a.cardinality());
    }

    public void testFoldNonZero_5() {

        SparseVector a = (SparseVector) factory().createVector(
                new double[] { 2.0, 0.0, 5.0, 0.0, 2.0 }
        );

        VectorAccumulator sum = Vectors.asSumAccumulator(0.0);
        VectorAccumulator product = Vectors.asProductAccumulator(1.0);

        assertEquals(9.0, a.foldNonZero(sum));
        // check whether the accumulator were flushed
        assertEquals(9.0, a.foldNonZero(sum));

        assertEquals(20.0, a.foldNonZero(product));
        // check whether the accumulator were flushed
        assertEquals(20.0, a.foldNonZero(product));
    }

    public void testIsZeroAt_4() {

        SparseVector a = (SparseVector) factory().createVector(
                new double[] { 1.0, 0.0, 0.0, 4.0 }
        );

        assertTrue(a.isZeroAt(1));
        assertFalse(a.isZeroAt(3));
    }

    public void testNonZeroAt_6() {

        SparseVector a = (SparseVector) factory().createVector(
                new double[] { 0.0, 5.0, 2.0, 0.0, 0.0, 0.0 }
        );

        assertTrue(a.nonZeroAt(1));
        assertFalse(a.nonZeroAt(3));
    }

    public void testGetOrElse_5() {

        SparseVector a = (SparseVector) factory().createVector(
                new double[] { 0.0, 0.0, 1.0, 0.0, 0.0 }
        );

        assertEquals(0.0, a.getOrElse(1, 0.0));
        assertEquals(1.0, a.getOrElse(2, 3.14));
        assertEquals(4.2, a.getOrElse(3, 4.2));
    }
}
