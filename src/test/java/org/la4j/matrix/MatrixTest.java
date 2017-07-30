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
 * Contributor(s): Evgenia Krivova
 *                 Jakob Moellers
 *                 Maxim Samoylov
 *                 Anveshi Charuvaka
 *                 Todd Brunhoff
 *                 Catherine da Graca
 * 
 */

package org.la4j.matrix;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Test;
import org.la4j.*;
import org.la4j.vector.DenseVector;

import static org.la4j.M.*;
import static org.la4j.V.*;

public abstract class MatrixTest<T extends Matrix> {

    protected MatrixFactory<T> factory;

    public MatrixTest(MatrixFactory<T> factory) {
        this.factory = factory;
    }

    public T m(double[]... values) {
        return M.m(values).to(factory);
    }

    public T mz(int rows, int columns) {
        return M.mz(rows, columns).to(factory);
    }

    @Test
    public void testInsertRowInEmptyMatrix() {

        Matrix a = m(a(1.0, 0.0, 0.0),
            a(0.0, 1.0, 0.0),
            a(0.0, 0.0, 1.0));

        Vector v1 = v(1.0, 0.0, 0.0);
        Vector v2 = v(0.0, 1.0, 0.0);
        Vector v3 = v(0.0, 0.0, 1.0);

        Matrix b = mz(0, 0);
        b = b.insertRow(0, v3);
        b = b.insertRow(0, v2);
        b = b.insertRow(0, v1);

        Assert.assertEquals(a, b);
    }

    @Test
    public void testInsertColumnInEmptyMatrix() {

        Matrix a = m(a(1.0, 0.0, 0.0),
            a(0.0, 1.0, 0.0),
            a(0.0, 0.0, 1.0));

        Vector v1 = v(1.0, 0.0, 0.0);
        Vector v2 = v(0.0, 1.0, 0.0);
        Vector v3 = v(0.0, 0.0, 1.0);

        Matrix b = mz(0, 0);
        b = b.insertColumn(0, v3);
        b = b.insertColumn(0, v2);
        b = b.insertColumn(0, v1);

        Assert.assertEquals(a, b);
    }

    @Test
    public void testInsertRowAtTheEnd() {

        Matrix a = m(a(1.0, 0.0, 0.0),
            a(0.0, 1.0, 0.0),
            a(0.0, 0.0, 1.0));

        Vector v1 = v(1.0, 0.0, 0.0);
        Vector v2 = v(0.0, 1.0, 0.0);
        Vector v3 = v(0.0, 0.0, 1.0);

        Matrix b = mz(0, 0);
        b = b.insertRow(0, v1);
        b = b.insertRow(1, v2);
        b = b.insertRow(2, v3);

        Assert.assertEquals(a, b);
    }

    @Test
    public void testInsertColumnAtTheEnd() {

        Matrix a = m(a(1.0, 0.0, 0.0),
            a(0.0, 1.0, 0.0),
            a(0.0, 0.0, 1.0));

        Vector v1 = v(1.0, 0.0, 0.0);
        Vector v2 = v(0.0, 1.0, 0.0);
        Vector v3 = v(0.0, 0.0, 1.0);

        Matrix b = mz(0, 0);
        b = b.insertColumn(0, v1);
        b = b.insertColumn(1, v2);
        b = b.insertColumn(2, v3);

        Assert.assertEquals(a, b);
    }

    @Test
    public void testInsert_3x3_into_3x3() {
        Matrix a = m(a(1.0, 2.0, 3.0),
                     a(4.0, 5.0, 6.0),
                     a(7.0, 8.0, 9.0));

        Matrix b = mz(3, 3);
        
        Assert.assertEquals(a, b.insert(a));
    }
    
    @Test
    public void testInsert_2x2_into_3x3() {
        Matrix a = m(a(1.0, 2.0),
                     a(3.0, 4.0));

        Matrix b = mz(3, 3);
        
        Assert.assertEquals(a, b.insert(a).slice(0, 0, 2, 2));
    }

    @Test
    public void testInsert_2x2_into_3x3_partial() {
        Matrix a = m(a(1.0, 2.0),
                     a(3.0, 4.0));

        Matrix b = mz(3, 3);
        
        Assert.assertEquals(a.slice(0, 0, 1, 2), b.insert(a, 1, 2).slice(0, 0, 1, 2));
    }
    
    @Test
    public void testInsert_3x3_slice_into_4x4_offset() {
        Matrix a = m(a(1.0, 2.0, 3.0),
                     a(4.0, 5.0, 6.0),
                     a(7.0, 8.0, 9.0));

        Matrix b = mz(4, 4);

        Assert.assertEquals(a.slice(1, 1, 3, 3), b.insert(a, 1, 1, 1, 1, 2, 2).slice(1, 1, 3, 3));
    }
    
    @Test
    public void testInsert_2x2_into_4x4_offset() {
        Matrix a = m(a(1.0, 2.0),
                     a(3.0, 4.0));
        
        Matrix b = mz(3, 3);
        
        Assert.assertEquals(a, b.insert(a, 1, 1, a.rows(), a.columns()).slice(1, 1, 3, 3));
    }

    @Test
    public void testInsert_3x1_into_3x3_offset() {
        Matrix a = m(a(1.0),
                     a(2.0),
                     a(3.0));

        Matrix b = mz(3, 3);

        Assert.assertEquals(a, b.insert(a, 0, 2, a.rows(), a.columns()).slice(0, 2, 3, 3));
    }

    @Test
    public void testInsert_1x3_into_3x3_offset() {
        Matrix a = m(a(1.0, 2.0, 3.0));

        Matrix b = mz(3, 3);

        Assert.assertEquals(a, b.insert(a, 2, 0, a.rows(), a.columns()).slice(2, 0, 3, 3));
    }
    
    @Test
    public void testAccess_3x3() {
        Matrix a = m(a(1.0, 0.0, 3.0),
                     a(0.0, 5.0, 0.0),
                     a(7.0, 0.0, 9.0));

        a.set(0, 1, a.get(1, 1) * 2);

        Assert.assertEquals(10.0, a.get(0, 1), Matrices.EPS);
    }

    @Test
    public void testGetColumn_4x4() {
        Matrix a = m(a(8.0, 3.0, 1.0, 9.0),
                     a(4.0, 9.0, 6.0, 6.0),
                     a(9.0, 1.0, 1.0, 4.0),
                     a(5.0, 7.0, 3.0, 0.0));

        Vector b = v(8.0, 4.0, 9.0, 5.0);
        Vector c = v(1.0, 6.0, 1.0, 3.0);

        Assert.assertEquals(b, a.getColumn(0));
        Assert.assertEquals(c, a.getColumn(2));
    }

    @Test
    public void testGetRow_4x4() {
        Matrix a = m(a(8.0, 3.0, 1.0, 9.0),
                     a(4.0, 9.0, 6.0, 6.0),
                     a(9.0, 1.0, 1.0, 4.0),
                     a(5.0, 7.0, 3.0, 0.0));

        Vector b = v(8.0, 3.0, 1.0, 9.0);
        Vector c = v(9.0, 1.0, 1.0, 4.0);

        Assert.assertEquals(b, a.getRow(0));
        Assert.assertEquals(c, a.getRow(2));
    }

    @Test
    public void testResize_3x3_to_4x4_to_2x2() {
        Matrix a = m(a(1.0, 0.0, 0.0),
                     a(0.0, 5.0, 0.0),
                     a(0.0, 0.0, 9.0));

        Matrix b = m(a(1.0, 0.0, 0.0, 0.0),
                     a(0.0, 5.0, 0.0, 0.0),
                     a(0.0, 0.0, 9.0, 0.0),
                     a(0.0, 0.0, 0.0, 0.0));

        Matrix c = m(a(1.0, 0.0),
                     a(0.0, 5.0));

        a = a.copyOfShape(a.rows() + 1, a.columns() + 1);
        Assert.assertEquals(b, a);

        a = a.copyOfShape(a.rows() - 2, a.columns() - 2);
        Assert.assertEquals(c, a);
    }

    @Test
    public void testResize_2x3_to_3x4_to_1x2() {
        Matrix a = m(a(1.0, 0.0, 0.0),
                     a(0.0, 5.0, 0.0));

        Matrix b = m(a(1.0, 0.0, 0.0, 0.0),
                     a(0.0, 5.0, 0.0, 0.0),
                     a(0.0, 0.0, 0.0, 0.0));

        Matrix c = m(a(1.0, 0.0));

        a = a.copyOfShape(a.rows() + 1, a.columns() + 1);
        Assert.assertEquals(b, a);

        a = a.copyOfShape(a.rows() - 2, a.columns() - 2);
        Assert.assertEquals(c, a);
    }

    @Test
    public void testResize_2x3_to_2x4_to_2x1() {
        Matrix a = m(a(1.0, 0.0, 0.0),
                     a(0.0, 5.0, 0.0));

        Matrix b = m(a(1.0, 0.0, 0.0, 0.0),
                     a(0.0, 5.0, 0.0, 0.0));

        Matrix c = m(a(1.0),
                     a(0.0));

        a = a.copyOfShape(a.rows(), a.columns() + 1);
        Assert.assertEquals(b, a);

        a = a.copyOfShape(a.rows(), a.columns() - 3);
        Assert.assertEquals(c, a);
    }

