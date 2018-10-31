package io.mosip.authentication.service.impl.indauth.service.demo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.function.ToIntBiFunction;

import org.junit.Test;

public class AgeMatchingStrategyTest {

	/**
	 * Check for Exact type matched with Enum value of Age Matching Strategy
	 */
//	@Test
//	public void TestValidExactMatchingStrategytype() {
//		assertEquals(AgeMatchingStrategy.EXACT.getType(), MatchingStrategyType.EXACT);
//	}
//
//	/**
//	 * Check for Exact type not matched with Enum value of Age Matching Strategy
//	 */
//	@Test
//	public void TestInvalidExactMatchingStrategytype() {
//		assertNotEquals(AgeMatchingStrategy.EXACT.getType(), "PARTIAL");
//	}
//
//	/**
//	 * Assert the Age Matching Strategy for Exact is Not null
//	 */
//	@Test
//	public void TestValidExactMatchingStrategyfunctionisNotNull() {
//		assertNotNull(AgeMatchingStrategy.EXACT.getMatchFunction());
//	}
//
//	/**
//	 * Assert the Age Matching Strategy for Exact is null
//	 */
//	@Test
//	public void TestExactMatchingStrategyfunctionisNull() {
//		ToIntBiFunction<Object, Object> matchFunction = AgeMatchingStrategy.EXACT.getMatchFunction();
//		matchFunction = null;
//		assertNull(matchFunction);
//	}
//
//	/**
//	 * Tests doMatch function on Matching Strategy Function
//	 */
//	@Test
//	public void TestValidExactMatchingStrategyFunction() {
//		ToIntBiFunction<Object, Object> matchFunction = AgeMatchingStrategy.EXACT.getMatchFunction();		
//		int value = matchFunction.applyAsInt(25,25);
//		assertEquals(100, value);
//		int value1 = matchFunction.applyAsInt(100,100);
//		assertEquals(100, value1);
//	}
//
//	/**
//	 * 
//	 * Tests the Match function with in-valid values
//	 */
//	@Test
//	public void TestInvalidExactMatchingStrategyFunction() {
//		ToIntBiFunction<Object, Object> matchFunction = AgeMatchingStrategy.EXACT.getMatchFunction();
//		
//		int value = matchFunction.applyAsInt(250,50);
//		assertEquals(0, value);
//		
//		int value1 = matchFunction.applyAsInt(50,25);
//		assertEquals(0, value1);
//
//		int value2 = matchFunction.applyAsInt(100,25);
//		assertEquals(0, value2);
//		
//		int value3 = matchFunction.applyAsInt(25,24);
//		assertEquals(0, value3);
//		
//		int value4 = matchFunction.applyAsInt(null,null);
//		assertEquals(0, value4);
//		
//		int value5 = matchFunction.applyAsInt(1,"abc");
//		assertEquals(0, value5);
//		
//		int value6 = matchFunction.applyAsInt("abc",1);
//		assertEquals(0, value6);
//	}
}
