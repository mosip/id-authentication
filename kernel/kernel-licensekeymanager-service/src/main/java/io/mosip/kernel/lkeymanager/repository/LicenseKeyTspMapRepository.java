package io.mosip.kernel.lkeymanager.repository;

import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.lkeymanager.entity.LicenseKeyTspMap;
import io.mosip.kernel.lkeymanager.entity.id.LicenseKeyTspMapID;

/**
 * Repository class for {@link LicenseKeyTspMap}.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 */
@Repository
public interface LicenseKeyTspMapRepository extends BaseRepository<LicenseKeyTspMap, LicenseKeyTspMapID> {
	/**
	 * Method to extract LicenseKeyTspMap entity based on license key and TSP ID.
	 * 
	 * @param licenseKey
	 *            the license key.
	 * @param tspID
	 *            the TSP ID.
	 * @return the entity response.
	 */
	public LicenseKeyTspMap findByLKeyAndTspId(String licenseKey, String tspID);
}
