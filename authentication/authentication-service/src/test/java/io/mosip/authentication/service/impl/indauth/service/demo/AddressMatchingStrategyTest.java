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
public class AddressMatchingStrategyTest {

	/**
	 * Check for Exact type matched with Enum value of Name Matching Strategy
	 *//*
	@Test
	public void TestValidExactMatchingStrategytype() {
		assertEquals(AddressMatchingStrategy.EXACT.getType(), MatchingStrategyType.EXACT);
	}

	*//**
	 * Check for Exact type not matched with Enum value of Name Matching Strategy
	 *//*
	@Test
	public void TestInvalidExactMatchingStrategytype() {
		assertNotEquals(AddressMatchingStrategy.EXACT.getType(), "PARTIAL");
	}

	*//**
	 * Assert the Name Matching Strategy for Exact is Not null
	 *//*
	@Test
	public void TestValidExactMatchingStrategyfunctionisNotNull() {
		assertNotNull(AddressMatchingStrategy.EXACT.getMatchFunction());
	}

	*//**
	 * Assert the Name Matching Strategy for Exact is null
	 *//*
	@Test
	public void TestExactMatchingStrategyfunctionisNull() {
		ToIntBiFunction<Object, IdentityValue> matchFunction = AddressMatchingStrategy.EXACT.getMatchFunction();
		matchFunction = null;
		assertNull(matchFunction);
	}

	*//**
	 * Tests doMatch function on Matching Strategy Function
	 *//*
	@Test
	public void TestValidExactMatchingStrategyFunction() {
		ToIntBiFunction<Object, IdentityValue> matchFunction = AddressMatchingStrategy.EXACT.getMatchFunction();
		int value = matchFunction.applyAsInt("no 1 second street chennai", "no 1 second street chennai");
		assertEquals(100, value);
	}

	*//**
	 * 
	 * Tests the Match function with in-valid values
	 *//*
	@Test
	public void TestInvalidExactMatchingStrategyFunction() {
		ToIntBiFunction<Object, IdentityValue> matchFunction = AddressMatchingStrategy.EXACT.getMatchFunction();
		int value = matchFunction.applyAsInt(2, 2);
		assertEquals(0, value);

		int value1 = matchFunction.applyAsInt(2, "no 1 second street chennai");
		assertEquals(0, value1);

		int value2 = matchFunction.applyAsInt("no 1 second street chennai", 2);
		assertEquals(0, value2);
	}*/

}
