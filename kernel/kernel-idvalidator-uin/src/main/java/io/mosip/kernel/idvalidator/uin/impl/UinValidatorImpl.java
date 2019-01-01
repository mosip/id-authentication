/**
 * 
 */
package io.mosip.kernel.idvalidator.uin.impl;

import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.idvalidator.exception.InvalidIDException;
import io.mosip.kernel.core.idvalidator.spi.IdValidator;
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
public class UinValidatorImpl implements IdValidator<String> {

	/**
	 * The length of the UIN is reading from property file
	 */
	@Value("${mosip.kernel.uin.length}")
	private int uinLength;

	/**
	 * This Field to hold regular expressions for checking UIN has only digits.
	 */
	private String numaricRegEx;

	/**
	 * Method to prepare regular expressions for checking UIN has only digits.
	 */
	@PostConstruct
	private void uinValidatorImplnumaricRegEx() {
		numaricRegEx = "\\d{" + uinLength + "}";
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
	 *            "426789089018"
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

//TODO : Need to write logic for Sequence validation without using Generator's util	
//		/**
//		 * 
//		 * The method isValidId(id) from MosipIDFilter will validate the UIN for the
//		 * following conditions
//		 * 
//		 * The UIN should not contain any sequential number for 2 or more than two
//		 * digits
//		 * 
//		 * The UIN should not contain any repeating numbers for 2 or more than two
//		 * digits
//		 * 
//		 * The UIN should not have repeated block of numbers for more than 2 digits
//		 * 
//		 */
//		if (IdFilterUtils.isValidId(id)) {
//			throw new InvalidIDException(
//					UinExceptionConstant.UIN_VAL_ILLEGAL_SEQUENCE_REPEATATIVE.getErrorCode(),
//					UinExceptionConstant.UIN_VAL_ILLEGAL_SEQUENCE_REPEATATIVE.getErrorMessage());
//		}

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

}