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
 * 
 */

package org.la4j.vector;

import java.io.Externalizable;
import java.text.NumberFormat;
import org.la4j.factory.Factory;
import org.la4j.iterator.VectorIterator;
import org.la4j.matrix.Matrix;
import org.la4j.vector.dense.DenseVector;
import org.la4j.vector.functor.VectorAccumulator;
import org.la4j.vector.functor.VectorFunction;
import org.la4j.vector.functor.VectorPredicate;
import org.la4j.vector.functor.VectorProcedure;
import org.la4j.operation.VectorMatrixOperation;
import org.la4j.operation.VectorOperation;
import org.la4j.operation.VectorVectorOperation;
import org.la4j.vector.sparse.SparseVector;

/**
 * The real vector interface.
 * 
 * A vector represents an array of elements. It can be re-sized.
 */
public interface Vector extends Externalizable, Iterable<Double> {

    /**
     * Gets the specified element of this vector.
     * 
     * @param i element's index
     * @return the element of this vector
     */
    double get(int i);

    /**
     * Sets the specified element of this matrix to given {@code value}.
     *
     * @param i element's index
     * @param value element's new value
     */
    void set(int i, double value);

    /**
     * Sets all elements of this vector to given {@code value}.
     *
     * @param value the element's new value
     */
    void setAll(double value);

    /**
     * This method is deprecated. Use {@link Vector#setAll(double)} (double)} instead.
     *
     * Assigns all the elements of this vector to zero.
     */
    @Deprecated
    void clear();

    /**
     * This method is deprecated. Use {@link Factory#createConstantVector(int, double)} instead.
     *
     * Assigns all elements of this vector to given {@code value}.
     *
     * @param value the element's new value
     */
    @Deprecated
    void assign(double value);

    /**
     * Returns the length of this vector.
     * 
     * @return length of this vector
     */
    int length();

    /**
     * Adds given {@code value} (v) to this vector (X).
     * 
     * @param value the right hand value for addition
     *
     * @return X + v
     */
    Vector add(double value);

    /**
     * Adds given {@code value} (v) to this vector (X).
     * 
     * @param value the right hand value for addition
     * @param factory the factory of result vector
     *
     * @return X + v
     */
    @Deprecated
    Vector add(double value, Factory factory);

    /**
     * Adds given {@code vector} (X) to this vector (Y).
     * 
     * @param that the right hand vector for addition
     *
     * @return X + Y
     */
    Vector add(Vector that);

    /**
     * Adds given {@code vector} (X) to this vector (Y).
     *
     * @param that the right hand vector for addition
     * @param factory the factory of result vector
     *
     * @return X + Y
     */
    @Deprecated
    Vector add(Vector that, Factory factory);

    /**
     * Multiplies this vector (X) by given {@code value} (v).
     * 
     * @param value the right hand value for multiplication
     *
     * @return X * v
     */
    Vector multiply(double value);

    /**
     * Multiplies this vector (X) by given {@code value} (v).
     *
     * @param value the right hand value for multiplication
     * @param factory the factory of result vector
     *
     * @return X * v
     */
    @Deprecated
    Vector multiply(double value, Factory factory);

    /**
     * Calculates the Hadamard (element-wise) product of this vector and given {@code that}.
     * 
     * @param that the right hand vector for Hadamard product
     *
     * @return the Hadamard product of two vectors
     */
    Vector hadamardProduct(Vector that);

    /**
     * Calculates the Hadamard (element-wise) product of this vector and given {@code that}.
     *
     * @param that the right hand vector for Hadamard product
     * @param factory the factory of result vector
     *
     * @return the Hadamard product of two vectors
     */
    @Deprecated
    Vector hadamardProduct(Vector that, Factory factory);

    /**
     * Multiples this vector (X) by given {@code that} (A).
     * 
     * @param that the right hand matrix for multiplication
     *
     * @return X * A
     */
    Vector multiply(Matrix that);

    /**
     * Multiples this vector (X) by given {@code that} (A).
     *
     * @param that the right hand matrix for multiplication
     * @param factory the factory of result vector
     *
     * @return X * A
     */
    @Deprecated
    Vector multiply(Matrix that, Factory factory);

