package io.mosip.kernel.datavalidator.phone.impl;



import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.datavalidator.exception.InvalidPhoneNumberException;
import io.mosip.kernel.core.datavalidator.spi.PhoneValidator;
import io.mosip.kernel.core.util.StringUtils;
import io.mosip.kernel.datavalidator.phone.constant.PhoneConstant;

/**
 * This class for validate the Given Phone number in String format
 *
 * @author Megha Tanga
 * 
 * @since 1.0.0
 * 
 */

@Component
public class PhoneValidatorImpl implements PhoneValidator<String> {

	/**
	 * This variable to hold the maximum length of the phone number and
	 * Reading maximum length of the phone number from property file
	 * 
	 */
	@Value("${mosip.kernel.phone.max-length}")
	private int phoneMaxLength;
	/**
	 * This variable to hold the minimum length of the phone number and 
	 * Reading minimum length of the phone number from property file
	 * 
	 */
	@Value("${mosip.kernel.phone.min-length}")
	private int phoneMinLength;
	/**
	 * This variable to hold the special characters of the phone number and 
	 * Reading special characters for the phone number from property file
	 * 
	 */
	@Value("${mosip.kernel.phone.special-char}")
	private String phoneSpecialChar;

	/**
	 * Regular Expression for match valid phone number
	 * 
	 */
	private String phoneRegex;

	@PostConstruct
	private void phoneValidatorImplPostConstruct() {
		phoneRegex = "[\\d" + phoneSpecialChar + "]{" + phoneMinLength + "," + phoneMaxLength + "}";
		
	}

	/**
	 * Method used for Validate Phone number against acceptance Criteria
	 * 
	 * @param phoneNum
	 *            pass a Phone number in String format
	 * 
	 * @return return boolean value true or false
	 * 
	 * @throws InvalidPhoneNumberException
	 *             If entered Phone number is empty or null.
	 * 
	 * @throws InvalidPhoneNumberException
	 *             If entered Phone number length is not in a specified number of
	 *             digits.
	 * 
	 * @throws InvalidPhoneNumberException
	 *             If entered phone number contain any special characters which are
	 *             not specified .
	 * 
	 */
	public boolean validatePhone(String phoneNum) {

		/**
		 * 
		 * Check Phone number, It Shouldn't be Null or Empty
		 * 
		 */
		if (StringUtils.isEmpty(phoneNum)) {
			throw new InvalidPhoneNumberException(
					PhoneConstant.PHONE_NUM_INVALID_NULL.getErrorCode(),
					PhoneConstant.PHONE_NUM_INVALID_NULL.getErrorMessage());
		}

		/**
		 * 
		 * Check the Length of the Phone number, It Should be specified number of Digits
		 * 
		 */
		if (phoneNum.length() < phoneMinLength || phoneNum.length() > phoneMaxLength) {
			throw new InvalidPhoneNumberException(
					PhoneConstant.PHONE_NUM_INVALID_MIN_MAX_LENGTH.getErrorCode(),
					PhoneConstant.PHONE_NUM_INVALID_MIN_MAX_LENGTH.getErrorMessage());
		}

		/**
		 * 
		 * 
		 * Check the Entered Phone number, It should contain only specified special
		 * characters
		 * 
		 * 
		 */
		if (!Pattern.matches(phoneRegex, phoneNum)) {
			throw new InvalidPhoneNumberException(
					PhoneConstant.PHONE_NUM_INVALID_DIGITS.getErrorCode(),
					PhoneConstant.PHONE_NUM_INVALID_DIGITS.getErrorMessage());
		}

		/**
		 * 
		 * once the above validation are passed then the method will going to return
		 * True That is, Phone Number is valid
		 * 
		 * 
		 */
		return true;
	}

}
