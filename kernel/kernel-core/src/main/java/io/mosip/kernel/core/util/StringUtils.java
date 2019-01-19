/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.mosip.kernel.core.util;

import java.util.Locale;

import io.mosip.kernel.core.exception.ArrayIndexOutOfBoundsException;
import io.mosip.kernel.core.exception.IllegalArgumentException;
import io.mosip.kernel.core.exception.PatternSyntaxException;
import io.mosip.kernel.core.util.constant.StringUtilConstants;

/**
 * This class contains methods used for operations on String type data
 * @author Sidhant Agarwal
 * @since 1.0.0
 *
 */

public final class StringUtils {
	/**
	 * Private Constructor for StringUtil class
	 * 
	 */
	private StringUtils() {

	}

	/**
	 * This method capitalizes the first character of the passed parameter
	 * 
	 * @param sourceString
	 *            input string
	 * @return String changing the first character to UpperCase
	 */
	public static String capitalizeFirstLetter(String sourceString) {
		return org.apache.commons.lang3.StringUtils.capitalize(sourceString);
	}

	/**
	 * This method compares two input string
	 * 
	 * @param sourceString1
	 *            String value
	 * @param sourceString2
	 *            String value
	 * @return 0 if sourceString1 is equal to sourceString2 (or both null) is less
	 *         than 0, if sourceString1 is less than sourceString2 is greater than
	 *         0, if sourceString1 is greater than sourceString2
	 */
	public static int compare(String sourceString1, String sourceString2) {
		return org.apache.commons.lang3.StringUtils.compare(sourceString1, sourceString2);
	}

	/**
	 * This method compares two Strings lexicographically
	 * 
	 * @param sourceString1
	 *            String to compare from
	 * @param sourceString2
	 *            String to compare to
	 * @param nullIsLess
	 *            whether consider null value less than non-null value
	 * @return = 0, if sourceString1 is equal to sourceString2 (or both null) is
	 *         less than 0, if sourceString1 is less than sourceString2 is greater
	 *         than 0, if sourceString1 is greater than sourceString2
	 */
	public static int compare(String sourceString1, String sourceString2, boolean nullIsLess) {
		return org.apache.commons.lang3.StringUtils.compare(sourceString1, sourceString2, nullIsLess);
	}

	/**
	 * This method compares two Strings lexicographically, ignoring case differences
	 * 
	 * @param sourceString1
	 *            String to compare from
	 * @param sourceString2
	 *            String to compare to
	 * @return = 0, if sourceString1 is equal to sourceString2 (or both null) is
	 *         less than 0, if sourceString1 is less than sourceString2 is greater
	 *         than 0, if sourceString1 is greater than sourceString2
	 */
	public static int compareIgnoreCase(String sourceString1, String sourceString2) {
		return org.apache.commons.lang3.StringUtils.compareIgnoreCase(sourceString1, sourceString2);
	}

	/**
	 * @param sourceString1
	 *            String to compare from
	 * @param sourceString2
	 *            String to compare to
	 * @param nullIsLess
	 *            whether consider null value less than non-null value
	 * @return = 0, if sourceString1 is equal to sourceString2 (or both null) is
	 *         less than 0, if sourceString1 is less than sourceString2 is greater
	 *         than 0, if sourceString1 is greater than sourceString2
	 */
	public static int compareIgnoreCase(String sourceString1, String sourceString2, boolean nullIsLess) {
		return org.apache.commons.lang3.StringUtils.compareIgnoreCase(sourceString1, sourceString2, nullIsLess);
	}

	/**
	 * This method checks if CharSequence contains a search CharSequence, handling
	 * null
	 * 
	 * @param seq
	 *            the CharSequence to check, may be null
	 * @param searchSeq
	 *            the CharSequence to find, may be null
	 * @return true if the CharSequence contains the search CharSequence false if
	 *         not or null string input
	 */
	public static boolean contains(CharSequence seq, CharSequence searchSeq) {
		return org.apache.commons.lang3.StringUtils.contains(seq, searchSeq);
	}

	/**
	 * This method checks if CharSequence contains a search character, handling null
	 * 
	 * @param seq
	 *            the CharSequence to check, may be null
	 * @param searchChar
	 *            the character to find
	 * @return true if the CharSequence contains the search CharSequence false if
	 *         not or null string input
	 */
	public static boolean contains(CharSequence seq, int searchChar) {
		return org.apache.commons.lang3.StringUtils.contains(seq, searchChar);
	}

	/**
	 * This method checks if CharSequence contains a search CharSequence
	 * irrespective of case, handling null.
	 * 
	 * @param sourceString
	 *            the CharSequence to check, may be null
	 * @param searchStr
	 *            the CharSequence to find, may be null
	 * @return true if the CharSequence contains the search CharSequence
	 *         irrespective of case false if not or null string input
	 */
	public static boolean containsIgnoreCase(CharSequence sourceString, CharSequence searchStr) {
		return org.apache.commons.lang3.StringUtils.containsIgnoreCase(sourceString, searchStr);
	}

	/**
	 * This method checks whether the given CharSequence contains any whitespace
	 * characters
	 * 
	 * @param seq
	 *            the CharSequence to check (may be null)
	 * @return true if the CharSequence is not empty and contains at least 1
	 *         (breaking) whitespace character false if no whitespace is present
	 */
	public static boolean containsWhitespace(CharSequence seq) {
		return org.apache.commons.lang3.StringUtils.containsWhitespace(seq);
	}

	/**
	 * This method checks if a CharSequence is empty ("") or null
	 * 
	 * @param charSeq
	 *            the CharSequence to check, may be null
	 * @return true if the CharSequence is empty or null
	 */
	public static boolean isEmpty(CharSequence charSeq) {
		return org.apache.commons.lang3.StringUtils.isEmpty(charSeq);
	}

	/**
	 * This method checks if a CharSequence is not empty ("") and not null
	 * 
	 * @param charSeq
	 *            the CharSequence to check, may be null
	 * @return true if the CharSequence is not empty and not null
	 */
	public static boolean isNotEmpty(CharSequence charSeq) {
		return org.apache.commons.lang3.StringUtils.isNotEmpty(charSeq);
	}

	/**
	 * This method checks if a CharSequence is empty (""), null or whitespace only
	 * 
	 * @param charSeq
	 *            the CharSequence to check, may be null
	 * @return true if the CharSequence is null, empty or whitespace only
	 */
	public static boolean isBlank(CharSequence charSeq) {
		return org.apache.commons.lang3.StringUtils.isBlank(charSeq);
	}

	/**
	 * This method checks if a CharSequence is not empty (""), not null and not
	 * whitespace only
	 * 
	 * @param charSeq
	 *            the CharSequence to check, may be null
	 * @return true if the CharSequence is not empty and not null and not whitespace
	 *         only
	 */
	public static boolean isNotBlank(CharSequence charSeq) {
		return org.apache.commons.lang3.StringUtils.isNotBlank(charSeq);
	}

	/**
	 * This method removes control characters (char is less than= 32) from both ends
	 * of this String, handling null by returning null
	 * 
	 * @param sourceString
	 *            the String to be trimmed, may be null
	 * @return the trimmed string null, if null String input
	 */
	public static String trim(String sourceString) {
		return org.apache.commons.lang3.StringUtils.trim(sourceString);
	}

	/**
	 * Truncates a String
	 * 
	 * @param sourceString
	 *            the String to truncate, may be null
	 * @param maxWidth
	 *            maximum length of result String, must be positive
	 * @return truncated String, null if null String input
	 */
	public static String truncate(String sourceString, int maxWidth) {
		return org.apache.commons.lang3.StringUtils.truncate(sourceString, maxWidth);
	}

	/**
	 * Truncates a String
	 * 
	 * @param sourceString
	 *            the String to check, may be null
	 * @param offset
	 *            left edge of source String
	 * @param maxWidth
	 *            maximum length of result String, must be positive
	 * @return truncated String, null if null String input
	 */
	public static String truncate(String sourceString, int offset, int maxWidth) {
		return org.apache.commons.lang3.StringUtils.truncate(sourceString, offset, maxWidth);
	}

