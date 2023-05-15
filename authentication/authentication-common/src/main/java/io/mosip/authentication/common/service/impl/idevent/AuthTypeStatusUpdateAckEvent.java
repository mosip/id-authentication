package io.mosip.authentication.common.service.impl.idevent;

import java.time.LocalDateTime;
import java.util.Map;

import io.mosip.authentication.common.service.websub.dto.EventInterface;
import lombok.Data;

/**
 * Instantiates a new auth type status update acknowledge event.
 * @author Loganathan Sekar
 */
@Data
public class AuthTypeStatusUpdateAckEvent implements EventInterface{
	
	/** The id. */
	private String id;
	
	/** The request id. */
	private String requestId;
	

	
	/** The timestamp. */
	private LocalDateTime timestamp;
	
	/** The data. */
	private Map<String, Object> data;

}
