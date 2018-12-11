package io.mosip.kernel.masterdata.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.masterdata.dto.DeviceSpecificationDto;
import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.dto.getresponse.DeviceSpecificationResponseDto;
import io.mosip.kernel.masterdata.dto.postresponse.IdResponseDto;
import io.mosip.kernel.masterdata.service.DeviceSpecificationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * 
 * Device specification controller with api to save and get list of documents
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
	 *            input from user
	 *            
	 * @return DeviceSpecificationResponseDto
	 * 				all device Specification details based on given language code
	 * 
	 */
	@GetMapping("/v1.0/devicespecifications/{langcode}")
	@ApiOperation(value = "Retrieve all Device Specification for given Languge Code", notes = "Retrieve all DeviceSpecification for the given Languge Code", response = DeviceSpecificationResponseDto.class)
	@ApiResponses({
			@ApiResponse(code = 200, message = "When Device Specification retrieved from database for the given Languge Code ", response = DeviceSpecificationResponseDto.class),
			@ApiResponse(code = 404, message = "When No Device Specificationfound for the given Languge Code and ID"),
			@ApiResponse(code = 500, message = "While retrieving Device Specifications any error occured") })
	public DeviceSpecificationResponseDto getDeviceSpecificationByLanguageCode(
			@PathVariable("langcode") String langCode) {
		List<DeviceSpecificationDto> deviceSpecificationDtos = deviceSpecificationService
				.findDeviceSpecificationByLangugeCode(langCode);
		return new DeviceSpecificationResponseDto(deviceSpecificationDtos);
	}

	/**
	 * Function to fetch list of device specification details based on language code
	 * and device Type Code
	 * 
	 * @param langCode
	 *            input from user
	 * @param deviceTypeCode
	 *            input from user
	 * @return {@link DeviceSpecificationResponseDto}
	 * 
	 */

	@GetMapping("/v1.0/devicespecifications/{langcode}/{devicetypecode}")
	@ApiOperation(value = "Retrieve all Device Specification for specific langCode and DeviceTypeCode", notes = "Retrieve all DeviceSpecification for specific langCode and DeviceTypeCode", response = DeviceSpecificationResponseDto.class)
	@ApiResponses({
			@ApiResponse(code = 200, message = "When Device Specification retrieved from database for specific langCode and DeviceTypeCode ", response = DeviceSpecificationResponseDto.class),
			@ApiResponse(code = 404, message = "When No Device Specificationfound for specific langCode and DeviceTypeCode"),
			@ApiResponse(code = 500, message = "While retrieving Device Specifications any error occured") })
	public DeviceSpecificationResponseDto getDeviceSpecificationByLanguageCodeAndDeviceTypeCode(
			@PathVariable("langcode") String langCode, @PathVariable("devicetypecode") String deviceTypeCode) {
		List<DeviceSpecificationDto> deviceSpecificationDtos = deviceSpecificationService
				.findDeviceSpecByLangCodeAndDevTypeCode(langCode, deviceTypeCode);
		return new DeviceSpecificationResponseDto(deviceSpecificationDtos);
		
	}


	/**
	 * Save device specification details to the database table
	 * 
	 * @param RequestDto
	 *            input from user Device specification DTO
	 *            
	 * @return {@link IdResponseDto}
	 */
	@PostMapping("/v1.0/devicespecifications")
	@ApiOperation(value = "Service to save Device Specification", notes = "Saves Device Specification and return Device Specification ID", response = IdResponseDto.class)
	@ApiResponses({
			@ApiResponse(code = 201, message = "When Device Specification successfully created", response = IdResponseDto.class),
			@ApiResponse(code = 400, message = "When Request body passed  is null or invalid"),
			@ApiResponse(code = 500, message = "While creating Device Specification any error occured") })
	public ResponseEntity<IdResponseDto> createDeviceSpecification(
		@Valid	@RequestBody RequestDto<DeviceSpecificationDto> deviceSpecification) {

		return new ResponseEntity<>(deviceSpecificationService.createDeviceSpecification(deviceSpecification), HttpStatus.CREATED);
	}

}
