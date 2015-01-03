/*
 * Copyright 2011-2014, by Vladimir Kostyukov and Contributors.
 *
 * This file is part of la4j project (http://la4j.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributor(s): -
 *
 */

package org.la4j.operation.ooplace;

import org.la4j.iterator.MatrixIterator;
import org.la4j.iterator.RowMajorMatrixIterator;
import org.la4j.matrix.Matrix;
import org.la4j.matrix.dense.DenseMatrix;
import org.la4j.operation.SymmetricMatrixMatrixOperation;
import org.la4j.matrix.sparse.ColumnMajorSparseMatrix;
import org.la4j.matrix.sparse.RowMajorSparseMatrix;
import org.la4j.matrix.sparse.SparseMatrix;

public class OoPlaceMatricesAddition extends SymmetricMatrixMatrixOperation<Matrix> {

    @Override
    public Matrix applySymmetric(DenseMatrix a, SparseMatrix b) {
        Matrix result = a.copy();
        MatrixIterator it = b.nonZeroIterator();

        while (it.hasNext()) {
            double x = it.next();
            int i = it.rowIndex();
            int j = it.columnIndex();
            result.set(i, j, result.get(i, j) + x);
        }

        return result;
    }

    @Override
    public Matrix applySymmetric(RowMajorSparseMatrix a, ColumnMajorSparseMatrix b) {
        Matrix result = a.blank();
        RowMajorMatrixIterator these = a.nonZeroRowMajorIterator();
        RowMajorMatrixIterator those = b.nonZeroRowMajorIterator();
        MatrixIterator both = these.orElseAdd(those);

        while (both.hasNext()) {
            double x = both.next();
            int i = both.rowIndex();
            int j = both.columnIndex();
            result.set(i, j, x);
        }

        return result;
    }

    @Override
    public Matrix apply(DenseMatrix a, DenseMatrix b) {
        Matrix result = a.blank();

        for (int i = 0; i < a.rows(); i++) {
            for (int j = 0; j < a.columns(); j++) {
                result.set(i, j, a.get(i, j) + b.get(i, j));
            }
        }

        return result;
    }

    @Override
    public Matrix applySymmetric(SparseMatrix a, SparseMatrix b) {
        Matrix result = a.blank();
        MatrixIterator these = a.nonZeroIterator();
        MatrixIterator those = b.nonZeroIterator();
        MatrixIterator both = these.orElseAdd(those);

        while (both.hasNext()) {
            double x = both.next();
            int i = both.rowIndex();
            int j = both.columnIndex();
            result.set(i, j, x);
        }

        return result;
    }

    @Override
    public void ensureApplicableTo(Matrix a, Matrix b) {
        if (a.rows() != b.rows() || a.columns() != b.columns()) {
            throw new IllegalArgumentException(
                "Given matrices should have the same shape: " +
                a.rows() + "x" + a.columns() + " does not equal to " +
                b.rows() + "x" + b.columns() + "."
            );
        }
    }
}
