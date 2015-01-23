package org.la4j;

import java.util.Arrays;

public final class V {

    public static Vector v(double... values) {
        return Vector.fromArray(values);
    }

    public static Iterable<Vector> vs(double... values) {
        return Arrays.asList(
            v(values).to(Vectors.BASIC),
            v(values).to(Vectors.COMPRESSED)
        );
    }

    public static Vector vz(int length) {
        return Vector.zero(length);
    }
}
