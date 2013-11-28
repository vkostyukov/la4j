package org.la4j.optimization;

import org.la4j.LinearAlgebra;
import org.la4j.factory.Factory;
import org.la4j.matrix.Matrix;
import org.la4j.vector.Vector;

public class NonlinearConjugateGradientOptimizer extends AbstractOptimizer implements LinearSystemOptimizer{

	private static final long serialVersionUID = -6730752031320270935L;

	private Vector start; 
	private int maxIterations = 10000;
	private double eps = 1e-5;
	
	public NonlinearConjugateGradientOptimizer(Matrix a) {
		super(a);		
		start = LinearAlgebra.BASIC1D_FACTORY.createVector(this.unknowns);
	}

	@Override
	public Vector solve(Vector b, Factory factory) {
		Vector oldX = start.copy();				
		Vector oldGradient = formGradient(matrix, start, b).multiply(-1.0);
		Vector newX = alphaLineSearch(matrix, b, oldX, oldGradient);
		Vector newGradient = formGradient(matrix, start, b).multiply(-1.0);
		Vector direction = newGradient.copy();
		
		int k = 0;
		double beta = 1;		
		
		while ((k++ < maxIterations) && (formGradient(matrix, oldX, b).norm() > eps)){			
			newGradient = formGradient(matrix, newX, b).multiply(-1.0);
			beta = Math.pow(formGradient(matrix, newX, b).norm(), 2) / Math.pow(formGradient(matrix, oldX, b).norm(), 2);
			direction = newGradient.add(direction.multiply(beta));
			oldX = newX.copy();
			newX = alphaLineSearch(matrix, b, oldX, direction);			
		}
		
		if (k >= maxIterations){
			System.out.println("Reached " + maxIterations + " iterations." );
		}
		
		return oldX;
	}

	@Override
	public boolean applicableTo(Matrix matrix) {	
		return true;
	}
	
	private double form(Matrix A, Vector x, Vector b){
		return Math.pow(A.multiply(x).add(b.multiply(-1.0)).norm(), 2);
	}
	
	private Vector formGradient(Matrix A, Vector x, Vector b){
		return A.transpose().multiply(A.multiply(x).add(b.multiply(-1.0))).multiply(2);
	}
	
	private Vector alphaLineSearch(Matrix A, Vector b, Vector x, Vector s){
		int k = 0;
		
		Vector newX = x.copy();
		Vector oldX = x.copy();
		double newF = form(A, x, b);
		double oldF = form(A, x, b);
		double alpha = eps;
		
		do{
			k++;
			oldX = newX;
			newX = oldX.add(s.multiply(alpha));
			oldF = newF;
			newF = form(A, newX, b);
			
		}while((newF < oldF) && (k < maxIterations));
		
		return  oldX;
		
	}	
}
