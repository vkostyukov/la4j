package org.la4j.optimization;

import java.io.Serializable;

import org.la4j.factory.Factory;
import org.la4j.matrix.Matrix;
import org.la4j.vector.Vector;

/**
 * Linear System Optimizator interface;
 * This class implements Strategy design pattern;
 */
public interface LinearSystemOptimizer extends Serializable{
	/**
     * Optimize the system A*x = b.
     *
     * @param b
     * @return
     */
    Vector solve(Vector b);

    /**
     * Optimize the system A*x = b.
     *
     * @param b
     * @param factory
     * @return
     */
    Vector solve(Vector b, Factory factory);

    /**
     * Returns the self matrix of the optimizator.
     *
     * @return
     */
    Matrix self();

    /**
     * Returns the number of unknowns in this optimizator.
     *
     * @return
     */
    int unknowns();

    /**
     * Returns the number of equations in this optimizator.
     *
     * @return
     */
    int equations();

    /**
     * Checks whether this optimizator applicable to given {@code matrix} or not.
     *
     * @param matrix
     */
    boolean applicableTo(Matrix matrix);

}