	/**
	 * Strips whitespace from the start and end of a String
	 * 
	 * @param sourceString
	 *            the String to remove whitespace from, may be null
	 * @return the stripped String, null if null String input
	 */
	public static String strip(String sourceString) {
		return org.apache.commons.lang3.StringUtils.strip(sourceString);
	}

	/**
	 * Strips any of a set of characters from the start and end of a String
	 * 
	 * @param sourceString
	 *            the String to remove characters from, may be null
	 * @param stripChars
	 *            the characters to remove, null treated as whitespace
	 * @return the stripped String null if null String input
	 */
	public static String strip(String sourceString, String stripChars) {
		return org.apache.commons.lang3.StringUtils.strip(sourceString, stripChars);
	}

	/**
	 * Compares two CharSequences
	 * 
	 * @param charSeq1
	 *            the first CharSequence, may be null
	 * @param charSeq2
	 *            charSeq2 the second CharSequence, may be null
	 * @return true if the CharSequences are equal (case-sensitive), or both null
	 */
	public static boolean equals(CharSequence charSeq1, CharSequence charSeq2) {
		return org.apache.commons.lang3.StringUtils.equals(charSeq1, charSeq2);
	}

	/**
	 * Compares two CharSequences ignoring case
	 * 
	 * @param sourceString1
	 *            the first CharSequence, may be null
	 * @param sourceString2
	 *            the second CharSequence, may be null
	 * @return true if the CharSequence are equal, case insensitive, or both null
	 */
	public static boolean equalsIgnoreCase(CharSequence sourceString1, CharSequence sourceString2) {
		return org.apache.commons.lang3.StringUtils.equalsIgnoreCase(sourceString1, sourceString2);
	}

	/**
	 * Returns the index within seq of the first occurrence of the specified
	 * character
	 * 
	 * @param seq
	 *            the CharSequence to check, may be null
	 * @param searchChar
	 *            the character to find
	 * @return the first index of the search character, -1 if no match or null
	 *         string input
	 */
	public static int indexOf(CharSequence seq, int searchChar) {
		return org.apache.commons.lang3.StringUtils.indexOf(seq, searchChar);
	}

	/**
	 * Returns the index within seq of the first occurrence of the specified
	 * character, starting the search at the specified index
	 * 
	 * @param seq
	 *            the CharSequence to check, may be null
	 * @param searchChar
	 *            the character to find
	 * @param startPos
	 *            the start position, negative treated as zero
	 * @return the first index of the search character (always ≥ startPos), -1 if no
	 *         match or null string input
	 */
	public static int indexOf(CharSequence seq, int searchChar, int startPos) {
		return org.apache.commons.lang3.StringUtils.indexOf(seq, searchChar, startPos);
	}

	/**
	 * Finds the first index within a CharSequence, handling null
	 * 
	 * @param seq
	 *            the CharSequence to check, may be null
	 * @param searchSeq
	 *            the CharSequence to find, may be null
	 * @return the first index of the search CharSequence, -1 if no match or null
	 *         string input
	 */
	public static int indexOf(CharSequence seq, CharSequence searchSeq) {
		return org.apache.commons.lang3.StringUtils.indexOf(seq, searchSeq);
	}

	/**
	 * Finds the first index within a CharSequence, handling null
	 * 
	 * @param seq
	 *            the CharSequence to check, may be null
	 * @param searchSeq
	 *            the CharSequence to find, may be null
	 * @param startPos
	 *            the start position, negative treated as zero
	 * @return the first index of the search CharSequence (always ≥ startPos), -1 if
	 *         no match or null string input
	 */
	public static int indexOf(CharSequence seq, CharSequence searchSeq, int startPos) {
		return org.apache.commons.lang3.StringUtils.indexOf(seq, searchSeq, startPos);
	}

	/**
	 * Returns the index within seq of the last occurrence of the specified
	 * character
	 * 
	 * @param seq
	 *            the CharSequence to check, may be null
	 * @param searchChar
	 *            the character to find
	 * @return the last index of the search character, -1 if no match or null string
	 *         input
	 */
	public static int lastIndexOf(CharSequence seq, int searchChar) {
		return org.apache.commons.lang3.StringUtils.lastIndexOf(seq, searchChar);
	}

	/**
	 * Returns the index within seq of the last occurrence of the specified
	 * character, searching backward starting at the specified index
	 * 
	 * @param seq
	 *            the CharSequence to check, may be null
	 * @param searchChar
	 *            the character to find
	 * @param startPos
	 *            the start position
	 * @return the last index of the search character (always ≤ startPos), -1 if no
	 *         match or null string input
	 */
	public static int lastIndexOf(CharSequence seq, int searchChar, int startPos) {
		return org.apache.commons.lang3.StringUtils.lastIndexOf(seq, searchChar, startPos);
	}

	/**
	 * Finds the last index within a CharSequence, handling null
	 * 
	 * @param seq
	 *            the CharSequence to check, may be null
	 * @param searchSeq
	 *            the CharSequence to find, may be null
	 * @return the last index of the search String, -1 if no match or null string
	 *         input
	 */
	public static int lastIndexOf(CharSequence seq, CharSequence searchSeq) {
		return org.apache.commons.lang3.StringUtils.lastIndexOf(seq, searchSeq);
	}

	/**
	 * Finds the last index within a CharSequence, handling null
	 * 
	 * @param seq
	 *            the CharSequence to check, may be null
	 * @param searchSeq
	 *            the CharSequence to find, may be null
	 * @param startPos
	 *            the start position, negative treated as zero
	 * @return the last index of the search CharSequence (always ≤ startPos), -1 if
	 *         no match or null string input
	 */
	public static int lastIndexOf(CharSequence seq, CharSequence searchSeq, int startPos) {
		return org.apache.commons.lang3.StringUtils.lastIndexOf(seq, searchSeq, startPos);
	}

	/**
	 * Gets a substring from the specified String avoiding exceptions.
	 * 
	 * @param sourceString
	 *            the String to get the substring from, may be null
	 * @param start
	 *            the position to start from, negative means count back from the end
	 *            of the String by this many characters
	 * @return substring from start position, null if null String input
	 */
	public static String substring(String sourceString, int start) {
		return org.apache.commons.lang3.StringUtils.substring(sourceString, start);
	}

	/**
	 * Gets a substring from the specified String avoiding exceptions
	 * 
	 * @param sourceString
	 *            the String to get the substring from, may be null
	 * @param start
	 *            the position to start from, negative means count back from the end
	 *            of the String by this many characters
	 * @param end
	 *            the position to end at (exclusive), negative means count back from
	 *            the end of the String by this many characters
	 * @return substring from start position to end position, null if null String
	 *         input
	 */
	public static String substring(String sourceString, int start, int end) {
		return org.apache.commons.lang3.StringUtils.substring(sourceString, start, end);
	}

	/**
	 * Gets the leftmost len characters of a String
	 * 
	 * @param sourceString
	 *            the String to get the leftmost characters from, may be null
	 * @param len
	 *            the length of the required String
	 * @return the leftmost characters, null if null String input
	 */
	public static String removeLeftChar(String sourceString, int len) {
		return org.apache.commons.lang3.StringUtils.left(sourceString, len);
	}

	/**
	 * Gets the rightmost len characters of a String
	 * 
	 * @param sourceString
	 *            the String to get the rightmost characters from, may be null
	 * @param len
	 *            the length of the required String
	 * @return the rightmost characters, null if null String input
	 */
	public static String removeRightChar(String sourceString, int len) {
		return org.apache.commons.lang3.StringUtils.right(sourceString, len);
	}

	/**
	 * Gets len characters from the middle of a String
	 * 
	 * @param sourceString
	 *            the String to get the characters from, may be null
	 * @param pos
	 *            the position to start from, negative treated as zero
	 * @param len
	 *            len the length of the required String
	 * @return the middle characters, null if null String input
	 */
	public static String removeMidChar(String sourceString, int pos, int len) {
		return org.apache.commons.lang3.StringUtils.mid(sourceString, pos, len);
	}

