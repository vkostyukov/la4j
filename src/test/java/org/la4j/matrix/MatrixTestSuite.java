package org.la4j.matrix;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.la4j.matrix.converter.MatrixConverterTestSuite;
import org.la4j.matrix.dense.DenseMatrixTestSuite;
import org.la4j.matrix.source.MatrixSourceTestSuite;
import org.la4j.matrix.sparse.SparseMatrixTestSuite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        DenseMatrixTestSuite.class,
        MatrixSourceTestSuite.class,
        SparseMatrixTestSuite.class,
        MatrixConverterTestSuite.class
})
public class MatrixTestSuite {

}
