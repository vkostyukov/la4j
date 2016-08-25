/*
 * Copyright 2016 emmanuj.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.la4j.linear;

import org.la4j.Matrix;
import org.la4j.Vector;
import org.la4j.decomposition.LUDecompositor;

/**
 *
 * Provides a more efficient solver using LU Decomposition. Matrix dimension must be NxN.
 * For Sparse LU Solver see @SparseLUSolver
 * 
 * @author Emmanuel U John
 */
public class LUSolver extends AbstractSolver implements LinearSystemSolver{
    private final Matrix mat;
    public LUSolver(Matrix a) {
        super(a);
        this.mat=a;
    }

    @Override
    public Vector solve(Vector b) {
        ensureRHSIsCorrect(b);
        final int N = mat.columns();
        Matrix[] lup = new LUDecompositor(mat).decompose(); //returns {l,u,p}
        
        //First solve Ly = b for y
        Vector y = b.blankOfLength(N);
        
        for(int i=0;i<N;i++){
            double rhs = 0;
            
            for(int j=0;j<i;j++){
                rhs+=(lup[0].get(i, j) * y.get(j));
            }
            
            y.set(i, 1/lup[0].get(i, i) * (b.get(i) - rhs));
        }
        
        //Using backward substitution solve Ux = y for x
        Vector x = b.blankOfLength(N);
        
        for(int i=N-1;i>=0;i--){
            double rhs = 0;
            for(int j=i+1;j<N;j++){
                rhs+=(lup[1].get(i, j) * x.get(j));
            }
            
            x.set(i, 1/lup[1].get(i, i) * (y.get(i) - rhs));
        }
        
        return x;
    }

    @Override
    public boolean applicableTo(Matrix matrix) {
        return matrix.rows() == matrix.columns();
    }
    
}
