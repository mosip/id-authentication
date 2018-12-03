package io.mosip.kernel.masterdata.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.masterdata.dto.DeviceTypeRequestDto;
import io.mosip.kernel.masterdata.entity.id.CodeAndLanguageCodeID;
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
	 * 				input from user Device Type DTO
	 *            
	 * @return {@link CodeAndLanguageCodeID}
	 */

	@PostMapping("/devicetypes")
	public ResponseEntity<CodeAndLanguageCodeID> createDeviceTypes(@RequestBody DeviceTypeRequestDto deviceTypes) {
		return new ResponseEntity<>( deviceTypeService.createDeviceTypes(deviceTypes), HttpStatus.CREATED);
	}

}
