/*
 * Copyright 2011-2013, by Vladimir Kostyukov and Contributors.
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
 * Contributor(s): Chandler May
 *                 Maxim Samoylov
 *                 Anveshi Charuvaka
 *                 Clement Skau
 *                 Catherine da Graca
 * 
 */

package org.la4j.matrix.sparse;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;

import org.la4j.LinearAlgebra;
import org.la4j.iterator.RowMajorMatrixIterator;
import org.la4j.iterator.VectorIterator;
import org.la4j.matrix.Matrices;
import org.la4j.matrix.Matrix;
import org.la4j.matrix.MatrixFactory;
import org.la4j.matrix.functor.MatrixFunction;
import org.la4j.matrix.functor.MatrixProcedure;
import org.la4j.matrix.source.MatrixSource;
import org.la4j.vector.Vector;
import org.la4j.vector.functor.VectorProcedure;
import org.la4j.vector.sparse.CompressedVector;

/**
 * This is a CRS (Compressed Row Storage) matrix class.
 */
public class CRSMatrix extends RowMajorSparseMatrix {

    /**
     * Creates a zero {@link CRSMatrix} of the given shape:
     * {@code rows} x {@code columns}.
     */
    public static CRSMatrix zero(int rows, int columns) {
        return new CRSMatrix(rows, columns);
    }

    /**
     * Creates a diagonal {@link CRSMatrix} of the given {@code size} whose
     * diagonal elements are equal to {@code diagonal}.
     */
    public static CRSMatrix diagonal(int size, double diagonal) {
        double values[] = new double[size];
        int columnIndices[] = new int[size];
        int rowPointers[] = new int[size + 1];

        for (int i = 0; i < size; i++) {
            columnIndices[i] = i;
            rowPointers[i] = i;
            values[i] = diagonal;
        }

        rowPointers[size] = size;

        return new CRSMatrix(size, size, size, values, columnIndices, rowPointers);
    }

    /**
     * Creates an identity {@link CRSMatrix} of the given {@code size}.
     */
    public static CRSMatrix identity(int size) {
        return CRSMatrix.diagonal(size, 1.0);
    }

    /**
     * Creates a random {@link CRSMatrix} of the given shape:
     * {@code rows} x {@code columns}.
     */
    public static CRSMatrix random(int rows, int columns, double density, Random random) {
        if (density < 0.0 || density > 1.0) {
            throw new IllegalArgumentException("The density value should be between 0 and 1.0");
        }

        int cardinality = Math.max((int) ((rows * columns) * density), rows);

        double values[] = new double[cardinality];
        int columnIndices[] = new int[cardinality];
        int rowPointers[] = new int[rows + 1];

        int kk = cardinality / rows;
        int indices[] = new int[kk];

        int k = 0;
        for (int i = 0; i < rows; i++) {

            rowPointers[i] = k;

            for (int ii = 0; ii < kk; ii++) {
                indices[ii] = random.nextInt(columns);
            }

            Arrays.sort(indices);

            int previous = -1;
            for (int ii = 0; ii < kk; ii++) {

                if (indices[ii] == previous) {
                    continue;
                }

                values[k] = random.nextDouble();
                columnIndices[k++] = indices[ii];
                previous = indices[ii];
            }
        }

        rowPointers[rows] = cardinality;

        return new CRSMatrix(rows, columns, cardinality, values,
                             columnIndices, rowPointers);
    }

    /**
     * Creates a random symmetric {@link CRSMatrix} of the given {@code size}.
     */
    public static CRSMatrix randomSymmetric(int size, double density, Random random) {
        int cardinality = (int) ((size * size) * density);

        // TODO: Issue 15
        // We can do better here. All we need to is to make sure
        // that all the writes to CRS matrix are done in a serial
        // order (row-major). This will give us O(1) performance
        // per write.

        CRSMatrix matrix = new CRSMatrix(size, size, cardinality);

        for (int k = 0; k < cardinality / 2; k++) {
            int i = random.nextInt(size);
            int j = random.nextInt(size);
            double value = random.nextDouble();

            matrix.set(i, j, value);
            matrix.set(j, i, value);
        }

        return matrix;
    }

