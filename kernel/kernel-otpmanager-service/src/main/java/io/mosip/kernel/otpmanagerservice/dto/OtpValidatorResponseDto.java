package io.mosip.kernel.otpmanagerservice.dto;

/**
 * The DTO class for OTP Validation response.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 * 
 */
public class OtpValidatorResponseDto {

	/**
	 * Variable to hold the OTP validation status.
	 */
	private String status;

	/**
	 * Variable to hold the OTP validation message.
	 */
	private String message;

	/**
	 * Getter for OTP Validation status.
	 * 
	 * @return The OTP validation status.
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * Setter for OTP validation status.
	 * 
	 * @param status
	 *            The OTP validation status to be set.
	 */
	public void setstatus(String status) {
		this.status = status;
	}

	/**
	 * Getter for OTP validation message.
	 * 
	 * @return The OTP validation message.
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Setter for OTP validation message.
	 * 
	 * @param message
	 *            The OTP validation message to be set
	 */
	public void setOrdMessage(String message) {
		this.message = message;
	}
}
