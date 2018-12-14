package io.mosip.kernel.syncdata.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.syncdata.entity.Holiday;

/**
 * 
 * @author Abhishek Kumar
 * @since 1.0.0
 */
@Repository
public interface HolidayRepository extends BaseRepository<Holiday, Integer> {

	/**
	 * Method to find all recently created,updated,deleted holidays associated to a
	 * registration center which machine belongs to by machine id and lastUpdated
	 * timeStamp.
	 * 
	 * @param machineId
	 *            id of the machine
	 * @param lastUpdated
	 *            timeStamp
	 * @return list of {@link Holiday}
	 */
	@Query(value = "SELECT lh.id, lh.location_code, lh.holiday_date, lh.holiday_name, lh.holiday_desc, lh.lang_code, lh.is_active, lh.cr_by, lh.cr_dtimes, lh.upd_by, lh.upd_dtimes, lh.is_deleted, lh.del_dtimes from  master.registration_center rs ,master.loc_holiday lh, master.reg_center_machine_device rcmd where rs.holiday_loc_code = lh.location_code and rs.id=rcmd.regcntr_id and rcmd.machine_id= ?1  and (lh.cr_dtimes > ?2 or lh.upd_dtimes > ?2 or lh.del_dtimes > ?2)", nativeQuery = true)
	List<Holiday> findAllLatestCreatedUpdateDeletedByMachineId(String machineId, LocalDateTime lastUpdated);

	/**
	 * Method to find all the holidays associated to a registration center which
	 * machine belongs to by machine id and lastUpdated timeStamp.
	 * 
	 * @param machineId
	 *            id of the machine
	 * @return list of {@link Holiday}
	 */
	@Query(value = "SELECT lh.id, lh.location_code, lh.holiday_date, lh.holiday_name, lh.holiday_desc, lh.lang_code, lh.is_active, lh.cr_by, lh.cr_dtimes, lh.upd_by, lh.upd_dtimes, lh.is_deleted, lh.del_dtimes from  master.registration_center rs ,master.loc_holiday lh, master.reg_center_machine_device rcmd where rs.holiday_loc_code = lh.location_code and rs.id=rcmd.regcntr_id and rcmd.machine_id= ?1", nativeQuery = true)
	List<Holiday> findAllByMachineId(String machineId);

}
