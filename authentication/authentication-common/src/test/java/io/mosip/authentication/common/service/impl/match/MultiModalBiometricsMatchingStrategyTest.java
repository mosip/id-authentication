package io.mosip.authentication.common.service.impl.match;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.spi.indauth.match.MatchFunction;
import io.mosip.authentication.core.spi.indauth.match.TriFunctionWithBusinessException;

public class MultiModalBiometricsMatchingStrategyTest {

	private MultiModalBiometricsMatchingStrategy createTestSubject() {
		return MultiModalBiometricsMatchingStrategy.PARTIAL;
	}

	@Test
	public void testGetMatchingStrategy() throws Exception {
		MultiModalBiometricsMatchingStrategy testSubject;

		// default test
		testSubject = createTestSubject();
		MatchFunction matchFunction = testSubject.getMatchingStrategy().getMatchFunction();
		
		Double matchVal = 100.0;
		
		Map<String, String> reqValues = new HashMap<>();
		Map<String, String> entityValues = new HashMap<>();
		Map<String, Object> matchProperties = new HashMap<>();
		TriFunctionWithBusinessException<
			Map<String, String>, 
			Map<String, String>, 
			Map<String, Object>, Double
			> func = (reqMap, entitityMap, props) -> matchVal;
		matchProperties.put(IdaIdMapping.MULTI_MODAL_BIOMETRICS.getIdname(), func);
		int result = matchFunction.match(reqValues, entityValues, matchProperties);
		
		assertEquals(result, matchVal.intValue());
	}
	
	@Test(expected=IdAuthenticationBusinessException.class)
	public void testGetMatchingStrategyException() throws Exception {
		MultiModalBiometricsMatchingStrategy testSubject;

		// default test
		testSubject = createTestSubject();
		MatchFunction matchFunction = testSubject.getMatchingStrategy().getMatchFunction();
		
		Map<String, String> reqValues = new HashMap<>();
		Map<String, String> entityValues = new HashMap<>();
		Map<String, Object> matchProperties = new HashMap<>();
		matchProperties.put(IdaIdMapping.MULTI_MODAL_BIOMETRICS.getIdname(), null);
		matchFunction.match(reqValues, entityValues, matchProperties);
		
	}
	
	@Test
	public void testGetMatchingStrategyEmpty() throws Exception {
		MultiModalBiometricsMatchingStrategy testSubject;

		// default test
		testSubject = createTestSubject();
		MatchFunction matchFunction = testSubject.getMatchingStrategy().getMatchFunction();
		
		Map<String, Object> matchProperties = new HashMap<>();
		matchProperties.put(IdaIdMapping.MULTI_MODAL_BIOMETRICS.getIdname(), null);
		int result = matchFunction.match(null, null, matchProperties);
		
		assertEquals(0, result);
		
	}
}