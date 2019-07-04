package io.mosip.kernel.core.idgenerator.spi;

/**
 * Interface that provides method for license generation of MISP.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 * @param <T>
 *            The generic type.
 */
public interface MISPLicenseGenerator<T> {
	/**
	 * Method that generates a license of specified length.
	 * 
	 * @return the generated license.
	 */
	public T generateLicense();
}
