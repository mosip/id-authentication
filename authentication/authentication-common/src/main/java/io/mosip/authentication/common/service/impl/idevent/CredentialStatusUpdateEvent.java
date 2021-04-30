package io.mosip.authentication.common.service.impl.idevent;

import lombok.Data;

/**
 * Instantiates a new credential status update event.
 * @author Loganathan Sekar
 */
@Data
public class CredentialStatusUpdateEvent {
	
	/** The id. */
	private String id;
	
	/** The request id. */
	private String requestId;
	
	/** The status. */
	private String status;
	
	/** The timestamp. */
	private String timestamp;

}
