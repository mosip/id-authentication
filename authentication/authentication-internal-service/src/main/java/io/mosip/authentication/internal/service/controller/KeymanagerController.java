package io.mosip.authentication.internal.service.controller;

import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import io.mosip.kernel.keymanagerservice.dto.CSRGenerateRequestDto;
import io.mosip.kernel.keymanagerservice.dto.KeyPairGenerateRequestDto;
import io.mosip.kernel.keymanagerservice.dto.KeyPairGenerateResponseDto;
import io.mosip.kernel.keymanagerservice.dto.UploadCertificateRequestDto;
import io.mosip.kernel.keymanagerservice.dto.UploadCertificateResponseDto;
import io.mosip.kernel.keymanagerservice.service.KeymanagerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;

/**
 * This class provides controller methods for Key manager.
 * 
 * @author Dharmesh Khandelwal
 * @author Urvil Joshi
 * @since 1.0.0
 *
 */
@CrossOrigin
@RestController
@Api(tags = { "keymanager" }, value = "Operation related to Keymanagement")
public class KeymanagerController {

	@Value("${mosip.sign.refid:SIGN}")
	private String certificateSignRefID;

	/** The sign applicationid. */
	@Value("${mosip.sign.applicationid:KERNEL}")
	private String signApplicationid;

	/**
	 * Instance for KeymanagerService
	 */
	@Autowired
	KeymanagerService keymanagerService;

	/**
	 * Generate Master Key for the provided APP ID.
	 * 
	 * @param objectType 			   response Object Type. Support types are Certificate/CSR. Path Parameter.
	 * @param keyPairGenRequestDto     {@link KeyPairGenerateRequestDto} request
	 * @return {@link KeyPairGenerateResponseDto} instance
	*/
	//t@PreAuthorize("hasAnyRole('KEY_MAKER')")
	@ResponseFilter
	@PostMapping(value = "/generateMasterKey/{objectType}")
	public ResponseWrapper<KeyPairGenerateResponseDto> generateMasterKey(
			@ApiParam("Response Type Certificate/CSR") @PathVariable("objectType") String objectType,
			@RequestBody @Valid RequestWrapper<KeyPairGenerateRequestDto> keyPairGenRequestDto) {

		ResponseWrapper<KeyPairGenerateResponseDto> response = new ResponseWrapper<>();
		response.setResponse(keymanagerService.generateMasterKey(objectType, keyPairGenRequestDto.getRequest()));
		return response;
	}

	/**
	 * Request to get Certificate for the Provided APP ID & REF ID.
	 * 
	 * @param applicationId Application id of the application requesting Certificate
	 * @param referenceId   Reference id of the application requesting Certificate. Blank in case of Master Key.
	 * @return {@link KeyPairGenerateResponseDto} instance
	*/
	@PreAuthorize("hasAnyRole('INDIVIDUAL','REGISTRATION_PROCESSOR','REGISTRATION_ADMIN','REGISTRATION_SUPERVISOR','REGISTRATION_OFFICER','ID_AUTHENTICATION','TEST','PRE_REGISTRATION_ADMIN','RESIDENT')")
	@ResponseFilter
	@GetMapping(value = "/getCertificate")
	public ResponseWrapper<KeyPairGenerateResponseDto> getCertificate(
		@ApiParam("Id of application") @RequestParam("applicationId") String applicationId,
		@ApiParam("Refrence Id as metadata") @RequestParam("referenceId") Optional<String> referenceId) {

		ResponseWrapper<KeyPairGenerateResponseDto> response = new ResponseWrapper<>();
		response.setResponse(keymanagerService.getCertificate(applicationId, referenceId));
		return response;
	}

	/**
	 * Request to Generate CSR for the provided APP ID & REF ID along with other certificate params.
	 * 
	 * @param csrGenRequestDto     {@link CSRGenerateRequestDto} request
	 * @return {@link KeyPairGenerateResponseDto} instance
	*/
	@PreAuthorize("hasAnyRole('INDIVIDUAL','REGISTRATION_PROCESSOR','REGISTRATION_ADMIN','REGISTRATION_SUPERVISOR','REGISTRATION_OFFICER','ID_AUTHENTICATION','TEST','PRE_REGISTRATION_ADMIN','RESIDENT')")
	@ResponseFilter
	@PostMapping(value = "/generateCSR")
	public ResponseWrapper<KeyPairGenerateResponseDto> generateCSR(
		@RequestBody @Valid RequestWrapper<CSRGenerateRequestDto> csrGenRequestDto) {

		ResponseWrapper<KeyPairGenerateResponseDto> response = new ResponseWrapper<>();
		response.setResponse(keymanagerService.generateCSR(csrGenRequestDto.getRequest()));
		return response;
	}

	/**
	 * Update signed certificate for the provided APP ID & REF ID.
	 * 
	 * @param uploadCertRequestDto     {@link UploadCertificateRequestDto} request
	 * @return {@link UploadCertificateResponseDto} instance
	*/
	@PreAuthorize("hasAnyRole('INDIVIDUAL','REGISTRATION_PROCESSOR','REGISTRATION_ADMIN','REGISTRATION_SUPERVISOR','REGISTRATION_OFFICER','ID_AUTHENTICATION','TEST','PRE_REGISTRATION_ADMIN','RESIDENT')")
	@ResponseFilter
	@PostMapping(value = "/uploadCertificate")
	public ResponseWrapper<UploadCertificateResponseDto> uploadCertificate(
		@RequestBody @Valid RequestWrapper<UploadCertificateRequestDto> uploadCertRequestDto) {

		ResponseWrapper<UploadCertificateResponseDto> response = new ResponseWrapper<>();
		response.setResponse(keymanagerService.uploadCertificate(uploadCertRequestDto.getRequest()));
		return response;
	}
}
