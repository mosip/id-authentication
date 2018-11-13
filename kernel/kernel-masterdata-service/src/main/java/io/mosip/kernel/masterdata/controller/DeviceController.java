

package io.mosip.kernel.masterdata.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.masterdata.dto.DeviceDto;
import io.mosip.kernel.masterdata.dto.DeviceLangCodeDtypeDto;
import io.mosip.kernel.masterdata.service.DeviceService;

/**
 * Controller with api to get Device Details
 * 
 * @author Megha Tanga
 * @since 1.0.0
 *
 */

@RestController
@RequestMapping(value = "/devices")
public class DeviceController {

	@Autowired
	private DeviceService deviceService;

	/**
	 * Get api to fetch a all device details
	 * 
	 * @return all device details
	 */

	@GetMapping
	public List<DeviceDto> getDeviceAll() {
		return deviceService.getDeviceAll();

	}
	
	@GetMapping(value = "/{langcode}")
	public List<DeviceDto> getDeviceIdLang(@PathVariable("langcode") String langCode) {
		return deviceService.getDeviceLangCode(langCode);

	}
	
	/**
	 * Get api to fetch a all device details based on device type and language code
	 * 
	 * @return all device details
	 */
	@GetMapping(value = "/{langcode}/{deviceType}")
	public List<DeviceLangCodeDtypeDto> getDeviceLangCodeAndDeviceType(@PathVariable("langcode") String langCode, @PathVariable("deviceType") String deviceType) {
		return deviceService.getDeviceLangCodeAndDeviceType(langCode, deviceType);

	}

}

