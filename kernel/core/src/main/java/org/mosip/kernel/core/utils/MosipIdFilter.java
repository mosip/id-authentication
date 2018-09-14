/**
 * 
 */
package org.mosip.kernel.core.utils;

import java.util.regex.Pattern;
import java.util.stream.IntStream;

/**
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
public class MosipIdFilter {

	/**
	 * Private constructor for MosipIdFilter
	 */
	private MosipIdFilter() {
	}

	/**
	 * Number of repeating digits allowed in id. For example if limit is 3, then 111
	 * is allowed but 1111 is not allowed in id
	 */
	private static final int REPEATING_LIMIT = 2;

	/**
	 * Number of digits in sequence allowed in id. For example if limit is 3, then
	 * 123 is allowed but 1234 is not allowed in id (in both ascending and
	 * descending order)
	 */
	private static final int SEQUENCE_LIMIT = 2;

	/**
	 * Number of digits in repeating block allowed in id. For example if limit is 3,
	 * then 482xx482 is allowed but 4827xx4827 is not allowed in id (x is any digit)
	 */
	private static final int REPEATING_BLOCK_LIMIT = 2;

	/**
	 * Ascending digits which will be checked for sequence in id
	 */
	private static final String SEQ_ASC = "0123456789";

	/**
	 * Descending digits which will be checked for sequence in id
	 */
	private static final String SEQ_DEC = "9876543210";

	/**
	 * Regex for matching repeating digits like 111, 1111.<br/>
	 * If number of allowed repeating digit is 2, then <b>Regex:</b>
	 * (?=(\d))\1{2,}<br/>
	 * <b>Explanation:</b><br/>
	 * <b>Positive Lookahead (?=(\d))</b> — Assert that the Regex below matches<br/>
	 * <b>1st Capturing Group (\d)</b><br/>
	 * <b>\d</b> matches a digit (equal to [0-9])<br/>
	 * <b>\1{2,}</b> matches the same text as most recently matched by the 1st
	 * capturing group<br/>
	 * <b>{2,} Quantifier</b> — Matches between 2 and unlimited times, as many times
	 * as possible, giving back as needed (greedy)
	 */
	private static final String REPEATING_REGEX = "(?=(\\d))\\1{" + REPEATING_LIMIT + ",}";

	/**
	 * Regex for matching repeating block of digits like 482xx482, 4827xx4827 (x is
	 * any digit).<br/>
	 * If number of allowed repeating block of digit is 2, then <b>Regex:</b>
	 * (\d{2,}).*?\1<br/>
	 * <b>Explanation:</b><br/>
	 * <b>1st Capturing Group (\d{2,})</b><br/>
	 * <b>\d{2,}</b> matches a digit (equal to [0-9])<br/>
	 * <b>{2,}</b> Quantifier — Matches between 2 and unlimited times, as many times
	 * as possible, giving back as needed (greedy)<br/>
	 * <b>.*?</b> matches any character (except for line terminators)<br/>
	 * <b>*?</b> Quantifier — Matches between zero and unlimited times, as few times
	 * as possible, expanding as needed (lazy)<br/>
	 * <b>\1</b> matches the same text as most recently matched by the 1st capturing
	 * group<br/>
	 */
	private static final String REPEATING_BLOCK_REGEX = "(\\d{" + REPEATING_BLOCK_LIMIT + ",}).*?\\1";

	/**
	 * Compiled regex pattern of {@link #REPEATING_REGEX}
	 */
	private static final Pattern REPEATING_PATTERN = Pattern.compile(REPEATING_REGEX);

	/**
	 * Compiled regex pattern of {@link #REPEATING_BLOCK_REGEX}
	 */
	private static final Pattern REPEATING_BLOCK_PATTERN = Pattern.compile(REPEATING_BLOCK_REGEX);

	/**
	 * Checks if the input id is valid by passing the id through
	 * {@link #SEQUENCE_LIMIT} filter, {@link #REPEATING_LIMIT} filter and
	 * {@link #REPEATING_BLOCK_LIMIT} filters
	 * 
	 * @param id
	 *            The input id to validate
	 * @return true if the input id is valid
	 */
	public static boolean isValidId(String id) {
		return !(sequenceFilter(id) && regexFilter(id, REPEATING_PATTERN) && regexFilter(id, REPEATING_BLOCK_PATTERN));
	}

	/**
	 * Checks the input id for {@link #SEQUENCE_LIMIT} filter
	 * 
	 * @param id
	 *            The input id to validate
	 * @return true if the id matches the filter
	 */
	private static boolean sequenceFilter(String id) {
		return IntStream.rangeClosed(0, id.length() - SEQUENCE_LIMIT).parallel()
				.mapToObj(index -> id.subSequence(index, index + SEQUENCE_LIMIT))
				.anyMatch((idSubSequence -> SEQ_ASC.contains(idSubSequence) || SEQ_DEC.contains(idSubSequence)));
	}

	/**
	 * Checks the input id if it matched the given regex pattern
	 * ({@link #REPEATING_PATTERN}, {@link #REPEATING_BLOCK_PATTERN})
	 * 
	 * @param id
	 *            The input id to validate
	 * @param pattern
	 *            The input regex Pattern
	 * @return true if the id matches the given regex pattern
	 */
	private static boolean regexFilter(String id, Pattern pattern) {
		return pattern.matcher(id).find();
	}

}
