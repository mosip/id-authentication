package io.mosip.kernel.masterdata.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.masterdata.dto.DeviceTypeRequestDto;
import io.mosip.kernel.masterdata.dto.PostResponseDto;
import io.mosip.kernel.masterdata.entity.CodeAndLanguageCodeId;
import io.mosip.kernel.masterdata.service.DeviceTypeService;

@RestController
public class DeviceTypeController {

	/**
	 * Reference to deviceTypeService.
	 */
	@Autowired
	private DeviceTypeService deviceTypeService;
	
	/**
	 * Save list of device Type details to the Database
	 * 
	 * @param DeviceTypeRequestDto
	 *            
	 * @return {@link PostResponseDto}
	 */

	@PostMapping("/devicetypes")
	public ResponseEntity<CodeAndLanguageCodeId> saveDeviceTypes(@RequestBody DeviceTypeRequestDto deviceTypes) {
		return new ResponseEntity<>( deviceTypeService.saveDeviceTypes(deviceTypes), HttpStatus.CREATED);
	}

}
