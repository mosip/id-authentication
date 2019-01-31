/**
 * 
 */
package io.mosip.kernel.idvalidator.vid.impl;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.idvalidator.exception.InvalidIDException;
import io.mosip.kernel.core.idvalidator.spi.VidValidator;
import io.mosip.kernel.core.util.ChecksumUtils;
import io.mosip.kernel.core.util.StringUtils;
import io.mosip.kernel.idvalidator.vid.constant.VidExceptionConstant;

/**
 * Class to validate the VID
 * 
 * @author M1037462
 * 
 * @since 1.0.0
 */
@Component
public class VidValidatorImpl implements VidValidator<String> {

	@Value("${mosip.kernel.vid.length}")
	private int vidLength;

	/**
	 * This Field to hold regular expressions for checking UIN has only digits.
	 */
	private String numaricRegEx;

	// ---------------------
	/**
	 * Upper bound of number of digits in sequence allowed in id. For example if
	 * limit is 3, then 12 is allowed but 123 is not allowed in id (in both
	 * ascending and descending order)
	 */
	@Value("${mosip.kernel.vid.length.sequence-limit}")
	private int sequenceLimit;

	/**
	 * Number of digits in repeating block allowed in id. For example if limit is 2,
	 * then 4xxx4 is allowed but 48xxx48 is not allowed in id (x is any digit)
	 */
	@Value("${mosip.kernel.vid.length.repeating-block-limit}")
	private int repeatingBlockLimit;

	/**
	 * Lower bound of number of digits allowed in between two repeating digits in
	 * id. For example if limit is 2, then 11 and 1x1 is not allowed in id (x is any
	 * digit)
	 */
	@Value("${mosip.kernel.vid.length.repeating-limit}")
	private int repeatingLimit;

	@Value("#{'${mosip.kernel.vid.not-start-with}'.split(',')}")
	private List<String> notStartWith;

	/**
	 * Ascending digits which will be checked for sequence in id
	 */
	private static final String SEQ_ASC = "0123456789";

	/**
	 * Descending digits which will be checked for sequence in id
	 */
	private static final String SEQ_DEC = "9876543210";

	/**
	 * Compiled regex pattern of {@link #repeatingRegEx}
	 */
	private Pattern repeatingPattern = null;

	/**
	 * Compiled regex pattern of {@link #repeatingBlockRegEx}
	 */
	private Pattern repeatingBlockPattern = null;
	// ------------------------------------------

	/**
	 * Method to prepare regular expressions for checking UIN has only digits.
	 */
	@PostConstruct
	private void vidValidatorImplPostConstruct() {
		/**
		 * Regex for matching repeating digits like 11, 1x1, 1xx1, 1xxx1, etc.<br/>
		 * If repeating digit limit is 2, then <b>Regex:</b> (\d)\d{0,2}\1<br/>
		 * <b>Explanation:</b><br/>
		 * <b>1st Capturing Group (\d)</b><br/>
		 * <b>\d</b> matches a digit (equal to [0-9])<br/>
		 * <b>{0,2} Quantifier</b> — Matches between 0 and 2 times, as many times as
		 * possible, giving back as needed (greedy)<br/>
		 * <b>\1</b> matches the same text as most recently matched by the 1st capturing
		 * group<br/>
		 */
		if (repeatingLimit > 0) {
			String repeatingRegEx = "(\\d)\\d{0," + (repeatingLimit - 1) + "}\\1";
			repeatingPattern = Pattern.compile(repeatingRegEx);
		}
		/**
		 * Regex for matching repeating block of digits like 482xx482, 4827xx4827 (x is
		 * any digit).<br/>
		 * If repeating block limit is 3, then <b>Regex:</b> (\d{3,}).*?\1<br/>
		 * <b>Explanation:</b><br/>
		 * <b>1st Capturing Group (\d{3,})</b><br/>
		 * <b>\d{3,}</b> matches a digit (equal to [0-9])<br/>
		 * <b>{3,}</b> Quantifier — Matches between 3 and unlimited times, as many times
		 * as possible, giving back as needed (greedy)<br/>
		 * <b>.*?</b> matches any character (except for line terminators)<br/>
		 * <b>*?</b> Quantifier — Matches between zero and unlimited times, as few times
		 * as possible, expanding as needed (lazy)<br/>
		 * <b>\1</b> matches the same text as most recently matched by the 1st capturing
		 * group<br/>
		 */
		if (repeatingBlockLimit > 0) {
			String repeatingBlockRegEx = "(\\d{" + repeatingBlockLimit + ",}).*?\\1";
			repeatingBlockPattern = Pattern.compile(repeatingBlockRegEx);
		}

	}

	/**
	 * Field for zero digit
	 */
	private static final char CHAR_ZERO = '0';
	/**
	 * Field for one digit
	 */
	private static final char CHAR_ONE = '1';

	/**
	 * Method used for Validate VID against acceptance Criteria
	 * 
	 * 
	 * @param id
	 *            pass a VID in String format example : String inputFile =
	 *            "426789089018"
	 * @return true if entered VID is valid
	 * 
	 * @throws InvalidIDException
	 *             If entered VID is empty or null.
	 * @throws InvalidIDException
	 *             If entered VID contain any sequential and repeated block of
	 *             number for 2 or more than two digits",
	 * @throws InvalidIDException
	 *             If entered VID length should be specified number of digits.
	 * @throws InvalidIDException
	 *             If entered VID contain any alphanumeric characters
	 * @throws InvalidIDException
	 *             If entered VID should not match with checksum
	 * @throws InvalidIDException
	 *             If entered VID contain Zero or One as first Digit.
	 */

