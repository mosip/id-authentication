package org.mosip.registration.dto;

import lombok.Data;

@Data
public class OSIDataDTO {
	// TODO Where to keep images of supervisor/operator bios?
	private String operatorUIN;
	private String operatorName;
	private String operatorUserID;
	private String supervisorUIN;
	private String supervisorName;
	private String supervisorUserID;
	// Below fields are used for Introducer or HOF
	private String introducerType;
}
