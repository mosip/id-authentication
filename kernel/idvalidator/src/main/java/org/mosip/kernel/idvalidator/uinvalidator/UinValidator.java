package org.mosip.kernel.idvalidator.uinvalidator;

import java.util.regex.Pattern;

import org.mosip.kernel.core.spi.idvalidator.MosipIdValidator;
import org.mosip.kernel.core.utils.MosipIdChecksum;
import org.mosip.kernel.core.utils.MosipIdFilter;
import org.mosip.kernel.idvalidator.exception.MosipInvalidIDException;
import org.mosip.kernel.idvalidator.uinvalidator.constants.MosipIDExceptionCodeConstants;

/**
 * Test class for validate the Given UIN in String format
 *
 * @author Megha Tanga
 * 
 * @since 1.0.0
 */

public class UinValidator implements MosipIdValidator<String> {

	/**
	 * Method used for Validate UIN against acceptance Criteria
	 * 
	 * 
	 * @param id
	 *            pass a UIN in String format example : String inputFile =
	 *            "426789089018"
	 * @return boolean True If entered is Valide else it will throw an error
	 * @throws MosipInvalidIDException
	 *             If entered UIN is empty or null.
	 * @throws MosipInvalidIDException
	 *             If entered UIN contain any sequential and repeated block of
	 *             number for 2 or more than two digits",
	 * @throws MosipInvalidIDException
	 *             If entered UIN length should be 12 digit.
	 * @throws MosipInvalidIDException
	 *             If entered UIN contain any alphanumeric characters
	 * @throws MosipInvalidIDException
	 *             If entered UIN should not match with checksum
	 * @throws MosipInvalidIDException
	 *             If entered UIN contain Zero or One as first Digit.
	 */
	private static final int len = 12;
	private static final String alphaRegex = "\\d{12}";
	private static final char charZero = '0';
	private static final char charOne = '1';

	public boolean validateId(String id) {

		/**
		 * 
		 * Check UIN, It Shouldn't be Null or empty
		 * 
		 */
		if (id == null) {
			throw new MosipInvalidIDException(MosipIDExceptionCodeConstants.UIN_VAL_INVALID_NULL.getErrorCode(),
					MosipIDExceptionCodeConstants.UIN_VAL_INVALID_NULL.getErrorMessage());
		}
		/**
		 * 
		 * Check the Length of the UIN, It Should be 12 Digit
		 * 
		 */
		if (id.length() != len) {
			throw new MosipInvalidIDException(MosipIDExceptionCodeConstants.UIN_VAL_ILLEGAL_LENGTH.getErrorCode(),
					MosipIDExceptionCodeConstants.UIN_VAL_ILLEGAL_LENGTH.getErrorCode());
		}
		/**
		 * 
		 * Validation for the UIN should not contain any alphanumeric characters
		 * 
		 */
		if (!Pattern.matches(alphaRegex, id)) {
			throw new MosipInvalidIDException(MosipIDExceptionCodeConstants.UIN_VAL_INVALID_DIGITS.getErrorCode(),
					MosipIDExceptionCodeConstants.UIN_VAL_INVALID_DIGITS.getErrorMessage());
		}
		/**
		 * 
		 * Validation for the UIN should not contain '0' or '1' as the first digit.
		 * 
		 */
		if (id.charAt(0) == charZero || id.charAt(0) == charOne) {
			throw new MosipInvalidIDException(MosipIDExceptionCodeConstants.UIN_VAL_INVALID_ZERO_ONE.getErrorCode(),
					MosipIDExceptionCodeConstants.UIN_VAL_INVALID_ZERO_ONE.getErrorMessage());
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
		if (MosipIdFilter.isValidId(id)) {
			throw new MosipInvalidIDException(
					MosipIDExceptionCodeConstants.UIN_VAL_ILLEGAL_SEQUENCE_REPEATATIVE.getErrorCode(),
					MosipIDExceptionCodeConstants.UIN_VAL_ILLEGAL_SEQUENCE_REPEATATIVE.getErrorMessage());
		}

		/**
		 * 
		 * The method validateChecksum(id) from MosipIdChecksum will validate
		 * 
		 * Validate the UIN by verifying the checksum
		 * 
		 */
		if (!MosipIdChecksum.validateChecksum(id)) {
			throw new MosipInvalidIDException(MosipIDExceptionCodeConstants.UIN_VAL_ILLEGAL_CHECKSUM.getErrorCode(),
					MosipIDExceptionCodeConstants.UIN_VAL_ILLEGAL_CHECKSUM.getErrorMessage());
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

}