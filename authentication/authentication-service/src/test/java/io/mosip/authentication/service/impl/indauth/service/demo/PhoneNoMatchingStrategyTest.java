package io.mosip.authentication.service.impl.indauth.service.demo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.function.ToIntBiFunction;

import org.junit.Test;

public class PhoneNoMatchingStrategyTest {

//	/**
//	 * Check for Exact type matched with Enum value of PhoneNo Matching Strategy
//	 */
//	@Test
//	public void TestValidExactMatchingStrategytype() {
//		assertEquals(PhoneNoMatchingStrategy.EXACT.getType(), MatchingStrategyType.EXACT);
//	}
//
//	/**
//	 * Check for Exact type not matched with Enum value of PhoneNo Matching Strategy
//	 */
//	@Test
//	public void TestInvalidExactMatchingStrategytype() {
//		assertNotEquals(PhoneNoMatchingStrategy.EXACT.getType(), "PARTIAL");
//	}
//
//	/**
//	 * Assert the PhoneNo Matching Strategy for Exact is Not null
//	 */
//	@Test
//	public void TestValidExactMatchingStrategyfunctionisNotNull() {
//		assertNotNull(PhoneNoMatchingStrategy.EXACT.getMatchFunction());
//	}
//
//	/**
//	 * Assert the PhoneNo Matching Strategy for Exact is null
//	 */
//	@Test
//	public void TestExactMatchingStrategyfunctionisNull() {
//		ToIntBiFunction<Object, Object> matchFunction = PhoneNoMatchingStrategy.EXACT.getMatchFunction();
//		matchFunction = null;
//		assertNull(matchFunction);
//	}
//
//	/**
//	 * Tests doMatch function on Matching Strategy Function
//	 */
//	@Test
//	public void TestValidExactMatchingStrategyFunction() {
//		ToIntBiFunction<Object, Object> matchFunction = PhoneNoMatchingStrategy.EXACT.getMatchFunction();		
//		int value = matchFunction.applyAsInt("9876543210","9876543210");
//		assertEquals(100, value);
//		int value1 = matchFunction.applyAsInt("+91-9876543210","+91-9876543210");
//		assertEquals(100, value1);
//		int value2 = matchFunction.applyAsInt("413-3432-321","413-3432-321");
//		assertEquals(100, value2);
//	}
//
//	/**
//	 * 
//	 * Tests the Match function with in-valid values
//	 */
//	@Test
//	public void TestInvalidExactMatchingStrategyFunction() {
//		ToIntBiFunction<Object, Object> matchFunction = PhoneNoMatchingStrategy.EXACT.getMatchFunction();
//		
//		int value = matchFunction.applyAsInt("9789438210","1234567890");
//		assertEquals(0, value);
//		
//		int value1 = matchFunction.applyAsInt("76348798","783248712");
//		assertEquals(0, value1);
//
//		int value2 = matchFunction.applyAsInt("789-7389-783","9832-129-322");
//		assertEquals(0, value2);
//		
//		int value3 = matchFunction.applyAsInt("+91-1234567890","1234567890");
//		assertEquals(0, value3);
//		
//		int value4 = matchFunction.applyAsInt(1,2);
//		assertEquals(0, value4);
//		
//		int value5 = matchFunction.applyAsInt(1,"123434545");
//		assertEquals(0, value5);
//		
//		int value6 = matchFunction.applyAsInt("9768456423",1);
//		assertEquals(0, value6);
//	}
}
