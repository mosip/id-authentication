/**
 * 
 */
package io.mosip.kernel.idvalidator.uin.impl;

import java.util.regex.Pattern;
import java.util.stream.IntStream;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.idvalidator.exception.InvalidIDException;
import io.mosip.kernel.core.idvalidator.spi.UinValidator;
import io.mosip.kernel.core.util.ChecksumUtils;
import io.mosip.kernel.core.util.StringUtils;
import io.mosip.kernel.idvalidator.uin.constant.UinExceptionConstant;

/**
 * Class for validate the Given UIN in String format
 *
 * @author Megha Tanga
 * 
 * @since 1.0.0
 */

@Component
public class UinValidatorImpl implements UinValidator<String> {

	/**
	 * The length of the UIN is reading from property file
	 */
	@Value("${mosip.kernel.uin.length:-1}")
	private int uinLength;

	/**
	 * This Field to hold regular expressions for checking UIN has only digits.
	 */
	private String numaricRegEx;

	/**
	 * Upper bound of number of digits in sequence allowed in id. For example if
	 * limit is 3, then 12 is allowed but 123 is not allowed in id (in both
	 * ascending and descending order)
	 */
	@Value("${mosip.kernel.uin.length.sequence-limit:-1}")
	private int sequenceLimit;

	/**
	 * Number of digits in repeating block allowed in id. For example if limit is 2,
	 * then 4xxx4 is allowed but 48xxx48 is not allowed in id (x is any digit)
	 */
	@Value("${mosip.kernel.uin.length.repeating-block-limit:-1}")
	private int repeatingBlockLimit;

	/**
	 * Lower bound of number of digits allowed in between two repeating digits in
	 * id. For example if limit is 2, then 11 and 1x1 is not allowed in id (x is any
	 * digit)
	 */
	@Value("${mosip.kernel.uin.length.repeating-limit:-1}")
	private int repeatingLimit;

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

	/**
	 * Method to prepare regular expressions for checking UIN has only digits.
	 */
	@PostConstruct
	private void uinValidatorImplnumaricRegEx() {
		numaricRegEx = "\\d{" + uinLength + "}";

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
	 * Method used for Validate UIN against acceptance Criteria
	 * 
	 * 
	 * @param id
	 *            pass a UIN in String format example : String inputFile =
	 *            "201308710214"
	 * @return boolean True If entered is Valid else it will throw an error
	 * @throws InvalidIDException
	 *             If entered UIN is empty or null.
	 * @throws InvalidIDException
	 *             If entered UIN contain any sequential and repeated block of
	 *             number for 2 or more than two digits",
	 * @throws InvalidIDException
	 *             If entered UIN length should be specified number of digit.
	 * @throws InvalidIDException
	 *             If entered UIN contain any alphanumeric characters
	 * @throws InvalidIDException
	 *             If entered UIN should not match with checksum
	 * @throws InvalidIDException
	 *             If entered UIN contain Zero or One as first Digit.
	 */

	public boolean validateId(String id) {

		/**
		 * 
		 * Check UIN, It Shouldn't be Null or empty
		 * 
		 */
		if (StringUtils.isEmpty(id)) {
			throw new InvalidIDException(UinExceptionConstant.UIN_VAL_INVALID_NULL.getErrorCode(),
					UinExceptionConstant.UIN_VAL_INVALID_NULL.getErrorMessage());
		}
		/**
		 * 
		 * Check the Length of the UIN, It Should be specified number of digits
		 * 
		 */
		if (id.length() != uinLength) {
			throw new InvalidIDException(UinExceptionConstant.UIN_VAL_ILLEGAL_LENGTH.getErrorCode(),
					UinExceptionConstant.UIN_VAL_ILLEGAL_LENGTH.getErrorMessage());
		}
		/**
		 * 
		 * Validation for the UIN should not contain any alphanumeric characters
		 * 
		 */
		if (!Pattern.matches(numaricRegEx, id)) {
			throw new InvalidIDException(UinExceptionConstant.UIN_VAL_INVALID_DIGITS.getErrorCode(),
					UinExceptionConstant.UIN_VAL_INVALID_DIGITS.getErrorMessage());
		}
		/**
		 * 
		 * Validation for the UIN should not contain '0' or '1' as the first digit.
		 * 
		 */
		if (id.charAt(0) == CHAR_ZERO || id.charAt(0) == CHAR_ONE) {
			throw new InvalidIDException(UinExceptionConstant.UIN_VAL_INVALID_ZERO_ONE.getErrorCode(),
					UinExceptionConstant.UIN_VAL_INVALID_ZERO_ONE.getErrorMessage());
		}

		/**
		 * 
		 * The method isValidId(id) from MosipIDFilter will validate the UIN for the
		 * following conditions
		 * 
		 * The UIN should not contain any sequential number for 2 or more than two
		 * digits
		 * 
		 * The UIN should not contain any repeating numbers for 2 or more than two
		 * digits
		 * 
		 * The UIN should not have repeated block of numbers for more than 2 digits
		 * 
		 */
		if (!isValidId(id)) {
			throw new InvalidIDException(UinExceptionConstant.UIN_VAL_ILLEGAL_SEQUENCE_REPEATATIVE.getErrorCode(),
					UinExceptionConstant.UIN_VAL_ILLEGAL_SEQUENCE_REPEATATIVE.getErrorMessage());
		}

		/**
		 * 
		 * The method validateChecksum(id) from MosipIdChecksum will validate
		 * 
		 * Validate the UIN by verifying the checksum
		 * 
		 */
		if (!ChecksumUtils.validateChecksum(id)) {
			throw new InvalidIDException(UinExceptionConstant.UIN_VAL_ILLEGAL_CHECKSUM.getErrorCode(),
					UinExceptionConstant.UIN_VAL_ILLEGAL_CHECKSUM.getErrorMessage());
		}
		/**
		 * 
		 * once the above validation are passed then the method will going to return
		 * True That is its Valid UIN Number
		 * 
		 * 
		 */
		return true;
	}

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

		return !(sequenceFilter(id) || regexFilter(id, repeatingPattern) || regexFilter(id, repeatingBlockPattern));
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

}