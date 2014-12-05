/*
 * Copyright 2011-2013, by Vladimir Kostyukov and Contributors.
 * 
 * This file is part of la4j project (http://la4j.org)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * Contributor(s): Alessio Placitelli
 *                 Maxim Samoylov
 * 
 */

package org.la4j.io;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;
import java.util.StringTokenizer;

import org.la4j.LinearAlgebra;
import org.la4j.factory.Factory;
import org.la4j.matrix.Matrix;
import org.la4j.matrix.dense.DenseMatrix;
import org.la4j.matrix.functor.MatrixProcedure;
import org.la4j.matrix.sparse.SparseMatrix;
import org.la4j.vector.Vector;
import org.la4j.vector.dense.DenseVector;
import org.la4j.vector.functor.VectorProcedure;
import org.la4j.vector.sparse.SparseVector;

public class MatrixMarketStream extends AbstractStream 
    implements MatrixStream, VectorStream {

    private static class SparseVectorWriteProcedure implements VectorProcedure {

        private final BufferedWriter writer;

        public SparseVectorWriteProcedure(BufferedWriter writer) {
            this.writer = writer;
        }

        @Override
        public void apply(int i, double value) {
            try {
                // In Matrix Market specification indices are 1-based
                writer.write((i + 1) + " " + String.format(Locale.US, "%.12f", value));
                writer.newLine();
            } catch (IOException expected) {
                throw new IllegalArgumentException("Can't write with writer.");
            }
        }
    }

    private static class SparseMatrixWriteProcedure implements MatrixProcedure {

        private final BufferedWriter writer;

        public SparseMatrixWriteProcedure(BufferedWriter writer) {
            this.writer = writer;
        }

        @Override
        public void apply(int i, int j, double value) {
            try {
                writer.write((i + 1) + " " + (j + 1) + " "
                        + String.format(Locale.US, "%.12f", value));
                writer.newLine();
            } catch (IOException expected) {
                throw new IllegalArgumentException("Can't write with writer.");
            }
        }
    }

    private StringTokenizer tokenizer;

    public MatrixMarketStream(InputStream in) {
        super(in);
    }

    public MatrixMarketStream(OutputStream out) {
        super(out);
    }

    @Override
    public Vector readVector() throws IOException {
        // TODO: Not a good idea
        return readVector(null);
    }

    @Override
    public Vector readVector(Factory factory) throws IOException {
        ensureReaderInitialized();

        Vector vector = parseVector(factory);
        closeReader();

        return vector;
    }

    @Override
    public void writeVector(Vector vector) throws IOException {
        ensureWriterInitialized();

        if (vector instanceof SparseVector) {
            writeSparseVector((SparseVector) vector);
        } else if (vector instanceof DenseVector) {
            writeDenseVector((DenseVector) vector);
        } else {
            throw new IllegalArgumentException("Unknown vector type.");
        }

        closeWriter();
    }

    @Override
    public Matrix readMatrix(Factory factory) throws IOException {
        ensureReaderInitialized();

        Matrix matrix = parseMatrix(factory);
        closeReader();

        return matrix;
    }

    @Override
    public Matrix readMatrix() throws IOException {
        // TODO: Not a good idea
        return readMatrix(null);
    }

    @Override
    public void writeMatrix(Matrix matrix) throws IOException {
        ensureWriterInitialized();

        if (matrix instanceof SparseMatrix) {
            writeSparseMatrix((SparseMatrix) matrix);
        } else if (matrix instanceof DenseMatrix) {
            writeDenseMatrix((DenseMatrix) matrix);
        } else {
            // Should never happen
            throw new IllegalArgumentException("Unknown matrix type.");
        }

        closeWriter();
    }

    private void writeSparseVector(SparseVector vector) throws IOException {
        writer.write("%%MatrixMarket vector coordinate real general");
        writer.newLine();

        writer.write(vector.length() + " " + vector.cardinality());
        writer.newLine();

        vector.eachNonZero(new SparseVectorWriteProcedure(writer));
    }

    private void writeDenseVector(DenseVector vector) throws IOException {
        writer.write("%%MatrixMarket vector array real general");
        writer.newLine();

        writer.write(Integer.toString(vector.length()));
        writer.newLine();

        for (int i = 0; i < vector.length(); i++) {
            double value = vector.get(i);
            writer.write(String.format(Locale.US, "%.12f", value));
            writer.newLine();
        }
    }

    private void writeSparseMatrix(SparseMatrix matrix) throws IOException {
        writer.write("%%MatrixMarket matrix coordinate real general");
        writer.newLine();

        writer.write(matrix.rows() + " " + matrix.columns() + " " 
                + matrix.cardinality());
        writer.newLine();

        matrix.eachNonZero(new SparseMatrixWriteProcedure(writer));
    }

    private void writeDenseMatrix(DenseMatrix matrix) throws IOException {
        writer.write("%%MatrixMarket matrix array real general");
        writer.newLine();

        writer.write(matrix.rows() + " " + matrix.columns());
        writer.newLine();

        for (int i = 0; i < matrix.rows(); i++) {
            for (int j = 0; j < matrix.columns(); j++) {
                double value = matrix.get(i, j);
                writer.write(String.format(Locale.US, "%.12f", value));
                writer.newLine();
            }
        }
    }

    private Vector parseVector(Factory factory) throws IOException {
        ensureNext("%%MatrixMarket");
        ensureNext("vector");

        String token = nextToken();

        ensureNext("real");
        ensureNext("general");

        if (token.equals("array")) {
            return parseDenseVector(chooseNotNull(factory, LinearAlgebra.DENSE_FACTORY));
        } else if (token.equals("coordinate")) {
            return parseSparseVector(chooseNotNull(factory, LinearAlgebra.SPARSE_FACTORY));
        } else {
            throw new IOException("Unexpected token at stream: \"" + token + "\".");
        }
    }

    private Vector parseDenseVector(Factory factory) throws IOException {
        int length = Integer.valueOf(nextToken());

        Vector vector = factory.createVector(length);

        for (int i = 0; i < length; i++) {
            double value = Double.valueOf(nextToken());
            vector.set(i, value);
        }

        return vector;
    }

    private Vector parseSparseVector(Factory factory) throws IOException {
        int length = Integer.valueOf(nextToken());
        int cardinality = Integer.valueOf(nextToken());

        Vector vector = factory.createVector(length);

        for (int k = 0; k < cardinality; k++) {
            // In Matrix Market specification indices are 1-based
            int i = Integer.valueOf(nextToken()) - 1;
            double value = Double.valueOf(nextToken());

            vector.set(i, value);
        }

        return vector;
    }

    private Matrix parseMatrix(Factory factory) throws IOException {
        ensureNext("%%MatrixMarket");
        ensureNext("matrix");

        String token = nextToken();

        ensureNext("real");
        ensureNext("general");

        if (token.equals("array")) {
            return parseDenseMatrix(chooseNotNull(factory, LinearAlgebra.DENSE_FACTORY));
        } else if (token.equals("coordinate")) {
            return parseSparseMatrix(chooseNotNull(factory, LinearAlgebra.SPARSE_FACTORY));
        } else {
            throw new IOException("Unexpected token at stream: \"" + token + "\".");
        }
    }

    private Matrix parseDenseMatrix(Factory factory) throws IOException {
        int rows = Integer.valueOf(nextToken());
        int columns = Integer.valueOf(nextToken());

        Matrix matrix = factory.createMatrix(rows, columns);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                double value = Double.valueOf(nextToken());
                matrix.set(i, j, value);
            }
        }

        return matrix;
    }
    
    private Matrix parseSparseMatrix(Factory factory) throws IOException {
        int rows = Integer.valueOf(nextToken());
        int columns = Integer.valueOf(nextToken());
        int cardinality = Integer.valueOf(nextToken());

        Matrix matrix = factory.createMatrix(rows, columns);

        for (int k = 0; k < cardinality; k++) {
            // In Matrix Market specification indices are 1-based, but we need a 0-based index
            int i = Integer.valueOf(nextToken()) - 1;
            int j = Integer.valueOf(nextToken()) - 1;
            double value = Double.valueOf(nextToken());

            matrix.set(i, j, value);
        }

        return matrix;
    }

    private String nextToken() throws IOException {
        while (tokenizer == null || !tokenizer.hasMoreTokens()) {
            String line = reader.readLine();

            // We want to skip comments and empty lines
            if (!"".equals(line) && (line.startsWith("%%") || !line.startsWith("%"))) {
                tokenizer = new StringTokenizer(line);
            }
        }
        return tokenizer.nextToken();
    }

    private void ensureNext(String value) throws IOException {
        if (!value.equals(nextToken())) {
            throw new IOException("Unexpected token at stream: \"" + value + "\".");
        }
    }

    private Factory chooseNotNull(Factory first, Factory second) {
        return first == null ? second : first;
    }
}
