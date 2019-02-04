package io.mosip.kernel.syncdata.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.syncdata.entity.RegistrationCenterMachine;
import io.mosip.kernel.syncdata.entity.id.RegistrationCenterMachineID;

/**
 * Repository to perform CRUD operations on RegistrationCenterMachine.
 * 
 * @author Neha
 * @since 1.0.0
 * @see RegistrationCenterMachine
 * @see BaseRepository
 *
 */
@Repository
public interface RegistrationCenterMachineRepository
		extends BaseRepository<RegistrationCenterMachine, RegistrationCenterMachineID> {
	
	@Query("From RegistrationCenterMachine rcm WHERE rcm.registrationCenterMachinePk.machineId =?1")
	List<RegistrationCenterMachine> findAllByMachineId(String machineId);

	@Query("From RegistrationCenterMachine rcm WHERE rcm.registrationCenterMachinePk.machineId =?1 AND (rcm.createdDateTime > ?2 OR rcm.updatedDateTime > ?2 OR rcm.deletedDateTime > ?2)")
	List<RegistrationCenterMachine> findAllLatestByMachineIdCreatedUpdatedDeleted(String machineId, LocalDateTime lastUpdated);

}
