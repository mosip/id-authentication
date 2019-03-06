package io.mosip.kernel.masterdata.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.masterdata.dto.RegistrationCenterMachineDeviceDto;
import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.dto.ResponseRegistrationCenterMachineDeviceDto;
import io.mosip.kernel.masterdata.entity.id.RegistrationCenterMachineDeviceID;
import io.mosip.kernel.masterdata.service.RegistrationCenterMachineDeviceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * 
 * @author Bal Vikash Sharma
 * @author Srinivasan
 * @since 1.0.0
 */
@RestController
@RequestMapping("/v1.0/registrationcentermachinedevice")
@Api(tags = { "RegistrationCenterMachineDevice" })
public class RegistrationCenterMachineDeviceController {

	@Autowired
	private RegistrationCenterMachineDeviceService registrationCenterMachineDeviceService;

	@PostMapping
	@ApiOperation(value = "Map provided registration center, machine and device", notes = "Map provided registration center id, machine id and device id", response = ResponseRegistrationCenterMachineDeviceDto.class)
	@ApiResponses({
			@ApiResponse(code = 201, message = "When registration center, machine and device mapped", response = ResponseRegistrationCenterMachineDeviceDto.class),
			@ApiResponse(code = 400, message = "When Request body passed  is invalid"),
			@ApiResponse(code = 500, message = "While mapping registration center, machine and device") })
	public ResponseEntity<ResponseRegistrationCenterMachineDeviceDto> createRegistrationCenterMachineAndDevice(
			@Valid @RequestBody RequestDto<RegistrationCenterMachineDeviceDto> requestDto) {
		return new ResponseEntity<>(
				registrationCenterMachineDeviceService.createRegistrationCenterMachineAndDevice(requestDto),
				HttpStatus.OK);
	}

	@DeleteMapping(value="/{regcenterid}/{machineid}/{deviceid}")
	@ApiOperation(value="delete mapping if this service is called.")
	@ApiResponses({
		@ApiResponse(code = 201, message = "When registration center, machine and device mapped", response = ResponseRegistrationCenterMachineDeviceDto.class),
		@ApiResponse(code = 400, message = "When Request body passed  is invalid"),
		@ApiResponse(code = 500, message = "While mapping registration center, machine and device") })
	public RegistrationCenterMachineDeviceID deleteRegistrationCentreMachineDeviceMappingDetails(@PathVariable(value="regcenterid") String regCenterId,@PathVariable(value="machineid") String machineId,@PathVariable(value="deviceid") String deviceId){
		return registrationCenterMachineDeviceService.deleteRegistrationCenterMachineAndDevice(regCenterId, machineId, deviceId);
	}

}
