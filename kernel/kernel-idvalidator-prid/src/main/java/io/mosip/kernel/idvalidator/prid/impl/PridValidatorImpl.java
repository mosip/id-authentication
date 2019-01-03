
package io.mosip.kernel.idvalidator.prid.impl;

import java.util.regex.Pattern;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.idvalidator.exception.InvalidIDException;
import io.mosip.kernel.core.idvalidator.spi.PridValidator;
import io.mosip.kernel.core.util.ChecksumUtils;
import io.mosip.kernel.core.util.StringUtils;
import io.mosip.kernel.idvalidator.prid.constant.PridExceptionConstant;

/**
 * Class to validate the Given PRID in String format
 *
 * @author M1037462
 * @author Abhishek Kumar
 * 
 * @since 1.0.0
 */
@Component
public class PridValidatorImpl implements PridValidator<String> {

	/**
	 * This Field to hold the length of PRID Reading length of PRID from property
	 * file
	 */
	@Value("${mosip.kernel.prid.length:-1}")
	private int pridLength;

	/**
	 * Upper bound of number of digits in sequence allowed in id. For example if
	 * limit is 3, then 12 is allowed but 123 is not allowed in id (in both
	 * ascending and descending order)
	 */
	@Value("${mosip.kernel.prid.sequence-limit:-1}")
	private int sequenceLimit;

	/**
	 * Number of digits in repeating block allowed in id. For example if limit is 2,
	 * then 4xxx4 is allowed but 48xxx48 is not allowed in id (x is any digit)
	 */
	@Value("${mosip.kernel.prid.repeating-block-limit:-1}")
	private int blockLimit;

	/**
	 * Lower bound of number of digits allowed in between two repeating digits in
	 * id. For example if limit is 2, then 11 and 1x1 is not allowed in id (x is any
	 * digit)
	 */
	@Value("${mosip.kernel.prid.repeating-limit:-1}")
	private int repeatLimit;

	/**
	 * Field for zero digit
	 */
	private static final char CHAR_ZERO = '0';

	/**
	 * Field for one digit
	 */
	private static final char CHAR_ONE = '1';

	/**
	 * Ascending digits which will be checked for sequence in id
	 */
	private static final String SEQ_ASC = "0123456789";

	/**
	 * Descending digits which will be checked for sequence in id
	 */
	private static final String SEQ_DEC = "9876543210";

	/**
	 * Compiled regex pattern for
	 */
	private Pattern repeatingPattern = null;

	/**
	 * Compiled regex pattern of {@link #repeatingBlockRegex}
	 */
	private Pattern repeatingBlockpattern = null;

	/**
	 * Method used to validate PRID against acceptance Criteria
	 * 
	 * @param id
	 *            the id to validate, pass a PRID in String format example : String
	 *            inputFile = "426789089018"
	 * @throws InvalidIDException
	 *             If entered PRID is empty or null.
	 * @throws InvalidIDException
	 *             If entered PRID contain any sequential and repeated block of
	 *             number for 2 or more than two digits",
	 * @throws InvalidIDException
	 *             If entered PRID length should be 14 digit.
	 * @throws InvalidIDException
	 *             If entered PRID contain any alphanumeric characters
	 * @throws InvalidIDException
	 *             If entered PRID should not match with checksum
	 * @throws InvalidIDException
	 *             If entered PRID contain Zero or One as first Digit.
	 */

	public boolean validateId(String id) {
		validateInputs(id, pridLength, sequenceLimit, repeatLimit, blockLimit);
		return true;
	}

	/**
	 * Method used to validate PRID against acceptance Criteria
	 * 
	 * 
	 * @param id
	 *            pass a PRID in String format example : String inputFile =
	 *            "426789089018"@param pridLength prid length to validate
	 * @param sequenceLimit
	 *            sequence in prid to limit
	 * @param repeatingLimit
	 *            repeating limit
	 * @param blockLimit
	 *            repeating block limit
	 * @throws InvalidIDException
	 *             If entered PRID is empty or null.
	 * @throws InvalidIDException
	 *             If entered PRID contain any sequential and repeated block of
	 *             number for 2 or more than two digits",
	 * @throws InvalidIDException
	 *             If entered PRID length should be 14 digit.
	 * @throws InvalidIDException
	 *             If entered PRID contain any alphanumeric characters
	 * @throws InvalidIDException
	 *             If entered PRID should not match with checksum
	 * @throws InvalidIDException
	 *             If entered PRID contain Zero or One as first Digit.
	 */

	public boolean validateId(String id, int pridLength, int sequenceLimit, int repeatingLimit, int blockLimit) {
		validateInputs(id, pridLength, sequenceLimit, repeatingLimit, blockLimit);
		return true;
	}

