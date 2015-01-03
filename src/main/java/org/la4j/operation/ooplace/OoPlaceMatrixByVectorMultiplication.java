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
import org.la4j.iterator.VectorIterator;
import org.la4j.matrix.Matrix;
import org.la4j.matrix.dense.DenseMatrix;
import org.la4j.operation.MatrixVectorOperation;
import org.la4j.matrix.sparse.ColumnMajorSparseMatrix;
import org.la4j.matrix.sparse.RowMajorSparseMatrix;
import org.la4j.vector.Vector;
import org.la4j.vector.Vectors;
import org.la4j.vector.dense.DenseVector;
import org.la4j.vector.sparse.SparseVector;

import java.util.Iterator;

public class OoPlaceMatrixByVectorMultiplication extends MatrixVectorOperation<Vector> {
    @Override
    public Vector apply(DenseMatrix a, DenseVector b) {
        Vector result = b.blankOfLength(a.rows());

        for (int i = 0; i < a.rows(); i++) {
            double acc = 0.0;
            for (int j = 0; j < a.columns(); j++) {
                acc += a.get(i, j) * b.get(j);
            }
            result.set(i, acc);
        }

        return result;
    }

    @Override
    public Vector apply(DenseMatrix a, SparseVector b) {
        Vector result = DenseVector.zero(a.rows());

        for (int i = 0; i < a.rows(); i++) {
            double acc = 0.0;
            VectorIterator it = b.nonZeroIterator();

            while (it.hasNext()) {
                double x = it.next();
                int j = it.index();
                acc += a.get(i, j) * x;
            }

            result.set(i, acc);
        }

        return result;
    }

    @Override
    public Vector apply(RowMajorSparseMatrix a, DenseVector b) {
        Vector result = DenseVector.zero(a.rows());
        MatrixIterator it = a.nonZeroIterator();

        while (it.hasNext()) {
            double x = it.next();
            int i = it.rowIndex();
            int j = it.columnIndex();
            result.set(i, result.get(i) + (x * b.get(j)));
        }

        return result;
    }

    @Override
    public Vector apply(RowMajorSparseMatrix a, SparseVector b) {
        Vector result = b.blankOfLength(a.rows());
        Iterator<Integer> it = a.iteratorOfNonZeroRows();

        while (it.hasNext()) {
            int i = it.next();
            VectorIterator these = a.nonZeroIteratorOfRow(i);
            VectorIterator those = b.nonZeroIterator();
            result.set(i, these.innerProduct(those));
        }

        return result;
    }

    @Override
    public Vector apply(ColumnMajorSparseMatrix a, DenseVector b) {
        Vector result = DenseVector.zero(a.rows());
        MatrixIterator it = a.nonZeroIterator();

        while (it.hasNext()) {
            double x = it.next();
            int i = it.rowIndex();
            int j = it.columnIndex();
            result.set(i, result.get(i) + (x * b.get(j)));
        }

        return result;
    }

    @Override
    public Vector apply(ColumnMajorSparseMatrix a, SparseVector b) {
        Vector result = b.blankOfLength(a.rows());
        VectorIterator it = b.nonZeroIterator();

        while (it.hasNext()) {
            double x = it.next();
            int j = it.index();
            VectorIterator these = a.nonZeroIteratorOfColumn(j);

            while (these.hasNext()) {
                double y = these.next();
                int i = these.index();
                result.updateAt(i, Vectors.asPlusFunction(x * y));
            }
        }

        return result;
    }

    @Override
    public void ensureApplicableTo(Matrix a, Vector b) {
        if (a.columns() != b.length()) {
            throw new IllegalArgumentException(
                "Given vector should have the same length as number of columns in the given matrix: " +
                b.length() + " does not equal to " + a.columns() + "."
            );
        }
    }
}
