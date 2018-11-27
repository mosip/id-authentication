package io.mosip.kernel.keymanagerservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.keymanagerservice.dto.KeyResponseDto;
import io.mosip.kernel.keymanagerservice.dto.PublicKeyRequestDto;
import io.mosip.kernel.keymanagerservice.dto.SymmetricKeyRequestDto;
import io.mosip.kernel.keymanagerservice.service.KeymanagerService;

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

	@PostMapping(value = "/publickey")
	public ResponseEntity<KeyResponseDto> getPublicKey(@RequestBody PublicKeyRequestDto publicKeyRequestDto) {

		return new ResponseEntity<>(keymanagerService.getPublicKey(publicKeyRequestDto), HttpStatus.CREATED);
	}

	@PostMapping(value = "/symmetricKey")
	public ResponseEntity<KeyResponseDto> decryptSymmetricKey(
			@RequestBody SymmetricKeyRequestDto symmetricKeyRequestDto) {

		return new ResponseEntity<>(keymanagerService.decryptSymmetricKey(symmetricKeyRequestDto), HttpStatus.CREATED);
	}
}
