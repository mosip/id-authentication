
package io.mosip.kernel.masterdata.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.masterdata.dto.DeviceLangCodeResponseDto;
import io.mosip.kernel.masterdata.dto.DeviceResponseDto;
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

	/**
	 * Reference to MachineDetailService.
	 */
	@Autowired
	private DeviceService deviceService;

	/**
	 * Get api to fetch a all device details based on language code
	 * 
	 * @return all device details
	 */
	@GetMapping(value = "/{langcode}")
	public DeviceResponseDto getDeviceLang(@PathVariable("langcode") String langCode) {
		return deviceService.getDeviceLangCode(langCode);
	}

	/**
	 * Get api to fetch a all device details based on device type and language code
	 * 
	 * @return all device details
	 */
	@GetMapping(value = "/{langcode}/{deviceType}")
	public DeviceLangCodeResponseDto getDeviceLangCodeAndDeviceType(@PathVariable("langcode") String langCode,
			@PathVariable("deviceType") String deviceType) {
		return deviceService.getDeviceLangCodeAndDeviceType(langCode, deviceType);

	}

}
