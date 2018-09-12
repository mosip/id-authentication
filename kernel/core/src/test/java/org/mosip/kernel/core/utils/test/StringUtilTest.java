package org.mosip.kernel.core.utils.test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Locale;

import org.junit.Test;
import org.mosip.kernel.core.utils.StringUtil;
import org.mosip.kernel.core.utils.exception.MosipArrayIndexOutOfBoundsException;
import org.mosip.kernel.core.utils.exception.MosipIllegalArgumentException;
import org.mosip.kernel.core.utils.exception.MosipPatternSyntaxException;

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
		assertThat(StringUtil.capitalizeFirstLetter(testString), is("Test"));
	}

	@Test
	public void compareTest() {
		assertThat(StringUtil.compare("test", testString), is(0));
	}

	@Test
	public void compareTestWithBoolean() {
		assertThat(StringUtil.compare("test", testString, true), is(0));

	}

	@Test
	public void compareIgnoreCaseTest() {
		assertThat(StringUtil.compareIgnoreCase("TeSt", testString), is(0));
	}

	@Test
	public void compareIgnoreCaseTestWithBoolean() {
		assertThat(StringUtil.compareIgnoreCase("tEsT", testString, true), is(0));

	}

	@Test
	public void containsTest() {

		assertThat(StringUtil.contains("abctestabc", testString), is(true));

	}

	@Test
	public void containsTestWithIntSearch() {
		assertThat(StringUtil.contains(testString, 101), is(true));
	}

	@Test
	public void containsIgnoreCaseTest() {
		assertThat(StringUtil.containsIgnoreCase(testString, "es"), is(true));
	}

	@Test
	public void containsWhiteSpaceTest() {
		assertThat(StringUtil.containsWhitespace(testString), is(false));
	}

	@Test
	public void isEmptyTest() {
		assertThat(StringUtil.isEmpty(testString), is(false));
	}

	@Test
	public void isNotEmptyTest() {
		assertThat(StringUtil.isNotEmpty(testString), is(true));
	}

	@Test
	public void isBlankTest() {
		assertThat(StringUtil.isBlank(""), is(true));
	}

	@Test
	public void isNotBlankTest() {
		assertThat(StringUtil.isNotBlank(testString), is(true));
	}

	@Test
	public void trimTest() {
		assertThat(StringUtil.trim("    test   "), is(testString));
	}

	@Test
	public void truncateTest() {
		assertThat(StringUtil.truncate("test1234   ", 4), is(testString));
	}

	@Test
	public void truncateTestWithOffset() {
		assertThat(StringUtil.truncate("abctest124 ", 3, 4), is(testString));
	}

	@Test
	public void stripTest() {
		assertThat(StringUtil.strip("    test    "), is(testString));
	}

	@Test
	public void stripTestWithChar() {
		assertThat(StringUtil.strip("abtest", "ab"), is(testString));
	}

	@Test
	public void equalsTest() {
		assertThat(StringUtil.equals("test", testString), is(true));
	}

	@Test
	public void equalsIgnoreCaseTest() {
		assertThat(StringUtil.equalsIgnoreCase("Test", testString), is(true));
	}

	@Test
	public void indexOfTest() {
		assertThat(StringUtil.indexOf(testString, 'e'), is(1));
	}

	@Test
	public void indexOfTestWithStartPos() {
		assertThat(StringUtil.indexOf(testString, 't', 1), is(3));
	}

	@Test
	public void indexOfTestWithSearchString() {
		assertThat(StringUtil.indexOf(testString, "es"), is(1));
	}

	@Test
	public void indexOfTestWithSearchStringAndStartPos() {
		assertThat(StringUtil.indexOf(testString, "t", 2), is(3));
	}

	@Test
	public void lastIndexOfTest() {
		assertThat(StringUtil.lastIndexOf(testString, 115), is(2));
	}

	@Test
	public void lastIndexOfTestWithStartPos() {
		assertThat(StringUtil.lastIndexOf(testString, 116, -1), is(-1));
	}

	@Test
	public void lastIndexOfTestWithSearchString() {
		assertThat(StringUtil.lastIndexOf("testabctest", testString), is(7));
	}

	@Test
	public void lastIndexOfTestWithSearchStringAndStartPos() {
		assertThat(StringUtil.lastIndexOf("testabctestabc", testString, -1), is(-1));
	}

	@Test
	public void substringTest() {
		assertThat(StringUtil.substring(testString, 1), is("est"));
	}

	@Test
	public void substringTestWithEndPos() {
		assertThat(StringUtil.substring(testString, 1, 3), is("es"));
	}

	@Test
	public void removeLeftCharTest() {
		assertThat(StringUtil.removeLeftChar(testString, 2), is("te"));
	}

	@Test
	public void removeRightCharTest() {
		assertThat(StringUtil.removeRightChar(testString, 2), is("st"));
	}

	@Test
	public void removeMidCharTest() {
		assertThat(StringUtil.removeMidChar(testString, 1, 2), is("es"));

	}

	@Test
	public void substringBeforeTest() {
		assertThat(StringUtil.substringBefore(testString, "s"), is("te"));
	}

	@Test
	public void substringAfterTest() {
		assertThat(StringUtil.substringAfter(testString, "t"), is("est"));
	}

	@Test
	public void splitTest() {
		assertThat(StringUtil.split("test abc"), is(testArray));
	}

	@Test
	public void splitTestWithSperatorChar() {
		assertThat(StringUtil.split("testxabc", 'x'), is(testArray));
	}

	@Test
	public void splitByCharacterTypeTest() {
		assertThat(StringUtil.splitByCharacterType(testMixString), is(testMixArray));
	}

	@Test
	public void splitByCharacterTypeWithCamelCaseTest() {
		String[] testSplitArray = { "MIND", "Tree" };
		assertThat(StringUtil.splitByCharacterTypeCamelCase("MINDTree"), is(testSplitArray));
	}

	@Test
	public void joinObjectTest() {
		assertThat(StringUtil.join(testArray, ';'), is("test;abc"));
	}

	@Test
	public void joinLongArrayTest() {
		long[] testArr = { 1, 2, 3 };
		assertThat(StringUtil.join(testArr, ';'), is(joinedTestArr));

	}

	@Test
	public void joinIntArrayTest() {
		int[] testArr = { 1, 2, 3 };
		assertThat(StringUtil.join(testArr, ';'), is(joinedTestArr));
	}

	@Test
	public void joinShortArrayTest() {
		short[] testArr = { 1, 2, 3 };
		assertThat(StringUtil.join(testArr, ';'), is(joinedTestArr));
	}

	@Test
	public void joinByteArrayTest() {
		byte[] testArr = { 1, 2, 3 };
		assertThat(StringUtil.join(testArr, ';'), is(joinedTestArr));
	}

	@Test
	public void joinCharArrayTest() {
		char[] testArr = { 'a', 'b', 'c' };
		assertThat(StringUtil.join(testArr, ';'), is("a;b;c"));
	}

	@Test
	public void joinFloatArrayTest() {
		float[] testArr = { 1, 2, 3 };
		assertThat(StringUtil.join(testArr, ';'), is("1.0;2.0;3.0"));
	}

	@Test
	public void joinDoubleArrayTest() {
		double[] testArr = { 1, 2, 3 };
		assertThat(StringUtil.join(testArr, ';'), is("1.0;2.0;3.0"));
	}

	@Test
	public void joinObjectArrayTestWithIndex() {
		assertThat(StringUtil.join(testArray, ';', 0, 2), is("test;abc"));
	}

	@Test(expected = MosipArrayIndexOutOfBoundsException.class)
	public void joinObjectArrayTestWithArrayIndexException() {
		StringUtil.join(testArray, ';', -1, 2);
	}

	@Test
	public void joinLongArrayTestWithIndex() {
		long[] testArr = { 1, 2, 3 };
		assertThat(StringUtil.join(testArr, ';', 0, 3), is(joinedTestArr));

	}

	@Test(expected = MosipArrayIndexOutOfBoundsException.class)
	public void joinLongArrayTestWithArrayIndexException() {
		StringUtil.join(testArray, ';', -1, 2);
	}

	@Test
	public void joinIntArrayTestWithIndex() {
		int[] testArr = { 1, 2, 3 };
		assertThat(StringUtil.join(testArr, ';', 0, 3), is(joinedTestArr));
	}

	@Test(expected = MosipArrayIndexOutOfBoundsException.class)
	public void joinIntArrayTestWithArrayIndexException() {
		StringUtil.join(testArray, ';', -1, 2);
	}

	@Test
	public void joinShortArrayTestWithIndex() {
		short[] testArr = { 1, 2, 3 };
		assertThat(StringUtil.join(testArr, ';', 0, 3), is(joinedTestArr));
	}

	@Test
	public void joinByteArrayTestWithIndex() {
		byte[] testArr = { 1, 2, 3 };
		assertThat(StringUtil.join(testArr, ';', 0, 3), is(joinedTestArr));
	}

	@Test(expected = MosipArrayIndexOutOfBoundsException.class)
	public void joinByteArrayTestWithArrayIndexException() {
		StringUtil.join(testArray, ';', -1, 2);
	}

	@Test
	public void joinCharArrayTestWithIndex() {
		char[] testArr = { 'a', 'b', 'c' };
		assertThat(StringUtil.join(testArr, ';', 0, 3), is("a;b;c"));
	}

	@Test(expected = MosipArrayIndexOutOfBoundsException.class)
	public void joinCharArrayTestWithArrayIndexException() {
		StringUtil.join(testArray, ';', -1, 2);
	}

	@Test
	public void joinFloatArrayTestWithIndex() {
		float[] testArr = { 1, 2, 3 };
		assertThat(StringUtil.join(testArr, ';', 0, 3), is("1.0;2.0;3.0"));
	}

	@Test(expected = MosipArrayIndexOutOfBoundsException.class)
	public void joinFloatArrayTestWithArrayIndexException() {
		StringUtil.join(testArray, ';', -1, 2);
	}

	@Test
	public void joinDoubleArrayTestWithIndex() {
		double[] testArr = { 1, 2, 3 };
		assertThat(StringUtil.join(testArr, ';', 0, 3), is("1.0;2.0;3.0"));
	}

	@Test(expected = MosipArrayIndexOutOfBoundsException.class)
	public void joinDoubleArrayTestWithArrayIndexException() {
		StringUtil.join(testArray, ';', -1, 2);
	}

	@Test
	public void deleteWhitespaceTest() {
		assertThat(StringUtil.deleteWhitespace("  a  b c    "), is("abc"));
	}

	@Test
	public void removeTest() {
		assertThat(StringUtil.remove(testString, "e"), is("tst"));
	}

	@Test
	public void removeIgnoreCaseTest() {
		assertThat(StringUtil.removeIgnoreCase(testString, "E"), is("tst"));
	}

	@Test
	public void removeTestWithCharRemove() {
		assertThat(StringUtil.remove(testString, 'e'), is("tst"));
	}

	@Test
	public void removeAllTest() {
		assertThat(StringUtil.removeAll(testString, "t"), is("es"));
	}

	@Test(expected = MosipPatternSyntaxException.class)
	public void removeAllTestWithPatternException() {
		StringUtil.removeAll(testString, "[");
	}

	@Test
	public void replaceOnceTest() {
		assertThat(StringUtil.replaceOnce(testString, "t", "for"), is("forest"));
	}

	@Test
	public void replaceOnceIgnoreCaseTest() {
		assertThat(StringUtil.replaceOnceIgnoreCase(testString, "T", "For"), is("Forest"));
	}

	@Test
	public void replacePatternTest() {
		assertThat(StringUtil.replacePattern("ABCabc123", "[a-z]", "_"), is("ABC___123"));
	}
	@Test(expected = MosipPatternSyntaxException.class)
	public void replacePatternTestWithException() {
		StringUtil.replacePattern("ABCabc123", "[", "_");
	}

	@Test
	public void removePatternTest() {
		assertThat(StringUtil.removePattern("ABCabc123", "[a-z]"), is("ABC123"));
	}
	@Test(expected = MosipPatternSyntaxException.class)
	public void removePatternTestWithException() {
		StringUtil.removePattern("ABCabc123", "[");
	}

	@Test
	public void replaceAllTest() {
		assertThat(StringUtil.replaceAll("abc", "", "ZZZ"), is("ZZZaZZZbZZZcZZZ"));
	}

	@Test(expected = MosipPatternSyntaxException.class)
	public void replaceAllTestWithPatternException() {
		StringUtil.replaceAll("abc", "[", "ZZ");
	}

	@Test
	public void replaceTest() {
		assertThat(StringUtil.replace(testString, "t", "y"), is("yesy"));
	}

	@Test
	public void replaceIgnoreCaseTest() {
		assertThat(StringUtil.replaceIgnoreCase(testString, "T", "y"), is("yesy"));
	}

	@Test
	public void replaceTestWithMaxPos() {
		assertThat(StringUtil.replace(testString, "t", "For", 1), is("Forest"));
	}

	@Test
	public void replaceIgnoreCaseTestWithMaxPos() {
		assertThat(StringUtil.replaceIgnoreCase(testString, "T", "For", 1), is("Forest"));
	}

	@Test
	public void replaceCharsTest() {
		assertThat(StringUtil.replaceChars(testString, 't', 'f'), is("fesf"));
	}

	@Test
	public void replaceCharsTestWithStringParameters() {
		assertThat(StringUtil.replace(testString, "es", "xz"), is("txzt"));
	}

	@Test
	public void overlayTest() {
		assertThat(StringUtil.overlay(testMixString, "12345", 3, 7), is("t@#12345%t"));
	}

	@Test
	public void chompTest() {
		assertThat(StringUtil.chomp("abc\n"), is("abc"));
	}

	@Test
	public void chopTest() {
		assertThat(StringUtil.chop(testString), is("tes"));
	}

	@Test
	public void repeatTestWithString() {
		assertThat(StringUtil.repeat("12", 3), is("121212"));
	}

	@Test
	public void repeatTestWithSeperator() {
		assertThat(StringUtil.repeat("Hello", "-", 3), is("Hello-Hello-Hello"));
	}

	@Test
	public void repeattestWithChar() {
		assertThat(StringUtil.repeat('a', 5), is("aaaaa"));
	}

	@Test
	public void lengthTest() {
		assertThat(StringUtil.length(testString), is(4));
	}

	@Test
	public void upperCaseTest() {
		assertThat(StringUtil.upperCase(testString), is("TEST"));
	}

	@Test
	public void upperCaseTestWithLocale() {
		assertThat(StringUtil.upperCase(testString, Locale.ENGLISH), is("TEST"));
	}

	@Test
	public void lowerCaseTest() {
		assertThat(StringUtil.lowerCase("TEST"), is(testString));
	}

	@Test
	public void lowerCaseTestWithLocale() {
		assertThat(StringUtil.lowerCase("TEST", Locale.ENGLISH), is(testString));
	}

	@Test
	public void uncapitalizeTest() {
		assertThat(StringUtil.uncapitalize("Test"), is(testString));
	}

	@Test
	public void swapCaseTest() {
		assertThat(StringUtil.swapCase("TeSt"), is("tEsT"));
	}

	@Test
	public void countMatchesTestWithCharSequence() {
		assertThat(StringUtil.countMatches(testString, "t"), is(2));

	}

	@Test
	public void countMatchesTestWithChar() {
		assertThat(StringUtil.countMatches(testString, 'e'), is(1));
	}

	@Test
	public void isAlphaTest() {
		assertThat(StringUtil.isAlpha(testString), is(true));
	}

	@Test
	public void isAlphanumericTest() {
		assertThat(StringUtil.isAlphanumeric("test123"), is(true));
	}

	@Test
	public void isAlphanumericSpaceTest() {
		assertThat(StringUtil.isAlphanumericSpace("test123#"), is(false));
	}

	@Test
	public void isNumericTest() {
		assertThat(StringUtil.isNumeric(testString), is(false));
	}

	@Test
	public void isNumericSpaceTest() {
		assertThat(StringUtil.isNumericSpace(testString), is(false));
	}

	@Test
	public void getDigitsTest() {
		assertThat(StringUtil.getDigits("test123"), is("123"));
	}

	@Test
	public void isWhitespaceTest() {
		assertThat(StringUtil.isWhitespace(" "), is(true));
	}

	@Test
	public void isAllLowerCaseTest() {
		assertThat(StringUtil.isAllLowerCase(testString), is(true));
	}

	@Test
	public void isAllUpperCase() {
		assertThat(StringUtil.isAllUpperCase("TEST"), is(true));
	}

	@Test
	public void isMixedCaseTest() {
		assertThat(StringUtil.isMixedCase("Test"), is(true));
	}

	@Test
	public void rotateTest() {
		assertThat(StringUtil.rotate(testString, 2), is("stte"));
	}

	@Test
	public void reverseTest() {
		assertThat(StringUtil.reverse(testString), is("tset"));
	}

	@Test
	public void reverseDelimitedTest() {
		assertThat(StringUtil.reverseDelimited(testString, 'e'), is("stet"));
	}

	@Test
	public void abbreviateTest() {
		assertThat(StringUtil.abbreviate("abcdefghijkl", 6), is("abc..."));
	}

	@Test(expected = MosipIllegalArgumentException.class)
	public void abbreviateTestWithIllegalArgumentException() {
		StringUtil.abbreviate("aa", -1);
	}

	@Test
	public void abbreviateTestWithOffset() {
		assertThat(StringUtil.abbreviate("abcdefghijkl", 10, 7), is("...ijkl"));
	}

	@Test(expected = MosipIllegalArgumentException.class)
	public void abbreviateTestWithOffsetWithIllegalArgumentException() {
		StringUtil.abbreviate("aa", 10, -1);
	}

	@Test
	public void abbreviateTestWithAbbrevMarker() {
		assertThat(StringUtil.abbreviate("abcdefghijkl", "@", 3), is("ab@"));
	}

	@Test(expected = MosipIllegalArgumentException.class)
	public void abbreviateTestWithMarkerWithIllegalArgumentException() {
		StringUtil.abbreviate("aa", "@", -1);
	}

	@Test
	public void abbreviateTestWithAbbrevMarkerAndOffset() {
		assertThat(StringUtil.abbreviate("abcdefghijkl", "*", 6, 4), is("*gh*"));
	}

	@Test(expected = MosipIllegalArgumentException.class)
	public void abbreviateTestWithMarkerAndOffsetWithIllegalArgumentException() {
		StringUtil.abbreviate("abcdefghijkl", "*", 4, -1);
	}

	@Test
	public void abbreviateMiddleTest() {
		assertThat(StringUtil.abbreviateMiddle("abcdefghijkl", "#", 5), is("ab#kl"));
	}

	@Test
	public void differenceTest() {
		assertThat(StringUtil.difference("ab", "abxyz"), is("xyz"));
	}

	@Test
	public void startsWithTest() {
		assertThat(StringUtil.startsWith(testString, "te"), is(true));
	}

	@Test
	public void startsWithIgnoreCaseTest() {
		assertThat(StringUtil.startsWithIgnoreCase(testString, "TE"), is(true));
	}

	@Test
	public void endsWithTest() {
		assertThat(StringUtil.endsWith(testString, "st"), is(true));
	}

	@Test
	public void endsWithIgnoreCaseTest() {
		assertThat(StringUtil.endsWithIgnoreCase(testString, "ST"), is(true));
	}

	@Test
	public void normalizeSpaceTest() {
		assertThat(StringUtil.normalizeSpace("  te     st  "), is("te st"));
	}

	@Test
	public void appendifMissingTest() {
		assertThat(StringUtil.appendIfMissing(testString, "abc"), is("testabc"));
	}

	@Test
	public void appendIfMissingIgnoreCaseTest() {
		assertThat(StringUtil.appendIfMissingIgnoreCase(testString, "sT"), is("test"));
	}

	@Test
	public void prependIfMissingTest() {
		assertThat(StringUtil.prependIfMissing(testString, "AT"), is("ATtest"));
	}

	@Test
	public void prependIfMissingIgnoreCaseTest() {
		assertThat(StringUtil.prependIfMissingIgnoreCase(testString, "AT"), is("ATtest"));
	}

	@Test
	public void wrapTestWithChar() {
		assertThat(StringUtil.wrap(testString, 'S'), is("StestS"));
	}

	@Test
	public void wrapTestWithString() {
		assertThat(StringUtil.wrap(testString, "XX"), is("XXtestXX"));
	}

	@Test
	public void wrapIfMissingTestWithChar() {
		assertThat(StringUtil.wrapIfMissing(testString, 'T'), is("TtestT"));
	}

	@Test
	public void wrapIfMissingTestWithString() {
		assertThat(StringUtil.wrapIfMissing(testString, "te"), is("testte"));
	}

	@Test
	public void unwrapTestWithString() {
		assertThat(StringUtil.unwrap("AABabcBAA", "AA"), is("BabcB"));
	}

	@Test
	public void unwrapTestWithChar() {
		assertThat(StringUtil.unwrap(testString, 't'), is("es"));
	}

}
