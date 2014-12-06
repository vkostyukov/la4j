package org.la4j.vector;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.la4j.vector.dense.DenseVectorTestSuite;
import org.la4j.vector.sparse.SparseVectorTestSuite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        DenseVectorTestSuite.class,
        SparseVectorTestSuite.class
})
public class VectorTestSuite {

}
