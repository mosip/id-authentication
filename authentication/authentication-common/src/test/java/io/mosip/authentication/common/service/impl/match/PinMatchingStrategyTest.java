package io.mosip.authentication.common.service.impl.match;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import io.mosip.authentication.common.service.impl.match.PinMatchingStrategy;
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
	@Test
	public void TestInvalidExactMatchingStrategyFunction() throws IdAuthenticationBusinessException {
		assertThrows(IdAuthenticationBusinessException.class, () -> {
		MatchFunction matchFunction = PinMatchingStrategy.EXACT.getMatchFunction();
		Map<String, Object> matchProperties = new HashMap<>();
		int value = matchFunction.match(2, "2", matchProperties);
		assertEquals(0, value);

		int value1 = matchFunction.match(2, "dinesh", matchProperties);
		assertEquals(0, value1);
			});
	}

	@Test
	public void TestInvalidExactMatchingStrategyFunctions() throws IdAuthenticationBusinessException {
		assertThrows(IdAuthenticationBusinessException.class, () -> {
		Map<String, Object> matchProperties = new HashMap<>();
		
		MatchFunction matchFunction = PinMatchingStrategy.EXACT.getMatchFunction();

		int value = matchFunction.match(2, "2", matchProperties);
		assertEquals(0, value);

			});
	}

	@Test
	public void TestInvalidMatchwithSecondaryLang() throws IdAuthenticationBusinessException {
		assertThrows(IdAuthenticationBusinessException.class, () -> {
		Map<String, Object> matchProperties = new HashMap<>();
		MatchFunction matchFunction = PinMatchingStrategy.EXACT.getMatchFunction();
		
		int value1 = matchFunction.match(2, "dinesh", matchProperties);
		assertEquals(0, value1);
			});
	}

	@Test
	public void TestInvalid() throws IdAuthenticationBusinessException {
		assertThrows(IdAuthenticationBusinessException.class, () -> {
		Map<String, Object> matchProperties = new HashMap<>();
		MatchFunction matchFunction = PinMatchingStrategy.EXACT.getMatchFunction();
		matchProperties.put("languageType", "test");
		int value2 = matchFunction.match(2, "invalid", matchProperties);
		assertEquals(0, value2);
			});
	}

	@Test
	public void TestInvalidExactMatchingStrategy() throws IdAuthenticationBusinessException {
		assertThrows(IdAuthenticationBusinessException.class, () -> {
		Map<String, Object> matchProperties = new HashMap<>();
		MatchFunction matchFunction = PinMatchingStrategy.EXACT.getMatchFunction();
		int value = matchFunction.match(5, 1, matchProperties);
		assertEquals(0, value);
			});
	}



}
