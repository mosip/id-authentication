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

/**
 * 
 * @author Bal Vikash Sharma
 * @since 1.0.0
 */
@RestController
@RequestMapping("/registration_center_machine")
public class RegistrationCenterMachineController {

	@Autowired
	private RegistrationCenterMachineService registrationCenterMachineService;

	@PostMapping
	public ResponseEntity<ResponseRrgistrationCenterMachineDto> mapRegistrationCenterAndMachine(
			@Valid @RequestBody RequestDto<RegistrationCenterMachineDto> requestDto) {
		return new ResponseEntity<ResponseRrgistrationCenterMachineDto>(
				registrationCenterMachineService.mapRegistrationCenterAndMachine(requestDto), HttpStatus.CREATED);
	}

}
