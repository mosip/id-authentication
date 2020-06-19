package io.mosip.authentication.internal.service.controller;

import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.authentication.common.service.integration.dto.CryptomanagerRequestDTO;
import io.mosip.authentication.common.service.integration.dto.CryptomanagerResponseDTO;
import io.mosip.authentication.common.service.integration.dto.PublicKeyResponseDto;
import io.mosip.authentication.common.service.integration.dto.TimestampRequestDTO;
import io.mosip.authentication.common.service.integration.dto.ValidatorResponseDTO;
import io.mosip.authentication.core.constant.IdAuthConfigKeyConstants;
import io.mosip.authentication.core.dto.SignatureStatusDto;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.internal.service.manager.KeyServiceManager;
import io.mosip.kernel.cryptomanager.dto.CryptomanagerRequestDto;
import io.mosip.kernel.cryptomanager.dto.CryptomanagerResponseDto;
import io.mosip.kernel.keymanagerservice.dto.PublicKeyResponse;
import io.swagger.annotations.ApiParam;

/**
 * Key manager controller
 * @author Nagarjuna
 *
 */

@RestController
public class KeymanagerController {

	@Autowired
	private Environment env;
	
	/**
	 * Instance for KeymanagerService
	 */
	@Autowired
	KeyServiceManager keymanagerService;
	
	/**
	 * Request mapping to get Public Key
	 * 
	 * @param applicationId Application id of the application requesting publicKey
	 * @param timeStamp     Timestamp of the request
	 * @param referenceId   Reference id of the application requesting publicKey
	 * @return {@link PublicKeyResponse} instance
	 * @throws IdAuthenticationBusinessException 
	 */
	@PreAuthorize("hasAnyRole('REGISTRATION_PROCESSOR','REGISTRATION_ADMIN','REGISTRATION_OFFICER','REGISTRATION_SUPERVISOR','RESIDENT','ID_AUTHENTICATION')")	
	@GetMapping(value = "/publickey/{applicationId}")
	public PublicKeyResponseDto getPublicKey(
			@ApiParam("Id of application") @PathVariable("applicationId") String applicationId,
			@ApiParam("Timestamp as metadata") @RequestParam("timeStamp") String timestamp,
			@ApiParam("Refrence Id as metadata") @RequestParam("referenceId") Optional<String> referenceId) throws IdAuthenticationBusinessException {
		PublicKeyResponseDto responseDto = new PublicKeyResponseDto();
		PublicKeyResponse<String> response = null;
		if (applicationId.equalsIgnoreCase(env.getProperty(IdAuthConfigKeyConstants.IDA_SIGN_APPID)) && referenceId.isPresent()
				&& referenceId.get().equals(env.getProperty(IdAuthConfigKeyConstants.IDA_SIGN_REFID))) {
			response = keymanagerService.getSignPublicKey(applicationId, timestamp, referenceId);
		} else {
			response = keymanagerService.getPublicKey(applicationId, timestamp, referenceId);
		}
		responseDto.setResponse(response);
		return responseDto;
	}
	
	/**
	 * Controller for Encrypt the data
	 * 
	 * @param cryptomanagerRequestDto {@link CryptomanagerRequestDto} request
	 * @return {@link CryptomanagerResponseDto} encrypted Data
	 * @throws IdAuthenticationBusinessException 
	 */
	@PreAuthorize("hasAnyRole('REGISTRATION_PROCESSOR','REGISTRATION_ADMIN','REGISTRATION_OFFICER','REGISTRATION_SUPERVISOR','RESIDENT','ID_AUTHENTICATION')")	
	@PostMapping(value = "/encrypt", produces = "application/json")
	public CryptomanagerResponseDTO encrypt(
			@ApiParam("Salt and Data to encrypt in BASE64 encoding with meta-data") @RequestBody @Valid CryptomanagerRequestDTO requestDto) throws IdAuthenticationBusinessException {
		CryptomanagerResponseDTO responseDto = new CryptomanagerResponseDTO();
		responseDto.setResponse(keymanagerService.encrypt(requestDto.getRequest().getData(),requestDto.getRequest().getReferenceId(),requestDto.getRequest().getAad(),requestDto.getRequest().getSalt()));
		return responseDto;
	}
	
	/**
	 * Controller for Decrypt the data
	 * 
	 * @param cryptomanagerRequestDto {@link CryptomanagerRequestDto} request
	 * @return {@link CryptomanagerResponseDto} decrypted Data
	 * @throws IdAuthenticationBusinessException 
	 */
	@PreAuthorize("hasAnyRole('REGISTRATION_PROCESSOR','REGISTRATION_ADMIN','REGISTRATION_OFFICER','REGISTRATION_SUPERVISOR','RESIDENT','ID_AUTHENTICATION')")		
	@PostMapping(value = "/decrypt", produces = "application/json")
	public CryptomanagerResponseDTO decrypt(
			@ApiParam("Salt and Data to decrypt in BASE64 encoding with meta-data") @RequestBody @Valid CryptomanagerRequestDTO cryptomanagerRequestDto) throws IdAuthenticationBusinessException {
		CryptomanagerResponseDTO responseDto = new CryptomanagerResponseDTO();
		responseDto.setResponse(keymanagerService.decrypt(cryptomanagerRequestDto.getRequest()));
		return responseDto;
	}
	
	/**
	 * Controller for verifying the jwsSignature the data
	 * 
	 * @param cryptomanagerRequestDto {@link CryptomanagerRequestDto} request
	 * @return {@link CryptomanagerResponseDto} decrypted Data
	 * @throws IdAuthenticationBusinessException 
	 */
	@PreAuthorize("hasAnyRole('REGISTRATION_PROCESSOR','REGISTRATION_ADMIN','REGISTRATION_OFFICER','REGISTRATION_SUPERVISOR','RESIDENT','ID_AUTHENTICATION')")	
	@PostMapping(value = "/verify", produces = "application/json")
	public SignatureStatusDto verify(
			@ApiParam("data to verify") @RequestBody @Valid String jwsSignature) throws IdAuthenticationBusinessException {
		return keymanagerService.verifySignature(jwsSignature);
	}
	
	/**
	 * Controller for validating the signature
	 * @param timestampRequestDto
	 * @return
	 * @throws IdAuthenticationBusinessException
	 */
	@PreAuthorize("hasAnyRole('REGISTRATION_PROCESSOR','REGISTRATION_ADMIN','REGISTRATION_OFFICER','REGISTRATION_SUPERVISOR','RESIDENT','ID_AUTHENTICATION')")	
	@PostMapping(value = "/validate")
	public ValidatorResponseDTO validate(
			@RequestBody @Valid TimestampRequestDTO timestampRequestDto) throws IdAuthenticationBusinessException {
		ValidatorResponseDTO responseDto = new ValidatorResponseDTO();
		responseDto.setResponse(keymanagerService.validateSinature(timestampRequestDto.getRequest()));
		return responseDto;
	}

}
