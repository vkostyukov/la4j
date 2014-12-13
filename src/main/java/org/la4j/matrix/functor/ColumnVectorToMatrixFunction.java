package org.la4j.matrix.functor;

import org.la4j.vector.functor.VectorFunction;

public class ColumnVectorToMatrixFunction implements MatrixFunction {
	private VectorFunction underlying;
	
	public ColumnVectorToMatrixFunction(VectorFunction underlying) {
		this.underlying = underlying;
	}

	@Override
	public double evaluate(int i, int j, double value) {
		return underlying.evaluate(i, value);
	}
}
