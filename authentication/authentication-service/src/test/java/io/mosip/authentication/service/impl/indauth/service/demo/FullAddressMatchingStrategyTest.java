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
 * 
 * @author Dinesh Karuppiah
 */
public class FullAddressMatchingStrategyTest {

	/**
	 * Check for Exact type matched with Enum value of Name Matching Strategy
	 */
	@Test
	public void TestValidExactMatchingStrategytype() {
		assertEquals(FullAddressMatchingStrategy.EXACT.getType(), MatchingStrategyType.EXACT);
	}

	/**
	 * Check for Exact type not matched with Enum value of Name Matching Strategy
	 */
	@Test
	public void TestInvalidExactMatchingStrategytype() {
		assertNotEquals(FullAddressMatchingStrategy.EXACT.getType(), "PARTIAL");
	}

	/**
	 * Assert the Name Matching Strategy for Exact is Not null
	 */
	@Test
	public void TestValidExactMatchingStrategyfunctionisNotNull() {
		assertNotNull(FullAddressMatchingStrategy.EXACT.getMatchFunction());
	}

	/**
	 * Assert the Name Matching Strategy for Exact is null
	 */
	@Test
	public void TestExactMatchingStrategyfunctionisNull() {
		MatchFunction matchFunction = FullAddressMatchingStrategy.EXACT.getMatchFunction();
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
		MatchFunction matchFunction = FullAddressMatchingStrategy.EXACT.getMatchFunction();
		String expected = "a k Chowdary Beach view colony apt 12 main st TamilNadu 560055";
		int value = matchFunction.match("a k Chowdary Beach view colony apt 12 main st TamilNadu 560055", expected,
				null);
		assertEquals(100, value);
	}

