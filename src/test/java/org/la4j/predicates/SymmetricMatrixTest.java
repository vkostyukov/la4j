package org.la4j.predicates;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.la4j.Matrices;
import org.la4j.Matrix;
import org.la4j.matrix.functor.AdvancedMatrixPredicate;

public class SymmetricMatrixTest {
	
	AdvancedMatrixPredicate predicateUnderTest = Matrices.SYMMETRIC_MATRIX;
	
	@Test
	public void nonSquare_horizontal() {
		
		Matrix a = Matrix.from2DArray(new double[][] {{1,2,3,4,5,6,7},
													  {1,2,3,4,5,6,7}});		
		
		boolean isSymmetric = predicateUnderTest.test(a);
		assertFalse(isSymmetric);
	}
	
	@Test
	public void nonSquare_vertical() {
		
		Matrix a = Matrix.from2DArray(new double[][] {{1,2},
													  {3,4},
													  {5,6},
													  {7,8}});		
		
		boolean isSymmetric = predicateUnderTest.test(a);
		assertFalse(isSymmetric);
	}
	
	@Test
	public void test_1x1() {
		
		Matrix a = Matrix.from2DArray(new double[][] {{1}});		
		
		boolean isSymmetric = predicateUnderTest.test(a);
		assertTrue(isSymmetric);
	}
	
	@Test
	public void test_2x2_not_symmetric() {
		
		Matrix a = Matrix.from2DArray(new double[][] {
			{1,2},
			{3,4}
		});
		
		
		boolean isSymmetric = predicateUnderTest.test(a);
		assertFalse(isSymmetric);
	}
	
	@Test
	public void test_2x2_withNaN() {
		
		Matrix a = Matrix.from2DArray(new double[][] {
			{1          ,Double.NaN},
			{Double.NaN ,         2}
		});		
		
		boolean isSymmetric = predicateUnderTest.test(a);
		assertTrue(isSymmetric);
	}
	
	@Test
	public void test_2x2_symmetric() {
		
		Matrix a = Matrix.from2DArray(new double[][] {
			{1,3},
			{3,2}
		});
		
		
		boolean isSymmetric = predicateUnderTest.test(a);
		assertTrue(isSymmetric);
	}
	
	@Test
	public void test_3x3_symmetric() {
		
		Matrix a = Matrix.from2DArray(new double[][] {
			{1,   1.1, 2},
			{1.1,   2,-1},
			{  2,  -1, 7}
		});
		
		boolean isSymmetric = predicateUnderTest.test(a);
		assertTrue(isSymmetric);
	}
	
	@Test
	public void test_3x3_not_symmetric() {
		
		Matrix a = Matrix.from2DArray(new double[][] {
			{1,   2.1, 4},
			{1.1,   2,-3},
			{  2,  -1, 8}
		});
		
		
		boolean isSymmetric = predicateUnderTest.test(a);
		assertFalse(isSymmetric);
	}
	
	@Test
	public void test_4x4_unit() {
		
		Matrix a = Matrix.from2DArray(new double[][] {
			{1,1,1,1},
			{1,1,1,1},
			{1,1,1,1},
			{1,1,1,1}
		});
		
		boolean isSymmetric = predicateUnderTest.test(a);
		assertTrue(isSymmetric);
	}
	
}