    /**
     * Subtracts given {@code value} (v) from this vector (X).
     * 
     * @param value the right hand value for subtraction
     *
     * @return X - v
     */
    Vector subtract(double value);

    /**
     * Subtracts given {@code value} (v) from this vector (X).
     *
     * @param value the right hand value for subtraction
     * @param factory the factory of result vector
     *
     * @return X - v
     */
    @Deprecated
    Vector subtract(double value, Factory factory);

    /**
     * Subtracts given {@code that} (Y) from this vector (X).
     * 
     * @param that the right hand vector for subtraction
     *
     * @return X - Y
     */
    Vector subtract(Vector that);

    /**
     * Subtracts given {@code vector} (Y) from this vector (X).
     *
     * @param that the right hand vector for subtraction
     * @param factory the factory of result vector
     *
     * @return X - Y
     */
    @Deprecated
    Vector subtract(Vector that, Factory factory);

    /**
     * Divides this vector (X) by given {@code value} (v).
     * 
     * @param value the right hand value for division
     *
     * @return X / v
     */
    Vector divide(double value);

    /**
     * Divides this vector (X) by given {@code value} (v).
     *
     * @param value the right hand value for division
     * @param factory the factory of result vector
     *
     * @return X / v
     */
    @Deprecated
    Vector divide(double value, Factory factory);

    /**
     * Multiplies up all elements of this vector.
     * 
     * @return product of all elements of this vector
     */
    double product();

    /**
     * Summarizes all elements of the vector
     * 
     * @return sum of all elements of the vector
     */
    double sum();

    /**
     * Calculates the inner product of this vector and given {@code that}.
     * 
     * @param that the right hand vector for inner product
     *
     * @return the inner product of two vectors
     */
    double innerProduct(Vector that);

    /**
     * Calculates the outer product of this vector and given {@code that}.
     * 
     * @param that the the right hand vector for outer product
     *
     * @return the outer product of two vectors
     */
    Matrix outerProduct(Vector that);

    /**
     * Calculates the outer product of this vector and given {@code that}.
     *
     * @param that the the right hand vector for outer product
     * @param factory the factory of result vector
     *
     * @return the outer product of two vectors
     */
    @Deprecated
    Matrix outerProduct(Vector that, Factory factory);

    /**
     * Calculates an Euclidean norm of this vector.
     *
     * @return an Euclidean norm
     */
    double norm();

    /**
     * Calculates an Euclidean norm of this vector.
     *
     * @return an Euclidean norm
     */
    double euclideanNorm();

    /**
     * Calculates a Manhattan norm of this vector.
     *
     * @return a Manhattan norm
     */
    double manhattanNorm();

    /**
     * Calculates an Infinity norm of this vector.
     *
     * @return an Infinity norm
     */
    double infinityNorm();

    /**
     * This method is deprecated. Use {@link Vector} instead.
     *
     * Swaps the specified elements of this vector.
     *
     * @param i element's index
     * @param j element's index
     */
    @Deprecated
    void swap(int i, int j);

    /**
     * Swaps the specified elements of this vector.
     *
     * @param i element's index
     * @param j element's index
     */
    void swapElements(int i, int j);

    /**
     * Creates a blank (an empty vector with same length) copy of this vector.
     * 
     * @return blank vector
     */
    Vector blank();

    /**
     * Creates a blank (an empty vector) copy of this vector with the given
     * {@code length}.
     *
     * @param length the length of the blank vector
     *
     * @return blank vector
     */
    Vector blankOfLength(int length);

    /**
     * Creates a blank (an empty vector with same length) copy of this vector.
     *
     * @param factory the factory of result vector
     *
     * @return blank vector
     */
    @Deprecated
    Vector blank(Factory factory);

    /**
     * Copies this vector.
     * 
     * @return the copy of this vector
     */
    Vector copy();

    /**
     * Copies this vector.
     *
     * @param factory the factory of result vector
     *
     * @return the copy of this vector
     */
    @Deprecated
    Vector copy(Factory factory);

    /**
     * This method is deprecated, use {@link Vector#copyOfLength(int)} instead.
     *
     * Copies this vector into the new vector with specified {@code length}.
     * 
     * @param length the length of new vector
     *
     * @return the copy of this vector with new length
     */
    @Deprecated
    Vector resize(int length);

