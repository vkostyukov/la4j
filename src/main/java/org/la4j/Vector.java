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
 * Contributor(s): Daniel Renshaw
 *                 Jakob Moellers
 *                 Maxim Samoylov
 *                 Miron Aseev
 *                 Ewald Grusk
 *
 */

package org.la4j;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

import org.la4j.iterator.VectorIterator;
import org.la4j.vector.VectorFactory;
import org.la4j.vector.DenseVector;
import org.la4j.vector.functor.VectorAccumulator;
import org.la4j.vector.functor.VectorFunction;
import org.la4j.vector.functor.VectorPredicate;
import org.la4j.vector.functor.VectorProcedure;
import org.la4j.operation.VectorMatrixOperation;
import org.la4j.operation.VectorOperation;
import org.la4j.operation.VectorVectorOperation;
import org.la4j.vector.SparseVector;

/**
 * A vector represents an array of elements. It can be re-sized.
 */
public abstract class Vector implements Iterable<Double> {

    private static final String DEFAULT_DELIMITER = " ";
    private static final NumberFormat DEFAULT_FORMATTER = new DecimalFormat("0.000");

    /**
     * Length of this vector.
     */
    protected int length;

    /**
     * Creates a vector of zero length.
     */
    public Vector() {
        this(0);
    }

    /**
     * Creates a vector of given {@code length}.
     *
     * @param length the length of the vector
     */
    public Vector(int length) {
        ensureLengthIsCorrect(length);
        this.length = length;
    }

    /**
     * Creates a zero {@link Vector} of the given {@code length}.
     */
    public static Vector zero(int length) {
        return length > 1000 ? SparseVector.zero(length) : DenseVector.zero(length);
    }

    /**
     * Creates a constant {@link Vector} of the given {@code length} with
     * the given {@code value}.
     */
    public static Vector constant(int length, double value) {
        return DenseVector.constant(length, value);
    }

    /**
     * Creates an unit {@link Vector} of the given {@code length}.
     */
    public static Vector unit(int length) {
        return DenseVector.constant(length, 1.0);
    }

    /**
     * Creates a random {@link Vector} of the given {@code length} with
     * the given {@code Random}.
     */
    public static Vector random(int length, Random random) {
        return DenseVector.random(length, random);
    }

    /**
     * Creates a new {@link Vector} from the given {@code array} w/o
     * copying the underlying array.
     */
    public static Vector fromArray(double[] array) {
        return DenseVector.fromArray(array);
    }

    /**
     * Parses {@link Vector} from the given CSV string.
     *
     * @param csv the CSV string representing a vector
     *
     * @return a parsed vector
     */
    public static Vector fromCSV(String csv) {
        StringTokenizer tokenizer = new StringTokenizer(csv, ", ");
        int estimatedLength = csv.length() / (5 + 2) + 1; // 5 symbols per element "0.000"
                                                          // 2 symbols for delimiter ", "
        Vector result = DenseVector.zero(estimatedLength);

        int i = 0;
        while (tokenizer.hasMoreTokens()) {
            if (result.length() == i) {
                result = result.copyOfLength((i * 3) / 2 + 1);
            }

            double x = Double.parseDouble(tokenizer.nextToken());
            result.set(i++, x);
        }

        return result.copyOfLength(i);
    }


