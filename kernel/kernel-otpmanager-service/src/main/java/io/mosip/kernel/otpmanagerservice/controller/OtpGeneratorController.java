package io.mosip.kernel.otpmanagerservice.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.otpmanagerservice.dto.OtpGeneratorRequestDto;
import io.mosip.kernel.otpmanagerservice.dto.OtpGeneratorResponseDto;
import io.mosip.kernel.otpmanagerservice.service.OtpGeneratorService;

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
	OtpGeneratorService otpGeneratorService;

	/**
	 * This method handles the OTP generation.
	 * 
	 * @param otpDto
	 *            The request DTO for OTP generation.
	 * @return The generated OTP as DTO response.
	 */
	@PostMapping(value = "/otpmanager/otps")
	public ResponseEntity<OtpGeneratorResponseDto> generateOtp(@Valid @RequestBody OtpGeneratorRequestDto otpDto) {
		return new ResponseEntity<>(otpGeneratorService.getOtp(otpDto), HttpStatus.CREATED);
	}
}
