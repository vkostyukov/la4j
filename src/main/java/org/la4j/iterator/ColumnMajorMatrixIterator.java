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

package org.la4j.iterator;

public abstract class ColumnMajorMatrixIterator extends MatrixIterator {

    public ColumnMajorMatrixIterator(int rows, int columns) {
    super(rows, columns);
  }

    @Override
    protected int cursor() {
    return columnIndex() * rows + rowIndex();
  }

    @Override
    public MatrixIterator orElseAdd(final MatrixIterator those) {
        return new CursorToColumnMajorMatrixIterator(super.orElse(those, JoinFunction.ADD), rows, columns);
    }

    @Override
    public MatrixIterator orElseSubtract(final MatrixIterator those) {
        return new CursorToColumnMajorMatrixIterator(super.orElse(those, JoinFunction.SUB), rows, columns);
    }

    @Override
    public MatrixIterator andAlsoMultiply(MatrixIterator those) {
        return new CursorToColumnMajorMatrixIterator(super.andAlso(those, JoinFunction.MUL), rows, columns);
    }

    @Override
    public MatrixIterator andAlsoDivide(MatrixIterator those) {
        return new CursorToColumnMajorMatrixIterator(super.andAlso(those, JoinFunction.DIV), rows, columns);
    }
}
