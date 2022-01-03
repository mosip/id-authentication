package io.mosip.authentication.common.service.impl.match;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.WebApplicationContext;

import io.mosip.authentication.common.service.util.EnvUtil;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.spi.indauth.match.MasterDataFetcher;
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
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class})
@Import(EnvUtil.class)
public class NameMatchingStrategyTest {

	@Autowired
	private EnvUtil env;

	@Mock
	DemoNormalizer demoNormalizer;
	
	@Mock
	private DemoMatcherUtil demoMatcherUtil;
	
	Map<String, Object> matchProperties = new HashMap<>();
	
	@Before
	public void before() {				
		matchProperties.put("demoMatcherUtil", demoMatcherUtil);
	}

	/**
	 * Check for Exact type matched with Enum value of Name Matching Strategy
	 */

	@Test
	public void TestValidExactMatchingStrategytype() {
		assertEquals(NameMatchingStrategy.EXACT.getType(), MatchingStrategyType.EXACT);
	}

	/**
	 * Check for Exact type not matched with Enum value of Name Matching Strategy
	 */
	@Test
	public void TestInvalidExactMatchingStrategytype() {
		assertNotEquals(NameMatchingStrategy.EXACT.getType(), "PARTIAL");
	}

	/**
	 * Assert the Name Matching Strategy for Exact is Not null
	 */
	@Test
	public void TestValidExactMatchingStrategyfunctionisNotNull() {
		assertNotNull(NameMatchingStrategy.EXACT.getMatchFunction());
	}

	/**
	 * Assert the Name Matching Strategy for Exact is null
	 */
	@Test
	public void TestExactMatchingStrategyfunctionisNull() {
		MatchFunction matchFunction = NameMatchingStrategy.EXACT.getMatchFunction();
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
		MatchFunction matchFunction = NameMatchingStrategy.EXACT.getMatchFunction();
		Mockito.when(demoMatcherUtil.doExactMatch(Mockito.anyString(), Mockito.anyString())).thenReturn(100);
		Mockito.when(demoNormalizer.normalizeName(Mockito.anyString(), Mockito.anyString(), Mockito.any()))
				.thenReturn("dinesh karuppiah");
		int value = matchFunction.match("dinesh karuppiah", "dinesh karuppiah", getFetcher());
		assertEquals(100, value);
	}

	private Map<String, Object> getFetcher() {
		HashMap<String, Object> valuemap = new HashMap<>();
		valuemap.put("demoNormalizer", demoNormalizer);
		valuemap.put("language", "english");
		valuemap.put("env", env);
		valuemap.put("demoMatcherUtil", demoMatcherUtil);
		MasterDataFetcher f = () -> createFetcher();
		valuemap.put("titlesFetcher", f);
		valuemap.put("langCode", "fra");
		return valuemap;
	}

	private Map<String, List<String>> createFetcher() {
		List<String> l = new ArrayList<>();
		l.add("Mr");
		l.add("Dr");
		l.add("Mrs");
		Map<String, List<String>> map = new HashMap<>();
		map.put("fra", l);
		return map;
	}

	/**
	 * 
	 * Tests the Match function with in-valid values
	 * 
	 * @throws IdAuthenticationBusinessException
	 */
	@Test
	public void TestInvalidExactMatchingStrategyFunction() throws IdAuthenticationBusinessException {
		MatchFunction matchFunction = NameMatchingStrategy.EXACT.getMatchFunction();		
		Mockito.when(demoMatcherUtil.doExactMatch(Mockito.anyString(), Mockito.anyString())).thenReturn(0);
		int value = matchFunction.match(2, "2", matchProperties);
		assertEquals(0, value);

		int value2 = matchFunction.match("2", 2, matchProperties);
		assertEquals(0, value2);

		int value1 = matchFunction.match(2, "dinesh", matchProperties);
		assertEquals(0, value1);
	}

	/**
	 * Assert Partial type matched with Enum value of Name Matching Strategy
	 */
	@Test
	public void TestValidPartialMatchingStrategytype() {
		assertEquals(NameMatchingStrategy.PARTIAL.getType(), MatchingStrategyType.PARTIAL);
	}

	/**
	 * Assert Partial type not-matched with Enum value of Name Matching Strategy
	 */
	@Test
	public void TestInvalidPartialMatchingStrategytype() {
		assertNotEquals(NameMatchingStrategy.PARTIAL.getType(), "EXACT");
	}

	/**
	 * Assert Partial Match function is not null
	 */
	@Test
	public void TestValidPartialMatchingStrategyfunctionisNotNull() {
		assertNotNull(NameMatchingStrategy.PARTIAL.getMatchFunction());
	}

