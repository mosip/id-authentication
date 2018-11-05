package io.mosip.authentication.service.impl.indauth.service.demo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.ToIntBiFunction;

import org.junit.Before;
import org.junit.Test;

import io.mosip.authentication.core.dto.indauth.IdentityValue;

public class DOBMatchingStrategyTest {
	SimpleDateFormat sdf = null;
	private IdentityValue identityValue = new IdentityValue();

	@Before
	public void setup() {
		sdf = new SimpleDateFormat("yyyy-MM-dd");
	}

	/**
	 * Check for Exact type matched with Enum value of DOB Matching Strategy
	 */
	@Test
	public void TestValidExactMatchingStrategytype() {
		assertEquals(DOBMatchingStrategy.EXACT.getType(), MatchingStrategyType.EXACT);
	}

	/**
	 * Check for Exact type not matched with Enum value of DOB Matching Strategy
	 */
	@Test
	public void TestInvalidExactMatchingStrategytype() {
		assertNotEquals(DOBMatchingStrategy.EXACT.getType(), "PARTIAL");
	}

	/**
	 * Assert the DOB Matching Strategy for Exact is Not null
	 */
	@Test
	public void TestValidExactMatchingStrategyfunctionisNotNull() {
		assertNotNull(DOBMatchingStrategy.EXACT.getMatchFunction());
	}

	/**
	 * Assert the DOB Matching Strategy for Exact is null
	 */
	@Test
	public void TestExactMatchingStrategyfunctionisNull() {
		ToIntBiFunction<Object, IdentityValue> matchFunction = DOBMatchingStrategy.EXACT.getMatchFunction();
		matchFunction = null;
		assertNull(matchFunction);
	}

	/**
	 * Tests doMatch function on Matching Strategy Function
	 */
	@Test
	public void TestValidExactMatchingStrategyFunction() {
		ToIntBiFunction<Object, IdentityValue> matchFunction = DOBMatchingStrategy.EXACT.getMatchFunction();
		int value = -1;

		identityValue.setValue("1993-02-07");
		value = matchFunction.applyAsInt("1993-02-07", identityValue);

		assertEquals(100, value);
	}

	/**
	 * 
	 * Tests the Match function with in-valid values
	 */
	@Test
	public void TestInvalidExactMatchingStrategyFunction() {
		ToIntBiFunction<Object, IdentityValue> matchFunction = DOBMatchingStrategy.EXACT.getMatchFunction();

		identityValue.setValue("1993-02-27");
		int value = matchFunction.applyAsInt("1993-02-07", identityValue);
		assertEquals(0, value);

		identityValue.setValue("1993-02-07");
		int value1 = matchFunction.applyAsInt(2, identityValue);
		assertEquals(0, value1);

		int value3 = matchFunction.applyAsInt(null, null);
		assertEquals(0, value3);

	}

}