    /**
     * Parses {@link Vector} from the given Matrix Market string.
     *
     * @param mm the string in Matrix Market format
     *
     * @return a parsed vector
     */
    public static Vector fromMatrixMarket(String mm) {
        StringTokenizer body = new StringTokenizer(mm);

        if (!"%%MatrixMarket".equals(body.nextToken())) {
            throw new IllegalArgumentException("Wrong input file format: can not read header '%%MatrixMarket'.");
        }

        String object = body.nextToken();
        if (!"vector".equals(object)) {
            throw new IllegalArgumentException("Unexpected object: " + object + ".");
        }

        String format = body.nextToken();
        if (!"coordinate".equals(format) && !"array".equals(format)) {
            throw new IllegalArgumentException("Unknown format: " + format + ".");
        }

        String field = body.nextToken();
        if (!"real".equals(field)) {
            throw new IllegalArgumentException("Unknown field type: " + field + ".");
        }

        int length = Integer.parseInt(body.nextToken());
        if ("coordinate".equals(format)) {
            int cardinality = Integer.parseInt(body.nextToken());
            Vector result = SparseVector.zero(length, cardinality);

            for (int k = 0; k < cardinality; k++) {
                int i = Integer.parseInt(body.nextToken());
                double x = Double.parseDouble(body.nextToken());
                result.set(i - 1, x);
            }

            return result;
        } else {
            Vector result = DenseVector.zero(length);

            for (int i = 0; i < length; i++) {
                result.set(i, Double.valueOf(body.nextToken()));
            }

            return result;
        }
    }

    /**
     * Creates new {@link org.la4j.vector.dense.BasicVector} from {@code list}
     */
    public static Vector fromCollection(Collection<? extends Number> list) {
        return DenseVector.fromCollection(list);
    }

    /**
     * Creates new {@link org.la4j.vector.SparseVector} from {@code list}
     */
    public static Vector fromMap(Map<Integer, ? extends Number> map, int length) {
        return SparseVector.fromMap(map, length);
    }

    //
    // ============ ABSTRACT METHODS ============
    //

    /**
     * Gets the specified element of this vector.
     * 
     * @param i element's index
     * @return the element of this vector
     */
    public abstract double get(int i);

    /**
     * Sets the specified element of this matrix to given {@code value}.
     *
     * @param i element's index
     * @param value element's new value
     */
    public abstract void set(int i, double value);

    /**
     * Creates a blank (an empty vector) copy of this vector with the given
     * {@code length}.
     *
     * @param length the length of the blank vector
     *
     * @return blank vector
     */
    public abstract Vector blankOfLength(int length);

    /**
     * Copies this vector into the new vector with specified {@code length}.
     *
     * @param length the length of new vector
     *
     * @return the copy of this vector with new length
     */
    public abstract Vector copyOfLength(int length);

    /**
     * Converts this vector to matrix with only one row.
     *
     * @return the row matrix
     */
    public abstract Matrix toRowMatrix();

    /**
     * Converts this vector to matrix with only one column.
     *
     * @return the column matrix
     */
    public abstract Matrix toColumnMatrix();

    /**
     * Converts this vector to a diagonal matrix.
     *
     * @return a diagonal matrix
     */
    public abstract Matrix toDiagonalMatrix();

    /**
     * Pipes this vector to a given {@code operation}.
     *
     * @param operation the vector operation
     *                  (an operation that take vector and returns {@code T})
     * @param <T> the result type
     *
     * @return the result of an operation applied to this vector
     */
    public abstract <T> T apply(VectorOperation<T> operation);

    /**
     * Pipes this vector to a given {@code operation}.
     *
     * @param operation the vector-vector operation
     *                  (an operation that takes two vectors and returns {@code T})
     * @param <T> the result type
     * @param that the right hand vector for the given operation
     *
     * @return the result of an operation applied to this and {@code that} vector
     */
    public abstract <T> T apply(VectorVectorOperation<T> operation, Vector that);

    /**
     * Pipes this vector to a given {@code operation}.
     *
     * @param operation the vector-matrix operation
     *                  (an operation that takes vector and matrix and returns {@code T})
     * @param <T> the result type
     * @param that the right hand matrix for the given operation
     *
     * @return the result of an operation applied to this vector and {@code that} matrix
     */
    public abstract <T> T apply(VectorMatrixOperation<T> operation, Matrix that);

    /**
     * Encodes this vector into a byte array.
     *
     * @return a byte array representing this vector
     */
    public abstract byte[] toBinary();

