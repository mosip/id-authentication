package io.mosip.kernel.masterdata.service;

import io.mosip.kernel.masterdata.dto.RegistrationCenterHierarchyLevelResponseDto;
import io.mosip.kernel.masterdata.dto.RegistrationCenterHolidayDto;
import io.mosip.kernel.masterdata.dto.RegistrationCenterResponseDto;

/**
 * This interface contains methods that provides registration centers details
 * based on user provided data.
 * 
 * @author Dharmesh Khandelwal
 * @author Abhishek Kumar
 * @author Urvil Joshi
 * @author Ritesh Sinha
 * @author Sagar Mahapatra
 * @author Sidhant Agarwal
 * @since 1.0.0
 *
 */
public interface RegistrationCenterService {

	/**
	 * Function to fetch specific registration center holidays by registration
	 * center id , year and language code
	 * 
	 * @param registrationCenterId
	 *            centerId of required center
	 * @param year
	 *            the year provided by user.
	 * @param langCode
	 *            languageCode of required center.
	 * @return {@link RegistrationCenterHolidayDto}
	 */
	RegistrationCenterHolidayDto getRegistrationCenterHolidays(String registrationCenterId, int year, String langCode);

	/**
	 * Function to fetch nearby registration centers using coordinates.
	 * 
	 * @param longitude
	 *            the longitude provided by user.
	 * @param latitude
	 *            the latitude provided by user.
	 * @param proximityDistance
	 *            the proximity distance provided by user.
	 * @param langCode
	 *            languageCode of required centers.
	 * @return {@link RegistrationCenterResponseDto}
	 */
	RegistrationCenterResponseDto getRegistrationCentersByCoordinates(double longitude, double latitude,
			int proximityDistance, String langCode);

	/**
	 * Function to fetch registration center using centerId and language code.
	 * 
	 * @param registrationCenterId
	 *            centerId of required center.
	 * @param langCode
	 *            languageCode of required center.
	 * @return {@link RegistrationCenterResponseDto}
	 */
	RegistrationCenterResponseDto getRegistrationCentersByIDAndLangCode(String registrationCenterId, String langCode);

	/**
	 * Function to fetch registration centers list using location code and language
	 * code.
	 * 
	 * @param locationCode
	 *            location code for which the registration center needs to be
	 *            searched.
	 * @param langCode
	 *            language code for which the registration center needs to be
	 *            searched.
	 * @return the list of registration centers.
	 */
	RegistrationCenterResponseDto getRegistrationCentersByLocationCodeAndLanguageCode(String locationCode,
			String langCode);

	/**
	 * Function to fetch all registration centers list.
	 * 
	 * @return the list of all registration centers.
	 */
	public RegistrationCenterResponseDto getAllRegistrationCenters();

	/**
	 * Function to fetch list of registration centers based on hierarchy level,text
	 * input and language code
	 * 
	 * @param hierarchyLevel
	 *            input from user
	 * @param text
	 *            input from user
	 * @param languageCode
	 *            input from user
	 * @return list of registration centers
	 */
	public RegistrationCenterHierarchyLevelResponseDto findRegistrationCenterByHierarchyLevelandTextAndLanguageCode(
			String hierarchyLevel, String text, String languageCode);
}
