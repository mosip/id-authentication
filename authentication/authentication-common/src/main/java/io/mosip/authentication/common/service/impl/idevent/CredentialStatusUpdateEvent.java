package io.mosip.authentication.common.service.impl.idevent;

import java.util.Map;

import io.mosip.authentication.common.service.websub.dto.EventInterface;
import lombok.Data;

/**
 * Instantiates a new credential status update event.
 * @author Loganathan Sekar
 */

/**
 * Instantiates a new credential status update event.
 */
@Data
public class CredentialStatusUpdateEvent implements EventInterface {
	
	/** The id. */
	private String id;
	
	/** The request id. */
	private String requestId;
	
	/** The status. */
	private String status;
	
	/** The timestamp. */
	private String timestamp;
	
	/** The data. */
	private Map<String, Object> data;

}
