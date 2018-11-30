package io.mosip.registration.dto;


/**
 * The DTO class for OTP Validation response.
 * 
 * @author Yaswanth S
 * @since 1.0.0
 * 
 */
public class OtpValidatorResponseDTO {
	
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
