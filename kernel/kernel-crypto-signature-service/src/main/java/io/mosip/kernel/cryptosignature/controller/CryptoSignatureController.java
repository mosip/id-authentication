package io.mosip.kernel.cryptosignature.controller;

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
import io.mosip.kernel.core.signatureutil.model.SignatureResponse;
import io.mosip.kernel.cryptosignature.dto.SignResponseRequestDto;
import io.mosip.kernel.cryptosignature.service.CryptoSignatureService;

/**
 * 
 * @author Uday Kumar
 * @since 1.0.0
 *
 */
@RestController
@CrossOrigin
public class CryptoSignatureController {
	/**
	 * Crypto signature Service field with functions related to signature
	 */
	@Autowired
	CryptoSignatureService service;

	/**
	 * Function to sign response
	 * 
	 * @param requestDto
	 *            {@link SignResponseRequestDto} having required fields.
	 * @return The {@link SignatureResponse}
	 */
	@PreAuthorize("hasAnyRole('INDIVIDUAL','REGISTRATION_PROCESSOR','ID_AUTHENTICATION','TEST')")
	@ResponseFilter
	@PostMapping(value = "/signresponse")
	public ResponseWrapper<SignatureResponse> signResponse(
			@RequestBody @Valid RequestWrapper<SignResponseRequestDto> requestDto) {
		ResponseWrapper<SignatureResponse> response = new ResponseWrapper<>();
		response.setResponse(service.signResponse(requestDto.getRequest()));
		return response;
	}

}
