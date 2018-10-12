package io.mosip.authentication.service.impl.indauth.service.demo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class GenderMatchingStrategyTest {

	/**
	 * Check for Exact type matched with Enum value of Gender Matching Strategy
	 */
	@Test
	public void TestValidExactMatchingStrategytype() {
		assertEquals(GenderMatchingStrategy.EXACT.getType(), MatchingStrategyType.EXACT);
	}

	/**
	 * Check for Exact type not matched with Enum value of Gender Matching Strategy
	 */
	@Test
	public void TestInvalidExactMatchingStrategytype() {
		assertNotEquals(GenderMatchingStrategy.EXACT.getType(), "PARTIAL");
	}

	/**
	 * Assert the Gender Matching Strategy for Exact is Not null
	 */
	@Test
	public void TestValidExactMatchingStrategyfunctionisNotNull() {
		assertNotNull(GenderMatchingStrategy.EXACT.getMatchFunction());
	}

	/**
	 * Assert the Gender Matching Strategy for Exact is null
	 */
	@Test
	public void TestExactMatchingStrategyfunctionisNull() {
		MatchFunction matchFunction = GenderMatchingStrategy.EXACT.getMatchFunction();
		matchFunction = null;
		assertNull(matchFunction);
	}

	/**
	 * Tests doMatch function on Matching Strategy Function
	 */
	@Test
	public void TestValidExactMatchingStrategyFunction() {
		MatchFunction matchFunction = GenderMatchingStrategy.EXACT.getMatchFunction();		
		int value = matchFunction.doMatch("M","M");
		assertEquals(100, value);
		int value1 = matchFunction.doMatch("F","F");
		assertEquals(100, value1);
		int value2 = matchFunction.doMatch("Male","Male");
		assertEquals(100, value2);
		int value3 = matchFunction.doMatch("Female","Female");
		assertEquals(100, value3);
	}

	/**
	 * 
	 * Tests the Match function with in-valid values
	 */
	@Test
	public void TestInvalidExactMatchingStrategyFunction() {
		MatchFunction matchFunction = GenderMatchingStrategy.EXACT.getMatchFunction();
		
		int value = matchFunction.doMatch("M","F");
		assertEquals(0, value);
		
		int value1 = matchFunction.doMatch("F","Female");
		assertEquals(0, value1);

		int value2 = matchFunction.doMatch("Male","M");
		assertEquals(0, value2);
		
		int value3 = matchFunction.doMatch("Female","Male");
		assertEquals(0, value3);
		
		int value4 = matchFunction.doMatch(1,2);
		assertEquals(0, value4);
		
		int value5 = matchFunction.doMatch(1,"Male");
		assertEquals(0, value5);
		
		int value6 = matchFunction.doMatch("Female",1);
		assertEquals(0, value6);
	}





}
