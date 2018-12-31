package io.mosip.kernel.masterdata.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
 * This class provide services to do CRUD operations on MachineSpecification.
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

	/**
	 * Put API to update a new row of Machine Specification data
	 * 
	 * @param machineSpecification
	 *            input Machine specification DTO from user
	 * @return ResponseEntity Machine Specification ID which is successfully updated
	 */
	@PutMapping("/v1.0/machinespecifications")
	@ApiOperation(value = "Service to update Machine Specification", notes = "update Machine Spacification and return Machine Spacification ID ", response = IdResponseDto.class)
	@ApiResponses({
			@ApiResponse(code = 200, message = "When Machine Specification successfully updated", response = IdResponseDto.class),
			@ApiResponse(code = 400, message = "When Request body passed  is null or invalid"),
			@ApiResponse(code = 404, message = "When No Machine Specification found"),
			@ApiResponse(code = 500, message = "While updating Machine Specification any error occured") })
	public ResponseEntity<IdResponseDto> updateMachineSpecification(
			@Valid @RequestBody RequestDto<MachineSpecificationDto> machineSpecification) {

		return new ResponseEntity<>(machineSpecificationService.updateMachineSpecification(machineSpecification),
				HttpStatus.OK);
	}

	/**
	 * Put API to delete a new row of Machine Specification data
	 * 
	 * @param id
	 *            input Machine specification id
	 * @return ResponseEntity Machine Specification ID which is successfully deleted
	 */
	@DeleteMapping("/v1.0/machinespecifications/{id}")
	@ApiOperation(value = "Service to delete Machine Specification", notes = "Delete Machine Spacification and return Machine Spacification ID ", response = IdResponseDto.class)
	@ApiResponses({
			@ApiResponse(code = 200, message = "When Machine Specification successfully deleted", response = IdResponseDto.class),
			@ApiResponse(code = 400, message = "When Request body passed  is null or invalid"),
			@ApiResponse(code = 404, message = "When No Machine Specification found"),
			@ApiResponse(code = 500, message = "While deleting Machine Specification any error occured") })
	public ResponseEntity<IdResponseDto> deleteMachineSpecification(@Valid @PathVariable("id") String id) {

		return new ResponseEntity<>(machineSpecificationService.deleteMachineSpecification(id), HttpStatus.OK);
	}
}
