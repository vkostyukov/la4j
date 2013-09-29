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
 */

package org.la4j;

import org.la4j.factory.Basic1DFactory;
import org.la4j.factory.Basic2DFactory;
import org.la4j.factory.CCSFactory;
import org.la4j.factory.CRSFactory;
import org.la4j.factory.Factory;
import org.la4j.factory.SafeFactory;
import org.la4j.linear.GaussianSolver;
import org.la4j.linear.JacobiSolver;
import org.la4j.linear.LUSolver;
import org.la4j.linear.LinearSystemSolver;
import org.la4j.linear.QRSolver;
import org.la4j.linear.SeidelSolver;
import org.la4j.linear.SquareRootSolver;
import org.la4j.linear.SweepSolver;
import org.la4j.matrix.Matrix;

/**
 * Tiny class for common things.
 */
public final class LinearAlgebra {

    /**
     * The la4j version.
     */
    public static final String VERSION = "0.4.5";

    /**
     * The la4j name.
     */
    public static final String NAME = "la4j";

    /**
     * The la4j date.
     */
    public static final String DATE = "Sep 2013";

    /**
     * The la4j full name.
     */
    public static final String FULL_NAME = NAME + "-" + VERSION + " (" + DATE + ")";

    /**
     * The machine epsilon, that is calculated at runtime.
     */
    public static final double EPS;

    /**
     * Exponent of machine epsilon
     */
    public static final int ROUND_FACTOR;

    // Determine the machine epsilon
    // Tolerance is 10e1
    static {
        int roundFactor = 0;
        double eps = 1.0;
        while (1 + eps > 1) {
            eps = eps / 2;
            roundFactor++;
        }
        EPS = eps * 10e1;
        ROUND_FACTOR = roundFactor - 1;
    }

    public static enum SolverFactory {
        GAUSSIAN {
            @Override
            public LinearSystemSolver create(Matrix matrix) {
                return new GaussianSolver(matrix);
            }
        },
        JACOBI {
            @Override
            public LinearSystemSolver create(Matrix matrix) {
                return new JacobiSolver(matrix);
            }
        },
        SEIDEL {
            @Override
            public LinearSystemSolver create(Matrix matrix) {
                return new SeidelSolver(matrix);
            }
        },
        LU {
            @Override
            public LinearSystemSolver create(Matrix matrix) {
                return new LUSolver(matrix);
            }
        },
        QR {
            @Override
            public LinearSystemSolver create(Matrix matrix) {
                return new QRSolver(matrix);
            }
        },
        SQUARE_ROOT {
            @Override
            public LinearSystemSolver create(Matrix matrix) {
                return new SquareRootSolver(matrix);
            }
        },
        SWEEP {
            @Override
            public LinearSystemSolver create(Matrix matrix) {
                return new SweepSolver(matrix);
            }
        },
        SMART {
            @Override
            public LinearSystemSolver create(Matrix matrix) {
                // TODO: We can do it smarter in future
                if (matrix.rows() == matrix.columns()) {
                    return new LUSolver(matrix);
                } else if (matrix.rows() > matrix.columns()) {
                    return new QRSolver(matrix);
                }

                throw new IllegalArgumentException("This coefficient matrix can not be used with any solver.");
            }
        };

        public abstract LinearSystemSolver create(Matrix matrix);
    }

    /**
     * The {@link org.la4j.factory.Basic1DFactory} singleton instance.
     */
    public static final Factory BASIC1D_FACTORY = new Basic1DFactory();

    /**
     * The {@link org.la4j.factory.Basic2DFactory} singleton instance.
     */
    public static final Factory BASIC2D_FACTORY = new Basic2DFactory();

    /**
     * The {@link org.la4j.factory.CRSFactory} singleton instance.
     */
    public static final Factory CRS_FACTORY = new CRSFactory();

    /**
     * The {@link org.la4j.factory.CCSFactory} singleton instance.
     */
    public static final Factory CCS_FACTORY = new CCSFactory();

