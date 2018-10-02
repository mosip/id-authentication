package io.mosip.kernel.otpmanagerservice.service;

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
	 *            The key against which the given OTP needs to be validated.
	 * @param otp
	 *            The given OTP that will be validated against the generated OTP.
	 * @return Returns true if the given OTP matches against the existing OTP.
	 *         Returns false if the given OTP doesn't matches against the existing
	 *         OTP. Returns false if the given OTP matches against the existing OTP
	 *         but the expiry limit is reached. Returns false if the given OTP
	 *         matches against the existing OTP but the OTP is consumed.
	 */
	public boolean validateOtp(String key, String otp);
}
