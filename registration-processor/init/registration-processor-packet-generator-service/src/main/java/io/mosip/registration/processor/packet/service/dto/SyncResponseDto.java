package io.mosip.registration.processor.packet.service.dto;

import java.io.Serializable;

import lombok.Data;

/**
 * The Class SyncResponseDto.
 * 
 * @author Ranjitha Siddegowda
 */
@Data
public class SyncResponseDto implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 4422198670538094471L;

	/** The registration id. */
	private String registrationId;

	/** The status. */
	private String status;

	/** The message. */
	private String message;

	/** The parent registration id. */
	private String parentRegistrationId;

	/**
	 * Instantiates a new sync response dto.
	 */
	public SyncResponseDto() {
		super();
	}

}
