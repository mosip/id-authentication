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

	@Query(value = "SELECT rcm.regcntr_id, mm.id, mm.name, mm.mac_address, mm.serial_num, mm.ip_address, mm.mspec_id, mm.lang_code, mm.is_active,mm.validity_end_dtimes, mm.cr_by, mm.cr_dtimes, mm.upd_by, mm.upd_dtimes FROM master.machine_master mm inner join master.reg_center_machine rcm on mm.id = rcm.machine_id where (rcm.is_deleted is null or rcm.is_deleted=false) and (mm.is_deleted is null or mm.is_deleted=false) and rcm.regcntr_id=?1", nativeQuery = true)
	List<Object[]> findByRegCenterIdAndIsDeletedFalseOrIsDeletedIsNullMachine(String regCenterId);

	@Query(value = "select count(*) from master.reg_center_machine where regcntr_id=?1 and (is_deleted is null or is_deleted=false)", nativeQuery = true)
	Long countCenterMachines(String centerId);

	/**
	 * Method that returns the list of registration centers mapped to machines.
	 * 
	 * @param regCenterID
	 *            the center ID of the reg-center which needs to be decommissioned.
	 * @return the list of registration centers mapped to machines.
	 */
	@Query(value = "FROM RegistrationCenterMachine rm WHERE rm.registrationCenterMachinePk.regCenterId =?1 and (rm.isDeleted is null or rm.isDeleted =false) and rm.isActive = true")
	List<RegistrationCenterMachine> findRegCenterMachineMappings(String regCenterID);
	
	@Query("FROM RegistrationCenterMachine rm where  (rm.isDeleted is null or rm.isDeleted =false) and rm.isActive = true")
	List<RegistrationCenterMachine> findAllCenterMachines();
    
	@Query("FROM RegistrationCenterMachine rm where rm.registrationCenterMachinePk.regCenterId=?1 and rm.registrationCenterMachinePk.machineId=?2 and rm.langCode=?3")
	RegistrationCenterMachine findByRegIdAndMachineId(String regId,String machineId,String langCode);
	
	@Query("FROM RegistrationCenterMachine rm where rm.registrationCenterMachinePk.machineId=?1 and rm.langCode=?2 and rm.isActive=true")
	RegistrationCenterMachine findByMachineId(String machineId,String langCode);
}
