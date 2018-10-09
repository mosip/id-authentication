package io.mosip.authentication.service.impl.indauth.service.demo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class NameMatchingStrategyTest {

	@Test
	public void TestValidExactMatchingStrategytype() {
		assertEquals(NameMatchingStrategy.EXACT.getType(), MatchStrategyType.EXACT);
	}

	@Test
	public void TestInvalidExactMatchingStrategytype() {
		assertNotEquals(NameMatchingStrategy.EXACT.getType(), "PARTIAL");
	}

	@Test
	public void TestValidExactMatchingStrategyfunctionisNotNull() {
		assertNotNull(NameMatchingStrategy.EXACT.getMatchFunction());
	}

	@Test
	public void TestExactMatchingStrategyfunctionisNull() {
		MatchFunction matchFunction = NameMatchingStrategy.EXACT.getMatchFunction();
		matchFunction = null;
		assertNull(matchFunction);
	}

	@Test
	public void TestValidExactMatchingStrategyFunction() {
		MatchFunction matchFunction = NameMatchingStrategy.EXACT.getMatchFunction();
		int value = matchFunction.doMatch("dinesh karuppiah", "dinesh karuppiah");
		assertEquals(100, value);
	}

	@Test
	public void TestInvalidExactMatchingStrategyFunction() {
		MatchFunction matchFunction = NameMatchingStrategy.EXACT.getMatchFunction();
		int value = matchFunction.doMatch(2, 2);
		assertEquals(0, value);
	}

	@Test
	public void TestValidPartialMatchingStrategytype() {
		assertEquals(NameMatchingStrategy.PARTIAL.getType(), MatchStrategyType.PARTIAL);
	}

	@Test
	public void TestInvalidPartialMatchingStrategytype() {
		assertNotEquals(NameMatchingStrategy.PARTIAL.getType(), "EXACT");
	}

	@Test
	public void TestValidPartialMatchingStrategyfunctionisNotNull() {
		assertNotNull(NameMatchingStrategy.PARTIAL.getMatchFunction());
	}

	@Test
	public void TestPartialMatchingStrategyfunctionisNull() {
		MatchFunction matchFunction = NameMatchingStrategy.PARTIAL.getMatchFunction();
		matchFunction = null;
		assertNull(matchFunction);
	}

	@Test
	public void TestValidPartialMatchingStrategyFunction() {
		MatchFunction matchFunction = NameMatchingStrategy.PARTIAL.getMatchFunction();
		int value = matchFunction.doMatch("dinesh thiagarajan", "dinesh karuppiah");
		assertEquals(33, value);
	}

	@Test
	public void TestInvalidPartialMatchingStrategyFunction() {
		MatchFunction matchFunction = NameMatchingStrategy.PARTIAL.getMatchFunction();
		int value = matchFunction.doMatch(2, 2);
		assertEquals(0, value);
	}

}
