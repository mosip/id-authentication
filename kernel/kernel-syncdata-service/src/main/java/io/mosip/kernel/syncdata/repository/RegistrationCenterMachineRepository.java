package io.mosip.kernel.syncdata.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.syncdata.entity.RegistrationCenterMachine;
import io.mosip.kernel.syncdata.entity.id.RegistrationCenterMachineID;

/**
 * Repository to perform CRUD operations on RegistrationCenterMachine.
 * 
 * @author Neha
 * @since 1.0.0
 * @see RegistrationCenterMachine
 * @see JpaRepository
 *
 */
@Repository
public interface RegistrationCenterMachineRepository
		extends JpaRepository<RegistrationCenterMachine, RegistrationCenterMachineID> {
	/**
	 * 
	 * @param machineId - machine id
	 * @return list of {@link RegistrationCenterMachine} - list of registration
	 *         center machine
	 */
	@Query("From RegistrationCenterMachine rcm WHERE rcm.registrationCenterMachinePk.machineId =?1")
	List<RegistrationCenterMachine> findAllByMachineId(String machineId);

	/**
	 * 
	 * @param regCenterId      - registration center id
	 * @param lastUpdated      - last updated time
	 * @param currentTimeStamp - current timestamp
	 * @return list of {@link RegistrationCenterMachine} - list of registration
	 *         center machine
	 * 
	 */
	@Query("From RegistrationCenterMachine rcm WHERE rcm.registrationCenterMachinePk.regCenterId =?1 AND ((rcm.createdDateTime > ?2 AND rcm.createdDateTime<=?3) OR (rcm.updatedDateTime > ?2 AND rcm.updatedDateTime<=?3) OR (rcm.deletedDateTime > ?2 AND rcm.deletedDateTime<=?3))")
	List<RegistrationCenterMachine> findAllLatestCreatedUpdatedDeleted(String regCenterId, LocalDateTime lastUpdated,
			LocalDateTime currentTimeStamp);

	/**
	 * 
	 * @param serialNumber - serial number
	 * @return list of {@link Object} - list of registration center machine
	 */
	@Query(value = "select distinct rcm.regcntr_id , rcm.machine_id from master.reg_center_machine rcm, master.machine_master mm where rcm.machine_id=mm.id and mm.serial_num=?1 and rcm.is_active=true", nativeQuery = true)
	List<Object[]> getRegistrationCenterMachineWithSerialNumber(String serialNumber);

	/**
	 * 
	 * @param macAddress - mac address
	 * @return list of {@link Object} - list of registration center machine
	 */
	@Query(value = "select distinct rcm.regcntr_id , rcm.machine_id from master.reg_center_machine rcm, master.machine_master mm where rcm.machine_id=mm.id and mm.mac_address=?1 and rcm.is_active=true", nativeQuery = true)
	List<Object[]> getRegistrationCenterMachineWithMacAddress(String macAddress);

	/**
	 * 
	 * @param macAddress - mac address
	 * @param serialNum  - serial number
	 * @return list of {@link Object} - list of registration center machine
	 */
	@Query(value = "select distinct rcm.regcntr_id , rcm.machine_id from master.reg_center_machine rcm, master.machine_master mm where rcm.machine_id=mm.id and (mm.mac_address=?1 and mm.serial_num=?2) and rcm.is_active=true", nativeQuery = true)
	List<Object[]> getRegistrationCenterMachineWithMacAddressAndSerialNum(String macAddress, String serialNum);

	/**
	 * 
	 * @param regCenterId - registration center id
	 * @param machineId   - machine id
	 * @return RegistrationCenterMachine - registrationCenterMachine
	 */
	@Query(value = "select * from reg_center_machine where regcntr_id=?1 and machine_id=?2 and is_active=true", nativeQuery = true)
	RegistrationCenterMachine getRegCenterIdWithRegIdAndMachineId(String regCenterId, String machineId);

	@Query(value="select distinct rcm.regcntr_id , rcm.machine_id from master.reg_center_machine rcm, master.machine_master mm where rcm.machine_id=mm.id and (mm.mac_address=?1 and mm.serial_num=?2) and mm.key_index=?3 and rcm.is_active=true",nativeQuery=true)
	List<Object[]>getRegistrationCenterMachineWithMacAddressAndSerialNumAndKeyIndex(String macAddress, String serialNum,String keyIndex);

	@Query(value="select distinct rcm.regcntr_id , rcm.machine_id from master.reg_center_machine rcm, master.machine_master mm where rcm.machine_id=mm.id and mm.mac_address=?1 and mm.key_index=?2 and rcm.is_active=true",nativeQuery=true)
	List<Object[]> getRegistrationCenterMachineWithMacAddressAndKeyIndex(String macAddress,String keyIndex);

	@Query(value="select distinct rcm.regcntr_id , rcm.machine_id from master.reg_center_machine rcm, master.machine_master mm where rcm.machine_id=mm.id and mm.serial_num=?1 and mm.key_index=?2 and rcm.is_active=true",nativeQuery=true)
	List<Object[]> getRegistrationCenterMachineWithSerialNumberAndKeyIndex(String serialNumber,String keyIndex);

	@Query(value="select distinct rcm.regcntr_id , rcm.machine_id from master.reg_center_machine rcm, master.machine_master mm where rcm.machine_id=mm.id  and mm.key_index=?1 and rcm.is_active=true",nativeQuery=true)
	List<Object[]> getRegistrationCenterMachineWithKeyIndex(String keyIndex);
}
