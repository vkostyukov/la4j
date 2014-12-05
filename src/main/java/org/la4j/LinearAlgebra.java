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
 */

package org.la4j;

import org.la4j.decomposition.CholeskyDecomposer;
import org.la4j.decomposition.EigenDecomposer;
import org.la4j.decomposition.LUDecomposer;
import org.la4j.decomposition.MatrixDecomposer;
import org.la4j.decomposition.QRDecomposer;
import org.la4j.decomposition.RawLUDecomposer;
import org.la4j.decomposition.RawQRDecomposer;
import org.la4j.decomposition.SingularValueDecomposer;
import org.la4j.factory.Basic1DFactory;
import org.la4j.factory.Basic2DFactory;
import org.la4j.factory.CCSFactory;
import org.la4j.factory.CRSFactory;
import org.la4j.factory.Factory;
import org.la4j.inversion.GaussJordanInverter;
import org.la4j.inversion.MatrixInverter;
import org.la4j.linear.ForwardBackSubstitutionSolver;
import org.la4j.linear.GaussianSolver;
import org.la4j.linear.JacobiSolver;
import org.la4j.linear.LeastSquaresSolver;
import org.la4j.linear.LinearSystemSolver;
import org.la4j.linear.SeidelSolver;
import org.la4j.linear.SquareRootSolver;
import org.la4j.linear.SweepSolver;
import org.la4j.matrix.Matrix;

/**
 * Tiny class for common things.
 */
public final class LinearAlgebra {

    /**
     * The library version.
     */
    public static final String VERSION = "0.5.0";

    /**
     * The library name.
     */
    public static final String NAME = "la4j";

    /**
     * The library release date.
     */
    public static final String DATE = "March 2014";

    /**
     * The library full name.
     */
    public static final String FULL_NAME = NAME + "-" + VERSION + " (" + DATE + ")";

    /**
     * The machine epsilon, which is calculated at runtime.
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
        FORWARD_BACK_SUBSTITUTION {
            @Override
            public LinearSystemSolver create(Matrix matrix) {
                return new ForwardBackSubstitutionSolver(matrix);
            }
        },
        LEAST_SQUARES {
            @Override
            public LinearSystemSolver create(Matrix matrix) {
                return new LeastSquaresSolver(matrix);
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
                    return new ForwardBackSubstitutionSolver(matrix);
                } else if (matrix.rows() > matrix.columns()) {
                    return new LeastSquaresSolver(matrix);
                }

                throw new IllegalArgumentException("Underdetermined system of linear equations can not be solved.");
            }
        };

        public abstract LinearSystemSolver create(Matrix matrix);
    }

    /**
     * References to the Gaussian solver factory.
     */
    public static final SolverFactory GAUSSIAN = SolverFactory.GAUSSIAN;

    /**
     * References to the Jacobi solver factory.
     */
    public static final SolverFactory JACOBI = SolverFactory.JACOBI;

    /**
     * References to the Seidel solver factory.
     */
    public static final SolverFactory SEIDEL = SolverFactory.SEIDEL;

    /**
     * References to the Least Squares solver factory.
     */
    public static final SolverFactory LEAST_SQUARES = SolverFactory.LEAST_SQUARES;

    /**
     * References to the Forward-Back Substitution solver factory.
     */
    public static final SolverFactory FORWARD_BACK_SUBSTITUTION = SolverFactory.FORWARD_BACK_SUBSTITUTION;

    /**
     * References to the Square Root solver factory.
     */
    public static final SolverFactory SQUARE_ROOT = SolverFactory.SQUARE_ROOT;

    /**
     * References to the Smart solver factory.
     */
    public static final SolverFactory SOLVER = SolverFactory.SMART;

    /**
     * References to the Sweep solver factory.
     */
    public static final SolverFactory SWEEP = SolverFactory.SWEEP;

    public static enum InverterFactory {
        GAUSS_JORDAN {
            @Override
            public MatrixInverter create(Matrix matrix) {
                return new GaussJordanInverter(matrix);
            }
        },
        SMART {
            @Override
            public MatrixInverter create(Matrix matrix) {
                return new GaussJordanInverter(matrix);
            }
        };

        public abstract MatrixInverter create(Matrix matrix);
    }

    /**
     * Reference to the Gaussian inverter factory.
     */
    public static final InverterFactory GAUSS_JORDAN = InverterFactory.GAUSS_JORDAN;

    /**
     * Reference to the Smart inverter factory.
     */
    public static final InverterFactory INVERTER = InverterFactory.SMART;

    public static enum DecomposerFactory {
          CHOLESKY {
              @Override
              public MatrixDecomposer create(Matrix matrix) {
                  return new CholeskyDecomposer(matrix);
              }
          },
          EIGEN {
              @Override
              public MatrixDecomposer create(Matrix matrix) {
                  return new EigenDecomposer(matrix);
              }
          },
          RAW_LU {
              @Override
              public MatrixDecomposer create(Matrix matrix) {
                  return new RawLUDecomposer(matrix);
              }
          },
          LU {
              @Override
              public MatrixDecomposer create(Matrix matrix) {
                  return new LUDecomposer(matrix);
              }
          },
          RAW_QR {
              @Override
              public MatrixDecomposer create(Matrix matrix) {
                  return new RawQRDecomposer(matrix);
              }
          },
          QR {
              @Override
              public MatrixDecomposer create(Matrix matrix) {
                  return new QRDecomposer(matrix);
              }
          },
          SVD {
              @Override
              public MatrixDecomposer create(Matrix matrix) {
                  return new SingularValueDecomposer(matrix);
              }
          };

        public abstract MatrixDecomposer create(Matrix matrix);
    }

    /**
     * Reference to Cholesky decomposer factory.
     */
    public static final DecomposerFactory CHOLESKY = DecomposerFactory.CHOLESKY;

    /**
     * Reference to Eigen decomposer factory.
     */
    public static final DecomposerFactory EIGEN = DecomposerFactory.EIGEN;

    /**
     * Reference to Raw LU decomposer factory.
     */
    public static final DecomposerFactory RAW_LU = DecomposerFactory.RAW_LU;

    /**
     * Reference to LU decomposer factory.
     */
    public static final DecomposerFactory LU = DecomposerFactory.LU;

    /**
     * Reference to Raw QR decomposer factory.
     */
    public static final DecomposerFactory RAW_QR = DecomposerFactory.RAW_QR;

    /**
     * Reference to QR decomposer factory.
     */
     public static final DecomposerFactory QR = DecomposerFactory.QR;

    /**
     * Reference to SVD decomposer factory.
     */
    public static final DecomposerFactory SVD = DecomposerFactory.SVD;

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
     * The default dense factory singleton instance. References the {@link LinearAlgebra#BASIC2D_FACTORY}.
     */
    public static final Factory DENSE_FACTORY = BASIC2D_FACTORY;

    /**
     * The default sparse factory singleton instance. References the {@link LinearAlgebra#CRS_FACTORY}.
     */
    public static final Factory SPARSE_FACTORY = CRS_FACTORY;

    /**
     * The default matrix factory singleton instance.
     * References the {@link LinearAlgebra#BASIC2D_FACTORY}.
     */
    public static final Factory DEFAULT_FACTORY = BASIC2D_FACTORY;

    /**
     * The array with all factories available. This is useful for testing.
     */
    public static final Factory FACTORIES[] = {
            BASIC1D_FACTORY, BASIC2D_FACTORY, CRS_FACTORY, CCS_FACTORY
    };
}