    @Test
    public void testResize_3x5_to_4x5_to_2x5() {
        Matrix a = m(a(1.0, 0.0, 0.0, 0.0, 0.0),
                     a(0.0, 5.0, 0.0, 0.0, 0.0),
                     a(0.0, 0.0, 7.0, 0.0, 0.0));

        Matrix b = m(a(1.0, 0.0, 0.0, 0.0, 0.0),
                     a(0.0, 5.0, 0.0, 0.0, 0.0),
                     a(0.0, 0.0, 7.0, 0.0, 0.0),
                     a(0.0, 0.0, 0.0, 0.0, 0.0));

        Matrix c = m(a(1.0, 0.0, 0.0, 0.0, 0.0),
                     a(0.0, 5.0, 0.0, 0.0, 0.0));

        a = a.copyOfShape(a.rows() + 1, a.columns());
        Assert.assertEquals(b, a);

        a = a.copyOfShape(a.rows() - 2, a.columns());
        Assert.assertEquals(c, a);
    }

    @Test
    public void testSlice_4x4_to_2x2() {
        Matrix a = m(a(1.0, 0.0, 0.0, 0.0),
                     a(0.0, 5.0, 0.0, 0.0),
                     a(0.0, 0.0, 9.0, 0.0),
                     a(0.0, 0.0, 0.0, 15.0));

        Matrix b = m(a(5.0, 0.0),
                     a(0.0, 9.0));

        Assert.assertEquals(b, a.slice(1, 1, 3, 3));
    }

    @Test
    public void testSlice_3x4_to_1x4() {
        Matrix a = m(a(1.0, 0.0, 3.0, 0.0),
                     a(0.0, 5.0, 0.0, 7.0),
                     a(4.0, 0.0, 9.0, 0.0));

        Matrix b = m(a(4.0, 0.0, 9.0, 0.0));

        Assert.assertEquals(b, a.slice(2, 0, 3, 4));
    }

    @Test
    public void testSwap_3x3() {
        Matrix a = m(a(1.0, 0.0, 0.0),
                     a(0.0, 5.0, 0.0),
                     a(0.0, 0.0, 9.0));

        Matrix b = m(a(0.0, 0.0, 9.0),
                     a(0.0, 5.0, 0.0),
                     a(1.0, 0.0, 0.0));

        Matrix c = m(a(9.0, 0.0, 0.0),
                     a(0.0, 5.0, 0.0),
                     a(0.0, 0.0, 1.0));

        a.swapRows(0, 2);
        Assert.assertEquals(b, a);

        b.swapColumns(0, 2);
        Assert.assertEquals(c, b);
    }

    @Test
    public void testSwap_2x4() {
        Matrix a = m(a(1.0, 0.0, 0.0, 3.0),
                     a(0.0, 5.0, 4.0, 0.0));

        Matrix b = m(a(0.0, 5.0, 4.0, 0.0),
                     a(1.0, 0.0, 0.0, 3.0));

        Matrix c = m(a(0.0, 4.0, 5.0, 0.0),
                     a(1.0, 0.0, 0.0, 3.0));

        a.swapRows(0, 1);
        Assert.assertEquals(b, a);

        b.swapColumns(1, 2);
        Assert.assertEquals(c, b);
    }

    @Test
    public void testSwap_5x3() {
        Matrix a = m(a(1.0, 0.0, 0.0),
                     a(0.0, 5.0, 4.0),
                     a(7.0, 0.0, 2.0),
                     a(0.0, 8.0, 0.0),
                     a(5.0, 0.0, 6.0));

        Matrix b = m(a(1.0, 0.0, 0.0),
                     a(0.0, 5.0, 4.0),
                     a(0.0, 8.0, 0.0),
                     a(7.0, 0.0, 2.0),
                     a(5.0, 0.0, 6.0));

        Matrix c = m(a(1.0, 0.0, 0.0),
                     a(0.0, 4.0, 5.0),
                     a(0.0, 0.0, 8.0),
                     a(7.0, 2.0, 0.0),
                     a(5.0, 6.0, 0.0));
        a.swapRows(2, 3);
        Assert.assertEquals(b, a);

        b.swapColumns(1, 2);
        Assert.assertEquals(c, b);
    }

    @Test
    public void testTranspose_4x4() {
        Matrix a = m(a(0.0, 14.2, 0.0, 4.0),
                     a(0.0, 5.0, 10.0, 0.0),
                     a(0.0, 3.0, 0.0, 2.3),
                     a(11.0, 7.0, 0.0, 1.0));

        Matrix b = m(a(0.0, 0.0, 0.0, 11.0),
                     a(14.2, 5.0, 3.0, 7.0),
                     a(0.0, 10.0, 0.0, 0.0),
                     a(4.0, 0.0, 2.3, 1.0));

        Matrix c = a.transpose();
        Assert.assertEquals(b, c);

        Matrix d = c.transpose();
        Assert.assertEquals(a, d);
    }

    @Test
    public void testTranspose_5x3() {
        Matrix a = m(a(0.0, 14.2, 0.0),
                     a(0.0, 5.0, 10.0),
                     a(0.0, 3.0, 0.0),
                     a(11.0, 7.0, 0.0),
                     a(12.0, 7.5, 0.0));

        Matrix b = m(a(0.0, 0.0, 0.0, 11.0, 12.0),
                     a(14.2, 5.0, 3.0, 7.0, 7.5),
                     a(0.0, 10.0, 0.0, 0.0, 0));

        Matrix c = a.transpose();
        Assert.assertEquals(b, c);

        Matrix d = c.transpose();
        Assert.assertEquals(a, d);
    }

    @Test
    public void testTranspose_6x5() {
        Matrix a = m(a(8.93, 3.96, 7.37, 3.43, 7.05),
                     a(5.88, 8.26, 5.79, 9.08, 7.75),
                     a(6.57, 2.51, 8.8, 1.16, 8.11),
                     a(9.3, 9.61, 0.87, 2.3, 2.93),
                     a(3.65, 4.63, 7.83, 3.66, 9.04),
                     a(0.08, 6.12, 6.15, 4.93, 6.72));

        Matrix b = m(a(8.93, 5.88, 6.57, 9.3, 3.65, 0.08),
                     a(3.96, 8.26, 2.51, 9.61, 4.63, 6.12),
                     a(7.37, 5.79, 8.8, 0.87, 7.83, 6.15),
                     a(3.43, 9.08, 1.16, 2.3, 3.66, 4.93),
                     a(7.05, 7.75, 8.11, 2.93, 9.04, 6.72));

        Matrix c = a.transpose();
        Assert.assertEquals(b, c);

        Matrix d = c.transpose();
        Assert.assertEquals(a, d);
    }

    @Test
    public void testAdd_3x3() {
        for (Matrix b: ms(a(11.0, 10.0, 10.0),
                          a(10.0, 15.0, 10.0),
                          a(10.0, 10.0, 19.0))) {

            Matrix a = m(a(1.0, 0.0, 0.0),
                         a(0.0, 5.0, 0.0),
                         a(0.0, 0.0, 9.0));

            Matrix c = m(a(12.0, 10.0, 10.0),
                         a(10.0, 20.0, 10.0),
                         a(10.0, 10.0, 28.0));

            Assert.assertEquals(b, a.add(10.0));
            Assert.assertEquals(c, a.add(b));
        }
    }

    @Test
    public void testAdd_4x2() {
        for (Matrix b: ms(a(11.0, 10.0),
                          a(10.0, 15.0),
                          a(17.0, 10.0),
                          a(10.0, 19.0))) {

            Matrix a = m(a(1.0, 0.0),
                         a(0.0, 5.0),
                         a(7.0, 0.0),
                         a(0.0, 9.0));

            Matrix c = m(a(12.0, 10.0),
                         a(10.0, 20.0),
                         a(24.0, 10.0),
                         a(10.0, 28.0));

            Assert.assertEquals(b, a.add(10.0));
            Assert.assertEquals(c, a.add(b));
        }
    }

    @Test
    public void testSubtract_3x3() {
        for (Matrix b: ms(a(-9.0, -10.0, -10.0),
                          a(-10.0, -5.0, -10.0),
                          a(-10.0, -10.0, -1.0))) {

            Matrix a = m(a(1.0, 0.0, 0.0),
                         a(0.0, 5.0, 0.0),
                         a(0.0, 0.0, 9.0));

            Matrix c = m(a(10.0, 10.0, 10.0),
                         a(10.0, 10.0, 10.0),
                         a(10.0, 10.0, 10.0));

            Assert.assertEquals(b, a.subtract(10.0));
            Assert.assertEquals(c, a.subtract(b));
        }
    }

    @Test
    public void testSubtract_2x4() {
        for (Matrix b: ms(a(-9.0, -10.0, -3.0, -10.0),
                          a(-10.0, -5.0, -10.0, -1.0))) {

            Matrix a = m(a(1.0, 0.0, 7.0, 0.0),
                         a(0.0, 5.0, 0.0, 9.0));

            Matrix c = m(a(10.0, 10.0, 10.0, 10.0),
                         a(10.0, 10.0, 10.0, 10.0));

            Assert.assertEquals(b, a.subtract(10.0));
            Assert.assertEquals(c, a.subtract(b));
        }
    }

