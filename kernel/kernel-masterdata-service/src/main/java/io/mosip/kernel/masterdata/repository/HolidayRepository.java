package io.mosip.kernel.masterdata.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.masterdata.entity.Holiday;

/**
 * Repository class for Holiday data
 * 
 * @author Abhishek Kumar
 * @author Sidhant Agarwal
 * @version 1.0.0
 * @since 23-10-2018
 */
@Repository
public interface HolidayRepository extends BaseRepository<Holiday, Integer> {

	/**
	 * get all the holidays for a specific id
	 * 
	 * @param id
	 *            input from user
	 * @return list of holidays for a particular id
	 */
	List<Holiday> findAllById(int id);

	/**
	 * get all the holidays for a specific location code
	 * @param locationCode - location code Eg: IND
	 * @param langCode - language code Eg:ENG
	 * @param year - Eg:1971
	 * @return
	 */

	@Query(value = "select id, location_code, holiday_date, holiday_name, holiday_desc, lang_code, is_active, cr_by, cr_dtimes, upd_by, upd_dtimes, is_deleted, del_dtimes from master.loc_holiday WHERE location_code = ?1 and lang_code = ?2 and extract(year from holiday_date) = ?3 and is_deleted = false", nativeQuery = true)
	List<Holiday> findAllByLocationCodeYearAndLangCode(String locationCode, String langCode, int year);

	/**
	 * get specific holiday by holiday id and language code
	 * 
	 * @param holidayId
	 *            input from user
	 * @param langCode
	 *            input from user
	 * @return list of holidays for the particular hoilday id and language code
	 */
	List<Holiday> findHolidayByIdAndHolidayIdLangCode(int holidayId, String langCode);

}