    /**
     * Converts this vector into the string in Matrix Market format
     * using the given {@code formatter};
     *
     * @param formatter the number formater
     *
     * @return a Matrix Market string representing this vector
     */
    public abstract String toMatrixMarket(NumberFormat formatter);

    //
    // ============ CONCRETE METHODS ============
    //

    /**
     * Sets all elements of this vector to given {@code value}.
     *
     * @param value the element's new value
     */
    public void setAll(double value) {
        VectorIterator it = iterator();

        while (it.hasNext()) {
            it.next();
            it.set(value);
        }
    }

    /**
     * Returns the length of this vector.
     * 
     * @return length of this vector
     */
    public int length() {
        return length;
    }

    /**
     * Adds given {@code value} (v) to this vector (X).
     * 
     * @param value the right hand value for addition
     *
     * @return X + v
     */
    public Vector add(double value) {
        VectorIterator it = iterator();
        Vector result = blank();

        while (it.hasNext()) {
            double x = it.next();
            int i = it.index();
            result.set(i, x + value);
        }

        return result;
    }

    /**
     * Adds given {@code vector} (X) to this vector (Y).
     * 
     * @param that the right hand vector for addition
     *
     * @return X + Y
     */
    public Vector add(Vector that) {
        return apply(LinearAlgebra.OO_PLACE_VECTORS_ADDITION, that);
    }

    /**
     * Multiplies this vector (X) by given {@code value} (v).
     * 
     * @param value the right hand value for multiplication
     *
     * @return X * v
     */
    public Vector multiply(double value) {
        VectorIterator it = iterator();
        Vector result = blank();

        while (it.hasNext()) {
            double x = it.next();
            int i = it.index();
            result.set(i, x * value);
        }

        return result;
    }

    /**
     * Calculates the Hadamard (element-wise) product of this vector and given {@code that}.
     * 
     * @param that the right hand vector for Hadamard product
     *
     * @return the Hadamard product of two vectors
     */
    public Vector hadamardProduct(Vector that) {
        return apply(LinearAlgebra.OO_PLACE_VECTOR_HADAMARD_PRODUCT, that);
    }

    /**
     * Multiples this vector (X) by given {@code that} (A).
     * 
     * @param that the right hand matrix for multiplication
     *
     * @return X * A
     */
    public Vector multiply(Matrix that) {
        return apply(LinearAlgebra.OO_PLACE_VECTOR_BY_MATRIX_MULTIPLICATION, that);
    }

    /**
     * Subtracts given {@code value} (v) from this vector (X).
     * 
     * @param value the right hand value for subtraction
     *
     * @return X - v
     */
    public Vector subtract(double value) {
        return add(-value);
    }

    /**
     * Subtracts given {@code that} (Y) from this vector (X).
     * 
     * @param that the right hand vector for subtraction
     *
     * @return X - Y
     */
    public Vector subtract(Vector that) {
        return apply(LinearAlgebra.OO_PLACE_VECTORS_SUBTRACTION, that);
    }

    /**
     * Divides this vector (X) by given {@code value} (v).
     * 
     * @param value the right hand value for division
     *
     * @return X / v
     */
    public Vector divide(double value) {
        return multiply(1.0 / value);
    }

    /**
     * Multiplies up all elements of this vector.
     * 
     * @return product of all elements of this vector
     */
    public double product() {
        return fold(Vectors.asProductAccumulator(1.0));
    }

    /**
     * Summarizes all elements of the vector
     * 
     * @return sum of all elements of the vector
     */
    public double sum() {
        return fold(Vectors.asSumAccumulator(0.0));
    }

    /**
     * Calculates the inner product of this vector and given {@code that}.
     * 
     * @param that the right hand vector for inner product
     *
     * @return the inner product of two vectors
     */
    public double innerProduct(Vector that) {
        return apply(LinearAlgebra.OO_PLACE_INNER_PRODUCT, that);
    }

