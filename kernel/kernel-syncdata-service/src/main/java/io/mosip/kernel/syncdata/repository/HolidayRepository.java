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
	 * get all the holidays for a specific id
	 * 
	 * @param id
	 * @return List<Holiday>
	 */
	List<Holiday> findAllByHolidayIdId(int id);

	/**
	 * get all the holidays for a specific location code
	 * 
	 * @param locationCode
	 * @return List<Holiday>
	 */

	@Query(value = "select id, location_code, holiday_date, holiday_name, holiday_desc, lang_code, is_active, cr_by, cr_dtimes, upd_by, upd_dtimes, is_deleted, del_dtimes from master.loc_holiday WHERE location_code = ?1 and lang_code = ?2 and extract(year from holiday_date) = ?3 and is_deleted = false", nativeQuery = true)
	List<Holiday> findAllByLocationCodeYearAndLangCode(String locationCode, String langCode, int year);

	/**
	 * get specific holiday by holiday id and language code
	 * 
	 * @param holidayId
	 * @param langCode
	 * @return {@link Holiday}
	 */
	List<Holiday> findHolidayByHolidayIdIdAndHolidayIdLangCode(int holidayId, String langCode);

	@Query(value = "SELECT lh.id, lh.location_code, lh.holiday_date, lh.holiday_name, lh.holiday_desc, lh.lang_code, lh.is_active, lh.cr_by, lh.cr_dtimes, lh.upd_by, lh.upd_dtimes, lh.is_deleted, lh.del_dtimes from  master.registration_center rs ,master.loc_holiday lh, master.reg_center_machine_device rcmd where rs.holiday_loc_code = lh.location_code and rs.id=rcmd.regcntr_id and rcmd.machine_id= ?1  and (lh.cr_dtimes > ?2 or lh.upd_dtimes > ?2 or lh.del_dtimes > ?2)", nativeQuery = true)
	List<Holiday> findAllLatestCreatedUpdateDeletedByMachineId(String machineId, LocalDateTime lastUpdated);

	@Query(value = "SELECT lh.id, lh.location_code, lh.holiday_date, lh.holiday_name, lh.holiday_desc, lh.lang_code, lh.is_active, lh.cr_by, lh.cr_dtimes, lh.upd_by, lh.upd_dtimes, lh.is_deleted, lh.del_dtimes from  master.registration_center rs ,master.loc_holiday lh, master.reg_center_machine_device rcmd where rs.holiday_loc_code = lh.location_code and rs.id=rcmd.regcntr_id and rcmd.machine_id= ?1", nativeQuery = true)
	List<Holiday> findAllByMachineId(String machineId);

}
