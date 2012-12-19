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

package org.la4j.decomposition;

import junit.framework.Test;
import junit.framework.TestSuite;

public class QRDecompositorTest extends AbstarctDecompositorTest {

    @Override
    public MatrixDecompositor decompositor() {
        return new QRDecompositor();
    }

    @Override
    public double[][] input() {
        return new double[][] {
                { 8.0, 0.0, 0.0 }, 
                { 0.0, 4.0, 6.0 }, 
                { 0.0, 0.0, 2.0 } 
        };
    }

    @Override
    public double[][][] output() {
        return new double[][][] {
                { 
                    { 1.0, 0.0, 0.0 }, 
                    { 0.0, 1.0, 0.0 }, 
                    { 0.0, 0.0, 1.0 } 
                },
                { 
                    { 8.0, 0.0, 0.0 }, 
                    { 0.0, 4.0, 6.0 }, 
                    { 0.0, 0.0, 2.0 } 
                } 
        };
    }

    public static Test suite() {
        return new TestSuite(QRDecompositorTest.class);
    }
}