	/**
	 * Gets the substring before the first occurrence of a separator
	 * 
	 * @param sourceString
	 *            the String to get a substring from, may be null
	 * @param separator
	 *            the String to search for, may be null
	 * @return the substring before the first occurrence of the separator, null if
	 *         null String input
	 */
	public static String substringBefore(String sourceString, String separator) {
		return org.apache.commons.lang3.StringUtils.substringBefore(sourceString, separator);
	}

	/**
	 * Gets the substring after the first occurrence of a separator
	 * 
	 * @param sourceString
	 *            the String to get a substring from, may be null
	 * @param separator
	 *            the String to search for, may be null
	 * @return substring after the first occurrence of the separator, null if null
	 *         String input
	 */
	public static String substringAfter(String sourceString, String separator) {
		return org.apache.commons.lang3.StringUtils.substringAfter(sourceString, separator);
	}

	/**
	 * Splits the provided text into an array, using whitespace as the separator
	 * 
	 * @param sourceString
	 *            String to parse, may be null
	 * @return array of parsed Strings, null if null String input
	 */
	public static String[] split(String sourceString) {
		return org.apache.commons.lang3.StringUtils.split(sourceString);
	}

	/**
	 * Splits the provided text into an array, separator specified
	 * 
	 * @param sourceString
	 *            the String to parse, may be null
	 * @param separatorChar
	 *            the character used as the delimiter
	 * @return an array of parsed Strings, null if null String input
	 */
	public static String[] split(String sourceString, char separatorChar) {
		return org.apache.commons.lang3.StringUtils.split(sourceString, separatorChar);
	}

	/**
	 * Splits a String by Character type
	 * 
	 * @param sourceString
	 *            the String to split, may be null
	 * @return an array of parsed Strings, null if null String input
	 */
	public static String[] splitByCharacterType(String sourceString) {
		return org.apache.commons.lang3.StringUtils.splitByCharacterType(sourceString);
	}

	/**
	 * Splits a String by Character type
	 * 
	 * @param sourceString
	 *            the String to split, may be null
	 * @return an array of parsed Strings, null if null String input
	 */
	public static String[] splitByCharacterTypeCamelCase(String sourceString) {
		return org.apache.commons.lang3.StringUtils.splitByCharacterTypeCamelCase(sourceString);
	}

	/**
	 * Joins the elements of the provided array into a single String containing the
	 * provided list of elements
	 * 
	 * @param array
	 *            the array of values to join together, may be null
	 * @param separator
	 *            the separator character to use
	 * @return the joined String, null if null array input
	 */
	public static String join(Object[] array, char separator) {
		return org.apache.commons.lang3.StringUtils.join(array, separator);
	}

	/**
	 * Joins the elements of the provided array into a single String containing the
	 * provided list of elements
	 * 
	 * @param array
	 *            the array of values to join together, may be null
	 * @param separator
	 *            the separator character to use
	 * @return the joined String, null if null array input
	 */
	public static String join(long[] array, char separator) {
		return org.apache.commons.lang3.StringUtils.join(array, separator);
	}

	/**
	 * Joins the elements of the provided array into a single String containing the
	 * provided list of elements
	 * 
	 * @param array
	 *            the array of values to join together, may be null
	 * @param separator
	 *            the separator character to use
	 * @return the joined String, null if null array input
	 */
	public static String join(int[] array, char separator) {
		return org.apache.commons.lang3.StringUtils.join(array, separator);
	}

	/**
	 * Joins the elements of the provided array into a single String containing the
	 * provided list of elements
	 * 
	 * @param array
	 *            the array of values to join together, may be null
	 * @param separator
	 *            the separator character to use
	 * @return the joined String, null if null array input
	 */
	public static String join(short[] array, char separator) {
		return org.apache.commons.lang3.StringUtils.join(array, separator);
	}

	/**
	 * Joins the elements of the provided array into a single String containing the
	 * provided list of elements
	 * 
	 * @param array
	 *            the array of values to join together, may be null
	 * @param separator
	 *            the separator character to use
	 * @return the joined String, null if null array input
	 */
	public static String join(byte[] array, char separator) {
		return org.apache.commons.lang3.StringUtils.join(array, separator);
	}

	/**
	 * Joins the elements of the provided array into a single String containing the
	 * provided list of elements
	 * 
	 * @param array
	 *            the array of values to join together, may be null
	 * @param separator
	 *            the separator character to use
	 * @return the joined String, null if null array input
	 */
	public static String join(char[] array, char separator) {
		return org.apache.commons.lang3.StringUtils.join(array, separator);
	}

	/**
	 * Joins the elements of the provided array into a single String containing the
	 * provided list of elements
	 * 
	 * @param array
	 *            the array of values to join together, may be null
	 * @param separator
	 *            the separator character to use
	 * @return the joined String, null if null array input
	 */
	public static String join(float[] array, char separator) {
		return org.apache.commons.lang3.StringUtils.join(array, separator);
	}

	/**
	 * Joins the elements of the provided array into a single String containing the
	 * provided list of elements
	 * 
	 * @param array
	 *            the array of values to join together, may be null
	 * @param separator
	 *            the separator character to use
	 * @return the joined String, null if null array input
	 */
	public static String join(double[] array, char separator) {
		return org.apache.commons.lang3.StringUtils.join(array, separator);
	}

	/**
	 * Joins the elements of the provided array into a single String containing the
	 * provided list of elements
	 * 
	 * @param array
	 *            the array of values to join together, may be null
	 * @param separator
	 *            the separator character to use
	 * @param startIndex
	 *            the first index to start joining from. It is an error to pass in
	 *            an end index past the end of the array
	 * @param endIndex
	 *            the index to stop joining from (exclusive). It is an error to pass
	 *            in an end index past the end of the array
	 * @return the joined String, null if null array input
	 * @throws ArrayIndexOutOfBoundsException
	 *             - if startIndex and endIndex are not valid positions
	 */
	public static String join(Object[] array, char separator, int startIndex, int endIndex) {
		try {
			return org.apache.commons.lang3.StringUtils.join(array, separator, startIndex, endIndex);
		} catch (java.lang.ArrayIndexOutOfBoundsException e) {
			throw new ArrayIndexOutOfBoundsException(
					StringUtilConstants.MOSIP_ARRAY_INDEX_OUT_OF_BOUNDS_ERROR_CODE.getErrorCode(),
					StringUtilConstants.MOSIP_ARRAY_INDEX_OUT_OF_BOUNDS_ERROR_CODE.getErrorMessage(), e.getCause());
		}
	}

	/**
	 * Joins the elements of the provided array into a single String containing the
	 * provided list of elements
	 * 
	 * @param array
	 *            the array of values to join together, may be null
	 * @param separator
	 *            the separator character to use
	 * @param startIndex
	 *            the first index to start joining from. It is an error to pass in
	 *            an end index past the end of the array
	 * @param endIndex
	 *            the index to stop joining from (exclusive). It is an error to pass
	 *            in an end index past the end of the array
	 * @return the joined String, null if null array input
	 * @throws ArrayIndexOutOfBoundsException
	 *             - if startIndex and endIndex are not valid positions
	 */
	public static String join(long[] array, char separator, int startIndex, int endIndex) {
		try {
			return org.apache.commons.lang3.StringUtils.join(array, separator, startIndex, endIndex);
		} catch (java.lang.ArrayIndexOutOfBoundsException e) {
			throw new ArrayIndexOutOfBoundsException(
					StringUtilConstants.MOSIP_ARRAY_INDEX_OUT_OF_BOUNDS_ERROR_CODE.getErrorCode(),
					StringUtilConstants.MOSIP_ARRAY_INDEX_OUT_OF_BOUNDS_ERROR_CODE.getErrorMessage(), e.getCause());
		}
	}

