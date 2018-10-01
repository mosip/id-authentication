package org.mosip.kernel.otpmanagerservice.dto;

/**
 * The DTO class for OTP Generation response.
 * 
 * @author Ritesh Sinha
 * @author Sagar Mahapatra
 * @since 1.0.0
 * 
 */
public class OtpGeneratorResponseDto {
	/**
	 * Variable to hold the OTP.
	 */
	private String otp;

	/**
	 * Variable to hold the status.
	 */
	private String status;

	/**
	 * Getter for OTP status.
	 * 
	 * @return The OTP status.
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * Setter for OTP status.
	 * 
	 * @param status
	 *            The OTP status to be set.
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * Getter for OTP.
	 * 
	 * @return The generated OTP.
	 */
	public String getOtp() {
		return otp;
	}

	/**
	 * Setter for OTP.
	 * 
	 * @param otp
	 *            The OTP to be set.
	 */
	public void setOtp(String otp) {
		this.otp = otp;
	}
}
