package io.mosip.kernel.masterdata.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.masterdata.dto.MachineTypeDto;
import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.entity.id.CodeAndLanguageCodeID;
import io.mosip.kernel.masterdata.service.MachineTypeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * 
 * This controller class to save Machine type details.
 * 
 * @author Megha Tanga
 * @since 1.0.0
 *
 */
@RestController
@Api(tags = { "MachineType" })
public class MachineTypeController {

	/**
	 * Reference to MachineType Service.
	 */
	@Autowired
	private MachineTypeService machinetypeService;

	/**
	 * Post API to insert a new row of Machine Type data
	 * 
	 * @param machineType
	 *            input Machine Type DTO from user
	 * 
	 * @return ResponseEntity Machine Type Code and Language Code which is
	 *         successfully inserted
	 * 
	 */

	@PostMapping("/v1.0/machinetypes")
	@ApiOperation(value = "Service to save Machine Type", notes = "Saves MachineType and return  code and Languge Code", response = CodeAndLanguageCodeID.class)
	@ApiResponses({
			@ApiResponse(code = 201, message = "When Machine Type successfully created", response = CodeAndLanguageCodeID.class),
			@ApiResponse(code = 400, message = "When Request body passed  is null or invalid"),
			@ApiResponse(code = 500, message = "While creating Machine Type any error occured") })
	public ResponseEntity<CodeAndLanguageCodeID> createMachineType(
			@Valid @RequestBody RequestDto<MachineTypeDto> machineType) {
		return new ResponseEntity<>(machinetypeService.createMachineType(machineType), HttpStatus.CREATED);
	}

}