	/**
	 * 
	 * 
	 * @throws IdAuthenticationBusinessException
	 */
	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestInvalidExactMatchingStrategyFunction() throws IdAuthenticationBusinessException {
		MatchFunction matchFunction = FullAddressMatchingStrategy.EXACT.getMatchFunction();
		Map<String, Object> matchProperties = new HashMap<>();
		matchProperties.put("languageType", LanguageType.PRIMARY_LANG);
		int value = matchFunction.match(2, "2", matchProperties);
		assertEquals(0, value);

	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestExactMatchInvalidSecondaryLang() throws IdAuthenticationBusinessException {
		MatchFunction matchFunction = FullAddressMatchingStrategy.EXACT.getMatchFunction();
		Map<String, Object> matchProperties = new HashMap<>();
		matchProperties.put("languageType", LanguageType.SECONDARY_LANG);
		int value1 = matchFunction.match(2, "no 1 second street chennai", matchProperties);
		assertEquals(0, value1);
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestExactMatchOtherLangType() throws IdAuthenticationBusinessException {
		MatchFunction matchFunction = FullAddressMatchingStrategy.EXACT.getMatchFunction();
		Map<String, Object> matchProperties = new HashMap<>();
		matchProperties = new HashMap<>();
		matchProperties.put("languageType", DemoAuthType.AD_PRI);
		int value2 = matchFunction.match(2, "no 1 second street chennai", matchProperties);
		assertEquals(0, value2);
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestInvalidPartialMatch() throws IdAuthenticationBusinessException {
		MatchFunction matchFunction = FullAddressMatchingStrategy.PARTIAL.getMatchFunction();
		Map<String, Object> matchProperties = new HashMap<>();
		matchProperties.put("languageType", LanguageType.PRIMARY_LANG);
		int value = matchFunction.match(2, "2", matchProperties);
		assertEquals(0, value);

	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestInvalidPartialMatchSecondaryLang() throws IdAuthenticationBusinessException {
		MatchFunction matchFunction = FullAddressMatchingStrategy.PARTIAL.getMatchFunction();
		Map<String, Object> matchProperties = new HashMap<>();
		matchProperties.put("languageType", LanguageType.SECONDARY_LANG);
		int value1 = matchFunction.match(2, "no 1 second street chennai", matchProperties);
		assertEquals(0, value1);
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestInvalidPartialMatchOtherLangType() throws IdAuthenticationBusinessException {
		MatchFunction matchFunction = FullAddressMatchingStrategy.PARTIAL.getMatchFunction();
		Map<String, Object> matchProperties = new HashMap<>();
		matchProperties = new HashMap<>();
		matchProperties.put("languageType", DemoAuthType.AD_PRI);
		int value2 = matchFunction.match(2, "no 1 second street chennai", matchProperties);
		assertEquals(0, value2);
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestInvalidPhoneticsMatch() throws IdAuthenticationBusinessException {
		MatchFunction matchFunction = FullAddressMatchingStrategy.PHONETICS.getMatchFunction();
		Map<String, Object> matchProperties = new HashMap<>();
		matchProperties.put("languageType", LanguageType.PRIMARY_LANG);
		int value = matchFunction.match(2, "2", matchProperties);
		assertEquals(0, value);

	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestInvalidPhoneticsMatchSecondaryLang() throws IdAuthenticationBusinessException {
		MatchFunction matchFunction = FullAddressMatchingStrategy.PHONETICS.getMatchFunction();
		Map<String, Object> matchProperties = new HashMap<>();
		matchProperties.put("languageType", LanguageType.SECONDARY_LANG);
		int value1 = matchFunction.match(2, "no 1 second street chennai", matchProperties);
		assertEquals(0, value1);
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestInvalidPhoneticsMatchOtherLangType() throws IdAuthenticationBusinessException {
		MatchFunction matchFunction = FullAddressMatchingStrategy.PHONETICS.getMatchFunction();
		Map<String, Object> matchProperties = new HashMap<>();
		matchProperties = new HashMap<>();
		matchProperties.put("languageType", DemoAuthType.AD_PRI);
		int value2 = matchFunction.match(2, "no 1 second street chennai", matchProperties);
		assertEquals(0, value2);
	}

	/**
	 * Assert Partial type matched with Enum value of Name Matching Strategy
	 */
	@Test
	public void TestValidPartialMatchingStrategytype() {
		assertEquals(FullAddressMatchingStrategy.PARTIAL.getType(), MatchingStrategyType.PARTIAL);
	}

	/**
	 * Assert Partial type not-matched with Enum value of Name Matching Strategy
	 */
	@Test
	public void TestInvalidPartialMatchingStrategytype() {
		assertNotEquals(FullAddressMatchingStrategy.PARTIAL.getType(), "EXACT");
	}

	/**
	 * Assert Partial Match function is not null
	 */
	@Test
	public void TestValidPartialMatchingStrategyfunctionisNotNull() {
		assertNotNull(FullAddressMatchingStrategy.PARTIAL.getMatchFunction());
	}

	/**
	 * Assert Partial Match function is null
	 */
	@Test
	public void TestPartialMatchingStrategyfunctionisNull() {
		MatchFunction matchFunction = FullAddressMatchingStrategy.PARTIAL.getMatchFunction();
		matchFunction = null;
		assertNull(matchFunction);
	}

	/**
	 * Assert the Partial Match Function with Do-Match Method
	 * 
	 * @throws IdAuthenticationBusinessException
	 */
	@Test
	public void TestValidPartialMatchingStrategyFunction() throws IdAuthenticationBusinessException {
		MatchFunction matchFunction = FullAddressMatchingStrategy.PARTIAL.getMatchFunction();
		int value = matchFunction.match("street chennai", "no IdentityValue1 second street chennai", null);
		assertEquals(50, value);
	}

	/**
	 * Assert the Partial Match Function not matched via Do-Match Method
	 * 
	 * @throws IdAuthenticationBusinessException
	 */
	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestInvalidPartialMatchingStrategyFunction() throws IdAuthenticationBusinessException {
		MatchFunction matchFunction = FullAddressMatchingStrategy.PARTIAL.getMatchFunction();
		Map<String, Object> matchProperties = new HashMap<>();

		int value = matchFunction.match(2, "2", matchProperties);
		assertEquals(0, value);

		int value1 = matchFunction.match(2, "2", matchProperties);
		assertEquals(0, value1);

	}

	/**
	 * Assert Phonetics Match Value is not-null on Name Matching Strategy
	 */
	@Test
	public void TestPhoneticsMatch() {
		assertNotNull(FullAddressMatchingStrategy.PHONETICS);
	}

	/**
	 * Assert Phonetics Match Type is equals with Matching Strategy type
	 */
	@Test
	public void TestPhoneticsMatchStrategyType() {
		assertEquals(FullAddressMatchingStrategy.PHONETICS.getType(), MatchingStrategyType.PHONETICS);
	}

	/**
	 * Assert Phonetics Match Value via doPhoneticsMatch method
	 * 
	 * @throws IdAuthenticationBusinessException
	 */
	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestPhoneticsMatchValue() throws IdAuthenticationBusinessException {
		Map<String, Object> matchProperties = new HashMap<>();
		MatchFunction matchFunction = FullAddressMatchingStrategy.PHONETICS.getMatchFunction();
		int value = matchFunction.match(2, "2", matchProperties);
		assertEquals(0, value);
	}

	/**
	 * Assert Phonetics Match Value via doPhoneticsMatch method
	 * 
	 * @throws IdAuthenticationBusinessException
	 */
	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestPhoneticsMatchValueWithLanguageCode_Return_NotMatched() throws IdAuthenticationBusinessException {
		MatchFunction matchFunction = FullAddressMatchingStrategy.PHONETICS.getMatchFunction();
		Map<String, Object> matchProperties = new HashMap<>();
		int value = matchFunction.match(2, "ar", matchProperties);
		assertEquals(0, value);

	}

	/**
	 * Assert Phonetics Match Value via doPhoneticsMatch method Doing phonetic match
	 * with input request and stored-request with language-name(arabic),NOT
	 * language-code(ar). If give language code, get
	 * java.lang.IllegalArgumentException: No rules found for gen, rules,
	 * language-code(ar).
	 * 
	 * @throws IdAuthenticationBusinessException
	 */

	@Test
	public void TestPhoneticsMatchValueWithLanguageName_ReturnWithMatchValue()
			throws IdAuthenticationBusinessException {
		MatchFunction matchFunction = FullAddressMatchingStrategy.PHONETICS.getMatchFunction();
		Map<String, Object> valueMap = new HashMap<>();
		valueMap.put("language", "arabic");
		int value = matchFunction.match("mos", "arabic", valueMap);
		assertEquals(20, value);
	}

	/**
	 * Assert Phonetics Match Value via doPhoneticsMatch method
	 * 
	 * @throws IdAuthenticationBusinessException
	 */
	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestPhoneticsMatchWithLanguageNameAndReqInfoAsInteger_Return_NotMatched()
			throws IdAuthenticationBusinessException {
		Map<String, Object> matchProperties = new HashMap<>();
		MatchFunction matchFunction = FullAddressMatchingStrategy.PHONETICS.getMatchFunction();
		int value = matchFunction.match(5, "arabic", matchProperties);
		assertEquals(0, value);
	}
}
