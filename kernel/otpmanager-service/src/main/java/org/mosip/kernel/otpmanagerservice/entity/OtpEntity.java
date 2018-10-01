package org.mosip.kernel.otpmanagerservice.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * The entity class for OTP.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 */
@Entity
@Table(name = "otp_data")
public class OtpEntity {
	/**
	 * The variable that holds the unique ID.
	 */
	@Id
	@Column(name = "key_id")
	private String keyId;

	/**
	 * The variable that holds the generated OTP.
	 */
	@Column(name = "generated_otp", nullable = false)
	private String generatedOtp;

	/**
	 * The variable that holds the number of validation attempts.
	 */
	@Column(name = "num_of_attempt", nullable = false)
	private int numOfAttempt;

	/**
	 * The variable that holds the time at which the OTP was generated.
	 */
	@Column(name = "generation_time", nullable = false)
	private LocalDateTime generationTime;

	/**
	 * The variable that holds the status of the OTP. It can be UNUSED, CONSUMED,
	 * MAX_ATTEMPT_REACHED.
	 */
	@Column(name = "otp_status", nullable = false)
	private String otpStatus;

	/**
	 * The variable that holds the time at which the OTP validation was last
	 * attempted. The default value is the generation time.
	 */
	@Column(name = "validation_time", nullable = false)
	private LocalDateTime validationTime;

	/**
	 * The default constructor for OtpEntity.
	 */
	public OtpEntity() {
		generationTime = LocalDateTime.now();
		validationTime = generationTime;
		otpStatus = "OTP_UNUSED";
	}

	/**
	 * Setter for keyId.
	 * 
	 * @param keyId
	 *            The keyId to be set.
	 */
	public void setKeyId(String keyId) {
		this.keyId = keyId;
	}

	/**
	 * Getter for generated OTP.
	 * 
	 * @return The generated OTP.
	 */
	public String getGeneratedOtp() {
		return generatedOtp;
	}

	/**
	 * Setter for generated OTP.
	 * 
	 * @param generatedOtp
	 *            The OTP to be set.
	 */
	public void setGeneratedOtp(String generatedOtp) {
		this.generatedOtp = generatedOtp;
	}

	/**
	 * Getter for number of OTP validation attempts.
	 * 
	 * @return The number of OTP validation attempts.
	 */
	public int getNumOfAttempt() {
		return numOfAttempt;
	}

	/**
	 * Setter to increment the number of OTP validation attempts.
	 * 
	 * @param numOfAttempt
	 *            The number of attempts to be set.
	 */
	public void setNumOfAttempt(int numOfAttempt) {
		this.numOfAttempt = numOfAttempt;
	}

	/**
	 * Getter for OTP generation time.
	 * 
	 * @return The OTP generation time.
	 */
	public LocalDateTime getGenerationTime() {
		return generationTime;
	}

	/**
	 * Getter for the present OTP status.
	 * 
	 * @return The OTP status.
	 */
	public String getOtpStatus() {
		return otpStatus;
	}

	/**
	 * Setter for OTP status.
	 * 
	 * @param otpStatus
	 *            The OTP status to be set.
	 */
	public void setOtpStatus(String otpStatus) {
		this.otpStatus = otpStatus;
	}

	/**
	 * Getter for validation time.
	 * 
	 * @return The time at which OTP validation was attempted.
	 */
	public LocalDateTime getValidationTime() {
		return validationTime;
	}

	/**
	 * Setter for validation time.
	 * 
	 * @param validationTime
	 *            The time at which OTP validation was attempted.
	 */
	public void setValidationTime(LocalDateTime validationTime) {
		this.validationTime = validationTime;
	}
}
