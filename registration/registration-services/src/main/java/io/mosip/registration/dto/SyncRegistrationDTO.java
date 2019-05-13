package io.mosip.registration.dto;

import java.math.BigInteger;

import lombok.Data;

/**
 * The DTO Class SyncRegistrationDTO.
 * 
 * @author Sreekar Chukka
 * @version 1.0.0
 */
@Data
public class SyncRegistrationDTO {
	private String langCode;
	private String registrationId;
	private String registrationType;
	private String packetHashValue;
	private BigInteger packetSize;
	private String supervisorStatus;
	private String supervisorComment;

}
