package io.mosip.kernel.masterdata.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.masterdata.dto.DeviceSpecificationDto;
import io.mosip.kernel.masterdata.dto.DeviceSpecificationResponseDto;
import io.mosip.kernel.masterdata.service.DeviceSpecificationService;
import io.swagger.annotations.ApiOperation;

/**
 * 
 * device specification controller controller with api to get list of documents
 * specification language code.
 * 
 * @author Uday Kumar
 * @since 1.0.0
 *
 */
@RestController
@RequestMapping("/devicespecifications")
public class DeviceSpecificationController {

	@Autowired
	DeviceSpecificationService deviceSpecificationService;

	@ApiOperation(value = "Fetch all the device specification avialbale for specific langCode")

	@GetMapping("/{languagecode}")
	public DeviceSpecificationResponseDto getDeviceSpecificationByLanguageCode(
			@PathVariable("languagecode") String languageCode) {
		List<DeviceSpecificationDto> deviceSpecificationDtos = deviceSpecificationService
				.findDeviceSpecificationByLangugeCode(languageCode);
		return new DeviceSpecificationResponseDto(deviceSpecificationDtos);
	}

	@ApiOperation(value = "Fetch all the device specification avialbale for specific langCode and DeviceTypeCode")
	@GetMapping("/{languagecode}/{devicetypecode}")
	public DeviceSpecificationResponseDto getDeviceSpecificationByLanguageCodeAndDeviceTypeCode(
			@PathVariable("languagecode") String languageCode, @PathVariable("devicetypecode") String deviceTypeCode) {
		List<DeviceSpecificationDto> deviceSpecificationDtos = deviceSpecificationService
				.findDeviceSpecificationByLangugeCodeAndDeviceTypeCode(languageCode, deviceTypeCode);
		return new DeviceSpecificationResponseDto(deviceSpecificationDtos);
	}
}
