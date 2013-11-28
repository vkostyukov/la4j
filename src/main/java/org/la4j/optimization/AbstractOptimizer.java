package org.la4j.optimization;

import org.la4j.matrix.Matrix;
import org.la4j.vector.Vector;

public abstract class AbstractOptimizer implements LinearSystemOptimizer {
	
	protected Matrix matrix;
	
	protected int unknowns;
    protected int equations;
    
    protected AbstractOptimizer(Matrix a) {
        if (!applicableTo(a)) {
            fail("Given coefficient matrix can not be used with this solver.");
        }

        this.matrix = a;
        this.unknowns = a.columns();
        this.equations = a.rows();
    }
    
    @Override
    public Vector solve(Vector b) {
        return solve(b, b.factory());
    }

    @Override
    public Matrix self() {
        return matrix;
    }

    @Override
    public int unknowns() {
        return unknowns;
    }

    @Override
    public int equations() {
        return equations;
    }

    protected void fail(String message) {
        throw new IllegalArgumentException(message);
    }

}
