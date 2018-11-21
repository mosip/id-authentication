package io.mosip.kernel.masterdata.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.masterdata.dto.PostResponseDto;
import io.mosip.kernel.masterdata.dto.RegistrationCenterTypeRequestDto;
import io.mosip.kernel.masterdata.service.RegistrationCenterTypeService;

@RestController
public class RegistrationCenterTypeController {

	@Autowired
	RegistrationCenterTypeService regCenterTypeService;
	
	@PostMapping("/registrationcentertypes")
	public PostResponseDto addRegistrationCenterType(
			@RequestBody RegistrationCenterTypeRequestDto registrationCenterTypeRequestDto) {
		return regCenterTypeService.addRegistrationCenterType(registrationCenterTypeRequestDto);

	}

}
