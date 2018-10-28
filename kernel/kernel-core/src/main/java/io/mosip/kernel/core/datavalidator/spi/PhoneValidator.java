/**
 * 
 */
package io.mosip.kernel.core.datavalidator.spi;
/**
 * Interface having a function to validate a phone number
 * 
 * @author Megha Tanga
 * @since 1.0.0
 *
 * @param <T>
 *            Type of phone number
 */
public interface PhoneValidator<T> {

	/**
	 * Function to validate phone number
	 * 
	 * @return The boolean value true or false depends on validation
	 */
	boolean validatePhone(T phoneNum);

}