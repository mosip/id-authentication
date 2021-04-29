package io.mosip.authentication.common.service.impl.idevent;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * Instantiates a new auth type status update acknowledge event.
 * @author Loganathan Sekar
 */
@Data
public class AuthTypeStatusUpdateAckEvent {
	
	/** The id. */
	private String id;
	
	/** The request id. */
	private String requestId;
	
	/** The status. */
	private String status;
	
	/** The timestamp. */
	private LocalDateTime timestamp;

}
