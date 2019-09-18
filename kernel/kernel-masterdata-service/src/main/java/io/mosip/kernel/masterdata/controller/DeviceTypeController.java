package io.mosip.kernel.masterdata.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.http.ResponseFilter;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.masterdata.constant.OrderEnum;
import io.mosip.kernel.masterdata.dto.DeviceTypeDto;
import io.mosip.kernel.masterdata.dto.getresponse.PageDto;
import io.mosip.kernel.masterdata.dto.getresponse.extn.DeviceTypeExtnDto;
import io.mosip.kernel.masterdata.dto.request.FilterValueDto;
import io.mosip.kernel.masterdata.dto.request.SearchDto;
import io.mosip.kernel.masterdata.dto.response.FilterResponseDto;
import io.mosip.kernel.masterdata.dto.response.PageResponseDto;
import io.mosip.kernel.masterdata.entity.id.CodeAndLanguageCodeID;
import io.mosip.kernel.masterdata.service.DeviceTypeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * Controller with api to save Device Type Details
 * 
 * @author Megha Tanga
 * @author Ayush Saxena
 * @since 1.0.0
 *
 */

@RestController
@Api(tags = { "DeviceType" })
public class DeviceTypeController {

	/**
	 * Reference to deviceTypeService.
	 */
	@Autowired
	private DeviceTypeService deviceTypeService;

	/**
	 * Save list of device Type details to the Database
	 * 
	 * @param deviceTypes
	 *            input from user Device Type DTO
	 * 
	 * @return {@link CodeAndLanguageCodeID}
	 */
	@ResponseFilter
	@PostMapping("/devicetypes")
	@PreAuthorize("hasRole('GLOBAL_ADMIN')")
	@ApiOperation(value = "Service to save Device Type", notes = "Saves Device Type and return Device Code and Languge Code")
	@ApiResponses({
			@ApiResponse(code = 201, message = "When Device Type successfully created", response = CodeAndLanguageCodeID.class),
			@ApiResponse(code = 400, message = "When Request body passed  is null or invalid"),
			@ApiResponse(code = 500, message = "While creating Device Type any error occured") })
	public ResponseWrapper<CodeAndLanguageCodeID> createDeviceType(
			@Valid @RequestBody RequestWrapper<DeviceTypeDto> deviceTypes) {

		ResponseWrapper<CodeAndLanguageCodeID> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(deviceTypeService.createDeviceType(deviceTypes.getRequest()));
		return responseWrapper;
	}

	/**
	 * This controller method provides with all device types.
	 * 
	 * @param pageNumber
	 *            the page number
	 * @param pageSize
	 *            the size of each page
	 * @param sortBy
	 *            the attributes by which it should be ordered
	 * @param orderBy
	 *            the order to be used
	 * @return the response i.e. pages containing the device types.
	 */
	@PreAuthorize("hasAnyRole('ZONAL_ADMIN','CENTRAL_ADMIN')")
	@ResponseFilter
	@GetMapping("/devicetypes/all")
	@ApiOperation(value = "Retrieve all the device types with additional metadata", notes = "Retrieve all the device types with additional metadata")
	@ApiResponses({ @ApiResponse(code = 200, message = "list of device types"),
			@ApiResponse(code = 500, message = "Error occured while retrieving device types") })
	public ResponseWrapper<PageDto<DeviceTypeExtnDto>> getAllDeviceTypes(
			@RequestParam(name = "pageNumber", defaultValue = "0") @ApiParam(value = "page no for the requested data", defaultValue = "0") int pageNumber,
			@RequestParam(name = "pageSize", defaultValue = "10") @ApiParam(value = "page size for the requested data", defaultValue = "10") int pageSize,
			@RequestParam(name = "sortBy", defaultValue = "createdDateTime") @ApiParam(value = "sort the requested data based on param value", defaultValue = "createdDateTime") String sortBy,
			@RequestParam(name = "orderBy", defaultValue = "desc") @ApiParam(value = "order the requested data based on param", defaultValue = "desc") OrderEnum orderBy) {
		ResponseWrapper<PageDto<DeviceTypeExtnDto>> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(deviceTypeService.getAllDeviceTypes(pageNumber, pageSize, sortBy, orderBy.name()));
		return responseWrapper;
	}
	
	/**
	 * Api to search Device Type based on filters provided.
	 * 
	 * @param request
	 *            the request DTO.
	 * @return the pages of {@link DeviceTypeExtnDto}.
	 */
	@ResponseFilter
	@PostMapping(value = "/devicetypes/search")
	@PreAuthorize("hasRole('GLOBAL_ADMIN')")
	@ApiOperation(value = "Retrieve all Device Types for the given Filter parameters", notes = "Retrieve all Device Types for the given Filter parameters")
	public ResponseWrapper<PageResponseDto<DeviceTypeExtnDto>> deviceTypeSearch(
			@Valid @RequestBody RequestWrapper<SearchDto> request) {
		ResponseWrapper<PageResponseDto<DeviceTypeExtnDto>> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(deviceTypeService.deviceTypeSearch(request.getRequest()));
		return responseWrapper;
	}
	
	/**
	 * Api to filter Device Type based on column and type provided.
	 * 
	 * @param request
	 *            the request DTO.
	 * @return the {@link FilterResponseDto}.
	 */
	@ResponseFilter
	@PostMapping("/devicetypes/filtervalues")
	@PreAuthorize("hasRole('GLOBAL_ADMIN')")
	public ResponseWrapper<FilterResponseDto> deviceTypeFilterValues(
			@RequestBody @Valid RequestWrapper<FilterValueDto> request) {
		ResponseWrapper<FilterResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(deviceTypeService.deviceTypeFilterValues(request.getRequest()));
		return responseWrapper;
	}

}
