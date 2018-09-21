package org.mosip.auth.core.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@Ignore //FIXME check the test failure
@RunWith(MockitoJUnitRunner.class)
public class TextMatcherUtilTest {

	TextMatcherUtil util;

	@Test
	public void testConstrcutor() {
	}

	@Test
	public void testExactMatchFail() {
		String testInputString = "Geetha";
		String testStoredString = "githa";
		assertFalse(TextMatcherUtil.exactMatch(testInputString, testStoredString));
	}

	@Test
	public void testPartialMatchFail() {
		String testInputString = "abc";
		String testStoredString = "def";
		assertFalse(TextMatcherUtil.partialMatch(testInputString, testStoredString));
	}

	@Test
	public void testValidateThreshold() {
		String testInputString = "Édouard Philippe";
		String testStoredString = "Edward Philip";
		assertFalse(TextMatcherUtil.phoneticMatch(testInputString, testStoredString, 800, "french"));
	}

	@Test
	public void testValidateThresholdProbability() {
		String testInputString = "Édouard Philippe";
		String testStoredString = "Eeeee";
		assertFalse(TextMatcherUtil.phoneticMatch(testInputString, testStoredString, 100, "french"));
	}

	@Test
	public void exactMatchTest() {
		String testInputString = "Geetha";
		String testStoredString = "geetha";
		assertTrue(TextMatcherUtil.exactMatch(testInputString, testStoredString));
	}

	@Test
	public void partialMatchTest() {
		String testInputString = "jeevan";
		String testStoredString = "jeev";
		assertTrue(TextMatcherUtil.partialMatch(testInputString, testStoredString));
	}

	@Test
	public void phoneticMatchFrenchWithConfiguredThresholdTest() {
		String testInputString = "Édouard Philippe";
		String testStoredString = "Edward Philip";
		assertTrue(TextMatcherUtil.phoneticMatch(testInputString, testStoredString, null, "french"));
	}

	@Test
	public void phoneticMatchArabicWithConfiguredThresholdTest() {
		String testInputString = "سلمان بن عبد العزیز آل سعود";
		String testStoredString = "سلمان بن عبد الع آل سعود";
		assertTrue(TextMatcherUtil.phoneticMatch(testInputString, testStoredString, null, "arabic"));
	}

	@Test
	public void phoneticMatchFrenchWithParameterizedThresholdTest() {
		String testInputString = "Édouard Philippe";
		String testStoredString = "Edward Philip";
		assertTrue(TextMatcherUtil.phoneticMatch(testInputString, testStoredString, 80, "french"));
	}

	@Test
	public void phoneticMatchArabicWithParameterizedThresholdTest() {
		String testInputString = "سلمان بن عبد العزیز آل سعود";
		String testStoredString = "سلمان بن عبد الع آل سعود";
		assertTrue(TextMatcherUtil.phoneticMatch(testInputString, testStoredString, 80, "arabic"));
	}
}
