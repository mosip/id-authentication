package io.mosip.kernel.masterdata.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.masterdata.dto.DeviceSpecificationRequestDto;
import io.mosip.kernel.masterdata.dto.DeviceTypeRequestDto;
import io.mosip.kernel.masterdata.dto.PostResponseDto;
import io.mosip.kernel.masterdata.service.DeviceTypeService;

@RestController
@RequestMapping("/devicetypes")
public class DeviceTypeController {

	/**
	 * Reference to deviceTypeService.
	 */
	@Autowired
	private DeviceTypeService deviceTypeService;
	
	/**
	 * Save list of device Type details to the DB
	 * 
	 * @param DeviceTypeRequestDto
	 *            
	 * @return {@link PostResponseDto}
	 */

	@PostMapping(value = "/")
	public PostResponseDto addDeviceTypes(@RequestBody DeviceTypeRequestDto deviceTypes) {
		return deviceTypeService.addDeviceTypes(deviceTypes);
	}

}
