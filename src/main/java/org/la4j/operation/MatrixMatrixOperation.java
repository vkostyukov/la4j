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

package org.la4j.operation;

import org.la4j.matrix.Matrix;
import org.la4j.matrix.dense.DenseMatrix;
import org.la4j.matrix.sparse.ColumnMajorSparseMatrix;
import org.la4j.matrix.sparse.RowMajorSparseMatrix;

public abstract class MatrixMatrixOperation<R> {

    public abstract R apply(final DenseMatrix a, final DenseMatrix b);
    public abstract R apply(final DenseMatrix a, final RowMajorSparseMatrix b);
    public abstract R apply(final DenseMatrix a, final ColumnMajorSparseMatrix b);

    public abstract R apply(final RowMajorSparseMatrix a, final DenseMatrix b);
    public abstract R apply(final RowMajorSparseMatrix a, final RowMajorSparseMatrix b);
    public abstract R apply(final RowMajorSparseMatrix a, final ColumnMajorSparseMatrix b);

    public abstract R apply(final ColumnMajorSparseMatrix a, final DenseMatrix b);
    public abstract R apply(final ColumnMajorSparseMatrix a, final RowMajorSparseMatrix b);
    public abstract R apply(final ColumnMajorSparseMatrix a, final ColumnMajorSparseMatrix b);

    public void ensureApplicableTo(final Matrix a, final Matrix b) { }

    public MatrixOperation<R> partiallyApply(final DenseMatrix a) {
        return new MatrixOperation<R>() {
            @Override
            public R apply(final DenseMatrix b) {
                return MatrixMatrixOperation.this.apply(a, b);
            }

            @Override
            public R apply(final RowMajorSparseMatrix b) {
                return MatrixMatrixOperation.this.apply(a, b);
            }

            @Override
            public R apply(final ColumnMajorSparseMatrix b) {
                return MatrixMatrixOperation.this.apply(a, b);
            }

            @Override
            public void ensureApplicableTo(final Matrix b) {
                MatrixMatrixOperation.this.ensureApplicableTo(a, b);
            }
        };
    }

    public MatrixOperation<R> partiallyApply(final RowMajorSparseMatrix a) {
        return new MatrixOperation<R>() {
            @Override
            public R apply(DenseMatrix b) {
                return MatrixMatrixOperation.this.apply(a, b);
            }

            @Override
            public R apply(RowMajorSparseMatrix b) {
                return MatrixMatrixOperation.this.apply(a, b);
            }

            @Override
            public R apply(ColumnMajorSparseMatrix b) {
                return MatrixMatrixOperation.this.apply(a, b);
            }

            @Override
            public void ensureApplicableTo(final Matrix b) {
                MatrixMatrixOperation.this.ensureApplicableTo(a, b);
            }
        };
    }

    public MatrixOperation<R> partiallyApply(final ColumnMajorSparseMatrix a) {
        return new MatrixOperation<R>() {
            @Override
            public R apply(DenseMatrix b) {
                return MatrixMatrixOperation.this.apply(a, b);
            }

            @Override
            public R apply(RowMajorSparseMatrix b) {
                return MatrixMatrixOperation.this.apply(a, b);
            }

            @Override
            public R apply(ColumnMajorSparseMatrix b) {
                return MatrixMatrixOperation.this.apply(a, b);
            }

            @Override
            public void ensureApplicableTo(final Matrix b) {
                MatrixMatrixOperation.this.ensureApplicableTo(a, b);
            }
        };
    }
}
