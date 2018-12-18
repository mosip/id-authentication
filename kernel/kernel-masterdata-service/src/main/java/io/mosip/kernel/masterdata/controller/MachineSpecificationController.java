package io.mosip.kernel.masterdata.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.masterdata.dto.MachineSpecificationDto;
import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.dto.postresponse.IdResponseDto;
import io.mosip.kernel.masterdata.service.MachineSpecificationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * 
 * This controller class to save Machine Specification details.
 * 
 * @author Megha Tanga
 * @since 1.0.0
 *
 */
@RestController
@Api(tags = { "MachineSpecification" })
public class MachineSpecificationController {

	/**
	 * Reference to MachineSpecificationService.
	 */
	@Autowired
	MachineSpecificationService machineSpecificationService;

	/**
	 * Post API to insert a new row of Machine Specification data
	 * 
	 * @param machineSpecification
	 *            input Machine specification DTO from user
	 * @return ResponseEntity Machine Specification ID which is successfully
	 *         inserted
	 */
	@PostMapping("/v1.0/machinespecifications")
	@ApiOperation(value = "Service to save Machine Specification", notes = "Saves Machine Spacification and return Machine Spacification ID ", response = IdResponseDto.class)
	@ApiResponses({
			@ApiResponse(code = 201, message = "When Machine Specification successfully created", response = IdResponseDto.class),
			@ApiResponse(code = 400, message = "When Request body passed  is null or invalid"),
			@ApiResponse(code = 500, message = "While creating Machine Specification any error occured") })
	public ResponseEntity<IdResponseDto> createMachineSpecification(
			@Valid @RequestBody RequestDto<MachineSpecificationDto> machineSpecification) {

		return new ResponseEntity<>(machineSpecificationService.createMachineSpecification(machineSpecification),
				HttpStatus.CREATED);
	}

}
