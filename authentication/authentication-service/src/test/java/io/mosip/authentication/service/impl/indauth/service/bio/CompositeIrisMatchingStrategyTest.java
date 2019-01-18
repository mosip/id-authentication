package io.mosip.authentication.service.impl.indauth.service.bio;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import org.junit.Test;

import io.mosip.authentication.core.dto.indauth.LanguageType;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.spi.indauth.match.MatchFunction;
import io.mosip.authentication.core.spi.irisauth.provider.IrisProvider;

public class CompositeIrisMatchingStrategyTest {
	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestInValidMatchingStrategy1() throws IdAuthenticationBusinessException {
		MatchFunction matchFunction = CompositeIrisMatchingStrategy.PARTIAL.getMatchFunction();
		Map<String, String> reqValues = new HashMap<>();
		reqValues.put("leftEye", "Test");
		reqValues.put("rightEye", "test");
		Map<String, String> entityValues = new HashMap<>();
		entityValues.put("leftEye", "Test");
		entityValues.put("rightEye", "test");
		Map<String, Object> matchProperties = new HashMap<>();
		matchProperties.put("languageType", LanguageType.PRIMARY_LANG);
		int value = matchFunction.match(reqValues, entityValues, matchProperties);
		assertEquals(0, value);
	}
	
	@Test
	public void TestInValidMatchingStrategy2() throws IdAuthenticationBusinessException {
		MatchFunction matchFunction = CompositeIrisMatchingStrategy.PARTIAL.getMatchFunction();
		Map<String, Object> matchProperties = new HashMap<>();
		matchProperties.put("languageType", LanguageType.PRIMARY_LANG);
		int value = matchFunction.match(1, 2, matchProperties);
		assertEquals(0, value);
	}
	@Test
	public void TestInValidMatchingStrategy3() throws IdAuthenticationBusinessException {
		MatchFunction matchFunction = CompositeIrisMatchingStrategy.PARTIAL.getMatchFunction();
		Map<String, Object> matchProperties = new HashMap<>();
		matchProperties.put("languageType", LanguageType.PRIMARY_LANG);
		assertEquals(0, matchFunction.match(1, 2, matchProperties));
	}
	
	@Test
	public void TestValidMatchingStrategy() throws IdAuthenticationBusinessException {
		MatchFunction matchFunction = CompositeIrisMatchingStrategy.PARTIAL.getMatchFunction();
		Map<String, String> reqValues = new HashMap<>();
		reqValues.put("leftEye", "Test");
		reqValues.put("rightEye", "test");
		Map<String, String> entityValues = new HashMap<>();
		entityValues.put("leftEye", "Test");
		entityValues.put("rightEye", "test");
		Map<String, Object> matchProperties = new HashMap<>();
		matchProperties.put("languageType", LanguageType.PRIMARY_LANG);
		matchProperties.put(IrisProvider.class.getSimpleName(), (BiFunction<Map<String, String>, Map<String, String>, Double>) (o1, o2) -> 0.00);
		int value = matchFunction.match(reqValues, entityValues, matchProperties);
		assertEquals(0, value);
	}
	
	/**
	 * Check for Exact type not matched with Enum value of CompositeIrisMatchingStrategy Matching Strategy
	 */
	@Test
	public void TestInvalidExactMatchingStrategytype() {
		assertNotEquals(CompositeIrisMatchingStrategy.PARTIAL.getType(), "EXACT");
	}

	/**
	 * Assert the CompositeIrisMatchingStrategy Matching Strategy for Exact is Not null
	 */
	@Test
	public void TestValidExactMatchingStrategyfunctionisNotNull() {
		assertNotNull(CompositeIrisMatchingStrategy.PARTIAL.getMatchFunction());
	}

	/**
	 * Assert the CompositeIrisMatchingStrategy Matching Strategy for Exact is null
	 */
	@Test
	public void TestExactMatchingStrategyfunctionisNull() {
		MatchFunction matchFunction = CompositeIrisMatchingStrategy.PARTIAL.getMatchFunction();
		matchFunction = null;
		assertNull(matchFunction);
	}



}
