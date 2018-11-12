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
		ToIntBiFunction<Object, IdentityValue> matchFunction = FullAddressMatchingStrategy.EXACT.getMatchFunction();
		matchFunction = null;
		assertNull(matchFunction);
	}

	/**
	 * Tests doMatch function on Matching Strategy Function
	 */
	@Test
	public void TestValidExactMatchingStrategyFunction() {
		ToIntBiFunction<Object, IdentityValue> matchFunction = FullAddressMatchingStrategy.EXACT.getMatchFunction();
		IdentityValue identityValue = new IdentityValue();
		identityValue.setValue("a k Chowdary Beach view colony apt 12 main st TamilNadu 560055");
		int value = matchFunction.applyAsInt("a k Chowdary Beach view colony apt 12 main st TamilNadu 560055",
				identityValue);
		assertEquals(100, value);
	}

	/**
	 * 
	 * Tests the Match function with in-valid values
	 */
	@Test
	public void TestInvalidExactMatchingStrategyFunction() {
		ToIntBiFunction<Object, IdentityValue> matchFunction = FullAddressMatchingStrategy.EXACT.getMatchFunction();
		IdentityValue identityValue = new IdentityValue();
		identityValue.setValue("2");
		int value = matchFunction.applyAsInt(2, identityValue);
		assertEquals(0, value);
		identityValue = new IdentityValue();
		identityValue.setValue("no 1 second street chennai");
		int value1 = matchFunction.applyAsInt(2, identityValue);
		assertEquals(0, value1);
		identityValue = new IdentityValue();
		identityValue.setValue("no 1 second street chennai");

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
		ToIntBiFunction<Object, IdentityValue> matchFunction = FullAddressMatchingStrategy.PARTIAL.getMatchFunction();
		matchFunction = null;
		assertNull(matchFunction);
	}

	/**
	 * Assert the Partial Match Function with Do-Match Method
	 */
	@Test
	public void TestValidPartialMatchingStrategyFunction() {
		ToIntBiFunction<Object, IdentityValue> matchFunction = FullAddressMatchingStrategy.PARTIAL.getMatchFunction();
		IdentityValue identityValue = new IdentityValue();
		identityValue.setValue("no IdentityValue1 second street chennai");
		int value = matchFunction.applyAsInt("street chennai", identityValue);
		assertEquals(50, value);
	}

	/**
	 * Assert the Partial Match Function not matched via Do-Match Method
	 */
	@Test
	public void TestInvalidPartialMatchingStrategyFunction() {
		ToIntBiFunction<Object, IdentityValue> matchFunction = FullAddressMatchingStrategy.PARTIAL.getMatchFunction();
		IdentityValue identityValue = new IdentityValue();
		identityValue.setValue("2");
		int value = matchFunction.applyAsInt(2, identityValue);
		assertEquals(0, value);

		int value1 = matchFunction.applyAsInt(2, identityValue);
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
	 */
	@Test
	public void TestPhoneticsMatchValue() {
		ToIntBiFunction<Object, IdentityValue> matchFunction = FullAddressMatchingStrategy.PHONETICS.getMatchFunction();
		IdentityValue identityValue = new IdentityValue();
		identityValue.setValue("2");
		int value = matchFunction.applyAsInt(2, identityValue);
		assertEquals(0, value);
	}
	

	/**
	 * Assert Phonetics Match Value via doPhoneticsMatch method
	 */
	@Test
	public void TestPhoneticsMatchValueWithLanguageCode_Return_NotMatched() {
		
		ToIntBiFunction<Object, IdentityValue> matchFunction = FullAddressMatchingStrategy.PHONETICS.getMatchFunction();
		IdentityValue identityValue = new IdentityValue();
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
		ToIntBiFunction<Object, IdentityValue> matchFunction = FullAddressMatchingStrategy.PHONETICS.getMatchFunction();
		IdentityValue identityValue = new IdentityValue();
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
		ToIntBiFunction<Object, IdentityValue> matchFunction = FullAddressMatchingStrategy.PHONETICS.getMatchFunction();
		IdentityValue identityValue = new IdentityValue();
		identityValue.setValue("misop*");
		identityValue.setLanguage("arabic");
		int value = matchFunction.applyAsInt(5, identityValue);
		assertEquals(0, value);
	}
}