	/**
	 * Joins the elements of the provided array into a single String containing the
	 * provided list of elements
	 * 
	 * @param array
	 *            the array of values to join together, may be null
	 * @param separator
	 *            the separator character to use
	 * @param startIndex
	 *            the first index to start joining from. It is an error to pass in
	 *            an end index past the end of the array
	 * @param endIndex
	 *            the index to stop joining from (exclusive). It is an error to pass
	 *            in an end index past the end of the array
	 * @return the joined String, null if null array input
	 * @throws ArrayIndexOutOfBoundsException
	 *             - if startIndex and endIndex are not valid positions
	 */
	public static String join(int[] array, char separator, int startIndex, int endIndex) {
		try {
			return org.apache.commons.lang3.StringUtils.join(array, separator, startIndex, endIndex);
		} catch (java.lang.ArrayIndexOutOfBoundsException e) {
			throw new ArrayIndexOutOfBoundsException(
					StringUtilConstants.MOSIP_ARRAY_INDEX_OUT_OF_BOUNDS_ERROR_CODE.getErrorCode(),
					StringUtilConstants.MOSIP_ARRAY_INDEX_OUT_OF_BOUNDS_ERROR_CODE.getErrorMessage(), e.getCause());
		}
	}

	/**
	 * Joins the elements of the provided array into a single String containing the
	 * provided list of elements
	 * 
	 * @param array
	 *            the array of values to join together, may be null
	 * @param separator
	 *            the separator character to use
	 * @param startIndex
	 *            the first index to start joining from. It is an error to pass in
	 *            an end index past the end of the array
	 * @param endIndex
	 *            the index to stop joining from (exclusive). It is an error to pass
	 *            in an end index past the end of the array
	 * @return the joined String, null if null array input
	 * @throws ArrayIndexOutOfBoundsException
	 *             - if startIndex and endIndex are not valid positions
	 */
	public static String join(byte[] array, char separator, int startIndex, int endIndex) {
		try {
			return org.apache.commons.lang3.StringUtils.join(array, separator, startIndex, endIndex);
		} catch (java.lang.ArrayIndexOutOfBoundsException e) {
			throw new ArrayIndexOutOfBoundsException(
					StringUtilConstants.MOSIP_ARRAY_INDEX_OUT_OF_BOUNDS_ERROR_CODE.getErrorCode(),
					StringUtilConstants.MOSIP_ARRAY_INDEX_OUT_OF_BOUNDS_ERROR_CODE.getErrorMessage(), e.getCause());
		}
	}

	/**
	 * Joins the elements of the provided array into a single String containing the
	 * provided list of elements
	 * 
	 * @param array
	 *            the array of values to join together, may be null
	 * @param separator
	 *            the separator character to use
	 * @param startIndex
	 *            the first index to start joining from. It is an error to pass in
	 *            an end index past the end of the array
	 * @param endIndex
	 *            the index to stop joining from (exclusive). It is an error to pass
	 *            in an end index past the end of the array
	 * @return the joined String, null if null array input
	 * @throws ArrayIndexOutOfBoundsException
	 *             - if startIndex and endIndex are not valid positions
	 */
	public static String join(short[] array, char separator, int startIndex, int endIndex) {
		try {
			return org.apache.commons.lang3.StringUtils.join(array, separator, startIndex, endIndex);
		} catch (java.lang.ArrayIndexOutOfBoundsException e) {
			throw new ArrayIndexOutOfBoundsException(
					StringUtilConstants.MOSIP_ARRAY_INDEX_OUT_OF_BOUNDS_ERROR_CODE.getErrorCode(),
					StringUtilConstants.MOSIP_ARRAY_INDEX_OUT_OF_BOUNDS_ERROR_CODE.getErrorMessage(), e.getCause());
		}
	}

	/**
	 * Joins the elements of the provided array into a single String containing the
	 * provided list of elements
	 * 
	 * @param array
	 *            the array of values to join together, may be null
	 * @param separator
	 *            the separator character to use
	 * @param startIndex
	 *            the first index to start joining from. It is an error to pass in
	 *            an end index past the end of the array
	 * @param endIndex
	 *            the index to stop joining from (exclusive). It is an error to pass
	 *            in an end index past the end of the array
	 * @return the joined String, null if null array input
	 * @throws ArrayIndexOutOfBoundsException
	 *             - if startIndex and endIndex are not valid positions
	 */
	public static String join(char[] array, char separator, int startIndex, int endIndex) {
		try {
			return org.apache.commons.lang3.StringUtils.join(array, separator, startIndex, endIndex);
		} catch (java.lang.ArrayIndexOutOfBoundsException e) {
			throw new ArrayIndexOutOfBoundsException(
					StringUtilConstants.MOSIP_ARRAY_INDEX_OUT_OF_BOUNDS_ERROR_CODE.getErrorCode(),
					StringUtilConstants.MOSIP_ARRAY_INDEX_OUT_OF_BOUNDS_ERROR_CODE.getErrorMessage(), e.getCause());
		}
	}

	/**
	 * Joins the elements of the provided array into a single String containing the
	 * provided list of elements
	 * 
	 * @param array
	 *            the array of values to join together, may be null
	 * @param separator
	 *            the separator character to use
	 * @param startIndex
	 *            the first index to start joining from. It is an error to pass in
	 *            an end index past the end of the array
	 * @param endIndex
	 *            the index to stop joining from (exclusive). It is an error to pass
	 *            in an end index past the end of the array
	 * @return the joined String, null if null array input
	 * @throws ArrayIndexOutOfBoundsException
	 *             - if startIndex and endIndex are not valid positions
	 */
	public static String join(double[] array, char separator, int startIndex, int endIndex) {
		try {
			return org.apache.commons.lang3.StringUtils.join(array, separator, startIndex, endIndex);
		} catch (java.lang.ArrayIndexOutOfBoundsException e) {
			throw new ArrayIndexOutOfBoundsException(
					StringUtilConstants.MOSIP_ARRAY_INDEX_OUT_OF_BOUNDS_ERROR_CODE.getErrorCode(),
					StringUtilConstants.MOSIP_ARRAY_INDEX_OUT_OF_BOUNDS_ERROR_CODE.getErrorMessage(), e.getCause());
		}
	}

	/**
	 * Joins the elements of the provided array into a single String containing the
	 * provided list of elements
	 * 
	 * @param array
	 *            the array of values to join together, may be null
	 * @param separator
	 *            the separator character to use
	 * @param startIndex
	 *            the first index to start joining from. It is an error to pass in
	 *            an end index past the end of the array
	 * @param endIndex
	 *            the index to stop joining from (exclusive). It is an error to pass
	 *            in an end index past the end of the array
	 * @return the joined String, null if null array input
	 * @throws ArrayIndexOutOfBoundsException
	 *             - if startIndex and endIndex are not valid positions
	 */
	public static String join(float[] array, char separator, int startIndex, int endIndex) {
		try {
			return org.apache.commons.lang3.StringUtils.join(array, separator, startIndex, endIndex);
		} catch (java.lang.ArrayIndexOutOfBoundsException e) {
			throw new ArrayIndexOutOfBoundsException(
					StringUtilConstants.MOSIP_ARRAY_INDEX_OUT_OF_BOUNDS_ERROR_CODE.getErrorCode(),
					StringUtilConstants.MOSIP_ARRAY_INDEX_OUT_OF_BOUNDS_ERROR_CODE.getErrorMessage(), e.getCause());
		}
	}

	/**
	 * Deletes all whitespaces from a String
	 * 
	 * @param sourceString
	 *            the String to delete whitespace from, may be null
	 * @return the String without whitespaces, null if null String input
	 */
	public static String deleteWhitespace(String sourceString) {
		return org.apache.commons.lang3.StringUtils.deleteWhitespace(sourceString);
	}

	/**
	 * Removes all occurrences of a substring from within the source string
	 * 
	 * @param sourceString
	 *            the source String to search, may be null
	 * @param remove
	 *            the String to search for and remove, may be null
	 * @return the substring with the string removed if found, null if null String
	 *         input
	 */
	public static String remove(String sourceString, String remove) {
		return org.apache.commons.lang3.StringUtils.remove(sourceString, remove);
	}

