package io.mosip.authentication.service.impl.indauth.service.demo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.function.ToIntBiFunction;

import org.junit.Test;

import io.mosip.authentication.core.dto.indauth.IdentityValue;

public class EmailMatchingStrategyTest {

	private IdentityValue identityValue = new IdentityValue();

	/**
	 * Check for Exact type matched with Enum value of EMAIL Matching Strategy
	 */
	@Test
	public void TestValidExactMatchingStrategytype() {
		assertEquals(EmailMatchingStrategy.EXACT.getType(), MatchingStrategyType.EXACT);
	}

	/**
	 * Check for Exact type not matched with Enum value of EMAIL Matching Strategy
	 */
	@Test
	public void TestInvalidExactMatchingStrategytype() {
		assertNotEquals(EmailMatchingStrategy.EXACT.getType(), "PARTIAL");
	}

	/**
	 * Assert the EMAIL Matching Strategy for Exact is Not null
	 */
	@Test
	public void TestValidExactMatchingStrategyfunctionisNotNull() {
		assertNotNull(EmailMatchingStrategy.EXACT.getMatchFunction());
	}

	/**
	 * Assert the EMAIL Matching Strategy for Exact is null
	 */
	@Test
	public void TestExactMatchingStrategyfunctionisNull() {
		ToIntBiFunction<Object, IdentityValue> matchFunction = EmailMatchingStrategy.EXACT.getMatchFunction();
		matchFunction = null;
		assertNull(matchFunction);
	}

	/**
	 * Tests doMatch function on Matching Strategy Function
	 */
	@Test
	public void TestValidExactMatchingStrategyFunction() {
		ToIntBiFunction<Object, IdentityValue> matchFunction = EmailMatchingStrategy.EXACT.getMatchFunction();
		identityValue.setValue("abc@mail.com");
		int value = matchFunction.applyAsInt("abc@mail.com", identityValue);
		assertEquals(100, value);
	}

	/**
	 * 
	 * Tests the Match function with in-valid values
	 */
	@Test
	public void TestInvalidExactMatchingStrategyFunction() {

		ToIntBiFunction<Object, IdentityValue> matchFunction = EmailMatchingStrategy.EXACT.getMatchFunction();
		identityValue.setValue("abc@email.com");
		int value = matchFunction.applyAsInt("abc@mail.com", identityValue);
		assertEquals(0, value);

		identityValue.setValue("2");
		int value4 = matchFunction.applyAsInt(1, identityValue);
		assertEquals(0, value4);

		identityValue.setValue("abc@mail.com");
		int value5 = matchFunction.applyAsInt(1, identityValue);
		assertEquals(0, value5);

	}
}
