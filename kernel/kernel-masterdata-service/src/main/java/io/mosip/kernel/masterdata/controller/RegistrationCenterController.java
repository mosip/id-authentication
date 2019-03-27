package io.mosip.kernel.masterdata.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.masterdata.dto.RegistrationCenterDto;
import io.mosip.kernel.masterdata.dto.RegistrationCenterHolidayDto;
import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.http.ResponseFilter;
import io.mosip.kernel.masterdata.dto.getresponse.RegistrationCenterResponseDto;
import io.mosip.kernel.masterdata.dto.getresponse.ResgistrationCenterStatusResponseDto;
import io.mosip.kernel.masterdata.dto.postresponse.IdResponseDto;
import io.mosip.kernel.masterdata.entity.id.IdAndLanguageCodeID;
import io.mosip.kernel.masterdata.service.RegistrationCenterService;
import io.swagger.annotations.Api;

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
 * @author Srinivasan
 * @author Uday Kumar
 * @since 1.0.0
 *
 */
@RestController
@Api(tags = { "Registration" })
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
	 * @return {@link RegistrationCenterResponseDto} RegistrationCenterResponseDto
	 */
	@ResponseFilter
	@GetMapping("/getlocspecificregistrationcenters/{langcode}/{locationcode}")
	public RegistrationCenterResponseDto getRegistrationCenterDetailsByLocationCode(
			@PathVariable("langcode") String langCode, @PathVariable("locationcode") String locationCode) {
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
	 * @return {@link RegistrationCenterHolidayDto} RegistrationCenterHolidayDto
	 */
	@ResponseFilter
	@GetMapping("/getregistrationcenterholidays/{langcode}/{registrationcenterid}/{year}")
	public RegistrationCenterHolidayDto getRegistrationCenterHolidays(@PathVariable("langcode") String langCode,
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
	 * @return {@link RegistrationCenterResponseDto} RegistrationCenterResponseDto
	 */
	@ResponseFilter
	@GetMapping("/getcoordinatespecificregistrationcenters/{langcode}/{longitude}/{latitude}/{proximitydistance}")
	public RegistrationCenterResponseDto getCoordinateSpecificRegistrationCenters(
			@PathVariable("langcode") String langCode, @PathVariable("longitude") double longitude,
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
	 * @return {@link RegistrationCenterResponseDto} RegistrationCenterResponseDto
	 */
	@ResponseFilter
	@GetMapping("/registrationcenters/{id}/{langcode}")
	public RegistrationCenterResponseDto getSpecificRegistrationCenterById(
			@PathVariable("id") String registrationCenterId, @PathVariable("langcode") String langCode) {
		return registrationCenterService.getRegistrationCentersByIDAndLangCode(registrationCenterId, langCode);
	}

	/**
	 * Function to fetch all registration centers.
	 * 
	 * @return {@link RegistrationCenterResponseDto} RegistrationCenterResponseDto
	 */
	@ResponseFilter
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
	 * @param name
	 *            input from user
	 * @return {@link RegistrationCenterResponseDto} RegistrationCenterResponseDto
	 */
	@ResponseFilter
	@GetMapping("/registrationcenters/{langcode}/{hierarchylevel}/{name}")
	public RegistrationCenterResponseDto getRegistrationCenterByHierarchyLevelAndTextAndlangCode(
			@PathVariable("langcode") String langCode, @PathVariable("hierarchylevel") Short hierarchyLevel,
			@PathVariable("name") String name) {
		return registrationCenterService.findRegistrationCenterByHierarchyLevelandTextAndLanguageCode(langCode,
				hierarchyLevel, name);

	}

	/**
	 * Check whether the time stamp sent for the given registration center id is not
	 * a holiday and is in between working hours.
	 * 
	 * @param regId
	 *            - registration center id
	 * @param langCode
	 *            - language code
	 * @param timeStamp
	 *            - timestamp based on the format YYYY-MM-ddTHH:mm:ss.SSSZ
	 * @return {@link ResgistrationCenterStatusResponseDto} -
	 *         RegistrationCenterStatusResponseDto
	 */
	@ResponseFilter
	@GetMapping("/registrationcenters/validate/{id}/{langCode}/{timestamp}")
	public ResgistrationCenterStatusResponseDto validateTimestamp(@PathVariable("id") String regId,
			@PathVariable("langCode") String langCode, @PathVariable("timestamp") String timeStamp) {
		return registrationCenterService.validateTimeStampWithRegistrationCenter(regId, langCode, timeStamp);

	}

	/**
	 * This method creates registration center.
	 * 
	 * @param registrationCenterDto
	 *            the request DTO for creating registration center.
	 * @return the response i.e. the id of the registration center created.
	 */
	@ResponseFilter
	@PostMapping("/registrationcenters")
	public ResponseEntity<IdResponseDto> createRegistrationCenter(
			@RequestBody @Valid RequestWrapper<RegistrationCenterDto> registrationCenterDto) {
		return new ResponseEntity<>(registrationCenterService.createRegistrationCenter(registrationCenterDto.getRequest()),
				HttpStatus.OK);
	}

	/**
	 * This method updates registration center.
	 * 
	 * @param registrationCenterDto
	 *            the request DTO for updating registration center.
	 * @return the response i.e. the id of the registration center updated.
	 */
	@ResponseFilter
	@PutMapping("/registrationcenters")
	public ResponseEntity<IdAndLanguageCodeID> updateRegistrationCenter(
			@RequestBody @Valid RequestWrapper<RegistrationCenterDto> registrationCenterDto) {
		return new ResponseEntity<>(registrationCenterService.updateRegistrationCenter(registrationCenterDto),
				HttpStatus.OK);

	}

	@ResponseFilter
	@DeleteMapping("/registrationcenters/{registrationCenterId}")
	public ResponseEntity<IdResponseDto> deleteRegistrationCenter(
			@PathVariable("registrationCenterId") String registrationCenterId) {
		return new ResponseEntity<>(registrationCenterService.deleteRegistrationCenter(registrationCenterId),
				HttpStatus.OK);

	}

	/**
	 * Function to fetch list of registration centers based on hierarchy level,List
	 * of text and language code
	 * 
	 * @param langCode
	 *            input from user
	 * @param hierarchyLevel
	 *            input from user
	 * @param names
	 *            input from user
	 * @return {@link RegistrationCenterResponseDto} RegistrationCenterResponseDto
	 */
	@ResponseFilter
	@GetMapping("/registrationcenters/{langcode}/{hierarchylevel}/names")
	public RegistrationCenterResponseDto getRegistrationCenterByHierarchyLevelAndListTextAndlangCode(
			@PathVariable("langcode") String langCode, @PathVariable("hierarchylevel") Short hierarchyLevel,
			@RequestParam("name") List<String> names) {
		return registrationCenterService.findRegistrationCenterByHierarchyLevelAndListTextAndlangCode(langCode,
				hierarchyLevel, names);
	}

}