    /**
     * This method is deprecated, use {@link Vector#copyOfLength(int)} instead.
     *
     * Copies this vector into the new vector with specified {@code length}.
     *
     * @param length the length of new vector
     * @param factory the factory of result vector
     *
     * @return the copy of this vector with new length
     */
    @Deprecated
    Vector resize(int length, Factory factory);

    /**
     * Copies this vector into the new vector with specified {@code length}.
     *
     * @param length the length of new vector
     *
     * @return the copy of this vector with new length
     */
    Vector copyOfLength(int length);

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
    Vector shuffle();

    /**
     * Shuffles this vector.
     *
     * Copies this vector in the new vector that contains the same elements but with
     * the elements shuffled around (which might also result in the same vector,
     * since all outcomes are equally probable).
     *
     * @param factory the factory of result vector
     *
     * @return the shuffled vector
     */
    @Deprecated
    Vector shuffle(Factory factory);

    /**
     * Retrieves the specified sub-vector of this vector. The sub-vector is specified by
     * interval of indices.
     * 
     * @param from the beginning of indices interval
     * @param until the ending of indices interval
     *
     * @return the sub-vector of this vector
     */
    Vector slice(int from, int until);

    /**
     * Retrieves the specified sub-vector of this vector. The sub-vector is specified by
     * interval of indices.
     *
     * @param from the beginning of indices interval
     * @param until the ending of indices interval
     * @param factory the factory of result vector
     *
     * @return the sub-vector of this vector
     */
    @Deprecated
    Vector slice(int from, int until, Factory factory);

    /**
     * Retrieves the specified sub-vector of this vector. The sub-vector is specified by
     * interval of indices. The left point of interval is fixed to zero.
     *
     * @param until the ending of indices interval
     *
     * @return the sub-vector of this vector
     */
    Vector sliceLeft(int until);

    /**
     * Retrieves the specified sub-vector of this vector. The sub-vector is specified by
     * interval of indices. The left point of interval is fixed to zero.
     *
     * @param until the ending of indices interval
     * @param factory the factory of result vector
     *
     * @return the sub-vector of this vector
     */
    @Deprecated
    Vector sliceLeft(int until, Factory factory);

    /**
     * Retrieves the specified sub-vector of this vector. The sub-vector is specified by
     * interval of indices. The right point of interval is fixed to vector's length.
     *
     * @param from the beginning of indices interval
     *
     * @return the sub-vector of this vector
     */
    Vector sliceRight(int from); 

    /**
     * Retrieves the specified sub-vector of this vector. The sub-vector is specified by
     * interval of indices. The right point of interval is fixed to vector's length.
     *
     * @param from the beginning of indices interval
     * @param factory the factory of result vector
     *
     * @return the sub-vector of this vector
     */
    @Deprecated
    Vector sliceRight(int from, Factory factory);

    /**
     * Returns a new vector with the selected elements.
     *
     * @param indices the array of indices
     *
     * @return the new vector with the selected elements
     */
    Vector select(int[] indices);

    /**
     * Returns a new vector with the selected elements.
     *
     * @param indices the array of indices
     * @param factory the factory of the result vector
     *
     * @return the new vector with the selected elements
     */
    @Deprecated
    Vector select(int[] indices, Factory factory);

    /**
     * Applies given {@code procedure} to each element of this vector.
     *
     * @param procedure the vector procedure
     */
    void each(VectorProcedure procedure);

    /**
     * Searches for the maximum value of the elements of this vector.
     *
     * @return the maximum value of this vector
     */
    double max();

    /**
     * Searches for the minimum value of the elements of this vector.
     *
     * @return the minimum value of this vector
     */
    double min();

    /**
     * Builds a new vector by applying given {@code function} to each element 
     * of this vector.
     *
     * @param function the vector function
     *
     * @return the transformed vector
     */
    Vector transform(VectorFunction function);

    /**
     * Builds a new vector by applying given {@code function} to each element 
     * of this vector.
     *
     * @param function the vector function
     * @param factory the factory of result vector
     *
     * @return the transformed vector
     */
    @Deprecated
    Vector transform(VectorFunction function, Factory factory);

