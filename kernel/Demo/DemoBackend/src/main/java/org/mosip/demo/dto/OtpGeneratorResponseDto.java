package org.mosip.demo.dto;

/**
 * The Dto class for OTP Generation reponse.
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 */
public class OtpGeneratorResponseDto {

	/**
	 * Variable to hold the generated otp.
	 */
	private String otp;

	/**
	 * Getter for OTP.
	 * 
	 * @return The generated otp.
	 */
	public String getOtp() {
		return otp;
	}

	/**
	 * Setter for OTP.
	 * 
	 * @param otp
	 *            Sets the otp.
	 */
	public void setOtp(String otp) {
		this.otp = otp;
	}

}
