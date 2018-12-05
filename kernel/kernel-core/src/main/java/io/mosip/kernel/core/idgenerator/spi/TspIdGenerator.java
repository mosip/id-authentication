package io.mosip.kernel.core.idgenerator.spi;

/**
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 * @param <T>
 *            the return type of generateId() method.
 */
public interface TspIdGenerator<T> {

	/**
	 * This method generate TSPID.
	 * 
	 * @return the provided type.
	 */
	public T generateId();

}
