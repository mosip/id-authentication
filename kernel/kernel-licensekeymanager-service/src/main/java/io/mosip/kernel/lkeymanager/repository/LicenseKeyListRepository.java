package io.mosip.kernel.lkeymanager.repository;

import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.lkeymanager.entity.LicenseKeyList;

/**
 * Repository class for {@link LicenseKeyList}.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 */
@Repository
public interface LicenseKeyListRepository extends BaseRepository<LicenseKeyList, String> {
	/**
	 * Method to extract licensekey list details by license key.
	 * 
	 * @param licenseKey
	 *            the license key.
	 * @return the entity response.
	 */
	public LicenseKeyList findByLicenseKey(String licenseKey);
}
