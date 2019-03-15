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

	@Query("From RegistrationCenterMachine rcm WHERE rcm.registrationCenterMachinePk.regCenterId =?1 AND ((rcm.createdDateTime > ?2 AND rcm.createdDateTime<=?3) OR (rcm.updatedDateTime > ?2 AND rcm.updatedDateTime<=?3) OR (rcm.deletedDateTime > ?2 AND rcm.deletedDateTime<=?3))")
	List<RegistrationCenterMachine> findAllLatestCreatedUpdatedDeleted(String regCenterId, LocalDateTime lastUpdated,LocalDateTime currentTimeStamp);
	
	@Query(value = "select distinct rcm.regcntr_id , rcm.machine_id from master.reg_center_machine rcm, master.machine_master mm where rcm.machine_id=mm.id and mm.serial_num=?1 and rcm.is_active=true", nativeQuery = true)
	List<Object[]> getRegistrationCenterMachineWithSerialNumber(String serialNumber);

	@Query(value = "select distinct rcm.regcntr_id , rcm.machine_id from master.reg_center_machine rcm, master.machine_master mm where rcm.machine_id=mm.id and mm.mac_address=?1 and rcm.is_active=true", nativeQuery = true)
	List<Object[]> getRegistrationCenterMachineWithMacAddress(String macAddress);

	@Query(value = "select distinct rcm.regcntr_id , rcm.machine_id from master.reg_center_machine rcm, master.machine_master mm where rcm.machine_id=mm.id and (mm.mac_address=?1 and mm.serial_num=?2) and rcm.is_active=true", nativeQuery = true)
	List<Object[]> getRegistrationCenterMachineWithMacAddressAndSerialNum(String macAddress,
			String serialNum);

	@Query(value = "select * from reg_center_machine where regcntr_id=?1 and machine_id=?2 and is_active=true", nativeQuery = true)
	RegistrationCenterMachine getRegCenterIdWithRegIdAndMachineId(String regId, String machineId);

}