    @Test
    public void testMultiply_2x3() {
        Matrix a = m(a(1.0, 0.0, 3.0),
                     a(0.0, 5.0, 0.0));

        Matrix b = m(a(2.0, 0.0, 6.0),
                     a(0.0, 10.0, 0.0));

        Assert.assertEquals(b, a.multiply(2.0));
    }

    @Test
    public void testMultiply_2x3_3() {
        for (Vector b: vs(10.0, 0.0, 30.0)) {
            Matrix a = m(a(1.0, 0.0, 3.0),
                         a(0.0, 5.0, 0.0));

            Vector c = v(100.0, 0.0);

            Assert.assertEquals(c, a.multiply(b));
        }
    }

    @Test
    public void testMultiply_5x2_2() {
        for (Vector b: vs(0.0, 10.0)) {
            Matrix a = m(a(1.0, 0.0),
                         a(0.0, 5.0),
                         a(7.0, 0.0),
                         a(3.0, 0.0),
                         a(0.0, 1.0));

            Vector c = v(0.0, 50.0, 0.0, 0.0, 10.0);

            Assert.assertEquals(c, a.multiply(b));
        }
    }

    @Test
    public void testMultiply_1x1_1x1() {
        for (Matrix b: ms(a(8.48))) {
            Matrix a = m(a(3.37));
            Matrix c = m(a(28.5776));

            Assert.assertEquals(c, a.multiply(b));
        }
    }

    @Test
    public void testMultiply_2x2_2x2() {
        for (Matrix b: ms(a(3.85, 8.28),
                          a(8.02, 8.39))) {

            Matrix a = m(a(2.46, 1.68),
                         a(7.57, 2.47));

            Matrix c = m(a(22.9446, 34.464),
                         a(48.9539, 83.4029));

            Assert.assertEquals(c, a.multiply(b));
        }
    }

    @Test
    public void testMultiply_4x4_4x4() {
        for (Matrix b: ms(a(4.0, 9.0, 0.0, 3.0),
                          a(6.0, 7.0, 7.0, 6.0),
                          a(9.0, 4.0, 3.0, 3.0),
                          a(4.0, 4.0, 1.0, 6.0))) {

            Matrix a = m(a(8.0, 3.0, 1.0, 9.0),
                         a(4.0, 9.0, 6.0, 6.0),
                         a(9.0, 1.0, 1.0, 4.0),
                         a(5.0, 7.0, 3.0, 0.0));

            Matrix c = m(a(95.0, 133.0, 33.0, 99.0),
                         a(148.0, 147.0, 87.0, 120.0),
                         a(67.0, 108.0, 14.0, 60.0),
                         a(89.0, 106.0, 58.0, 66.0));

            Assert.assertEquals(c, a.multiply(b));
        }
    }

    @Test
    public void testMultiply_4x1_1x4() {
        for (Matrix b: ms(a(5.19, 6.06, 6.12, 5.92))) {
            Matrix a = m(a(6.31),
                         a(6.06),
                         a(4.94),
                         a(9.62));

            Matrix c = m(a(32.7489, 38.2386, 38.6172, 37.3552),
                         a(31.4514, 36.7236, 37.0872, 35.8752),
                         a(25.6386, 29.9364, 30.2328, 29.2448),
                         a(49.9278, 58.2972, 58.8744, 56.9504));

            Assert.assertEquals(c, a.multiply(b));
        }
    }

    @Test
    public void testMultiply_1x10_10x1() {
        for (Matrix b: ms(a(9.81),
                          a(0.14),
                          a(8.91),
                          a(8.54),
                          a(0.97),
                          a(2.55),
                          a(1.11),
                          a(2.52),
                          a(7.71),
                          a(1.69))) {

            Matrix a = m(a(0.28, 1.61, 5.11, 1.71, 2.21, 5.97, 2.61, 2.58, 0.07, 3.78));

            Matrix c = m(a(96.7995));

            Assert.assertEquals(c, a.multiply(b));
        }
    }

    @Test
    public void testMultiply_3x2_2x3() {
        for (Matrix b: ms(a(0.0, 3.0, 0.0),
                          a(2.0, 0.0, 4.0))) {

            Matrix a = m(a(1.0, 9.0),
                         a(9.0, 1.0),
                         a(8.0, 9.0));

            Matrix c = m(a(18.0, 3.0, 36.0),
                         a(2.0, 27.0, 4.0),
                         a(18.0, 24.0, 36.0));

            Assert.assertEquals(c, a.multiply(b));
        }
    }

    @Test
    public void testMultiply_4x9_9x4() {
        for (Matrix b: ms(a(9.28, 7.63, 4.1, 4.71),
                          a(4.68, 2.82, 9.18, 5.39),
                          a(4.54, 6.86, 1.29, 5.4),
                          a(8.72, 2.06, 4.28, 7.37),
                          a(2.43, 3.7, 7.52, 5.87),
                          a(8.21, 9.36, 4.85, 0.3),
                          a(9.87, 8.19, 5.03, 6.14),
                          a(9.47, 4.28, 3.86, 3.12),
                          a(5.29, 4.41, 5.23, 4.85))) {

            Matrix a = m(a(5.98, 3.76, 9.01, 9.68, 2.12, 6.34, 0.64, 6.22, 1.16),
                         a(8.4, 9.65, 7.06, 2.56, 7.66, 4.69, 3.29, 8.6, 8.55),
                         a(4.99, 7.06, 6.07, 7.53, 0.08, 1.08, 9.69, 8.51, 6.61),
                         a(4.72, 7.06, 4.0, 0.75, 2.45, 4.4, 8.33, 5.81, 0.57));

            Matrix c = m(a(326.9658, 242.1452, 192.0747, 211.7362),
                         a(393.7521, 318.7092, 317.9021, 283.44),
                         a(392.8255, 270.4737, 247.3277, 268.7303),
                         a(283.873, 230.76, 199.6044, 175.1515));

            Assert.assertEquals(c, a.multiply(b));
        }
    }

    @Test
    public void testMultiplyByItsTranspose_2x2() {
        Matrix a = m(a(1.0, 2.0),
                     a(0.0, 1.0));

        Matrix b = m(a(5.0, 2.0),
                     a(2.0, 1.0));

        Assert.assertEquals(a.multiplyByItsTranspose(), b);
    }

    @Test
    public void testMultiplyByItsTranspose_4x4() {
        Matrix a = m(a(3.0, 6.0, 7.0, 11.0),
                     a(5.0, 2.0, 4.0, 7.0),
                     a(1.0, -6.0, 2.0, 9.0),
                     a(10.0, -3.0, -7.0, 9.0));

        Matrix b = m(a(215.0, 132.0, 80.0, 62.0),
                     a(132.0, 94.0, 64.0, 79.0),
                     a(80.0, 64.0, 122.0, 95.0),
                     a(62.0, 79.0, 95.0, 239.0));

        Assert.assertEquals(a.multiplyByItsTranspose(), b);
    }

    @Test
    public void testMultiplyByItsTranspose_2x3() {
        Matrix a = m(a(1.0, 2.0, 3.0),
                     a(0.0, 1.0, 2.0));

        Assert.assertEquals(a.multiplyByItsTranspose(), a.multiply(a.transpose()));
    }

    @Test
    public void testMultiplyByItsTranspose_5x3() {
        Matrix a = m(a(0.0, 2.0, 0.0),
                     a(0.0, 1.0, 2.0),
                     a(4.0, 0.0, 0.0),
                     a(0.0, 5.0, 7.0),
                     a(1.0, 1.0, 0.0));

        Assert.assertEquals(a.multiplyByItsTranspose(), a.multiply(a.transpose()));
    }

    @Test
    public void testDivide_3x3() {
        Matrix a = m(a(1.0, 0.0, 0.0),
                     a(0.0, 5.0, 0.0),
                     a(0.0, 0.0, 9.0));

        Matrix b = m(a(0.1, 0.0, 0.0),
                     a(0.0, 0.5, 0.0),
                     a(0.0, 0.0, 0.9));

        Assert.assertEquals(b, a.divide(10.0));
    }

    @Test
    public void testKronecker_3x3_2x2() {
        for (Matrix b: ms(a(10.0, 0.0),
                          a(0.0, 20.0))) {

            Matrix a = m(a(1.0, 0.0, 3.0),
                         a(0.0, 5.0, 0.0));

            Matrix c = m(a(10.0, 0.0, 0.0, 0.0, 30.0, 0.0),
                         a(0.0, 20.0, 0.0, 0.0, 0.0, 60.0),
                         a(0.0, 0.0, 50.0, 0.0, 0.0, 0.0),
                         a(00.0, 0.0, 0.0, 100.0, 0.0, 0.0));

            Assert.assertEquals(c, a.kroneckerProduct(b));
        }
    }

