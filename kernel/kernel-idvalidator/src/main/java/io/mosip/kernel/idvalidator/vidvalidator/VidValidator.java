/**
 * 
 */
package io.mosip.kernel.idvalidator.vidvalidator;

import java.util.regex.Pattern;

import io.mosip.kernel.core.spi.idvalidator.MosipIdValidator;
import io.mosip.kernel.core.util.ChecksumUtils;
import io.mosip.kernel.core.util.IdFilterUtils;

import io.mosip.kernel.idvalidator.exception.MosipInvalidIDException;
import io.mosip.kernel.idvalidator.vidvalidator.constants.MosipVidExceptionCodeConstants;

public class VidValidator implements MosipIdValidator<String> {

	/**
	 * Class to validate the VID
	 * 
	 * @author M1037462
	 * 
	 * @since 1.0.0
	 */
	private static final int VID_LENGTH = 16;
	private static final String NUMERIC_REGEX = "\\d{16}";

	/**
	 * Method used for Validate VID against acceptance Criteria
	 * 
	 * 
	 * @param id
	 *            pass a VID in String format example : String inputFile =
	 *            "426789089018"
	 * @return true if entered VID is valid
	 * @throws MosipInvalidIDException
	 *             If entered VID is empty or null.
	 * @throws MosipInvalidIDException
	 *             If entered VID contain any sequential and repeated block of
	 *             number for 2 or more than two digits",
	 * @throws MosipInvalidIDException
	 *             If entered VID length should be 16 digits.
	 * @throws MosipInvalidIDException
	 *             If entered VID contain any alphanumeric characters
	 * @throws MosipInvalidIDException
	 *             If entered VID should not match with checksum
	 * @throws MosipInvalidIDException
	 *             If entered VID contain Zero or One as first Digit.
	 */

	public boolean validateId(String id) {

		/**
		 * 
		 * Checks the value of VID, It Shouldn't be Null or empty
		 * 
		 */
		if (id == null) {
			throw new MosipInvalidIDException(MosipVidExceptionCodeConstants.VID_VAL_INVALID_NULL.getErrorCode(),
					MosipVidExceptionCodeConstants.VID_VAL_INVALID_NULL.getErrorMessage());
		}

		/**
		 * 
		 * Checks the Length of the VID, It Should be of 16 Digits
		 * 
		 */
		if (id.length() != VID_LENGTH) {
			throw new MosipInvalidIDException(MosipVidExceptionCodeConstants.VID_VAL_ILLEGAL_LENGTH.getErrorCode(),
					MosipVidExceptionCodeConstants.VID_VAL_ILLEGAL_LENGTH.getErrorMessage());
		}

		/**
		 * 
		 * Validate the value of VID, It should not contain any alphanumeric characters
		 * 
		 */
		if (!Pattern.matches(NUMERIC_REGEX, id)) {
			throw new MosipInvalidIDException(MosipVidExceptionCodeConstants.VID_VAL_INVALID_DIGITS.getErrorCode(),
					MosipVidExceptionCodeConstants.VID_VAL_INVALID_DIGITS.getErrorMessage());
		}

		/**
		 * 
		 * Validate the value of VID, It should not contain '0' or '1' as the first
		 * digit.
		 * 
		 */

		if (id.charAt(0) == '0' || id.charAt(0) == '1') {
			throw new MosipInvalidIDException(MosipVidExceptionCodeConstants.VID_VAL_INVALID_ZERO_ONE.getErrorCode(),
					MosipVidExceptionCodeConstants.VID_VAL_INVALID_ZERO_ONE.getErrorMessage());
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
		if (!IdFilterUtils.isValidId(id)) {
			throw new MosipInvalidIDException(
					MosipVidExceptionCodeConstants.VID_VAL_ILLEGAL_SEQUENCE_REPEATATIVE.getErrorCode(),
					MosipVidExceptionCodeConstants.VID_VAL_ILLEGAL_SEQUENCE_REPEATATIVE.getErrorMessage());
		}

		/**
		 * 
		 * The method validateChecksum(id) from MosipIdChecksum will validate VID by
		 * verifying the checksum
		 * 
		 */
		if (!ChecksumUtils.validateChecksum(id)) {
			throw new MosipInvalidIDException(MosipVidExceptionCodeConstants.VID_VAL_ILLEGAL_CHECKSUM.getErrorCode(),
					MosipVidExceptionCodeConstants.VID_VAL_ILLEGAL_CHECKSUM.getErrorMessage());
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

}
