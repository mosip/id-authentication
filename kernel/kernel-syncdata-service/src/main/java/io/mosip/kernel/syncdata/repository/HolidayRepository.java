package io.mosip.kernel.syncdata.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.syncdata.entity.Holiday;

/**
 * 
 * @author Abhishek Kumar
 * @since 1.0.0
 */
@Repository
public interface HolidayRepository extends JpaRepository<Holiday, Integer> {

	/**
	 * Method to find all recently created,updated,deleted holidays associated to a
	 * registration center which machine belongs to by machine id and lastUpdated
	 * timeStamp.
	 * 
	 * @param machineId        id of the machine
	 * @param lastUpdated      timeStamp - last updated time stamp
	 * @param currentTimeStamp - currentTimestamp
	 * @return list of {@link Holiday} - list of holiday
	 */
	@Query(value = "select lh.id, lh.location_code, lh.holiday_date, lh.holiday_name, lh.holiday_desc, lh.lang_code, lh.is_active, lh.cr_by, lh.cr_dtimes, lh.upd_by, lh.upd_dtimes, lh.is_deleted, lh.del_dtimes from master.loc_holiday lh join master.registration_center rc  on lh.location_code = rc.holiday_loc_code join master.reg_center_machine rcm on rcm.regcntr_id = rc.id and rc.lang_code = rcm.lang_code where rcm.machine_id=?1  and ((lh.cr_dtimes > ?2 and lh.cr_dtimes <=?3) or (lh.upd_dtimes > ?2 and lh.upd_dtimes <=?3) or (lh.del_dtimes > ?2 and lh.del_dtimes <=?3))", nativeQuery = true)
	List<Holiday> findAllLatestCreatedUpdateDeletedByMachineId(String machineId, LocalDateTime lastUpdated,
			LocalDateTime currentTimeStamp);

	/**
	 * Method to find all the holidays associated to a registration center which
	 * machine belongs to by machine id and lastUpdated timeStamp.
	 * 
	 * @param machineId id of the machine
	 * @return list of {@link Holiday} - list of holiday
	 */
	@Query(value = "SELECT lh.id, lh.location_code, lh.holiday_date, lh.holiday_name, lh.holiday_desc, lh.lang_code, lh.is_active, lh.cr_by, lh.cr_dtimes, lh.upd_by, lh.upd_dtimes, lh.is_deleted, lh.del_dtimes from  master.registration_center rs ,master.loc_holiday lh, master.reg_center_machine_device rcmd where rs.holiday_loc_code = lh.location_code and rs.id=rcmd.regcntr_id and rcmd.machine_id= ?1", nativeQuery = true)
	List<Holiday> findAllByMachineId(String machineId);

}
