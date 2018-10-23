package io.mosip.registration.processor.status.dto;

import java.io.Serializable;

public class RegistrationExternalStatusDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 444222047399177587L;
	private String statusCode;

	public RegistrationExternalStatusDto() {
		super();
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

}
