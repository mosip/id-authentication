package io.mosip.kernel.masterdata.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.masterdata.dto.DeviceTypeResponseDto;
import io.mosip.kernel.masterdata.entity.DeviceType;
import io.mosip.kernel.masterdata.service.DeviceTypeService;

@RestController
public class DeviceTypeController {
	
	/**
	 * Reference to MachineDetailService.
	 */
	@Autowired
	private DeviceTypeService deviceTypeService;
	
	@PostMapping(value = "/devicetypes/add-devicetype")
	public DeviceTypeResponseDto addDeviceType(@RequestBody List<DeviceType> deviceTypes)
	{
		return deviceTypeService.addDeviceType(deviceTypes);
	}

}
