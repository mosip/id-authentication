package io.mosip.kernel.core.test.util;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Locale;

import org.junit.Test;

import io.mosip.kernel.core.exception.ArrayIndexOutOfBoundsException;
import io.mosip.kernel.core.exception.IllegalArgumentException;
import io.mosip.kernel.core.exception.PatternSyntaxException;
import io.mosip.kernel.core.util.StringUtils;

/**
 * Test classes for String Util
 * @author Sidhant Agarwal
 * @since 1.0.0
 *
 */
public class StringUtilTest {
	String testString = "test";
	String testMixString = "t@#ef1s%t";
	String[] testArray = { "test", "abc" };
	String[] testMixArray = { "t", "@#", "ef", "1", "s", "%", "t" };
	String joinedTestArr = "1;2;3";

	@Test
	public void capitalizeFirstLetterTest() {
		assertThat(StringUtils.capitalizeFirstLetter(testString), is("Test"));
	}

	@Test
	public void compareTest() {
		assertThat(StringUtils.compare("test", testString), is(0));
	}

	@Test
	public void compareTestWithBoolean() {
		assertThat(StringUtils.compare("test", testString, true), is(0));

	}

	@Test
	public void compareIgnoreCaseTest() {
		assertThat(StringUtils.compareIgnoreCase("TeSt", testString), is(0));
	}

	@Test
	public void compareIgnoreCaseTestWithBoolean() {
		assertThat(StringUtils.compareIgnoreCase("tEsT", testString, true), is(0));

	}

	@Test
	public void containsTest() {

		assertThat(StringUtils.contains("abctestabc", testString), is(true));

	}

	@Test
	public void containsTestWithIntSearch() {
		assertThat(StringUtils.contains(testString, 101), is(true));
	}

	@Test
	public void containsIgnoreCaseTest() {
		assertThat(StringUtils.containsIgnoreCase(testString, "es"), is(true));
	}

	@Test
	public void containsWhiteSpaceTest() {
		assertThat(StringUtils.containsWhitespace(testString), is(false));
	}

	@Test
	public void isEmptyTest() {
		assertThat(StringUtils.isEmpty(testString), is(false));
	}

	@Test
	public void isNotEmptyTest() {
		assertThat(StringUtils.isNotEmpty(testString), is(true));
	}

	@Test
	public void isBlankTest() {
		assertThat(StringUtils.isBlank(""), is(true));
	}

	@Test
	public void isNotBlankTest() {
		assertThat(StringUtils.isNotBlank(testString), is(true));
	}

	@Test
	public void trimTest() {
		assertThat(StringUtils.trim("    test   "), is(testString));
	}

	@Test
	public void truncateTest() {
		assertThat(StringUtils.truncate("test1234   ", 4), is(testString));
	}

	@Test
	public void truncateTestWithOffset() {
		assertThat(StringUtils.truncate("abctest124 ", 3, 4), is(testString));
	}

	@Test
	public void stripTest() {
		assertThat(StringUtils.strip("    test    "), is(testString));
	}

	@Test
	public void stripTestWithChar() {
		assertThat(StringUtils.strip("abtest", "ab"), is(testString));
	}

	@Test
	public void equalsTest() {
		assertThat(StringUtils.equals("test", testString), is(true));
	}

	@Test
	public void equalsIgnoreCaseTest() {
		assertThat(StringUtils.equalsIgnoreCase("Test", testString), is(true));
	}

	@Test
	public void indexOfTest() {
		assertThat(StringUtils.indexOf(testString, 'e'), is(1));
	}

	@Test
	public void indexOfTestWithStartPos() {
		assertThat(StringUtils.indexOf(testString, 't', 1), is(3));
	}

	@Test
	public void indexOfTestWithSearchString() {
		assertThat(StringUtils.indexOf(testString, "es"), is(1));
	}

	@Test
	public void indexOfTestWithSearchStringAndStartPos() {
		assertThat(StringUtils.indexOf(testString, "t", 2), is(3));
	}

	@Test
	public void lastIndexOfTest() {
		assertThat(StringUtils.lastIndexOf(testString, 115), is(2));
	}

	@Test
	public void lastIndexOfTestWithStartPos() {
		assertThat(StringUtils.lastIndexOf(testString, 116, -1), is(-1));
	}

