package io.mosip.registration.processor.status.dto;

import java.io.Serializable;

/**
 * The Class SyncResponseDto.
 * 
 * @author Ranjitha Siddegowda
 */
public class SyncResponseDto implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 4422198670538094471L;

	/** The status. */
	private String status;
	private String registrationId;

	/**
	 * Instantiates a new sync response dto.
	 */
	public SyncResponseDto() {
		super();
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
	 * @param status
	 *            the new status
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	public String getRegistrationId() {
		return registrationId;
	}

	public void setRegistrationId(String registrationId) {
		this.registrationId = registrationId;
	}

}
