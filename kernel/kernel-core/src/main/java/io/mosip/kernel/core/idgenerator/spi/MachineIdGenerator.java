package io.mosip.kernel.core.idgenerator.spi;

/**
 * Interface that provides methods to generate Machine ID.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 * @param <T>
 *            the id type.
 */
public interface MachineIdGenerator<T> {
	/**
	 * This method generates machine ID.
	 * 
	 * @return the generated machine ID.
	 */
	public T generateMachineId();

}
