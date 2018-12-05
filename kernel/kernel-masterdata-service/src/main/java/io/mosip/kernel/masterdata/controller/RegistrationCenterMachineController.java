package io.mosip.kernel.masterdata.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.masterdata.dto.RegistrationCenterMachineDto;
import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.dto.ResponseRrgistrationCenterMachineDto;
import io.mosip.kernel.masterdata.service.RegistrationCenterMachineService;
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
@RequestMapping("/registrationcentermachine")
@Api(tags = { "registrationcentermachine" })
public class RegistrationCenterMachineController {

	@Autowired
	private RegistrationCenterMachineService registrationCenterMachineService;

	@PostMapping
	@ApiOperation(value = "Map provided registration center and machine", notes = "Map provided registration center id and machine id", response = ResponseRrgistrationCenterMachineDto.class)
	@ApiResponses({
			@ApiResponse(code = 201, message = "When registration center and machine mapped", response = ResponseRrgistrationCenterMachineDto.class),
			@ApiResponse(code = 400, message = "When Request body passed  is invalid"),
			@ApiResponse(code = 500, message = "While mapping registration center and machine") })
	public ResponseEntity<ResponseRrgistrationCenterMachineDto> mapRegistrationCenterAndMachine(
			@Valid @RequestBody RequestDto<RegistrationCenterMachineDto> requestDto) {
		return new ResponseEntity<>(registrationCenterMachineService.mapRegistrationCenterAndMachine(requestDto),
				HttpStatus.CREATED);
	}

}
