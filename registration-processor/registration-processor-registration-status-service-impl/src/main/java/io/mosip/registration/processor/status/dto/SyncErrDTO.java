package io.mosip.registration.processor.status.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "registrationId", "status", "errorMessage", "errorCode" })
public class SyncErrDTO extends SyncErrorDTO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6676203855461436263L;
	/** The registration id. */
	private String registrationId;

	public SyncErrDTO(String errorcode, String message) {
		super(errorcode, message);

	}

	public String getRegistrationId() {
		return registrationId;
	}

	public void setRegistrationId(String registrationId) {
		this.registrationId = registrationId;
	}

}
