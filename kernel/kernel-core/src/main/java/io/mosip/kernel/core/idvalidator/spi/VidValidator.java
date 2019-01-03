package io.mosip.kernel.core.idvalidator.spi;

/**
 * Interface having a function to validate an Id
 * 
 * @author Abhishek Kumar
 * @since 1.0.0
 * @param <T>
 */
public interface VidValidator<T> {
	/**
	 * Function to validate given Id
	 * 
	 * @param id
	 *            The Id to validate
	 * @return true if Id is valid
	 */
	boolean validateId(T id);
}
