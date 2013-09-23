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
 */

package org.la4j;

/**
 * Tiny class for common things.
 */
public final class LinearAlgebra {

    /**
     * The la4j version.
     */
    public static final String VERSION = "0.4.5";

    /**
     * The la4j name.
     */
    public static final String NAME = "la4j";

    /**
     * The la4j date.
     */
    public static final String DATE = "Sep 2013";

    /**
     * The la4j full name.
     */
    public static final String FULL_NAME = NAME + "-" + VERSION + " (" + DATE + ")";

    /**
     * The machine epsilon, that is calculated at runtime.
     */
    public static final double EPS;

    /**
     * Exponent of machine epsilon
     */
    public static final int ROUND_FACTOR;

    // Determine the machine epsilon
    // Tolerance is 10e1
    static {
        int roundFactor = 0;
        double eps = 1.0;
        while (1 + eps > 1) {
            eps = eps / 2;
            roundFactor++;
        }
        EPS = eps * 10e1;
        ROUND_FACTOR = roundFactor - 1;
    }
}
