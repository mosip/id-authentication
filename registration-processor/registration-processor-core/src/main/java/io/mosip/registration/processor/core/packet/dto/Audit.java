package io.mosip.registration.processor.core.packet.dto;

import lombok.Data;
/**
 * Instantiates a new audit.
 */

/**
 * Instantiates a new audit.
 */
@Data
public class Audit {

	/** The event id. */
	private String eventId;

	/** The start timestamp. */
	private String startTimestamp;

	/** The end timestamp. */
	private String endTimestamp;

}
