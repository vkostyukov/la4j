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

package org.la4j.matrix.sparse;

import org.la4j.matrix.Matrix;
import org.la4j.matrix.functor.MatrixAccumulator;
import org.la4j.matrix.functor.MatrixProcedure;
import org.la4j.vector.Vector;
import org.la4j.vector.functor.VectorFunction;
import org.la4j.vector.functor.VectorProcedure;

public interface SparseMatrix extends Matrix {

    /**
     * Returns the cardinality (the number of non-zero elements)
     * of this sparse matrix.
     * 
     * @return the cardinality of this matrix
     */
    int cardinality();

    /**
     * Returns the density (non-zero elements divided by total elements)
     * of this sparse matrix.
     * 
     * @return the density of this matrix
     */
    double density();

    /**
     * Whether or not the specified element is zero.
     *
     * @param i element's row index
     * @param j element's column index
     *
     * @return {@code true} if specified element is zero, {@code false} otherwise
     */
    boolean isZeroAt(int i, int j);

    /**
     * Whether or not the specified element is not zero.
     *
     * @param i element's row index
     * @param j element's column index
     *
     * @return {@code true} if specified element is not zero, {@code false} otherwise
     */
    boolean nonZeroAt(int i, int j);

    /**
     * Applies given {@code procedure} to each non-zero element of this matrix.
     *
     * @param procedure the matrix procedure
     */
    void eachNonZero(MatrixProcedure procedure);

    /**
     * Deprecated. Use {@link #eachNonZeroInRow(int, VectorFunction)} instead.
     * <p/>
     * Applies given {@code procedure} to each non-zero element of specified row of this matrix.
     *
     * @param i the row index
     * @param procedure the matrix procedure
     */
    @Deprecated
    void eachNonZeroInRow(int i, MatrixProcedure procedure);
    
    /**
     * Applies the given {@code procedure} to each non-zero element of the specified row of this matrix.
     * 
     * @param i the row index. 
     * @param procedure the {@link VectorProcedure}. 
     */
    void eachNonZeroInRow(int i, VectorProcedure procedure);

    /**
     * Deprecated. Use {@link #eachNonZeroInColumn(int, VectorFunction)} instead.
     * <p/>
     * Applies given {@code procedure} to each non-zero element of specified column of this matrix.
     *
     * @param j the column index
     * @param procedure the matrix procedure
     */
    @Deprecated
    void eachNonZeroInColumn(int j, MatrixProcedure procedure);
    
    /**
     * Applies the given {@code procedure} to each non-zero element of the specified column of this matrix.
     * 
     * @param j the column index.
     * @param procedure the {@link VectorProcedure}.
     */
    void eachNonZeroInColumn(int j, VectorProcedure procedure);

    /**
     * Folds non-zero elements of this matrix with given {@code accumulator}.
     *
     * @param accumulator the matrix accumulator
     *
     * @return the accumulated value
     */
    double foldNonZero(MatrixAccumulator accumulator);

    /**
     * Deprecated. Use {@link #foldNonZeroInRow(int, VectorFunction)} instead.
     * <p/>
     * Folds non-zero elements of specified row in this matrix with given {@code accumulator}.
     *
     * @param i the row index
     * @param accumulator the matrix accumulator
     *
     * @return the accumulated value
     */
    @Deprecated
    double foldNonZeroInRow(int i, MatrixAccumulator accumulator);
    
    /**
     * Folds non-zero elements of the specified row in this matrix with the given {@code function}.
     * 
     * @param i the row index.
     * @param function the {@link VectorFunction}.
     * 
     * @return the accumulated value.
     */
    double foldNonZeroInRow(int i, VectorFunction function);

    /**
     * Deprecated. Use {@link #foldNonZeroInColumn(int, VectorFunction)} instead.
     * <p/>
     * Folds non-zero elements of specified column in this matrix with given {@code accumulator}.
     *
     * @param j the column index
     * @param accumulator the matrix accumulator
     *
     * @return the accumulated value
     */
    @Deprecated
    double foldNonZeroInColumn(int j, MatrixAccumulator accumulator);
    
    /**
     * Folds non-zero elements of the specified column in this matrix with the given {@code function}.
     * 
     * @param j the column index.
     * @param function the {@link VectorFunction}.
     * 
     * @return the accumulated value.
     */
    double foldNonZeroInColumn(int j, VectorFunction function);

    /**
     * Folds non-zero elements (in a column-by-column manner) of this matrix with given {@code accumulator}.
     *
     * @param accumulator the matrix accumulator
     *
     * @return the accumulated vector
     */
    Vector foldNonZeroInColumns(MatrixAccumulator accumulator);

    /**
     * Folds non-zero elements (in a row-by-row manner) of this matrix with given {@code accumulator}.
     *
     * @param accumulator the matrix accumulator
     *
     * @return the accumulated vector
     */
    Vector foldNonZeroInRows(MatrixAccumulator accumulator);

    /**
     * Gets the specified element, or a {@code defaultValue} if there
     * is no actual element at ({@code i}, {@code j}) in this sparse matrix.
     *
     * @param i the element's row index
     * @param j the element's column index
     * @param defaultValue the default value
     *
     * @return the element of this vector or a default value
     */
    double getOrElse(int i, int j, double defaultValue);
}
