/**
 * 
 */
package io.mosip.kernel.core.datavalidator.spi;

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


	boolean validateEmail(T email);

}
