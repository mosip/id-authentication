package io.mosip.registration.processor.packet.service.dto;

import java.io.Serializable;

import lombok.Data;

/**
 * The Class SyncRegistrationDTO.
 * 
 * @author Rishabh Keshari
 */
@Data
public class SyncRegistrationDTO implements Serializable {

	/** The lang code. */
	private String langCode;

	/** The parent registration id. */
	private String parentRegistrationId;

	/** The registration id. */
	private String registrationId;

	/** The status comment. */
	private String statusComment;

	/** The sync status. */
	private String syncStatus;

	/** The sync type. */
	private String syncType;

	
}
