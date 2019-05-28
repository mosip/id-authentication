package io.mosip.admin.uinmgmt.dto;

import lombok.Data;

@Data
public class UinGenerationStatusDto {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 3950974388837686098L;

	/** The registration id. */
	private String registrationId;
	
	private String statusCode;

}
