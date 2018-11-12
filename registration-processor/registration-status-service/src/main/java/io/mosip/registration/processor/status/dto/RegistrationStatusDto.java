package io.mosip.registration.processor.status.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class RegistrationStatusDto implements Serializable{

	private static final long serialVersionUID = 3950974324937686098L;
	
	/** The registration id. */
	private String registrationId;
	
	/** The status code. */
	private String statusCode;
	
}
