/**
 * 
 */
package io.mosip.kernel.core.pinvalidator.spi;

/**
 * Interface having a method to validate a pin
 * 
 * @author Uday Kumar
 * @since 1.0.0
 *
 * @param <T>
 *            Type of pin
 */
public interface PinValidator<T> {

	/**
	 * Function to validate given pin
	 * 
	 * @param pin
	 *            The pin to validate
	 * @return true if pin is valid
	 */
	boolean validatePin(T pin);
}
