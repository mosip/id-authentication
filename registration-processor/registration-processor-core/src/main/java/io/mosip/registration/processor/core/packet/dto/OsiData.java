package io.mosip.registration.processor.core.packet.dto;

import lombok.Data;

@Data
public class OsiData {

	private String operatorId;
	private String operatorFingerprintImage;
	private String operatorIrisName;
	private String supervisorId;
	private String supervisorName;
	private String supervisorFingerprintImage;
	private String supervisorIrisName;

	private String introducerType;
	private String introducerUIN;
	private String introducerName;
	private String introducerUINHash;
	private Object introducerRID;
	private Object introducerRIDHash;
	private String introducerFingerprintImage;
	private String introducerIrisImage;

}
