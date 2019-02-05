package io.mosip.kernel.lkeymanager.repository;

import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.lkeymanager.entity.LicenseKeyTspMap;
import io.mosip.kernel.lkeymanager.entity.id.LicenseKeyTspMapID;

@Repository
public interface LicenseKeyTspMapRepository extends BaseRepository<LicenseKeyTspMap, LicenseKeyTspMapID> {
	public LicenseKeyTspMap findByLKeyAndTspId(String licenseKey, String tspID);

}
