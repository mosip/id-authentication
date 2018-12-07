package io.mosip.kernel.otpmanager.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.core.otpmanager.spi.OtpGenerator;
import io.mosip.kernel.otpmanager.dto.OtpGeneratorRequestDto;
import io.mosip.kernel.otpmanager.dto.OtpGeneratorResponseDto;

/**
 * This class provides controller methods for OTP generation.
 * 
 * @author Sagar Mahapatra
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */
@RestController
public class OtpGeneratorController {
	/**
	 * The reference that autowires the OtpGeneratorService class.
	 */
	@Autowired
	OtpGenerator<OtpGeneratorRequestDto, OtpGeneratorResponseDto> otpGeneratorService;

	/**
	 * This method handles the OTP generation.
	 * 
	 * @param otpDto
	 *            The request DTO for OTP generation.
	 * @return The generated OTP as DTO response.
	 */

	@PostMapping(value = "/v1.0/otp/generate")
	public ResponseEntity<OtpGeneratorResponseDto> generateOtp(@Valid @RequestBody OtpGeneratorRequestDto otpDto) {
		return new ResponseEntity<>(otpGeneratorService.getOtp(otpDto), HttpStatus.CREATED);
	}
}
