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
     * Returns an element that stored at index {@code i} in this vector.
     * 
     * @param i index
     * @return vector element
     */
    double get(int i);

    /**
     * Assigns an element that stored at index {@code i} in this vector to 
     * given {@code value}.
     * 
     * @param i index
     * @param value 
     */
    void set(int i, double value);

    /**
     * Assigns all elements of this vector to given {@code value}.
     * 
     * @param value
     */
    void assign(double value);

    /**
     * Returns the length of this vector.
     * 
     * @return length of this vector
     */
    int length();

    /**
     * Adds given {@code value} to this vector. The new vector will be 
     * constructed with default {@link Factory factory}. 
     * 
     * @param value
     * @return new vector
     */
    Vector add(double value);

    /**
     * Adds given {@code value} to this vector. The new vector will be 
     * constructed with given {@code factory}.
     * 
     * @param value
     * @param factory
     * @return new vector
     */
    Vector add(double value, Factory factory);

    /**
     * Adds given {@code vector} to this vector. The new vector will be
     * constructed with default {@link Factory factory}.
     * 
     * @param vector
     * @return new vector
     */
    Vector add(Vector vector);

    /**
     * Adds given {@code vector} to this vector. The new vector will be 
     * constructed with given {@code factory}. 
     * 
     * @param vector
     * @param factory
     * @return new vector
     */
    Vector add(Vector vector, Factory factory);

    /**
     * Multiplies this vector by given {@code value}. The new vector will be
     * constructed with default {@link Factory factory}.
     * 
     * @param value
     * @return new vector
     */
    Vector multiply(double value);

    /**
     * Multiplies this vector by given {@code value}. The new vector will be
     * constructed with given {@code factory}.
     * 
     * @param value
     * @param factory
     * @return new vector
     */
    Vector multiply(double value, Factory factory);

    /**
     * Calculates the Hadamard (element-wise/pointwise) product of this vector
     * and given {@code vector}. The new vector will be constructed with
     * default {@link Factory factory}.
     * 
     * @param vector
     * @return new vector
     */
    Vector hadamardProduct(Vector vector);

    /**
     * Calculates the Hadamard (element-wise/pointwise) product of this vector
     * and given {@code vector}. The new vector will be constructed with given
     * {@link Factory factory}.
     * 
     * @param vector
     * @param factory 
     * @return new vector
     */
    Vector hadamardProduct(Vector vector, Factory factory);

    /**
     * Multiples this vector by given {@code matrix}. The new vector will be 
     * constructed with default {@link Factory factory}.
     * 
     * @param matrix
     * @return new vector
     */
    Vector multiply(Matrix matrix);

    /**
     * Multiples this vector by given {@code matrix}. The new vector will be 
     * constructed with given {@code factory}.

     * @param matrix
     * @param factory
     * @return new vector
     */
    Vector multiply(Matrix matrix, Factory factory);

    /**
     * Subtracts given {@code value} from this vector. The new vector will be
     * constructed with default {@link Factory factory}.
     * 
     * @param value
     * @return new vector
     */
    Vector subtract(double value);

    /**
     * Subtracts given {@code value} from this vector. The new vector will be
     * constructed with given {@code factory}.

     * @param value
     * @param factory
     * @return new vector
     */
    Vector subtract(double value, Factory factory);

    /**
     * Subtracts given {@code vector} from this vector. The new vector will be
     * constructed with default {@link Factory factory}.
     * 
     * @param vector
     * @return new vector
     */
    Vector subtract(Vector vector);

    /**
     * Subtracts given {@code vector} from this vector. The new vector will be
     * constructed with given {@code factory}.
     *
     * @param vector
     * @param factory
     * @return new vector
     */
    Vector subtract(Vector vector, Factory factory);

    /**
     * Divides this vector by {@code value}. The new vector will be
     * constructed with default {@link Factory factory}.
     * 
     * @param value
     * @return new vector
     */
    Vector divide(double value);

    /**
     * Divides this vector by {@code value}. The new vector will be
     * constructed with given {@code factory}.

     * @param value
     * @param factory
     * @return new vector
     */
    Vector divide(double value, Factory factory);

    /**
     * Productizes all elements of the vector
     * 
     * @return product of all vector elements
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
     * @param vector
     * @return product of two vectors
     */
    double innerProduct(Vector vector);

    /**
     * Calculates the outer product of this vector and given {@code vector}.
     * The new matrix will be constructed with default {@code factory}.
     * 
     * @param vector
     * @return outer product of two vectors
     */
    Matrix outerProduct(Vector vector);

    /**
     * Calculates the outer product of this vector and given {@code vector}.
     * The new matrix will be constructed with given {@code factory}.
     * 
     * @param vector
     * @param factory
     * @return outer product of two vectors
     */
    Matrix outerProduct(Vector vector, Factory factory);

    /**
     * Calculates the norm of this vector.
     * 
     * @return norm of this vector
     */
    double norm();

    /**
     * Normalizes this vector. The new vector will be constructed 
     * with default {@link Factory factory}.
     * 
     * @return normalized vector
     */
    Vector normalize();

    /**
     * Normalizes this vector. The new vector will be constructed 
     * with given {@code factory}.
     * 
     * @param factory
     * @return normalized vector
     */
    Vector normalize(Factory factory);

    /**
     * Swaps two elements of this vector. Elements that stored at {@code i} and 
     * {@code j} indices will be swapped.
     * 
     * @param i index
     * @param j index
     */
    void swap(int i, int j);

    /**
     * Creates a blank copy of this vector. The new vector will be constructed 
     * with default {@link Factory factory}. 
     * 
     * @return blank vector
     */
    Vector blank();

    /**
     * Creates a blank copy of this vector. The new vector will be constructed 
     * with given {@code factory}. 
     * 
     * @param factory
     * @return blank vector
     */
    Vector blank(Factory factory);

    /**
     * Copies this vector. The new vector will be constructed 
     * with default {@link Factory factory}.
     * 
     * @return copy of this vector
     */
    Vector copy();

    /**
     * Copies this vector. The new vector will be constructed 
     * with given {@code factory}.
     *
     * @param factory
     * @return copy of this vector
     */
    Vector copy(Factory factory);

    /**
     * Resizes this vector to new {@code length}. The new vector 
     * will be constructed with default {@link Factory factory}.
     * 
     * @param length
     * @return new vector
     */
    Vector resize(int length);

    /**
     * Resizes this vector to new {@code length}. The new vector 
     * will be constructed with given {@code factory}.
     * 
     * @param length
     * @param factory
     * @return new vector
     */
    Vector resize(int length, Factory factory);

    /**
     * Vector that contains the same elements but with the elements shuffled
     * around (which might also result in the same vector (all outcomes are
     * equally probable)).
     * 
     * @return The shuffled vector.
     */
    Vector shuffle();

    /**
     * Vector that contains the same elements but with the elements shuffled
     * around (which might also result in the same vector (all outcomes are
     * equally probable)).
     * 
     * @param factory
     *            The factory to use for this
     * @return The shuffled vector.
     */
    Vector shuffle(Factory factory);

    /**
     * Slices this vector to given interval [{@code from}; {@code until}). 
     * The new vector will be constructed with default {@link Factory factory}.
     * 
     * @param from
     * @param until
     * @return new vector
     */
    Vector slice(int from, int until);

    /**
     * Slices this vector to given interval [{@code from}; {@code until}). 
     * The new vector will be constructed with given {@code factory}.
     * 
     * @param from
     * @param until
     * @param factory
     * @return new vector
     */
    Vector slice(int from, int until, Factory factory);

    /**
     * Slices this vector to given left interval [0; {@code until}). 
     * The new vector will be constructed with default {@link Factory factory}.
     *  
     * @param until
     * @return new vector
     */
    Vector sliceLeft(int until);

    /**
     * Slices this vector to given left interval [0; {@code until}). 
     * The new vector will be constructed with given {@code factory}.
     * 
     * @param until
     * @param factory
     * @return new vector
     */
    Vector sliceLeft(int until, Factory factory);

    /**
     * Slices this vector to given right interval [{@code from}; {@code length}). 
     * The new vector will be constructed with default {@link Factory factory}.
     * 
     * @param from
     * @return new vector
     */
    Vector sliceRight(int from); 

    /**
     * Slices this vector to given right interval [{@code from}; {@code length}). 
     * The new vector will be constructed with given {@code factory}.
     * 
     * @param from
     * @param factory
     * @return new vector
     */
    Vector sliceRight(int from, Factory factory); 

    /**
     * Returns a factory that associated with this vector.
     * 
     * @return factory
     */
    Factory factory();

    /**
     * Applies given {@code procedure} to each element of this vector.
     *
     * @param procedure
     */
    void each(VectorProcedure procedure);

    /**
     * Applies given {@code procedure} to each non-zero element of this vector.
     *
     * @param procedure
     */
    void eachNonZero(VectorProcedure procedure);

    /**
     * Finds maximum among vector components.
     *
     * @return max
     */
    double max();

    /**
     * Finds minimum among vector components.
     *
     * @return max
     */
    double min();

    /**
     * Builds a new vector by applying given {@code function} to each element 
     * of this vector. The new vector will be constructed with default 
     * {@link Factory factory}.
     * 
     * @param function
     * @return new vector
     */
    Vector transform(VectorFunction function);

    /**
     * Builds a new vector by applying given {@code function} to each element 
     * of this vector. The new vector will be constructed with given 
     * {@code factory}.
     * 
     * @param function
     * @param factory
     * @return new vector
     */
    Vector transform(VectorFunction function, Factory factory);

    /**
     * Builds a new vector by applying given {@code function} to element that 
     * stored at {@code i} index in this vector. The new vector will be 
     * constructed with default {@link Factory factory}.
     * 
     * @param i index
     * @param function
     * @return new vector
     */
    Vector transform(int i, VectorFunction function);

    /**
     * Builds a new vector by applying given {@code function} to element that 
     * stored at {@code i} index in this vector. The new vector will be 
     * constructed with given {@code factory}.
     * 
     * @param i index
     * @param function
     * @param factory
     * @return new vector
     */
    Vector transform(int i, VectorFunction function, Factory factory);

    /**
     * Updates all elements of this vector by evaluating a given {@code function}.
     * 
     * @param function
     */
    void update(VectorFunction function); 

    /**
     * Updates element that stored at {@code i} index of this vector by 
     * evaluating a given {@code function}. 
     * 
     * @param i index
     * @param function
     */
    void update(int i, VectorFunction function);

    /**
     * Combines all elements of this vector into the value by using given 
     * {@code accumulator}.
     * 
     * @param accumulator
     * @return
     */
    double fold(VectorAccumulator accumulator);

    /**
     * Checks whether this vector matches to given {@code predicate}.
     * 
     * @param predicate
     * @return whether matches or not
     */
    boolean is(VectorPredicate predicate);

    /**
     * Wraps this vector with safe interface.
     * 
     * @return safe vector
     */
    Vector safe();

    /**
     * Wraps this vector with unsafe interface.
     * 
     * @return unsafe vector
     */
    Vector unsafe();
}
