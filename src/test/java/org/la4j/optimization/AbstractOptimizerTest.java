package org.la4j.optimization;

import org.la4j.LinearAlgebra;
import org.la4j.factory.Factory;
import org.la4j.linear.LinearSystemSolver;
import org.la4j.matrix.Matrix;
import org.la4j.vector.MockVector;
import org.la4j.vector.Vector;

import junit.framework.TestCase;

public class AbstractOptimizerTest extends TestCase {
	
	private double absoluteEps = 1e-7;
	
	public void performTest(LinearAlgebra.OptimizerFactory optimizerFactory,
			double coefficientMatrix[][], double rightHandVector[], double accuracy) {

		for (Factory factory : LinearAlgebra.FACTORIES) {

			Matrix a = factory.createMatrix(coefficientMatrix);
			Vector b = factory.createVector(rightHandVector);

			LinearSystemOptimizer solver = a.withOptimizer(optimizerFactory);
			Vector x = solver.solve(b, factory, accuracy);

			double eps = (new MockVector(b)).add((new MockVector(a.multiply(x)).multiply(-1.0))).max();
			
			assertTrue(Math.abs(eps) <= accuracy);
		}
	}
}
