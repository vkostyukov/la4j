package org.la4j;

import java.util.Arrays;

public final class VnM {

    public static Vector v(double... values) {
        return Vector.fromArray(values);
    }

    public static Iterable<Vector> vs(double... values) {
        return Arrays.asList(
            v(values).to(Vectors.BASIC),
            v(values).to(Vectors.COMPRESSED)
        );
    }

    public static Matrix m2x4(double... values) {
        return Matrix.from1DArray(2, 4, values);
    }

    public static Iterable<Matrix> ms2x4(double... values) {
        return Arrays.asList(
            m2x4(values).to(Matrices.CRS),
            m2x4(values).to(Matrices.CCS),
            m2x4(values).to(Matrices.BASIC_1D),
            m2x4(values).to(Matrices.BASIC_2D)
        );
    }

    public static Matrix m3x1(double... values) {
        return Matrix.from1DArray(3, 1, values);
    }

    public static Iterable<Matrix> ms3x1(double... values) {
        return Arrays.asList(
            m3x1(values).to(Matrices.CRS),
            m3x1(values).to(Matrices.CCS),
            m3x1(values).to(Matrices.BASIC_1D),
            m3x1(values).to(Matrices.BASIC_2D)
        );
    }

    public static Matrix m3x4(double... values) {
        return Matrix.from1DArray(3, 4, values);
    }

    public static Iterable<Matrix> ms3x4(double... values) {
        return Arrays.asList(
            m3x4(values).to(Matrices.CRS),
            m3x4(values).to(Matrices.CCS),
            m3x4(values).to(Matrices.BASIC_1D),
            m3x4(values).to(Matrices.BASIC_2D)
        );
    }

    public static Matrix m1x2(double... values) {
        return Matrix.from1DArray(1, 2, values);
    }

    public static Iterable<Matrix> ms1x2(double... values) {
        return Arrays.asList(
            m1x2(values).to(Matrices.CRS),
            m1x2(values).to(Matrices.CCS),
            m1x2(values).to(Matrices.BASIC_1D),
            m1x2(values).to(Matrices.BASIC_2D)
        );
    }

    public static Matrix m4x2(double... values) {
        return Matrix.from1DArray(4, 2, values);
    }

    public static Iterable<Matrix> ms4x2(double... values) {
        return Arrays.asList(
            m4x2(values).to(Matrices.CRS),
            m4x2(values).to(Matrices.CCS),
            m4x2(values).to(Matrices.BASIC_1D),
            m4x2(values).to(Matrices.BASIC_2D)
        );
    }
}
