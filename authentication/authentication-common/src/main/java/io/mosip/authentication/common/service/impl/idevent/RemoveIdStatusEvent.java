package io.mosip.authentication.common.service.impl.idevent;

import java.util.Map;

import io.mosip.authentication.common.service.websub.dto.EventInterface;
import lombok.Data;

/**
 * Instantiates a new remove id status event.
 * 
 * @author Ritik Jain
 */
@Data
public class RemoveIdStatusEvent implements EventInterface {

	/** The id. */
	private String id;

	/** The timestamp. */
	private String timestamp;

	/** The data. */
	private Map<String, Object> data;

}
