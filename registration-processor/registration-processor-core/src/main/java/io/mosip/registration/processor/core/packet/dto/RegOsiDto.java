package io.mosip.registration.processor.core.packet.dto;

import lombok.Data;

@Data
public class RegOsiDto {
	private String regId;

	private String preregId;

	private String officerId;

	private String officerIrisImageName;

	private String officerfingerType;

	private String officerIrisType;

	private String officerPhotoName;

	private String officerHashedPin;

	private String officerHashedPwd;

	private String officerFingerpImageName;

	private String supervisorId;

	private String supervisorFingerpImageName;

	private String supervisorIrisImageName;

	private String supervisorFingerType;

	private String supervisorIrisType;

	private String supervisorHashedPwd;

	private String supervisorHashedPin;

	private String supervisorPhotoName;

	private String introducerId;

	private String introducerTyp;

	private String introducerRegId;

	private String introducerIrisImageName;

	private String introducerFingerpType;

	private String introducerIrisType;

	private String introducerFingerpImageName;
	
	private String introducerPhotoName;

	private String introducerUin;

	private Boolean isActive;

	private Boolean isDeleted;
}
