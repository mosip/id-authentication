package io.mosip.kernel.core.idgenerator.spi;

/**
 * Interface having a function to generate an Id
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 * @param <T>
 *            Type of Id
 */
public interface UinGenerator<T> {

	/**
	 * Function to generate an Id
	 * 
	 * @return The generated id
	 */
	T generateId();

}