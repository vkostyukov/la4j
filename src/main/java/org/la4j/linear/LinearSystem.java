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

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.la4j.factory.Basic2DFactory;
import org.la4j.factory.Factory;
import org.la4j.matrix.Matrices;
import org.la4j.matrix.Matrix;
import org.la4j.vector.Vector;

/**
 * This class encapsulates the
 * <a href="http://mathworld.wolfram.com/MatrixEquation.html"> Linear System.</a>
 */
public class LinearSystem implements Externalizable {

    private static final LinearSystemSolver SOLVERS[] = {
        new SweepSolver(),
        new JacobiSolver(),
        new SeidelSolver(),
        new SquareRootSolver(),
        new GaussianSolver()
    };

    private int equations;
    private int variables;

    private Matrix a;
    private Vector b;

    private LinearSystemSolver solver;
    private Factory factory;

    public LinearSystem(Matrix a, Vector b) {
        this(a, b, new Basic2DFactory(), null);
    }

    public LinearSystem(Matrix a, Vector b, Factory factory) {
        this (a, b, factory, null);
    }

    public LinearSystem(Matrix a, Vector b, LinearSystemSolver solver) {
        this(a, b, new Basic2DFactory(), solver);
    }

    public LinearSystem(Matrix a, Vector b, Factory factory, 
        LinearSystemSolver solver) {

        this.a = a;
        this.b = b;

        this.equations = a.rows();
        this.variables = a.columns();

        this.factory = factory;

        if (solver == null) {
            solver = chooseEffectiveSolver();
        }

        this.solver = solver;
    }

    public int equations() {
        return equations;
    }

    public int variables() {
        return variables;
    }

    public Matrix coefficientsMatrix() {
        return a;
    }

    public Vector rightHandVector() {
        return b;
    }

    public Vector solve() {
        return solve(solver, factory);
    }

    public Vector solve(Factory factory) {
        return solve(solver, factory);
    }

    public Vector solve(LinearSystemSolver solver) {
        return solve(solver, factory);
    }

    public Vector solve(LinearSystemSolver solver, Factory factory) {
        return solver.solve(this, factory);
    }

    /**
     * Checks whether <code>vector</code> is solution for this linear system.
     */
    public boolean isSolution(Vector vector) {

        if (vector == null) {
            return false;
        }

        if (vector.length() != variables) {
            return false;
        }

        Vector r = innacary(vector);

        boolean result = true;
        for (int i = 0; i < r.length(); i++) {
            result = result && (Math.abs(r.get(i)) < Matrices.EPS);
        }

        return result;
    }

    private Vector innacary(Vector vector) {
        return a.multiply(vector).subtract(b);
    }

    private LinearSystemSolver chooseEffectiveSolver() {
        for (LinearSystemSolver solver: SOLVERS) {
            if (solver.suitableFor(this)) {
                return solver;
            }
        }

        return Matrices.DEFAULT_SOLVER;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {
        a = (Matrix) in.readObject();
        b = (Vector) in.readObject();

        equations = a.rows();
        variables = a.columns();

        solver = (LinearSystemSolver) in.readObject();
        factory = (Factory) in.readObject();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(a);
        out.writeObject(b);

        out.writeObject(solver);
        out.writeObject(factory);
    }
}
