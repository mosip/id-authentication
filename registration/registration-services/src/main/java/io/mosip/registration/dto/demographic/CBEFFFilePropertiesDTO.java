package io.mosip.registration.dto.demographic;

import io.mosip.registration.dto.BaseDTO;

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
public class CBEFFFilePropertiesDTO extends BaseDTO {

	/** The format. */
	private String format;

	/** The version. */
	private double version;

	/** The value. */
	private String value;
}
