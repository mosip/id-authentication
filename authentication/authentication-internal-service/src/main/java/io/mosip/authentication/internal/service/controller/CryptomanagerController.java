/*
 * 
 * 
 * 
 * 
 */
package io.mosip.authentication.internal.service.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.http.ResponseFilter;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.cryptomanager.dto.CryptoWithPinRequestDto;
import io.mosip.kernel.cryptomanager.dto.CryptoWithPinResponseDto;
import io.mosip.kernel.cryptomanager.dto.CryptomanagerRequestDto;
import io.mosip.kernel.cryptomanager.dto.CryptomanagerResponseDto;
import io.mosip.kernel.cryptomanager.service.CryptomanagerService;
import io.swagger.annotations.Api;
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
	@PreAuthorize("hasAnyRole('INDIVIDUAL','ID_AUTHENTICATION','TEST', 'REGISTRATION_ADMIN', 'REGISTRATION_SUPERVISOR', 'REGISTRATION_OFFICER', 'REGISTRATION_PROCESSOR','PRE_REGISTRATION_ADMIN','RESIDENT')")
	@ResponseFilter
	@PostMapping(value = "/encrypt", produces = "application/json")
	public ResponseWrapper<CryptomanagerResponseDto> encrypt(
			@ApiParam("Salt and Data to encrypt in BASE64 encoding with meta-data") @RequestBody @Valid RequestWrapper<CryptomanagerRequestDto> cryptomanagerRequestDto) {
		ResponseWrapper<CryptomanagerResponseDto> response = new ResponseWrapper<>();
		response.setResponse(cryptomanagerService.encrypt(cryptomanagerRequestDto.getRequest()));
		return response;
	}

	/**
	 * Controller for Decrypt the data
	 * 
	 * @param cryptomanagerRequestDto {@link CryptomanagerRequestDto} request
	 * @return {@link CryptomanagerResponseDto} decrypted Data
	 */
	@PreAuthorize("hasAnyRole('INDIVIDUAL','ID_AUTHENTICATION', 'TEST', 'REGISTRATION_ADMIN', 'REGISTRATION_SUPERVISOR', 'REGISTRATION_OFFICER', 'REGISTRATION_PROCESSOR','PRE_REGISTRATION_ADMIN','RESIDENT')")
	@ResponseFilter
	@PostMapping(value = "/decrypt", produces = "application/json")
	public ResponseWrapper<CryptomanagerResponseDto> decrypt(
			@ApiParam("Salt and Data to decrypt in BASE64 encoding with meta-data") @RequestBody @Valid RequestWrapper<CryptomanagerRequestDto> cryptomanagerRequestDto) {
		ResponseWrapper<CryptomanagerResponseDto> response = new ResponseWrapper<>();
		response.setResponse(cryptomanagerService.decrypt(cryptomanagerRequestDto.getRequest()));
		return response;
	}

	/**
	 * Controller for Encrypt the data Using Pin
	 * 
	 * @param requestDto {@link CryptoWithPinRequestDto} request
	 * @return {@link CryptoWithPinResponseDto} encrypted Data
	 */
	@PreAuthorize("hasAnyRole('INDIVIDUAL','ID_AUTHENTICATION','TEST', 'REGISTRATION_ADMIN', 'REGISTRATION_SUPERVISOR', 'REGISTRATION_OFFICER', 'REGISTRATION_PROCESSOR','PRE_REGISTRATION_ADMIN','RESIDENT')")
	@ResponseFilter
	@PostMapping(value = "/encryptWithPin", produces = "application/json")
	public ResponseWrapper<CryptoWithPinResponseDto> encryptWithPin(
			@ApiParam("Pin and Data to encrypt") @RequestBody @Valid RequestWrapper<CryptoWithPinRequestDto> requestDto) {
		ResponseWrapper<CryptoWithPinResponseDto> responseDto = new ResponseWrapper<>();
		responseDto.setResponse(cryptomanagerService.encryptWithPin(requestDto.getRequest()));
		return responseDto;
	}

	/**
	 * Controller for Decrypt the data Using Pin
	 * 
	 * @param requestDto {@link CryptoWithPinRequestDto} request
	 * @return {@link CryptoWithPinResponseDto} decrypted Data
	 */
	@PreAuthorize("hasAnyRole('INDIVIDUAL','ID_AUTHENTICATION', 'TEST', 'REGISTRATION_ADMIN', 'REGISTRATION_SUPERVISOR', 'REGISTRATION_OFFICER', 'REGISTRATION_PROCESSOR','PRE_REGISTRATION_ADMIN','RESIDENT')")
	@ResponseFilter
	@PostMapping(value = "/decryptWithPin", produces = "application/json")
	public ResponseWrapper<CryptoWithPinResponseDto> decryptWithPin(
			@ApiParam("Pin and Data to decrypt") @RequestBody @Valid RequestWrapper<CryptoWithPinRequestDto> requestDto) {
		ResponseWrapper<CryptoWithPinResponseDto> responseDto = new ResponseWrapper<>();
		responseDto.setResponse(cryptomanagerService.decryptWithPin(requestDto.getRequest()));
		return responseDto;
	}
}
