package io.mosip.authentication.service.impl.indauth.service.demo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.function.ToIntBiFunction;

import org.junit.Test;

import io.mosip.authentication.core.dto.indauth.IdentityValue;

public class GenderMatchingStrategyTest {

	private IdentityValue identityValue = new IdentityValue();

	/**
	 * Check for Exact type matched with Enum value of Gender Matching Strategy
	 */
	@Test
	public void TestValidExactMatchingStrategytype() {
		assertEquals(GenderMatchingStrategy.EXACT.getType(), MatchingStrategyType.EXACT);
	}

	/**
	 * Check for Exact type not matched with Enum value of Gender Matching Strategy
	 */
	@Test
	public void TestInvalidExactMatchingStrategytype() {
		assertNotEquals(GenderMatchingStrategy.EXACT.getType(), "PARTIAL");
	}

	/**
	 * Assert the Gender Matching Strategy for Exact is Not null
	 */
	@Test
	public void TestValidExactMatchingStrategyfunctionisNotNull() {
		assertNotNull(GenderMatchingStrategy.EXACT.getMatchFunction());
	}

	/**
	 * Assert the Gender Matching Strategy for Exact is null
	 */
	@Test
	public void TestExactMatchingStrategyfunctionisNull() {
		ToIntBiFunction<Object, IdentityValue> matchFunction = GenderMatchingStrategy.EXACT.getMatchFunction();
		matchFunction = null;
		assertNull(matchFunction);
	}

	/**
	 * Tests doMatch function on Matching Strategy Function
	 */
	@Test
	public void TestValidExactMatchingStrategyFunction() {

		ToIntBiFunction<Object, IdentityValue> matchFunction = GenderMatchingStrategy.EXACT.getMatchFunction();
		identityValue.setValue("M");
		int value = matchFunction.applyAsInt("M", identityValue);
		assertEquals(100, value);

		identityValue.setValue("F");
		int value1 = matchFunction.applyAsInt("F", identityValue);
		assertEquals(100, value1);

		identityValue.setValue("Male");
		int value2 = matchFunction.applyAsInt("Male", identityValue);
		assertEquals(100, value2);

		identityValue.setValue("Female");
		int value3 = matchFunction.applyAsInt("Female", identityValue);
		assertEquals(100, value3);
	}

	/**
	 * 
	 * Tests the Match function with in-valid values
	 */
	@Test
	public void TestInvalidExactMatchingStrategyFunction() {

		ToIntBiFunction<Object, IdentityValue> matchFunction = GenderMatchingStrategy.EXACT.getMatchFunction();

		identityValue.setValue("F");
		int value = matchFunction.applyAsInt("M", identityValue);
		assertEquals(0, value);

		identityValue.setValue("Female");
		int value1 = matchFunction.applyAsInt("F", identityValue);
		assertEquals(0, value1);

		identityValue.setValue("M");
		int value2 = matchFunction.applyAsInt("Male", identityValue);
		assertEquals(0, value2);

		identityValue.setValue("Male");
		int value3 = matchFunction.applyAsInt("Female", identityValue);
		assertEquals(0, value3);

		identityValue.setValue("2");
		int value4 = matchFunction.applyAsInt(1, identityValue);
		assertEquals(0, value4);

	}

}
