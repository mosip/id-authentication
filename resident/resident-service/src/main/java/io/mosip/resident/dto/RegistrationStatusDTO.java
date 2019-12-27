package io.mosip.resident.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class RegistrationStatusDTO implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 3950974324937686098L;

	/** The registration id. */
	private String registrationId;

	/** The status code. */
	private String statusCode;


}