	/**
	 * Case insensitive removal of all occurrences of a substring from within the
	 * source string
	 * 
	 * @param sourceString
	 *            the source String to search, may be null
	 * @param remove
	 *            the String to search for (case insensitive) and remove, may be
	 *            null
	 * @return the substring with the string removed if found, null if null String
	 *         input
	 */
	public static String removeIgnoreCase(String sourceString, String remove) {
		return org.apache.commons.lang3.StringUtils.removeIgnoreCase(sourceString, remove);
	}

	/**
	 * Removes all occurrences of a character from within the source string
	 * 
	 * @param sourceString
	 *            the source String to search, may be null
	 * @param remove
	 *            the char to search for and remove, may be null
	 * @return the substring with the char removed if found, null if null String
	 *         input
	 */
	public static String remove(String sourceString, char remove) {
		return org.apache.commons.lang3.StringUtils.remove(sourceString, remove);
	}

	/**
	 * Removes each substring of the text String that matches the given regular
	 * expression
	 * 
	 * @param text
	 *            text to remove from, may be null
	 * @param regex
	 *            the regular expression to which this string is to be matched
	 * @return the text with any removes processed, null if null String input
	 * @throws PatternSyntaxException
	 *             - if the regular expression's syntax is invalid
	 */
	public static String removeAll(String text, String regex) {
		try {
			return org.apache.commons.lang3.StringUtils.removeAll(text, regex);
		} catch (java.util.regex.PatternSyntaxException e) {
			throw new PatternSyntaxException(StringUtilConstants.MOSIP_PATTERN_SYNTAX_ERROR_CODE.getErrorCode(),
					StringUtilConstants.MOSIP_PATTERN_SYNTAX_ERROR_CODE.getErrorMessage(), e.getCause());
		}
	}

	/**
	 * Replaces a String with another String inside a larger String, once
	 * 
	 * @param text
	 *            text to search and replace in, may be null
	 * @param searchString
	 *            the String to search for, may be null
	 * @param replacement
	 *            the String to replace with, may be null
	 * @return the text with any replacements processed, null if null String input
	 */
	public static String replaceOnce(String text, String searchString, String replacement) {
		return org.apache.commons.lang3.StringUtils.replaceOnce(text, searchString, replacement);
	}

	/**
	 * Case insensitively replaces a String with another String inside a larger
	 * String, once
	 * 
	 * @param text
	 *            text to search and replace in, may be null
	 * @param searchString
	 *            the String to search for (case insensitive), may be null
	 * @param replacement
	 *            the String to replace with, may be null
	 * @return the text with any replacements processed, null if null String input
	 */
	public static String replaceOnceIgnoreCase(String text, String searchString, String replacement) {
		return org.apache.commons.lang3.StringUtils.replaceOnceIgnoreCase(text, searchString, replacement);
	}

	/**
	 * Replaces each substring of the source String that matches the given regular
	 * expression with the given replacement
	 * 
	 * @param source
	 *            the source string
	 * @param regex
	 *            the regular expression to which this string is to be matched
	 * @param replacement
	 *            the string to be substituted for each match
	 * @return The resulting replaced String
	 * @throws PatternSyntaxException
	 *             - if the regular expression's syntax is invalid
	 */
	public static String replacePattern(String source, String regex, String replacement) {
		try {
			return org.apache.commons.lang3.StringUtils.replacePattern(source, regex, replacement);
		} catch (java.util.regex.PatternSyntaxException e) {
			throw new PatternSyntaxException(StringUtilConstants.MOSIP_PATTERN_SYNTAX_ERROR_CODE.getErrorCode(),
					StringUtilConstants.MOSIP_PATTERN_SYNTAX_ERROR_CODE.getErrorMessage(), e.getCause());
		}
	}

	/**
	 * Removes each substring of the source String that matches the given regular
	 * expression
	 * 
	 * @param source
	 *            the source string
	 * @param regex
	 *            the regular expression to which this string is to be matched
	 * @return The resulting String
	 * @throws PatternSyntaxException
	 *             - if the regular expression's syntax is invalid
	 */
	public static String removePattern(String source, String regex) {
		try {
			return org.apache.commons.lang3.StringUtils.removePattern(source, regex);
		} catch (java.util.regex.PatternSyntaxException e) {
			throw new PatternSyntaxException(StringUtilConstants.MOSIP_PATTERN_SYNTAX_ERROR_CODE.getErrorCode(),
					StringUtilConstants.MOSIP_PATTERN_SYNTAX_ERROR_CODE.getErrorMessage(), e.getCause());
		}
	}

	/**
	 * Replaces each substring of the text String that matches the given regular
	 * expression with the given replacement
	 * 
	 * @param text
	 *            text to search and replace in, may be null
	 * @param regex
	 *            the regular expression to which this string is to be matched
	 * @param replacement
	 *            the string to be substituted for each match
	 * @return the text with any replacements processed, null if null String input
	 * @throws PatternSyntaxException
	 *             if the regular expression's syntax is invalid
	 */
	public static String replaceAll(String text, String regex, String replacement) {
		try {
			return org.apache.commons.lang3.StringUtils.replaceAll(text, regex, replacement);
		} catch (java.util.regex.PatternSyntaxException e) {
			throw new PatternSyntaxException(StringUtilConstants.MOSIP_PATTERN_SYNTAX_ERROR_CODE.getErrorCode(),
					StringUtilConstants.MOSIP_PATTERN_SYNTAX_ERROR_CODE.getErrorMessage(), e.getCause());
		}
	}

	/**
	 * Replaces all occurrences of a String within another String
	 * 
	 * @param text
	 *            text to search and replace in, may be null
	 * @param searchString
	 *            the String to search for, may be null
	 * @param replacement
	 *            the String to replace it with, may be null
	 * @return the text with any replacements processed, null if null String input
	 */
	public static String replace(String text, String searchString, String replacement) {
		return org.apache.commons.lang3.StringUtils.replace(text, searchString, replacement);
	}

	/**
	 * Case insensitively replaces all occurrences of a String within another String
	 * 
	 * @param text
	 *            text to search and replace in, may be null
	 * @param searchString
	 *            the String to search for (case insensitive), may be null
	 * @param replacement
	 *            the String to replace it with, may be null
	 * @return the text with any replacements processed, null if null String input
	 */
	public static String replaceIgnoreCase(String text, String searchString, String replacement) {
		return org.apache.commons.lang3.StringUtils.replaceIgnoreCase(text, searchString, replacement);
	}

	/**
	 * Replaces a String with another String inside a larger String, for the first
	 * max values of the search String
	 * 
	 * @param text
	 *            text to search and replace in, may be null
	 * @param searchString
	 *            the String to search for, may be null
	 * @param replacement
	 *            the String to replace it with, may be null
	 * @param max
	 *            maximum number of values to replace, or -1 if no maximum
	 * @return the text with any replacements processed, null if null String input
	 */
	public static String replace(String text, String searchString, String replacement, int max) {
		return org.apache.commons.lang3.StringUtils.replace(text, searchString, replacement, max);
	}

	/**
	 * Case insensitively replaces a String with another String inside a larger
	 * String, for the first max values of the search String
	 * 
	 * @param text
	 *            text to search and replace in, may be null
	 * @param searchString
	 *            the String to search for (case insensitive), may be null
	 * @param replacement
	 *            the String to replace it with, may be null
	 * @param max
	 *            maximum number of values to replace, or -1 if no maximum
	 * @return the text with any replacements processed, null if null String input
	 */
	public static String replaceIgnoreCase(String text, String searchString, String replacement, int max) {
		return org.apache.commons.lang3.StringUtils.replaceIgnoreCase(text, searchString, replacement, max);
	}

	/**
	 * Replaces all occurrences of a character in a String with another
	 * 
	 * @param sourceString
	 *            String to replace characters in, may be null
	 * @param searchChar
	 *            the character to search for, may be null
	 * @param replaceChar
	 *            the character to replace, may be null
	 * @return modified String, null if null string input
	 */
	public static String replaceChars(String sourceString, char searchChar, char replaceChar) {
		return org.apache.commons.lang3.StringUtils.replaceChars(sourceString, searchChar, replaceChar);
	}

