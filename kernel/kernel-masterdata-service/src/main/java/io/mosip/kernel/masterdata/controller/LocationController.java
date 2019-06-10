package io.mosip.kernel.masterdata.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.http.ResponseFilter;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.masterdata.dto.LocationDto;
import io.mosip.kernel.masterdata.dto.getresponse.LocationHierarchyResponseDto;
import io.mosip.kernel.masterdata.dto.getresponse.LocationResponseDto;
import io.mosip.kernel.masterdata.dto.getresponse.StatusResponseDto;
import io.mosip.kernel.masterdata.dto.postresponse.CodeResponseDto;
import io.mosip.kernel.masterdata.dto.postresponse.PostLocationCodeResponseDto;
import io.mosip.kernel.masterdata.service.LocationService;
import io.swagger.annotations.Api;

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
@Api(tags = { "Location" })
@RequestMapping(value = "/locations")
public class LocationController {

	/**
	 * Creates an instance of {@link LocationService}
	 */
	@Autowired
	private LocationService locationHierarchyService;

	/**
	 * This API fetches all location hierachy details irrespective of the arguments.
	 * 
	 * @param langcode
	 *            language code
	 * @return list of location hierarchies
	 */
	@PreAuthorize("hasAnyRole('INDIVIDUAL','ID_AUTHENTICATION', 'REGISTRATION_ADMIN', 'REGISTRATION_SUPERVISOR', 'REGISTRATION_OFFICER', 'REGISTRATION_PROCESSOR','ZONAL_ADMIN','ZONAL_APPROVER')")
	@ResponseFilter
	@GetMapping(value = "/{langcode}")
	public ResponseWrapper<LocationHierarchyResponseDto> getLocationHierarchyDetails(@PathVariable String langcode) {
		ResponseWrapper<LocationHierarchyResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(locationHierarchyService.getLocationDetails(langcode));
		return responseWrapper;
	}

	@ResponseFilter
	@PostMapping()
	public ResponseWrapper<PostLocationCodeResponseDto> createLocationHierarchyDetails(
			@Valid @RequestBody RequestWrapper<LocationDto> locationRequestDto) {

		ResponseWrapper<PostLocationCodeResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(locationHierarchyService.createLocationHierarchy(locationRequestDto.getRequest()));
		return responseWrapper;
	}

	/**
	 * 
	 * @param locationCode
	 *            location code
	 * @param langCode
	 *            language code
	 * @return list of location hierarchies
	 */
	@ResponseFilter
	@GetMapping(value = "/{locationcode}/{langcode}")
	public ResponseWrapper<LocationResponseDto> getLocationHierarchyByLangCode(
			@PathVariable("locationcode") String locationCode, @PathVariable("langcode") String langCode) {

		ResponseWrapper<LocationResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(locationHierarchyService.getLocationHierarchyByLangCode(locationCode, langCode));
		return responseWrapper;
	}

	/**
	 * @param hierarchyName
	 *            hierarchy Name
	 * @return list of location hierarchies
	 */
	@PreAuthorize("hasAnyRole('INDIVIDUAL','ID_AUTHENTICATION', 'REGISTRATION_ADMIN', 'REGISTRATION_SUPERVISOR', 'REGISTRATION_OFFICER', 'REGISTRATION_PROCESSOR')")
	@ResponseFilter
	@GetMapping(value = "/locationhierarchy/{hierarchyname}")
	public ResponseWrapper<LocationResponseDto> getLocationDataByHierarchyName(
			@PathVariable(value = "hierarchyname") String hierarchyName) {

		ResponseWrapper<LocationResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(locationHierarchyService.getLocationDataByHierarchyName(hierarchyName));
		return responseWrapper;

	}

	/**
	 * 
	 * @param locationRequestDto
	 *            - location request DTO
	 * @return PostLocationCodeResponseDto
	 */
	@PreAuthorize("hasAnyRole('CENTRAL_ADMIN')")
	@ResponseFilter
	@PutMapping
	public ResponseWrapper<PostLocationCodeResponseDto> updateLocationHierarchyDetails(
			@Valid @RequestBody RequestWrapper<LocationDto> locationRequestDto) {

		ResponseWrapper<PostLocationCodeResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(locationHierarchyService.updateLocationDetails(locationRequestDto.getRequest()));
		return responseWrapper;
	}

	/**
	 * This API call would update isDeleted to true when called.
	 * 
	 * @param locationCode
	 *            -location code
	 * @return CodeResponseDto
	 */
	@ResponseFilter
	@DeleteMapping(value = "/{locationcode}")
	public ResponseWrapper<CodeResponseDto> deleteLocationHierarchyDetails(
			@PathVariable(value = "locationcode") String locationCode) {
		ResponseWrapper<CodeResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(locationHierarchyService.deleteLocationDetials(locationCode));
		return responseWrapper;
	}

	/**
	 * 
	 * @param locationCode
	 *            location code
	 * @param langCode
	 *            language code
	 * @return list of location hierarchies
	 */
	@PreAuthorize("hasAnyRole('INDIVIDUAL')")
	@ResponseFilter
	@GetMapping(value = "immediatechildren/{locationcode}/{langcode}")
	public ResponseWrapper<LocationResponseDto> getImmediateChildrenByLocCodeAndLangCode(
			@PathVariable("locationcode") String locationCode, @PathVariable("langcode") String langCode) {

		ResponseWrapper<LocationResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper
				.setResponse(locationHierarchyService.getImmediateChildrenByLocCodeAndLangCode(locationCode, langCode));
		return responseWrapper;
	}

	/**
	 * checks whether the given location name is valid or not
	 * 
	 * @param locationName
	 * @return StatusResponseCode
	 */
	@PreAuthorize("hasAnyRole('REGISTRATION_PROCESSOR')")
	@ResponseFilter
	@GetMapping(value = "/validate/{locationname}")
	public ResponseWrapper<StatusResponseDto> validateLocationName(@PathVariable("locationname") String locationName) {
		ResponseWrapper<StatusResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(locationHierarchyService.validateLocationName(locationName));
		return responseWrapper;

	}

}
