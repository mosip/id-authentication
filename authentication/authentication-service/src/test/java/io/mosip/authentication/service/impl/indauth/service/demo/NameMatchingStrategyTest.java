package io.mosip.authentication.service.impl.indauth.service.demo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.function.ToIntBiFunction;

import org.junit.Test;

import io.mosip.authentication.core.dto.indauth.IdentityValue;

/**
 * 
 * @author Dinesh Karuppiah
 */
public class NameMatchingStrategyTest {

	/**
	 * Check for Exact type matched with Enum value of Name Matching Strategy
	 */

	private IdentityValue identityValue = new IdentityValue();

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
		ToIntBiFunction<Object, IdentityValue> matchFunction = NameMatchingStrategy.EXACT.getMatchFunction();
		matchFunction = null;
		assertNull(matchFunction);
	}

	/**
	 * Tests doMatch function on Matching Strategy Function
	 */
	@Test
	public void TestValidExactMatchingStrategyFunction() {
		ToIntBiFunction<Object, IdentityValue> matchFunction = NameMatchingStrategy.EXACT.getMatchFunction();

		identityValue.setValue("dinesh karuppiah");
		int value = matchFunction.applyAsInt("dinesh karuppiah", identityValue);
		assertEquals(100, value);
	}

	/**
	 * 
	 * Tests the Match function with in-valid values
	 */
	@Test
	public void TestInvalidExactMatchingStrategyFunction() {
		ToIntBiFunction<Object, IdentityValue> matchFunction = NameMatchingStrategy.EXACT.getMatchFunction();
		identityValue.setValue("2");
		int value = matchFunction.applyAsInt(2, identityValue);
		assertEquals(0, value);
		identityValue.setValue("dinesh");
		int value1 = matchFunction.applyAsInt(2, identityValue);
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
		ToIntBiFunction<Object, IdentityValue> matchFunction = NameMatchingStrategy.PARTIAL.getMatchFunction();
		matchFunction = null;
		assertNull(matchFunction);
	}

	/**
	 * Assert the Partial Match Function with Do-Match Method
	 */
	@Test
	public void TestValidPartialMatchingStrategyFunction() {
		ToIntBiFunction<Object, IdentityValue> matchFunction = NameMatchingStrategy.PARTIAL.getMatchFunction();
		identityValue.setValue("dinesh karuppiah");
		int value = matchFunction.applyAsInt("dinesh thiagarajan", identityValue);
		assertEquals(33, value);
	}

	/**
	 * Assert the Partial Match Function not matched via Do-Match Method
	 */
	@Test
	public void TestInvalidPartialMatchingStrategyFunction() {

		ToIntBiFunction<Object, IdentityValue> matchFunction = NameMatchingStrategy.PARTIAL.getMatchFunction();
		identityValue.setValue("2");
		int value = matchFunction.applyAsInt(2, identityValue);
		assertEquals(0, value);
		identityValue.setValue("dinesh");
		int value1 = matchFunction.applyAsInt(2, identityValue);
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
		ToIntBiFunction<Object, IdentityValue> matchFunction = NameMatchingStrategy.PHONETICS.getMatchFunction();
		identityValue.setValue("2");
		int value = matchFunction.applyAsInt(2, identityValue);
		assertEquals(0, value);

	}
	

	/**
	 * Assert Phonetics Match Value via doPhoneticsMatch method
	 */
	@Test
	public void TestPhoneticsMatchValueWithLanguageCode_Return_NotMatched() {
		ToIntBiFunction<Object, IdentityValue> matchFunction = NameMatchingStrategy.PHONETICS.getMatchFunction();
		identityValue.setValue("2");
		identityValue.setLanguage("ar");
		int value = matchFunction.applyAsInt(2, identityValue);
		assertEquals(0, value);

	}

	/**
	 * Assert Phonetics Match Value via doPhoneticsMatch method
	 */
	@Test
	public void TestPhoneticsMatchValueWithLanguageName_ReturnWithMatchValue() {
		ToIntBiFunction<Object, IdentityValue> matchFunction = NameMatchingStrategy.PHONETICS.getMatchFunction();
		identityValue.setValue("misop*");
		identityValue.setLanguage("arabic");
		int value = matchFunction.applyAsInt("mos", identityValue);
		assertEquals(20, value);
	}
	
	/**
	 * Assert Phonetics Match Value via doPhoneticsMatch method
	 */
	@Test
	public void TestPhoneticsMatchWithLanguageNameAndReqInfoAsInteger_Return_NotMatched() {
		ToIntBiFunction<Object, IdentityValue> matchFunction = NameMatchingStrategy.PHONETICS.getMatchFunction();
		identityValue.setValue("misop*");
		identityValue.setLanguage("arabic");
		int value = matchFunction.applyAsInt(5, identityValue);
		assertEquals(0, value);
	}
}
