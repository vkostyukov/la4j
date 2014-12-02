package org.la4j.linear;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        ForwardBackSubstitutionSolverTest.class,
        GaussianSolverTest.class,
        JacobiSolverTest.class,
        LeastSquaresSolverTest.class,
        SeidelSolverTest.class,
        SquareRootSolverTest.class,
        SweepSolverTest.class
})
public class LinearSystemsTestSuite {

}
