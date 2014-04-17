package org.la4j.vector;

import java.util.Iterator;

public interface VectorIterator extends Iterator<Double> {
    int index();
    double value();
    void mutate(double value);
    void jump(int index);
}