	/**
	 * Assert Partial Match function is null
	 */
	@Test
	public void TestPartialMatchingStrategyfunctionisNull() {
		MatchFunction matchFunction = NameMatchingStrategy.PARTIAL.getMatchFunction();
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
		MatchFunction matchFunction = NameMatchingStrategy.PARTIAL.getMatchFunction();
		Mockito.when(demoNormalizer.normalizeName(Mockito.anyString(), Mockito.anyString(), Mockito.any()))
		.thenReturn("dinesh karuppiah");
		Mockito.when(demoMatcherUtil.doPartialMatch(Mockito.anyString(), Mockito.anyString())).thenReturn(100);
		int value = matchFunction.match("dinesh thiagarajan", "dinesh karuppiah", getFetcher());
		assertEquals(100, value);
	}

	/**
	 * Assert the Partial Match Function not matched via Do-Match Method
	 * 
	 * @throws IdAuthenticationBusinessException
	 */
	@Test
	public void TestInvalidPartialMatchingStrategyFunction() throws IdAuthenticationBusinessException {
		
		MatchFunction matchFunction = NameMatchingStrategy.PARTIAL.getMatchFunction();
		Mockito.when(demoMatcherUtil.doPartialMatch(Mockito.anyString(), Mockito.anyString())).thenReturn(0);
		int value = matchFunction.match(2, "2", matchProperties);
		assertEquals(0, value);

		int value1 = matchFunction.match("2", 2, matchProperties);
		assertEquals(0, value1);

	}

	@Test
	public void TestInvalidPartialMatchingStrategyFunction1() throws IdAuthenticationBusinessException {
		
		MatchFunction matchFunction = NameMatchingStrategy.PARTIAL.getMatchFunction();
		Mockito.when(demoMatcherUtil.doPartialMatch(Mockito.anyString(), Mockito.anyString())).thenReturn(0);
		int value = matchFunction.match(2, "2", matchProperties);
		assertEquals(0, value);

	}

	@Test
	public void TestInvalidPartialMatchwithSecondaryLang() throws IdAuthenticationBusinessException {
		MatchFunction matchFunction = NameMatchingStrategy.PARTIAL.getMatchFunction();
		Mockito.when(demoMatcherUtil.doPartialMatch(Mockito.anyString(), Mockito.anyString())).thenReturn(0);
		
		int value1 = matchFunction.match(2, "dinesh", matchProperties);
		assertEquals(0, value1);
	}

	@Test
	public void TestInvalidPartialMatch() throws IdAuthenticationBusinessException {		
		MatchFunction matchFunction = NameMatchingStrategy.PARTIAL.getMatchFunction();
		Mockito.when(demoMatcherUtil.doPartialMatch(Mockito.anyString(), Mockito.anyString())).thenReturn(0);
		matchProperties.put("languageType", "test");
		int value2 = matchFunction.match(2, "invalid", matchProperties);
		assertEquals(0, value2);
	}

	@Test
	public void TestInvalidExactMatchingStrategyFunctions() throws IdAuthenticationBusinessException {		
		
		Mockito.when(demoMatcherUtil.doExactMatch(Mockito.anyString(), Mockito.anyString())).thenReturn(0);
		MatchFunction matchFunction = NameMatchingStrategy.EXACT.getMatchFunction();

		int value = matchFunction.match(2, "2", matchProperties);
		assertEquals(0, value);

	}

	@Test
	public void TestInvalidMatchwithSecondaryLang() throws IdAuthenticationBusinessException {		
		MatchFunction matchFunction = NameMatchingStrategy.EXACT.getMatchFunction();		
		
		Mockito.when(demoMatcherUtil.doExactMatch(Mockito.anyString(), Mockito.anyString())).thenReturn(0);
		int value1 = matchFunction.match(2, "dinesh", matchProperties);
		assertEquals(0, value1);
	}

	@Test
	public void TestInvalid() throws IdAuthenticationBusinessException {
		MatchFunction matchFunction = NameMatchingStrategy.EXACT.getMatchFunction();
		Mockito.when(demoMatcherUtil.doExactMatch(Mockito.anyString(), Mockito.anyString())).thenReturn(0);
		matchProperties.put("languageType", "test");
		int value2 = matchFunction.match(2, "invalid", matchProperties);
		assertEquals(0, value2);
	}

	@Test
	public void TestInvalidPhoneticsMatchingStrategyFunctions() throws IdAuthenticationBusinessException {
		
		MatchFunction matchFunction = NameMatchingStrategy.PHONETICS.getMatchFunction();
		Mockito.when(demoMatcherUtil.doExactMatch(Mockito.anyString(), Mockito.anyString())).thenReturn(0);
		int value = matchFunction.match(2, "2", matchProperties);
		assertEquals(0, value);

		int value1 = matchFunction.match("2", 2, matchProperties);
		assertEquals(0, value1);

	}