	@Test
	public void lastIndexOfTestWithSearchString() {
		assertThat(StringUtils.lastIndexOf("testabctest", testString), is(7));
	}

	@Test
	public void lastIndexOfTestWithSearchStringAndStartPos() {
		assertThat(StringUtils.lastIndexOf("testabctestabc", testString, -1), is(-1));
	}

	@Test
	public void substringTest() {
		assertThat(StringUtils.substring(testString, 1), is("est"));
	}

	@Test
	public void substringTestWithEndPos() {
		assertThat(StringUtils.substring(testString, 1, 3), is("es"));
	}

	@Test
	public void removeLeftCharTest() {
		assertThat(StringUtils.removeLeftChar(testString, 2), is("te"));
	}

	@Test
	public void removeRightCharTest() {
		assertThat(StringUtils.removeRightChar(testString, 2), is("st"));
	}

	@Test
	public void removeMidCharTest() {
		assertThat(StringUtils.removeMidChar(testString, 1, 2), is("es"));

	}

	@Test
	public void substringBeforeTest() {
		assertThat(StringUtils.substringBefore(testString, "s"), is("te"));
	}

	@Test
	public void substringAfterTest() {
		assertThat(StringUtils.substringAfter(testString, "t"), is("est"));
	}

	@Test
	public void splitTest() {
		assertThat(StringUtils.split("test abc"), is(testArray));
	}

	@Test
	public void splitTestWithSperatorChar() {
		assertThat(StringUtils.split("testxabc", 'x'), is(testArray));
	}

	@Test
	public void splitByCharacterTypeTest() {
		assertThat(StringUtils.splitByCharacterType(testMixString), is(testMixArray));
	}

	@Test
	public void splitByCharacterTypeWithCamelCaseTest() {
		String[] testSplitArray = { "MIND", "Tree" };
		assertThat(StringUtils.splitByCharacterTypeCamelCase("MINDTree"), is(testSplitArray));
	}

	@Test
	public void joinObjectTest() {
		assertThat(StringUtils.join(testArray, ';'), is("test;abc"));
	}

	@Test
	public void joinLongArrayTest() {
		long[] testArr = { 1, 2, 3 };
		assertThat(StringUtils.join(testArr, ';'), is(joinedTestArr));

	}

	@Test
	public void joinIntArrayTest() {
		int[] testArr = { 1, 2, 3 };
		assertThat(StringUtils.join(testArr, ';'), is(joinedTestArr));
	}

	@Test
	public void joinShortArrayTest() {
		short[] testArr = { 1, 2, 3 };
		assertThat(StringUtils.join(testArr, ';'), is(joinedTestArr));
	}

	@Test
	public void joinByteArrayTest() {
		byte[] testArr = { 1, 2, 3 };
		assertThat(StringUtils.join(testArr, ';'), is(joinedTestArr));
	}

	@Test
	public void joinCharArrayTest() {
		char[] testArr = { 'a', 'b', 'c' };
		assertThat(StringUtils.join(testArr, ';'), is("a;b;c"));
	}

	@Test
	public void joinFloatArrayTest() {
		float[] testArr = { 1, 2, 3 };
		assertThat(StringUtils.join(testArr, ';'), is("1.0;2.0;3.0"));
	}

	@Test
	public void joinDoubleArrayTest() {
		double[] testArr = { 1, 2, 3 };
		assertThat(StringUtils.join(testArr, ';'), is("1.0;2.0;3.0"));
	}

	@Test
	public void joinObjectArrayTestWithIndex() {
		assertThat(StringUtils.join(testArray, ';', 0, 2), is("test;abc"));
	}

	@Test(expected = ArrayIndexOutOfBoundsException.class)
	public void joinObjectArrayTestWithArrayIndexException() {
		StringUtils.join(testArray, ';', -1, 2);
	}

	@Test
	public void joinLongArrayTestWithIndex() {
		long[] testArr = { 1, 2, 3 };
		assertThat(StringUtils.join(testArr, ';', 0, 3), is(joinedTestArr));

	}

	@Test(expected = ArrayIndexOutOfBoundsException.class)
	public void joinLongArrayTestWithArrayIndexException() {
		StringUtils.join(testArray, ';', -1, 2);
	}

	@Test
	public void joinIntArrayTestWithIndex() {
		int[] testArr = { 1, 2, 3 };
		assertThat(StringUtils.join(testArr, ';', 0, 3), is(joinedTestArr));
	}

