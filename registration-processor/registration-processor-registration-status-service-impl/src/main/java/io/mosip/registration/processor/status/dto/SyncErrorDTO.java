package io.mosip.registration.processor.status.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import io.mosip.registration.processor.core.common.rest.dto.ErrorDTO;

@JsonPropertyOrder({"registrationId","status","message","parentRegistrationId","errorcode"}) 
public class SyncErrorDTO extends ErrorDTO{

	public SyncErrorDTO(String errorcode, String message) {
		super(errorcode, message);
	}

	private static final long serialVersionUID = -5261464773892046294L;

	/** The registration id. */
	private String registrationId;
	
	public String getRegistrationId() {
		return registrationId;
	}

	public void setRegistrationId(String registrationId) {
		this.registrationId = registrationId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getParentRegistrationId() {
		return parentRegistrationId;
	}

	public void setParentRegistrationId(String parentRegistrationId) {
		this.parentRegistrationId = parentRegistrationId;
	}

	/** The status. */
	private String status;
	
	/** The parent registration id. */
	private String parentRegistrationId;
}
