package io.mosip.kernel.otpmanager.service;

import org.springframework.http.ResponseEntity;

import io.mosip.kernel.otpmanager.dto.OtpValidatorResponseDto;

/**
 * This interface provides the methods which can be used for OTP validation.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 */
public interface OtpValidatorService {
	/**
	 * This method can be used to validate OTP against an existing key and a given
	 * OTP.
	 * 
	 * @param key
	 *            the key against which the given OTP needs to be validated.
	 * @param otp
	 *            the given OTP that will be validated against the generated OTP.
	 * @return returns true if the given OTP matches against the existing OTP.
	 *         returns false if the given OTP doesn't matches against the existing
	 *         OTP. returns false if the given OTP matches against the existing OTP
	 *         but the expiry limit is reached. returns false if the given OTP
	 *         matches against the existing OTP but the OTP is consumed.
	 */
	public ResponseEntity<OtpValidatorResponseDto> validateOtp(String key, String otp);
}
