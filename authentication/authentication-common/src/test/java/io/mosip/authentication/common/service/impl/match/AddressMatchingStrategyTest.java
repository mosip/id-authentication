package io.mosip.authentication.common.service.impl.match;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.spi.indauth.match.MatchFunction;
import io.mosip.authentication.core.spi.indauth.match.MatchingStrategyType;
import io.mosip.authentication.core.util.DemoMatcherUtil;
import io.mosip.authentication.core.util.DemoNormalizer;

/**
 * 
 * @author Dinesh Karuppiah
 * @author Nagarjuna
 */
@RunWith(SpringRunner.class)
public class AddressMatchingStrategyTest {
	
	@Mock
	DemoNormalizer demoNormalizer;
	
	@Mock
	private DemoMatcherUtil demoMatcherUtil;
	
	Map<String, Object> matchProperties = new HashMap<>();
	
	@Before
	public void setup() {		
		matchProperties.put("demoMatcherUtil", demoMatcherUtil);
	}

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
		matchProperties.put("demoNormalizer", demoNormalizer);
		matchProperties.put("langCode", "fra");
		Mockito.when(demoMatcherUtil.doExactMatch(Mockito.anyString(),Mockito.anyString())).thenReturn(100);
		Mockito.when(demoNormalizer.normalizeAddress(Mockito.anyString(), Mockito.anyString())).thenReturn("no 1 second street chennai");
		int value = matchFunction.match("no 1 second street chennai", "no 1 second street chennai", matchProperties);
		assertEquals(100, value);
	}

	@Test
	public void TestInValidExactMatchingStrategyFunction() throws IdAuthenticationBusinessException {
		MatchFunction matchFunction = AddressMatchingStrategy.EXACT.getMatchFunction();
		int value = matchFunction.match("no 1 second street chennai", 2, matchProperties);
		assertEquals(0, value);
	}

	@Test
	public void TestInvalidPrimaryLang() throws IdAuthenticationBusinessException {
		MatchFunction matchFunction = AddressMatchingStrategy.EXACT.getMatchFunction();		
		int value = matchFunction.match(2, 2, matchProperties);
		assertEquals(DemoMatcherUtil.EXACT_MATCH_VALUE, value);
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
		
		
		Mockito.when(demoMatcherUtil.doExactMatch(Mockito.anyString(),Mockito.anyString())).thenReturn(100);
		int value = matchFunction.match(2, 2, matchProperties);
		assertEquals(DemoMatcherUtil.EXACT_MATCH_VALUE, value);		
		
		Mockito.when(demoMatcherUtil.doExactMatch(Mockito.anyString(),Mockito.anyString())).thenReturn(0);
		int value1 = matchFunction.match(2, "no 1 second street chennai", matchProperties);
		assertEquals(0, value1);

		
		matchProperties.put("languageType", DemoAuthType.ADDRESS);
		Mockito.when(demoMatcherUtil.doExactMatch(Mockito.anyString(),Mockito.anyString())).thenReturn(0);
		int value2 = matchFunction.match(2, "no 1 second street chennai", matchProperties);
		assertEquals(0, value2);
	}

	@Test
	public void TestInvalidAddressmatchingStrategy() throws IdAuthenticationBusinessException {
		MatchFunction matchFunction = AddressMatchingStrategy.EXACT.getMatchFunction();
		matchProperties.put("languageType", DemoAuthType.ADDRESS);
		int value2 = matchFunction.match(2, "no 1 second street chennai", matchProperties);
		assertEquals(0, value2);
	}

}
