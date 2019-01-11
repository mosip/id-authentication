package io.mosip.kernel.core.idgenerator.spi;

/**
 * Interface that provides method to generate Registration Center ID.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 * @param <T>
 *            the id type.
 */
public interface RegistrationCenterIdGenerator<T> {
	/**
	 * This method generates registration center id.
	 * 
	 * @return the generated registration center id.
	 */
	public T generateRegistrationCenterId();
}
