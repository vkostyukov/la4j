package org.la4j.decomposition;

import org.la4j.factory.Factory;
import org.la4j.matrix.Matrices;
import org.la4j.matrix.Matrix;

/**
 * This class represents LU decomposition of matrices. More details
 * <p>
 * <a href="http://math.fullerton.edu/mathews/n2003/CholeskyMod.html"> here.</a>
 * </p>
 */
public class CroutDecompositor implements MatrixDecompositor {
	
    /**
     * Returns the result of Crout decomposition of given matrix
     * <p>
     * See <a href="http://math.fullerton.edu/mathews/n2003/CholeskyMod.html">
     * http://math.fullerton.edu/mathews/n2003/CholeskyMod.html</a> for more details.
     * </p>
     * 
     * @param matrix
     * @param factory
     * @return { L, U }
     */
    @Override
    public Matrix[] decompose(Matrix matrix, Factory factory) {

        if (matrix.rows() != matrix.columns()) {
            throw new IllegalArgumentException("Wrong matrix size: " 
                    +  "rows != columns");
        }

        Matrix l = factory.createMatrix(matrix.rows(), matrix.columns());
        Matrix u = factory.createIdentityMatrix(matrix.rows());
        for (int j = 0; j < l.columns(); j++){
            for (int i = j; i < l.rows(); i++){
                double s = 0.0;
                for (int k = 0; k < j; k++){
                    s = s + l.get(i, k) * u.get(k, j);
                }
                l.set(i,j,matrix.get(i, j) - s);
            }
            
            for (int i = j; i < l.rows(); i++){
                double s = 0.0;
                for (int k = 0; k < j; k++){
                    s = s + l.get(j, k) * u.get(k, i);
                }
                if (Math.abs(l.get(j, j)) < Matrices.EPS){
                    throw new IllegalArgumentException("Singular matrix!");
                }
                    u.set(j, i, (matrix.get(j, i) - s) / l.get(j, j));
                }        		        	
        }
  
        return new Matrix[] { l, u };
    }		

}
