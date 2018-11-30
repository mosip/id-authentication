package io.mosip.kernel.masterdata.repository;

import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.masterdata.entity.RegistrationCenterDeviceHistory;
import io.mosip.kernel.masterdata.entity.RegistrationCenterDeviceHistoryPk;

/**
 * Repository to perform CRUD operations on RegistrationCenterDeviceHistory.
 * 
 * @author Bal Vikash Sharma
 * @since 1.0.0
 * @see RegistrationCenterDeviceHistory
 * @see BaseRepository
 *
 */
@Repository
public interface RegistrationCenterDeviceHistoryRepository
		extends BaseRepository<RegistrationCenterDeviceHistory, RegistrationCenterDeviceHistoryPk> {
}
