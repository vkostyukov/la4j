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
 * Contributor(s): -
 * 
 */

package org.la4j.matrix;

import java.util.Arrays;

import org.la4j.factory.Basic1DFactory;
import org.la4j.factory.Basic2DFactory;
import org.la4j.factory.CCSFactory;
import org.la4j.factory.CRSFactory;
import org.la4j.factory.Factory;
import org.la4j.matrix.functor.AdvancedMatrixPredicate;
import org.la4j.matrix.functor.MatrixPredicate;

public final class Matrices {

    private static class DiagonalMatrixPredicate implements MatrixPredicate {
        @Override
        public boolean test(int i, int j, double value) {
            return (i != j) ? Math.abs(value) < Matrix.EPS : true;
        }
    }

    private static class IdentityMatrixPredicate implements MatrixPredicate {
        @Override
        public boolean test(int i, int j, double value) {
            return (i == j) ? Math.abs(1.0 - value) < Matrix.EPS 
                            : Math.abs(value) < Matrix.EPS; 
        }
    }

    private static class ZeroMatrixPredicate implements MatrixPredicate {
        @Override
        public boolean test(int i, int j, double value) {
            return Math.abs(value) < Matrix.EPS;
        }
    }

    private static class TridiagonalMatrixPredicate implements MatrixPredicate {
        @Override
        public boolean test(int i, int j, double value) {
            return Math.abs(i - j) > 1 ?  Math.abs(value) < Matrix.EPS : true; 
        }
    }

    private static class PositiveMatrixPredicate implements MatrixPredicate {
        @Override
        public boolean test(int i, int j, double value) {
            return value > 0;
        }
    }

    private static class NegativeMatrixPredicate implements MatrixPredicate {
        @Override
        public boolean test(int i, int j, double value) {
            return value < 0;
        }
    }

    private static class LowerBidiagonalMatrixPredicate 
            implements MatrixPredicate {
        @Override
        public boolean test(int i, int j, double value) {
            return (i == j) || (i == j + 1) 
                    ? Math.abs(value) < Matrix.EPS
                    : true;
        }
    }

    private static class UpperBidiagonalMatrixPredicate 
            implements MatrixPredicate {
        @Override
        public boolean test(int i, int j, double value) {
            return (i == j) || (i == j - 1) 
                    ? Math.abs(value) < Matrix.EPS
                            : true;
        }
    }

    private static class LowerTriangularMatrixPredicate 
            implements MatrixPredicate {
        @Override
        public boolean test(int i, int j, double value) {
            return (i > j) ? Math.abs(value) < Matrix.EPS : true;
        }
    }

    private static class UpperTriangularMatrixPredicate 
            implements MatrixPredicate {
        @Override
        public boolean test(int i, int j, double value) {
            return (i < j) ? Math.abs(value) < Matrix.EPS : true;
        }
    }

    //
    // TODO:
    //
    // Please, send me a pull-request, if you know how to write it better
    // 
    private static class SymmetricMatrixPredicate
            implements AdvancedMatrixPredicate {

        private double values[];
        private int size;

        @Override
        public boolean test(int rows, int columns) {
            if (rows != columns) {
                return false;
            } else {
                size = rows;
                values = new double[(size * size - size) / 2 ];
                Arrays.fill(values, Double.NaN);

                return true;
            }
        }

        @Override
        public boolean test(int i, int j, double value) {
            int offset = -1;

            if (i < j) {
                offset = j - (i + 1) + (((size  - 1) + (size - i)) / 2) * i;
            } else if (i > j) {
                offset = i - (j + 1) + (((size - 1) + (size - j)) / 2) * j;
            } else return true;

            if (Double.isNaN(values[offset])) {
                values[offset] = value;

                return true;
            } else {
                return Math.abs(value - values[offset]) < Matrix.EPS;
            }
        }
    }

    public final static MatrixPredicate DIAGONAL_MATRIX = 
            new DiagonalMatrixPredicate();

    public final static MatrixPredicate IDENTITY_MATRIX = 
            new IdentityMatrixPredicate();

    public final static MatrixPredicate ZERO_MATRIX = 
            new ZeroMatrixPredicate();

    public final static MatrixPredicate TRIDIAGONAL_MATRIX = 
            new TridiagonalMatrixPredicate();

    public final static MatrixPredicate POSITIVE_MATRIX = 
            new PositiveMatrixPredicate();

    public final static MatrixPredicate NEGATIVE_MATRIX = 
            new NegativeMatrixPredicate();

    public final static MatrixPredicate LOWER_BIDIAGONAL_MATRIX = 
            new LowerBidiagonalMatrixPredicate();

    public final static MatrixPredicate UPPER_BIDIAGONAL_MATRIX = 
            new UpperBidiagonalMatrixPredicate();

    public final static MatrixPredicate LOWER_TRIANGULAR_MARTIX = 
            new LowerTriangularMatrixPredicate();

    public final static MatrixPredicate UPPER_TRIANGULAR_MATRIX = 
            new UpperTriangularMatrixPredicate();

    public final static MatrixPredicate SYMMETRIC_MATRIX = 
            new SymmetricMatrixPredicate();

    public final static Factory BASIC1D_FACTORY = new Basic1DFactory();

    public final static Factory BASIC2D_FACTORY = new Basic2DFactory();

    public final static Factory CRS_FACTORY = new CRSFactory();

    public final static Factory CCS_FACTORY = new CCSFactory();

    public final static Factory DEFAULT_DENSE_FACTORY = BASIC2D_FACTORY;

    public final static Factory DEFAULT_SPARSE_FACTORY = CRS_FACTORY;

    public final static Factory DEFAULT_FACTORY = BASIC2D_FACTORY;

    public static Matrix asSingletonMatrix(double value) {
        return DEFAULT_FACTORY.createMatrix(new double[][]{{ value }});
    }
}