    /**
     * Safe version of {@link LinearAlgebra#BASIC1D_FACTORY}.
     *
     * <p>
     * The safe factory creates matrices that is wrapped with safe accessors and modifiers.
     * </p>
     */
    public static final Factory SAFE_BASIC1D_FACTORY = new SafeFactory(BASIC1D_FACTORY);

    /**
     * Safe version of {@link LinearAlgebra#BASIC2D_FACTORY}.
     *
     * <p>
     * The safe factory creates matrices that is wrapped with safe accessors and modifiers.
     * </p>
     */
    public static final Factory SAFE_BASIC2D_FACTORY = new SafeFactory(BASIC2D_FACTORY);

    /**
     * Safe version of {@link LinearAlgebra#CRS_FACTORY}.
     *
     * <p>
     * The safe factory creates matrices that is wrapped with safe accessors and modifiers.
     * </p>
     */
    public static final Factory SAFE_CRS_FACTORY = new SafeFactory(CRS_FACTORY);

    /**
     * Safe version of {@link LinearAlgebra#CCS_FACTORY}.
     *
     * <p>
     * The safe factory creates matrices that is wrapped with safe accessors and modifiers.
     * </p>
     */
    public static final Factory SAFE_CCS_FACTORY = new SafeFactory(CCS_FACTORY);

    /**
     * Reference to the {@link LinearAlgebra#BASIC1D_FACTORY}.
     */
    public static final Factory UNSAFE_BASIC1D_FACTORY = BASIC1D_FACTORY;

    /**
     * Reference to the {@link LinearAlgebra#BASIC2D_FACTORY}.
     */
    public static final Factory UNSAFE_BASIC2D_FACTORY = BASIC2D_FACTORY;

    /**
     * Reference to the {@link LinearAlgebra#CRS_FACTORY}.
     */
    public static final Factory UNSAFE_CRS_FACTORY = CRS_FACTORY;

    /**
     * Reference to the {@link LinearAlgebra#CCS_FACTORY}.
     */
    public static final Factory UNSAFE_CCS_FACTORY = CCS_FACTORY;

    /**
     * The default dense factory singleton instance. References the {@link LinearAlgebra#BASIC2D_FACTORY}.
     */
    public static final Factory DENSE_FACTORY = BASIC2D_FACTORY;

    /**
     * The default sparse factory singleton instance. References the {@link LinearAlgebra#CRS_FACTORY}.
     */
    public static final Factory SPARSE_FACTORY = CRS_FACTORY;

    /**
     * The default safe dense factory singleton instance. References the {@link LinearAlgebra#SAFE_BASIC2D_FACTORY}.
     */
    public static final Factory SAFE_DENSE_FACTORY = SAFE_BASIC2D_FACTORY;

    /**
     * The default unsafe dense factory singleton instance. References the {@link LinearAlgebra#BASIC2D_FACTORY}.
     */
    public static final Factory UNSAFE_DENSE_FACTORY = DENSE_FACTORY;

    /**
     * The default safe sparse factory singleton instance. References the {@link LinearAlgebra#SAFE_CRS_FACTORY}.
     */
    public static final Factory SAFE_SPARSE_FACTORY = SAFE_CRS_FACTORY;

    /**
     * The default unsafe sparse factory singleton instance. References the {@link LinearAlgebra#CRS_FACTORY}.
     */
    public static final Factory UNSAFE_SPARSE_FACTORY = SPARSE_FACTORY;

    /**
     * The default matrix factory singleton instance. References the {@link LinearAlgebra#BASIC2D_FACTORY}.
     */
    public static final Factory DEFAULT_FACTORY = BASIC2D_FACTORY;

    /**
     * The array with all factories available.
     */
    public static final Factory FACTORIES[] = {
            BASIC1D_FACTORY, BASIC2D_FACTORY, CRS_FACTORY, CCS_FACTORY
    };

    /**
     * The array with unsafe factories available.
     */
    public static final Factory UNSAFE_FACTORIES[] = FACTORIES;

    /**
     * The array with safe factories available.
     */
    public static final Factory SAFE_FACTORIES[] = {
            SAFE_BASIC1D_FACTORY, SAFE_BASIC2D_FACTORY,
            SAFE_CRS_FACTORY, SAFE_CCS_FACTORY
    };
}
