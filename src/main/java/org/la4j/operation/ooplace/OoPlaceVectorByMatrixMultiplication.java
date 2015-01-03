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
import org.la4j.matrix.sparse.ColumnMajorSparseMatrix;
import org.la4j.matrix.sparse.RowMajorSparseMatrix;
import org.la4j.vector.Vector;
import org.la4j.vector.Vectors;
import org.la4j.vector.dense.DenseVector;
import org.la4j.operation.VectorMatrixOperation;
import org.la4j.vector.sparse.SparseVector;

import java.util.Iterator;

public class OoPlaceVectorByMatrixMultiplication extends VectorMatrixOperation<Vector> {
    @Override
    public Vector apply(SparseVector a, DenseMatrix b) {
        Vector result = DenseVector.zero(b.columns());

        for (int j = 0; j < b.columns(); j++) {
            double acc = 0.0;
            VectorIterator it = a.nonZeroIterator();

            while (it.hasNext()) {
                double x = it.next();
                int i = it.index();
                acc += x * b.get(i, j);
            }
            result.set(j, acc);
        }

        return result;
    }

    @Override
    public Vector apply(SparseVector a, RowMajorSparseMatrix b) {
        // TODO: use sequential writes
        Vector result = a.blankOfLength(b.columns());
        VectorIterator these = a.nonZeroIterator();

        while (these.hasNext()) {
            double x = these.next();
            int i = these.index();
            VectorIterator those = b.iteratorOfRow(i);

            while (those.hasNext()) {
                double y = those.next();
                int j = those.index();
                result.updateAt(j, Vectors.asPlusFunction(x * y));
            }
        }

        return result;
    }

    @Override
    public Vector apply(SparseVector a, ColumnMajorSparseMatrix b) {
        Vector result = a.blankOfLength(b.columns());
        Iterator<Integer> columns = b.iteratorOrNonZeroColumns();

        while (columns.hasNext()) {
            int j = columns.next();
            VectorIterator these = a.nonZeroIterator();
            VectorIterator those = b.nonZeroIteratorOfColumn(j);
            result.set(j, these.innerProduct(those));
        }

        return result;
    }

    @Override
    public Vector apply(DenseVector a, DenseMatrix b) {
        Vector result = a.blankOfLength(b.columns());

        for (int j = 0; j < b.columns(); j++) {
            double acc = 0.0;

            for (int i = 0; i < b.rows(); i++) {
                acc += a.get(i) * b.get(i, j);
            }
            result.set(j, acc);
        }

        return result;
    }

    @Override
    public Vector apply(DenseVector a, RowMajorSparseMatrix b) {
        // TODO: use sequential writes
        Vector result = SparseVector.zero(b.columns());
        MatrixIterator it = b.rowMajorIterator();

        while (it.hasNext()) {
            double x = it.next();
            int i = it.rowIndex();
            int j = it.columnIndex();
            result.updateAt(j, Vectors.asPlusFunction(x * a.get(i)));
        }

        return result;
    }

    @Override
    public Vector apply(DenseVector a, ColumnMajorSparseMatrix b) {
        Vector result = SparseVector.zero(b.columns());
        Iterator<Integer> columns = b.iteratorOrNonZeroColumns();

        while (columns.hasNext()) {
            int j = columns.next();
            VectorIterator it = b.nonZeroIteratorOfColumn(j);
            double acc = 0.0;

            while (it.hasNext()) {
                double x = it.next();
                int i = it.index();
                acc += x * a.get(i);
            }

            result.set(j, acc);

        }

        return result;
    }

    @Override
    public void ensureApplicableTo(Vector a, Matrix b) {
        if (a.length() != b.rows()) {
            throw new IllegalArgumentException(
                "Given vector should have the same length as number of rows in the given matrix: " +
                a.length() + " does not equal to " + b.rows() + "."
            );
        }
    }
}
