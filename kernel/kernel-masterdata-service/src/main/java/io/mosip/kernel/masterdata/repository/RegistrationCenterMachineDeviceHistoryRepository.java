package io.mosip.kernel.masterdata.repository;

import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.masterdata.entity.RegistrationCenterMachineDeviceHistory;
import io.mosip.kernel.masterdata.entity.id.RegistrationCenterMachineDeviceID;

/**
 * Repository to perform CRUD operations on RegistrationCenterMachineDeviceHistory.
 * 
 * @author Bal Vikash Sharma
 * @since 1.0.0
 * @see RegistrationCenterMachineDeviceHistory
 * @see BaseRepository
 *
 */
@Repository
public interface RegistrationCenterMachineDeviceHistoryRepository
		extends BaseRepository<RegistrationCenterMachineDeviceHistory, RegistrationCenterMachineDeviceID> {
}