	public boolean validateId(String id) {

		/**
		 * 
		 * Checks the value of VID, It Shouldn't be Null or empty
		 * 
		 */
		if (StringUtils.isEmpty(id)) {
			throw new InvalidIDException(VidExceptionConstant.VID_VAL_INVALID_NULL.getErrorCode(),
					VidExceptionConstant.VID_VAL_INVALID_NULL.getErrorMessage());
		}

		/**
		 * 
		 * Checks the Length of the VID, It Should be specified number of digits
		 * 
		 */
		if (id.length() != vidLength) {
			throw new InvalidIDException(VidExceptionConstant.VID_VAL_ILLEGAL_LENGTH.getErrorCode(),
					VidExceptionConstant.VID_VAL_ILLEGAL_LENGTH.getErrorMessage());
		}

		/**
		 * 
		 * Validate the value of VID, It should not contain any alphanumeric characters
		 * 
		 */
		if (numaricRegEx != null && !Pattern.matches(numaricRegEx, id)) {
			throw new InvalidIDException(VidExceptionConstant.VID_VAL_INVALID_DIGITS.getErrorCode(),
					VidExceptionConstant.VID_VAL_INVALID_DIGITS.getErrorMessage());
		}

		/**
		 * 
		 * Validate the value of VID, It should not contain '0' or '1' as the first
		 * digit.
		 * 
		 */

		if (id.charAt(0) == CHAR_ZERO || id.charAt(0) == CHAR_ONE) {
			//throw new InvalidIDException(VidExceptionConstant.VID_VAL_INVALID_ZERO_ONE.getErrorCode(),
			//		VidExceptionConstant.VID_VAL_INVALID_ZERO_ONE.getErrorMessage());
		}

		/**
		 * 
		 * The method isValidId(id) from MosipIDFilter will validate the VID for the
		 * following conditions
		 * 
		 * The VID should not contain any sequential number for 2 or more than two
		 * digits
		 * 
		 * The VID should not contain any repeating numbers for 2 or more than two
		 * digits
		 * 
		 * The VID should not have repeated block of numbers for more than 2 digits
		 * 
		 */
		if (!isValidId(id)) {
			throw new InvalidIDException(VidExceptionConstant.VID_VAL_ILLEGAL_SEQUENCE_REPEATATIVE.getErrorCode(),
					VidExceptionConstant.VID_VAL_ILLEGAL_SEQUENCE_REPEATATIVE.getErrorMessage());
		}

		/**
		 * 
		 * The method validateChecksum(id) from MosipIdChecksum will validate VID by
		 * verifying the checksum
		 * 
		 */
		if (!ChecksumUtils.validateChecksum(id)) {
			throw new InvalidIDException(VidExceptionConstant.VID_VAL_ILLEGAL_CHECKSUM.getErrorCode(),
					VidExceptionConstant.VID_VAL_ILLEGAL_CHECKSUM.getErrorMessage());
		}

		/**
		 * 
		 * once the VID will pass all the acceptance criteria then the method will
		 * return True That is its Valid VID
		 * 
		 * 
		 */
		return true;
	}

	// --------------------------------------------------------------
	/**
	 * Checks if the input id is valid by passing the id through
	 * {@link #sequenceLimit} filter, {@link #repeatingLimit} filter and
	 * {@link #repeatingBlockLimit} filters
	 * 
	 * @param id
	 *            The input id to validate
	 * @return true if the input id is valid
	 */
	private boolean isValidId(String id) {

		return !(sequenceFilter(id) || regexFilter(id, repeatingPattern) || regexFilter(id, repeatingBlockPattern)
				|| validateNotStartWith(id));
	}

	/**
	 * Checks the input id for {@link #SEQUENCE_LIMIT} filter
	 * 
	 * @param id
	 *            The input id to validate
	 * @return true if the id matches the filter
	 */
	private boolean sequenceFilter(String id) {
		if (sequenceLimit > 0)
			return IntStream.rangeClosed(0, id.length() - sequenceLimit).parallel()
					.mapToObj(index -> id.subSequence(index, index + sequenceLimit))
					.anyMatch(idSubSequence -> SEQ_ASC.contains(idSubSequence) || SEQ_DEC.contains(idSubSequence));
		return false;
	}

	/**
	 * Checks the input id if it matched the given regex pattern
	 * ({@link #repeatingPattern}, {@link #repeatingBlockPattern})
	 * 
	 * @param id
	 *            The input id to validate
	 * @param pattern
	 *            The input regex Pattern
	 * @return true if the id matches the given regex pattern
	 */
	private boolean regexFilter(String id, Pattern pattern) {
		if (pattern != null)
			return pattern.matcher(id).find();
		return false;
	}

	/**
	 * Method to validate that the vid should not contains the specified digit at
	 * first index
	 * 
	 * @param id
	 *            The input id to validate
	 * @return true if found otherwise false
	 */
	private boolean validateNotStartWith(String id) {
		if (notStartWith != null && !notStartWith.isEmpty()) {
			for (String str : notStartWith) {
				if (id.startsWith(str))
					return true;
			}
		}
		return false;
	}

}
