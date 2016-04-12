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

package org.la4j.vector;

import java.text.NumberFormat;
import java.util.Collection;
import java.util.Map;
import java.util.Random;

import org.la4j.iterator.VectorIterator;
import org.la4j.Matrix;
import org.la4j.matrix.sparse.CRSMatrix;
import org.la4j.matrix.ColumnMajorSparseMatrix;
import org.la4j.matrix.RowMajorSparseMatrix;
import org.la4j.Vector;
import org.la4j.Vectors;
import org.la4j.vector.functor.VectorAccumulator;
import org.la4j.vector.functor.VectorProcedure;
import org.la4j.operation.VectorMatrixOperation;
import org.la4j.operation.VectorOperation;
import org.la4j.operation.VectorVectorOperation;
import org.la4j.vector.sparse.CompressedVector;

/**
 * A sparse vector.
 * 
 * A vector represents an array of elements. It can be re-sized.
 * 
 * A sparse data structure does not store blank elements, and instead just stores
 * elements with values. A sparse data structure can be initialized with a large
 * length but take up no storage until the space is filled with non-zero elements.
 * 
 * However, there is a performance cost. Fetch/store operations take O(log n)
 * time instead of the O(1) time of a dense data structure.
 * 
 */
public abstract class SparseVector extends Vector {

    protected int cardinality;

    public SparseVector(int length) {
        this(length, 0);
    }

    public SparseVector(int length, int cardinality) {
        super(length);
        this.cardinality = cardinality;
    }

    /**
     * Creates a zero {@link SparseVector} of the given {@code length}.
     */
    public static SparseVector zero(int length) {
        return CompressedVector.zero(length);
    }

    /**
     * Creates a zero {@link SparseVector} of the given {@code length} with
     * the given {@code capacity}.
     */
    public static SparseVector zero(int length, int capacity) {
        return CompressedVector.zero(length);
    }

    /**
     * Creates a constant {@link SparseVector} of the given {@code length} with
     * the given {@code value}.
     */
    public static SparseVector random(int length, double density, Random random) {
        return CompressedVector.random(length, density, random);
    }

    /**
     * Creates a new {@link SparseVector} from the given {@code array} with
     * compressing (copying) the underlying array.
     */
    public static SparseVector fromArray(double[] array) {
        return CompressedVector.fromArray(array);
    }

    /**
     * Parses {@link SparseVector} from the given CSV string.
     *
     * @param csv the CSV string representing a vector
     *
     * @return a parsed vector
     */
    public static SparseVector fromCSV(String csv) {
        return Vector.fromCSV(csv).to(Vectors.SPARSE);
    }

    /**
     * Parses {@link SparseVector} from the given Matrix Market string.
     *
     * @param mm the string in Matrix Market format
     *
     * @return a parsed vector
     */
    public static SparseVector fromMatrixMarket(String mm) {
        return Vector.fromMatrixMarket(mm).to(Vectors.SPARSE);
    }

    /**
     * Creates new {@link SparseVector} from collection
     *
     * @param list value list
     *
     * @return created vector
     */
    public static SparseVector fromCollection(Collection<? extends Number> list) {
        return Vector.fromCollection(list).to(Vectors.SPARSE);
    }

    /**
     * Creates new {@link SparseVector} from given index-value map
     *
     * @param map
     *
     * @return
     */
    public static SparseVector fromMap(Map<Integer, ? extends Number> map, int length) {
        return CompressedVector.fromMap(map, length);
    }

    /**
     * Returns the cardinality (the number of non-zero elements)
     * of this sparse vector.
     *
     * @return the cardinality of this vector
     */
    public int cardinality() {
        return cardinality;
    }

    /**
     * Returns the density (non-zero elements divided by total elements)
     * of this sparse vector.
     *
     * @return the density of this vector
     */
    public double density() {
        return cardinality / (double) length;
    }

    @Override
    public double get(int i) {
        return getOrElse(i, 0.0);
    }

    /**
     * Gets the specified element, or a {@code defaultValue} if there
     * is no actual element at index {@code i} in this sparse vector.
     *
     * @param i the element's index
     * @param defaultValue the default value
     *
     * @return the element of this vector or a default value
     */
    public abstract double getOrElse(int i, double defaultValue);

    /**
     * Whether or not the specified element is zero.
     *
     * @param i element's index
     *
     * @return {@code true} if specified element is zero, {@code false} otherwise
     */
    public boolean isZeroAt(int i) {
        return !nonZeroAt(i);
    }

    /**
     * * Whether or not the specified element is not zero.
     *
     * @param i element's index
     *
     * @return {@code true} if specified element is zero, {@code false} otherwise
     */
    public abstract boolean nonZeroAt(int i);

