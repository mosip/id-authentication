package io.mosip.registration.processor.status.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "status", "errorMessage", "errorCode" })
public class SyncErrorDTO extends ErrorDTO {

	public SyncErrorDTO(String errorcode, String message) {
		super(errorcode, message);
	}

	private static final long serialVersionUID = -5261464773892046294L;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	/** The status. */
	private String status;

}
