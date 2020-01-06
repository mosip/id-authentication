package io.mosip.kernel.core.idgenerator.spi;

/**
 * @author Uday Kumar
 * @since 1.0.0
 *
 * @param <T>
 *            the return type of generateId() method.
 */
public interface PartnerIdGenerator<T> {

	/**
	 * This method generate PartnerId.
	 * 
	 * @return the provided type.
	 */
	public T generateId();

}
