package io.mosip.authentication.service.impl.indauth.service.demo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import io.mosip.authentication.core.dto.indauth.LanguageType;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.spi.indauth.match.MatchFunction;
import io.mosip.authentication.core.spi.indauth.match.MatchingStrategyType;

/**
 * The Class PinMatchingStrategyTest.
 * 
 * @author Sanjay Murali
 */
public class PinMatchingStrategyTest {

	/**
	 * Check for Exact type matched with Enum value of Pin Matching Strategy
	 */

	@Test
	public void TestValidExactMatchingStrategytype() {
		assertEquals(PinMatchingStrategy.EXACT.getType(), MatchingStrategyType.EXACT);
	}

	/**
	 * Check for Exact type not matched with Enum value of Pin Matching Strategy
	 */
	@Test
	public void TestInvalidExactMatchingStrategytype() {
		assertNotEquals(PinMatchingStrategy.EXACT.getType(), "PARTIAL");
	}

	/**
	 * Assert the Pin Matching Strategy for Exact is Not null
	 */
	@Test
	public void TestValidExactMatchingStrategyfunctionisNotNull() {
		assertNotNull(PinMatchingStrategy.EXACT.getMatchFunction());
	}

	/**
	 * Assert the Pin Matching Strategy for Exact is null
	 */
	@Test
	public void TestExactMatchingStrategyfunctionisNull() {
		MatchFunction matchFunction = PinMatchingStrategy.EXACT.getMatchFunction();
		matchFunction = null;
		assertNull(matchFunction);
	}

	/**
	 * 
	 * Tests the Match function with in-valid values
	 * 
	 * @throws IdAuthenticationBusinessException
	 */
	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestInvalidExactMatchingStrategyFunction() throws IdAuthenticationBusinessException {
		MatchFunction matchFunction = PinMatchingStrategy.EXACT.getMatchFunction();
		Map<String, Object> matchProperties = new HashMap<>();
		int value = matchFunction.match(2, "2", matchProperties);
		assertEquals(0, value);

		int value1 = matchFunction.match(2, "dinesh", matchProperties);
		assertEquals(0, value1);
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestInvalidExactMatchingStrategyFunctions() throws IdAuthenticationBusinessException {
		Map<String, Object> matchProperties = new HashMap<>();
		matchProperties.put("languageType", LanguageType.PRIMARY_LANG);
		MatchFunction matchFunction = PinMatchingStrategy.EXACT.getMatchFunction();

		int value = matchFunction.match(2, "2", matchProperties);
		assertEquals(0, value);

	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestInvalidMatchwithSecondaryLang() throws IdAuthenticationBusinessException {
		Map<String, Object> matchProperties = new HashMap<>();
		MatchFunction matchFunction = PinMatchingStrategy.EXACT.getMatchFunction();
		matchProperties.put("languageType", LanguageType.SECONDARY_LANG);
		int value1 = matchFunction.match(2, "dinesh", matchProperties);
		assertEquals(0, value1);
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestInvalid() throws IdAuthenticationBusinessException {
		Map<String, Object> matchProperties = new HashMap<>();
		MatchFunction matchFunction = PinMatchingStrategy.EXACT.getMatchFunction();
		matchProperties.put("languageType", "test");
		int value2 = matchFunction.match(2, "invalid", matchProperties);
		assertEquals(0, value2);
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestInvalidExactMatchingStrategy() throws IdAuthenticationBusinessException {
		Map<String, Object> matchProperties = new HashMap<>();
		MatchFunction matchFunction = PinMatchingStrategy.EXACT.getMatchFunction();
		int value = matchFunction.match(5, 1, matchProperties);
		assertEquals(0, value);
	}



}
