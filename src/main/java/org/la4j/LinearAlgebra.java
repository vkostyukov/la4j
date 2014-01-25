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

import org.la4j.decomposition.CholeskyDecompositor;
import org.la4j.decomposition.EigenDecompositor;
import org.la4j.decomposition.LUDecompositor;
import org.la4j.decomposition.MatrixDecompositor;
import org.la4j.decomposition.QRDecompositor;
import org.la4j.decomposition.RawLUDecompositor;
import org.la4j.decomposition.RawQRDecompositor;
import org.la4j.decomposition.SingularValueDecompositor;
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
     * The la4j version.
     */
    public static final String VERSION = "0.4.9";

    /**
     * The la4j name.
     */
    public static final String NAME = "la4j";

    /**
     * The la4j date.
     */
    public static final String DATE = "Jan 2014";

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

    public static enum DecompositorFactory {
          CHOLESKY {
              @Override
              public MatrixDecompositor create(Matrix matrix) {
                  return new CholeskyDecompositor(matrix);
              }
          },
          EIGEN {
              @Override
              public MatrixDecompositor create(Matrix matrix) {
                  return new EigenDecompositor(matrix);
              }
          },
          RAW_LU {
              @Override
              public MatrixDecompositor create(Matrix matrix) {
                  return new RawLUDecompositor(matrix);
              }
          },
          LU {
              @Override
              public MatrixDecompositor create(Matrix matrix) {
                  return new LUDecompositor(matrix);
              }
          },
          RAW_QR {
              @Override
              public MatrixDecompositor create(Matrix matrix) {
                  return new RawQRDecompositor(matrix);
              }
          },
          QR {
              @Override
              public MatrixDecompositor create(Matrix matrix) {
                  return new QRDecompositor(matrix);
              }
          },
          SVD {
              @Override
              public MatrixDecompositor create(Matrix matrix) {
                  return new SingularValueDecompositor(matrix);
              }
          };

        public abstract MatrixDecompositor create(Matrix matrix);
    }

    /**
     * Reference to Cholesky decompositor factory.
     */
    public static final DecompositorFactory CHOLESKY = DecompositorFactory.CHOLESKY;

    /**
     * Reference to Eigen decompositor factory.
     */
    public static final DecompositorFactory EIGEN = DecompositorFactory.EIGEN;

    /**
     * Reference to Raw LU decompositor factory.
     */
    public static final DecompositorFactory RAW_LU = DecompositorFactory.RAW_LU;

    /**
     * Reference to LU decompositor factory.
     */
    public static final DecompositorFactory LU = DecompositorFactory.LU;

    /**
     * Reference to Raw QR decompositor factory.
     */
    public static final DecompositorFactory RAW_QR = DecompositorFactory.RAW_QR;

    /**
     * Reference to QR decompositor factory.
     */
     public static final DecompositorFactory QR = DecompositorFactory.QR;

    /**
     * Reference to SVD decompositor factory.
     */
    public static final DecompositorFactory SVD = DecompositorFactory.SVD;

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
     * The default matrix factory singleton instance. References the {@link LinearAlgebra#BASIC2D_FACTORY}.
     */
    public static final Factory DEFAULT_FACTORY = BASIC2D_FACTORY;

    /**
     * The array with all factories available.
     */
    public static final Factory FACTORIES[] = {
            BASIC1D_FACTORY, BASIC2D_FACTORY, CRS_FACTORY, CCS_FACTORY
    };
}
