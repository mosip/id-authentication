package io.mosip.kernel.masterdata.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.masterdata.dto.LocationDto;
import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.dto.getresponse.LocationHierarchyResponseDto;
import io.mosip.kernel.masterdata.dto.getresponse.LocationResponseDto;
import io.mosip.kernel.masterdata.dto.postresponse.PostLocationCodeResponseDto;
import io.mosip.kernel.masterdata.service.LocationService;

/**
 * 
 * Class handles REST calls with appropriate URLs.Service class
 * {@link LocationService} is called wherein the business logics are handled.
 * 
 * @author Srinivasan
 * @since 1.0.0
 *
 */
@RestController
@RequestMapping(value = "/v1.0/locations")
public class LocationController {

	/**
	 * Creates an instance of {@link LocationService}
	 */
	@Autowired
	private LocationService locationHierarchyService;

	/**
	 * This API fetches all location hierachy details irrespective of the arguments.
	 * @param langcode language code
	 * @return  list of location hierarchies
	 */
	@GetMapping(value = "/{langcode}")
	public LocationHierarchyResponseDto getLocationHierarchyDetails(@PathVariable String langcode) {
		return locationHierarchyService.getLocationDetails(langcode);

	}

	@PostMapping()
	public ResponseEntity<PostLocationCodeResponseDto> createLocationHierarchyDetails(@Valid@RequestBody RequestDto<LocationDto> locationRequestDto) {
		
		return new ResponseEntity<>(locationHierarchyService.createLocationHierarchy(locationRequestDto),HttpStatus.CREATED);
	}

	/**
	 * 
	 * @param locationCode
	 *                location code
	 * @param langCode
	 *                language code
	 * @return list of location hierarchies
	 */
	@GetMapping(value = "/{locationcode}/{langcode}")
	public LocationResponseDto getLocationHierarchyByLangCode(@PathVariable("locationcode") String locationCode,
			@PathVariable("langcode") String langCode) {

		return locationHierarchyService.getLocationHierarchyByLangCode(locationCode, langCode);

	}
}