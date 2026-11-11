package io.mosip.authentication.common.service.impl.match;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.spi.indauth.match.TriFunctionWithBusinessException;
import io.mosip.authentication.core.spi.indauth.match.MatchFunction;

public class CompositeIrisMatchingStrategyTest {
	@Test
	public void TestInValidMatchingStrategy1() throws IdAuthenticationBusinessException {
		assertThrows(IdAuthenticationBusinessException.class, () -> {
		MatchFunction matchFunction = CompositeIrisMatchingStrategy.PARTIAL.getMatchFunction();
		Map<String, String> reqValues = new HashMap<>();
		reqValues.put("leftEye", "Test");
		reqValues.put("rightEye", "test");
		Map<String, String> entityValues = new HashMap<>();
		entityValues.put("leftEye", "Test");
		entityValues.put("rightEye", "test");
		Map<String, Object> matchProperties = new HashMap<>();
		
		int value = matchFunction.match(reqValues, entityValues, matchProperties);
		assertEquals(0, value);
			});
	}

	@Test
	public void TestInValidMatchingStrategy2() throws IdAuthenticationBusinessException {
		MatchFunction matchFunction = CompositeIrisMatchingStrategy.PARTIAL.getMatchFunction();
		Map<String, Object> matchProperties = new HashMap<>();
		
		int value = matchFunction.match(1, 2, matchProperties);
		assertEquals(0, value);
	}

	@Test
	public void TestInValidMatchingStrategy3() throws IdAuthenticationBusinessException {
		MatchFunction matchFunction = CompositeIrisMatchingStrategy.PARTIAL.getMatchFunction();
		Map<String, Object> matchProperties = new HashMap<>();
		
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
		
		matchProperties.put(IdaIdMapping.IRIS.getIdname(),
				(TriFunctionWithBusinessException<Map<String, String>, Map<String, String>, Map<String, String>, Double>) (o1, o2, o3) -> 0.00);
		int value = matchFunction.match(reqValues, entityValues, matchProperties);
		assertEquals(0, value);
	}

	/**
	 * Check for Exact type not matched with Enum value of
	 * CompositeIrisMatchingStrategy Matching Strategy
	 */
	@Test
	public void TestInvalidExactMatchingStrategytype() {
		assertNotEquals(CompositeIrisMatchingStrategy.PARTIAL.getType(), "EXACT");
	}

	/**
	 * Assert the CompositeIrisMatchingStrategy Matching Strategy for Exact is Not
	 * null
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
