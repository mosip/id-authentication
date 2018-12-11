package io.mosip.kernel.masterdata.controller;

import javax.validation.Valid;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.masterdata.dto.DeviceTypeDto;
import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.entity.id.CodeAndLanguageCodeID;
import io.mosip.kernel.masterdata.service.DeviceTypeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * Controller with api to save and get Device Type Details
 * 
 * @author Megha Tanga
 * @since 1.0.0
 *
 */

@RestController
@Api(tags = { "DeviceType" })
public class DeviceTypeController {

	/**
	 * Reference to deviceTypeService.
	 */
	@Autowired
	private DeviceTypeService deviceTypeService;
	
	/**
	 * Save list of device Type details to the Database
	 * 
	 * @param RequestDto
	 * 				input from user Device Type DTO
	 *            
	 * @return {@link CodeAndLanguageCodeID}
	 */

	@PostMapping("/v1.0/devicetypes")
	@ApiOperation(value = "Service to save Device Type", notes = "Saves Device Type and return Device Code and Languge Code", response = CodeAndLanguageCodeID.class)
	@ApiResponses({
			@ApiResponse(code = 201, message = "When Device Type successfully created", response = CodeAndLanguageCodeID.class),
			@ApiResponse(code = 400, message = "When Request body passed  is null or invalid"),
			@ApiResponse(code = 500, message = "While creating Device Type any error occured") })
	public ResponseEntity<CodeAndLanguageCodeID> createDeviceTypes(@Valid @RequestBody RequestDto<DeviceTypeDto> deviceTypes) {
		return new ResponseEntity<>( deviceTypeService.createDeviceTypes(deviceTypes), HttpStatus.CREATED);
	}

}
