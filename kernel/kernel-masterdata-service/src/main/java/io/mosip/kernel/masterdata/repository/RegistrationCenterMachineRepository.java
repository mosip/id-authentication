package io.mosip.kernel.masterdata.repository;

import java.util.List;
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

	@Query("FROM RegistrationCenterMachine WHERE registrationCenterMachinePk =?1 and (isDeleted is null or isDeleted =false) and isActive = true")
	Optional<RegistrationCenterMachine> findAllNondeletedMappings(
			RegistrationCenterMachineID registrationCenterMachinePk);

	@Query("FROM RegistrationCenterMachine rm where rm.registrationCenterMachinePk.machineId = ?1 AND (rm.isDeleted is null or rm.isDeleted=false) and rm.isActive = true")
	List<RegistrationCenterMachine> findByMachineIdAndIsDeletedFalseOrIsDeletedIsNull(String machineId);

	@Query("FROM RegistrationCenterMachine rm where rm.registrationCenterMachinePk.regCenterId = ?1 AND (rm.isDeleted is null or rm.isDeleted=false) and rm.isActive = true")
	List<RegistrationCenterMachine> findByRegCenterIdAndIsDeletedFalseOrIsDeletedIsNull(String regCenterId);

}
