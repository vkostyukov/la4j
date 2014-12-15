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
 * Contributor(s): -
 * 
 */

package org.la4j.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;
import java.util.StringTokenizer;

import org.la4j.LinearAlgebra;
import org.la4j.factory.Factory;
import org.la4j.matrix.Matrix;
import org.la4j.vector.Vector;

public class SymbolSeparatedStream extends AbstractStream 
    implements MatrixStream, VectorStream {

    private String separator;

    public SymbolSeparatedStream(InputStream in) {
        this(in, ", ");
    }

    public SymbolSeparatedStream(InputStream in, String separator) {
        super(in);
        this.separator = separator;
    }

    public SymbolSeparatedStream(OutputStream out) {
        this(out, ", ");
    }

    public SymbolSeparatedStream(OutputStream out, String separator) {
        super(out);
        this.separator = separator;
    }

    @Override
    public Vector readVector() throws IOException {
        return readVector(LinearAlgebra.DEFAULT_FACTORY);
    }

    @Override
    public Vector readVector(Factory factory) throws IOException {
        ensureReaderInitialized();

        Vector vector = factory.createVector(10);

        StringTokenizer tokenizer = new StringTokenizer(reader.readLine(), 
                separator);

        int length = 0;
        while (tokenizer.hasMoreTokens()) {
            if (length == vector.length()) {
                vector = vector.copyOfLength((vector.length() * 3) / 2 + 1);
            }

            vector.set(length++, Double.valueOf(tokenizer.nextToken()));
        }

        vector = vector.copyOfLength(length);

        closeReader();
        return vector;
    }

    @Override
    public void writeVector(Vector vector) throws IOException {
        ensureWriterInitialized();

        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < vector.length(); i++) {
            double value = vector.get(i);
            builder.append(String.format(Locale.US, "%.12f", value));
            if (i + 1 < vector.length()) {
                builder.append(separator);
            }
        }

        writer.write(builder.toString());
        writer.newLine();

        closeWriter();
    }

    @Override
    public Matrix readMatrix() throws IOException {
        return readMatrix(LinearAlgebra.DEFAULT_FACTORY);
    }

    @Override
    public Matrix readMatrix(Factory factory) throws IOException {
        ensureReaderInitialized();

        Matrix matrix = factory.createMatrix(10, 10);

        int rows = 0, columns = 0;
        for (String line = reader.readLine(); line != null; 
                line = reader.readLine(), rows++) {

            if (rows == matrix.rows()) {
                matrix = matrix.resizeRows((matrix.rows() * 3) / 2  + 1);
            }

            StringTokenizer tokenizer = new StringTokenizer(line, separator);

            int j = 0;
            while (tokenizer.hasMoreTokens()) {
                if (j == matrix.columns()) {
                    matrix = matrix.resizeColumns((matrix.columns() * 3) / 2 + 1);
                }
                double value = Double.valueOf(tokenizer.nextToken());
                matrix.set(rows, j++, value);
            }

            columns = j > columns ? j : columns;
        }

        matrix = matrix.resize(rows, columns);

        closeReader();
        return matrix;
    }

    @Override
    public void writeMatrix(Matrix matrix) throws IOException {
        ensureWriterInitialized();

        for (int i = 0; i < matrix.rows(); i++) {

            StringBuilder builder = new StringBuilder();

            for (int j = 0; j < matrix.columns(); j++) {
                double value = matrix.get(i, j);
                builder.append(String.format(Locale.US, "%.12f", value));
                if (j + 1 < matrix.columns()) {
                    builder.append(separator);
                }
            }

            writer.write(builder.toString());
            writer.newLine();
        }

        closeWriter();
    }
}

