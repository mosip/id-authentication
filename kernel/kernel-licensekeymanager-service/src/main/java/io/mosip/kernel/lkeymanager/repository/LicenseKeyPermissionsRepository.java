package io.mosip.kernel.lkeymanager.repository;

import java.util.List;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.lkeymanager.entity.LicenseKeyPermissions;
import io.mosip.kernel.lkeymanager.entity.id.LicenseKeyID;

public interface LicenseKeyPermissionsRepository extends BaseRepository<LicenseKeyPermissions, LicenseKeyID> {
	List<LicenseKeyPermissions> findByTspId(String tspId);
}
