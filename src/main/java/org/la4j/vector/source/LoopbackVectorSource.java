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

package org.la4j.vector.source;

import org.la4j.vector.Vector;

@Deprecated
public class LoopbackVectorSource implements VectorSource {

    private final Vector vector;

    public LoopbackVectorSource(Vector vector) {
        this.vector = vector;
    }

    @Override
    public double get(int i) {
        return vector.get(i);
    }

    @Override
    public int length() {
        return vector.length();
    }
}
