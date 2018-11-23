package io.mosip.authentication.service.impl.indauth.service.demo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.function.ToIntBiFunction;

import org.junit.Test;

import io.mosip.authentication.core.dto.indauth.IdentityValue;

public class DOBTypeMatchingStrategyTest {
	
	private IdentityValue identityValue = new IdentityValue();
	
	/**
	 * Check for Exact type matched with Enum value of DOB Type Matching Strategy
	 */
	@Test
	public void TestValidExactMatchingStrategytype() {
		assertEquals(DOBTypeMatchingStrategy.EXACT.getType(), MatchingStrategyType.EXACT);
	}

	/**
	 * Check for Exact type not matched with Enum value of DOB Type Matching Strategy
	 */
	@Test
	public void TestInvalidExactMatchingStrategytype() {
		assertNotEquals(DOBTypeMatchingStrategy.EXACT.getType(), "PARTIAL");
	}

	/**
	 * Assert the DOB Type Matching Strategy for Exact is Not null
	 */
	@Test
	public void TestValidExactMatchingStrategyfunctionisNotNull() {
		assertNotNull(DOBTypeMatchingStrategy.EXACT.getMatchFunction());
	}

	/**
	 * Assert the DOB Type Matching Strategy for Exact is null
	 */
	@Test
	public void TestExactMatchingStrategyfunctionisNull() {
		ToIntBiFunction<Object, IdentityValue> matchFunction = DOBTypeMatchingStrategy.EXACT.getMatchFunction();
		matchFunction = null;
		assertNull(matchFunction);
	}
	
	/**
	 * Tests doMatch function on Matching Strategy Function
	 */
	@Test
	public void TestValidExactMatchingStrategyFunction() {
		ToIntBiFunction<Object, IdentityValue> matchFunction = DOBTypeMatchingStrategy.EXACT.getMatchFunction();
		int value = -1;

		identityValue.setValue("V");
		value = matchFunction.applyAsInt("V", identityValue);

		assertEquals(100, value);
	}
	
	/**
	 * 
	 * Tests the Match function with in-valid values
	 */
	@Test
	public void TestInvalidExactMatchingStrategyFunction() {
		ToIntBiFunction<Object, IdentityValue> matchFunction = DOBTypeMatchingStrategy.EXACT.getMatchFunction();

		identityValue.setValue("V");
		int value = matchFunction.applyAsInt(332, identityValue);
		assertEquals(0, value);

		identityValue.setValue("V");
		int value1 = matchFunction.applyAsInt("A", identityValue);
		assertEquals(0, value1);

		int value3 = matchFunction.applyAsInt(null, null);
		assertEquals(0, value3);

	}

}
