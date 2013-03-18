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

        Vector a = factory().createVector(
                new double[] { 0.0, 0.0, 3.0, 0.0, 0.0 });

        a.set(0, a.get(2) + 10);
        assertEquals(13.0, a.get(0));
    }

    public void testResize() {

        Vector a = factory().createVector(new double[] { 0.0, 0.0, 1.0 });
        Vector b = factory().createVector(
                new double[] { 0.0, 0.0, 1.0, 0.0, 0.0 });
        Vector c = factory().createVector(new double[] { 0.0, 0.0 });

        a.resize(5);
        assertEquals(b, a);

        a.resize(2);
        assertEquals(c, a);
    }

    public void testSwap() {

        Vector a = factory().createVector(
                new double[] { 1.0, 0.0, 0.0, 0.0, 3.0 });
        Vector b = factory().createVector(
                new double[] { 3.0, 0.0, 0.0, 0.0, 1.0 });

        a.swap(0, 4);
        assertEquals(b, a);
    }

    public void testNorm() {

        Vector a = factory().createVector(new double[] { 0.0, 0.0, 0.0, 4.0 });
        Vector b = factory().createVector(new double[] { 0.0, 0.0, 0.0, 1.0 });

        double norm = a.norm();
        assertEquals(4.0, norm);

        Vector c = a.normalize();
        assertEquals(b, c);
    }

    public void testAdd() {

        Vector a = factory().createVector(new double[] { 0.0, 0.0, 3.0 });
        Vector b = factory().createVector(new double[] { 0.0, 5.0, 0.0 });

        Vector c = a.add(7.0);
        assertEquals(factory().createVector(new double[] { 7.0, 7.0, 10.0 }), c);

        Vector d = a.add(b);
        assertEquals(factory().createVector(new double[] { 0.0, 5.0, 3.0 }), d);
    }

    public void testSubtract() {

        Vector a = factory().createVector(new double[] { 0.0, 0.0, 3.0 });
        Vector b = factory().createVector(new double[] { 4.0, 0.0, 0.0 });

        Vector c = a.subtract(7.0);
        assertEquals(factory().createVector(new double[] { -7.0, -7.0, -4.0 }),
                c);

        Vector d = a.subtract(b);
        assertEquals(factory().createVector(new double[] { -4.0, 0.0, 3.0 }), d);

    }

    public void testMultiply() {

        Vector a = factory().createVector(new double[] { 0.0, 0.0, 1.0 });
        Vector b = factory().createVector(new double[] { 0.0, 5.0, 0.0 });

        Vector c = a.multiply(10.0);
        assertEquals(factory().createVector(new double[] { 0.0, 0.0, 10.0 }), c);

        Vector d = a.multiply(b);
        assertEquals(factory().createVector(new double[] { 0.0, 0.0, 0.0 }), d);
    }

    public void testDiv() {

        Vector a = factory().createVector(new double[] { 0.0, 0.0, 3.0 });

        Vector b = a.div(10);
        assertEquals(factory().createVector(new double[] { 0.0, 0.0, 0.3 }), b);
    }

    public void testCopy() {

        Vector a = factory().createVector(
                new double[] { 0.0, 0.0, 0.0, 0.0, 1.0 });

        Vector b = a.copy();

        assertEquals(a, b);
    }

    public void testBlank() {

        Vector a = factory().createVector(
                new double[] { 0.0, 0.0, 0.0, 0.0, 1.0 });
        Vector b = factory().createVector(
                new double[] { 0.0, 0.0, 0.0, 0.0, 0.0 });

        Vector c = a.blank();

        assertEquals(b, c);
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
