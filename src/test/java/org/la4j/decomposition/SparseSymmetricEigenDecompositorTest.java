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

public class SparseSymmetricEigenDecompositorTest extends AbstractDecompositorTest {

    @Override
    public MatrixDecompositor decompositor() {
        fail("Eigendecomposition is currently broken for this matrix test case.");
        return new EigenDecompositor();
    }

    @Override
    public double[][] input() {
        double[][] matrix = new double[24][24];
        matrix[0][16] = 6.488917882340233;
        matrix[1][14] = 1.0500849109563222;
        matrix[1][18] = 8.057338506021317;
        matrix[2][18] = 5.600914629504165;
        matrix[4][22] = 9.912502191842943;
        matrix[5][13] = 10.983197714528727;
        matrix[8][13] = 6.5109096798879476;
        matrix[9][16] = 1.321253910633423;
        matrix[10][14] = 7.175174747019432;
        matrix[10][17] = 3.0219813644087403;
        matrix[11][18] = 3.5278266874188975;
        matrix[12][13] = 7.425806151241948;
        matrix[12][15] = 3.7347574055072483;
        matrix[13][5] = 10.983197714528727;
        matrix[13][8] = 6.5109096798879476;
        matrix[13][12] = 7.425806151241948;
        matrix[14][1] = 1.0500849109563222;
        matrix[14][10] = 7.175174747019432;
        matrix[15][12] = 3.7347574055072483;
        matrix[15][22] = 3.1315457717146056;
        matrix[16][0] = 6.488917882340233;
        matrix[16][9] = 1.321253910633423;
        matrix[17][10] = 3.0219813644087403;
        matrix[17][18] = 8.059047125400168;
        matrix[18][1] = 8.057338506021317;
        matrix[18][2] = 5.600914629504165;
        matrix[18][11] = 3.5278266874188975;
        matrix[18][17] = 8.059047125400168;
        matrix[22][4] = 9.912502191842943;
        matrix[22][15] = 3.1315457717146056;
        return matrix;
    }

