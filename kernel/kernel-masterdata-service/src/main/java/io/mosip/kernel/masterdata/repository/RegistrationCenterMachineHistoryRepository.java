package io.mosip.kernel.masterdata.repository;

import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.masterdata.entity.RegistrationCenterMachineHistory;
import io.mosip.kernel.masterdata.entity.RegistrationCenterMachineHistoryPk;

/**
 * Repository to perform CRUD operations on RegistrationCenterMachineHistory.
 * 
 * @author Bal Vikash Sharma
 * @since 1.0.0
 * @see RegistrationCenterMachineHistory
 * @see BaseRepository
 *
 */
@Repository
public interface RegistrationCenterMachineHistoryRepository
		extends BaseRepository<RegistrationCenterMachineHistory, RegistrationCenterMachineHistoryPk> {
}
