package io.mosip.kernel.masterdata.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.masterdata.dto.DeviceSpecPostResponseDto;
import io.mosip.kernel.masterdata.dto.DeviceSpecificationDto;
import io.mosip.kernel.masterdata.dto.DeviceSpecificationRequestDto;
import io.mosip.kernel.masterdata.dto.DeviceSpecificationResponseDto;
import io.mosip.kernel.masterdata.service.DeviceSpecificationService;
import io.swagger.annotations.ApiOperation;

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
	@GetMapping("/devicespecifications/{langcode}")
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
	@GetMapping("/devicespecifications/{langcode}/{devicetypecode}")
	public DeviceSpecificationResponseDto getDeviceSpecificationByLanguageCodeAndDeviceTypeCode(
			@PathVariable("langcode") String langCode, @PathVariable("devicetypecode") String deviceTypeCode) {
		List<DeviceSpecificationDto> deviceSpecificationDtos = deviceSpecificationService
				.findDeviceSpecificationByLangugeCodeAndDeviceTypeCode(langCode, deviceTypeCode);
		return new DeviceSpecificationResponseDto(deviceSpecificationDtos);
	}

	/**
	 * Save list of device specification details to the database table
	 * 
	 * @param deviceSpecifications
	 *            input from user Device specification DTO
	 * @return {@link DeviceSpecificationRequestDto}
	 */
	@PostMapping("/devicespecifications")
	public DeviceSpecPostResponseDto addDeviceType(
			@RequestBody DeviceSpecificationRequestDto deviceSpecifications) {
		return deviceSpecificationService.saveDeviceSpecifications(deviceSpecifications);
	}

}
