package io.mosip.kernel.masterdata.repository;

import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.masterdata.entity.RegistrationCenterMachine;
import io.mosip.kernel.masterdata.entity.RegistrationCenterMachinePk;

/**
 * Repository to perform CRUD operations on RegistrationCenterMachine.
 * 
 * @author Bal Vikash Sharma
 * @since 1.0.0
 * @see RegistrationCenterMachine
 * @see BaseRepository
 *
 */
@Repository
public interface RegistrationCenterMachineRepository
		extends BaseRepository<RegistrationCenterMachine, RegistrationCenterMachinePk> {
}
