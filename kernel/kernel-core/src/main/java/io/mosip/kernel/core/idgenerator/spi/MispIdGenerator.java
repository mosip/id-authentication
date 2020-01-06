package io.mosip.kernel.core.idgenerator.spi;

/**
 * This is an interface for the generation of MISPID
 * 
 * @author Sidhant Agarwal
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */
public interface MispIdGenerator<T> {
	/**
	 * Function to generate an Id
	 * 
	 * @return The generated id
	 */
	public T generateId();

}
