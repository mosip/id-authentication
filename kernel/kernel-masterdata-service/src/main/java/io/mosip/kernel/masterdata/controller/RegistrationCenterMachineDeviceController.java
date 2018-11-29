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

/**
 * 
 * @author Bal Vikash Sharma
 * @since 1.0.0
 */
@RestController
@RequestMapping("/registration_center_machine_device")
public class RegistrationCenterMachineDeviceController {

	@Autowired
	private RegistrationCenterMachineDeviceService registrationCenterMachineDeviceService;

	@PostMapping
	public ResponseEntity<ResponseRrgistrationCenterMachineDeviceDto> mapRegistrationCenterMachineAndDevice(
			@Valid @RequestBody RequestDto<RegistrationCenterMachineDeviceDto> requestDto) {
		return new ResponseEntity<ResponseRrgistrationCenterMachineDeviceDto>(
				registrationCenterMachineDeviceService.mapRegistrationCenterMachineAndDevice(requestDto),
				HttpStatus.CREATED);
	}

}
