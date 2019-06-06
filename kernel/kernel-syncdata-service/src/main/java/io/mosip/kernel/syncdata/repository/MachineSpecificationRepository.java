package io.mosip.kernel.syncdata.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import io.mosip.kernel.syncdata.entity.MachineSpecification;

@Repository
public interface MachineSpecificationRepository extends JpaRepository<MachineSpecification, String> {

	/**
	 * Method to fetch the Machine Specification by machine id.
	 * 
	 * @param machineId id of the machine
	 * @return {@link MachineSpecification} - list of machine specification
	 */
	@Query(value = "SELECT ms.id, ms.name, ms.brand, ms.model, ms.mtyp_code, ms.min_driver_ver, ms.descr, ms.lang_code, ms.is_active, ms.cr_by, ms.cr_dtimes, ms.upd_by, ms.upd_dtimes, ms.is_deleted, ms.del_dtimes FROM master.machine_spec ms, master.machine_master mm WHERE ms.id= mm.mspec_id and mm.id=?1", nativeQuery = true)
	List<MachineSpecification> findByMachineId(String machineId);

	/**
	 * Method to fetch the recently created,updated,deleted Machine Specification by
	 * machine id and lastUpdated timeStamp.
	 * 
	 * @param regCenterId      id of the registration center
	 * @param lastUpdated      timeStamp - last updated time
	 * @param currentTimeStamp - currentTimestamp
	 * @return {@link MachineSpecification} -list of machine specification
	 */
	@Query(value = "SELECT ms.id, ms.name, ms.brand, ms.model, ms.mtyp_code, ms.min_driver_ver, ms.descr, ms.lang_code, ms.is_active, ms.cr_by, ms.cr_dtimes, ms.upd_by, ms.upd_dtimes, ms.is_deleted, ms.del_dtimes from master.machine_spec ms where	ms.id in( select distinct mm.mspec_id from master.machine_master mm inner join master.reg_center_machine rcm on	mm.id=rcm.machine_id and rcm.regcntr_id=?1) and ((ms.cr_dtimes > ?2 and ms.cr_dtimes <=?3) or (ms.upd_dtimes >?2 and ms.upd_dtimes <=?3) or (ms.del_dtimes > ?2 and ms.del_dtimes <=?3))", nativeQuery = true)
	List<MachineSpecification> findLatestByRegCenterId(String regCenterId, LocalDateTime lastUpdated,
			LocalDateTime currentTimeStamp);
}
