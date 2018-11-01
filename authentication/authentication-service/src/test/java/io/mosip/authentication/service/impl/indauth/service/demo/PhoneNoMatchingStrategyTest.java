package io.mosip.authentication.service.impl.indauth.service.demo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.function.ToIntBiFunction;

import org.junit.Test;

import io.mosip.authentication.core.dto.indauth.IdentityValue;

public class PhoneNoMatchingStrategyTest {

	private IdentityValue identityValue = new IdentityValue();

	/**
	 * Check for Exact type matched with Enum value of PhoneNo Matching Strategy
	 */
	@Test
	public void TestValidExactMatchingStrategytype() {
		assertEquals(PhoneNoMatchingStrategy.EXACT.getType(), MatchingStrategyType.EXACT);
	}

	/**
	 * Check for Exact type not matched with Enum value of PhoneNo Matching Strategy
	 */
	@Test
	public void TestInvalidExactMatchingStrategytype() {
		assertNotEquals(PhoneNoMatchingStrategy.EXACT.getType(), "PARTIAL");
	}

	/**
	 * Assert the PhoneNo Matching Strategy for Exact is Not null
	 */
	@Test
	public void TestValidExactMatchingStrategyfunctionisNotNull() {
		assertNotNull(PhoneNoMatchingStrategy.EXACT.getMatchFunction());
	}

	/**
	 * Assert the PhoneNo Matching Strategy for Exact is null
	 */
	@Test
	public void TestExactMatchingStrategyfunctionisNull() {
		ToIntBiFunction<Object, IdentityValue> matchFunction = PhoneNoMatchingStrategy.EXACT.getMatchFunction();
		matchFunction = null;
		assertNull(matchFunction);
	}

	/**
	 * Tests doMatch function on Matching Strategy Function
	 */
	@Test
	public void TestValidExactMatchingStrategyFunction() {
		ToIntBiFunction<Object, IdentityValue> matchFunction = PhoneNoMatchingStrategy.EXACT.getMatchFunction();

		identityValue.setValue("9876543210");
		int value = matchFunction.applyAsInt("9876543210", identityValue);
		assertEquals(100, value);
		identityValue.setValue("+91-9876543210");
		int value1 = matchFunction.applyAsInt("+91-9876543210", identityValue);
		assertEquals(100, value1);
		identityValue.setValue("413-3432-321");
		int value2 = matchFunction.applyAsInt("413-3432-321", identityValue);
		assertEquals(100, value2);
	}

	/**
	 * 
	 * Tests the Match function with in-valid values
	 */
	@Test
	public void TestInvalidExactMatchingStrategyFunction() {

		ToIntBiFunction<Object, IdentityValue> matchFunction = PhoneNoMatchingStrategy.EXACT.getMatchFunction();

		identityValue.setValue("1234567890");
		int value = matchFunction.applyAsInt("9789438210", identityValue);
		assertEquals(0, value);

		identityValue.setValue("783248712");
		int value1 = matchFunction.applyAsInt("76348798", identityValue);
		assertEquals(0, value1);

		identityValue.setValue("9832-129-322");
		int value2 = matchFunction.applyAsInt("789-7389-783", identityValue);
		assertEquals(0, value2);

		identityValue.setValue("1234567890");
		int value3 = matchFunction.applyAsInt("+91-1234567890", identityValue);
		assertEquals(0, value3);

		identityValue.setValue("2");
		int value4 = matchFunction.applyAsInt(1, identityValue);
		assertEquals(0, value4);

		identityValue.setValue("123434545");
		int value5 = matchFunction.applyAsInt(1, identityValue);
		assertEquals(0, value5);

	}
}
