package org.la4j.matrix;

import junit.framework.TestCase;
import org.la4j.matrix.dense.Basic2DMatrix;
import org.la4j.matrix.functor.AdvancedMatrixPredicate;

import java.util.Random;


public class PositiveDefiniteTest extends TestCase {


    public void testPositiveDefiniteTest() {
        warmUp(Matrices.POSITIVE_DEFINITE);
        System.out.println("testPositiveDefiniteTest()");
        positiveDefiniteAbstractTest(Matrices.POSITIVE_DEFINITE);
    }

    public void testPositiveDefiniteOldTest() {
        warmUp(Matrices.POSITIVE_DEFINITE_OLD);
        System.out.println("testPositiveDefiniteOldTest()");
        positiveDefiniteAbstractTest(Matrices.POSITIVE_DEFINITE_OLD);
    }

    public void positiveDefiniteAbstractTest(AdvancedMatrixPredicate predicate) {
        int size = 10;
        int iterations = 10000;
        int maxValue = 10;

        while (size <= 1000) {
            long maxTime = 0;

            for (int i = 0; i < iterations; ++i) {
                Matrix matrix = new Basic2DMatrix(createArray(size, maxValue));
                long startTime = System.nanoTime();
                matrix.is(predicate);
                long totalTime = (System.nanoTime() - startTime) / 1000000;

                if (totalTime > maxTime) {
                    maxTime = totalTime;
                }
            }

            System.out.println("size = " + size + " iterations = " + iterations + " max time = " + maxTime + " ms");

            size *= 10;
        }
    }

    public void warmUp(AdvancedMatrixPredicate predicate) {
        int iterations = 1000;
        int size = 100;
        int maxValue = 10;

        for (int i = 0; i < iterations; ++i) {
            Matrix matrix = new Basic2DMatrix(createArray(size, maxValue));
            matrix.is(predicate);
        }
    }

    public static double[][] createArray(int size, int maxValue) {
        double[][] array = new double[size][size];
        Random r = new Random(maxValue);

        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j) {
                array[i][j] = r.nextDouble();
            }
        }

        return array;
    }
}
