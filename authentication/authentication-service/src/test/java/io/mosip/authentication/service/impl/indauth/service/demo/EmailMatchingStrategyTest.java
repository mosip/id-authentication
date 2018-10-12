package io.mosip.authentication.service.impl.indauth.service.demo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class EmailMatchingStrategyTest {

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
		MatchFunction matchFunction = EmailMatchingStrategy.EXACT.getMatchFunction();
		matchFunction = null;
		assertNull(matchFunction);
	}

	/**
	 * Tests doMatch function on Matching Strategy Function
	 */
	@Test
	public void TestValidExactMatchingStrategyFunction() {
		MatchFunction matchFunction = EmailMatchingStrategy.EXACT.getMatchFunction();		
		int value = matchFunction.doMatch("abc@mail.com","abc@mail.com");
		assertEquals(100, value);
	}

	/**
	 * 
	 * Tests the Match function with in-valid values
	 */
	@Test
	public void TestInvalidExactMatchingStrategyFunction() {
		MatchFunction matchFunction = EmailMatchingStrategy.EXACT.getMatchFunction();
		
		int value = matchFunction.doMatch("abc@mail.com","abc@email.com");
		assertEquals(0, value);
		
		int value1 = matchFunction.doMatch("abc@email.com","abc@mail.com");
		assertEquals(0, value1);

		int value2 = matchFunction.doMatch("abc@mail.com","xyz@mail.com");
		assertEquals(0, value2);
		
		int value3 = matchFunction.doMatch("@mail.com","abc@mail.com");
		assertEquals(0, value3);
		
		int value4 = matchFunction.doMatch(1,2);
		assertEquals(0, value4);
		
		int value5 = matchFunction.doMatch(1,"abc@mail.com");
		assertEquals(0, value5);
		
		int value6 = matchFunction.doMatch("abc@mail.com",1);
		assertEquals(0, value6);
	}
}
