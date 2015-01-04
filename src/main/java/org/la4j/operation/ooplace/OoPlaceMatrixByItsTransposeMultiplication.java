package org.la4j.operation.ooplace;

import org.la4j.matrix.Matrix;
import org.la4j.matrix.dense.DenseMatrix;
import org.la4j.matrix.sparse.ColumnMajorSparseMatrix;
import org.la4j.matrix.sparse.RowMajorSparseMatrix;
import org.la4j.operation.MatrixOperation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class OoPlaceMatrixByItsTransposeMultiplication extends MatrixOperation<Matrix> {

    @Override
    public Matrix apply(DenseMatrix a) {
        Matrix result = a.blankOfShape(a.rows(), a.rows());

        for (int i = 0; i < a.rows(); i++) {
            for (int j = 0; j < a.rows(); j++) {
                double acc = 0.0;
                for (int k = 0; k < a.columns(); k++) {
                    acc += a.get(i, k) * a.get(j, k);
                }
                result.set(i, j, acc);
            }
        }

        return result;
    }

    @Override
    public Matrix apply(RowMajorSparseMatrix a) {
        Matrix result = a.blankOfShape(a.rows(), a.rows());
        List<Integer> nzRows = new ArrayList<Integer>();
        Iterator<Integer> it = a.iteratorOfNonZeroRows();

        while (it.hasNext()) {
            nzRows.add(it.next());
        }

        for (int i: nzRows) {
            for (int j: nzRows) {
                result.set(i, j, a.nonZeroIteratorOfRow(i)
                                  .innerProduct(a.nonZeroIteratorOfRow(j)));
            }
        }

        return result;
    }

    @Override
    public Matrix apply(ColumnMajorSparseMatrix a) {
        // TODO: Implement its own algorithm
        return apply(a.toRowMajorSparseMatrix());
    }
}