    @Test
    public void testTrace_3x3() {
        Matrix a = m(a(1.0, 0.0, 0.0),
                     a(0.0, 5.0, 0.0),
                     a(0.0, 0.0, 9.0));

        Assert.assertTrue(Math.abs(a.trace() - 15.0) < Matrices.EPS);
    }

    @Test
    public void testDiagonalProduct_3x3() {
        Matrix a = m(a(1.0, 0.0, 0.0),
                     a(0.0, 5.0, 0.0),
                     a(0.0, 0.0, 9.0));

        Assert.assertTrue(Math.abs(a.diagonalProduct() - 45.0) < Matrices.EPS);
    }

    @Test
    public void testProduct_3x3() {
        Matrix a = m(a(1.0, 1.0, 1.0),
                     a(1.0, 5.0, 1.0),
                     a(1.0, 1.0, 9.0));

        Assert.assertTrue(Math.abs(a.product() - 45.0) < Matrices.EPS);
    }

    @Test
    public void testSum_3x3() {
        Matrix a = m(a(1.0, 0.0, 0.0),
                     a(0.0, 5.0, 0.0),
                     a(0.0, 0.0, 9.0));

        Assert.assertTrue(Math.abs(a.sum() - 15.0) < Matrices.EPS);
    }

    @Test
    public void testHadamardProduct_3x3() {
        for (Matrix b: ms(a(9.0, 8.0, 7.0),
                          a(6.0, 5.0, 4.0),
                          a(3.0, 2.0, 1.0))) {

            Matrix a = m(a(1.0, 2.0, 3.0),
                         a(4.0, 5.0, 6.0),
                         a(7.0, 8.0, 9.0));

            Matrix c = m(a(9.0, 16.0, 21.0),
                         a(24.0, 25.0, 24.0),
                         a(21.0, 16.0, 9.0));

            Assert.assertEquals(c, a.hadamardProduct(b));
        }
    }

    @Test
    public void testHadamardProduct_5x2() {
        for (Matrix b: ms(a(6.0, 5.0),
                          a(5.0, 4.0),
                          a(4.0, 3.0),
                          a(3.0, 2.0),
                          a(2.0, 1.0))) {

            Matrix a = m(a(1.0, 2.0),
                         a(2.0, 3.0),
                         a(3.0, 4.0),
                         a(4.0, 5.0),
                         a(5.0, 6.0));

            Matrix c = m(a(6.0, 10.0),
                         a(10.0, 12.0),
                         a(12.0, 12.0),
                         a(12.0, 10.0),
                         a(10.0, 6.0));

            Assert.assertEquals(c, a.hadamardProduct(b));
        }
    }

    @Test
    public void testHadamardProduct_3x4() {
        for (Matrix b: ms(a(6.0, 5.0, 4.0, 3.0),
                          a(5.0, 4.0, 3.0, 2.0),
                          a(4.0, 3.0, 2.0, 1.0))) {

            Matrix a = m(a(1.0, 2.0, 3.0, 4.0),
                         a(2.0, 3.0, 4.0, 5.0),
                         a(3.0, 4.0, 5.0, 6.0));

            Matrix c = m(a(6.0, 10.0, 12.0, 12.0),
                         a(10.0, 12.0, 12.0, 10.0),
                         a(12.0, 12.0, 10.0, 6.0));

            Assert.assertEquals(c, a.hadamardProduct(b));
        }
    }

    @Test
    public void testHadamardProduct_1x3() {
        for (Matrix b: ms(a(6.0, 5.0, 4.0, 3.0))) {
            Matrix a = m(a(1.0, 2.0, 3.0, 4.0));

            Matrix c = m(a(6.0, 10.0, 12.0, 12.0));

            Assert.assertEquals(c, a.hadamardProduct(b));
        }
    }

    @Test
    public void testDeterminant_3x3() {
        Matrix a = m(a(1.0, 0.0, 0.0),
                     a(0.0, 5.0, 0.0),
                     a(0.0, 0.0, 9.0));

        Assert.assertEquals(45.0, a.determinant(), 1e-5);
    }

    @Test
    public void testDeterminant_4x4() {
        Matrix a = m(a(3.0, 3.0, 4.0, 43.0),
                     a(5.0, 5.0, 6.0, 6.0),
                     a(70.0, 7.0, 8.0, 8.0),
                     a(9.0, 9.0, 10.0, 11.0));

        Assert.assertEquals(-9954.0, a.determinant(), 1e-5);
    }

    @Test
    public void testDeterminant_4x4_2() {
        Matrix a = m(a(1.0, 2.0, 3.0, 4.0),
                     a(1.0, 1.0, 1.0, 1.0),
                     a(2.0, 3.0, 4.0, 5.0),
                     a(2.0, 2.0, 2.0, 2.0));

        Assert.assertEquals(0.0, a.determinant(), 1e-5);
    }

    @Test
    public void testDeterminant_7x7() {
        Matrix a = m(a(1.0, 6.0, -8.0, 5.0, -3.0, 41.0, -2.0),
                     a(-8.0, -5.0, 7.0, 23.0, -7.0, 12.0, -2.0),
                     a(8.0, 77.0, -65.0, 13.0, -8.0, 55.0, -47.0),
                     a(26.0, 27.0, -81.0, -1.0, 10.0, -48.0, -3.0),
                     a(0.0, 34.0, -79.0, 4.0, -1.0, 28.0, 6.0),
                     a(-5.0, 8.0, -20.0, 36.0, -12.0, -7.0, -10.0),
                     a(-6.0, 13.0, 9.0, -4.0, 95.0, 2.0, 46.0));

        Assert.assertEquals(-9134649369.0, a.determinant(), 1e-5);
    }

    @Test
    public void testDeterminant_6x6() {
        Matrix a = m(a(5.0, 89.0, 6.0, 23.0, 6.0, 4.0),
                     a(4.0, 0.0, 27.0, 90.0, 42.0, 12.0),
                     a(6.0, 0.0, 24.0, 9.0, 41.0, 15.0),
                     a(31.0, 0.0, 3.0, 22.0, 2.0, 1.0),
                     a(2.0, 0.0, 37.0, 4.0, 0.0, 21.0),
                     a(23.0, 8.0, 7.0, 0.0, 12.0, 10.0));

        Assert.assertEquals(-1695428964.0, a.determinant(), 1e-5);
    }

    @Test
    public void testDeterminant_5x5() {
        Matrix a = m(a(-10.0, 89.0, -6.0, 23.0, 6.0, 4.0),
                     a(4.0, 5.0, 27.0, 90.0, 0.0, 12.0),
                     a(6.0, 0.0, 0.0, 9.0, 1.0, 15.0),
                     a(31.0, 11.0, 0.0, 12.0, 2.0, 1.0),
                     a(2.0, 0.0, 5.0, 4.0, -1.0, -21.0),
                     a(3.0, -18.0, 7.0, 0.0, 12.0, 10.0));

        Assert.assertEquals(3180462.0, a.determinant(), 1e-5);
    }

    @Test
    public void testRank_3x3() {
        Matrix a = m(a(1.0, 0.0, 0.0),
                     a(0.0, 5.0, 0.0),
                     a(0.0, 0.0, 9.0));

        Assert.assertEquals(3, a.rank());
    }

    @Test
    public void testRank_4x4() {
        Matrix a = m(a(1.0, 0.0, 0.0, 3.5),
                     a(0.0, 0.0, 0.0, 2.0),
                     a(0.0, 0.0, 9.0, 0.0),
                     a(1.0, 0.0, 0.0, 0.0));

        Assert.assertEquals(3, a.rank());
    }

    @Test
    public void testRank_4x4_2() {
        Matrix a = m(a(1.0, 2.0, 3.0, 4.0),
                     a(1.0, 1.0, 1.0, 1.0),
                     a(2.0, 3.0, 4.0, 5.0),
                     a(2.0, 2.0, 2.0, 2.0));

        Assert.assertEquals(2, a.rank());
    }

    @Test
    public void testRank_2x4() {
        Matrix a = m(a(1.0, 0.0, 0.0, 3.5),
                     a(0.0, 1.3, 0.0, 2.0));

        Assert.assertEquals(2, a.rank());
    }

    @Test
    public void testRank_5x3() {
        Matrix a = m(a(1.0, 0.0, 0.0),
                     a(0.0, 0.0, 0.0),
                     a(1.0, 0.0, 9.0),
                     a(1.0, 0.0, 0.0),
                     a(1.0, 0.0, 0.0));

        Assert.assertEquals(2, a.rank());
    }

    @Test
    public void testRank_10x10() {
        Matrix a = m(a(15, 6, 84, 67, 4, 11, 81, 47, 57, 41),
                     a(21, 94, 86, 55, 16, 31, 60, 62, 33, 61),
                     a(3, 32, 57, 56, 55, 66, 70, 87, 6, 37),
                     a(71, 48, 8, 24, 43, 61, 56, 24, 37, 63),
                     a(79, 45, 36, 20, 13, 96, 31, 77, 67, 54),
                     a(20, 63, 41, 79, 46, 100, 90, 23, 42, 94),
                     a(15, 10, 36, 18, 25, 81, 76, 29, 23, 53),
                     a(43, 79, 60, 94, 26, 24, 50, 38, 53, 12),
                     a(100, 59, 26, 99, 72, 17, 29, 3, 76, 14),
                     a(18, 38, 141, 123, 59, 77, 151, 134, 63, 78));

        Assert.assertEquals(9, a.rank());
    }

