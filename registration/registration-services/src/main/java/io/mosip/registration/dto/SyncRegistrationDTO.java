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
	private String parentRegistrationId;
	private String registrationId;
	private String statusComment;
	private String syncStatus;
	private String syncType;
	private BigInteger packetSize;
	private String packetHash;
	private String supervisorStatus;
	private String supervisorComments;

}
