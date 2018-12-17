package io.mosip.kernel.masterdata.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.masterdata.dto.RegistrationCenterMachineDeviceDto;
import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.dto.ResponseRrgistrationCenterMachineDeviceDto;
import io.mosip.kernel.masterdata.service.RegistrationCenterMachineDeviceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * 
 * @author Bal Vikash Sharma
 * @since 1.0.0
 */
@RestController
@RequestMapping("/v1.0/registrationcentermachinedevice")
@Api(tags = { "RegistrationCenterMachineDevice" })
public class RegistrationCenterMachineDeviceController {

	@Autowired
	private RegistrationCenterMachineDeviceService registrationCenterMachineDeviceService;

	@PostMapping
	@ApiOperation(value = "Map provided registration center, machine and device", notes = "Map provided registration center id, machine id and device id", response = ResponseRrgistrationCenterMachineDeviceDto.class)
	@ApiResponses({
			@ApiResponse(code = 201, message = "When registration center, machine and device mapped", response = ResponseRrgistrationCenterMachineDeviceDto.class),
			@ApiResponse(code = 400, message = "When Request body passed  is invalid"),
			@ApiResponse(code = 500, message = "While mapping registration center, machine and device") })
	public ResponseEntity<ResponseRrgistrationCenterMachineDeviceDto> createRegistrationCenterMachineAndDevice(
			@Valid @RequestBody RequestDto<RegistrationCenterMachineDeviceDto> requestDto) {
		return new ResponseEntity<>(
				registrationCenterMachineDeviceService.createRegistrationCenterMachineAndDevice(requestDto),
				HttpStatus.CREATED);
	}

}
