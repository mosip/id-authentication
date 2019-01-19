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
	 * 
	 * @throws IdAuthenticationBusinessException
	 */
	@Test
	public void TestValidExactMatchingStrategyFunction() throws IdAuthenticationBusinessException {
		MatchFunction matchFunction = EmailMatchingStrategy.EXACT.getMatchFunction();
		int value = matchFunction.match("abc@mail.com", "abc@mail.com", null);
		assertEquals(100, value);
	}

	/**
	 * 
	 * Tests the Match function with in-valid values
	 * 
	 * @throws IdAuthenticationBusinessException
	 */
	@Test
	public void TestInvalidExactMatchingStrategyFunction() throws IdAuthenticationBusinessException {

		MatchFunction matchFunction = EmailMatchingStrategy.EXACT.getMatchFunction();
		Map<String, Object> matchProperties = new HashMap<>();
		int value = matchFunction.match("abc@mail.com", "abc@email.com", matchProperties);
		assertEquals(0, value);
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestInvalidEmail() throws IdAuthenticationBusinessException {
		Map<String, Object> matchProperties = new HashMap<>();
		MatchFunction matchFunction = EmailMatchingStrategy.EXACT.getMatchFunction();
		int value4 = matchFunction.match(1, "2", matchProperties);
		assertEquals(0, value4);
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestInvalidE_mail() throws IdAuthenticationBusinessException {
		Map<String, Object> matchProperties = new HashMap<>();
		MatchFunction matchFunction = EmailMatchingStrategy.EXACT.getMatchFunction();
		int value5 = matchFunction.match(1, "abc@mail.com", matchProperties);
		assertEquals(0, value5);
	}
}
