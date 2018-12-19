package io.mosip.authentication.service.impl.indauth.service.demo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.spi.indauth.match.MatchFunction;
import io.mosip.authentication.core.spi.indauth.match.MatchingStrategyType;

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
	 * 
	 * @throws IdAuthenticationBusinessException
	 */
	@Test
	public void TestValidExactMatchingStrategyFunction() throws IdAuthenticationBusinessException {

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
	 * 
	 * @throws IdAuthenticationBusinessException
	 */
	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestInvalidExactMatchingStrategyFunction() throws IdAuthenticationBusinessException {
		Map<String, Object> matchProperties = new HashMap<>();
		MatchFunction matchFunction = GenderMatchingStrategy.EXACT.getMatchFunction();

		int value = matchFunction.match("M", "F", matchProperties);
		assertEquals(0, value);

		int value1 = matchFunction.match("F", "Female", matchProperties);
		assertEquals(0, value1);

		int value2 = matchFunction.match("Male", "M", matchProperties);
		assertEquals(0, value2);

		int value3 = matchFunction.match("Female", "Male", matchProperties);
		assertEquals(0, value3);

		int value4 = matchFunction.match(1, "2", null);
		assertEquals(0, value4);

	}

}
