package io.mosip.kernel.keymanagerservice.controller;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.keymanagerservice.dto.PublicKeyResponse;
import io.mosip.kernel.keymanagerservice.dto.SymmetricKeyRequestDto;
import io.mosip.kernel.keymanagerservice.dto.SymmetricKeyResponseDto;
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

	@GetMapping(value = "/publickey/{applicationId}")
	public ResponseEntity<PublicKeyResponse> getPublicKey(@PathVariable("applicationId") String applicationId,
			@RequestParam("timeStamp") @DateTimeFormat(iso = ISO.DATE_TIME) LocalDateTime timeStamp,
			@RequestParam("referenceId") Optional<String> referenceId) {

		return new ResponseEntity<>(keymanagerService.getPublicKey(applicationId, timeStamp, referenceId), HttpStatus.OK);
	}

	@PostMapping(value = "/symmetricKey")
	public ResponseEntity<SymmetricKeyResponseDto> decryptSymmetricKey(
			@RequestBody SymmetricKeyRequestDto symmetricKeyRequestDto) {

		return new ResponseEntity<>(keymanagerService.decryptSymmetricKey(symmetricKeyRequestDto), HttpStatus.CREATED);
	}
}
