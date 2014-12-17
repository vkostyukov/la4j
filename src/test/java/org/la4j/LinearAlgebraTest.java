package org.la4j;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.la4j.decomposition.*;
import org.la4j.factory.FactoryTestSuite;
import org.la4j.inversion.InverterTestSuite;
import org.la4j.io.StreamTestSuite;
import org.la4j.linear.LinearSystemsTestSuite;
import org.la4j.matrix.Matrix;
import org.la4j.matrix.MatrixTestSuite;
import org.la4j.matrix.dense.Basic2DMatrix;
import org.la4j.matrix.sparse.CRSMatrix;
import org.la4j.vector.VectorTestSuite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        DecompositorTestSuite.class,
        FactoryTestSuite.class,
        InverterTestSuite.class,
        StreamTestSuite.class,
        LinearSystemsTestSuite.class,
        MatrixTestSuite.class,
        VectorTestSuite.class
})
public class LinearAlgebraTest {

}
