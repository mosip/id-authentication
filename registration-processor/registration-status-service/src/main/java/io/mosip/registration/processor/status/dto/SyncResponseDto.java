package io.mosip.registration.processor.status.dto;

import java.io.Serializable;

/**
 * The Class SyncResponseDto.
 * 
 * @author Ranjitha Siddegowda
 */
public class SyncResponseDto implements Serializable{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 4422198670538094471L;

	/** The registration id. */
	private String registrationId;
	
	/** The status. */
	private String status;
	
	/** The message. */
	private String message;
	
	/** The parent registration id. */
	private String parentRegistrationId;
	
	/**
	 * Instantiates a new sync response dto.
	 */
	public SyncResponseDto() {
		super();
	}

	/**
	 * Gets the registration id.
	 *
	 * @return the registration id
	 */
	public String getRegistrationId() {
		return registrationId;
	}

	/**
	 * Sets the registration id.
	 *
	 * @param registrationId the new registration id
	 */
	public void setRegistrationId(String registrationId) {
		this.registrationId = registrationId;
	}

	/**
	 * Gets the status.
	 *
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * Sets the status.
	 *
	 * @param status the new status
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * Gets the message.
	 *
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Sets the message.
	 *
	 * @param message the new message
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * Gets the parent registration id.
	 *
	 * @return the parent registration id
	 */
	public String getParentRegistrationId() {
		return parentRegistrationId;
	}

	/**
	 * Sets the parent registration id.
	 *
	 * @param parentRegistrationId the new parent registration id
	 */
	public void setParentRegistrationId(String parentRegistrationId) {
		this.parentRegistrationId = parentRegistrationId;
	}
	
}
