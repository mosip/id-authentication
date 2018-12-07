package io.mosip.kernel.otpmanager.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
public class OtpValidatorController {
	/**
	 * The reference that autowires the OtpValidatorService class.
	 */
	@Autowired
	OtpValidator<ResponseEntity<OtpValidatorResponseDto>> otpValidatorService;

	/**
	 * This method validates the OTP against a key.
	 * 
	 * @param key
	 *            the key against which the OTP needs to be validated.
	 * @param otp
	 *            the OTP to be validated.
	 * @return the validation status as DTO response.
	 */
	@GetMapping(value = "/v1.0/otp/validate")
	public ResponseEntity<OtpValidatorResponseDto> validateOtp(@RequestParam String key, @RequestParam String otp) {
		return otpValidatorService.validateOtp(key, otp);
	}
}
