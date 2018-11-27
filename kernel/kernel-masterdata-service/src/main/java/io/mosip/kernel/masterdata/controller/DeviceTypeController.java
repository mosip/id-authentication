package io.mosip.kernel.masterdata.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.masterdata.dto.DeviceTypeRequestDto;
import io.mosip.kernel.masterdata.dto.PostResponseDto;
import io.mosip.kernel.masterdata.service.DeviceTypeService;

@RestController
public class DeviceTypeController {

	/**
	 * Reference to deviceTypeService.
	 */
	@Autowired
	private DeviceTypeService deviceTypeService;
	
	/**
	 * Save list of device Type details to the Databse
	 * 
	 * @param DeviceTypeRequestDto
	 *            
	 * @return {@link PostResponseDto}
	 */

	@PostMapping("/devicetypes")
	public PostResponseDto saveDeviceTypes(@RequestBody DeviceTypeRequestDto deviceTypes) {
		return deviceTypeService.saveDeviceTypes(deviceTypes);
	}

}
