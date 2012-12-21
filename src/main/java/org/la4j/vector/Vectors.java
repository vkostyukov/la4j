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

package org.la4j.vector;

import org.la4j.vector.functor.VectorPredicate;

public final class Vectors {

    private static class ZeroVectorPredicate implements VectorPredicate {
        @Override
        public boolean test(int i, double value) {
            return Math.abs(value) < Vector.EPS;
        }
    }

    private static class PositiveVectorPredicate implements VectorPredicate {
        @Override
        public boolean test(int i, double value) {
            return value > 0;
        }
    }

    private static class NegativeVectorPredicate implements VectorPredicate {
        @Override
        public boolean test(int i, double value) {
            return value < 0;
        }
    }

    public static final VectorPredicate ZERO_VECTOR =
            new ZeroVectorPredicate();

    public static final VectorPredicate POSITIVE_VECTOR =
            new PositiveVectorPredicate();

    public static final VectorPredicate NEGATIVE_VECTOR = 
            new NegativeVectorPredicate();
}
