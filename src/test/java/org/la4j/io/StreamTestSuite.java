package org.la4j.io;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        MatrixMarketStreamTest.class,
        SymbolSeparatedStreamTest.class
})
public class StreamTestSuite {

}
