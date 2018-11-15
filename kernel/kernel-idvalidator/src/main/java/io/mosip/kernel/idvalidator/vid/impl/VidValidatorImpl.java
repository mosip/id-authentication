/**
 * 
 */
package io.mosip.kernel.idvalidator.vid.impl;

import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.idvalidator.exception.InvalidIDException;
import io.mosip.kernel.core.idvalidator.spi.IdValidator;
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
public class VidValidatorImpl implements IdValidator<String> {

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
	private void VidValidatorImplPostConstruct() {
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
		if (!Pattern.matches(numaricRegEx, id)) {
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
			throw new InvalidIDException(VidExceptionConstant.VID_VAL_INVALID_ZERO_ONE.getErrorCode(),
					VidExceptionConstant.VID_VAL_INVALID_ZERO_ONE.getErrorMessage());
		}

//TODO : Need to write logic for Sequence validation without using Generator's util	
//		/**
//		 * 
//		 * The method isValidId(id) from MosipIDFilter will validate the VID for the
//		 * following conditions
//		 * 
//		 * The VID should not contain any sequential number for 2 or more than two
//		 * digits
//		 * 
//		 * The VID should not contain any repeating numbers for 2 or more than two
//		 * digits
//		 * 
//		 * The VID should not have repeated block of numbers for more than 2 digits
//		 * 
//		 */
//		if (!IdFilterUtils.isValidId(id)) {
//			throw new InvalidIDException(
//					VidExceptionConstant.VID_VAL_ILLEGAL_SEQUENCE_REPEATATIVE.getErrorCode(),
//					VidExceptionConstant.VID_VAL_ILLEGAL_SEQUENCE_REPEATATIVE.getErrorMessage());
//		}

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

}
