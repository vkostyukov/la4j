package org.la4j.vector;

/**
 * Interface for matrix type-to-type convertion
 *
 * @param <T>
 */
public interface VectorConverter<T extends Vector> {
    T convert(Vector vector);
}
