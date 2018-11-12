package io.mosip.kernel.keymanager.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.keymanager.dto.KeymanagerRequestDto;
import io.mosip.kernel.keymanager.dto.KeymanagerResponseDto;
import io.mosip.kernel.keymanager.service.KeymanagerService;

/**
 * This class provides controller methods for Key manager.
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
@RestController
public class KeymanagerController {

	@Autowired
	KeymanagerService keymanagerService;

	@PostMapping(value = "/keymanager/generate")
	public ResponseEntity<KeymanagerResponseDto> generateOtp(
			@Valid @RequestBody KeymanagerRequestDto keymanagerRequestDto) {

		return new ResponseEntity<>(keymanagerService.getKey(keymanagerRequestDto), HttpStatus.CREATED);
	}
}
