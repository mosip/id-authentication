package io.mosip.kernel.masterdata.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.masterdata.dto.LocationHierarchyResponseDto;
import io.mosip.kernel.masterdata.dto.LocationResponseDto;
import io.mosip.kernel.masterdata.service.LocationService;

/**
 * 
 * Class handles REST calls with appropriate URLs.Service class
 * {@link LocationService} is called wherein the business logics are
 * handled.
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
	 * This API fetches all location hierachy details irrespective of the
	 * arguments.
	 * 
	 * @return List<LocationHierarchyDto>
	 */
	@GetMapping
	public LocationHierarchyResponseDto getLocationHierarchyDetails(@RequestParam(value = "langcode",required = false,defaultValue = "ENG")String langCode) {
		return locationHierarchyService.getLocationDetails(langCode);

	}
    /**
     * This API fetches location hierachy details based on location code and language code
	 * arguments
     * @param locCode
     * @param langCode
     * @return List<LocationHierarchyDto>
     */
	@GetMapping(value = "/{locCode}/{langCode}")
	public LocationResponseDto getLocationHierarchyByLangCode(@PathVariable String locCode,
			@PathVariable String langCode) {

		return locationHierarchyService.getLocationHierarchyByLangCode(locCode, langCode);

	}
}