    @Test
    public void testRank_3x6() {
        Matrix a = m(a(5.0, 7.0, 10.0, 3.0, 5.0, 8.0),
                     a(5.0, 2.0, 3.0, 10.0, 11.0, 9.0),
                     a(4.0, 3.0, 9.0, 12.0, 8.0, 9.0));

        Assert.assertEquals(3, a.rank());
    }

    @Test
    public void testRank_7x11() {
        Matrix a = m(a(0.0, 13.0, 25.0, 43.0, 81.0, 0.0, 39.0, 60.0, 70.0, 21.0, 44.0, 0.0),
                     a(44.0, 0.0, 13.0, 67.0, 35.0, 0.0, 84.0, 35.0, 23.0, 88.0, 11.0, 0.0),
                     a(5.0, 34.0, 0.0, 143.0, 35.0, 0.0, 65.0, 99.0, 22.0, 13.0, 26.0, 0.0),
                     a(89.0, 23.0, 13.0, 0.0, 78.0, 0.0, 13.0, 24.0, 98.0, 65.0, 0.0, 0.0),
                     a(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0),
                     a(56.0, 4.0, 24.0, 56.0, 78.0, 0.0, 13.0, 0.0, 24.0, 57.0, 8.0, 1.0),
                     a(0.0, 0.0, 46.0, 666.0, 34.0, 13.0, 67.0, 9.0, 12.0, 45.0, 38.0, 0.0));

        Assert.assertEquals(6, a.rank());
    }

    @Test
    public void testRank_1x4() {
        Matrix a = m(a(0.0, 1.0, 0.0, 0.0));
        Assert.assertEquals(1, a.rank());
    }

    @Test
    public void testRank_3x3_empty() {
        Matrix a = mz(3, 3);
        Assert.assertEquals(0, a.rank());
    }

    @Test
    public void testRank_0x0() {
        Matrix a = mz(0, 0);
        Assert.assertEquals(0, a.rank());
    }

    @Test
    public void testRank_7x3() {
        Matrix a = m(a(1, 2, 0),
                     a(0, 0, 0),
                     a(0, 0, 0),
                     a(0, 0, 0),
                     a(0, 0, 1),
                     a(0, 0, -1),
                     a(1, 2, 1));

        Assert.assertEquals(2, a.rank());
    }

    @Test
    public void testRank_3x1() {
        Matrix a = m(a(1),
                     a(2),
                     a(0));

        Assert.assertEquals(1, a.rank());
    }

    @Test
    public void testRowAccess_2x1() {
        Matrix a = m(a(99.0),
                     a(88.0));

        Matrix b = m(a(99.0),
                     a(99.0));

        a.setRow(1, a.getRow(0));

        Assert.assertEquals(b, a);
    }

    @Test
    public void testRowAccess_3x3() {
        Matrix a = m(a(1.0, 0.0, 0.0),
                     a(0.0, 5.0, 0.0),
                     a(0.0, 0.0, 9.0));

        Matrix b = m(a(1.0, 0.0, 0.0),
                     a(0.0, 5.0, 0.0),
                     a(1.0, 0.0, 0.0));

        a.setRow(2, a.getRow(0));

        Assert.assertEquals(b, a);
    }

    @Test
    public void testRowAccess_2x4() {
        Matrix a = m(a(1.0, 0.0, 4.0, 0.0),
                     a(0.0, 5.0, 0.0, 7.0));

        Matrix b = m(a(1.0, 0.0, 4.0, 0.0),
                     a(1.0, 0.0, 4.0, 0.0));

        a.setRow(1, a.getRow(0));

        Assert.assertEquals(b, a);
    }

    @Test
    public void testRowAccess_5x3() {
        Matrix a = m(a(1.0, 0.0, 4.0),
                     a(0.0, 5.0, 3.0),
                     a(9.0, 0.0, 0.0),
                     a(0.0, 1.0, 8.0),
                     a(2.0, 0.0, 0.0));

        Matrix b = m(a(1.0, 0.0, 4.0),
                     a(0.0, 5.0, 3.0),
                     a(9.0, 0.0, 0.0),
                     a(9.0, 0.0, 0.0),
                     a(2.0, 0.0, 0.0));

        a.setRow(3, a.getRow(2));

        Assert.assertEquals(b, a);
    }

    @Test
    public void testRowAccess_6x4() {
        Matrix a = m(a(0.0, 18.0, 15.0, 0.0),
                     a(1.0, 0.0, -55.0, 9.0),
                     a(0.0, 0.0, 71.0, 19.0),
                     a(-1.0, -8.0, 54.0, 0.0),
                     a(25.0, 18.0, 0.0, 0.0),
                     a(78.0, 28.0, 0.0, -8.0));

        Matrix b = m(a(0.0, 18.0, 15.0, 0.0),
                     a(1.0, 0.0, -55.0, 9.0),
                     a(0.0, 0.0, 71.0, 19.0),
                     a(-1.0, -8.0, 54.0, 0.0),
                     a(25.0, 18.0, 0.0, 0.0),
                     a(25.0, 18.0, 0.0, 0.0));

        a.setRow(5, a.getRow(4));

        Assert.assertEquals(b, a);
    }

    @Test
    public void testInsertRow_2x3() {
        Matrix a = m(a(1.0, 0.0, 4.0),
                     a(9.0, 0.0, 0.0));

        Vector row = v(a(0.0, 5.0, 3.0));

        Matrix b = m(a(1.0, 0.0, 4.0),
                     a(0.0, 5.0, 3.0),
                     a(9.0, 0.0, 0.0));

        Assert.assertEquals(b, a.insertRow(1, row));
    }

    @Test
    public void testInsertColumn_3x2() {
        Matrix a = m(a(1.0, 4.0),
                     a(0.0, 3.0),
                     a(9.0, 0.0));

        Vector column = v(a(0.0, 5.0, 0.0));

        Matrix b = m(a(1.0, 0.0, 4.0),
                     a(0.0, 5.0, 3.0),
                     a(9.0, 0.0, 0.0));

        Assert.assertEquals(b, a.insertColumn(1, column));
    }

    @Test
    public void testRemoveRow_3x3() {
        Matrix a = m(a(1.0, 0.0, 4.0),
                     a(0.0, 5.0, 3.0),
                     a(9.0, 0.0, 0.0));

        Matrix b = m(a(1.0, 0.0, 4.0),
                     a(9.0, 0.0, 0.0));

        Assert.assertEquals(b, a.removeRow(1));
    }

