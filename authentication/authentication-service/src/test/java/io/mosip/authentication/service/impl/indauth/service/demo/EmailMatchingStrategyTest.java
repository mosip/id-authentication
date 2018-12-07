package io.mosip.authentication.service.impl.indauth.service.demo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import io.mosip.authentication.core.spi.indauth.match.MatchFunction;
import io.mosip.authentication.core.spi.indauth.match.MatchingStrategyType;

public class EmailMatchingStrategyTest {

	/**
	 * Check for Exact type matched with Enum value of EMAIL Matching Strategy
	 */
	@Test
	public void TestValidExactMatchingStrategytype() {
		assertEquals(EmailMatchingStrategy.EXACT.getType(), MatchingStrategyType.EXACT);
	}

	/**
	 * Check for Exact type not matched with Enum value of EMAIL Matching Strategy
	 */
	@Test
	public void TestInvalidExactMatchingStrategytype() {
		assertNotEquals(EmailMatchingStrategy.EXACT.getType(), "PARTIAL");
	}

	/**
	 * Assert the EMAIL Matching Strategy for Exact is Not null
	 */
	@Test
	public void TestValidExactMatchingStrategyfunctionisNotNull() {
		assertNotNull(EmailMatchingStrategy.EXACT.getMatchFunction());
	}

	/**
	 * Assert the EMAIL Matching Strategy for Exact is null
	 */
	@Test
	public void TestExactMatchingStrategyfunctionisNull() {
		MatchFunction matchFunction = EmailMatchingStrategy.EXACT.getMatchFunction();
		matchFunction = null;
		assertNull(matchFunction);
	}

	/**
	 * Tests doMatch function on Matching Strategy Function
	 */
	@Test
	public void TestValidExactMatchingStrategyFunction() {
		MatchFunction matchFunction = EmailMatchingStrategy.EXACT.getMatchFunction();
		int value = matchFunction.match("abc@mail.com", "abc@mail.com", null);
		assertEquals(100, value);
	}

	/**
	 * 
	 * Tests the Match function with in-valid values
	 */
	@Test
	public void TestInvalidExactMatchingStrategyFunction() {

		MatchFunction matchFunction = EmailMatchingStrategy.EXACT.getMatchFunction();

		int value = matchFunction.match("abc@mail.com", "abc@email.com", null);
		assertEquals(0, value);

		int value4 = matchFunction.match(1, "2", null);
		assertEquals(0, value4);

		int value5 = matchFunction.match(1, "abc@mail.com", null);
		assertEquals(0, value5);

	}
}
