/*
 * 
 * 
 * 
 * 
 */
package io.mosip.kernel.cryptomanager.controller;

import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.http.ResponseFilter;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.cryptomanager.dto.CryptoEncryptRequestDto;
import io.mosip.kernel.cryptomanager.dto.CryptoEncryptResponseDto;
import io.mosip.kernel.cryptomanager.dto.CryptomanagerRequestDto;
import io.mosip.kernel.cryptomanager.dto.CryptomanagerResponseDto;
import io.mosip.kernel.cryptomanager.dto.PublicKeyResponse;
import io.mosip.kernel.cryptomanager.dto.SignatureRequestDto;
import io.mosip.kernel.cryptomanager.dto.SignatureResponseDto;
import io.mosip.kernel.cryptomanager.service.CryptomanagerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * Rest Controller for Crypto-Manager-Service
 * 
 * @author Urvil Joshi
 * @author Srinivasan
 *
 * @since 1.0.0
 */
@CrossOrigin
@RestController
@Api(value = "Operation related to Encryption and Decryption", tags = { "cryptomanager" })
public class CryptomanagerController {

	/**
	 * {@link CryptomanagerService} instance
	 */
	@Autowired
	private CryptomanagerService cryptomanagerService;

	/**
	 * Controller for Encrypt the data
	 * 
	 * @param cryptomanagerRequestDto {@link CryptomanagerRequestDto} request
	 * @return {@link CryptomanagerResponseDto} encrypted Data
	 */
	@PreAuthorize("hasAnyRole('INDIVIDUAL','ID_AUTHENTICATION','TEST', 'REGISTRATION_ADMIN', 'REGISTRATION_SUPERVISOR', 'REGISTRATION_OFFICER', 'REGISTRATION_PROCESSOR')")
	@ResponseFilter
	@PostMapping(value = "/encrypt", produces = "application/json")
	public ResponseWrapper<CryptomanagerResponseDto> encrypt(
			@ApiParam("Salt and Data to encrypt in BASE64 encoding with meta-data") @RequestBody @Valid RequestWrapper<CryptomanagerRequestDto> cryptomanagerRequestDto) {
		ResponseWrapper<CryptomanagerResponseDto> response = new ResponseWrapper<>();
		response.setResponse(cryptomanagerService.encrypt(cryptomanagerRequestDto.getRequest()));
		return response;
	}

	/**
	 * Encrypts data with private key
	 * 
	 * @param cryptomanagerRequestDto
	 * @return {@link ResponseWrapper<CryptoEncryptResponseDto> }
	 */
	@ResponseFilter
	@ApiOperation(value = "Encrypt the data with private key", response = CryptoEncryptResponseDto.class)
	@PostMapping(value = "/private/encrypt", produces = "application/json")
	public ResponseWrapper<CryptoEncryptResponseDto> encryptWithPrivate(
			@ApiParam("Data to encrypt in BASE64 encoding") @RequestBody @Valid RequestWrapper<CryptoEncryptRequestDto> cryptomanagerRequestDto) {
		ResponseWrapper<CryptoEncryptResponseDto> response = new ResponseWrapper<>();
		response.setResponse(cryptomanagerService.encryptWithPrivate(cryptomanagerRequestDto.getRequest()));
		return response;
	}

	
	/**
	 * Compute signature
	 * 
	 * @param cryptomanagerRequestDto
	 * @return {@link ResponseWrapper<CryptoEncryptResponseDto> }
	 */
	@PreAuthorize("hasAnyRole('INDIVIDUAL','ID_AUTHENTICATION', 'REGISTRATION_ADMIN', 'REGISTRATION_SUPERVISOR', 'REGISTRATION_OFFICER', 'REGISTRATION_PROCESSOR')")
	@ResponseFilter
	@ApiOperation(value = "Sign Data Using Certificate")
	@PostMapping("signature/private/encrypt")
	public ResponseWrapper<SignatureResponseDto> signature(
			@RequestBody RequestWrapper<SignatureRequestDto> signatureResponseDto) {
		ResponseWrapper<SignatureResponseDto> response = new ResponseWrapper<>();
		response.setResponse(cryptomanagerService.signaturePrivateEncrypt(signatureResponseDto.getRequest()));
		return response;
    }
	
	@PreAuthorize("hasAnyRole('INDIVIDUAL','ID_AUTHENTICATION', 'REGISTRATION_ADMIN', 'REGISTRATION_SUPERVISOR', 'REGISTRATION_OFFICER', 'REGISTRATION_PROCESSOR')")
	@ResponseFilter
	@ApiOperation(value = "Get Signature Public Key")
	@GetMapping("signature/publickey/{applicationId}")
	public ResponseWrapper<PublicKeyResponse> getSignaturePublicKey(
			@ApiParam("Id of application") @PathVariable("applicationId") String applicationId,
			@ApiParam("Timestamp as metadata") @RequestParam("timeStamp") String timeStamp,
			@ApiParam("Refrence Id as metadata") @RequestParam("referenceId") Optional<String> referenceId) {
		ResponseWrapper<PublicKeyResponse> response = new ResponseWrapper<>();
		response.setResponse(cryptomanagerService.getSignPublicKey(applicationId,timeStamp,referenceId));
		return response;
    }
	
	
	/**
	 * Controller for Decrypt the data
	 * 
	 * @param cryptomanagerRequestDto {@link CryptomanagerRequestDto} request
	 * @return {@link CryptomanagerResponseDto} decrypted Data
	 */
	@PreAuthorize("hasAnyRole('INDIVIDUAL','ID_AUTHENTICATION', 'TEST', 'REGISTRATION_ADMIN', 'REGISTRATION_SUPERVISOR', 'REGISTRATION_OFFICER', 'REGISTRATION_PROCESSOR')")
	@ResponseFilter
	@PostMapping(value = "/decrypt", produces = "application/json")
	public ResponseWrapper<CryptomanagerResponseDto> decrypt(
			@ApiParam("Salt and Data to decrypt in BASE64 encoding with meta-data") @RequestBody @Valid RequestWrapper<CryptomanagerRequestDto> cryptomanagerRequestDto) {
		ResponseWrapper<CryptomanagerResponseDto> response = new ResponseWrapper<>();
		response.setResponse(cryptomanagerService.decrypt(cryptomanagerRequestDto.getRequest()));
		return response;
	}
}
