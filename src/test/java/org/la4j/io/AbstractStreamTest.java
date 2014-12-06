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

package org.la4j.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.Test;
import org.la4j.LinearAlgebra;
import org.la4j.factory.Factory;
import org.la4j.matrix.Matrix;
import org.la4j.vector.Vector;

import static org.junit.Assert.assertEquals;

public abstract class AbstractStreamTest {


    @Test
    public void testMatrix_3x3() throws IOException {
        for (Factory factory: LinearAlgebra.FACTORIES) {

            Matrix a = factory.createMatrix(new double[][] {
                    { 1.0, 0.0, 3.0 },
                    { 0.0, 5.0, 0.0 },
                    { 7.0, 0.0, 9.0 }
            });

            ByteArrayOutputStream bos = new ByteArrayOutputStream();

            MatrixStream out = stream(bos);
            out.writeMatrix(a);

            ByteArrayInputStream bis = 
                    new ByteArrayInputStream(bos.toByteArray());

            MatrixStream in = stream(bis);
            Matrix b = in.readMatrix(factory);

            assertEquals(a, b);
        }
    }

    @Test
    public void testMatrix_2x5() throws IOException {
        for (Factory factory: LinearAlgebra.FACTORIES) {

            Matrix a = factory.createMatrix(new double[][] {
                    { 1.0, 0.0, 3.0, 0.0, 5.0 },
                    { 0.0, 7.0, 0.0, 9.0, 0.0 },
            });

            ByteArrayOutputStream bos = new ByteArrayOutputStream();

            MatrixStream out = stream(bos);
            out.writeMatrix(a);

            ByteArrayInputStream bis = 
                    new ByteArrayInputStream(bos.toByteArray());

            MatrixStream in = stream(bis);
            Matrix b = in.readMatrix(factory);

            assertEquals(a, b);
        }
    }

    @Test
    public void testMatrix_3x1() throws IOException {
        for (Factory factory: LinearAlgebra.FACTORIES) {

            Matrix a = factory.createMatrix(new double[][] {
                    { 1.0 },
                    { 0.0 },
                    { 3.0 }
            });

            ByteArrayOutputStream bos = new ByteArrayOutputStream();

            MatrixStream out = stream(bos);
            out.writeMatrix(a);

            ByteArrayInputStream bis = 
                    new ByteArrayInputStream(bos.toByteArray());

            MatrixStream in = stream(bis);
            Matrix b = in.readMatrix(factory);

            assertEquals(a, b);
        }
    }

    @Test
    public void testMatrix_1x1() throws IOException {
        for (Factory factory: LinearAlgebra.FACTORIES) {

            Matrix a = factory.createMatrix(new double[][] {{ 1.0 }});

            ByteArrayOutputStream bos = new ByteArrayOutputStream();

            MatrixStream out = stream(bos);
            out.writeMatrix(a);

            ByteArrayInputStream bis = 
                    new ByteArrayInputStream(bos.toByteArray());

            MatrixStream in = stream(bis);
            Matrix b = in.readMatrix(factory);

            assertEquals(a, b);
        }
    }

    @Test
    public void testMatrix_0x0() throws IOException {
        for (Factory factory: LinearAlgebra.FACTORIES) {

            Matrix a = factory.createMatrix(new double[][] {{}});

            ByteArrayOutputStream bos = new ByteArrayOutputStream();

            MatrixStream out = stream(bos);
            out.writeMatrix(a);

            ByteArrayInputStream bis = 
                    new ByteArrayInputStream(bos.toByteArray());

            MatrixStream in = stream(bis);
            Matrix b = in.readMatrix(factory);

            assertEquals(a, b);
        }
    }

    @Test
    public void testVector_3() throws IOException {
        for (Factory factory: LinearAlgebra.FACTORIES) {

            Vector a = factory.createVector(new double[] { 1.0, 0.0, 3.0 });

            ByteArrayOutputStream bos = new ByteArrayOutputStream();

            VectorStream out = stream(bos);
            out.writeVector(a);

            ByteArrayInputStream bis = 
                    new ByteArrayInputStream(bos.toByteArray());

            VectorStream in = stream(bis);
            Vector b = in.readVector(factory);

            assertEquals(a, b);
        }
    }

    @Test
    public void testVector_1() throws IOException {
        for (Factory factory: LinearAlgebra.FACTORIES) {

            Vector a = factory.createVector(new double[] { 1.0 });

            ByteArrayOutputStream bos = new ByteArrayOutputStream();

            VectorStream out = stream(bos);
            out.writeVector(a);

            ByteArrayInputStream bis = 
                    new ByteArrayInputStream(bos.toByteArray());

            VectorStream in = stream(bis);
            Vector b = in.readVector(factory);

            assertEquals(a, b);
        }
    }

    @Test
    public void testVector_0() throws IOException {
        for (Factory factory: LinearAlgebra.FACTORIES) {

            Vector a = factory.createVector(new double[] {});

            ByteArrayOutputStream bos = new ByteArrayOutputStream();

            VectorStream out = stream(bos);
            out.writeVector(a);

            ByteArrayInputStream bis = 
                    new ByteArrayInputStream(bos.toByteArray());

            VectorStream in = stream(bis);
            Vector b = in.readVector(factory);

            assertEquals(a, b);
        }
    }

    public abstract AbstractStream stream(InputStream in);
    public abstract AbstractStream stream(OutputStream out);
}
