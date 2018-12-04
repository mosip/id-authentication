package io.mosip.authentication.service.impl.indauth.service.demo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class DOBTypeMatchingStrategyTest {

	/**
	 * Check for Exact type matched with Enum value of DOB Type Matching Strategy
	 */
	@Test
	public void TestValidExactMatchingStrategytype() {
		assertEquals(DOBTypeMatchingStrategy.EXACT.getType(), MatchingStrategyType.EXACT);
	}

	/**
	 * Check for Exact type not matched with Enum value of DOB Type Matching
	 * Strategy
	 */
	@Test
	public void TestInvalidExactMatchingStrategytype() {
		assertNotEquals(DOBTypeMatchingStrategy.EXACT.getType(), "PARTIAL");
	}

	/**
	 * Assert the DOB Type Matching Strategy for Exact is Not null
	 */
	@Test
	public void TestValidExactMatchingStrategyfunctionisNotNull() {
		assertNotNull(DOBTypeMatchingStrategy.EXACT.getMatchFunction());
	}

	/**
	 * Assert the DOB Type Matching Strategy for Exact is null
	 */
	@Test
	public void TestExactMatchingStrategyfunctionisNull() {
		MatchFunction matchFunction = DOBTypeMatchingStrategy.EXACT.getMatchFunction();
		matchFunction = null;
		assertNull(matchFunction);
	}

	/**
	 * Tests doMatch function on Matching Strategy Function
	 */
	@Test
	public void TestValidExactMatchingStrategyFunction() {
		MatchFunction matchFunction = DOBTypeMatchingStrategy.EXACT.getMatchFunction();
		int value = -1;

		value = matchFunction.match("V", "V", null);

		assertEquals(100, value);
	}

	/**
	 * 
	 * Tests the Match function with in-valid values
	 */
	@Test
	public void TestInvalidExactMatchingStrategyFunction() {

		MatchFunction matchFunction = DOBTypeMatchingStrategy.EXACT.getMatchFunction();

		int value = matchFunction.match(332, "V", null);
		assertEquals(0, value);

		int value1 = matchFunction.match("A", "V", null);
		assertEquals(0, value1);

		int value3 = matchFunction.match(null, null, null);
		assertEquals(0, value3);

	}

}
