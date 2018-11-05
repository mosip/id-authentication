package io.mosip.authentication.service.impl.indauth.service.demo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.function.ToIntBiFunction;

import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import io.mosip.authentication.core.dto.indauth.IdentityValue;

/**
 * 
 * @author Dinesh Karuppiah
 */
public class AddressMatchingStrategyTest {

	private IdentityValue identityValue = new IdentityValue();

	/**
	 * Check for Exact type matched with Enum value of Name Matching Strategy
	 */
	@Test
	public void TestValidExactMatchingStrategytype() {
		assertEquals(AddressMatchingStrategy.EXACT.getType(), MatchingStrategyType.EXACT);
	}

	/**
	 * Check for Exact type not matched with Enum value of Name Matching Strategy
	 */
	@Test
	public void TestInvalidExactMatchingStrategytype() {
		assertNotEquals(AddressMatchingStrategy.EXACT.getType(), "PARTIAL");
	}

	/**
	 * Assert the Name Matching Strategy for Exact is Not null
	 */
	@Test
	public void TestValidExactMatchingStrategyfunctionisNotNull() {
		assertNotNull(AddressMatchingStrategy.EXACT.getMatchFunction());
	}

	/**
	 * Assert the Name Matching Strategy for Exact is null
	 */
	@Test
	public void TestExactMatchingStrategyfunctionisNull() {
		ToIntBiFunction<Object, IdentityValue> matchFunction = AddressMatchingStrategy.EXACT.getMatchFunction();
		matchFunction = null;
		assertNull(matchFunction);
	}

	/**
	 * Tests doMatch function on Matching Strategy Function
	 */
	@Test
	public void TestValidExactMatchingStrategyFunction() {
		ToIntBiFunction<Object, IdentityValue> matchFunction = AddressMatchingStrategy.EXACT.getMatchFunction();
		identityValue.setValue("no 1 second street chennai");
		int value = matchFunction.applyAsInt("no 1 second street chennai", identityValue);
		assertEquals(100, value);
	}

	/**
	 * 
	 * Tests the Match function with in-valid values
	 */
	@Test
	public void TestInvalidExactMatchingStrategyFunction() {

		ToIntBiFunction<Object, IdentityValue> matchFunction = AddressMatchingStrategy.EXACT.getMatchFunction();
		identityValue.setValue("2");
		int value = matchFunction.applyAsInt(2, identityValue);
		assertEquals(0, value);

		identityValue.setValue("no 1 second street chennai");
		int value1 = matchFunction.applyAsInt(2, identityValue);
		assertEquals(0, value1);

	}

}
