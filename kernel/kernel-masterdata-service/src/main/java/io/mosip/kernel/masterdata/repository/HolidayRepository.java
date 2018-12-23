package io.mosip.kernel.masterdata.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.masterdata.entity.Holiday;

/**
 * Repository class for Holiday data
 * 
 * @author Abhishek Kumar
 * @author Sidhant Agarwal
 * @since 1.0.0
 */
@Repository
public interface HolidayRepository extends BaseRepository<Holiday, Integer> {

	/**
	 * get all the holidays for a specific id
	 * 
	 * @param id
	 *            holiday id input from user
	 * @return list of holidays for a particular id
	 */
	List<Holiday> findAllById(int id);

	/**
	 * fetch all the non deleted holidays
	 * 
	 * @return list of {@link Holiday}
	 */
	@Query("FROM Holiday WHERE isDeleted = false or isDeleted is null")
	List<Holiday> findAllNonDeletedHoliday();

	/**
	 * get all the holidays for a specific location code
	 * 
	 * @param locationCode
	 *            - location code Eg: IND
	 * @param langCode
	 *            - language code Eg:ENG
	 * @param year
	 *            - Eg:1971
	 * @return list of holidays
	 */

	@Query(value = "select id, location_code, holiday_date, holiday_name, holiday_desc, lang_code, is_active, cr_by, cr_dtimes, upd_by, upd_dtimes, is_deleted, del_dtimes from master.loc_holiday WHERE location_code = ?1 and lang_code = ?2 and extract(year from holiday_date) = ?3 and (is_deleted = false  or is_deleted is null)", nativeQuery = true)
	List<Holiday> findAllByLocationCodeYearAndLangCode(String locationCode, String langCode, int year);

	/**
	 * get specific holiday by holiday id and language code
	 * 
	 * @param holidayId
	 *            input from user
	 * @param langCode
	 *            input from user
	 * @return list of holidays for the particular holiday id and language code
	 */
	List<Holiday> findHolidayByIdAndHolidayIdLangCode(int holidayId, String langCode);

	/**
	 * 
	 * @param holidayId
	 *            id of the holiday
	 * @return list of holidays for the particular holiday id
	 */
	@Query("FROM Holiday WHERE holidayId.holidayName = ?1 AND holidayId.holidayDate = ?2 AND holidayId.locationCode = ?3 AND (isDeleted is null or isDeleted=false)")
	List<Holiday> findHolidayByHolidayIdAndByIsDeletedFalseOrIsDeletedNull(String holidayName, LocalDate holidayDate,
			String locationCode);

	/**
	 * fetch the holiday by id and location code
	 * 
	 * @param id
	 *            id of the holiday
	 * @param locationCode
	 *            location code of the holiday
	 * @return {@link Holiday}
	 */
	Holiday findHolidayByIdAndHolidayIdLocationCode(int id, String locationCode);

	@Modifying
	@Transactional
	@Query("UPDATE Holiday h SET h.isDeleted=true ,h.deletedDateTime =?1 WHERE h.holidayId.holidayName = ?2 AND h.holidayId.holidayDate = ?3 AND h.holidayId.locationCode = ?4 AND (isDeleted is null OR isDeleted = false)")
	int deleteHolidays(LocalDateTime deletedTime, String holidayName, LocalDate holidayDate, String locationCode);

}
