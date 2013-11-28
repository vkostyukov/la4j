package org.la4j.optimization;

import org.la4j.LinearAlgebra;

public class NonlinearConjugateGradientOptimizerTest extends AbstractOptimizerTest {
	
	private double epsForSolving = 1e-7;
	
	//Ensure optimizer solves good linear systems as well.
	public void testSolve_1x1() {

        double a[][] = new double[][] {
            { 99.0 }
        };

        double b[] = new double[] { -33.0 };

        performTest(LinearAlgebra.OptimizerFactory.NLCG, a, b, epsForSolving);
    }
	
	//Ensure optimizer solves good linear systems as well.
	public void testSolve_2x2() {

        double a[][] = new double[][] {
            { 5.0, 10.0 },
            { 15.0, -20.0 }
        };

        double b[] = new double[] { 21.0, -37.0 };

        performTest(LinearAlgebra.OptimizerFactory.NLCG, a, b, epsForSolving);
    }
	
	//Ensure optimizer solves good linear systems as well.
	public void testSolve_3x3() {

        double a[][] = new double[][] {
            { -8.0, 4.0, 2.0 },
            { 10.0, 18.0, -70.0 },
            { 3.0, -54.0, 19.0 }
        };

        double b[] = new double[] { 7.0, -85.5, 0.5 };

        performTest(LinearAlgebra.OptimizerFactory.NLCG, a, b, epsForSolving);
    }
	
	public void testSolve_3x4 () {
		double[][] a = new double[][] {
				   { 1.0, 2.0, 3.0, 4.0 },
				   { 4.0, 5.0, 6.0, 7.0 },
				   { 7.0, 8.0, 9.0, 10.0 }
				};
		double[] b = new double[] { 1.0, 2.0, 3.0 };
		
		performTest(LinearAlgebra.OptimizerFactory.NLCG, a, b, 0.001);				
	}
}
