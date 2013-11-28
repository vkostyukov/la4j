package org.la4j.optimization;

import org.la4j.LinearAlgebra;
import org.la4j.factory.Factory;
import org.la4j.linear.LinearSystemSolver;
import org.la4j.matrix.Matrix;
import org.la4j.vector.MockVector;
import org.la4j.vector.Vector;

import junit.framework.TestCase;

public class AbstractOptimizerTest extends TestCase {
	public void performTest(LinearAlgebra.OptimizerFactory optimizerFactory,
			double coefficientMatrix[][], double rightHandVector[]) {

		for (Factory factory : LinearAlgebra.FACTORIES) {

			Matrix a = factory.createMatrix(coefficientMatrix);
			Vector b = factory.createVector(rightHandVector);

			LinearSystemOptimizer solver = a.withOptimizer(optimizerFactory);
			Vector x = solver.solve(b, factory);

			assertEquals(new MockVector(b), new MockVector(a.multiply(x)));
		}
	}
}
