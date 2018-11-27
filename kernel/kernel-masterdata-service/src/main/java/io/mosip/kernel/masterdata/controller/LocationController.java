package io.mosip.kernel.masterdata.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.masterdata.dto.LocationCodeResponseDto;
import io.mosip.kernel.masterdata.dto.LocationHierarchyResponseDto;
import io.mosip.kernel.masterdata.dto.LocationRequestDto;
import io.mosip.kernel.masterdata.dto.LocationResponseDto;
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
@RequestMapping(value = "/locations")
public class LocationController {

	/**
	 * Creates an instance of {@link LocationService}
	 */
	@Autowired
	LocationService locationHierarchyService;

	/**
	 * This API fetches all location hierachy details irrespective of the arguments.
	 * 
	 * @return List<LocationHierarchyDto>
	 */
	@GetMapping(value = "/{langcode}")
	public LocationHierarchyResponseDto getLocationHierarchyDetails(@PathVariable String langcode) {
		return locationHierarchyService.getLocationDetails(langcode);

	}

	@PostMapping()
	public LocationCodeResponseDto saveLocationHierarchyDetails(@RequestBody LocationRequestDto locationRequestDto) {
		return locationHierarchyService.saveLocationHierarchy(locationRequestDto);
	}

	/**
	 * This API fetches location hierarchy details based on location code and
	 * language code arguments
	 * 
	 * @param locCode
	 * @param langCode
	 * @return List<LocationHierarchyDto>
	 */
	@GetMapping(value = "/{locationcode}/{langcode}")
	public LocationResponseDto getLocationHierarchyByLangCode(@PathVariable("locationcode") String locationCode,
			@PathVariable("langcode") String langCode) {

		return locationHierarchyService.getLocationHierarchyByLangCode(locationCode, langCode);

	}
}