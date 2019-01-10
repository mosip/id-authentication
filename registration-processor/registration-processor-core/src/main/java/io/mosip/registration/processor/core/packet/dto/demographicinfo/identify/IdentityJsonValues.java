package io.mosip.registration.processor.core.packet.dto.demographicinfo.identify;
	
import lombok.Data;

/**
 * Instantiates a new identity json values.
 */
@Data
public class IdentityJsonValues {
	
	/** The value. */
	private String value;
	
	/** The weight. */
	private int weight;
}
