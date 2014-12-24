package org.la4j.matrix.converter;

import org.junit.Test;
import org.la4j.LinearAlgebra;
import org.la4j.matrix.Matrices;
import org.la4j.matrix.Matrix;
import org.la4j.matrix.MatrixFactory;

import static org.junit.Assert.assertTrue;

public class MatrixConverterTest {

    @Test
    public void testConverter_empty() throws Exception {
        Matrix m = LinearAlgebra.DEFAULT_FACTORY.createMatrix();
        performTest(m);
    }

    @Test
    public void testConverter_1x1() throws Exception {
        Matrix m = LinearAlgebra.DEFAULT_FACTORY.createMatrix(new double[][]{
                {0.5}
        });
        performTest(m);
    }

    @Test
    public void testConverter_2x1() throws Exception {
        Matrix m = LinearAlgebra.DEFAULT_FACTORY.createMatrix(new double[][]{
                {0.5},
                {-1.0}
        });
        performTest(m);
    }

    @Test
    public void testConverter_1x2() throws Exception {
        Matrix m = LinearAlgebra.DEFAULT_FACTORY.createMatrix(new double[][]{
                {0.5, -1.0}
        });
        performTest(m);
    }

    @Test
    public void testConverter_2x2() throws Exception {
        Matrix m = LinearAlgebra.DEFAULT_FACTORY.createMatrix(new double[][]{
                {0.5, 1.7},
                {-1.0, 2}
        });
        performTest(m);
    }

    @Test
    public void testConverter_3x3() throws Exception {
        Matrix m = LinearAlgebra.DEFAULT_FACTORY.createMatrix(new double[][]{
                {0.5, 1.7, 0},
                {-1.0, 2, -0.5},
                {-1.0, 2, -0.5}
        });
        performTest(m);
    }

    @Test
    public void testConverter_3x4() throws Exception {
        Matrix m = LinearAlgebra.DEFAULT_FACTORY.createMatrix(new double[][]{
                {0.5, 1.7, 0, 1e7},
                {-1.0, 2, -0.5, 1e-7},
                {-1.0, 2, -0.5, 0}
        });
        performTest(m);
    }

    @Test
    public void testConverter_4x3() throws Exception {
        Matrix m = LinearAlgebra.DEFAULT_FACTORY.createMatrix(new double[][]{
                {0.5, 1.7, 0},
                {-1.0, 2, -0.5},
                {-1.0, 2, -0.5},
                {-1e8, 2e-7, 4}
        });
        performTest(m);
    }

    private void performTest(Matrix m) {
        for (MatrixFactory converter : Matrices.CONVERTERS) {
            assertTrue(m.to(converter).equals(m));
        }
    }
}
