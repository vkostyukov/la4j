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

import org.la4j.inversion.MatrixInverter;
import org.la4j.iterator.MatrixIterator;
import org.la4j.iterator.VectorIterator;
import org.la4j.matrix.Matrix;
import org.la4j.matrix.dense.DenseMatrix;
import org.la4j.matrix.sparse.ColumnMajorSparseMatrix;
import org.la4j.matrix.sparse.RowMajorSparseMatrix;
import org.la4j.vector.dense.DenseVector;
import org.la4j.operation.VectorVectorOperation;
import org.la4j.vector.sparse.SparseVector;

public class OoPlaceOuterProduct extends VectorVectorOperation<Matrix> {

    @Override
    public Matrix apply(SparseVector a, SparseVector b) {
        Matrix result = RowMajorSparseMatrix.zero(a.length(), b.length());

        VectorIterator these = a.nonZeroIterator();
        while (these.hasNext()) {
            double x = these.next();
            int i = these.index();
            VectorIterator those = b.nonZeroIterator();
            while (those.hasNext()) {
                double y = those.next();
                int j = those.index();

                result.set(i, j, x * y);
            }
        }

        return result;
    }

    @Override
    public Matrix apply(DenseVector a, DenseVector b) {
        Matrix result = DenseMatrix.zero(a.length(), b.length());

        for (int i = 0; i < a.length(); i++) {
            for (int j = 0; j < b.length(); j++) {
                result.set(i, j, a.get(i) * b.get(j));
            }
        }

        return result;
    }

    @Override
    public Matrix apply(DenseVector a, SparseVector b) {
        Matrix result = ColumnMajorSparseMatrix.zero(a.length(), b.length());
        VectorIterator it = b.nonZeroIterator();

        while (it.hasNext()) {
            double x = it.next();
            int j = it.index();
            for (int i = 0; i < a.length(); i++) {
                result.set(i, j, x * a.get(i));
            }
        }

        return result;
    }

    @Override
    public Matrix apply(SparseVector a, DenseVector b) {
        Matrix result = RowMajorSparseMatrix.zero(a.length(), b.length());
        VectorIterator it = a.nonZeroIterator();

        while (it.hasNext()) {
            double x = it.next();
            int i = it.index();
            for (int j = 0; j < b.length(); j++) {
                result.set(i, j, x * b.get(j));
            }
        }

        return result;
    }
}