    /**
     * Calculates the outer product of this vector and given {@code that}.
     * 
     * @param that the the right hand vector for outer product
     *
     * @return the outer product of two vectors
     */
    public Matrix outerProduct(Vector that) {
        return apply(LinearAlgebra.OO_PLACE_OUTER_PRODUCT, that);
    }

    /**
     * Calculates the cosine similarity between this vector and given {@code that}.
     *
     * @param that the vector to calculated cosine similarity with
     *
     * @return the cosine similarity of the two vectors
     */
    public double cosineSimilarity(Vector that) {
        return this.innerProduct(that) / (this.euclideanNorm() * that.euclideanNorm());
    }

    /**
     * Calculates an Euclidean norm of this vector.
     *
     * @return an Euclidean norm
     */
    public double norm() {
        return euclideanNorm();
    }

    /**
     * Calculates an Euclidean norm of this vector.
     *
     * @return an Euclidean norm
     */
    public double euclideanNorm() {
        return fold(Vectors.mkEuclideanNormAccumulator());
    }

    /**
     * Calculates a Manhattan norm of this vector.
     *
     * @return a Manhattan norm
     */
    public double manhattanNorm() {
        return fold(Vectors.mkManhattanNormAccumulator());
    }

    /**
     * Calculates an Infinity norm of this vector.
     *
     * @return an Infinity norm
     */
    public double infinityNorm() {
        return fold(Vectors.mkInfinityNormAccumulator());
    }

    /**
     * Swaps the specified elements of this vector.
     *
     * @param i element's index
     * @param j element's index
     */
    public void swapElements(int i, int j) {
        if (i != j) {
            double s = get(i);
            set(i, get(j));
            set(j, s);
        }
    }

    /**
     * Creates a blank (an empty vector with same length) copy of this vector.
     * 
     * @return blank vector
     */
    public Vector blank() {
        return blankOfLength(length);
    }

    /**
     * Copies this vector.
     * 
     * @return the copy of this vector
     */
    public Vector copy() {
        return copyOfLength(length);
    }

    /**
     * Shuffles this vector.
     *
     * <p>
     * Copies this vector in the new vector that contains the same elements but with
     * the elements shuffled around (which might also result in the same vector
     * (all outcomes are equally probable)).
     * </p>
     * 
     * @return the shuffled vector
     */
    public Vector shuffle() {
        Vector result = copy();

        // Conduct Fisher-Yates shuffle
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            int j = random.nextInt(length - i) + i;
            swapElements(i, j);
        }