	/**
	 * Method to validate prid with given inputs
	 * 
	 * @param id
	 *            pass a PRID in String format example : String inputFile =
	 *            "426789089018"@param pridLength prid length to validate
	 * @param pridLength
	 *            the length of the prid
	 * @param sequenceLimit
	 *            sequence in prid to limit
	 * @param repeatLimit
	 *            repeating limit
	 * @param blockLimit
	 *            repeating block limit
	 */
	private void validateInputs(String id, int pridLength, int sequenceLimit, int repeatLimit, int blockLimit) {
		if (pridLength <= 0 || sequenceLimit <= 0 || repeatLimit <= 0 || blockLimit <= 0) {
			throw new InvalidIDException(PridExceptionConstant.PRID_VAL_INVALID_VALUE.getErrorCode(),
					PridExceptionConstant.PRID_VAL_INVALID_VALUE.getErrorMessage());
		}

		/**
		 * 
		 * Check PRID, It Shouldn't be Null or empty
		 * 
		 */
		if (StringUtils.isEmpty(id)) {
			throw new InvalidIDException(PridExceptionConstant.PRID_VAL_INVALID_NULL.getErrorCode(),
					PridExceptionConstant.PRID_VAL_INVALID_NULL.getErrorMessage());
		}

		/**
		 * 
		 * Check the Length of the PRID, It Should be specified number of digits
		 * 
		 */

		if (id.length() != pridLength) {
			throw new InvalidIDException(PridExceptionConstant.PRID_VAL_ILLEGAL_LENGTH.getErrorCode(),
					PridExceptionConstant.PRID_VAL_ILLEGAL_LENGTH.getErrorMessage());
		}

		/**
		 * 
		 * Validate the PRID, It should not contain any alphanumeric characters
		 * 
		 */
		String numaricRegEx = "\\d{" + pridLength + "}";
		if (!Pattern.matches(numaricRegEx, id)) {
			throw new InvalidIDException(PridExceptionConstant.PRID_VAL_INVALID_DIGITS.getErrorCode(),
					PridExceptionConstant.PRID_VAL_INVALID_DIGITS.getErrorMessage());
		}
		/**
		 * 
		 * Validate the PRID, It should not contain '0' or '1' as the first digit.
		 * 
		 */

		if (id.charAt(0) == CHAR_ZERO || id.charAt(0) == CHAR_ONE) {
			throw new InvalidIDException(PridExceptionConstant.PRID_VAL_INVALID_ZERO_ONE.getErrorCode(),
					PridExceptionConstant.PRID_VAL_INVALID_ZERO_ONE.getErrorMessage());
		}

		/**
		 *
		 * The method isValidId(id) from IDFilter will validate the PRID for the
		 * following conditions
		 *
		 * The PRID should not contain any sequential number for 2 or more than two
		 * digits
		 *
		 * The PRID should not contain any repeating numbers for 2 or more than two
		 * digits
		 *
		 * The PRID should not have repeated block of numbers for more than 2 digits
		 *
		 */
		if (!isValidId(id, sequenceLimit, repeatLimit, blockLimit)) {
			throw new InvalidIDException(PridExceptionConstant.PRID_VAL_ILLEGAL_SEQUENCE_REPEATATIVE.getErrorCode(),
					PridExceptionConstant.PRID_VAL_ILLEGAL_SEQUENCE_REPEATATIVE.getErrorMessage());
		}
		/**
		 * 
		 * The method validateChecksum(id) from IdChecksum will validate
		 * 
		 * Validate the PRID by verifying the checksum
		 * 
		 */

		if (!ChecksumUtils.validateChecksum(id)) {
			throw new InvalidIDException(PridExceptionConstant.PRID_VAL_ILLEGAL_CHECKSUM.getErrorCode(),
					PridExceptionConstant.PRID_VAL_ILLEGAL_CHECKSUM.getErrorMessage());
		}
	}

	public void initializeRegEx(int repeatingLimit, int repeatingBlockLimit) {

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
		String repeatingRegEx = "(\\d)\\d{0," + (repeatingLimit - 1) + "}\\1";

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
		String repeatingBlockRegex = "(\\d{" + repeatingBlockLimit + ",}).*?\\1";

		/**
		 * Compiled regex pattern of {@link #repeatingRegEx}
		 */
		repeatingPattern = Pattern.compile(repeatingRegEx);
		/**
		 * Compiled regex pattern of {@link #repeatingBlockRegex}
		 */
		repeatingBlockpattern = Pattern.compile(repeatingBlockRegex);
	}

	/**
	 * Checks if the input id is valid by passing the id through
	 * {@link #sequenceLimit} filter, {@link #repeatLimit} filter and
	 * {@link #blockLimit} filters
	 * 
	 * @param id
	 *            The input id to validate
	 * 
	 * @param sequenceLimit
	 *            sequence in prid to limit
	 * @param repeatingLimit
	 *            repeating limit
	 * @param repeatingBlockLimit
	 *            repeating block limit
	 * @return true if the input id is valid
	 */
	private boolean isValidId(String id, int sequenceLimit, int repeatingLimit, int repeatingBlockLimit) {
		initializeRegEx(repeatingLimit, repeatingBlockLimit);
		return !(sequenceFilter(id, sequenceLimit) || regexFilter(id, repeatingPattern)
				|| regexFilter(id, repeatingBlockpattern));
	}

	/**
	 * Checks the input id for {@link #sequenceLimit} filter
	 * 
	 * @param id
	 *            The input id to validate
	 * @return true if the id matches the filter
	 */
	private boolean sequenceFilter(String id, int sequenceLimit) {
		return IntStream.rangeClosed(0, id.length() - sequenceLimit).parallel()
				.mapToObj(index -> id.subSequence(index, index + sequenceLimit))
				.anyMatch(idSubSequence -> SEQ_ASC.contains(idSubSequence) || SEQ_DEC.contains(idSubSequence));
	}

	/**
	 * Checks the input id if it matched the given regex pattern
	 * ({@link #REPEATING_PATTERN}, {@link #repeatingBlockpattern})
	 * 
	 * @param id
	 *            The input id to validate
	 * @param pattern
	 *            The input regex Pattern
	 * @return true if the id matches the given regex pattern
	 */
	private boolean regexFilter(String id, Pattern pattern) {
		return pattern.matcher(id).find();
	}

}