	/**
	 * Replaces multiple characters in a String in one go(can also be used to delete
	 * characters)
	 * 
	 * @param sourceString
	 *            String to replace characters in, may be null
	 * @param searchChars
	 *            a set of characters to search for, may be null
	 * @param replaceChars
	 *            a set of characters to replace, may be null
	 * @return modified String, null if null string inputSince:
	 */
	public static String replaceChars(String sourceString, String searchChars, String replaceChars) {
		return org.apache.commons.lang3.StringUtils.replaceChars(sourceString, searchChars, replaceChars);
	}

	/**
	 * Overlays part of a String with another String
	 * 
	 * @param sourceString
	 *            the String to do overlaying in, may be null
	 * @param overlay
	 *            the String to overlay, may be null
	 * @param start
	 *            the position to start overlaying at
	 * @param end
	 *            the position to stop overlaying before
	 * @return overlayed String, null if null String input
	 */
	public static String overlay(String sourceString, String overlay, int start, int end) {
		return org.apache.commons.lang3.StringUtils.overlay(sourceString, overlay, start, end);
	}

	/**
	 * Removes one newline from end of a String if it's there, otherwise leave it
	 * alone
	 * 
	 * @param sourceString
	 *            the String to chomp a newline from, may be null
	 * @return String without newline, null if null String input
	 */
	public static String chomp(String sourceString) {
		return org.apache.commons.lang3.StringUtils.chomp(sourceString);
	}

	/**
	 * Remove the last character from a String
	 * 
	 * @param sourceString
	 *            the String to chop last character from, may be null
	 * @return String without last character, null if null String input
	 */
	public static String chop(String sourceString) {
		return org.apache.commons.lang3.StringUtils.chop(sourceString);
	}

	/**
	 * Repeat a String repeat times to form a new String
	 * 
	 * @param sourceString
	 *            the String to repeat, may be null
	 * @param repeat
	 *            number of times to repeat sourceString, negative treated as zero
	 * @return a new String consisting of the original String repeated, null if null
	 *         String input
	 */
	public static String repeat(String sourceString, int repeat) {
		return org.apache.commons.lang3.StringUtils.repeat(sourceString, repeat);
	}

	/**
	 * Repeat a String repeat times to form a new String, with a String separator
	 * injected each time
	 * 
	 * @param sourceString
	 *            the String to repeat, may be null
	 * @param separator
	 *            the String to inject, may be null
	 * @param repeat
	 *            number of times to repeat sourceString, negative treated as zero
	 * @return a new String consisting of the original String repeated, null if null
	 *         String input
	 */
	public static String repeat(String sourceString, String separator, int repeat) {
		return org.apache.commons.lang3.StringUtils.repeat(sourceString, separator, repeat);
	}

	/**
	 * Returns padding using the specified delimiter repeated to a given length
	 * 
	 * @param ch
	 *            character to repeat
	 * @param repeat
	 *            number of times to repeat char, negative treated as zeroReturns:
	 * @return String with repeated character
	 */
	public static String repeat(char ch, int repeat) {
		return org.apache.commons.lang3.StringUtils.repeat(ch, repeat);
	}

	/**
	 * String with repeated character
	 * 
	 * @param charSeq
	 *            a CharSequence or null
	 * @return CharSequence length or 0 if the CharSequence is null
	 */
	public static int length(CharSequence charSeq) {
		return org.apache.commons.lang3.StringUtils.length(charSeq);
	}

	/**
	 * CharSequence length or 0 if the CharSequence is null
	 * 
	 * @param sourceString
	 *            the String to upper case, may be null
	 * @return the upper cased String, null if null String input
	 */
	public static String upperCase(String sourceString) {
		return org.apache.commons.lang3.StringUtils.upperCase(sourceString);
	}

	/**
	 * Converts a String to upper case
	 * 
	 * @param sourceString
	 *            the String to upper case, may be null
	 * @param locale
	 *            the locale that defines the case transformation rules, must not be
	 *            null
	 * @return the upper cased String, null if null String inputSince:
	 */
	public static String upperCase(String sourceString, Locale locale) {
		return org.apache.commons.lang3.StringUtils.upperCase(sourceString, locale);
	}

	/**
	 * CharSequence length or 0 if the CharSequence is null
	 * 
	 * @param sourceString
	 *            the String to lower case, may be null
	 * @return the lower cased String, null if null String input
	 */
	public static String lowerCase(String sourceString) {
		return org.apache.commons.lang3.StringUtils.lowerCase(sourceString);
	}

	/**
	 * Converts a String to local case
	 * 
	 * @param sourceString
	 *            the String to local case, may be null
	 * @param locale
	 *            the locale that defines the case transformation rules, must not be
	 *            null
	 * @return the local cased String, null if null String inputSince:
	 */
	public static String lowerCase(String sourceString, Locale locale) {
		return org.apache.commons.lang3.StringUtils.lowerCase(sourceString, locale);
	}

	/**
	 * Uncapitalizes a String, changing the first character to lower case
	 * 
	 * @param sourceString
	 *            the String to uncapitalize, may be null
	 * @return the uncapitalized String, null if null String input
	 */
	public static String uncapitalize(String sourceString) {
		return org.apache.commons.lang3.StringUtils.uncapitalize(sourceString);
	}

	/**
	 * Swaps the case of a String changing upper and title case to lower case, and
	 * lower case to upper case
	 * 
	 * @param sourceString
	 *            the String to swap case, may be null
	 * @return the changed String, null if null String input
	 */
	public static String swapCase(String sourceString) {
		return org.apache.commons.lang3.StringUtils.swapCase(sourceString);
	}

	/**
	 * Counts how many times the substring appears in the larger string
	 * 
	 * @param sourceString
	 *            the CharSequence to check, may be null
	 * @param sub
	 *            the substring to count, may be null
	 * @return the number of occurrences, 0 if either CharSequence is null
	 */
	public static int countMatches(CharSequence sourceString, CharSequence sub) {
		return org.apache.commons.lang3.StringUtils.countMatches(sourceString, sub);
	}

	/**
	 * Counts how many times the char appears in the given string
	 * 
	 * @param sourceString
	 *            the CharSequence to check, may be null
	 * @param ch
	 *            the char to count
	 * @return the number of occurrences, 0 if the CharSequence is null
	 */
	public static int countMatches(CharSequence sourceString, char ch) {
		return org.apache.commons.lang3.StringUtils.countMatches(sourceString, ch);
	}

	/**
	 * Checks if the CharSequence contains only Unicode letters
	 * 
	 * @param charSeq
	 *            charSeq the CharSequence to check, may be null
	 * @return true if only contains letters, and is non-null
	 */
	public static boolean isAlpha(CharSequence charSeq) {
		return org.apache.commons.lang3.StringUtils.isAlpha(charSeq);
	}

	/**
	 * Checks if the CharSequence contains only Unicode letters and space (' ')
	 * 
	 * @param charSeq
	 *            the CharSequence to check, may be null
	 * @return true if only contains letters and space, and is non-null
	 */
	public static boolean isAlphaSpace(CharSequence charSeq) {
		return org.apache.commons.lang3.StringUtils.isAlphaSpace(charSeq);
	}

	/**
	 * Checks if the CharSequence contains only Unicode letters or digits
	 * 
	 * @param charSeq
	 *            the CharSequence to check, may be null
	 * @return true if only contains letters or digits, and is non-null
	 */
	public static boolean isAlphanumeric(CharSequence charSeq) {
		return org.apache.commons.lang3.StringUtils.isAlphanumeric(charSeq);
	}

	/**
	 * Checks if the CharSequence contains only Unicode letters, digits or space ('
	 * ')
	 * 
	 * @param charSeq
	 *            the CharSequence to check, may be null
	 * @return true if only contains letters, digits or space, and is non-null
	 */
	public static boolean isAlphanumericSpace(CharSequence charSeq) {
		return org.apache.commons.lang3.StringUtils.isAlphanumericSpace(charSeq);
	}

	/**
	 * Checks if the CharSequence contains only Unicode digits. A decimal point is
	 * not a Unicode digit and returns false
	 * 
	 * @param charSeq
	 *            the CharSequence to check, may be null
	 * @return true if only contains digits, and is non-null
	 */
	public static boolean isNumeric(CharSequence charSeq) {
		return org.apache.commons.lang3.StringUtils.isNumeric(charSeq);
	}

