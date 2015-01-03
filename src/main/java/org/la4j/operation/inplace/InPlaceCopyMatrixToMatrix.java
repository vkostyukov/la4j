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

package org.la4j.operation.inplace;

import org.la4j.iterator.MatrixIterator;
import org.la4j.matrix.Matrix;
import org.la4j.matrix.dense.DenseMatrix;
import org.la4j.operation.SimpleMatrixMatrixOperation;
import org.la4j.matrix.sparse.ColumnMajorSparseMatrix;
import org.la4j.matrix.sparse.RowMajorSparseMatrix;
import org.la4j.matrix.sparse.SparseMatrix;

public class InPlaceCopyMatrixToMatrix extends SimpleMatrixMatrixOperation<Matrix> {

    @Override
    public Matrix applySimple(DenseMatrix a, SparseMatrix b) {
        MatrixIterator it = b.iterator();
        while (it.hasNext()) {
            it.next();
            int i = it.rowIndex();
            int j = it.columnIndex();
            double x = a.get(i, j);
            if (x != 0.0) {
                it.set(x);
            }
        }

        return b;
    }

    @Override
    public Matrix applySimple(SparseMatrix a, DenseMatrix b) {
        return fromSparseToMatrix(a, b);
    }

    @Override
    public Matrix applySimple(SparseMatrix a, SparseMatrix b) {
        return fromSparseToMatrix(a, b);
    }

    @Override
    public Matrix apply(DenseMatrix a, DenseMatrix b) {
        for (int i = 0; i < a.rows(); i++) {
            for (int j = 0; j < a.columns(); j++) {
                b.set(i, j, a.get(i, j));
            }
        }

        return b;
    }

    @Override
    public Matrix apply(RowMajorSparseMatrix a, ColumnMajorSparseMatrix b) {
        MatrixIterator it = a.nonZeroColumnMajorIterator();
        while (it.hasNext()) {
            double x = it.next();
            int i = it.rowIndex();
            int j = it.columnIndex();
            b.set(i, j, x);
        }

        return b;
    }

    @Override
    public Matrix apply(ColumnMajorSparseMatrix a, RowMajorSparseMatrix b) {
        MatrixIterator it = a.nonZeroRowMajorIterator();
        while (it.hasNext()) {
            double x = it.next();
            int i = it.rowIndex();
            int j = it.columnIndex();
            b.set(i, j, x);
        }

        return b;
    }

    private Matrix fromSparseToMatrix(SparseMatrix a, Matrix b) {
        MatrixIterator it = a.nonZeroIterator();
        while (it.hasNext()) {
            double x = it.next();
            int i = it.rowIndex();
            int j = it.columnIndex();
            b.set(i, j, x);
        }

        return b;
    }
}
