package io.mosip.kernel.masterdata.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.masterdata.dto.RegistrationCenterDeviceDto;
import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.dto.ResponseRegistrationCenterDeviceDto;
import io.mosip.kernel.masterdata.service.RegistrationCenterDeviceService;

/**
 * 
 * @author Bal Vikash Sharma
 * @since 1.0.0
 */
@RestController
@RequestMapping("/registrationcenterdevice")
public class RegistrationCenterDeviceController {

	@Autowired
	private RegistrationCenterDeviceService registrationCenterDeviceService;

	@PostMapping
	public ResponseEntity<ResponseRegistrationCenterDeviceDto> mapRegistrationCenterAndDevice(
			@Valid @RequestBody RequestDto<RegistrationCenterDeviceDto> requestDto) {
		return new ResponseEntity<ResponseRegistrationCenterDeviceDto>(
				registrationCenterDeviceService.mapRegistrationCenterAndDevice(requestDto), HttpStatus.CREATED);
	}
}
