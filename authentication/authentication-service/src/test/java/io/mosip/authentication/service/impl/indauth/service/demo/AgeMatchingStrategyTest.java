package io.mosip.authentication.service.impl.indauth.service.demo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class AgeMatchingStrategyTest {

	/**
	 * Check for Exact type matched with Enum value of Age Matching Strategy
	 */
	@Test
	public void TestValidExactMatchingStrategytype() {
		assertEquals(AgeMatchingStrategy.EXACT.getType(), MatchStrategyType.EXACT);
	}

	/**
	 * Check for Exact type not matched with Enum value of Age Matching Strategy
	 */
	@Test
	public void TestInvalidExactMatchingStrategytype() {
		assertNotEquals(AgeMatchingStrategy.EXACT.getType(), "PARTIAL");
	}

	/**
	 * Assert the Age Matching Strategy for Exact is Not null
	 */
	@Test
	public void TestValidExactMatchingStrategyfunctionisNotNull() {
		assertNotNull(AgeMatchingStrategy.EXACT.getMatchFunction());
	}

	/**
	 * Assert the Age Matching Strategy for Exact is null
	 */
	@Test
	public void TestExactMatchingStrategyfunctionisNull() {
		MatchFunction matchFunction = AgeMatchingStrategy.EXACT.getMatchFunction();
		matchFunction = null;
		assertNull(matchFunction);
	}

	/**
	 * Tests doMatch function on Matching Strategy Function
	 */
	@Test
	public void TestValidExactMatchingStrategyFunction() {
		MatchFunction matchFunction = AgeMatchingStrategy.EXACT.getMatchFunction();		
		int value = matchFunction.doMatch(25,25);
		assertEquals(100, value);
		int value1 = matchFunction.doMatch(100,100);
		assertEquals(100, value1);
	}

	/**
	 * 
	 * Tests the Match function with in-valid values
	 */
	@Test
	public void TestInvalidExactMatchingStrategyFunction() {
		MatchFunction matchFunction = AgeMatchingStrategy.EXACT.getMatchFunction();
		
		int value = matchFunction.doMatch(250,50);
		assertEquals(0, value);
		
		int value1 = matchFunction.doMatch(50,25);
		assertEquals(0, value1);

		int value2 = matchFunction.doMatch(100,25);
		assertEquals(0, value2);
		
		int value3 = matchFunction.doMatch(25,24);
		assertEquals(0, value3);
		
		int value4 = matchFunction.doMatch(null,null);
		assertEquals(0, value4);
		
		int value5 = matchFunction.doMatch(1,"abc");
		assertEquals(0, value5);
		
		int value6 = matchFunction.doMatch("abc",1);
		assertEquals(0, value6);
	}
}
