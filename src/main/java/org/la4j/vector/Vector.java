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

import org.la4j.factory.Factory;
import org.la4j.matrix.Matrix;
import org.la4j.vector.functor.VectorAccumulator;
import org.la4j.vector.functor.VectorFunction;
import org.la4j.vector.functor.VectorPredicate;
import org.la4j.vector.functor.VectorProcedure;

/**
 * The real vector interface.
 */
public interface Vector extends Externalizable {

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
     * Assigns all elements of this vector to given {@code value}.
     * 
     * @param value the elements' new value
     */
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
    Vector add(double value, Factory factory);

    /**
     * Adds given {@code vector} (X) to this vector (Y).
     * 
     * @param vector the right hand vector for addition
     *
     * @return X + Y
     */
    Vector add(Vector vector);

    /**
     * Adds given {@code vector} (X) to this vector (Y).
     *
     * @param vector the right hand vector for addition
     * @param factory the factory of result vector
     *
     * @return X + Y
     */
    Vector add(Vector vector, Factory factory);

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
    Vector multiply(double value, Factory factory);

    /**
     * Calculates the Hadamard (element-wise) product of this vector and given {@code vector}.
     * 
     * @param vector the right hand vector for Hadamard product
     *
     * @return the Hadamard product of two vectors
     */
    Vector hadamardProduct(Vector vector);

    /**
     * Calculates the Hadamard (element-wise) product of this vector and given {@code vector}.
     *
     * @param vector the right hand vector for Hadamard product
     * @param factory the factory of result vector
     *
     * @return the Hadamard product of two vectors
     */
    Vector hadamardProduct(Vector vector, Factory factory);

    /**
     * Multiples this vector (X) by given {@code matrix} (A).
     * 
     * @param matrix the right hand matrix for multiplication
     *
     * @return X * A
     */
    Vector multiply(Matrix matrix);

    /**
     * Multiples this vector (X) by given {@code matrix} (A).
     *
     * @param matrix the right hand matrix for multiplication
     * @param factory the factory of result vector
     *
     * @return X * A
     */
    Vector multiply(Matrix matrix, Factory factory);

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
    Vector subtract(double value, Factory factory);

    /**
     * Subtracts given {@code vector} (Y) from this vector (X).
     * 
     * @param vector the right hand vector for subtraction
     *
     * @return X - Y
     */
    Vector subtract(Vector vector);

    /**
     * Subtracts given {@code vector} (Y) from this vector (X).
     *
     * @param vector the right hand vector for subtraction
     * @param factory the factory of result vector
     *
     * @return X - Y
     */
    Vector subtract(Vector vector, Factory factory);

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
     * Calculates the inner product of this vector and given {@code vector}.
     * 
     * @param vector the right hand vector for inner product
     *
     * @return the inner product of two vectors
     */
    double innerProduct(Vector vector);

    /**
     * Calculates the outer product of this vector and given {@code vector}.
     * 
     * @param vector the the right hand vector for outer product
     *
     * @return the outer product of two vectors
     */
    Matrix outerProduct(Vector vector);

    /**
     * Calculates the outer product of this vector and given {@code vector}.
     *
     * @param vector the the right hand vector for outer product
     * @param factory the factory of result vector
     *
     * @return the outer product of two vectors
     */
    Matrix outerProduct(Vector vector, Factory factory);

    /**
     * Calculates the Euclidean norm of this vector.
     *
     * This method is deprecated. Use the following approach instead:
     *
     * <code>
     *     Vector a = ...;
     *     double norm0   = a.fold(Vectors.mkEuclideanNormAccumulator());
     *     double norm1   = a.fold(Vectors.mkManhattanNormAccumulator());
     *     double normInf = a.fold(Vectors.mkInfinityNormAccumulator());
     * <code/>
     *
     * @return the Euclidean norm of this vector
     */
    @Deprecated
    double norm();

    /**
     * Normalizes this vector.
     *
     * This method is deprecated. Use the following approach instead:
     *
     * <code>
     *     Vector a = ...;
     *     double norm1 = a.fold(Vectors.mkManhattanNormAccumulator());
     *     Vector b = a.divide(norm1);
     * <code/>
     *
     * @return the normalized vector
     */
    @Deprecated
    Vector normalize();

    /**
     * Normalizes this vector.
     *
     * @param factory the factory of result vector
     *
     * @return the normalized vector
     */
    @Deprecated
    Vector normalize(Factory factory);

    /**
     * Swaps the specified elements of this vector.
     * 
     * @param i element's index
     * @param j element's index
     */
    void swap(int i, int j);

    /**
     * Creates a blank (an empty vector with same length) copy of this vector.
     * 
     * @return blank vector
     */
    Vector blank();

    /**
     * Creates a blank (an empty vector with same length) copy of this vector.
     *
     * @param factory the factory of result vector
     *
     * @return blank vector
     */
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
    Vector copy(Factory factory);

    /**
     * Copies this vector into the new vector with specified {@code length}.
     * 
     * @param length the length of new vector
     *
     * @return the copy of this vector with new length
     */
    Vector resize(int length);

    /**
     * Copies this vector into the new vector with specified {@code length}.
     *
     * @param length the length of new vector
     * @param factory the factory of result vector
     *
     * @return the copy of this vector with new length
     */
    Vector resize(int length, Factory factory);

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
     * <p>
     * Copies this vector in the new vector that contains the same elements but with
     * the elements shuffled around (which might also result in the same vector
     * (all outcomes are equally probable)).
     * </p>
     *
     * @param factory the factory of result vector
     *
     * @return the shuffled vector
     */
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
    Vector sliceRight(int from, Factory factory);


    /**
     * Returns a new vector with the selected elements.
     *
     * @param indices the array of indices
     *
     * @return the new vector with the selected elements
     */
    public Vector select(int[] indices);

    /**
     * Returns a new vector with the selected elements.
     *
     * @param indices the array of indices
     *
     * @return the new vector with the selected elements
     */
    public Vector select(int[] indices, Factory factory);

    /**
     * Returns the factory of this vector.
     * 
     * @return the factory of this vector
     */
    Factory factory();

    /**
     * Applies given {@code procedure} to each element of this vector.
     *
     * @param procedure the vector procedure
     */
    void each(VectorProcedure procedure);

    /**
     * Applies given {@code procedure} to each non-zero element of this vector.
     *
     * @param procedure the vector procedure
     */
    void eachNonZero(VectorProcedure procedure);

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
    Vector transform(VectorFunction function, Factory factory);

    /**
     * Builds a new vector by applying given {@code function} to the specified element
     * of this vector.
     *
     * @param i element's index
     * @param function the vector function
     *
     * @return the transformed vector
     */
    Vector transform(int i, VectorFunction function);

    /**
     * Builds a new vector by applying given {@code function} to the specified element
     * of this vector.
     *
     * @param i element's index
     * @param function the vector function
     * @param factory the factory of result vector
     *
     * @return the transformed vector
     */
    Vector transform(int i, VectorFunction function, Factory factory);

    /**
     * Updates all elements of this vector by applying given {@code function}.
     * 
     * @param function the the vector function
     */
    void update(VectorFunction function); 

    /**
     * Updates the specified element of this vector by applying given {@code function}.
     * 
     * @param i element's index
     * @param function the vector function
     */
    void update(int i, VectorFunction function);

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
    Matrix toColumnMatrix(Factory factory);
}
