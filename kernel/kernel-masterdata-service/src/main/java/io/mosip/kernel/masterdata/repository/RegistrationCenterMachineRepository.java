package io.mosip.kernel.masterdata.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.masterdata.entity.RegistrationCenterMachine;
import io.mosip.kernel.masterdata.entity.id.RegistrationCenterMachineID;

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
		extends BaseRepository<RegistrationCenterMachine, RegistrationCenterMachineID> {

@Query("FROM RegistrationCenterMachine WHERE registrationCenterMachinePk =?1 and (isDeleted is null or isDeleted =false)")
Optional<RegistrationCenterMachine> findAllNondeletedMappings(RegistrationCenterMachineID registrationCenterMachinePk);


}