	/**
	 * Checks if the CharSequence contains only Unicode digits or space (' '). A
	 * decimal point is not a Unicode digit and returns false
	 * 
	 * @param charSeq
	 *            the CharSequence to check, may be null
	 * @return true if only contains digits or space, and is non-null
	 */
	public static boolean isNumericSpace(CharSequence charSeq) {
		return org.apache.commons.lang3.StringUtils.isNumericSpace(charSeq);
	}

	/**
	 * Checks if a String sourceString contains Unicode digits, if yes then
	 * concatenate all the digits in sourceString and return it as a String
	 * 
	 * @param sourceString
	 *            the String to extract digits from, may be null
	 * @return String with only digits, or an empty ("") String if no digits found,
	 *         or null String if sourceString is null
	 */
	public static String getDigits(String sourceString) {
		return org.apache.commons.lang3.StringUtils.getDigits(sourceString);
	}

	/**
	 * Checks if the CharSequence contains only whitespace
	 * 
	 * @param charSeq
	 *            the CharSequence to check, may be null
	 * @return true if only contains whitespace, and is non-null
	 */
	public static boolean isWhitespace(CharSequence charSeq) {
		return org.apache.commons.lang3.StringUtils.isWhitespace(charSeq);
	}

	/**
	 * Checks if the CharSequence contains only lowercase characters
	 * 
	 * @param charSeq
	 *            the CharSequence to check, may be null
	 * @return true if only contains lowercase characters, and is non-null
	 */
	public static boolean isAllLowerCase(CharSequence charSeq) {
		return org.apache.commons.lang3.StringUtils.isAllLowerCase(charSeq);
	}

	/**
	 * Checks if the CharSequence contains only uppercase characters
	 * 
	 * @param charSeq
	 *            the CharSequence to check, may be null
	 * @return true if only contains uppercase characters, and is non-null
	 */
	public static boolean isAllUpperCase(CharSequence charSeq) {
		return org.apache.commons.lang3.StringUtils.isAllUpperCase(charSeq);
	}

	/**
	 * Checks if the CharSequence contains mixed casing of both uppercase and
	 * lowercase characters
	 * 
	 * @param charSeq
	 *            the CharSequence to check, may be null
	 * @return true if the CharSequence contains both uppercase and lowercase
	 *         characters
	 */
	public static boolean isMixedCase(CharSequence charSeq) {
		return org.apache.commons.lang3.StringUtils.isMixedCase(charSeq);
	}

	/**
	 * Rotate (circular shift) a String of shift characters
	 * 
	 * @param sourceString
	 *            the String to rotate, may be null
	 * @param shift
	 *            number of time to shift (positive : right shift, negative : left
	 *            shift)
	 * @return the rotated String, or the original String if shift == 0, or null if
	 *         null String input
	 */
	public static String rotate(String sourceString, int shift) {
		return org.apache.commons.lang3.StringUtils.rotate(sourceString, shift);
	}

	/**
	 * Reverses a String
	 * 
	 * @param sourceString
	 *            the String to reverse, may be null
	 * @return the reversed String, null if null String input
	 */
	public static String reverse(String sourceString) {
		return org.apache.commons.lang3.StringUtils.reverse(sourceString);
	}

	/**
	 * Reverses a String that is delimited by a specific character
	 * 
	 * @param sourceString
	 *            the String to reverse, may be null
	 * @param separatorChar
	 *            the separator character to use
	 * @return the reversed String, null if null String input
	 */
	public static String reverseDelimited(String sourceString, char separatorChar) {
		return org.apache.commons.lang3.StringUtils.reverseDelimited(sourceString, separatorChar);
	}

	/**
	 * Abbreviates a String using ellipses
	 * 
	 * @param sourceString
	 *            the String to check, may be null
	 * @param maxWidth
	 *            maximum length of result String, must be at least 4
	 * @return abbreviated String, null if null String input
	 * @throws IllegalArgumentException
	 *             - if the width is too small
	 */
	public static String abbreviate(String sourceString, int maxWidth) {
		try {
			return org.apache.commons.lang3.StringUtils.abbreviate(sourceString, maxWidth);
		} catch (java.lang.IllegalArgumentException e) {
			throw new IllegalArgumentException(
					StringUtilConstants.MOSIP_ILLEGAL_ARGUMENT_ERROR_CODE.getErrorCode(),
					StringUtilConstants.MOSIP_ILLEGAL_ARGUMENT_ERROR_CODE.getErrorMessage(), e.getCause());
		}
	}

	/**
	 * Abbreviates a String using ellipses
	 * 
	 * @param sourceString
	 *            the String to check, may be null
	 * @param offset
	 *            left edge of source String
	 * @param maxWidth
	 *            maximum length of result String, must be at least 4
	 * @return abbreviated String, null if null String input
	 * @throws IllegalArgumentException
	 *             - if the width is too small
	 */
	public static String abbreviate(String sourceString, int offset, int maxWidth) {
		try {
			return org.apache.commons.lang3.StringUtils.abbreviate(sourceString, offset, maxWidth);
		} catch (java.lang.IllegalArgumentException e) {
			throw new IllegalArgumentException(
					StringUtilConstants.MOSIP_ILLEGAL_ARGUMENT_ERROR_CODE.getErrorCode(),
					StringUtilConstants.MOSIP_ILLEGAL_ARGUMENT_ERROR_CODE.getErrorMessage(), e.getCause());
		}
	}

	/**
	 * Abbreviates a String using another given String as replacement marker
	 * 
	 * @param sourceString
	 *            the String to check, may be null
	 * @param abbrevMarker
	 *            the String used as replacement marker
	 * @param maxWidth
	 *            maximum length of result String, must be at least
	 *            abbrevMarker.length + 1
	 * @return abbreviated String, null if null String input
	 * @throws IllegalArgumentException
	 *             - if the width is too small
	 */
	public static String abbreviate(String sourceString, String abbrevMarker, int maxWidth) {
		try {
			return org.apache.commons.lang3.StringUtils.abbreviate(sourceString, abbrevMarker, maxWidth);
		} catch (java.lang.IllegalArgumentException e) {
			throw new IllegalArgumentException(
					StringUtilConstants.MOSIP_ILLEGAL_ARGUMENT_ERROR_CODE.getErrorCode(),
					StringUtilConstants.MOSIP_ILLEGAL_ARGUMENT_ERROR_CODE.getErrorMessage(), e.getCause());
		}
	}

	/**
	 * Abbreviates a String using a given replacement marker
	 * 
	 * @param sourceString
	 *            the String to check, may be null
	 * @param abbrevMarker
	 *            the String used as replacement marker
	 * @param offset
	 *            left edge of source String
	 * @param maxWidth
	 *            maximum length of result String, must be at least 4
	 * @return abbreviated String, null if null String input
	 * @throws IllegalArgumentException
	 *             - if the width is too small
	 */
	public static String abbreviate(String sourceString, String abbrevMarker, int offset, int maxWidth) {
		try {
			return org.apache.commons.lang3.StringUtils.abbreviate(sourceString, abbrevMarker, offset, maxWidth);
		} catch (java.lang.IllegalArgumentException e) {
			throw new IllegalArgumentException(
					StringUtilConstants.MOSIP_ILLEGAL_ARGUMENT_ERROR_CODE.getErrorCode(),
					StringUtilConstants.MOSIP_ILLEGAL_ARGUMENT_ERROR_CODE.getErrorMessage(), e.getCause());
		}
	}

	/**
	 * Abbreviates a String to the length passed, replacing the middle characters
	 * with the supplied replacement String
	 * 
	 * @param sourceString
	 *            the String to check, may be null
	 * @param middle
	 *            the String to replace the middle characters with, may be null
	 * @param length
	 *            the length to abbreviate str to
	 * @return the abbreviated String if the above criteria is met, or the original
	 *         String supplied for abbreviation
	 */
	public static String abbreviateMiddle(String sourceString, String middle, int length) {
		return org.apache.commons.lang3.StringUtils.abbreviateMiddle(sourceString, middle, length);
	}

