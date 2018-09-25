/**
 * 
 */
package org.mosip.kernel.idvalidator.uinvalidator;

import java.util.regex.Pattern;

import org.mosip.kernel.core.security.exception.MosipInvalidDataException;
import org.mosip.kernel.core.security.exception.MosipInvalidKeyException;
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
	 * @return Processed array
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
	private static final int length = 12;
	private static final String alphanumericRegex = "\\d{12}";
	private static final char zero = '0';
	private static final char one = '1';

	public boolean validateId(String id) throws MosipInvalidIDException {

		/**
		 * 
		 * Check UIN, It Shouldn't be Null or empty
		 * 
		 */
		if (id == null) {
			throw new MosipInvalidIDException(MosipIDExceptionCodeConstants.UIN_VAL_INVALID_NULL,
					MosipIDExceptionCodeConstants.UIN_VAL_INVALID_NULL);
		}
		/**
		 * 
		 * Check the Length of the UIN, It Should be 12 Digit
		 * 
		 */
		if (id.length() != length) {
			throw new MosipInvalidIDException(MosipIDExceptionCodeConstants.UIN_VAL_ILLEGAL_LENGTH,
					MosipIDExceptionCodeConstants.UIN_VAL_ILLEGAL_LENGTH);
		}
		/**
		 * 
		 * Validation for the UIN should not contain any alphanumeric characters
		 * 
		 */
		if (!Pattern.matches(alphanumericRegex, id)) {
			throw new MosipInvalidIDException(MosipIDExceptionCodeConstants.UIN_VAL_INVALID_DIGITS,
					MosipIDExceptionCodeConstants.UIN_VAL_INVALID_DIGITS);
		}
		/**
		 * 
		 * Validation for the UIN should not contain '0' or '1' as the first digit.
		 * 
		 */
		if (id.charAt(0) == zero && id.charAt(0) == one) {
			throw new MosipInvalidIDException(MosipIDExceptionCodeConstants.UIN_VAL_INVALID_ZERO_ONE,
					MosipIDExceptionCodeConstants.UIN_VAL_INVALID_ZERO_ONE);
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
			throw new MosipInvalidIDException(MosipIDExceptionCodeConstants.UIN_VAL_ILLEGAL_SEQUENCE_REPEATATIVE,
					MosipIDExceptionCodeConstants.UIN_VAL_ILLEGAL_SEQUENCE_REPEATATIVE);
		}

		/**
		 * 
		 * The method validateChecksum(id) from MosipIdChecksum will validate
		 * 
		 * Validate the UIN by verifying the checksum
		 * 
		 */
		if (!MosipIdChecksum.validateChecksum(id)) {
			throw new MosipInvalidIDException(MosipIDExceptionCodeConstants.UIN_VAL_ILLEGAL_CHECKSUM,
					MosipIDExceptionCodeConstants.UIN_VAL_ILLEGAL_CHECKSUM);
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