	@Test
	public void TestInvalidPhoneticsMatchwithSecondaryLang() throws IdAuthenticationBusinessException {
		Map<String, Object> matchProperties = new HashMap<>();
		MatchFunction matchFunction = NameMatchingStrategy.PHONETICS.getMatchFunction();
		
		int value1 = matchFunction.match(2, "dinesh", matchProperties);
		assertEquals(0, value1);
	}

	@Test
	public void TestInvalidPhonetics() throws IdAuthenticationBusinessException {
		Map<String, Object> matchProperties = new HashMap<>();
		MatchFunction matchFunction = NameMatchingStrategy.PHONETICS.getMatchFunction();
		matchProperties.put("languageType", "test");
		int value2 = matchFunction.match(2, "invalid", matchProperties);
		assertEquals(0, value2);
	}

	/**
	 * Assert Phonetics Match Value is not-null on Name Matching Strategy
	 */
	@Test
	public void TestPhoneticsMatch() {
		assertNotNull(NameMatchingStrategy.PHONETICS);
	}

	/**
	 * Assert Phonetics Match Type is equals with Matching Strategy type
	 */
	@Test
	public void TestPhoneticsMatchStrategyType() {
		assertEquals(NameMatchingStrategy.PHONETICS.getType(), MatchingStrategyType.PHONETICS);
	}

	/**
	 * Assert Phonetics Match Value via doPhoneticsMatch method
	 * 
	 * @throws IdAuthenticationBusinessException
	 */
	@Test
	public void TestPhoneticsMatchValueWithoutLanguageName_Return_NotMatched()
			throws IdAuthenticationBusinessException {
		Map<String, Object> matchProperties = new HashMap<>();
		MatchFunction matchFunction = NameMatchingStrategy.PHONETICS.getMatchFunction();
		int value = matchFunction.match(2, "2", matchProperties);
		assertEquals(0, value);

	}

	/**
	 * Assert Phonetics Match Value via doPhoneticsMatch method
	 * 
	 * @throws IdAuthenticationBusinessException
	 */

	@Test
	public void TestPhoneticsMatchValueWithLanguageCode_Return_NotMatched() throws IdAuthenticationBusinessException {
		Map<String, Object> matchProperties = new HashMap<>();
		MatchFunction matchFunction = NameMatchingStrategy.PHONETICS.getMatchFunction();
		int value = matchFunction.match(2, "ara", matchProperties);
		assertEquals(0, value);

	}

	/**
	 * Assert Phonetics Match Value via doPhoneticsMatch method
	 * 
	 * @throws IdAuthenticationBusinessException
	 */

	@Test
	public void TestPhoneticsMatchValueWithLanguageName_ReturnWithMatchValue()
			throws IdAuthenticationBusinessException {
		MatchFunction matchFunction = NameMatchingStrategy.PHONETICS.getMatchFunction();
		Map<String, Object> valueMap = new HashMap<>();
		valueMap.put("language", "arabic");
		Mockito.when(demoMatcherUtil.doPhoneticsMatch(Mockito.anyString(), Mockito.anyString(),Mockito.anyString())).thenReturn(100);
		Mockito.when(demoNormalizer.normalizeName(Mockito.anyString(), Mockito.anyString(), Mockito.any()))
		.thenReturn("dinesh karuppiah");
		int value = matchFunction.match("mos", "arabic", getFetcher());
		assertEquals(100, value);
	}

	/**
	 * Assert Phonetics Match Value via doPhoneticsMatch method
	 * 
	 * @throws IdAuthenticationBusinessException
	 */
	@Test
	public void TestPhoneticsMatchWithLanguageNameAndReqInfoAsInteger_Return_NotMatched()
			throws IdAuthenticationBusinessException {
		MatchFunction matchFunction = NameMatchingStrategy.PHONETICS.getMatchFunction();
		Mockito.when(demoMatcherUtil.doPhoneticsMatch(Mockito.anyString(), Mockito.anyString(),Mockito.anyString())).thenReturn(0);
		int value = matchFunction.match(5, "arabic", matchProperties);
		assertEquals(0, value);
	}

	@Test
	public void TestInvalidExactMatchingStrategy() throws IdAuthenticationBusinessException {
		MatchFunction matchFunction = NameMatchingStrategy.EXACT.getMatchFunction();
		Mockito.when(demoMatcherUtil.doExactMatch(Mockito.anyString(), Mockito.anyString())).thenReturn(0);
		int value = matchFunction.match(5, 1, matchProperties);
		assertEquals(0, value);
	}

}
