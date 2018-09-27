/**
 * 
 */
package org.mosip.kernel.idvalidator.pridvalidator;

import java.util.regex.Pattern;

import org.mosip.kernel.core.spi.idvalidator.MosipIdValidator;
import org.mosip.kernel.core.utils.MosipIdChecksum;
import org.mosip.kernel.core.utils.MosipIdFilter;
import org.mosip.kernel.idvalidator.exception.MosipInvalidIDException;
import org.mosip.kernel.idvalidator.pridvalidator.constants.MosipPridExceptionConstants;

public class PridValidator implements MosipIdValidator<String> {

	/**
	 * Class to validate the Given PRID in String format
	 *
	 * @author M1037462
	 * 
	 * @since 1.0.0
	 */
	private static final int LEN = 14;
	private static final String ISNUMERIC_REGEX = "\\d{14}";

	/**
	 * Method used to validate PRID against acceptance Criteria
	 * 
	 * 
	 * @param id
	 *            pass a PRID in String format example : String inputFile =
	 *            "426789089018"
	 * @return true if Id is valid
	 * @throws MosipInvalidIDException
	 *             If entered PRID is empty or null.
	 * @throws MosipInvalidIDException
	 *             If entered PRID contain any sequential and repeated block of
	 *             number for 2 or more than two digits",
	 * @throws MosipInvalidIDException
	 *             If entered PRID length should be 14 digit.
	 * @throws MosipInvalidIDException
	 *             If entered PRID contain any alphanumeric characters
	 * @throws MosipInvalidIDException
	 *             If entered PRID should not match with checksum
	 * @throws MosipInvalidIDException
	 *             If entered PRID contain Zero or One as first Digit.
	 */

	public boolean validateId(String id) {

		/**
		 * 
		 * Check PRID, It Shouldn't be Null or empty
		 * 
		 */
		if (id == null) {
			throw new MosipInvalidIDException(MosipPridExceptionConstants.PRID_VAL_INVALID_NULL.getErrorCode(),
					MosipPridExceptionConstants.PRID_VAL_INVALID_NULL.getErrorMessage());
		}

		/**
		 * 
		 * Check the Length of the PRID, It Should be 14 Digit
		 * 
		 */

		if (id.length() != LEN) {
			throw new MosipInvalidIDException(MosipPridExceptionConstants.PRID_VAL_ILLEGAL_LENGTH.getErrorCode(),
					MosipPridExceptionConstants.PRID_VAL_ILLEGAL_LENGTH.getErrorMessage());
		}

		/**
		 * 
		 * Validate the PRID, It should not contain any alphanumeric characters
		 * 
		 */

		if (!Pattern.matches(ISNUMERIC_REGEX, id)) {
			throw new MosipInvalidIDException(MosipPridExceptionConstants.PRID_VAL_INVALID_DIGITS.getErrorCode(),
					MosipPridExceptionConstants.PRID_VAL_INVALID_DIGITS.getErrorMessage());
		}
		/**
		 * 
		 * Validate the PRID, It should not contain '0' or '1' as the first digit.
		 * 
		 */

		if (id.charAt(0) == '0' || id.charAt(0) == '1') {
			throw new MosipInvalidIDException(MosipPridExceptionConstants.PRID_VAL_INVALID_ZERO_ONE.getErrorCode(),
					MosipPridExceptionConstants.PRID_VAL_INVALID_ZERO_ONE.getErrorMessage());
		}
		/**
		 * 
		 * The method isValidId(id) from MosipIDFilter will validate the PRID for the
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

		if (!MosipIdFilter.isValidId(id)) {
			throw new MosipInvalidIDException(
					MosipPridExceptionConstants.PRID_VAL_ILLEGAL_SEQUENCE_REPEATATIVE.getErrorCode(),
					MosipPridExceptionConstants.PRID_VAL_ILLEGAL_SEQUENCE_REPEATATIVE.getErrorMessage());
		}
		/**
		 * 
		 * The method validateChecksum(id) from MosipIdChecksum will validate
		 * 
		 * Validate the PRID by verifying the checksum
		 * 
		 */

		if (!MosipIdChecksum.validateChecksum(id)) {
			throw new MosipInvalidIDException(MosipPridExceptionConstants.PRID_VAL_ILLEGAL_CHECKSUM.getErrorCode(),
					MosipPridExceptionConstants.PRID_VAL_ILLEGAL_CHECKSUM.getErrorMessage());
		}

		return true;
	}

}
