package io.mosip.kernel.masterdata.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.masterdata.dto.RegistrationCenterHierarchyLevelResponseDto;
import io.mosip.kernel.masterdata.dto.RegistrationCenterHolidayDto;
import io.mosip.kernel.masterdata.dto.RegistrationCenterResponseDto;
import io.mosip.kernel.masterdata.service.RegistrationCenterService;

/**
 * This controller class provides registration centers details based on user
 * provided data.
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
@RestController
public class RegistrationCenterController {

	/**
	 * Reference to RegistrationCenterService.
	 */
	@Autowired
	RegistrationCenterService registrationCenterService;

	/**
	 * Function to fetch registration centers list using location code and language
	 * code.
	 * 
	 * @param langCode
	 *            language code for which the registration center needs to be
	 *            searched.
	 * @param locationCode
	 *            location code for which the registration center needs to be
	 *            searched.
	 * @return {@link RegistrationCenterResponseDto}.
	 */
	@GetMapping("/getlocspecificregistrationcenters/{langCode}/{locationcode}")
	public RegistrationCenterResponseDto getRegistrationCenterDetailsByLocationCode(
			@PathVariable("langCode") String langCode, @PathVariable("locationcode") String locationCode) {
		return registrationCenterService.getRegistrationCentersByLocationCodeAndLanguageCode(locationCode, langCode);
	}

	/**
	 * Function to fetch specific registration center holidays by registration
	 * center id , year and language code.
	 * 
	 * @param langCode
	 *            langCode of required center.
	 * @param registrationCenterId
	 *            centerId of required center
	 * @param year
	 *            the year provided by user.
	 * @return {@link RegistrationCenterHolidayDto}
	 */
	@GetMapping("/getregistrationcenterholidays/{langCode}/{registrationcenterid}/{year}")
	public RegistrationCenterHolidayDto getRegistrationCenterHolidays(@PathVariable("langCode") String langCode,
			@PathVariable("registrationcenterid") String registrationCenterId, @PathVariable("year") int year) {
		return registrationCenterService.getRegistrationCenterHolidays(registrationCenterId, year, langCode);
	}

	/**
	 * Function to fetch nearby registration centers using coordinates
	 * 
	 * @param langCode
	 *            langCode of required centers.
	 * @param longitude
	 *            the longitude provided by user.
	 * @param latitude
	 *            the latitude provided by user.
	 * @param proximityDistance
	 *            the proximity distance provided by user.
	 * @return {@link RegistrationCenterResponseDto}
	 */
	@GetMapping("/getcoordinatespecificregistrationcenters/{langCode}/{longitude}/{latitude}/{proximitydistance}")
	public RegistrationCenterResponseDto getCoordinateSpecificRegistrationCenters(
			@PathVariable("langCode") String langCode, @PathVariable("longitude") double longitude,
			@PathVariable("latitude") double latitude, @PathVariable("proximitydistance") int proximityDistance) {
		return registrationCenterService.getRegistrationCentersByCoordinates(longitude, latitude, proximityDistance,
				langCode);
	}

	/**
	 * Function to fetch registration center using centerId and language code.
	 * 
	 * @param registrationCenterId
	 *            centerId of required center.
	 * @param langCode
	 *            langCode of required center.
	 * @return {@link RegistrationCenterResponseDto}
	 */
	@GetMapping("/registrationcenters/{id}/{langCode}")
	public RegistrationCenterResponseDto getSpecificRegistrationCenterById(
			@PathVariable("id") String registrationCenterId, @PathVariable("langCode") String langCode) {
		return registrationCenterService.getRegistrationCentersByIDAndLangCode(registrationCenterId, langCode);
	}

	/**
	 * Function to fetch all registration centers.
	 * 
	 * @return {@link RegistrationCenterResponseDto}
	 */
	@GetMapping("/registrationcenters")
	public RegistrationCenterResponseDto getAllRegistrationCentersDetails() {
		return registrationCenterService.getAllRegistrationCenters();
	}

	/**
	 * Function to fetch list of registration centers based on hierarchy level,text
	 * and language code
	 * 
	 * @param langCode
	 *            input from user
	 * @param hierarchyLevel
	 *            input from user
	 * @param text
	 *            input from user
	 * @return {@link RegistrationCenterHierarchyLevelResponseDto}
	 */
	@GetMapping("/registrationcenters/{langcode}/{hierarchylevelname}/{name}")
	public RegistrationCenterHierarchyLevelResponseDto getRegistrationCenterByHierarchyLevelAndTextAndlangCode(
			@PathVariable("langcode") String langCode, @PathVariable("hierarchylevelname") String hierarchyLevel,
			@PathVariable("name") String text) {
		return registrationCenterService.findRegistrationCenterByHierarchyLevelandTextAndLanguageCode(langCode,
				hierarchyLevel, text);

	}

}
