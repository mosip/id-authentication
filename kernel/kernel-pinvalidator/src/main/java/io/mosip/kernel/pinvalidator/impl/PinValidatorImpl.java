package io.mosip.kernel.pinvalidator.impl;

import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.pinvalidator.exception.InvalidPinException;
import io.mosip.kernel.core.pinvalidator.spi.PinValidator;
import io.mosip.kernel.core.util.StringUtils;
import io.mosip.kernel.pinvalidator.constant.PinExceptionConstant;

/**
 * This class for validate the given pin in the String format
 *
 * @author Uday Kumar
 * 
 * @since 1.0.0
 * 
 */
@Component
public class PinValidatorImpl implements PinValidator<String> {
	/**
	 * This variable to hold Pin length
	 * 
	 */
	@Value("${mosip.kernel.pin.length}")
	private int pinLength;
	/**
	 * Validate the pin, It should not contain any alphanumeric characters
	 */
	private String numaricRegEx;

	/**
	 * Method to prepare regular expressions for checking pin has only digits.
	 */
	@PostConstruct
	private void uinValidatorImplnumaricRegEx() {
		numaricRegEx = "\\d{" + pinLength + "}";
	}

	@Override
	public boolean validatePin(String pin) {
		/**
		 * 
		 * Check Pin, It Shouldn't be Null or empty
		 * 
		 */
		if (StringUtils.isEmpty(pin)) {
			throw new InvalidPinException(PinExceptionConstant.PIN_INVALID_NULL.getErrorCode(),
					PinExceptionConstant.PIN_INVALID_NULL.getErrorMessage());
		}
		/**
		 * 
		 * Check the Length of the pin, It Should be specified number of digits
		 * 
		 */

		if (pin.length() != pinLength) {
			throw new InvalidPinException(PinExceptionConstant.PIN_INVALID_LENGTH.getErrorCode(),
					PinExceptionConstant.PIN_INVALID_LENGTH.getErrorMessage() + pinLength);
		}
		/**
		 * 
		 * Validate the pin, It should not contain any alphanumeric characters
		 * 
		 */
		if (!Pattern.matches(numaricRegEx, pin)) {
			throw new InvalidPinException(PinExceptionConstant.PIN_INVALID_CHAR.getErrorCode(),
					PinExceptionConstant.PIN_INVALID_CHAR.getErrorMessage());
		}

		return true;
	}

}
