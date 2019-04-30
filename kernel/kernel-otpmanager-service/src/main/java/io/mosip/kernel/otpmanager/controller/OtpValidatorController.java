package io.mosip.kernel.otpmanager.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.core.http.ResponseFilter;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.core.otpmanager.spi.OtpValidator;
import io.mosip.kernel.otpmanager.dto.OtpValidatorResponseDto;

/**
 * This class provides controller methods for OTP validation.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 */
@RestController
@CrossOrigin
public class OtpValidatorController {
	/**
	 * Autowired reference for {@link OtpValidator}.
	 */
	@Autowired
	OtpValidator<ResponseEntity<OtpValidatorResponseDto>> otpValidatorService;

	/**
	 * This method validates the OTP against a key.
	 * 
	 * @param key the key against which the OTP needs to be validated.
	 * @param otp the OTP to be validated.
	 * @return the validation status as DTO response.
	 */
	@PreAuthorize("hasAnyRole('INDIVIDUAL','REGISTRATION_ADMIN','REGISTRATION_SUPERVISOR','REGISTRATION_OFFICER','ID_AUTHENTICATION','AUTH')")
	@ResponseFilter
	@GetMapping(value = "/otp/validate")
	public ResponseWrapper<OtpValidatorResponseDto> validateOtp(@RequestParam String key, @RequestParam String otp) {
		ResponseWrapper<OtpValidatorResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(otpValidatorService.validateOtp(key, otp).getBody());
		return responseWrapper;
	}
}
