package io.mosip.authentication.common.service.impl.match;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;

import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.spi.indauth.match.TriFunctionWithBusinessException;
import io.mosip.authentication.core.spi.indauth.match.MatchFunction;

public class FaceMatchingStrategyTest {

	@Test
	public void TestUnknownError() throws IdAuthenticationBusinessException {
		MatchFunction matchFunction = FaceMatchingStrategy.PARTIAL.getMatchFunction();
		Map<String, Object> matchProperties = new HashMap<>();
		matchProperties.put(IdaIdMapping.FACE.getIdname(), "test");
		matchFunction.match("name", "name1", matchProperties);
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestInValidMatchingStrategy1() throws IdAuthenticationBusinessException {
		MatchFunction matchFunction = FaceMatchingStrategy.PARTIAL.getMatchFunction();
		Map<String, String> reqValues = new HashMap<>();
		reqValues.put("face", "Test");
		Map<String, String> entityValues = new HashMap<>();
		entityValues.put("face", "Test");
		Map<String, Object> matchProperties = new HashMap<>();
		
		int value = matchFunction.match(reqValues, entityValues, matchProperties);
		assertEquals(0, value);
	}

	@Test
	public void TestInValidMatchingStrategy2() throws IdAuthenticationBusinessException {
		MatchFunction matchFunction = FaceMatchingStrategy.PARTIAL.getMatchFunction();
		Map<String, Object> matchProperties = new HashMap<>();
		
		int value = matchFunction.match(1, 2, matchProperties);
		assertEquals(0, value);
	}

	@Test
	public void TestInValidMatchingStrategy3() throws IdAuthenticationBusinessException {
		Map<String, String> reqValues = new HashMap<>();
		reqValues.put("face", "test");
		MatchFunction matchFunction = FaceMatchingStrategy.PARTIAL.getMatchFunction();
		Map<String, Object> matchProperties = new HashMap<>();
		
		assertEquals(0, matchFunction.match(reqValues, 2, matchProperties));
	}

	@Test
	public void TestValidMatchingStrategy() throws IdAuthenticationBusinessException {
		MatchFunction matchFunction = FaceMatchingStrategy.PARTIAL.getMatchFunction();
		Map<String, String> reqValues = new HashMap<>();
		reqValues.put("face", "test");
		Map<String, String> entityValues = new HashMap<>();
		entityValues.put("face", "test");
		Map<String, Object> matchProperties = new HashMap<>();
		
		matchProperties.put(IdaIdMapping.FACE.getIdname(),
				(TriFunctionWithBusinessException<Map<String, String>, Map<String, String>, Map<String, Object>, Double>) (o1, o2, o3) -> 100.00);
		int value = matchFunction.match(reqValues, entityValues, matchProperties);
		assertEquals(100, value);
	}

	@Test  
	public void TestInValidMatchingStrategy() throws IdAuthenticationBusinessException {
		MatchFunction matchFunction = FaceMatchingStrategy.PARTIAL.getMatchFunction();
		Map<String, String> reqValues = new HashMap<>();
		reqValues.put("face", "test");
		Map<String, String> entityValues = new HashMap<>();
		entityValues.put("face", "test");
		Map<String, Object> matchProperties = new HashMap<>();
		
		matchProperties.put(IdaIdMapping.FACE.getIdname(),
				(TriFunctionWithBusinessException<Map<String, String>, Map<String, String>, Map<String, String>, Double>) (o1, o2, o3) -> 0.00);
		int value = matchFunction.match(reqValues, entityValues, matchProperties);
		assertEquals(0, value);
	}

	/**
	 * Check for Exact type not matched with Enum value of IrisMatchingStrategy
	 * Matching Strategy
	 */
	@Test
	public void TestInvalidExactMatchingStrategytype() {
		assertNotEquals(FaceMatchingStrategy.PARTIAL.getType(), "EXACT");
	}

	/**
	 * Assert the IrisMatchingStrategy Matching Strategy for Exact is Not null
	 */
	@Test
	public void TestValidExactMatchingStrategyfunctionisNotNull() {
		assertNotNull(FaceMatchingStrategy.PARTIAL.getMatchFunction());
	}

	/**
	 * Assert the IrisMatchingStrategy Matching Strategy for Exact is null
	 */
	@Test
	public void TestExactMatchingStrategyfunctionisNull() {
		MatchFunction matchFunction = FaceMatchingStrategy.PARTIAL.getMatchFunction();
		matchFunction = null;
		assertNull(matchFunction);
	}

}
