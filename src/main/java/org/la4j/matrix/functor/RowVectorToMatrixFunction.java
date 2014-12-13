package org.la4j.matrix.functor;

import org.la4j.vector.functor.VectorFunction;

public class RowVectorToMatrixFunction implements MatrixFunction {
	private VectorFunction underlying;
	
	public RowVectorToMatrixFunction(VectorFunction underlying) {
		this.underlying = underlying;
	}
	
	@Override
	public double evaluate(int i, int j, double value) {
		return underlying.evaluate(j, value);
	}
}
