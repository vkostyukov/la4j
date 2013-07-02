package org.la4j.decomposition;

import junit.framework.Test;
import junit.framework.TestSuite;

public class CroutDecompositorTest extends AbstractDecompositorTest {

    @Override
	public MatrixDecompositor decompositor() {
	    return new CroutDecompositor();
    }

    @Override
    public double[][] input() {
        return new double[][] {
                { 1.0, 0.0, 2.0 }, 
                { 0.0, 10.0, 0.0 }, 
                { 2.0, 0.0, 9.0 } 
        };
    }

    @Override
    public double[][][] output() {
        return new double[][][] {
                { 
                    { 1.0, 0.0, 0.0 },
                    { 0.0, 10.0, 0.0},
                    { 2.0, 0.0, 5.0 } 
                },
                { 

                    { 1.0, 0.0, 2.0 }, 
                    { 0.0, 1.0, 0.0 }, 
                    { 0.0, 0.0, 1.0 } 
                } 
        };
    }

    public static Test suite() {
        return new TestSuite(CroutDecompositorTest.class);
    }
}
