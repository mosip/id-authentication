package org.mosip.kernel.otpmanagerservice.controller;

import org.mosip.kernel.otpmanagerservice.constants.OtpStatusConstants;
import org.mosip.kernel.otpmanagerservice.dto.OtpValidatorResponseDto;
import org.mosip.kernel.otpmanagerservice.service.OtpValidatorService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
	 * The reference that autowires the OtpGeneratorService class.
	 */
	@Autowired
	OtpValidatorService otpValidatorService;

	/**
	 * @param key
	 *            The key against which the OTP needs to be validated.
	 * @param otp
	 *            The OTP to be validated.
	 * @return The validation status as DTO response.
	 */
	@GetMapping(value = "/otpmanager/otps")
	public ResponseEntity<OtpValidatorResponseDto> validateOtp(@RequestParam String key, @RequestParam String otp) {
		OtpValidatorResponseDto responseDto = new OtpValidatorResponseDto();
		ResponseEntity<OtpValidatorResponseDto> validationResponseEntity = null;
		boolean isMatching = otpValidatorService.validateOtp(key, otp);
		if (isMatching) {
			responseDto.setstatus(OtpStatusConstants.SUCCESS_STATUS.getProperty());
			responseDto.setOrdMessage(OtpStatusConstants.SUCCESS_MESSAGE.getProperty());
			validationResponseEntity = new ResponseEntity<>(responseDto, HttpStatus.OK);
		} else {
			responseDto.setstatus(OtpStatusConstants.FAILURE_STATUS.getProperty());
			responseDto.setOrdMessage(OtpStatusConstants.FAILURE_MESSAGE.getProperty());
			validationResponseEntity = new ResponseEntity<>(responseDto, HttpStatus.NOT_ACCEPTABLE);
		}
		return validationResponseEntity;
	}
}
