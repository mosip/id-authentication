
package io.mosip.kernel.idvalidator.prid.impl;

import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.idvalidator.exception.InvalidIDException;
import io.mosip.kernel.core.idvalidator.spi.IdValidator;
import io.mosip.kernel.core.util.ChecksumUtils;

import io.mosip.kernel.core.util.StringUtils;
import io.mosip.kernel.idvalidator.prid.constant.PridExceptionConstant;

/**
 * Class to validate the Given PRID in String format
 *
 * @author M1037462
 * 
 * @since 1.0.0
 */
@Component
public class PridValidatorImpl implements IdValidator<String> {

	/**
	 * This Field to hold the length of PRID Reading length of PRID from property
	 * file
	 */
	@Value("${mosip.kernel.prid.length}")
	private int pridLength;

	/**
	 * This Field to hold regular expressions for checking UIN has only digits.
	 */
	private String numaricRegEx;

	/**
	 * Method to prepare regular expressions for checking UIN has only digits.
	 */

	@PostConstruct
	private void pridValidatorImplPostConstruct() {
		numaricRegEx = "\\d{" + pridLength + "}";
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
	 * Method used to validate PRID against acceptance Criteria
	 * 
	 * 
	 * @param id
	 *            pass a PRID in String format example : String inputFile =
	 *            "426789089018"
	 * @return true if Id is valid
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
		
		
//TODO : Need to write logic for Sequence validation without using Generator's util		
//		/**
//		 * 
//		 * The method isValidId(id) from IDFilter will validate the PRID for the
//		 * following conditions
//		 * 
//		 * The PRID should not contain any sequential number for 2 or more than two
//		 * digits
//		 * 
//		 * The PRID should not contain any repeating numbers for 2 or more than two
//		 * digits
//		 * 
//		 * The PRID should not have repeated block of numbers for more than 2 digits
//		 * 
//		 */
//
//		if (!IdFilterUtils.isValidId(id)) {
//			throw new InvalidIDException(
//					PridExceptionConstant.PRID_VAL_ILLEGAL_SEQUENCE_REPEATATIVE.getErrorCode(),
//					PridExceptionConstant.PRID_VAL_ILLEGAL_SEQUENCE_REPEATATIVE.getErrorMessage());
//		}
		
		
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

		return true;
	}

}
