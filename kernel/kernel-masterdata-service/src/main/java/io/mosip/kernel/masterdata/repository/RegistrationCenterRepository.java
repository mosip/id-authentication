package io.mosip.kernel.masterdata.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.masterdata.entity.RegistrationCenter;

/**
 * @author Dharmesh Khandelwal
 * @author Abhishek Kumar
 * @author Sidhant Agarwal
 * @since 1.0.0
 *
 */
@Repository
public interface RegistrationCenterRepository extends BaseRepository<RegistrationCenter, String> {

	List<RegistrationCenter> findAllByIsActiveTrueAndIsDeletedFalse(Class<RegistrationCenter> entityClass);

	/**
	 * This method trigger query to fetch registration centers based on
	 * latitude,longitude,proximity distance and language code
	 * 
	 * @param latitude
	 *            latitude provided by user
	 * @param longitude
	 *            longitude provided by user
	 * @param proximityDistance
	 *            proximityDistance provided by user as a radius
	 * @param langCode
	 *            langCode provided by user
	 * @return List<RegistrationCenter> fetched from database
	 */
	@Query
	List<RegistrationCenter> findRegistrationCentersByLat(@Param("latitude") double latitude,
			@Param("longitude") double longitude, @Param("proximitydistance") double proximityDistance,
			@Param("langcode") String langCode);

	/**
	 * This method trigger query to fetch registration centers based on id and
	 * language code.
	 * 
	 * @param id
	 *            the centerId
	 * @param languageCode
	 *            the languageCode
	 * @return the RegistrationCenter
	 */
	RegistrationCenter findByIdAndLanguageCodeAndIsDeletedFalse(String id, String languageCode);

	String findRegistrationCenterHolidayLocationCodeByIdAndLanguageCode(String id, String languageCode);

	/**
	 * This method trigger query to fetch registration centers based on locationCode
	 * and language code.
	 * 
	 * @param locationCode
	 *            locationCode provided by user
	 * @param languageCode
	 *            languageCode provided by user
	 * @return List<RegistrationCenter> fetched from database
	 */
	List<RegistrationCenter> findByLocationCodeAndLanguageCodeAndIsDeletedFalse(String locationCode,
			String languageCode);

	/**
	 * This method trigger query to fetch registration centers based on hierarchy
	 * level,text input and language code
	 * 
	 * @param languageCode
	 *            provided by user
	 * @param hierarchyLevel
	 *            provided by user
	 * @param text
	 *            provided by user
	 * @return List<RegistrationCenter> fetched from database
	 */
	@Query(value = "SELECT r.id, r.name, r.cntrtyp_code, r.addr_line1, r.addr_line2, r.addr_line3,r.number_of_kiosks,r.per_kiosk_process_time,r.center_end_time,r.center_start_time,r.time_zone,r.contact_person,r.lunch_start_time,r.lunch_end_time,r.latitude, r.longitude, r.location_code,r.holiday_loc_code,r.contact_phone, r.working_hours, r.lang_code,r.is_active, r.cr_by,r.cr_dtimes, r.upd_by,r.upd_dtimes, r.is_deleted, r.del_dtimes FROM master.registration_center r JOIN master.location loc ON r.location_code = loc.code WHERE loc.lang_code = ?1 AND loc.hierarchy_level_name = ?2 AND UPPER(loc.name) = UPPER(?3)", nativeQuery = true)
	List<RegistrationCenter> findRegistrationCenterHierarchyLevelName(String languageCode, String hierarchyLevel,
			String text);
}
