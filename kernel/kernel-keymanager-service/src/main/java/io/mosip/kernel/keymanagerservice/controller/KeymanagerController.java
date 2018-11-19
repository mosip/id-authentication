package io.mosip.kernel.keymanagerservice.controller;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.keymanagerservice.dto.KeyResponseDto;
import io.mosip.kernel.keymanagerservice.dto.PublicKeyRequestDto;
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

	@PostMapping(value = "/publickey/{appId}")
	public ResponseEntity<KeyResponseDto> getPublicKey(@RequestBody PublicKeyRequestDto publicKeyRequestDto) {

		return new ResponseEntity<>(keymanagerService.getPublicKey(publicKeyRequestDto.getApplicationId(),
				publicKeyRequestDto.getTimeStamp(), publicKeyRequestDto.getMachineId()), HttpStatus.CREATED);
	}

	@PostMapping(value = "/symmetricKey/{appId}")
	public ResponseEntity<KeyResponseDto> decryptSymmetricKey(@PathVariable("appId") String appId,
			@RequestParam("timeStamp") @DateTimeFormat(iso = ISO.DATE_TIME) LocalDateTime timeStamp,
			@RequestParam("machineId") Optional<String> machineId, @RequestBody byte[] encryptedSymmetricKey) {

		return new ResponseEntity<>(
				keymanagerService.decryptSymmetricKey(appId, timeStamp, machineId, encryptedSymmetricKey),
				HttpStatus.CREATED);
	}
}
