package org.la4j;

import java.util.Arrays;

public class M {

    public static double[] a(double... values) {
        return values;
    }

    public static Matrix m(double[]... values) {
        return Matrix.from2DArray(values);
    }

    public static Iterable<Matrix> ms(double[]... values) {
        return Arrays.asList(
            m(values).to(Matrices.CCS),
            m(values).to(Matrices.CRS),
            m(values).to(Matrices.BASIC_1D),
            m(values).to(Matrices.BASIC_2D)
        );
    }

    public static Matrix mz(int rows, int columns) {
        return Matrix.zero(rows, columns);
    }
}
