package io.mosip.kernel.core.licensekeymanager.spi;

import java.util.List;

/**
 * Interface that provides methods for license key management.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 * @param <T>
 *            the return type.
 * @param <D>
 *            the input argument type.
 * @param <S>
 *            the input argument type.
 */
public interface LicenseKeyManagerService<T, D, S> {
	/**
	 * Method to generate license key.
	 * 
	 * @param licenseKeyGenerationDto
	 *            the request DTO.
	 * @return the response.
	 */
	public T generateLicenseKey(D licenseKeyGenerationDto);

	/**
	 * Method to map license key with permissions.
	 * 
	 * @param licenseKeyMappingDto
	 *            the request DTO.
	 * @return the response.
	 */
	public T mapLicenseKey(S licenseKeyMappingDto);

	/**
	 * Method to fetch permissions mapped to a license key.
	 * 
	 * @param tspID
	 *            the TSP ID to which the license is mapped.
	 * @param licenseKey
	 *            the mapped license key.
	 * @return the response.
	 */
	public List<T> fetchLicenseKeyPermissions(T tspID, T licenseKey);
}
