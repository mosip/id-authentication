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
	RegistrationCenter findByIdAndLanguageCodeAndIsActiveTrueAndIsDeletedFalse(String id, String languageCode);

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
	List<RegistrationCenter> findByLocationCodeAndLanguageCodeAndIsActiveTrueAndIsDeletedFalse(String locationCode, String languageCode);

}