    /**
     * Folds non-zero elements of this vector with given {@code accumulator}.
     *
     * @param accumulator the vector accumulator
     *
     * @return the accumulated value
     */
    public double foldNonZero(VectorAccumulator accumulator) {
        eachNonZero(Vectors.asAccumulatorProcedure(accumulator));
        return accumulator.accumulate();
    }

    /**
     * Applies given {@code procedure} to each non-zero element of this vector.
     *
     * @param procedure the vector procedure
     */
    public void eachNonZero(VectorProcedure procedure) {
        VectorIterator it = nonZeroIterator();

        while (it.hasNext()) {
            double x = it.next();
            int i = it.index();
            procedure.apply(i, x);
        }
    }

    @Override
    public Vector add(double value) {
        Vector result = DenseVector.constant(length, value);
        VectorIterator it = nonZeroIterator();

        while (it.hasNext()) {
            double x = it.next();
            int i = it.index();
            result.set(i, x + value);
        }

        return result;
    }

    @Override
    public Vector multiply(double value) {
        Vector result = blank();
        VectorIterator it = nonZeroIterator();

        while (it.hasNext()) {
            double x = it.next();
            int i = it.index();
            result.set(i, x * value);
        }

        return result;
    }

    @Override
    public double max() {
        double max = foldNonZero(Vectors.mkMaxAccumulator());
        return (max > 0.0) ? max : 0.0;
    }

    @Override
    public double min() {
        double min = foldNonZero(Vectors.mkMinAccumulator());
        return (min < 0.0) ? min : 0.0;
    }

    @Override
    public double euclideanNorm() {
        return foldNonZero(Vectors.mkEuclideanNormAccumulator());
    }

    @Override
    public double manhattanNorm() {
        return foldNonZero(Vectors.mkManhattanNormAccumulator());
    }

    @Override
    public double infinityNorm() {
        double norm = foldNonZero(Vectors.mkInfinityNormAccumulator());
        return (norm > 0.0) ? norm : 0.0;
    }

    /**
     * Returns a non-zero vector iterator.
     *
     * @return a non-zero vector iterator
     */
    public abstract VectorIterator nonZeroIterator();

    @Override
    public <T extends Vector> T to(VectorFactory<T> factory) {
        T result = factory.apply(length);
        VectorIterator it = nonZeroIterator();

        while (it.hasNext()) {
            double x = it.next();
            int i = it.index();
            result.set(i, x);
        }

        return result;
    }

    @Override
    public int hashCode() {
        int result = 17;
        VectorIterator it = nonZeroIterator();

        while (it.hasNext()) {
            long x = it.next().longValue();
            long i = (long) it.index();
            result = 37 * result + (int) (x ^ (x >>> 32));
            result = 37 * result + (int) (i ^ (i >>> 32));
        }

        return result;
    }

    @Override
    public <T> T apply(VectorOperation<T> operation) {
        operation.ensureApplicableTo(this);
        return operation.apply(this);
    }

    @Override
    public <T> T apply(VectorVectorOperation<T> operation, Vector that) {
        return that.apply(operation.partiallyApply(this));
    }

    @Override
    public <T> T apply(VectorMatrixOperation<T> operation, Matrix that) {
        return that.apply(operation.partiallyApply(this));
    }

    @Override
    public Matrix toRowMatrix() {
        VectorIterator it = nonZeroIterator();
        Matrix result = CRSMatrix.zero(1, length);

        while (it.hasNext()) {
            double x = it.next();
            int j = it.index();
            result.set(0, j, x);
        }

        return result;
    }

    @Override
    public Matrix toColumnMatrix() {
        VectorIterator it = nonZeroIterator();
        Matrix result = ColumnMajorSparseMatrix.zero(length, 1);

        while (it.hasNext()) {
            double x = it.next();
            int i = it.index();
            result.set(i, 0, x);
        }

        return result;
    }

    @Override
    public Matrix toDiagonalMatrix() {
        VectorIterator it = nonZeroIterator();
        Matrix result = RowMajorSparseMatrix.zero(length, length);

        while (it.hasNext()) {
            double x = it.next();
            int i = it.index();
            result.set(i, i, x);
        }

        return result;
    }

    @Override
    public String toMatrixMarket(NumberFormat formatter) {
        StringBuilder out = new StringBuilder();
        VectorIterator it = nonZeroIterator();

        out.append("%%MatrixMarket vector coordinate real\n");
        out.append(length).append(' ').append(cardinality).append('\n');
        while (it.hasNext()) {
            double x = it.next();
            int i = it.index();
            out.append(i + 1).append(' ').append(formatter.format(x)).append('\n');
        }

        return out.toString();
    }

    /**
     * Ensures the provided index is in the bounds of this {@link SparseVector}.
     * 
     * @param i The index to check.
     */
    protected void ensureIndexIsInBounds(int i) {
        if (i < 0 || i >= length) {
            throw new IndexOutOfBoundsException("Index '" + i + "' is invalid.");
        }
    }
}
