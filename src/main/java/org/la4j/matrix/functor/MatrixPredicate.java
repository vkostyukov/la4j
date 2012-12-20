package org.la4j.matrix.functor;

public interface MatrixPredicate {

    /**
     * 
     * @param i
     * @param j
     * @param value
     * @return
     */
    boolean test(int i, int j, double value); 
}
