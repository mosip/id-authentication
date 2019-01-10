package io.mosip.authentication.service.impl.indauth.service.bio;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import io.mosip.authentication.core.dto.indauth.LanguageType;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.spi.fingerprintauth.provider.FingerprintProvider;
import io.mosip.authentication.core.spi.indauth.match.MatchFunction;
import io.mosip.authentication.service.impl.indauth.service.demo.DemoAuthType;

public class FingerPrintMatchingStrategyTest {

	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestInValidMatchingStrategy() throws IdAuthenticationBusinessException {
		MatchFunction matchFunction = FingerPrintMatchingStrategy.PARTIAL.getMatchFunction();
		Map<String, Object> matchProperties = new HashMap<>();
		matchProperties.put("languageType", LanguageType.PRIMARY_LANG);
		int value = matchFunction.match("dinesh karuppiah", 2, matchProperties);
		assertEquals(0, value);
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestFgrMinException() throws IdAuthenticationBusinessException {
		Map<String, Object> matchProperties = new HashMap<>();
		MatchFunction matchFunction = FingerPrintMatchingStrategy.PARTIAL.getMatchFunction();
		matchProperties.put(BioAuthType.class.getSimpleName(), BioAuthType.FGR_MIN);
		int value = matchFunction.match("dinesh karuppiah", 2, matchProperties);
		assertEquals(0, value);
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestFgrImgException() throws IdAuthenticationBusinessException {
		Map<String, Object> matchProperties = new HashMap<>();
		MatchFunction matchFunction = FingerPrintMatchingStrategy.PARTIAL.getMatchFunction();
		matchProperties.put(BioAuthType.class.getSimpleName(), BioAuthType.FGR_IMG);
		int value1 = matchFunction.match("dinesh karuppiah", 2, matchProperties);
		assertEquals(0, value1);

	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestOtherType() throws IdAuthenticationBusinessException {
		Map<String, Object> matchProperties = new HashMap<>();
		MatchFunction matchFunction = FingerPrintMatchingStrategy.PARTIAL.getMatchFunction();
		matchProperties.put(BioAuthType.class.getSimpleName(), DemoAuthType.AD_PRI);
		int value2 = matchFunction.match("dinesh karuppiah", 2, matchProperties);
		assertEquals(0, value2);
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestInvalidAuthtype() throws IdAuthenticationBusinessException {
		Map<String, Object> matchProperties = new HashMap<>();
		MatchFunction matchFunction = FingerPrintMatchingStrategy.PARTIAL.getMatchFunction();
		matchProperties.put(BioAuthType.class.getSimpleName(), BioAuthType.IRIS_IMG);
		int value3 = matchFunction.match("dinesh karuppiah", 2, matchProperties);
		assertEquals(0, value3);
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestvalidFingerPrint() throws IdAuthenticationBusinessException {
		Map<String, Object> matchProperties = new HashMap<>();
		String value = "Rk1SACAyMAAAAAEIAAABPAFiAMUAxQEAAAAoJ4CEAOs8UICiAQGXUIBzANXIV4CmARiXUEC6AObFZIB3ALUSZEBlATPYZICIAKUCZEBmAJ4YZEAnAOvBZIDOAKTjZEBCAUbQQ0ARANu0ZECRAOC4NYBnAPDUXYCtANzIXUBhAQ7bZIBTAQvQZICtASqWZEDSAPnMZICaAUAVZEDNAS63Q0CEAVZiSUDUAT+oNYBhAVprSUAmAJyvZICiAOeyQ0CLANDSPECgAMzXQ0CKAR8OV0DEAN/QZEBNAMy9ZECaAKfwZEC9ATieUEDaAMfWUEDJAUA2NYB5AVttSUBKAI+oZECLAG0FZAAA";
		MatchFunction matchFunction = FingerPrintMatchingStrategy.PARTIAL.getMatchFunction();
		matchProperties.put(FingerprintProvider.class.getSimpleName(), "Test");
		int value3 = matchFunction.match(value, value, matchProperties);
		System.err.println(value3);

	}

}
