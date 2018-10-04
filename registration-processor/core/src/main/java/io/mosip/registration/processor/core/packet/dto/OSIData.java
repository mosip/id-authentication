package io.mosip.registration.processor.core.packet.dto;

import lombok.Data;

@Data
public class OSIData {
	// TODO Where to keep images of supervisor/operator bios?
	private String operatorUIN;
	private String operatorName;
	private String operatorFingerprintName;
	private String operatorIrisName;
	private String supervisorUIN;
	private String supervisorName;
	private String supervisorFingerprintName;
	private String supervisorIrisName;
	// Below fields are used for Introducer or HOF
	private String introducerType;
	private String introducerUIN;
	private String introducerName;
	private String introducerEID;
	private String introducerFingerprintName;
	private String introducerIrisName;

}