    @Override
    public double[][][] output() {
        int index = 0;
        double[][][] vd = new double[2][][];
        vd[0] = new double[6][24];
        vd[0][1][index] = -0.0000;
        vd[0][2][index] = 0;
        vd[0][3][index] = -0.0000;
        vd[0][4][index] = -0.0000;
        vd[0][5][index] = -0.0000;
        vd[0][6][index] = -0.0000;
        index++;
        vd[0][1][index] = -0.0000;
        vd[0][2][index] = 0.4195;
        vd[0][3][index] = -0.0000;
        vd[0][4][index] = -0.0000;
        vd[0][5][index] = 0.4197;
        vd[0][6][index] = -0.0000;
        index++;
        vd[0][1][index] = -0.0000;
        vd[0][2][index] = 0.2851;
        vd[0][3][index] = -0.0000;
        vd[0][4][index] = -0.0000;
        vd[0][5][index] = 0.2934;
        vd[0][6][index] = -0.0000;
        index++;
        vd[0][1][index] = 0.0000;
        vd[0][2][index] = -0.0000;
        vd[0][3][index] = 0.0000;
        vd[0][4][index] = 0;
        vd[0][5][index] = 0.0000;
        vd[0][6][index] = 0;
        index++;
        vd[0][1][index] = -0.0255;
        vd[0][2][index] = -0.0000;
        vd[0][3][index] = -0.6678;
        vd[0][4][index] = 0.6678;
        vd[0][5][index] = 0.0000;
        vd[0][6][index] = 0.0255;
        index++;
        vd[0][1][index] = -0.5152;
        vd[0][2][index] = -0.0000;
        vd[0][3][index] = 0.0601;
        vd[0][4][index] = 0.0601;
        vd[0][5][index] = -0.0000;
        vd[0][6][index] = -0.5152;
        index++;
        vd[0][1][index] = 0.0000;
        vd[0][2][index] = -0.0000;
        vd[0][3][index] = 0.0000;
        vd[0][4][index] = 0;
        vd[0][5][index] = 0.0000;
        vd[0][6][index] = 0;
        index++;
        vd[0][1][index] = 0.0000;
        vd[0][2][index] = -0.0000;
        vd[0][3][index] = 0.0000;
        vd[0][4][index] = -0.0000;
        vd[0][5][index] = 0.0000;
        vd[0][6][index] = 0.0000;
        index++;
        vd[0][1][index] = -0.3054;
        vd[0][2][index] = -0.0000;
        vd[0][3][index] = 0.0357;
        vd[0][4][index] = 0.0357;
        vd[0][5][index] = -0.0000;
        vd[0][6][index] = -0.3054;
        index++;
        vd[0][1][index] = 0.0000;
        vd[0][2][index] = -0.0000;
        vd[0][3][index] = 0.0000;
        vd[0][4][index] = 0;
        vd[0][5][index] = 0.0000;
        vd[0][6][index] = 0;
        index++;
        vd[0][1][index] = -0.0000;
        vd[0][2][index] = 0.1647;
        vd[0][3][index] = -0.0000;
        vd[0][4][index] = 0.0000;
        vd[0][5][index] = -0.1184;
        vd[0][6][index] = 0.0000;
        index++;
        vd[0][1][index] = -0.0000;
        vd[0][2][index] = 0.1795;
        vd[0][3][index] = 0;
        vd[0][4][index] = -0.0000;
        vd[0][5][index] = 0.1848;
        vd[0][6][index] = -0.0000;
        index++;
        vd[0][1][index] = -0.3738;
        vd[0][2][index] = -0.0000;
        vd[0][3][index] = -0.0400;
        vd[0][4][index] = -0.0400;
        vd[0][5][index] = -0.0000;
        vd[0][6][index] = -0.3738;
        index++;
        vd[0][1][index] = -0.6993;
        vd[0][2][index] = -0.0000;
        vd[0][3][index] = 0.0571;
        vd[0][4][index] = -0.0571;
        vd[0][5][index] = 0.0000;
        vd[0][6][index] = 0.6993;
        index++;
        vd[0][1][index] = 0;
        vd[0][2][index] = 0.1206;
        vd[0][3][index] = -0.0000;
        vd[0][4][index] = 0.0000;
        vd[0][5][index] = 0.0307;
        vd[0][6][index] = -0.0000;
        index++;
        vd[0][1][index] = -0.1017;
        vd[0][2][index] = -0.0000;
        vd[0][3][index] = -0.2253;
        vd[0][4][index] = 0.2253;
        vd[0][5][index] = 0.0000;
        vd[0][6][index] = 0.1017;
        index++;
        vd[0][1][index] = -0.0000;
        vd[0][2][index] = 0.0000;
        vd[0][3][index] = -0.0000;
        vd[0][4][index] = 0.0000;
        vd[0][5][index] = 0.0000;
        vd[0][6][index] = -0.0000;
        index++;
        vd[0][1][index] = -0.0000;
        vd[0][2][index] = 0.4472;
        vd[0][3][index] = -0.0000;
        vd[0][4][index] = -0.0000;
        vd[0][5][index] = 0.4491;
        vd[0][6][index] = -0.0000;
        index++;
        vd[0][1][index] = -0.0000;
        vd[0][2][index] = 0.6848;
        vd[0][3][index] = -0.0000;
        vd[0][4][index] = 0.0000;
        vd[0][5][index] = -0.6979;
        vd[0][6][index] = 0.0000;
        index++;
        vd[0][1][index] = 0.0000;
        vd[0][2][index] = -0.0000;
        vd[0][3][index] = 0.0000;
        vd[0][4][index] = 0;
        vd[0][5][index] = 0.0000;
        vd[0][6][index] = 0.0000;
        index++;
        vd[0][1][index] = 0.0000;
        vd[0][2][index] = -0.0000;
        vd[0][3][index] = 0.0000;
        vd[0][4][index] = 0;
        vd[0][5][index] = 0.0000;
        vd[0][6][index] = 0.0000;
        index++;
        vd[0][1][index] = 0.0000;
        vd[0][2][index] = -0.0000;
        vd[0][3][index] = 0.0000;
        vd[0][4][index] = -0.0000;
        vd[0][5][index] = 0.0000;
        vd[0][6][index] = 0.0000;
        index++;
        vd[0][1][index] = -0.0383;
        vd[0][2][index] = -0.0000;
        vd[0][3][index] = -0.7025;
        vd[0][4][index] = -0.7025;
        vd[0][5][index] = -0.0000;
        vd[0][6][index] = -0.0383;
        index++;
        vd[0][1][index] = 0.0000;
        vd[0][2][index] = -0.0000;
        vd[0][3][index] = 0.0000;
        vd[0][4][index] = -0.0000;
        vd[0][5][index] = 0.0000;
        vd[0][6][index] = 0.0000;
        index++;
        vd[1] = new double[6][6];
        vd[1][0][0] = 14.9063;
        vd[1][1][1] = 13.4549;
        vd[1][2][2] = 10.4274;
        vd[1][3][3] = -10.4274;
        vd[1][4][4] = -13.3208;
        vd[1][5][5] = -14.9063;
        return vd;
    }

    public static Test suite() {
        return new TestSuite(SparseSymmetricEigenDecompositorTest.class);
    }
}
