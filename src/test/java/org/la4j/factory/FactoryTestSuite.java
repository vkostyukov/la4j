package org.la4j.factory;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.la4j.decomposition.DecompositorTestSuite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        Basic1DFactoryTest.class,
        Basic2DFactoryTest.class,
        CCSFactoryTest.class,
        CRSFactoryTest.class
})
public class FactoryTestSuite {

}