    /**
     * Creates a new {@link CRSMatrix} from the given 1D {@code array} with
     * compressing (copying) the underlying array.
     */
    public static CRSMatrix from1DArray(int rows, int columns, double[] array) {
        CRSMatrix result = CRSMatrix.zero(rows, columns);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                int k = i * columns + j;
                if (array[k] != 0.0) {
                    result.set(i, j, array[k]);
                }
            }
        }

        return result;
    }

    /**
     * Creates a new {@link CRSMatrix} from the given 2D {@code array} with
     * compressing (copying) the underlying array.
     */
    public static CRSMatrix from2DArray(double[][] array) {
        int rows = array.length;
        int columns = array[0].length;
        CRSMatrix result = CRSMatrix.zero(rows, columns);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (array[i][j] != 0.0) {
                    result.set(i, j, array[i][j]);
                }
            }
        }

        return result;
    }

    /**
     * Creates a block {@link CRSMatrix} of the given blocks {@code a},
     * {@code b}, {@code c} and {@code d}.
     */
    public static CRSMatrix block(Matrix a, Matrix b, Matrix c, Matrix d) {
        if ((a.rows() != b.rows()) || (a.columns() != c.columns()) ||
            (c.rows() != d.rows()) || (b.columns() != d.columns())) {
            throw new IllegalArgumentException("Sides of blocks are incompatible!");
        }

        int rows = a.rows() + c.rows(), columns = a.columns() + b.columns();
        ArrayList<Double> values = new ArrayList<Double>();
        ArrayList<Integer> columnIndices = new ArrayList<Integer>();
        int rowPointers[] = new int[rows + 1];

        int k = 0;
        rowPointers[0] = 0;
        double current = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if ((i < a.rows()) && (j < a.columns())) {
                    current = a.get(i, j);
                }
                if ((i < a.rows()) && (j > a.columns())) {
                    current = b.get(i, j);
                }
                if ((i > a.rows()) && (j < a.columns())) {
                    current = c.get(i, j);
                }
                if ((i > a.rows()) && (j > a.columns())) {
                    current = d.get(i, j);
                }
                if (Math.abs(current) > Matrices.EPS) {
                    values.add(current);
                    columnIndices.add(j);
                    k++;
                }
            }
            rowPointers[i + 1] = k;
        }
        double valuesArray[] = new double[values.size()];
        int colIndArray[] = new int[columnIndices.size()];
        for (int i = 0; i < values.size(); i++) {
            valuesArray[i] = values.get(i);
            colIndArray[i] = columnIndices.get(i);
        }

        return new CRSMatrix(rows, columns, k, valuesArray, colIndArray, rowPointers);
    }

    private static final long serialVersionUID = 4071505L;

    private static final int MINIMUM_SIZE = 32;

    private double values[];
    private int columnIndices[];
    private int rowPointers[];

    public CRSMatrix() {
        this(0, 0);
    }

    public CRSMatrix(int rows, int columns) {
        this(rows, columns, 0);
    }

    @Deprecated
    public CRSMatrix(int rows, int columns, double array[]) {
        this(Matrices.asArray1DSource(rows, columns, array));
    }

    @Deprecated
    public CRSMatrix(Matrix matrix) {
        this(Matrices.asMatrixSource(matrix));
    }

    @Deprecated
    public CRSMatrix(double array[][]) {
        this(Matrices.asArray2DSource(array));
    }

    @Deprecated
    public CRSMatrix(MatrixSource source) {
        this(source.rows(), source.columns(), 0);

        for (int i = 0; i < rows; i++) {
            rowPointers[i] = cardinality;
            for (int j = 0; j < columns; j++) {
                double value = source.get(i, j);
                // if (Math.abs(value) > Matrices.EPS || value < 0.0) {
                if (value != 0.0) {

                    if (values.length < cardinality + 1) {
                        growUp();
                    }

                    values[cardinality] = value;
                    columnIndices[cardinality] = j;
                    cardinality++;
                }
            }
        }

        rowPointers[rows] = cardinality;
    }

    public CRSMatrix(int rows, int columns, int cardinality) {
        super(LinearAlgebra.CRS_FACTORY, rows, columns);
        ensureCardinalityIsCorrect(rows, columns, cardinality);

        int alignedSize = align(cardinality);

        this.cardinality = 0;
        this.values = new double[alignedSize];
        this.columnIndices = new int[alignedSize];
        this.rowPointers = new int[rows + 1];
    }

    public CRSMatrix(int rows, int columns, int cardinality, double values[], int columnIndices[], int rowPointers[]) {
        super(LinearAlgebra.CRS_FACTORY, rows, columns);
        ensureCardinalityIsCorrect(rows, columns, cardinality);

        this.cardinality = cardinality;

        this.values = values;
        this.columnIndices = columnIndices;
        this.rowPointers = rowPointers;
    }

    @Override
    public double getOrElse(int i, int j, double defaultValue) {
        ensureIndexesAreInBounds(i, j);
        int k = searchForColumnIndex(j, rowPointers[i], rowPointers[i + 1]);

        if (k < rowPointers[i + 1] && columnIndices[k] == j) {
            return values[k];
        }

        return defaultValue;
    }

    @Override
    public void set(int i, int j, double value) {
        ensureIndexesAreInBounds(i, j);
        int k = searchForColumnIndex(j, rowPointers[i], rowPointers[i + 1]);

        if (k < rowPointers[i + 1] && columnIndices[k] == j) {
            // if (Math.abs(value) < Matrices.EPS && value >= 0.0) {
            if (value == 0.0) {
                remove(k, i);
            } else {
                values[k] = value;
            }
        } else {
            insert(k, i, j, value);
        }
    }

    @Override
    public void setAll(double value) {
        if (value == 0.0) {
            cardinality = 0;
        } else {
            int size = (int) capacity();

            if (values.length < size) {
                values = new double[size];
                columnIndices = new int[size];
                rowPointers = new int[rows + 1];
            }

            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < columns; j++) {
                    values[i * columns + j] = value;
                    columnIndices[i * columns + j] = j;
                }
                rowPointers[i] = columns * i;
            }

            rowPointers[rows] = size;
            cardinality = size;
        }
    }

    @Override
    public Vector getRow(int i) {
        int rowCardinality = rowPointers[i + 1] - rowPointers[i];
        double rowValues[] = new double[rowCardinality];
        int rowIndices[] = new int[rowCardinality];

        System.arraycopy(values, rowPointers[i], rowValues, 0, rowCardinality);
        System.arraycopy(columnIndices, rowPointers[i], rowIndices, 
                         0, rowCardinality);

        return new CompressedVector(columns, rowCardinality, rowValues, 
                                    rowIndices);
    }

    @Override
    public Vector getColumn(int j) {
        Vector result = CompressedVector.zero(rows);
        int i = 0;

        while (rowPointers[i] < cardinality) {
            int k = searchForColumnIndex(j, rowPointers[i], rowPointers[i + 1]);
            if (k < rowPointers[i + 1] && columnIndices[k] == j) {
                result.set(i, values[k]);
            }

            i++;
        }

        return result;
    }

    @Override
    public Matrix copyOfShape(int rows, int columns) {
        ensureDimensionsAreCorrect(rows, columns);

        if (rows >= this.rows && columns >= this.columns) {
            double $values[] = new double[align(cardinality)];
            int $columnIndices[] = new int[align(cardinality)];
            int $rowPointers[] = new int[rows + 1];

            System.arraycopy(values, 0, $values, 0, cardinality);
            System.arraycopy(columnIndices, 0, $columnIndices, 0, cardinality);
            System.arraycopy(rowPointers, 0, $rowPointers, 0, this.rows + 1);

            for (int i = this.rows; i < rows + 1; i++) {
                $rowPointers[i] = cardinality;
            }

            return new CRSMatrix(rows, columns, cardinality, $values, $columnIndices, $rowPointers);
        }

        double $values[] = new double[align(cardinality)];
        int $columnIndices[] = new int[align(cardinality)];
        int $rowPointers[] = new int[rows + 1];

        int $cardinality = 0;

        int k = 0, i = 0;
        while (k < cardinality && i < rows) {

            $rowPointers[i] = $cardinality;

            for (int j = rowPointers[i]; j < rowPointers[i + 1]
                    && columnIndices[j] < columns; j++, k++) {

                $values[$cardinality] = values[j];
                $columnIndices[$cardinality] = columnIndices[j];
                $cardinality++;
            }
            i++;
        }

        for (; i < rows + 1; i++) {
            $rowPointers[i] = $cardinality;
        }

        return new CRSMatrix(rows, columns, $cardinality, $values, $columnIndices, $rowPointers);
    }

    @Override
    public void eachNonZero(MatrixProcedure procedure) {
        int k = 0, i = 0;
        while (k < cardinality) {
            for (int j = rowPointers[i]; j < rowPointers[i + 1]; j++, k++) {
                procedure.apply(i, columnIndices[j], values[j]);
            }
            i++;
        }
    }

    @Override
    public void each(MatrixProcedure procedure) {
        int k = 0;
        for (int i = 0; i < rows; i++) {
            int valuesSoFar = rowPointers[i + 1];
            for (int j = 0; j < columns; j++) {
                if (k < valuesSoFar && j == columnIndices[k]) {
                    procedure.apply(i, j, values[k++]);
                } else {
                    procedure.apply(i, j, 0.0);
                }
            }
        }
    }

    @Override
    public void eachInRow(int i, VectorProcedure procedure) {
        int k = rowPointers[i];
        int valuesSoFar = rowPointers[i + 1];
        for (int j = 0; j < columns; j++) {
            if (k < valuesSoFar && j == columnIndices[k]) {
                procedure.apply(j, values[k++]);
            } else {
                procedure.apply(j, 0.0);
            }
        }
    }

    @Override
    public void eachNonZeroInRow(int i, VectorProcedure procedure) {
        for (int j = rowPointers[i]; j < rowPointers[i + 1]; j++) {
            procedure.apply(columnIndices[j], values[j]);
        }
    }

    @Override
    public void updateAt(int i, int j, MatrixFunction function) {
        int k = searchForColumnIndex(j, rowPointers[i], rowPointers[i + 1]);
        if (k < rowPointers[i + 1] && columnIndices[k] == j) {

            double value = function.evaluate(i, j, values[k]);

            // if (Math.abs(value) < Matrices.EPS && value >= 0.0) {
            if (value == 0.0) {
                remove(k, i);
            } else {
                values[k] = value;
            }
        } else {
            insert(k, i, j, function.evaluate(i, j, 0));
        }
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(rows);
        out.writeInt(columns);
        out.writeInt(cardinality);

        // write pairs (value, column index)
        for (int i = 0; i < cardinality; i++) {
            out.writeDouble(values[i]);
            out.writeInt(columnIndices[i]);
        }

        // write row pointers
        for (int i = 0; i < rows + 1; i++) {
            out.writeInt(rowPointers[i]);
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        rows = in.readInt();
        columns = in.readInt();
        cardinality = in.readInt();

        int alignedSize = align(cardinality);

        values = new double[alignedSize];
        columnIndices = new int[alignedSize];
        rowPointers = new int[rows + 1];

        // read pairs (value, column index)
        for (int i = 0; i < cardinality; i++) {
            values[i] = in.readDouble();
            columnIndices[i] = in.readInt();
        }

        // read row pointers
        for (int i = 0; i < rows + 1; i++) {
            rowPointers[i] = in.readInt();
        }
    }

    @Override
    public boolean nonZeroAt(int i, int j) {
        int k = searchForColumnIndex(j, rowPointers[i], rowPointers[i + 1]);
        return k < rowPointers[i + 1] && columnIndices[k] == j;
    }

    private int searchForColumnIndex(int j, int left, int right) {
        if (right - left == 0 || j > columnIndices[right - 1]) {
            return right;
        }

        while (left < right) {
            int p = (left + right) / 2;
            if (columnIndices[p] > j) {
                right = p;
            } else if (columnIndices[p] < j) {
                left = p + 1;
            } else {
                return p;
            }
        }

        return left;
    }

    private void insert(int k, int i, int j, double value) {
        //if (Math.abs(value) < Matrices.EPS && value >= 0.0) {
        if (value == 0.0) {
            return;
        }

        if (values.length < cardinality + 1) {
            growUp();
        }

        if (cardinality - k > 0) {
            System.arraycopy(values, k, values, k + 1, cardinality - k);
            System.arraycopy(columnIndices, k, columnIndices, k + 1, cardinality - k);
        }

//      for (int k = cardinality; k > position; k--) {
//          values[k] = values[k - 1];
//          columnIndices[k] = columnIndices[k - 1];
//      }

        values[k] = value;
        columnIndices[k] = j;

        for (int ii = i + 1; ii < rows + 1; ii++) {
            rowPointers[ii]++;
        }

        cardinality++;
    }

    private void remove(int k, int i) {
        cardinality--;

        if (cardinality - k > 0) {
            System.arraycopy(values, k + 1, values, k, cardinality - k);
            System.arraycopy(columnIndices, k + 1, columnIndices, k, cardinality - k);
        }

//        for (int kk = k; kk < cardinality; kk++) {
//            values[kk] = values[kk + 1];
//            columnIndices[kk] = columnIndices[kk + 1];
//        }

        for (int ii = i + 1; ii < rows + 1; ii++) {
            rowPointers[ii]--;
        }
    }

    private void growUp() {

        if (values.length == capacity()) {
            // This should never happen
            throw new IllegalStateException("This matrix can't grow up.");
        }

        int min = (
            (rows != 0 && columns > Integer.MAX_VALUE / rows) ?
            Integer.MAX_VALUE :
            (rows * columns)
            );
        int capacity = Math.min(min, (cardinality * 3) / 2 + 1);

        double $values[] = new double[capacity];
        int $columnIndices[] = new int[capacity];

        System.arraycopy(values, 0, $values, 0, cardinality);
        System.arraycopy(columnIndices, 0, $columnIndices, 0, cardinality);

        values = $values;
        columnIndices = $columnIndices;
    }

    private int align(int cardinality) {
        return ((cardinality / MINIMUM_SIZE) + 1) * MINIMUM_SIZE;
    }

    @Override
    public double max() {
        double max = Double.NEGATIVE_INFINITY;

        for (int i = 0; i < cardinality; i++) {
            if (values[i] > max) {
                max = values[i];
            }
        }

        return (max > 0.0) ? max : 0.0;
    }

    @Override
    public double min() {
        double min = Double.POSITIVE_INFINITY;

        for (int i = 0; i < cardinality; i++) {
            if (values[i] < min) {
                min = values[i];
            }
        }

        return (min < 0.0) ? min : 0.0;
    }

    @Override
    public double maxInRow(int i) {
        double max = Double.NEGATIVE_INFINITY;

        for (int k = rowPointers[i]; k < rowPointers[i + 1]; k++) {
            if (values[k] > max) {
                max = values[k];
            }
        }

        return (max > 0.0) ? max : 0.0;
    }

    @Override
    public double minInRow(int i) {
        double min = Double.POSITIVE_INFINITY;

        for (int k = rowPointers[i]; k < rowPointers[i + 1]; k++) {
            if (values[k] < min) {
                min = values[k];
            }
        }

        return (min < 0.0) ? min : 0.0;
    }
    
    /**
     * Returns a CRSMatrix with the selected rows and columns.
     */
    @Override
    public Matrix select(int[] rowIndices, int[] columnIndices) {
        int newRows = rowIndices.length;
        int newCols = columnIndices.length;
        
        if (newRows == 0 || newCols == 0) {
            fail("No rows or columns selected.");
        }

        // determine number of non-zero values (cardinality)
        // before allocating space, this is perhaps more efficient
        // than single pass and calling grow() when required.
        int newCardinality = 0;
        for (int i = 0; i < newRows; i++) {
            for (int j = 0; j < newCols; j++) {
                if (get(rowIndices[i], columnIndices[j]) != 0.0) {
                    newCardinality++;
                }
            }
        }

        // Construct the raw structure for the sparse matrix
        double[] newValues = new double[newCardinality];
        int[] newColumnIndices = new int[newCardinality];
        int[] newRowPointers = new int[newRows + 1];

        newRowPointers[0] = 0;
        int endPtr = 0;
        for (int i = 0; i < newRows; i++) {
            newRowPointers[i + 1] = newRowPointers[i];
            for (int j = 0; j < newCols; j++) {
                double val = get(rowIndices[i], columnIndices[j]);
                if (val != 0.0) {
                    newValues[endPtr] = val;
                    newColumnIndices[endPtr] = j;
                    endPtr++;
                    newRowPointers[i + 1] += 1;

                }
            }
        }

        return new CRSMatrix(newRows, newCols, newCardinality, newValues,
                             newColumnIndices, newRowPointers);
    }

    @Override
    public <T extends Matrix> T to(MatrixFactory<T> factory) {
        if (factory.outputClass == CRSMatrix.class) {
            return factory.outputClass.cast(this);
        }

        return super.to(factory);
    }

    @Override
    public Matrix blankOfShape(int rows, int columns) {
        return CRSMatrix.zero(rows, columns);
    }

    @Override
    public Iterator<Integer> iteratorOfNonZeroRows() {
        return new Iterator<Integer>() {
            private int i = -1;

            @Override
            public boolean hasNext() {
                while (i + 1 < rows &&
                       rowPointers[i + 1] < cardinality &&
                       rowPointers[i + 1] == rowPointers[i + 2]) {
                    i++;
                }

                return i + 1 < rows && rowPointers[i + 1] < cardinality ;
            }

            @Override
            public Integer next() {
                i++;
                return i;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Can not remove from this iterator.");
            }
        };
    }

    @Override
    public RowMajorMatrixIterator rowMajorIterator() {
        return new RowMajorMatrixIterator(rows, columns) {
            private long limit = (long) rows * columns;
            private boolean currentNonZero = false;
            private int i = -1;
            private int k = 0;

            @Override
            public int rowIndex() {
                return i / columns;
            }

            @Override
            public int columnIndex() {
                return i - rowIndex() * columns;
            }

            @Override
            public double get() {
                return currentNonZero ? values[k] : 0.0;
            }

            @Override
            public void set(double value) {
                if (currentNonZero) {
                    if (value == 0.0) {
                        CRSMatrix.this.remove(k, rowIndex());
                        currentNonZero = false;
                    } else {
                        values[k] = value;
                    }
                } else {
                    CRSMatrix.this.insert(k, rowIndex(), columnIndex(), value);
                    currentNonZero = true;
                }
            }

            @Override
            public boolean hasNext() {
                return i + 1 < limit;
            }

            @Override
            public Double next() {
                if (currentNonZero) {
                    k++;
                }

                i++;
                currentNonZero = k < rowPointers[rowIndex() + 1] && columnIndices[k] == columnIndex();

                return get();
            }
        };
    }

    @Override
    public RowMajorMatrixIterator nonZeroRowMajorIterator() {
        return new RowMajorMatrixIterator(rows, columns) {
            private int i = 0;
            private int k = -1;
            private boolean currentIsRemoved = false;
            private int removedIndex = -1;

            @Override
            public int rowIndex() {
                return i;
            }

            @Override
            public int columnIndex() {
                return currentIsRemoved ? removedIndex : columnIndices[k];
            }

            @Override
            public double get() {
                return currentIsRemoved ? 0.0 : values[k];
            }

            @Override
            public void set(double value) {
                if (value == 0.0 && !currentIsRemoved) {
                    currentIsRemoved = true;
                    removedIndex = columnIndices[k];
                    CRSMatrix.this.remove(k--, i);
                } else if (value != 0.0 && !currentIsRemoved) {
                    values[k] = value;
                } else {
                    currentIsRemoved = false;
                    CRSMatrix.this.insert(++k, i, removedIndex, value);
                }
            }

            @Override
            public boolean hasNext() {
                return k + 1 < cardinality;
            }

            @Override
            public Double next() {
                currentIsRemoved = false;
                if (rowPointers[i + 1] == ++k) {
                    i++;
                }
                return get();
            }
        };
    }

    @Override
    public VectorIterator nonZeroIteratorOfRow(int i) {
        final int ii = i;
        return new VectorIterator(columns) {
            private int k = rowPointers[ii] - 1;
            private boolean currentIsRemoved = false;
            private int removedIndex = -1;

            @Override
            public int index() {
                return currentIsRemoved ? removedIndex : columnIndices[k];
            }

            @Override
            public double get() {
                return currentIsRemoved ? 0.0 : values[k];
            }

            @Override
            public void set(double value) {
                if (value == 0.0 && !currentIsRemoved) {
                    currentIsRemoved = true;
                    removedIndex = columnIndices[k];
                    CRSMatrix.this.remove(k--, ii);
                } else if (value != 0.0 && !currentIsRemoved) {
                    values[k] = value;
                } else {
                    currentIsRemoved = false;
                    CRSMatrix.this.insert(++k, ii, removedIndex, value);
                }
            }

            @Override
            public boolean hasNext() {
                return k + 1 < rowPointers[ii + 1];
            }

            @Override
            public Double next() {
                currentIsRemoved = false;
                return values[++k];
            }
        };
    }

    @Override
    public VectorIterator iteratorOfRow(int i) {
        final int ii = i;
        return new VectorIterator(columns) {
            private int j = -1;
            private int k = rowPointers[ii];

            @Override
            public int index() {
                return j;
            }

            @Override
            public double get() {
                if (k < rowPointers[ii + 1] && columnIndices[k] == j) {
                    return values[k];
                }
                return 0.0;
            }

            @Override
            public void set(double value) {
                if (k < rowPointers[ii + 1] && columnIndices[k] == j) {
                    if (value == 0.0) {
                        CRSMatrix.this.remove(k, ii);
                    } else {
                        values[k] = value;
                    }
                } else {
                    CRSMatrix.this.insert(k, ii, j, value);
                }
            }

            @Override
            public boolean hasNext() {
                return j + 1 < columns;
            }

            @Override
            public Double next() {
                j++;
                if (k < rowPointers[ii + 1] && columnIndices[k] == j - 1) {
                    k++;
                }

                return get();
            }
        };
    }
}
