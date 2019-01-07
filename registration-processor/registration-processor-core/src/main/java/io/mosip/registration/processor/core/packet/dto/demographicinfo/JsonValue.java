package io.mosip.registration.processor.core.packet.dto.demographicinfo;

import lombok.Data;

/**
 * Instantiates a new json value.
 */
@Data	
public class JsonValue {

	/** The label. */
	private String label;
	
	/** The language. */
	private String language;
	
	/** The value. */
	private String value;
}
