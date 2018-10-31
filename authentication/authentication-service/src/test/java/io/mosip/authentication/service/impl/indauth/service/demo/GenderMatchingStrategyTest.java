package io.mosip.authentication.service.impl.indauth.service.demo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.function.ToIntBiFunction;

import org.junit.Test;

public class GenderMatchingStrategyTest {

//	/**
//	 * Check for Exact type matched with Enum value of Gender Matching Strategy
//	 */
//	@Test
//	public void TestValidExactMatchingStrategytype() {
//		assertEquals(GenderMatchingStrategy.EXACT.getType(), MatchingStrategyType.EXACT);
//	}
//
//	/**
//	 * Check for Exact type not matched with Enum value of Gender Matching Strategy
//	 */
//	@Test
//	public void TestInvalidExactMatchingStrategytype() {
//		assertNotEquals(GenderMatchingStrategy.EXACT.getType(), "PARTIAL");
//	}
//
//	/**
//	 * Assert the Gender Matching Strategy for Exact is Not null
//	 */
//	@Test
//	public void TestValidExactMatchingStrategyfunctionisNotNull() {
//		assertNotNull(GenderMatchingStrategy.EXACT.getMatchFunction());
//	}
//
//	/**
//	 * Assert the Gender Matching Strategy for Exact is null
//	 */
//	@Test
//	public void TestExactMatchingStrategyfunctionisNull() {
//		ToIntBiFunction<Object, Object> matchFunction = GenderMatchingStrategy.EXACT.getMatchFunction();
//		matchFunction = null;
//		assertNull(matchFunction);
//	}
//
//	/**
//	 * Tests doMatch function on Matching Strategy Function
//	 */
//	@Test
//	public void TestValidExactMatchingStrategyFunction() {
//		ToIntBiFunction<Object, Object> matchFunction = GenderMatchingStrategy.EXACT.getMatchFunction();		
//		int value = matchFunction.applyAsInt("M","M");
//		assertEquals(100, value);
//		int value1 = matchFunction.applyAsInt("F","F");
//		assertEquals(100, value1);
//		int value2 = matchFunction.applyAsInt("Male","Male");
//		assertEquals(100, value2);
//		int value3 = matchFunction.applyAsInt("Female","Female");
//		assertEquals(100, value3);
//	}
//
//	/**
//	 * 
//	 * Tests the Match function with in-valid values
//	 */
//	@Test
//	public void TestInvalidExactMatchingStrategyFunction() {
//		ToIntBiFunction<Object, Object> matchFunction = GenderMatchingStrategy.EXACT.getMatchFunction();
//		
//		int value = matchFunction.applyAsInt("M","F");
//		assertEquals(0, value);
//		
//		int value1 = matchFunction.applyAsInt("F","Female");
//		assertEquals(0, value1);
//
//		int value2 = matchFunction.applyAsInt("Male","M");
//		assertEquals(0, value2);
//		
//		int value3 = matchFunction.applyAsInt("Female","Male");
//		assertEquals(0, value3);
//		
//		int value4 = matchFunction.applyAsInt(1,2);
//		assertEquals(0, value4);
//		
//		int value5 = matchFunction.applyAsInt(1,"Male");
//		assertEquals(0, value5);
//		
//		int value6 = matchFunction.applyAsInt("Female",1);
//		assertEquals(0, value6);
//	}





}
