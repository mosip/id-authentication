package io.mosip.authentication.common.service.impl.match;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.LanguageType;
import io.mosip.authentication.core.spi.bioauth.util.DemoNormalizer;
import io.mosip.authentication.core.spi.indauth.match.MatchFunction;
import io.mosip.authentication.core.spi.indauth.match.MatchingStrategyType;

/**
 * 
 * @author Dinesh Karuppiah
 */
@RunWith(SpringRunner.class)
public class AddressMatchingStrategyTest {
	
	@Mock
	DemoNormalizer demoNormalizer;

	/**
	 * Check for Exact type matched with Enum value of Name Matching Strategy
	 */
	@Test
	public void TestValidExactMatchingStrategytype() {
		assertEquals(AddressMatchingStrategy.EXACT.getType(), MatchingStrategyType.EXACT);
	}

	/**
	 * Check for Exact type not matched with Enum value of Name Matching Strategy
	 */
	@Test
	public void TestInvalidExactMatchingStrategytype() {
		assertNotEquals(AddressMatchingStrategy.EXACT.getType(), "PARTIAL");
	}

	/**
	 * Assert the Name Matching Strategy for Exact is Not null
	 */
	@Test
	public void TestValidExactMatchingStrategyfunctionisNotNull() {
		assertNotNull(AddressMatchingStrategy.EXACT.getMatchFunction());
	}

	/**
	 * Assert the Name Matching Strategy for Exact is null
	 */
	@Test
	public void TestExactMatchingStrategyfunctionisNull() {
		MatchFunction matchFunction = AddressMatchingStrategy.EXACT.getMatchFunction();
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
		MatchFunction matchFunction = AddressMatchingStrategy.EXACT.getMatchFunction();
		Map<String,Object> valueMap=new HashMap<>();
		valueMap.put("demoNormalizer", demoNormalizer);
		valueMap.put("langCode", "fra");
		Mockito.when(demoNormalizer.normalizeAddress(Mockito.anyString(), Mockito.anyString())).thenReturn("no 1 second street chennai");
		int value = matchFunction.match("no 1 second street chennai", "no 1 second street chennai", valueMap);
		assertEquals(100, value);
	}

	@Test
	public void TestInValidExactMatchingStrategyFunction() throws IdAuthenticationBusinessException {
		MatchFunction matchFunction = AddressMatchingStrategy.EXACT.getMatchFunction();
		Map<String, Object> matchProperties = new HashMap<>();
		int value = matchFunction.match("no 1 second street chennai", 2, matchProperties);
		assertEquals(0, value);
	}

	@Test
	public void TestInvalidPrimaryLang() throws IdAuthenticationBusinessException {
		MatchFunction matchFunction = AddressMatchingStrategy.EXACT.getMatchFunction();
		Map<String, Object> matchProperties = new HashMap<>();

		matchProperties = new HashMap<>();
		matchProperties.put("languageType", LanguageType.SECONDARY_LANG);
		int value = matchFunction.match(2, 2, matchProperties);
		assertEquals(0, value);
	}

	/**
	 * 
	 * Tests the Match function with in-valid values
	 * 
	 * @throws IdAuthenticationBusinessException
	 */
	@Test
	public void TestInvalidExactMatchingStrategyFunction() throws IdAuthenticationBusinessException {

		MatchFunction matchFunction = AddressMatchingStrategy.EXACT.getMatchFunction();
		Map<String, Object> matchProperties = new HashMap<>();

		matchProperties = new HashMap<>();
		matchProperties.put("languageType", LanguageType.PRIMARY_LANG);
		int value = matchFunction.match(2, 2, matchProperties);
		assertEquals(0, value);

		matchProperties = new HashMap<>();
		matchProperties.put("languageType", LanguageType.SECONDARY_LANG);
		int value1 = matchFunction.match(2, "no 1 second street chennai", matchProperties);
		assertEquals(0, value1);

		matchProperties = new HashMap<>();
		matchProperties.put("languageType", DemoAuthType.ADDRESS);
		int value2 = matchFunction.match(2, "no 1 second street chennai", matchProperties);
		assertEquals(0, value2);
	}

	@Test
	public void TestInvalidAddressmatchingStrategy() throws IdAuthenticationBusinessException {
		MatchFunction matchFunction = AddressMatchingStrategy.EXACT.getMatchFunction();
		Map<String, Object> matchProperties = new HashMap<>();
		matchProperties = new HashMap<>();
		matchProperties.put("languageType", DemoAuthType.ADDRESS);
		int value2 = matchFunction.match(2, "no 1 second street chennai", matchProperties);
		assertEquals(0, value2);
	}

}
