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

public abstract class MatrixIterator extends CursorIterator<MatrixIterator.MatrixCursor> {

    public final class MatrixCursor implements Comparable<MatrixCursor> {

        public final int row;
        public final int column;

        public MatrixCursor(int row, int column) {
            this.row = row;
            this.column = column;
        }

        @Override
        public int compareTo(MatrixCursor that) {
            return (this.row < that.row) ? -1 :
                   (this.row > that.row) ? 1 : this.column - that.column;
        }
    }

    public abstract int row();
    public abstract int column();

/*
    public MatrixIterator or(final MatrixIterator those, final JoinFunction function) {
        return new CursorToMatrixIterator(super.orElse(those, function));
    }

    public MatrixIterator and(final MatrixIterator those, final JoinFunction function) {
        return new CursorToMatrixIterator(super.andAlso(those, function));
    }
*/
    @Override
    protected MatrixCursor cursor() {
        // TODO: this is a bottleneck
        return new MatrixCursor(row(), column());
    }
}
