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
 * Contributor(s): -
 * 
 */

package org.la4j.linear;

import java.io.Serializable;

import org.la4j.factory.Factory;
import org.la4j.matrix.Matrix;
import org.la4j.vector.Vector;

/**
 * Linear System Solver interface;
 * This class implements Strategy design pattern;
 */
public interface LinearSystemSolver extends Serializable {

    /**
     * Solves the system A*x = b.
     *
     * @param b
     * @return
     */
    Vector solve(Vector b);

    /**
     * Solves the system A*x = b.
     *
     * @param b
     * @param factory
     * @return
     */
    Vector solve(Vector b, Factory factory);

    /**
     * Returns the self matrix of the solver.
     *
     * @return
     */
    Matrix self();

    /**
     * Returns the number os unknowns in this solver.
     *
     * @return
     */
    int unknowns();

    /**
     * Returns the number of equations in this solver.
     *
     * @return
     */
    int equations();

    /**
     * Checks whether this solver applicable to given {@code matrix} or not.
     *
     * @param matrix
     */
    boolean applicableTo(Matrix matrix);
}
