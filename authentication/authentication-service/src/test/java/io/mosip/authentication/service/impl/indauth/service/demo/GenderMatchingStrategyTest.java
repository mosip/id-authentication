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

		int value = matchFunction.match("M", "M", null);
		assertEquals(100, value);

		int value1 = matchFunction.match("F", "F", null);
		assertEquals(100, value1);

		int value2 = matchFunction.match("Male", "Male", null);
		assertEquals(100, value2);

		int value3 = matchFunction.match("Female", "Female", null);
		assertEquals(100, value3);
	}

	/**
	 * 
	 * Tests the Match function with in-valid values
	 */
	@Test
	public void TestInvalidExactMatchingStrategyFunction() {

		MatchFunction matchFunction = GenderMatchingStrategy.EXACT.getMatchFunction();

		int value = matchFunction.match("M", "F", null);
		assertEquals(0, value);

		int value1 = matchFunction.match("F", "Female", null);
		assertEquals(0, value1);

		int value2 = matchFunction.match("Male", "M", null);
		assertEquals(0, value2);

		int value3 = matchFunction.match("Female", "Male", null);
		assertEquals(0, value3);

		int value4 = matchFunction.match(1, "2", null);
		assertEquals(0, value4);

	}

}
