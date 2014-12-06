package org.la4j.decomposition;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        CholeskyDecompositorTest.class,
        EigenDecompositorTest.class,
        LUDecompositorTest.class,
        QRDecompositorTest.class,
        SingularValueDecompositorTest.class
        })
public class DecompositorTestSuite {

}
