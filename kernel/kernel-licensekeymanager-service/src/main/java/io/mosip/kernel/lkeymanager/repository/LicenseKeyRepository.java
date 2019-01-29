package io.mosip.kernel.lkeymanager.repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.lkeymanager.entity.LicenseKey;
import io.mosip.kernel.lkeymanager.entity.id.LicenseKeyID;

public interface LicenseKeyRepository extends BaseRepository<LicenseKey, LicenseKeyID> {
	LicenseKey findByTspIdAndLKey(String tspId, String licenseKey);

}
