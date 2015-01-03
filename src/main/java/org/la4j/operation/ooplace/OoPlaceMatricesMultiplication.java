package org.la4j.operation.ooplace;

import org.la4j.iterator.MatrixIterator;
import org.la4j.iterator.VectorIterator;
import org.la4j.matrix.Matrices;
import org.la4j.matrix.Matrix;
import org.la4j.matrix.dense.DenseMatrix;
import org.la4j.matrix.sparse.ColumnMajorSparseMatrix;
import org.la4j.matrix.sparse.RowMajorSparseMatrix;
import org.la4j.operation.MatrixMatrixOperation;
import org.la4j.vector.Vector;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class OoPlaceMatricesMultiplication extends MatrixMatrixOperation<Matrix> {

    @Override
    public Matrix apply(DenseMatrix a, DenseMatrix b) {
        Matrix result = a.blankOfShape(a.rows(), b.columns());

        for (int j = 0; j < b.columns(); j++) {
            Vector column = b.getColumn(j);
            for (int i = 0; i < a.rows(); i++) {
                double acc = 0.0;
                for (int k = 0; k < a.columns(); k++) {
                    acc += a.get(i, k) * column.get(k);
                }
                result.set(i, j, acc);
            }
        }

        return result;
    }

    @Override
    public Matrix apply(DenseMatrix a, RowMajorSparseMatrix b) {
        Matrix result = ColumnMajorSparseMatrix.zero(a.rows(), b.columns());
        MatrixIterator it = b.nonZeroRowMajorIterator();

        while (it.hasNext()) {
            double x = it.next();
            int i = it.rowIndex();
            int j = it.columnIndex();

            for (int k = 0; k < a.rows(); k++) {
                result.updateAt(k, j, Matrices.asPlusFunction(x * a.get(k, i)));
            }
        }

       return result;
    }

    @Override
    public Matrix apply(DenseMatrix a, ColumnMajorSparseMatrix b) {
        Matrix result = b.blankOfShape(a.rows(), b.columns());
        Iterator<Integer> nzColumns = b.iteratorOrNonZeroColumns();

        while (nzColumns.hasNext()) {
            int j = nzColumns.next();

            for (int i = 0; i < a.rows(); i++) {
                double acc = 0.0;
                VectorIterator it = b.nonZeroIteratorOfColumn(j);
                while (it.hasNext()) {
                    double x = it.next();
                    acc += x * a.get(i, it.index());
                }

                result.set(i, j, acc);
            }
        }

        return result;
    }

    @Override
    public Matrix apply(RowMajorSparseMatrix a, DenseMatrix b) {
        Matrix result = a.blankOfShape(a.rows(), b.columns());
        Iterator<Integer> nzRows = a.iteratorOfNonZeroRows();

        while (nzRows.hasNext()) {
            int i = nzRows.next();

            for (int j = 0; j < b.columns(); j++) {
                double acc = 0.0;
                VectorIterator it = a.nonZeroIteratorOfRow(i);
                while (it.hasNext()) {
                    double x = it.next();
                    acc += x * b.get(it.index(), j);
                }

                result.set(i, j, acc);
            }
        }

        return result;
    }

    @Override
    public Matrix apply(RowMajorSparseMatrix a, RowMajorSparseMatrix b) {
        // TODO: Can we do it w/o updateAt?
        Matrix result = a.blankOfShape(a.rows(), b.columns());
        MatrixIterator these = a.nonZeroRowMajorIterator();

        while (these.hasNext()) {
            double x = these.next();
            int i = these.rowIndex();
            int j = these.columnIndex();

            VectorIterator those = b.nonZeroIteratorOfRow(j);
            while (those.hasNext()) {
                double y = those.next();
                int k = those.index();
                result.updateAt(i, k, Matrices.asPlusFunction(x * y));
            }
        }

        return result;
    }

    @Override
    public Matrix apply(RowMajorSparseMatrix a, ColumnMajorSparseMatrix b) {
        Matrix result = a.blankOfShape(a.rows(), b.columns());
        Iterator<Integer> nzRows = a.iteratorOfNonZeroRows();
        Iterator<Integer> nzColumnsIt = b.iteratorOrNonZeroColumns();
        List<Integer> nzColumns = new ArrayList<Integer>();
        while(nzColumnsIt.hasNext()) {
            nzColumns.add(nzColumnsIt.next());
        }

        while(nzRows.hasNext()) {
            int i = nzRows.next();
            for (int j: nzColumns) {
                result.set(i, j, a.nonZeroIteratorOfRow(i)
                                  .innerProduct(b.nonZeroIteratorOfColumn(j)));
            }
        }

        return result;
    }

    @Override
    public Matrix apply(ColumnMajorSparseMatrix a, DenseMatrix b) {
        Matrix result = a.blankOfShape(a.rows(), b.columns());
        MatrixIterator it = a.nonZeroColumnMajorIterator();

        while (it.hasNext()) {
            double x = it.next();
            int i = it.rowIndex();
            int j = it.columnIndex();

            for (int k = 0; k < b.columns(); k++) {
                result.updateAt(i, k, Matrices.asPlusFunction(x * b.get(j, k)));
            }
        }

        return result;
    }

    @Override
    public Matrix apply(ColumnMajorSparseMatrix a, RowMajorSparseMatrix b) {
        // TODO: Might be improved a bit.
        Matrix result = b.blankOfShape(a.rows(), b.columns());
        MatrixIterator these = a.nonZeroColumnMajorIterator();

        while (these.hasNext()) {
            double x = these.next();
            int i = these.rowIndex();
            int j = these.columnIndex();

            VectorIterator those = b.nonZeroIteratorOfRow(j);
            while (those.hasNext()) {
                double y = those.next();
                int k = those.index();
                result.updateAt(i, k, Matrices.asPlusFunction(x * y));
            }
        }

        return result;
    }

    @Override
    public Matrix apply(ColumnMajorSparseMatrix a, ColumnMajorSparseMatrix b) {
        // TODO: Might be improved a bit.
        Matrix result = a.blankOfShape(a.rows(), b.columns());
        MatrixIterator these = b.nonZeroColumnMajorIterator();

        while (these.hasNext()) {
            double x = these.next();
            int i = these.rowIndex();
            int j = these.columnIndex();

            VectorIterator those = a.nonZeroIteratorOfColumn(i);
            while (those.hasNext()) {
                double y = those.next();
                int k = those.index();
                result.updateAt(k, j, Matrices.asPlusFunction(x * y));
            }
        }

        return result;
    }

    @Override
    public void ensureApplicableTo(Matrix a, Matrix b) {
        if (a.columns() != b.rows()) {
            throw new IllegalArgumentException(
                "The number of rows in the left-hand matrix should be equal to the number of " +
                "columns in the right-hand matrix: " + a.rows() + " does not equal to " + b.columns() + "."
            );
        }
    }
}
