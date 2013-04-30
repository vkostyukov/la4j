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

    public void testNorm_4() {

        Vector a = factory().createVector(new double[] { 0.0, 0.0, 0.0, 4.0 });
        Vector b = factory().createVector(new double[] { 0.0, 0.0, 0.0, 1.0 });

        assertEquals(4.0, a.norm());
        assertEquals(b, a.normalize());
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
        assertEquals(d, a.multiply(b));
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
}
