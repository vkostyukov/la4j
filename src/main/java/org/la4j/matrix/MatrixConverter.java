package org.la4j.matrix;

import org.la4j.matrix.Matrix;

/**
 * Interface for matrix type-to-type convertion
 *
 * @param <T>
 */
public interface MatrixConverter<T extends Matrix> {
    T convert(Matrix matrix);
}
