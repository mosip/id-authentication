/**
 * 
 */
package io.mosip.kernel.idvalidator.vidvalidator;

import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.spi.idvalidator.MosipIdValidator;
import io.mosip.kernel.core.util.ChecksumUtils;
import io.mosip.kernel.core.util.IdFilterUtils;
import io.mosip.kernel.core.util.StringUtils;
import io.mosip.kernel.idvalidator.exception.MosipInvalidIDException;
import io.mosip.kernel.idvalidator.vidvalidator.constants.MosipVidExceptionConstants;

/**
 * Class to validate the VID
 * 
 * @author M1037462
 * 
 * @since 1.0.0
 */
@Component
public class VidValidator implements MosipIdValidator<String> {

	@Value("${mosip.kernel.vid.length}")
	private int vidLength;

	/**
	 * This Field to hold regular expressions for checking UIN has only digits.
	 */
	private String numaricRegEx;

	/**
	 * Method to prepare regular expressions for checking UIN has only digits.
	 */
	@PostConstruct
	public void preparNumaricRegEx() {
		numaricRegEx = "\\d{" + vidLength + "}";
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
	 * @throws MosipInvalidIDException
	 *             If entered VID is empty or null.
	 * @throws MosipInvalidIDException
	 *             If entered VID contain any sequential and repeated block of
	 *             number for 2 or more than two digits",
	 * @throws MosipInvalidIDException
	 *             If entered VID length should be specified number of digits.
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
		if (StringUtils.isEmpty(id)) {
			throw new MosipInvalidIDException(MosipVidExceptionConstants.VID_VAL_INVALID_NULL.getErrorCode(),
					MosipVidExceptionConstants.VID_VAL_INVALID_NULL.getErrorMessage());
		}

		/**
		 * 
		 * Checks the Length of the VID, It Should be specified number of digits
		 * 
		 */
		if (id.length() != vidLength) {
			throw new MosipInvalidIDException(MosipVidExceptionConstants.VID_VAL_ILLEGAL_LENGTH.getErrorCode(),
					MosipVidExceptionConstants.VID_VAL_ILLEGAL_LENGTH.getErrorMessage());
		}

		/**
		 * 
		 * Validate the value of VID, It should not contain any alphanumeric characters
		 * 
		 */
		if (!Pattern.matches(numaricRegEx, id)) {
			throw new MosipInvalidIDException(MosipVidExceptionConstants.VID_VAL_INVALID_DIGITS.getErrorCode(),
					MosipVidExceptionConstants.VID_VAL_INVALID_DIGITS.getErrorMessage());
		}

		/**
		 * 
		 * Validate the value of VID, It should not contain '0' or '1' as the first
		 * digit.
		 * 
		 */

		if (id.charAt(0) == CHAR_ZERO || id.charAt(0) == CHAR_ONE) {
			throw new MosipInvalidIDException(MosipVidExceptionConstants.VID_VAL_INVALID_ZERO_ONE.getErrorCode(),
					MosipVidExceptionConstants.VID_VAL_INVALID_ZERO_ONE.getErrorMessage());
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
					MosipVidExceptionConstants.VID_VAL_ILLEGAL_SEQUENCE_REPEATATIVE.getErrorCode(),
					MosipVidExceptionConstants.VID_VAL_ILLEGAL_SEQUENCE_REPEATATIVE.getErrorMessage());
		}

		/**
		 * 
		 * The method validateChecksum(id) from MosipIdChecksum will validate VID by
		 * verifying the checksum
		 * 
		 */
		if (!ChecksumUtils.validateChecksum(id)) {
			throw new MosipInvalidIDException(MosipVidExceptionConstants.VID_VAL_ILLEGAL_CHECKSUM.getErrorCode(),
					MosipVidExceptionConstants.VID_VAL_ILLEGAL_CHECKSUM.getErrorMessage());
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
