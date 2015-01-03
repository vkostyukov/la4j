package org.la4j.operation;

import org.la4j.matrix.dense.DenseMatrix;
import org.la4j.matrix.sparse.ColumnMajorSparseMatrix;
import org.la4j.matrix.sparse.RowMajorSparseMatrix;
import org.la4j.matrix.sparse.SparseMatrix;

public abstract class SymmetricMatrixMatrixOperation<R> extends MatrixMatrixOperation<R> {

    @Override
    public R apply(DenseMatrix a, RowMajorSparseMatrix b) {
        return applySymmetric(a, b);
    }

    @Override
    public R apply(DenseMatrix a, ColumnMajorSparseMatrix b) {
        return applySymmetric(a, b);
    }

    @Override
    public R apply(RowMajorSparseMatrix a, DenseMatrix b) {
        return applySymmetric(b, a);
    }

    @Override
    public R apply(RowMajorSparseMatrix a, ColumnMajorSparseMatrix b) {
        return applySymmetric(a, b);
    }

    @Override
    public R apply(ColumnMajorSparseMatrix a, DenseMatrix b) {
        return applySymmetric(b, a);
    }

    @Override
    public R apply(RowMajorSparseMatrix a, RowMajorSparseMatrix b) {
        return applySymmetric(a, b);
    }

    @Override
    public R apply(ColumnMajorSparseMatrix a, ColumnMajorSparseMatrix b) {
        return applySymmetric(a, b);
    }

    @Override
    public R apply(ColumnMajorSparseMatrix a, RowMajorSparseMatrix b) {
        return applySymmetric(b, a);
    }

    public abstract R applySymmetric(final DenseMatrix a, final SparseMatrix b);
    public abstract R applySymmetric(final SparseMatrix a, final SparseMatrix b);
    public abstract R applySymmetric(final RowMajorSparseMatrix a, final ColumnMajorSparseMatrix b);
}
