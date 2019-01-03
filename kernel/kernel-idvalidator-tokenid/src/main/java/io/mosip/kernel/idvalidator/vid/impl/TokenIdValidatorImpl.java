/**
 * 
 */
package io.mosip.kernel.idvalidator.vid.impl;

import java.util.regex.Pattern;
import java.util.stream.IntStream;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.idvalidator.exception.InvalidIDException;
import io.mosip.kernel.core.idvalidator.spi.IdValidator;
import io.mosip.kernel.core.util.ChecksumUtils;

import io.mosip.kernel.core.util.StringUtils;
import io.mosip.kernel.idvalidator.vid.constant.TokenIdExceptionConstant;

/**
 * Class to validate the TOKENID
 * 
 * @author M1037462
 * 
 * @since 1.0.0
 */
@Component
public class TokenIdValidatorImpl implements IdValidator<String> {

	@Value("${mosip.kernel.tokenid.length}")
	private int tokenIdLength;

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
	@Value("${mosip.kernel.tokenid.length.sequence-limit}")
	private int sequenceLimit;

	
	/**
	 * Ascending digits which will be checked for sequence in id
	 */
	private static final String SEQ_ASC = "0123456789";

	/**
	 * Descending digits which will be checked for sequence in id
	 */
	private static final String SEQ_DEC = "9876543210";


	// ------------------------------------------

	/**
	 * Method to prepare regular expressions for checking token Id  has only digits.
	 */
	@PostConstruct
	private void TokenIdValidatorImplPostConstruct() {
		numaricRegEx = "\\d{" + tokenIdLength + "}";
		

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
	 * Method used for Validate TOKENID against acceptance Criteria
	 * 
	 * 
	 * @param id
	 *            pass a TOKENID in String format example : String inputFile =
	 *            "426789089018"
	 * @return true if entered TOKENID is valid
	 * 
	 * @throws InvalidIDException
	 *             If entered TOKENID is empty or null.
	 * @throws InvalidIDException
	 *             If entered TOKENID contain any sequential and repeated block of
	 *             number for 2 or more than two digits",
	 * @throws InvalidIDException
	 *             If entered TOKENID length should be specified number of digits.
	 * @throws InvalidIDException
	 *             If entered TOKENID contain any alphanumeric characters
	 * @throws InvalidIDException
	 *             If entered TOKENID should not match with checksum
	 * @throws InvalidIDException
	 *             If entered TOKENID contain Zero or One as first Digit.
	 */

	public boolean validateId(String id) {

		/**
		 * 
		 * Checks the value of TOKENID, It Shouldn't be Null or empty
		 * 
		 */
		if (StringUtils.isEmpty(id)) {
			throw new InvalidIDException(TokenIdExceptionConstant.VID_VAL_INVALID_NULL.getErrorCode(),
					TokenIdExceptionConstant.VID_VAL_INVALID_NULL.getErrorMessage());
		}

		/**
		 * 
		 * Checks the Length of the TOKENID, It Should be specified number of digits
		 * 
		 */
		if (id.length() != tokenIdLength) {
			throw new InvalidIDException(TokenIdExceptionConstant.VID_VAL_ILLEGAL_LENGTH.getErrorCode(),
					TokenIdExceptionConstant.VID_VAL_ILLEGAL_LENGTH.getErrorMessage());
		}

		/**
		 * 
		 * Validate the value of TOKENID, It should not contain any alphanumeric characters
		 * 
		 */
		if (!Pattern.matches(numaricRegEx, id)) {
			throw new InvalidIDException(TokenIdExceptionConstant.VID_VAL_INVALID_DIGITS.getErrorCode(),
					TokenIdExceptionConstant.VID_VAL_INVALID_DIGITS.getErrorMessage());
		}

		/**
		 * 
		 * Validate the value of TOKENID, It should not contain '0' or '1' as the first
		 * digit.
		 * 
		 */

		if (id.charAt(0) == CHAR_ZERO || id.charAt(0) == CHAR_ONE) {
			throw new InvalidIDException(TokenIdExceptionConstant.VID_VAL_INVALID_ZERO_ONE.getErrorCode(),
					TokenIdExceptionConstant.VID_VAL_INVALID_ZERO_ONE.getErrorMessage());
		}

		/**
		 * 
		 * The method isValidId(id) from MosipIDFilter will validate the TOKENID for the
		 * following conditions
		 * 
		 * The TOKENID should not contain any sequential number for 2 or more than two
		 * digits
		 * 
		 * 
		 */
		if (!isValidId(id)) {
			throw new InvalidIDException(TokenIdExceptionConstant.VID_VAL_ILLEGAL_SEQUENCE_REPEATATIVE.getErrorCode(),
					TokenIdExceptionConstant.VID_VAL_ILLEGAL_SEQUENCE_REPEATATIVE.getErrorMessage());
		}

		/**
		 * 
		 * The method validateChecksum(id) from MosipIdChecksum will validate TOKENID by
		 * verifying the checksum
		 * 
		 */
		if (!ChecksumUtils.validateChecksum(id)) {
			throw new InvalidIDException(TokenIdExceptionConstant.VID_VAL_ILLEGAL_CHECKSUM.getErrorCode(),
					TokenIdExceptionConstant.VID_VAL_ILLEGAL_CHECKSUM.getErrorMessage());
		}

		/**
		 * 
		 * once the TOKENID will pass all the acceptance criteria then the method will
		 * return True That is its Valid TOKENID
		 * 
		 * 
		 */
		return true;
	}

	// --------------------------------------------------------------
	/**
	 * Checks if the input id is valid by passing the id through
	 * {@link #sequenceLimit} filter, 
	 * 
	 * @param id
	 *            The input id to validate
	 * @return true if the input id is valid
	 */
	private boolean isValidId(String id) {

		return !(sequenceFilter(id));
	}

	/**
	 * Checks the input id for {@link #SEQUENCE_LIMIT} filter
	 * 
	 * @param id
	 *            The input id to validate
	 * @return true if the id matches the filter
	 */
	private boolean sequenceFilter(String id) {
		return IntStream.rangeClosed(0, id.length() - sequenceLimit).parallel()
				.mapToObj(index -> id.subSequence(index, index + sequenceLimit))
				.anyMatch(idSubSequence -> SEQ_ASC.contains(idSubSequence) || SEQ_DEC.contains(idSubSequence));
	}

	

}
