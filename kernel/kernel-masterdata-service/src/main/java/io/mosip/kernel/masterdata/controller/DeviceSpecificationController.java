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
public class DeviceSpecificationController {

	@Autowired
	DeviceSpecificationService deviceSpecificationService;

	@ApiOperation(value = "Fetch all the device specification avialbale for specific langCode")
	/**
	 * Function to fetch list of device specification details based on language code
	 * 
	 * @param langCode
	 *            input from user
	 * @return {@link DeviceSpecificationResponseDto}
	 * 
	 */
	@GetMapping("/v1.0/devicespecifications/{langcode}")
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
	 * @param devicetypecode
	 *            input from user
	 * @return {@link DeviceSpecificationResponseDto}
	 * 
	 */

	@ApiOperation(value = "Fetch all the device specification avialbale for specific langCode and DeviceTypeCode")
	@GetMapping("/v1.0/devicespecifications/{langcode}/{devicetypecode}")
	public DeviceSpecificationResponseDto getDeviceSpecificationByLanguageCodeAndDeviceTypeCode(
			@PathVariable("langcode") String langCode, @PathVariable("devicetypecode") String deviceTypeCode) {
		List<DeviceSpecificationDto> deviceSpecificationDtos = deviceSpecificationService
				.findDeviceSpecificationByLangugeCodeAndDeviceTypeCode(langCode, deviceTypeCode);
		return new DeviceSpecificationResponseDto(deviceSpecificationDtos);
	}

	/**
	 * Save device specification details to the database table
	 * 
	 * @param deviceSpecification
	 *            input from user Device specification DTO
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