	@Test(expected = ArrayIndexOutOfBoundsException.class)
	public void joinIntArrayTestWithArrayIndexException() {
		StringUtils.join(testArray, ';', -1, 2);
	}

	@Test
	public void joinShortArrayTestWithIndex() {
		short[] testArr = { 1, 2, 3 };
		assertThat(StringUtils.join(testArr, ';', 0, 3), is(joinedTestArr));
	}

	@Test
	public void joinByteArrayTestWithIndex() {
		byte[] testArr = { 1, 2, 3 };
		assertThat(StringUtils.join(testArr, ';', 0, 3), is(joinedTestArr));
	}

	@Test(expected = ArrayIndexOutOfBoundsException.class)
	public void joinByteArrayTestWithArrayIndexException() {
		StringUtils.join(testArray, ';', -1, 2);
	}

	@Test
	public void joinCharArrayTestWithIndex() {
		char[] testArr = { 'a', 'b', 'c' };
		assertThat(StringUtils.join(testArr, ';', 0, 3), is("a;b;c"));
	}

	@Test(expected = ArrayIndexOutOfBoundsException.class)
	public void joinCharArrayTestWithArrayIndexException() {
		StringUtils.join(testArray, ';', -1, 2);
	}

	@Test
	public void joinFloatArrayTestWithIndex() {
		float[] testArr = { 1, 2, 3 };
		assertThat(StringUtils.join(testArr, ';', 0, 3), is("1.0;2.0;3.0"));
	}

	@Test(expected = ArrayIndexOutOfBoundsException.class)
	public void joinFloatArrayTestWithArrayIndexException() {
		StringUtils.join(testArray, ';', -1, 2);
	}

	@Test
	public void joinDoubleArrayTestWithIndex() {
		double[] testArr = { 1, 2, 3 };
		assertThat(StringUtils.join(testArr, ';', 0, 3), is("1.0;2.0;3.0"));
	}

	@Test(expected = ArrayIndexOutOfBoundsException.class)
	public void joinDoubleArrayTestWithArrayIndexException() {
		StringUtils.join(testArray, ';', -1, 2);
	}

	@Test
	public void deleteWhitespaceTest() {
		assertThat(StringUtils.deleteWhitespace("  a  b c    "), is("abc"));
	}

	@Test
	public void removeTest() {
		assertThat(StringUtils.remove(testString, "e"), is("tst"));
	}

	@Test
	public void removeIgnoreCaseTest() {
		assertThat(StringUtils.removeIgnoreCase(testString, "E"), is("tst"));
	}

	@Test
	public void removeTestWithCharRemove() {
		assertThat(StringUtils.remove(testString, 'e'), is("tst"));
	}

	@Test
	public void removeAllTest() {
		assertThat(StringUtils.removeAll(testString, "t"), is("es"));
	}

	@Test(expected = PatternSyntaxException.class)
	public void removeAllTestWithPatternException() {
		StringUtils.removeAll(testString, "[");
	}

	@Test
	public void replaceOnceTest() {
		assertThat(StringUtils.replaceOnce(testString, "t", "for"), is("forest"));
	}

	@Test
	public void replaceOnceIgnoreCaseTest() {
		assertThat(StringUtils.replaceOnceIgnoreCase(testString, "T", "For"), is("Forest"));
	}

	@Test
	public void replacePatternTest() {
		assertThat(StringUtils.replacePattern("ABCabc123", "[a-z]", "_"), is("ABC___123"));
	}
	@Test(expected = PatternSyntaxException.class)
	public void replacePatternTestWithException() {
		StringUtils.replacePattern("ABCabc123", "[", "_");
	}

	@Test
	public void removePatternTest() {
		assertThat(StringUtils.removePattern("ABCabc123", "[a-z]"), is("ABC123"));
	}
	@Test(expected = PatternSyntaxException.class)
	public void removePatternTestWithException() {
		StringUtils.removePattern("ABCabc123", "[");
	}

	@Test
	public void replaceAllTest() {
		assertThat(StringUtils.replaceAll("abc", "", "ZZZ"), is("ZZZaZZZbZZZcZZZ"));
	}

	@Test(expected = PatternSyntaxException.class)
	public void replaceAllTestWithPatternException() {
		StringUtils.replaceAll("abc", "[", "ZZ");
	}

