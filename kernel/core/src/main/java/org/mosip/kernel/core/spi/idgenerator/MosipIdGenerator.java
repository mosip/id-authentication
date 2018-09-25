package org.mosip.kernel.core.spi.idgenerator;

/**
 * Interface having a function to generate an Id
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 * @param <T>
 *            Type of Id
 */
public interface MosipIdGenerator<T> {

	/**
	 * Function to generate an Id
	 * 
	 * @return The generated id
	 */
	T generateId();

}