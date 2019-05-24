package io.mosip.kernel.masterdata.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.http.ResponseFilter;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.masterdata.dto.DeviceSpecificationDto;
import io.mosip.kernel.masterdata.dto.getresponse.DeviceSpecificationResponseDto;
import io.mosip.kernel.masterdata.dto.postresponse.IdResponseDto;
import io.mosip.kernel.masterdata.entity.id.IdAndLanguageCodeID;
import io.mosip.kernel.masterdata.service.DeviceSpecificationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * 
 * Device specification controller with api to save and get list of Device
 * specification.
 * 
 * @author Uday Kumar
 * @author Megha Tanga
 * 
 * @since 1.0.0
 *
 */
@RestController
@Api(tags = { "DeviceSpecification" })
public class DeviceSpecificationController {

	/**
	 * Reference to DeviceSpecificationService.
	 */
	@Autowired
	DeviceSpecificationService deviceSpecificationService;

	/**
	 * Function to fetch list of device specification details based on language code
	 * 
	 * @param langCode
	 *            pass language code as String
	 * 
	 * @return DeviceSpecificationResponseDto all device Specification details based
	 *         on given language code
	 * 
	 */
	@PreAuthorize("hasAnyRole('ZONAL_ADMIN','ZONAL_APPROVER')")
	@ResponseFilter
	@GetMapping("/devicespecifications/{langcode}")
	@ApiOperation(value = "Retrieve all Device Specification for given Languge Code", notes = "Retrieve all DeviceSpecification for the given Languge Code")
	@ApiResponses({
			@ApiResponse(code = 200, message = "When Device Specification retrieved from database for the given Languge Code "),
			@ApiResponse(code = 404, message = "When No Device Specificationfound for the given Languge Code and ID"),
			@ApiResponse(code = 500, message = "While retrieving Device Specifications any error occured") })
	public ResponseWrapper<DeviceSpecificationResponseDto> getDeviceSpecificationByLanguageCode(
			@PathVariable("langcode") String langCode) {
		List<DeviceSpecificationDto> deviceSpecificationDtos = deviceSpecificationService
				.findDeviceSpecificationByLangugeCode(langCode);

		ResponseWrapper<DeviceSpecificationResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(new DeviceSpecificationResponseDto(deviceSpecificationDtos));
		return responseWrapper;
	}

	/**
	 * Function to fetch list of device specification details based on language code
	 * and device Type Code
	 * 
	 * @param langCode
	 *            pass language code as String
	 * @param deviceTypeCode
	 *            pass deviceTypeCode as String
	 * @return {@link DeviceSpecificationResponseDto}
	 * 
	 */
	@ResponseFilter
	@GetMapping("/devicespecifications/{langcode}/{devicetypecode}")
	@ApiOperation(value = "Retrieve all Device Specification for specific langCode and DeviceTypeCode", notes = "Retrieve all DeviceSpecification for specific langCode and DeviceTypeCode")
	@ApiResponses({
			@ApiResponse(code = 200, message = "When Device Specification retrieved from database for specific langCode and DeviceTypeCode "),
			@ApiResponse(code = 404, message = "When No Device Specificationfound for specific langCode and DeviceTypeCode"),
			@ApiResponse(code = 500, message = "While retrieving Device Specifications any error occured") })
	public ResponseWrapper<DeviceSpecificationResponseDto> getDeviceSpecificationByLanguageCodeAndDeviceTypeCode(
			@PathVariable("langcode") String langCode, @PathVariable("devicetypecode") String deviceTypeCode) {
		List<DeviceSpecificationDto> deviceSpecificationDtos = deviceSpecificationService
				.findDeviceSpecByLangCodeAndDevTypeCode(langCode, deviceTypeCode);

		ResponseWrapper<DeviceSpecificationResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(new DeviceSpecificationResponseDto(deviceSpecificationDtos));
		return responseWrapper;
	}

	/**
	 * Post API to insert a new row of DeviceSpecification data
	 * 
	 * @param deviceSpecification
	 *            input parameter deviceRequestDto
	 * 
	 * @return {@link IdResponseDto}
	 */
	@ResponseFilter
	@PostMapping("/devicespecifications")
	@ApiOperation(value = "Service to save Device Specification", notes = "Saves Device Specification and return Device Specification ID")
	@ApiResponses({ @ApiResponse(code = 201, message = "When Device Specification successfully created"),
			@ApiResponse(code = 400, message = "When Request body passed  is null or invalid"),
			@ApiResponse(code = 500, message = "While creating Device Specification any error occured") })
	public ResponseWrapper<IdAndLanguageCodeID> createDeviceSpecification(
			@Valid @RequestBody RequestWrapper<DeviceSpecificationDto> deviceSpecification) {

		ResponseWrapper<IdAndLanguageCodeID> responseWrapper = new ResponseWrapper<>();
		responseWrapper
				.setResponse(deviceSpecificationService.createDeviceSpecification(deviceSpecification.getRequest()));
		return responseWrapper;
	}

	@ResponseFilter
	@PutMapping("/devicespecifications")
	@ApiOperation(value = "Service to update device specification", notes = "update Device Specification and return Device Specification ID")
	@ApiResponses({ @ApiResponse(code = 200, message = "When device specification successfully updated"),
			@ApiResponse(code = 400, message = "When Request body passed  is null or invalid"),
			@ApiResponse(code = 404, message = "When No device specification found"),
			@ApiResponse(code = 500, message = "While updating device specification any error occured") })
	public ResponseWrapper<IdAndLanguageCodeID> updateDeviceSpecification(
			@Valid @RequestBody RequestWrapper<DeviceSpecificationDto> deviceSpecification) {

		ResponseWrapper<IdAndLanguageCodeID> responseWrapper = new ResponseWrapper<>();
		responseWrapper
				.setResponse(deviceSpecificationService.updateDeviceSpecification(deviceSpecification.getRequest()));
		return responseWrapper;
	}

	@ResponseFilter
	@DeleteMapping("/devicespecifications/{id}")
	@ApiOperation(value = "Service to delete device specifications", notes = "Delete device specifications and return device specification id")
	@ApiResponses({ @ApiResponse(code = 200, message = "When device specifications successfully deleted"),
			@ApiResponse(code = 400, message = "When Request body passed  is null or invalid"),
			@ApiResponse(code = 404, message = "When No device specifications found"),
			@ApiResponse(code = 500, message = "While deleting device specifications  error occured") })
	public ResponseWrapper<IdResponseDto> deleteDeviceSpecification(@PathVariable("id") String id) {

		ResponseWrapper<IdResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(deviceSpecificationService.deleteDeviceSpecification(id));
		return responseWrapper;
	}

}
