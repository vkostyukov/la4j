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

package org.la4j.matrix.sparse;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.la4j.factory.CCSFactory;
import org.la4j.factory.Factory;
import org.la4j.matrix.Matrix;

import java.io.*;

public class CCSMatrixTest extends SparseMatrixTest {

    @Override
    public Factory factory() {
        return new CCSFactory();
    }

    public static Test suite() {
        return new TestSuite(CCSMatrixTest.class);
    }

    /*
      Specifically tests the behaviour when a matrix is declared as larger than its initial population. Ensures
      columns between and beyond those originally populuated are correctly set on re-read.
     */
    public void testExportImport() throws IOException, ClassNotFoundException
    {
        final String filename = "CCSMatrixTextExport.out";
        Matrix toExport = factory().createMatrix(10,10);
        toExport.set(0, 0, 1.0);
        toExport.set(2, 3, 2.0);
        FileOutputStream fos = new FileOutputStream(filename);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        toExport.writeExternal(oos);
        oos.close();
        fos.close();

        Matrix imported = factory().createMatrix();
        FileInputStream fis = new FileInputStream(filename);
        imported.readExternal(new ObjectInputStream(fis));
        fis.close();
        assertEquals(imported,toExport);
    }
}
