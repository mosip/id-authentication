package io.mosip.registration.processor.core.packet.dto.idjson;

import lombok.Getter;
import lombok.Setter;

/**
 * This class contains the properties required for the CBEFF file.
 *
 * @author Balaji Sridharan
 * @since 1.0.0
 */
@Getter
@Setter
public class CBEFFFilePropertiesDTO {

	/** The format. */
	private String format;

	/** The version. */
	private double version;

	/** The value. */
	private String value;
}