	@Test
	public void replaceTest() {
		assertThat(StringUtils.replace(testString, "t", "y"), is("yesy"));
	}

	@Test
	public void replaceIgnoreCaseTest() {
		assertThat(StringUtils.replaceIgnoreCase(testString, "T", "y"), is("yesy"));
	}

	@Test
	public void replaceTestWithMaxPos() {
		assertThat(StringUtils.replace(testString, "t", "For", 1), is("Forest"));
	}

	@Test
	public void replaceIgnoreCaseTestWithMaxPos() {
		assertThat(StringUtils.replaceIgnoreCase(testString, "T", "For", 1), is("Forest"));
	}

	@Test
	public void replaceCharsTest() {
		assertThat(StringUtils.replaceChars(testString, 't', 'f'), is("fesf"));
	}

	@Test
	public void replaceCharsTestWithStringParameters() {
		assertThat(StringUtils.replace(testString, "es", "xz"), is("txzt"));
	}

	@Test
	public void overlayTest() {
		assertThat(StringUtils.overlay(testMixString, "12345", 3, 7), is("t@#12345%t"));
	}

	@Test
	public void chompTest() {
		assertThat(StringUtils.chomp("abc\n"), is("abc"));
	}

	@Test
	public void chopTest() {
		assertThat(StringUtils.chop(testString), is("tes"));
	}

	@Test
	public void repeatTestWithString() {
		assertThat(StringUtils.repeat("12", 3), is("121212"));
	}

	@Test
	public void repeatTestWithSeperator() {
		assertThat(StringUtils.repeat("Hello", "-", 3), is("Hello-Hello-Hello"));
	}

	@Test
	public void repeattestWithChar() {
		assertThat(StringUtils.repeat('a', 5), is("aaaaa"));
	}

	@Test
	public void lengthTest() {
		assertThat(StringUtils.length(testString), is(4));
	}

	@Test
	public void upperCaseTest() {
		assertThat(StringUtils.upperCase(testString), is("TEST"));
	}

	@Test
	public void upperCaseTestWithLocale() {
		assertThat(StringUtils.upperCase(testString, Locale.ENGLISH), is("TEST"));
	}

	@Test
	public void lowerCaseTest() {
		assertThat(StringUtils.lowerCase("TEST"), is(testString));
	}

	@Test
	public void lowerCaseTestWithLocale() {
		assertThat(StringUtils.lowerCase("TEST", Locale.ENGLISH), is(testString));
	}

	@Test
	public void uncapitalizeTest() {
		assertThat(StringUtils.uncapitalize("Test"), is(testString));
	}

	@Test
	public void swapCaseTest() {
		assertThat(StringUtils.swapCase("TeSt"), is("tEsT"));
	}

	@Test
	public void countMatchesTestWithCharSequence() {
		assertThat(StringUtils.countMatches(testString, "t"), is(2));

	}

	@Test
	public void countMatchesTestWithChar() {
		assertThat(StringUtils.countMatches(testString, 'e'), is(1));
	}

	@Test
	public void isAlphaTest() {
		assertThat(StringUtils.isAlpha(testString), is(true));
	}

	@Test
	public void isAlphanumericTest() {
		assertThat(StringUtils.isAlphanumeric("test123"), is(true));
	}

	@Test
	public void isAlphanumericSpaceTest() {
		assertThat(StringUtils.isAlphanumericSpace("test123#"), is(false));
	}

	@Test
	public void isNumericTest() {
		assertThat(StringUtils.isNumeric(testString), is(false));
	}

	@Test
	public void isNumericSpaceTest() {
		assertThat(StringUtils.isNumericSpace(testString), is(false));
	}

	@Test
	public void getDigitsTest() {
		assertThat(StringUtils.getDigits("test123"), is("123"));
	}

	@Test
	public void isWhitespaceTest() {
		assertThat(StringUtils.isWhitespace(" "), is(true));
	}

	@Test
	public void isAllLowerCaseTest() {
		assertThat(StringUtils.isAllLowerCase(testString), is(true));
	}

	@Test
	public void isAllUpperCase() {
		assertThat(StringUtils.isAllUpperCase("TEST"), is(true));
	}

	@Test
	public void isMixedCaseTest() {
		assertThat(StringUtils.isMixedCase("Test"), is(true));
	}

