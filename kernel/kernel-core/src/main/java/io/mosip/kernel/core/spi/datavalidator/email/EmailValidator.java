/**
 * 
 */
package io.mosip.kernel.core.spi.datavalidator.email;

/**
 * Interface having a function to validate a Email Id
 * 
 * @author Megha Tanga
 * @since 1.0.0
 *
 * @param <T>
 *            Type of Email
 */
public interface EmailValidator<T> {

	/**
	 * Function to generate an Id
	 * 
	 * @return  The boolean value true or false depends on validation
	 */
	boolean validateEmail(T email);

}
