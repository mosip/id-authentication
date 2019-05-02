package io.mosip.authentication.common.service.impl.match;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import io.mosip.authentication.common.service.impl.match.BioAuthType;
import io.mosip.authentication.common.service.impl.match.DemoAuthType;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.LanguageType;
import io.mosip.authentication.core.spi.indauth.match.MatchFunction;

public class MultiFingerPrintMatchingStratergyTest {
	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestInValidMatchingStrategy() throws IdAuthenticationBusinessException {
		MatchFunction matchFunction = MultiFingerprintMatchingStrategy.PARTIAL.getMatchFunction();
		Map<String, String> reqValues = new HashMap<>();
		reqValues.put("leftIndex", "Test");
		reqValues.put("rightIndex", "test");
		Map<String, String> entityValues = new HashMap<>();
		entityValues.put("leftIndex", "Test");
		entityValues.put("rightIndex", "test");
		Map<String, Object> matchProperties = new HashMap<>();
		matchProperties.put("languageType", LanguageType.PRIMARY_LANG);
		int value = matchFunction.match(reqValues, entityValues, matchProperties);
		assertEquals(0, value);
	}
	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestBusinessException() throws IdAuthenticationBusinessException {
		Map<String, String> reqValues = new HashMap<>();
		MatchFunction matchFunction = MultiFingerprintMatchingStrategy.PARTIAL.getMatchFunction();
		reqValues.put("leftIndex", "Test");
		reqValues.put("rightIndex", "test");
		Map<String, String> entityValues = new HashMap<>();
		entityValues.put("leftIndex", "Test");
		entityValues.put("rightIndex", "test");
		Map<String, Object> matchProperties = new HashMap<>();
		matchProperties.put(BioAuthType.class.getSimpleName(), BioAuthType.FGR_MIN);
		int value = matchFunction.match(reqValues, entityValues, matchProperties);
		assertEquals(0, value);

		matchProperties = new HashMap<>();
		matchProperties.put(BioAuthType.class.getSimpleName(), BioAuthType.FGR_IMG);
		int value1 = matchFunction.match(reqValues, entityValues, matchProperties);
		assertEquals(0, value1);

		matchProperties = new HashMap<>();
		matchProperties.put(BioAuthType.class.getSimpleName(), DemoAuthType.ADDRESS);
		int value2 = matchFunction.match(reqValues, entityValues, matchProperties);
		assertEquals(0, value2);

		matchProperties = new HashMap<>();
		matchProperties.put(BioAuthType.class.getSimpleName(), BioAuthType.IRIS_IMG);
		int value3 = matchFunction.match(reqValues, entityValues, matchProperties);
		assertEquals(0, value3);
	}
	
	@Test
	public void TestInvalidMatchingStrategy() throws IdAuthenticationBusinessException {
		MatchFunction matchFunction = MultiFingerprintMatchingStrategy.PARTIAL.getMatchFunction();
		Map<String, Object> matchProperties = new HashMap<>();
		int value = matchFunction.match("test", "test", matchProperties);
		assertEquals(0, value);
	}

}
