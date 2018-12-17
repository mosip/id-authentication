package io.mosip.authentication.service.impl.indauth.service.demo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import io.mosip.authentication.core.spi.indauth.match.MatchFunction;
import io.mosip.authentication.core.spi.indauth.match.MatchingStrategyType;

/**
 * 
 * @author Dinesh Karuppiah
 */
public class NameMatchingStrategyTest {

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
	 */
	@Test
	public void TestValidExactMatchingStrategyFunction() {
		MatchFunction matchFunction = NameMatchingStrategy.EXACT.getMatchFunction();

		int value = matchFunction.match("dinesh karuppiah", "dinesh karuppiah", null);
		assertEquals(100, value);
	}

	/**
	 * 
	 * Tests the Match function with in-valid values
	 */
	@Test
	public void TestInvalidExactMatchingStrategyFunction() {
		MatchFunction matchFunction = NameMatchingStrategy.EXACT.getMatchFunction();

		int value = matchFunction.match(2, "2", null);
		assertEquals(0, value);

		int value1 = matchFunction.match(2, "dinesh", null);
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
	 */
	@Test
	public void TestValidPartialMatchingStrategyFunction() {
		MatchFunction matchFunction = NameMatchingStrategy.PARTIAL.getMatchFunction();
		int value = matchFunction.match("dinesh thiagarajan", "dinesh karuppiah", null);
		assertEquals(33, value);
	}

	/**
	 * Assert the Partial Match Function not matched via Do-Match Method
	 */
	@Test
	public void TestInvalidPartialMatchingStrategyFunction() {

		MatchFunction matchFunction = NameMatchingStrategy.PARTIAL.getMatchFunction();

		int value = matchFunction.match(2, "2", null);
		assertEquals(0, value);

		int value1 = matchFunction.match(2, "dinesh", null);
		assertEquals(0, value1);
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
	 */
	@Test
	public void TestPhoneticsMatchValueWithoutLanguageName_Return_NotMatched() {
		MatchFunction matchFunction = NameMatchingStrategy.PHONETICS.getMatchFunction();
		int value = matchFunction.match(2, "2", null);
		assertEquals(0, value);

	}

	/**
	 * Assert Phonetics Match Value via doPhoneticsMatch method
	 */

	@Test
	public void TestPhoneticsMatchValueWithLanguageCode_Return_NotMatched() {

		MatchFunction matchFunction = NameMatchingStrategy.PHONETICS.getMatchFunction();
		int value = matchFunction.match(2, "ar", null);
		assertEquals(0, value);

	}

	/**
	 * Assert Phonetics Match Value via doPhoneticsMatch method
	 */

	@Test
	public void TestPhoneticsMatchValueWithLanguageName_ReturnWithMatchValue() {
		MatchFunction matchFunction = NameMatchingStrategy.PHONETICS.getMatchFunction();
		Map<String, Object> valueMap = new HashMap<>();
		valueMap.put("language", "arabic");
		int value = matchFunction.match("mos", "arabic", valueMap);
		assertEquals(20, value);
	}

	/**
	 * Assert Phonetics Match Value via doPhoneticsMatch method
	 */
	@Test
	public void TestPhoneticsMatchWithLanguageNameAndReqInfoAsInteger_Return_NotMatched() {
		MatchFunction matchFunction = NameMatchingStrategy.PHONETICS.getMatchFunction();
		int value = matchFunction.match(5, "arabic", null);
		assertEquals(0, value);
	}
}
