package io.mosip.kernel.masterdata.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.http.ResponseFilter;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.masterdata.dto.RegistrationCenterMachineDto;
import io.mosip.kernel.masterdata.dto.ResponseRrgistrationCenterMachineDto;
import io.mosip.kernel.masterdata.dto.getresponse.ResponseDto;
import io.mosip.kernel.masterdata.entity.id.RegistrationCenterMachineID;
import io.mosip.kernel.masterdata.service.RegistrationCenterMachineService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * 
 * @author Dharmesh Khandelwal
 * @author Bal Vikash Sharma
 * @since 1.0.0
 */
@RestController
@RequestMapping("/registrationcentermachine")
@Api(tags = { "RegistrationCenterMachine" })
public class RegistrationCenterMachineController {

	@Autowired
	private RegistrationCenterMachineService registrationCenterMachineService;

	@ResponseFilter
	@PostMapping
	@ApiOperation(value = "Map provided registration center and machine", notes = "Map provided registration center id and machine id")
	@ApiResponses({ @ApiResponse(code = 201, message = "When registration center and machine mapped"),
			@ApiResponse(code = 400, message = "When Request body passed  is invalid"),
			@ApiResponse(code = 500, message = "While mapping registration center and machine") })
	public ResponseWrapper<ResponseRrgistrationCenterMachineDto> createRegistrationCenterAndMachine(
			@Valid @RequestBody RequestWrapper<RegistrationCenterMachineDto> requestDto) {

		ResponseWrapper<ResponseRrgistrationCenterMachineDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(
				registrationCenterMachineService.createRegistrationCenterAndMachine(requestDto.getRequest()));
		return responseWrapper;
	}

	/**
	 * Delete the mapping of registration center and machine
	 * 
	 * @param regCenterId Registration center id to be deleted
	 * @param machineId   MachineId id to be deleted
	 * @return {@link RegistrationCenterMachineID}
	 */
	@ResponseFilter
	@ApiOperation(value = "Delete the mapping of registration center and machine")
	@DeleteMapping("/{regCenterId}/{machineId}")
	public ResponseWrapper<RegistrationCenterMachineID> deleteRegistrationCenterMachineMapping(
			@ApiParam("Registration center id to be deleted") @PathVariable String regCenterId,
			@ApiParam("MachineId id to be deleted") @PathVariable String machineId) {

		ResponseWrapper<RegistrationCenterMachineID> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(
				registrationCenterMachineService.deleteRegistrationCenterMachineMapping(regCenterId, machineId));
		return responseWrapper;
	}
	
	@ResponseFilter
	@ApiOperation(value="Un-map center to machine map ")
	@GetMapping("/unmap/{regCenterId}/{machineId}")
	public ResponseWrapper<ResponseDto> unMapRegistrationCenterMachine(@ApiParam("Registration center id") @PathVariable String regCenterId,
			@ApiParam("MachineId id ") @PathVariable String machineId){
		ResponseWrapper<ResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(
				registrationCenterMachineService.unMapCenterToMachineMapping(regCenterId, machineId));
		return responseWrapper;
	}
	
	@ResponseFilter
	@ApiOperation(value="Un-map center to machine map ")
	@GetMapping("/map/{regCenterId}/{machineId}")
	public ResponseWrapper<ResponseDto> mapRegistrationCenterMachine(@ApiParam("Registration center id") @PathVariable String regCenterId,
			@ApiParam("MachineId id ") @PathVariable String machineId){
		ResponseWrapper<ResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(
				registrationCenterMachineService.mapCenterToMachineMapping(regCenterId, machineId));
		return responseWrapper;
	}
	

}
