package io.mosip.registration.dto.json.metadata;

import lombok.Data;

@Data
public class OSIData {
	private String operatorId;
	private String operatorFingerprintImage;
	private String operatorIrisName;
	private String supervisorId;
	private String supervisorName;
	private String supervisorFingerprintImage;
	private String supervisorIrisName;
	// Below fields are used for Introducer or HOF
	private String introducerType;
	private String introducerName;
	private String introducerUIN;
	private String introducerUINHash;
	private String introducerRID;
	private String introducerRIDHash;
	private String introducerFingerprintImage;
	private String introducerIrisImage;

}