        return result;
    }

    /**
     * Retrieves the specified sub-vector of this vector. The sub-vector is specified by
     * interval of indices.
     * 
     * @param from the beginning of indices interval
     * @param until the ending of indices interval
     *
     * @return the sub-vector of this vector
     */
    public Vector slice(int from, int until) {
        if (until - from < 0) {
            fail("Wrong slice range: [" + from + ".." + until + "].");
        }

        Vector result = blankOfLength(until - from);

        for (int i = from; i < until; i++) {
            result.set(i - from, get(i));
        }

        return result;
    }

    /**
     * Retrieves the specified sub-vector of this vector. The sub-vector is specified by
     * interval of indices. The left point of interval is fixed to zero.
     *
     * @param until the ending of indices interval
     *
     * @return the sub-vector of this vector
     */
    public Vector sliceLeft(int until) {
        return slice(0, until);
    }

    /**
     * Retrieves the specified sub-vector of this vector. The sub-vector is specified by
     * interval of indices. The right point of interval is fixed to vector's length.
     *
     * @param from the beginning of indices interval
     *
     * @return the sub-vector of this vector
     */
    public Vector sliceRight(int from) {
        return slice(from, length);
    }

    /**
     * Returns a new vector with the selected elements.
     *
     * @param indices the array of indices
     *
     * @return the new vector with the selected elements
     */
    public Vector select(int[] indices) {
        int newLength = indices.length;

        if (newLength == 0) {
            fail("No elements selected.");
        }

        Vector result = blankOfLength(newLength);

        for (int i = 0; i < newLength; i++) {
            result.set(i, get(indices[i]));
        }

        return result;
    }

    /**
     * Applies given {@code procedure} to each element of this vector.
     *
     * @param procedure the vector procedure
     */
    public void each(VectorProcedure procedure) {
        VectorIterator it = iterator();

        while (it.hasNext()) {
            double x = it.next();
            int i = it.index();
            procedure.apply(i, x);
        }
    }

    /**
     * Searches for the maximum value of the elements of this vector.
     *
     * @return the maximum value of this vector
     */
    public double max() {
        return fold(Vectors.mkMaxAccumulator());
    }

    /**
     * Searches for the minimum value of the elements of this vector.
     *
     * @return the minimum value of this vector
     */
    public double min() {
        return fold(Vectors.mkMinAccumulator());
    }

    /**
     * Builds a new vector by applying given {@code function} to each element 
     * of this vector.
     *
     * @param function the vector function
     *
     * @return the transformed vector
     */
    public Vector transform(VectorFunction function) {
        VectorIterator it = iterator();
        Vector result = blank();

        while (it.hasNext()) {
            double x = it.next();
            int i = it.index();
            result.set(i, function.evaluate(i, x));
        }

        return result;
    }

    /**
     * Updates all elements of this vector by applying given {@code function}.
     * 
     * @param function the the vector function
     */
    public void update(VectorFunction function) {
        VectorIterator it = iterator();

        while (it.hasNext()) {
            double x = it.next();
            int i = it.index();
            it.set(function.evaluate(i, x));
        }
    }

    /**
     * Updates the specified element of this vector by applying given {@code function}.
     *
     * @param i element's index
     * @param function the vector function
     */
    public void updateAt(int i, VectorFunction function) {
        set(i, function.evaluate(i, get(i)));
    }

    /**
     * Folds all elements of this vector with given {@code accumulator}.
     * 
     * @param accumulator the vector accumulator
     *
     * @return the accumulated value
     */
    public double fold(VectorAccumulator accumulator) {
        each(Vectors.asAccumulatorProcedure(accumulator));
        return accumulator.accumulate();
    }

    /**
     * Checks whether this vector compiles with given {@code predicate} or not.
     * 
     * @param predicate the vector predicate
     *
     * @return whether this vector compiles with predicate
     */
    public boolean is(VectorPredicate predicate) {
        boolean result = true;
        VectorIterator it = iterator();

        while (it.hasNext()) {
            double x = it.next();
            int i = it.index();
            result = result && predicate.test(i, x);
        }

        return result;
    }

    /**
     * Checks whether this vector compiles with given {@code predicate} or not.
     *
     * @param predicate the vector predicate
     *
     * @return whether this vector compiles with predicate
     */
    public boolean non(VectorPredicate predicate) {
        return !is(predicate);
    }

    /**
     * Returns true when vector is equal to given {@code that} vector with given
     * {@code precision}.
     *
     * @param that vector
     * @param precision given precision
     *
     * @return equals of this matrix to that
     */
    public boolean equals(Vector that, double precision) {
        if (this == that) {
            return true;
        }

        if (this.length != that.length()) {
            return false;
        }

        boolean result = true;

        for (int i = 0; result && i < length; i++) {
            double a = get(i);
            double b = that.get(i);
            double diff = Math.abs(a - b);
            result = (a == b) ||
                    (diff < precision || diff / Math.max(Math.abs(a), Math.abs(b)) < precision);
        }

        return result;
    }

    /**
     * Converts this vector into the string representation.
     *
     * @param formatter the number formatter
     *
     * @return the vector converted to a string
     */
    public String mkString(NumberFormat formatter) {
        return mkString(formatter, DEFAULT_DELIMITER);
    }

    /**
     * Converts this vector into the string representation.
     *
     * @param formatter the number formatter
     * @param delimiter the element's delimiter
     *
     * @return the vector converted to a string
     */
    public String mkString(NumberFormat formatter, String delimiter) {
        StringBuilder sb = new StringBuilder();
        VectorIterator it = iterator();

        while (it.hasNext()) {
            double x = it.next();
            int i = it.index();
            sb.append(formatter.format(x))
                    .append((i < length - 1 ? delimiter : ""));
        }

        return sb.toString();
    }

    /**
     * Converts this vector into a string representation.
     *
     * @return a string representation of this vector
     */
    @Override
    public String toString() {
        return mkString(DEFAULT_FORMATTER, DEFAULT_DELIMITER);
    }

    /**
     * Checks where this vector is equal to the given object {@code o}.
     */
    @Override
    public boolean equals(Object o) {
        return o != null && (o instanceof Vector) && equals((Vector) o, Vectors.EPS);
    }

    /**
     * Calculates the hash-code of this vector.
     */
    @Override
    public int hashCode() {
        VectorIterator it = iterator();
        int result = 17;

        while (it.hasNext()) {
            long value = it.next().longValue();
            result = 37 * result + (int) (value ^ (value >>> 32));
        }

        return result;
    }

    /**
     * Returns a vector iterator.
     *
     * @return a vector iterator.
     */
    @Override
    public VectorIterator iterator() {
        return new VectorIterator(length) {
            private int i = -1;

            @Override
            public int index() {
                return i;
            }

            @Override
            public double get() {
                return Vector.this.get(i);
            }

            @Override
            public void set(double value) {
                Vector.this.set(i, value);
            }

            @Override
            public boolean hasNext() {
                return i + 1 < length;
            }

            @Override
            public Double next() {
                if(!hasNext()) {
                    throw new NoSuchElementException();
                }
                i++;
                return get();
            }
        };
    }

    /**
     * Converts this vector using the given {@code factory}.
     *
     * @param factory the factory that creates an output vector
     * @param <T> type of the result vector
     *
     * @return a converted vector
     */
    public <T extends Vector> T to(VectorFactory<T> factory) {
        VectorIterator it = iterator();
        T result = factory.apply(length);

        while (it.hasNext()) {
            double x = it.next();
            int i = it.index();
            result.set(i, x);
        }

        return result;
    }

    /**
     * Converts this vector into a {@link org.la4j.vector.DenseVector}.
     *
     * @return a dense vector
     */
    public DenseVector toDenseVector() {
        return to(Vectors.DENSE);
    }

    /**
     * Converts this vector into a {@link org.la4j.vector.SparseVector}.
     *
     * @return a sparse vector
     */
    public SparseVector toSparseVector() {
        return to(Vectors.SPARSE);
    }

    /**
     * Converts this vector into the CSV (Comma Separated Value) string.
     *
     * @return a CSV string representing this vector
     */
    public String toCSV() {
        return toCSV(DEFAULT_FORMATTER);
    }

    /**
     * Converts this vector into the CSV (Comma Separated Value) string
     * using the given {@code formatter}.
     *
     * @return a CSV string representing this vector
     */
    public String toCSV(NumberFormat formatter) {
        return mkString(formatter, ", ");
    }

    /**
     * Converts this vector into the string in Matrix Market format.
     *
     * @return a Matrix Market string representing this vector
     */
    public String toMatrixMarket() {
        return toMatrixMarket(DEFAULT_FORMATTER);
    }

    protected void ensureLengthIsCorrect(int length) {
        if (length < 0) {
            fail("Wrong vector length: " + length);
        }
        if (length == Integer.MAX_VALUE) {
            fail("Wrong vector length: use 'Integer.MAX_VALUE - 1' instead.");
        }
    }

    protected void fail(String message) {
        throw new IllegalArgumentException(message);
    }
}