    @Test
    public void testRemoveColumn_3x3() {
        Matrix a = m(a(1.0, 0.0, 4.0),
                     a(0.0, 5.0, 3.0),
                     a(9.0, 0.0, 0.0));

        Matrix b = m(a(1.0, 4.0),
                     a(0.0, 3.0),
                     a(9.0, 0.0));

        Assert.assertEquals(b, a.removeColumn(1));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testRemoveRow_3x3_Exception() {
        Matrix a = m(a(1.0, 0.0, 4.0),
                     a(0.0, 5.0, 3.0),
                     a(9.0, 0.0, 0.0));

       a.removeRow(3);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testRemoveColumn_3x3_Exception() {
        Matrix a = m(a(1.0, 0.0, 4.0),
                     a(0.0, 5.0, 3.0),
                     a(9.0, 0.0, 0.0));

        a.removeColumn(3);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testRemoveRow_empty() {
        Matrix a = mz(0, 0);
        a.removeFirstRow();
    }

    /**
     * Returns true if both matrices contain the same elements and have equal
     * dimensions.
     *
     * @param matrix1 Matrix 1
     * @param matrix2 Matrix 2
     * @return True if both matrices contain the same elements and have equal
     * dimensions.
     */
    public boolean testWhetherMatricesContainSameElements(Matrix matrix1,
                                                          Matrix matrix2) {

        // Test for equal columns and rows
        if (matrix1.rows() != matrix2.rows()) {
            return false;
        }
        if (matrix1.columns() != matrix2.columns()) {
            return false;
        }

        // Test for same elements
        double[] a = new double[matrix1.columns() * matrix1.rows()];
        int k = 0;
        for (int i = 0; i < matrix1.rows(); i++) {
            for (int j = 0; j < matrix1.columns(); j++) {
                a[k] = matrix1.get(i, j);
                k++;
            }
        }
        ArrayList<Double> b = new ArrayList<Double>();
        for (int i = 0; i < matrix2.rows(); i++) {
            for (int j = 0; j < matrix2.columns(); j++) {
                b.add(matrix2.get(i, j));
            }
        }
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < b.size(); j++) {
                if (a[i] == b.get(j)) {
                    b.remove(j); // If match found, remove it from ArrayList to
                    // decrease complexity
                    break;
                }
            }
        }

        return b.size() == 0;
    }

    @Test
    public void testTestWhetherMatricesContainSameElements() {
        Matrix a = m(a(1.0, 1.0, 3.0),
                      a(4.0, 5.0, 6.0),
                      a(7.0, 8.0, 9.0));

        Matrix b = m(a(1.0, 1.0, 4.0),
                      a(5.0, 6.0, 9.0),
                      a(7.0, 3.0, 8.0));

        Assert.assertTrue(testWhetherMatricesContainSameElements(b, a));

        Matrix c = m(a(1.0, 1.0, 3.0),
                     a(4.0, 52.0, 6.0),
                     a(7.0, 8.0, 9.0));

        Assert.assertFalse(testWhetherMatricesContainSameElements(c, a));
    }

    @Test
    public void testShuffle_3x2() {
        Matrix a = m(a(1.0, 2.0),
                     a(4.0, 5.0),
                     a(7.0, 8.0));

        Matrix b = a.shuffle();

        Assert.assertTrue(testWhetherMatricesContainSameElements(b, a));
    }

    @Test
    public void testShuffle_5x3() {
        Matrix a = m(a(1.0, 2.0, 3.0),
                      a(4.0, 5.0, 6.0),
                      a(7.0, 8.0, 9.0),
                      a(10.0, 11.0, 12.0),
                      a(13.0, 14.0, 15.0));

        Matrix b = a.shuffle();

        Assert.assertTrue(testWhetherMatricesContainSameElements(b, a));
    }

    @Test
    public void testRotate_3x1() {
        Matrix a = m(a(1.0),
                      a(3.0),
                      a(5.0));

        Matrix b = m(a(5.0, 3.0, 1.0));

        Assert.assertEquals(b, a.rotate());
    }

    @Test
    public void testRotate_2x2() {
        Matrix a = m(a(1.0, 2.0),
                      a(3.0, 4.0));

        Matrix b = m(a(3.0, 1.0),
                      a(4.0, 2.0));

        Assert.assertEquals(b, a.rotate());
    }

    @Test
    public void testRotate_2x4() {
        Matrix a = m(a(1.0, 2.0, 3.0, 4.0),
                      a(5.0, 6.0, 7.0, 8.0));

        Matrix b = m(a(5.0, 1.0),
                      a(6.0, 2.0),
                      a(7.0, 3.0),
                      a(8.0, 4.0));

        Assert.assertEquals(b, a.rotate());
    }

    @Test
    public void testRotate_5x3() {
        Matrix a = m(a(1.0, 2.0, 3.0),
                      a(4.0, 5.0, 6.0),
                      a(7.0, 8.0, 9.0),
                      a(10.0, 11.0, 12.0),
                      a(13.0, 14.0, 15.0));

        Matrix b = m(a(13.0, 10.0, 7.0, 4.0, 1.0),
                      a(14.0, 11.0, 8.0, 5.0, 2.0),
                      a(15.0, 12.0, 9.0, 6.0, 3.0));

        Assert.assertEquals(b, a.rotate());
    }

    @Test
    public void testColumnAccess_2x1() {
        Matrix a = m(a(11.0, 22.0));
        Matrix b = m(a(22.0, 22.0));

        a.setColumn(0, a.getColumn(1));

        Assert.assertEquals(b, a);
    }

    @Test
    public void testColumnAccess_3x3() {
        Matrix a = m(a(1.0, 0.0, 0.0),
                     a(0.0, 5.0, 0.0),
                     a(0.0, 0.0, 9.0));

        Matrix b = m(a(1.0, 0.0, 1.0),
                     a(0.0, 5.0, 0.0),
                     a(0.0, 0.0, 0.0));

        a.setColumn(2, a.getColumn(0));

        Assert.assertEquals(b, a);
    }

    @Test
    public void testColumnAccess_2x4() {
        Matrix a = m(a(1.0, 0.0, 0.0, 4.0),
                     a(0.0, 5.0, 0.0, 9.0));

        Matrix b = m(a(1.0, 0.0, 0.0, 1.0),
                     a(0.0, 5.0, 0.0, 0.0));

        a.setColumn(3, a.getColumn(0));

        Assert.assertEquals(b, a);
    }

    @Test
    public void testColumnAccess_5x3() {
        Matrix a = m(a(1.0, 0.0, 0.0),
                     a(0.0, 5.0, 6.0),
                     a(3.0, 0.0, 4.0),
                     a(0.0, 0.0, 0.0),
                     a(2.0, 7.0, 0.0));

        Matrix b = m(a(1.0, 1.0, 0.0),
                     a(0.0, 0.0, 6.0),
                     a(3.0, 3.0, 4.0),
                     a(0.0, 0.0, 0.0),
                     a(2.0, 2.0, 0.0));

        a.setColumn(1, a.getColumn(0));

        Assert.assertEquals(b, a);
    }

    @Test
    public void testColumnAccess_6x4() {
        Matrix a = m(a(0.0, 18.0, 15.0, 0.0),
                     a(1.0, 0.0, -55.0, 9.0),
                     a(0.0, 0.0, 71.0, 19.0),
                     a(-1.0, -8.0, 54.0, 0.0),
                     a(25.0, 18.0, 0.0, 0.0),
                     a(78.0, 28.0, 0.0, -8.0));

        Matrix b = m(a(0.0, 18.0, 15.0, 0.0),
                     a(1.0, 0.0, -55.0, 1.0),
                     a(0.0, 0.0, 71.0, 0.0),
                     a(-1.0, -8.0, 54.0, -1.0),
                     a(25.0, 18.0, 0.0, 25.0),
                     a(78.0, 28.0, 0.0, 78.0));
        a.setColumn(3, a.getColumn(0));

        Assert.assertEquals(b, a);
    }

    @Test
    public void testCopy_3x3() {
        Matrix a = m(a(1.0, 2.0, 3.0),
                     a(4.0, 5.0, 6.0),
                     a(7.0, 8.0, 9.0));

        Assert.assertEquals(a, a.copy());
    }

    @Test
    public void testBlank_3x3() {
        Matrix a = m(a(0.0, 0.0, 3.0),
                     a(0.0, 0.0, 6.0),
                     a(0.0, 0.0, 9.0));

        Matrix b = m(a(0.0, 0.0, 0.0),
                     a(0.0, 0.0, 0.0),
                     a(0.0, 0.0, 0.0));

        Assert.assertEquals(b, a.blank());
    }
    
    @Test
    public void testNormalize_EuclideanNormAccumulator() {
        Matrix a = m(a(0.0, 3.0), a(4.0, 0.0));

        Assert.assertEquals(5.0, a.euclideanNorm(), Matrices.EPS);
    }
    
    @Test
    public void testNormalize_ManhattanNormAccumulator() {
        Matrix a = m(a(0.0, 3.0), a(4.0, 0.0));

        Assert.assertEquals(7.0, a.manhattanNorm(), Matrices.EPS);
    }
    
    @Test
    public void testNormalize_InfinityNormAccumulator() {
        Matrix a = m(a(0.0, 3.0), a(4.0, 0.0));

        Assert.assertEquals(4.0, a.infinityNorm(), Matrices.EPS);
    }

    @Test
    public void testPower_2x2() {
        Matrix a = m(a(1.0, 2.0),
                     a(3.0, 4.0));

        Matrix b = m(a(7.0, 10.0),
                     a(15.0, 22.0));

        Matrix c = a.power(2);
        Assert.assertEquals(b, c);

        Matrix d = m(a(37.0, 54.0),
                     a(81.0, 118.0));

        Matrix e = a.power(3);
        Assert.assertEquals(d, e);

        Matrix f = m(a(5743.0, 8370.0),
                     a(12555.0, 18298.0));

        Matrix g = a.power(6);
        Assert.assertEquals(f, g);
    }

    @Test
    public void testPower_3x3() {
        Matrix h = m(a(1.0, 0.0, 0.0),
                     a(4.0, 3.0, 6.0),
                     a(0.0, 0.0, 9.0));

        Matrix i = m(a(1.0, 0.0, 0.0),
                     a(160.0, 81.0, 6480.0),
                     a(0.0, 0.0, 6561.0));

        Matrix j = h.power(4);
        Assert.assertEquals(i, j);

        Matrix k = h.power(1);
        Assert.assertEquals(h, k);
    }

    @Test
    public void testMax() {
        Matrix a = m(a(0, 0, -1),
                     a(0, -3, 0),
                     a(6, -7, -2));

        Assert.assertEquals(6.0, a.max(), Matrices.EPS);
    }

    @Test
    public void testMaxCompressed() {
        Matrix a = m(a(0, 0, -1),
                     a(0, -3, 0),
                     a(0, -7, -2));

        Assert.assertEquals(0.0, a.max(), Matrices.EPS);
    }

    @Test
    public void testMinCompressed() {
        Matrix a = m(a(0, 0, 1),
                     a(0, 3, 0),
                     a(0, 7, 2));

        Assert.assertEquals(0.0, a.min(), Matrices.EPS);
    }

    @Test
    public void testMin() {
        Matrix a = m(a(0, 0, -1),
                     a(0, -3, 0),
                     a(0, -7, -2));

        Assert.assertEquals(-7.0, a.min(), Matrices.EPS);
    }

    @Test
    public void testMaxInRow() {
        Matrix a = m(a(0, 0, 1, 0),
                     a(-3, 2, 0, 1),
                     a(-2, 0, 0, -1));

        Assert.assertEquals(0.0, a.maxInRow(2), Vectors.EPS);
    }

    @Test
    public void testMinInRow() {
        Matrix a = m(a(0, 0, 1, 0),
                     a(-3, 2, 0, 1),
                     a(2, 0, 0, 1));

        Assert.assertEquals(0.0, a.minInRow(2), Vectors.EPS);
    }

    @Test
    public void testMaxInColumn() {
        Matrix a = m(a(0, 0, 1, 0),
                     a(-3, 2, 0, 1),
                     a(-2, 0, 0, -1));

        Assert.assertEquals(0.0, a.maxInColumn(0), Vectors.EPS);
    }

    @Test
    public void testMinInColumn() {
        Matrix a = m(a(0, 0, 1, 0),
                     a(-3, 2, 0, 1),
                     a(-2, 0, 0, -1));

        Assert.assertEquals(-1.0, a.minInColumn(3), Vectors.EPS);
    }

    private Matrix matrixA() {
        return m(a(8.93, 5.88, 6.57, 9.3, 3.65, 0.08),
                 a(3.96, 8.26, 2.51, 9.61, 4.63, 6.12),// 1
                 a(7.37, 5.79, 8.8, 0.87, 7.83, 6.15),// 2
                 a(3.43, 9.08, 1.16, 2.3, 3.66, 4.93),// 3
                 a(7.05, 7.75, 8.11, 2.93, 9.04, 6.72)); // 4
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testSelect1() {
        // Throw exception when row indices are invalid
        Matrix a = matrixA();
        int[] rowInd = new int[]{3, 4, 10}; // 10 is too large of a row index
        int[] colInd = new int[]{0, 1, 2};
        a.select(rowInd, colInd);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testSelect2() {
        // Throw exception when column indices are invalid
        Matrix a = matrixA();
        int[] rowInd = new int[]{0, 1, 2};
        int[] colInd = new int[]{-1, 1, 2}; // -1 is a negative column index
        a.select(rowInd, colInd);
    }

    @Test
    public void testSelect3() {
        // All columns and a subset of rows selected.
        Matrix a = matrixA();
        int[] rowInd = new int[]{1, 3, 4};
        int[] colInd = new int[]{0, 1, 2, 3, 4, 5}; // all columns

        Matrix b = m(a(3.96, 8.26, 2.51, 9.61, 4.63, 6.12),// 1
                     a(3.43, 9.08, 1.16, 2.3, 3.66, 4.93),// 3
                     a(7.05, 7.75, 8.11, 2.93, 9.04, 6.72));// 4

        Assert.assertEquals(b, a.select(rowInd, colInd));
    }

    @Test
    public void testSelect4() {
        // All rows and a subset of columns selected.
        Matrix a = matrixA();
        int[] rowInd = new int[]{0, 1, 2, 3, 4};
        int[] colInd = new int[]{0, 2, 4, 5}; // all columns

        Matrix c = m(a(8.93, 6.57, 3.65, 0.08),// 0
                     a(3.96, 2.51, 4.63, 6.12),// 1
                     a(7.37, 8.8, 7.83, 6.15),// 2
                     a(3.43, 1.16, 3.66, 4.93),// 3
                     a(7.05, 8.11, 9.04, 6.72)); // 4

        Assert.assertEquals(c, a.select(rowInd, colInd));
    }

    @Test
    public void testSelect5() {
        // A subset of rows and columns is selected.
        Matrix a = matrixA();
        int[] rowInd = new int[]{1, 3, 4};
        int[] colInd = new int[]{2, 4, 5};

        Matrix d = m(a(2.51, 4.63, 6.12),
                     a(1.16, 3.66, 4.93),// 3
                     a(8.11, 9.04, 6.72)); // 4

        Assert.assertEquals(d, a.select(rowInd, colInd));
    }

    @Test
    public void testSelect6() {
        // Duplication of rows and columns.
        Matrix a = matrixA();
        int[] rowInd = new int[]{1, 3, 3, 4};
        int[] colInd = new int[]{2, 2, 4, 5, 5};

        Matrix d = m(a(2.51, 2.51, 4.63, 6.12, 6.12),// 1
                     a(1.16, 1.16, 3.66, 4.93, 4.93),// 3
                     a(1.16, 1.16, 3.66, 4.93, 4.93),// 3
                     a(8.11, 8.11, 9.04, 6.72, 6.72)); // 4

        Assert.assertEquals(d, a.select(rowInd, colInd));
    }

    @Test
    public void testFoldSum() {
        Matrix d = m(a(6.436, 4.808, 3.923, 5.866),
                     a(7.072, 5.899, 4.771, 3.537),
                     a(7.282, 0.636, 0.010, 3.673),
                     a(5.833, 0.201, 6.941, 5.914),
                     a(5.757, 6.807, 7.830, 2.601),
                     a(0.434, 1.996, 9.329, 1.115));

        Vector columnSums = v(32.814, 20.347, 32.804, 22.706);

        for (int col = 0; col < d.columns(); col++) {
            double sum = d.foldColumn(col, Vectors.asSumAccumulator(0.0));
            Assert.assertEquals(sum, columnSums.get(col), Matrices.EPS);
        }

        double[] s = d.foldColumns(Vectors.asSumAccumulator(0.0));
        Assert.assertEquals(DenseVector.fromArray(s), columnSums);

        Vector rowSums = v(21.033, 21.279, 11.601, 18.889, 22.995, 12.874);

        for (int row = 0; row < d.columns(); row++) {
            double sum = d.foldRow(row, Vectors.asSumAccumulator(0.0));
            Assert.assertEquals(sum, rowSums.get(row), Matrices.EPS);
        }

        s = d.foldRows(Vectors.asSumAccumulator(0.0));
        Assert.assertEquals(DenseVector.fromArray(s), rowSums);
    }

    @Test
    public void testDiagonalMatrixPredicate() {
        Matrix a = m(a(1.0, 0.0, 0.0),
                     a(0.0, 1.0, 0.0),
                     a(0.0, 0.0, 1.0));

        Assert.assertTrue(a.is(Matrices.DIAGONAL_MATRIX));

        Matrix b = m(a(0.0, 0.0, 0.0),
                     a(2.0, 1.0, 0.0),
                     a(0.0, 0.0, 1.0));

        Assert.assertFalse(b.is(Matrices.DIAGONAL_MATRIX));
    }

    @Test
    public void testIdentityMatrixPredicate() {
        Matrix a = m(a(1.0, 0.0, 0.0),
                     a(0.0, 1.0, 0.0),
                     a(0.0, 0.0, 1.0));

        Assert.assertTrue(a.is(Matrices.IDENTITY_MATRIX));

        Matrix b = m(a(0.0, 0.0, 0.0),
                     a(1.0, 0.0, 0.0),
                     a(0.0, 0.0, 1.0));

        Assert.assertFalse(b.is(Matrices.IDENTITY_MATRIX));
    }

    @Test
    public void testZeroMatrixPredicate() {
        Matrix a = m(a(0.0, 0.0, 0.0),
                     a(0.0, 0.0, 0.0));

        Assert.assertTrue(a.is(Matrices.ZERO_MATRIX));

        Matrix b = m(a(0.0, 0.0, 0.0),
                     a(0.0, 0.0, 0.0),
                     a(0.0, 0.0, 1.0));

        Assert.assertFalse(b.is(Matrices.ZERO_MATRIX));
    }

    @Test
    public void testTridiagonalMatrixPredicate() {
        Matrix a = m(a(0.0, 1.0, 0.0, 0.0),
                     a(1.0, 2.0, 3.0, 0.0),
                     a(0.0, 1.0, 0.0, 2.0),
                     a(0.0, 0.0, 1.0, 2.0));

        Assert.assertTrue(a.is(Matrices.TRIDIAGONAL_MATRIX));

        Matrix b = m(a(0.0, 1.0, 0.0, 0.0),
                     a(1.0, 2.0, 3.0, 0.0),
                     a(5.0, 0.0, 0.0, 2.0),
                     a(0.0, 0.0, 1.0, 2.0));

        Assert.assertFalse(b.is(Matrices.TRIDIAGONAL_MATRIX));
    }

    @Test
    public void testSymmetricMatrixPredicate() {
        Matrix a = m(a(0.0, 1.0, 0.0, 0.0),
                     a(1.0, 2.0, 3.0, 5.0),
                     a(0.0, 3.0, 0.0, 0.0),
                     a(0.0, 5.0, 0.0, 2.0));

        Assert.assertTrue(a.is(Matrices.SYMMETRIC_MATRIX));

        Matrix b = m(a(0.0, 0.0, 0.0, 0.0),
                     a(0.0, 2.0, 3.0, 0.0),
                     a(3.0, 3.0, 0.0, 0.0),
                     a(0.0, 0.0, 0.0, 2.0));

        Assert.assertFalse(b.is(Matrices.SYMMETRIC_MATRIX));
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void testGet_IndexCheck_RowNegative() {
        Matrix a = m(a(0.0, 0.0, 0.0, 1.0, 0.0),
                     a(0.0, 0.0, 3.0, 0.0, 0.0));

        a.get(-1, 1);
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void testGet_IndexCheck_ColumnNegative() {
        Matrix a = m(a(0.0, 0.0, 0.0, 1.0, 0.0),
                     a(0.0, 0.0, 3.0, 0.0, 0.0));

        a.get(1, -1);
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void testGet_IndexCheck_RowTooLarge() {
         Matrix a = m(a(0.0, 0.0, 0.0, 1.0, 0.0),
                      a(0.0, 0.0, 3.0, 0.0, 0.0));

        a.get(a.rows(), 1);
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void testGet_IndexCheck_ColumnTooLarge() {
        Matrix a = m(a(0.0, 0.0, 0.0, 1.0, 0.0),
                     a(0.0, 0.0, 3.0, 0.0, 0.0));

        a.get(1, a.columns());
    }
    
    @Test
    public void testGet_IndexCheck_Valid() {
        Matrix a = m(a(0.0, 0.0, 0.0, 1.0, 0.0),
                     a(0.0, 0.0, 3.0, 0.0, 0.0));

        Assert.assertEquals(0.0, a.get(1, 1), 0.0);
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void testSet_IndexCheck_RowNegative() {
        Matrix a = m(a(0.0, 0.0, 0.0, 1.0, 0.0),
                     a(0.0, 0.0, 3.0, 0.0, 0.0));

        a.set(-1, 1, 1.0);
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void testSet_IndexCheck_ColumnNegative() {
        Matrix a = m(a(0.0, 0.0, 0.0, 1.0, 0.0),
                     a(0.0, 0.0, 3.0, 0.0, 0.0));

        a.set(1, -1, 1.0);
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void testSet_IndexCheck_RowTooLarge() {
        Matrix a = m(a(0.0, 0.0, 0.0, 1.0, 0.0),
                     a(0.0, 0.0, 3.0, 0.0, 0.0));

        a.set(a.rows(), 1, 1.0);
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void testSet_IndexCheck_ColumnTooLarge() {
        Matrix a = m(a(0.0, 0.0, 0.0, 1.0, 0.0),
                     a(0.0, 0.0, 3.0, 0.0, 0.0));

        a.set(1, a.columns(), 1.0);
    }
    
    @Test
    public void testSet_IndexCheck_Valid() {
        Matrix a = m(a(0.0, 0.0, 0.0, 1.0, 0.0),
                     a(0.0, 0.0, 3.0, 0.0, 0.0));

        a.set(1, 1, 1.0);
        Assert.assertEquals(1.0, a.get(1, 1), 0.0);
    }
    @Test
    public void testEqualsWithPrecision() throws Exception {
        Matrix a = mz(0, 0);
        Assert.assertTrue(a.equals(a, Matrices.EPS));
        Assert.assertTrue(a.equals(a.copy(), Matrices.EPS));

        Matrix b = mz(2, 2);
        Matrix c = mz(2, 2);

        Assert.assertTrue(b.equals(c, Matrices.EPS));
        Assert.assertTrue(c.equals(b, Matrices.EPS));
        Assert.assertFalse(b.equals(a, Matrices.EPS));
        Assert.assertFalse(c.equals(a, Matrices.EPS));

        Matrix d = m(a(1.0, 0.0),
                     a(0.0, 1.0));

        Assert.assertFalse(d.equals(b, Matrices.EPS));
        Assert.assertFalse(d.equals(a, Matrices.EPS));
        
        Matrix e = m(a(Double.MIN_VALUE, Double.MIN_VALUE),
                     a(Double.MIN_VALUE, Double.MIN_VALUE));

        Assert.assertTrue(e.equals(b, Matrices.EPS));

        Matrix f = m(a(Double.MIN_NORMAL, Double.MIN_NORMAL),
                     a(Double.MIN_NORMAL, Double.MIN_NORMAL));

        Assert.assertTrue(f.equals(b, Matrices.EPS));
        Assert.assertTrue(f.equals(e, Matrices.EPS));
        Assert.assertTrue(b.equals(f, Matrices.EPS));
        Assert.assertTrue(e.equals(f, Matrices.EPS));
    }

    @Test
    public void testEquals() throws Exception {
        Matrix a = mz(0, 0);
        Assert.assertTrue(a.equals(a));
        Assert.assertTrue(a.copy().equals(a));

        Matrix b = mz(2, 2);
        Matrix c = mz(2, 2);

        Assert.assertTrue(b.equals(c));
        Assert.assertTrue(c.equals(b));
        Assert.assertFalse(b.equals(a));
        Assert.assertFalse(c.equals(a));

        Matrix d = m(a(1.0, 0.0),
                     a(0.0, 1.0));

        Assert.assertFalse(d.equals(b));
        Assert.assertFalse(d.equals(a));

        Matrix e = m(a(Double.MIN_VALUE, Double.MIN_VALUE),
                     a(Double.MIN_VALUE, Double.MIN_VALUE));

        Assert.assertTrue(e.equals(b));

        Matrix f = m(a(Double.MIN_NORMAL, Double.MIN_NORMAL),
                     a(Double.MIN_NORMAL, Double.MIN_NORMAL));

        Assert.assertTrue(f.equals(b));
        Assert.assertTrue(f.equals(e));
        Assert.assertTrue(b.equals(f));
        Assert.assertTrue(e.equals(f));
    }

    @Test
    public void testFromMatrixMarket() throws Exception {
        String mm = "%%MatrixMarket matrix coordinate real general\n" +
                "% This ASCII file represents a sparse 5x5 matrix with 8 \n" +
                "% nonzeros in the Matrix Market format.\n" +
                "%\n" +
                "  5  5  8\n" +
                "    1     1   1.000e+00\n" +
                "    2     2   1.050e+01\n" +
                "    3     3   1.500e-02\n" +
                "    1     4   6.000e+00\n" +
                "    4     2   2.505e+02\n" +
                "    4     4  -2.800e+02\n" +
                "    4     5   3.332e+01\n" +
                "    5     5   1.200e+01";

        RowMajorSparseMatrix matrix = RowMajorSparseMatrix.zero(5, 5, 8);
        matrix.set(0, 0, 1.000e+00);
        matrix.set(1, 1, 1.050e+01);
        matrix.set(2, 2, 1.500e-02);
        matrix.set(0, 3, 6.000e+00);
        matrix.set(3, 1, 2.505e+02);
        matrix.set(3, 3, -2.800e+02);
        matrix.set(3, 4, 3.332e+01);
        matrix.set(4, 4, 1.200e+01);

        Matrix from_mm_matrix = Matrix.fromMatrixMarket(mm);
        Assert.assertNotNull(from_mm_matrix);
        Assert.assertTrue(matrix.equals(from_mm_matrix));

        String bcsstm02_mm = "%%MatrixMarket matrix coordinate real general\n" +
                "16 16 15\n" +
                "1 1  9.2138580510000e-02\n" +
                "2 2  9.2138580510000e-02\n" +
                "3 3  9.2138580510000e-02\n" +
                "4 4  1.3799573798300e-01\n" +
                "5 5  1.3799573798300e-01\n" +
                "6 6  1.3799573798300e-01\n" +
                "7 7  1.3799573798300e-01\n" +
                "8 8  1.3799573798300e-01\n" +
                "9 9  1.3799573798300e-01\n" +
                "10 10  9.2138580510000e-02\n" +
                "11 11  9.2138580510000e-02\n" +
                "12 12  9.2138580510000e-02\n" +
                "13 13  1.7282857345500e-01\n" +
                "14 14  1.7282857345500e-01\n" +
                "15 15  1.7282857345500e-01\n";

        RowMajorSparseMatrix bcs_matrix = RowMajorSparseMatrix.zero(16, 16, 15);
        bcs_matrix.set(0, 0, 9.2138580510000e-02);
        bcs_matrix.set(1, 1, 9.2138580510000e-02);
        bcs_matrix.set(2, 2, 9.2138580510000e-02);
        bcs_matrix.set(3, 3, 1.3799573798300e-01);
        bcs_matrix.set(4, 4, 1.3799573798300e-01);
        bcs_matrix.set(5, 5, 1.3799573798300e-01);
        bcs_matrix.set(6, 6, 1.3799573798300e-01);
        bcs_matrix.set(7, 7, 1.3799573798300e-01);
        bcs_matrix.set(8, 8, 1.3799573798300e-01);
        bcs_matrix.set(9, 9, 9.2138580510000e-02);
        bcs_matrix.set(10, 10, 9.2138580510000e-02);
        bcs_matrix.set(11, 11, 9.2138580510000e-02);
        bcs_matrix.set(12, 12, 1.7282857345500e-01);
        bcs_matrix.set(13, 13, 1.7282857345500e-01);
        bcs_matrix.set(14, 14, 1.7282857345500e-01);

        Matrix bcs_mm_matrix = Matrix.fromMatrixMarket(bcsstm02_mm);

        Assert.assertNotNull(bcs_mm_matrix);
        Assert.assertTrue(bcs_matrix.equals(bcs_mm_matrix));
    }
}
