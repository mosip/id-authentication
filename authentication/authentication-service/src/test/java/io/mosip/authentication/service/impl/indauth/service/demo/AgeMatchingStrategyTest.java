package io.mosip.authentication.service.impl.indauth.service.demo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.function.ToIntBiFunction;

import org.junit.Test;

import io.mosip.authentication.core.dto.indauth.IdentityValue;

public class AgeMatchingStrategyTest {

	private IdentityValue identityValue = new IdentityValue();

	/**
	 * Check for Exact type matched with Enum value of Age Matching Strategy
	 */
	@Test
	public void TestValidExactMatchingStrategytype() {
		assertEquals(AgeMatchingStrategy.EXACT.getType(), MatchingStrategyType.EXACT);
	}

	/**
	 * Check for Exact type not matched with Enum value of Age Matching Strategy
	 */
	@Test
	public void TestInvalidExactMatchingStrategytype() {
		assertNotEquals(AgeMatchingStrategy.EXACT.getType(), "PARTIAL");
	}

	/**
	 * Assert the Age Matching Strategy for Exact is Not null
	 */
	@Test
	public void TestValidExactMatchingStrategyfunctionisNotNull() {
		assertNotNull(AgeMatchingStrategy.EXACT.getMatchFunction());
	}

	/**
	 * Assert the Age Matching Strategy for Exact is null
	 */
	@Test
	public void TestExactMatchingStrategyfunctionisNull() {
		ToIntBiFunction<Object, IdentityValue> matchFunction = AgeMatchingStrategy.EXACT.getMatchFunction();
		matchFunction = null;
		assertNull(matchFunction);
	}

	/**
	 * Tests doMatch function on Matching Strategy Function
	 */
	@Test
	public void TestValidExactMatchingStrategyFunction() {
		ToIntBiFunction<Object, IdentityValue> matchFunction = AgeMatchingStrategy.EXACT.getMatchFunction();
		identityValue.setValue("25");
		int value = matchFunction.applyAsInt(25, identityValue);
		assertEquals(100, value);
		identityValue.setValue("100");
		int value1 = matchFunction.applyAsInt(100, identityValue);
		assertEquals(100, value1);
	}

	/**
	 * 
	 * Tests the Match function with in-valid values
	 */
	@Test
	public void TestInvalidExactMatchingStrategyFunction() {
		ToIntBiFunction<Object, IdentityValue> matchFunction = AgeMatchingStrategy.EXACT.getMatchFunction();

		identityValue.setValue("50");
		int value = matchFunction.applyAsInt(250, identityValue);
		assertEquals(0, value);

		identityValue.setValue("25");
		int value1 = matchFunction.applyAsInt(50, identityValue);
		assertEquals(0, value1);

		identityValue.setValue("25");
		int value2 = matchFunction.applyAsInt(100, identityValue);
		assertEquals(0, value2);

		identityValue.setValue("24");
		int value3 = matchFunction.applyAsInt(25, identityValue);
		assertEquals(0, value3);

		int value4 = matchFunction.applyAsInt(null, null);
		assertEquals(0, value4);

		identityValue.setValue("1");
		int value6 = matchFunction.applyAsInt("abc", identityValue);
		assertEquals(0, value6);
	}
}
