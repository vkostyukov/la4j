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

package org.la4j.matrix.source;

import java.util.Arrays;
import java.util.Random;

@Deprecated
public class RandomSymmetricMatrixSource implements MatrixSource {

    private final int size;
    private final Random random;

    private final double values[];

    public RandomSymmetricMatrixSource(int size) {
        this.size = size;
        this.random = new Random();

        values = new double[(size * size - size) / 2 ];
        Arrays.fill(values, Double.NaN);
    }

    @Override
    public double get(int i, int j) {
        if (i == j) {
            return random.nextDouble();
        } else {
            int offset = -1;

            // I got these formulas from arithmetic progression 
            // http://en.wikipedia.org/wiki/Arithmetic_progression

            if (i < j) {
                offset = j - (i + 1) + (int)((((size - 1) + (size - i)) / 2.0) 
                         * i);
            } else {
                offset = i - (j + 1) + (int)((((size - 1) + (size - j)) / 2.0) 
                         * j);
            }

            if (Double.isNaN(values[offset])) {
                values[offset] = random.nextDouble();
            }

            return values[offset];
        }
    }

    @Override
    public int columns() {
        return size;
    }

    @Override
    public int rows() {
        return size;
    }
}
