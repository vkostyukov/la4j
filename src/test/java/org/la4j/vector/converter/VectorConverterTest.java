package org.la4j.vector.converter;

import org.junit.Test;
import org.la4j.LinearAlgebra;
import org.la4j.vector.Vector;
import org.la4j.vector.VectorFactory;
import org.la4j.vector.Vectors;

import static org.junit.Assert.assertTrue;

public class VectorConverterTest {

    @Test
    public void testVectorConverter_empty() throws Exception {
        Vector vector = LinearAlgebra.DEFAULT_FACTORY.createVector(new double[]{

        });
        performTest(vector);
    }

    @Test
    public void testVectorConverter_1() throws Exception {
        Vector vector = LinearAlgebra.DEFAULT_FACTORY.createVector(new double[]{
                3
        });
        performTest(vector);
    }

    @Test
    public void testVectorConverter_2() throws Exception {
        Vector vector = LinearAlgebra.DEFAULT_FACTORY.createVector(new double[]{
                1e-8, 1e8
        });
        performTest(vector);
    }

    @Test
    public void testVectorConverter_3() throws Exception {
        Vector vector = LinearAlgebra.CCS_FACTORY.createVector(new double[]{
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0
        });
        performTest(vector);
    }

    @Test
    public void testVectorConverter_4() throws Exception {
        Vector vector = LinearAlgebra.CCS_FACTORY.createVector(new double[]{
                3, 4, 0, 0, 0, 0, 4, 0, 0, 4, 0
        });
        performTest(vector);
    }

    private void performTest(Vector vector) {
        for (VectorFactory converter : Vectors.FACTORIES) {
            assertTrue(vector.to(converter).equals(vector));
        }
    }
}