	/**
	 * Compares two Strings, and returns the portion where they differ
	 * 
	 * @param sourceString1
	 *            the first String, may be null
	 * @param sourceString2
	 *            the second String, may be null
	 * @return the portion of sourceString2 where it differs from sourceString1;
	 *         returns the empty String if they are equal
	 */
	public static String difference(String sourceString1, String sourceString2) {
		return org.apache.commons.lang3.StringUtils.difference(sourceString1, sourceString2);
	}

	/**
	 * Check if a CharSequence starts with a specified prefix
	 * 
	 * @param sourceString
	 *            the CharSequence to check, may be null
	 * @param prefix
	 *            the prefix to find, may be null
	 * @return true if the CharSequence starts with the prefix, case sensitive, or
	 *         both null
	 */
	public static boolean startsWith(CharSequence sourceString, CharSequence prefix) {
		return org.apache.commons.lang3.StringUtils.startsWith(sourceString, prefix);
	}

	/**
	 * Case insensitive check if a CharSequence starts with a specified prefix
	 * 
	 * @param sourceString
	 *            the CharSequence to check, may be null
	 * @param prefix
	 *            the prefix to find, may be null
	 * @return true if the CharSequence starts with the prefix, case insensitive, or
	 *         both null
	 */
	public static boolean startsWithIgnoreCase(CharSequence sourceString, CharSequence prefix) {
		return org.apache.commons.lang3.StringUtils.startsWithIgnoreCase(sourceString, prefix);
	}

	/**
	 * Check if a CharSequence ends with a specified suffix
	 * 
	 * @param sourceString
	 *            the CharSequence to check, may be null
	 * @param suffix
	 *            the suffix to find, may be null
	 * @return true if the CharSequence ends with the suffix, case sensitive, or
	 *         both null
	 */
	public static boolean endsWith(CharSequence sourceString, CharSequence suffix) {
		return org.apache.commons.lang3.StringUtils.endsWith(sourceString, suffix);
	}

	/**
	 * Case insensitive check if a CharSequence ends with a specified suffix
	 * 
	 * @param sourceString
	 *            the CharSequence to check, may be null
	 * @param suffix
	 *            the suffix to find, may be null
	 * @return true if the CharSequence ends with the suffix, case insensitive, or
	 *         both null
	 */
	public static boolean endsWithIgnoreCase(CharSequence sourceString, CharSequence suffix) {
		return org.apache.commons.lang3.StringUtils.endsWithIgnoreCase(sourceString, suffix);
	}

	/**
	 * Returns the argument string with whitespace normalized
	 * 
	 * @param sourceString
	 *            the source String to normalize whitespaces from, may be null
	 * @return the modified string with whitespace normalized, null if null String
	 *         input
	 */
	public static String normalizeSpace(String sourceString) {
		return org.apache.commons.lang3.StringUtils.normalizeSpace(sourceString);
	}

	/**
	 * Appends the suffix to the end of the string if the string does not already
	 * end with any of the suffixes
	 * 
	 * @param sourceString
	 *            The string
	 * @param suffix
	 *            The suffix to append to the end of the string
	 * @param suffixes
	 *            Additional suffixes that are valid terminators
	 * @return A new String if suffix was appended, the same string otherwise
	 */
	public static String appendIfMissing(String sourceString, CharSequence suffix, CharSequence... suffixes) {
		return org.apache.commons.lang3.StringUtils.appendIfMissing(sourceString, suffix, suffixes);
	}

	/**
	 * Appends the suffix to the end of the string if the string does not already
	 * end, case insensitive, with any of the suffixes
	 * 
	 * @param sourceString
	 *            The string
	 * @param suffix
	 *            The suffix to append to the end of the string
	 * @param suffixes
	 *            Additional suffixes that are valid terminators
	 * @return A new String if suffix was appended, the same string otherwise
	 */
	public static String appendIfMissingIgnoreCase(String sourceString, CharSequence suffix, CharSequence... suffixes) {
		return org.apache.commons.lang3.StringUtils.appendIfMissingIgnoreCase(sourceString, suffix, suffixes);
	}

	/**
	 * Prepends the prefix to the start of the string if the string does not already
	 * start with any of the prefixes
	 * 
	 * @param sourceString
	 *            The string
	 * @param prefix
	 *            The prefix to append to the end of the string
	 * @param prefixes
	 *            Additional prefixes that are valid terminators
	 * @return A new String if suffix was appended, the same string otherwise
	 */
	public static String prependIfMissing(String sourceString, CharSequence prefix, CharSequence... prefixes) {
		return org.apache.commons.lang3.StringUtils.prependIfMissing(sourceString, prefix, prefixes);
	}

	/**
	 * Prepends the prefix to the start of the string if the string does not already
	 * start, case insensitive, with any of the prefixes
	 * 
	 * @param sourceString
	 *            The string
	 * @param prefix
	 *            The prefix to prepend to the start of the string
	 * @param prefixes
	 *            Additional prefixes that are valid (optional)
	 * @return A new String if prefix was prepended, the same string otherwise
	 */
	public static String prependIfMissingIgnoreCase(String sourceString, CharSequence prefix,
			CharSequence... prefixes) {
		return org.apache.commons.lang3.StringUtils.prependIfMissingIgnoreCase(sourceString, prefix, prefixes);
	}

	/**
	 * Wraps a string with a char
	 * 
	 * @param sourceString
	 *            the string to be wrapped, may be null
	 * @param wrapWith
	 *            the char that will wrap sourceString
	 * @return the wrapped string, or null if sourceString==null
	 */
	public static String wrap(String sourceString, char wrapWith) {
		return org.apache.commons.lang3.StringUtils.wrap(sourceString, wrapWith);
	}

	/**
	 * Wraps a String with another String
	 * 
	 * @param sourceString
	 *            the String to be wrapper, may be null
	 * @param wrapWith
	 *            the String that will wrap sourceString
	 * @return wrapped String, null if null String input
	 */
	public static String wrap(String sourceString, String wrapWith) {
		return org.apache.commons.lang3.StringUtils.wrap(sourceString, wrapWith);
	}

	/**
	 * Wraps a string with a char if that char is missing from the start or end of
	 * the given string
	 * 
	 * @param sourceString
	 *            the string to be wrapped, may be null
	 * @param wrapWith
	 *            the char that will wrap sourceString
	 * @return the wrapped string, or null if sourceString==null
	 */
	public static String wrapIfMissing(String sourceString, char wrapWith) {
		return org.apache.commons.lang3.StringUtils.wrapIfMissing(sourceString, wrapWith);
	}

	/**
	 * Wraps a string with a string if that string is missing from the start or end
	 * of the given string
	 * 
	 * @param sourceString
	 *            the string to be wrapped, may be null
	 * @param wrapWith
	 *            the char that will wrap sourceString
	 * @return the wrapped string, or null if sourceString==null
	 */
	public static String wrapIfMissing(String sourceString, String wrapWith) {
		return org.apache.commons.lang3.StringUtils.wrapIfMissing(sourceString, wrapWith);
	}

	/**
	 * Unwraps a given string from anther string
	 * 
	 * @param sourceString
	 *            the String to be unwrapped, can be null
	 * @param wrapToken
	 *            the String used to unwrap
	 * @return unwrapped String or the original string if it is not quoted properly
	 *         with the wrapToken
	 */
	public static String unwrap(String sourceString, String wrapToken) {
		return org.apache.commons.lang3.StringUtils.unwrap(sourceString, wrapToken);
	}

	/**
	 * Unwraps a given string from a character
	 * 
	 * @param sourceString
	 *            the String to be unwrapped, can be null
	 * @param wrapChar
	 *            the character used to unwrap
	 * @return unwrapped String or the original string if it is not quoted properly
	 *         with the wrapChar
	 */
	public static String unwrap(String sourceString, char wrapChar) {
		return org.apache.commons.lang3.StringUtils.unwrap(sourceString, wrapChar);
	}

}
