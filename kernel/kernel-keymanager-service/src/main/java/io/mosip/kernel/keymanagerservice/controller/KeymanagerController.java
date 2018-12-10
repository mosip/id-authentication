package io.mosip.kernel.keymanagerservice.controller;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.keymanagerservice.dto.PublicKeyResponse;
import io.mosip.kernel.keymanagerservice.dto.SymmetricKeyRequestDto;
import io.mosip.kernel.keymanagerservice.dto.SymmetricKeyResponseDto;
import io.mosip.kernel.keymanagerservice.service.KeymanagerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * This class provides controller methods for Key manager.
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
@CrossOrigin
@RestController
@RequestMapping("/v1.0")
@Api(tags = { "keymanager" }, value = "Operation related to Keymanagement")
public class KeymanagerController {

	/**
	 * Instance for KeymanagerService
	 */
	@Autowired
	KeymanagerService keymanagerService;

	/**
	 * Request mapping to get Public Key
	 * 
	 * @param applicationId
	 *            Application id of the application requesting publicKey
	 * @param timeStamp
	 *            Timestamp of the request
	 * @param referenceId
	 *            Reference id of the application requesting publicKey
	 * @return {@link PublicKeyResponse} instance
	 */
	@ApiOperation(value = "Get the public key of a particular application",response = PublicKeyResponse.class)
	@GetMapping(value = "/publickey/{applicationId}")
	public ResponseEntity<PublicKeyResponse<String>> getPublicKey(@ApiParam("Id of application")@PathVariable("applicationId") String applicationId,
			@ApiParam("Timestamp as metadata")	@RequestParam("timeStamp") @DateTimeFormat(iso = ISO.DATE_TIME) LocalDateTime timeStamp,
			@ApiParam("Refrence Id as metadata")@RequestParam("referenceId") Optional<String> referenceId) {

		return new ResponseEntity<>(keymanagerService.getPublicKey(applicationId, timeStamp, referenceId),
				HttpStatus.OK);
	}

	/**
	 * Request mapping to decrypt symmetric key
	 * 
	 * @param symmetricKeyRequestDto
	 *            having encrypted symmetric key
	 * 
	 * @return {@link SymmetricKeyResponseDto} symmetricKeyResponseDto
	 */
	@ApiOperation(value = "Decrypt the encrypted Symmetric key",response = SymmetricKeyResponseDto.class)
	@PostMapping(value = "/symmetrickey")
	public ResponseEntity<SymmetricKeyResponseDto> decryptSymmetricKey(@ApiParam("Symmetric key to encrypt in BASE64 encoding with meta-data")
			@RequestBody SymmetricKeyRequestDto symmetricKeyRequestDto) {

		return new ResponseEntity<>(keymanagerService.decryptSymmetricKey(symmetricKeyRequestDto), HttpStatus.CREATED);
	}
}
