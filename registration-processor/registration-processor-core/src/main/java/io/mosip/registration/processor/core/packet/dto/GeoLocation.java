package io.mosip.registration.processor.core.packet.dto;

import lombok.Data;

/**
 * Instantiates a new geo location.
 */
@Data
public class GeoLocation {
	
	/** The latitude. */
	private Double latitude;
	
	/** The longitude. */
	private Double longitude;
}
