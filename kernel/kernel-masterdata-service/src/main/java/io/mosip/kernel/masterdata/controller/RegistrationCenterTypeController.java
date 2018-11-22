package io.mosip.kernel.masterdata.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.masterdata.dto.PostResponseDto;
import io.mosip.kernel.masterdata.dto.RegistrationCenterTypeRequestDto;
import io.mosip.kernel.masterdata.service.RegistrationCenterTypeService;

/**
 * Controller class for RegistrationCenterType operations.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 */
@RestController
public class RegistrationCenterTypeController {

	/**
	 * Autowired reference for {@link RegistrationCenterTypeService}.
	 */
	@Autowired
	RegistrationCenterTypeService regCenterTypeService;

	/**
	 * Controller method for creating a registration center type.
	 * 
	 * @param registrationCenterTypeRequestDto
	 *            the request dto containing the list of registration center type to
	 *            be added.
	 * @return the response dto.
	 */
	@PostMapping("/registrationcentertypes")
	public ResponseEntity<PostResponseDto> addRegistrationCenterType(
			@RequestBody RegistrationCenterTypeRequestDto registrationCenterTypeRequestDto) {
		return new ResponseEntity<>(regCenterTypeService.addRegistrationCenterType(registrationCenterTypeRequestDto),
				HttpStatus.CREATED);
	}
}
