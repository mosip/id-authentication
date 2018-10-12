/**
 * 
 */
package io.mosip.kernel.idvalidator.uinvalidator;

import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.spi.idvalidator.MosipIdValidator;
import io.mosip.kernel.core.util.ChecksumUtils;
import io.mosip.kernel.core.util.IdFilterUtils;
import io.mosip.kernel.core.util.StringUtils;
import io.mosip.kernel.idvalidator.exception.MosipInvalidIDException;
import io.mosip.kernel.idvalidator.uinvalidator.constants.MosipUinExceptionConstants;

/**
 * Test class for validate the Given UIN in String format
 *
 * @author Megha Tanga
 * 
 * @since 1.0.0
 */

@Component
public class UinValidator implements MosipIdValidator<String> {

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
	public void preparNumaricRegEx() {
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
	 * @throws MosipInvalidIDException
	 *             If entered UIN is empty or null.
	 * @throws MosipInvalidIDException
	 *             If entered UIN contain any sequential and repeated block of
	 *             number for 2 or more than two digits",
	 * @throws MosipInvalidIDException
	 *             If entered UIN length should be specified number of digit.
	 * @throws MosipInvalidIDException
	 *             If entered UIN contain any alphanumeric characters
	 * @throws MosipInvalidIDException
	 *             If entered UIN should not match with checksum
	 * @throws MosipInvalidIDException
	 *             If entered UIN contain Zero or One as first Digit.
	 */

	public boolean validateId(String id) {

		/**
		 * 
		 * Check UIN, It Shouldn't be Null or empty
		 * 
		 */
		if (StringUtils.isEmpty(id)) {
			throw new MosipInvalidIDException(MosipUinExceptionConstants.UIN_VAL_INVALID_NULL.getErrorCode(),
					MosipUinExceptionConstants.UIN_VAL_INVALID_NULL.getErrorMessage());
		}
		/**
		 * 
		 * Check the Length of the UIN, It Should be specified number of digits
		 * 
		 */
		if (id.length() != uinLength) {
			throw new MosipInvalidIDException(MosipUinExceptionConstants.UIN_VAL_ILLEGAL_LENGTH.getErrorCode(),
					MosipUinExceptionConstants.UIN_VAL_ILLEGAL_LENGTH.getErrorCode());
		}
		/**
		 * 
		 * Validation for the UIN should not contain any alphanumeric characters
		 * 
		 */
		if (!Pattern.matches(numaricRegEx, id)) {
			throw new MosipInvalidIDException(MosipUinExceptionConstants.UIN_VAL_INVALID_DIGITS.getErrorCode(),
					MosipUinExceptionConstants.UIN_VAL_INVALID_DIGITS.getErrorMessage());
		}
		/**
		 * 
		 * Validation for the UIN should not contain '0' or '1' as the first digit.
		 * 
		 */
		if (id.charAt(0) == CHAR_ZERO || id.charAt(0) == CHAR_ONE) {
			throw new MosipInvalidIDException(MosipUinExceptionConstants.UIN_VAL_INVALID_ZERO_ONE.getErrorCode(),
					MosipUinExceptionConstants.UIN_VAL_INVALID_ZERO_ONE.getErrorMessage());
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
		if (IdFilterUtils.isValidId(id)) {
			throw new MosipInvalidIDException(
					MosipUinExceptionConstants.UIN_VAL_ILLEGAL_SEQUENCE_REPEATATIVE.getErrorCode(),
					MosipUinExceptionConstants.UIN_VAL_ILLEGAL_SEQUENCE_REPEATATIVE.getErrorMessage());
		}

		/**
		 * 
		 * The method validateChecksum(id) from MosipIdChecksum will validate
		 * 
		 * Validate the UIN by verifying the checksum
		 * 
		 */
		if (!ChecksumUtils.validateChecksum(id)) {
			throw new MosipInvalidIDException(MosipUinExceptionConstants.UIN_VAL_ILLEGAL_CHECKSUM.getErrorCode(),
					MosipUinExceptionConstants.UIN_VAL_ILLEGAL_CHECKSUM.getErrorMessage());
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