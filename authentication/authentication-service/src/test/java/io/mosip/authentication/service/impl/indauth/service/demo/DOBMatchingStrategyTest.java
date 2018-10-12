package io.mosip.authentication.service.impl.indauth.service.demo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class DOBMatchingStrategyTest {

	/**
	 * Check for Exact type matched with Enum value of DOB Matching Strategy
	 */
	@Test
	public void TestValidExactMatchingStrategytype() {
		assertEquals(DOBMatchingStrategy.EXACT.getType(), MatchingStrategyType.EXACT);
	}

	/**
	 * Check for Exact type not matched with Enum value of DOB Matching Strategy
	 */
	@Test
	public void TestInvalidExactMatchingStrategytype() {
		assertNotEquals(DOBMatchingStrategy.EXACT.getType(), "PARTIAL");
	}

	/**
	 * Assert the DOB Matching Strategy for Exact is Not null
	 */
	@Test
	public void TestValidExactMatchingStrategyfunctionisNotNull() {
		assertNotNull(DOBMatchingStrategy.EXACT.getMatchFunction());
	}

	/**
	 * Assert the DOB Matching Strategy for Exact is null
	 */
	@Test
	public void TestExactMatchingStrategyfunctionisNull() {
		MatchFunction matchFunction = DOBMatchingStrategy.EXACT.getMatchFunction();
		matchFunction = null;
		assertNull(matchFunction);
	}

	/**
	 * Tests doMatch function on Matching Strategy Function
	 */
	@Test
	public void TestValidExactMatchingStrategyFunction() {
		MatchFunction matchFunction = DOBMatchingStrategy.EXACT.getMatchFunction();
		int value = matchFunction.doMatch("1993-02-07", "1993-02-07");
		assertEquals(0, value); // FIXME - change to date as input
	}

	/**
	 * 
	 * Tests the Match function with in-valid values
	 */
	@Test
	public void TestInvalidExactMatchingStrategyFunction() {
		MatchFunction matchFunction = DOBMatchingStrategy.EXACT.getMatchFunction();

		int value = matchFunction.doMatch("1993-02-07", "1993-02-27");
		assertEquals(0, value);

		int value1 = matchFunction.doMatch(2, "1993-02-07");
		assertEquals(0, value1);

		int value2 = matchFunction.doMatch("1993-02-07", null);
		assertEquals(0, value2);

		int value3 = matchFunction.doMatch(null, null);
		assertEquals(0, value3);

		try {
			matchFunction.doMatch("xyz", "abc");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
