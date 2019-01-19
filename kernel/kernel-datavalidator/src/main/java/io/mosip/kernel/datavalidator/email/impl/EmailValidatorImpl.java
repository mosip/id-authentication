package io.mosip.kernel.datavalidator.email.impl;

import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.datavalidator.exception.InvalideEmailException;
import io.mosip.kernel.core.datavalidator.spi.EmailValidator;
import io.mosip.kernel.core.util.StringUtils;
import io.mosip.kernel.datavalidator.email.constant.EmailConstant;

/**
 * This class for validate the given Email ID in the String format
 *
 * @author Megha Tanga
 * 
 * @since 1.0.0
 * 
 */
@Component
public class EmailValidatorImpl implements EmailValidator<String> {
	/**
	 * This variable to hold email maximum length
	 * 
	 */
	@Value("${mosip.kernel.email.max-length}")
	private int emailMaxLength;
	/**
	 * This variable to hold email minimum length
	 * 
	 */
	@Value("${mosip.kernel.email.min-length}")
	private int emailMinLength;
	/**
	 * This variable to hold special characters for user name of the email
	 * 
	 */
	@Value("${mosip.kernel.email.special-char}")
	private String unserNameSpecialChar;
	/**
	 * This variable to hold special character for domain name of the email
	 * 
	 */
	@Value("${mosip.kernel.email.domain.special-char}")
	private String domainSpecialChar;
	/**
	 * This variable to hold minimum length of domain extension
	 * 
	 */
	@Value("${mosip.kernel.email.domain.ext-max-lenght}")
	private int domainExtMaxLength;
	/**
	 * This variable to hold maximum length of domain extension
	 * 
	 */
	@Value("${mosip.kernel.email.domain.ext-min-lenght}")
	private int domainExtMinLength;

	/**
	 * Regular Expression for matching the email ID for the following policies
	 * 
	 * First character of User-Name must be alphabets(a-zA-Z) and digits(0-9)
	 * special characters are not allowed for first character special characters for
	 * the User-Name are reading from the property file
	 * 
	 * Maximum and Minimum length of the whole email reading from property file
	 * 
	 * Domain name should not contain any numerics that is only alphabets (a-zA-Z)
	 * and special characters for the domain name are reading from the property file
	 * 
	 * Domain extension should have only alphabets(a-zA-Z) and Maximum and Minimum
	 * length of domain extension reading from property file
	 * 
	 * 
	 */
	private String emailRegex;

	@PostConstruct
	private void emailValidatorImplPostConstruct() {

		emailRegex = "^[a-zA-Z0-9][a-zA-Z0-9" + unserNameSpecialChar + "]+(?:\\.[a-zA-Z0-9" + unserNameSpecialChar
				+ "]+)*@(?:[a-zA-Z" + domainSpecialChar + "]+\\.)+[a-zA-Z]{" + domainExtMinLength + ","
				+ domainExtMaxLength + "}$";
	}

	/**
	 * Method used for Validate Email ID against acceptance Criteria
	 * 
	 * @param email
	 *            pass a Email ID in String format
	 * 
	 * @return return boolean value true or false for valid and invalid email
	 * 
	 * @throws InvalideEmailException
	 *             If entered Email Id is empty or null.
	 * 
	 * @throws InvalideEmailException
	 *             If entered Email Id whole length is not in a specified number of
	 *             characters.
	 * 
	 * @throws InvalideEmailException
	 *             If entered Email Id contain any special characters which are not
	 *             specified .
	 * 
	 * @throws InvalideEmailException
	 *             If entered Email Id Domain Extensions length is not in a
	 *             specified number of characters.
	 * 
	 */
	public boolean validateEmail(String email) {

		/**
		 * This variable holds the length of the domain extension of given email
		 * 
		 */
		int domainLen = email.substring(email.indexOf('.') + 1, email.length()).length();

		/**
		 * 
		 * Check Email ID, It Shouldn't be Null or empty
		 * 
		 */

		if (StringUtils.isEmpty(email)) {
			throw new InvalideEmailException(EmailConstant.EMAIL_INVALID_NULL.getErrorCode(),
					EmailConstant.EMAIL_INVALID_NULL.getErrorMessage());
		}
		/**
		 * 
		 * Check the length of the whole Email ID, It Should be specified number of
		 * characters
		 * 
		 */

		if (email.length() < emailMinLength || email.length() > emailMaxLength) {
			throw new InvalideEmailException(EmailConstant.EMAIL_INVALID_LENGTH.getErrorCode(),
					EmailConstant.EMAIL_INVALID_LENGTH.getErrorMessage());
		}

		/**
		 * 
		 * Check the length of the domain extensions of the given Email Id, It Should be
		 * specified number of characters
		 * 
		 */
		if (domainLen < domainExtMinLength || domainLen > domainExtMaxLength) {
			throw new InvalideEmailException(
					EmailConstant.EMAIL_INVALID_DOMAIN_LENGTH.getErrorCode(),
					EmailConstant.EMAIL_INVALID_DOMAIN_LENGTH.getErrorMessage());
		}
		/**
		 * 
		 * Validate the given Email Id with Regular Expression having the following
		 * conditions
		 * 
		 * First character of User-Name must be only alphabets(a-zA-Z) and digits(0-9)
		 * special characters are not allowed for first character and special characters
		 * for User-Name are reading from the property file
		 * 
		 * Domain name should not contain any numerics that is only alphabets (a-zA-Z)
		 * and special characters for the domain name are reading from the property file
		 * 
		 * Domain extension should have only alphabets(a-zA-Z) length of the domain
		 * extension reading from property file
		 * 
		 * 
		 */
		if (!Pattern.matches(emailRegex, email)) {
			throw new InvalideEmailException(EmailConstant.EMAIL_INVALID_CHAR.getErrorCode(),
					EmailConstant.EMAIL_INVALID_CHAR.getErrorMessage());
		}

		/**
		 * 
		 * Once the above validation are passed then the method will going to return
		 * True That is, Email ID is valid
		 * 
		 * 
		 */
		return true;
	}

}
