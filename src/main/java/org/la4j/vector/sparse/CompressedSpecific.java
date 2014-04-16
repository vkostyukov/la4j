package org.la4j.vector.sparse;

public interface CompressedSpecific {
    int[] indices();
    double[] values();
    int cardinality();

    void mutate(int indices[], double values[], int cardinality);
}