    /**
     * Updates all elements of this vector by applying given {@code function}.
     * 
     * @param function the the vector function
     */
    void update(VectorFunction function); 

    /**
     * Updates the specified element of this vector by applying given {@code function}.
     *
     * <p/>
     *
     * This method is deprecated. Use {@link Vector#updateAt(int, VectorFunction)}
     * instead.
     * 
     * @param i element's index
     * @param function the vector function
     */
    @Deprecated
    void update(int i, VectorFunction function);

    /**
     * Updates the specified element of this vector by applying given {@code function}.
     *
     * @param i element's index
     * @param function the vector function
     */
    void updateAt(int i, VectorFunction function);

    /**
     * Folds all elements of this vector with given {@code accumulator}.
     * 
     * @param accumulator the vector accumulator
     *
     * @return the accumulated value
     */
    double fold(VectorAccumulator accumulator);

    /**
     * Checks whether this vector compiles with given {@code predicate} or not.
     * 
     * @param predicate the vector predicate
     *
     * @return whether this vector compiles with predicate
     */
    boolean is(VectorPredicate predicate);

    /**
     * Checks whether this vector compiles with given {@code predicate} or not.
     *
     * @param predicate the vector predicate
     *
     * @return whether this vector compiles with predicate
     */
    boolean non(VectorPredicate predicate);

    /**
     * Converts this vector to matrix with only one row.
     *
     * @return the row matrix
     */
    Matrix toRowMatrix();

    /**
     * Converts this vector to matrix with only one row.
     *
     * @param factory the factory of result matrix
     *
     * @return the row matrix
     */
    @Deprecated
    Matrix toRowMatrix(Factory factory);

    /**
     * Converts this vector to matrix with only one column.
     *
     * @return the column matrix
     */
    Matrix toColumnMatrix();

    /**
     * Converts this vector to matrix with only one column.
     *
     * @param factory the factory of result matrix
     *
     * @return the column matrix
     */
    @Deprecated
    Matrix toColumnMatrix(Factory factory);

    /**
     * Converts this vector to a diagonal matrix.
     *
     * @return a diagonal matrix
     */
    Matrix toDiagonalMatrix();

    /**
     * Returns true when vector is equal to given {@code that} vector with given {@code precision}.
     *
     * @param that vector
     * @param precision given precision
     *
     * @return equals of this matrix to that
     */
    public boolean equals(Vector that, double precision);

    /**
     * Converts this vector into the string representation.
     *
     * @param formatter the number formatter
     *
     * @return the vector converted to a string
     */
    String mkString(NumberFormat formatter);

    /**
     * Converts this vector into the string representation.
     *
     * @param formatter the number formatter
     * @param delimiter the element's delimiter
     *
     * @return the vector converted to a string
     */
    String mkString(NumberFormat formatter, String delimiter);

    /**
     * Returns a vector iterator.
     *
     * @return a vector iterator.
     */
    @Override
    VectorIterator iterator();

    /**
     * Pipes this vector to a given {@code operation}.
     *
     * @param operation the vector operation
     *                  (an operation that take vector and returns {@code T})
     * @param <T> the result type
     *
     * @return the result of an operation applied to this vector
     */
    <T> T apply(VectorOperation<T> operation);

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
    <T> T apply(VectorVectorOperation<T> operation, Vector that);

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
    <T> T apply(VectorMatrixOperation<T> operation, Matrix that);

    /**
     * @return the factory of this vector
     */
    @Deprecated
    Factory factory();

    /**
     * Converts this vector using the given {@code factory}.
     *
     * @param factory the factory that creates an output vector
     * @param <T> type of the result vector
     *
     * @return a converted vector
     */
    <T extends Vector> T to(VectorFactory<T> factory);

    /**
     * Converts this vector into a {@link org.la4j.vector.dense.DenseVector}.
     *
     * @return a dense vector
     */
    DenseVector toDenseVector();

    /**
     * Converts this vector into a {@link org.la4j.vector.sparse.SparseVector}.
     *
     * @return a sparse vector
     */
    SparseVector toSparseVector();
}
