package org.mosip.demo.dto;

/**
 * The DTO class for OTP Validation response.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 * 
 */
public class OtpValidatorResponseDto {

	/**
	 * Variable to hold the otp response status.
	 */
	private String status;

	/**
	 * Variable to hold the otp response message.
	 */
	private String message;

	/**
	 * Getter for status.
	 * 
	 * @return The status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * Setter for status.
	 * 
	 * @param status
	 *            The status to be set.
	 */
	public void setstatus(String status) {
		this.status = status;
	}

	/**
	 * Getter for message.
	 * 
	 * @return The message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Setter for message.
	 * 
	 * @param message
	 *            The message to be set
	 */
	public void setOrdMessage(String message) {
		this.message = message;
	}
}
