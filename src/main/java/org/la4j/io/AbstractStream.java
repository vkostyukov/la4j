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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public abstract class AbstractStream implements MatrixStream, VectorStream {

    protected BufferedReader reader;
    protected BufferedWriter writer;

    public AbstractStream(InputStream in) {
        if (in == null) {
            throw new IllegalArgumentException();
        }

        this.reader = new BufferedReader(new InputStreamReader(in));
    }

    public AbstractStream(OutputStream out) {
        if (out == null) {
            throw new IllegalArgumentException();
        }

        this.writer = new BufferedWriter(new OutputStreamWriter(out));
    }

    protected void ensureReaderInitialized() {
        if (reader == null) {
            throw new IllegalStateException();
        }
    }

    protected void ensureWriterInitialized() {
        if (writer == null) {
            throw new IllegalStateException();
        }
    }

    protected void closeWriter() throws IOException {
        writer.flush();
        writer.close();
    }

    protected void closeReader() throws IOException {
        reader.close();
    }
}