	@Test
	public void rotateTest() {
		assertThat(StringUtils.rotate(testString, 2), is("stte"));
	}

	@Test
	public void reverseTest() {
		assertThat(StringUtils.reverse(testString), is("tset"));
	}

	@Test
	public void reverseDelimitedTest() {
		assertThat(StringUtils.reverseDelimited(testString, 'e'), is("stet"));
	}

	@Test
	public void abbreviateTest() {
		assertThat(StringUtils.abbreviate("abcdefghijkl", 6), is("abc..."));
	}

	@Test(expected = IllegalArgumentException.class)
	public void abbreviateTestWithIllegalArgumentException() {
		StringUtils.abbreviate("aa", -1);
	}

	@Test
	public void abbreviateTestWithOffset() {
		assertThat(StringUtils.abbreviate("abcdefghijkl", 10, 7), is("...ijkl"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void abbreviateTestWithOffsetWithIllegalArgumentException() {
		StringUtils.abbreviate("aa", 10, -1);
	}

	@Test
	public void abbreviateTestWithAbbrevMarker() {
		assertThat(StringUtils.abbreviate("abcdefghijkl", "@", 3), is("ab@"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void abbreviateTestWithMarkerWithIllegalArgumentException() {
		StringUtils.abbreviate("aa", "@", -1);
	}

	@Test
	public void abbreviateTestWithAbbrevMarkerAndOffset() {
		assertThat(StringUtils.abbreviate("abcdefghijkl", "*", 6, 4), is("*gh*"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void abbreviateTestWithMarkerAndOffsetWithIllegalArgumentException() {
		StringUtils.abbreviate("abcdefghijkl", "*", 4, -1);
	}

	@Test
	public void abbreviateMiddleTest() {
		assertThat(StringUtils.abbreviateMiddle("abcdefghijkl", "#", 5), is("ab#kl"));
	}

	@Test
	public void differenceTest() {
		assertThat(StringUtils.difference("ab", "abxyz"), is("xyz"));
	}

	@Test
	public void startsWithTest() {
		assertThat(StringUtils.startsWith(testString, "te"), is(true));
	}

	@Test
	public void startsWithIgnoreCaseTest() {
		assertThat(StringUtils.startsWithIgnoreCase(testString, "TE"), is(true));
	}

	@Test
	public void endsWithTest() {
		assertThat(StringUtils.endsWith(testString, "st"), is(true));
	}

	@Test
	public void endsWithIgnoreCaseTest() {
		assertThat(StringUtils.endsWithIgnoreCase(testString, "ST"), is(true));
	}

	@Test
	public void normalizeSpaceTest() {
		assertThat(StringUtils.normalizeSpace("  te     st  "), is("te st"));
	}

	@Test
	public void appendifMissingTest() {
		assertThat(StringUtils.appendIfMissing(testString, "abc"), is("testabc"));
	}

	@Test
	public void appendIfMissingIgnoreCaseTest() {
		assertThat(StringUtils.appendIfMissingIgnoreCase(testString, "sT"), is("test"));
	}

	@Test
	public void prependIfMissingTest() {
		assertThat(StringUtils.prependIfMissing(testString, "AT"), is("ATtest"));
	}

	@Test
	public void prependIfMissingIgnoreCaseTest() {
		assertThat(StringUtils.prependIfMissingIgnoreCase(testString, "AT"), is("ATtest"));
	}

	@Test
	public void wrapTestWithChar() {
		assertThat(StringUtils.wrap(testString, 'S'), is("StestS"));
	}

	@Test
	public void wrapTestWithString() {
		assertThat(StringUtils.wrap(testString, "XX"), is("XXtestXX"));
	}

	@Test
	public void wrapIfMissingTestWithChar() {
		assertThat(StringUtils.wrapIfMissing(testString, 'T'), is("TtestT"));
	}

	@Test
	public void wrapIfMissingTestWithString() {
		assertThat(StringUtils.wrapIfMissing(testString, "te"), is("testte"));
	}

	@Test
	public void unwrapTestWithString() {
		assertThat(StringUtils.unwrap("AABabcBAA", "AA"), is("BabcB"));
	}

	@Test
	public void unwrapTestWithChar() {
		assertThat(StringUtils.unwrap(testString, 't'), is("es"));
	}

}
