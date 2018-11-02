package io.mosip.registration.processor.core.packet.dto;

import lombok.Data;

/**
 * Instantiates a new meta data.
 */
@Data
public class MetaData {
	
	/** The geo location. */
	private GeoLocation geoLocation;
	
	/** The application type. */
	private String applicationType;
	
	/** The registration category. */
	private String registrationCategory;
	
	/** The pre registration id. */
	private String preRegistrationId;
	
	/** The registration id. */
	private String registrationId;
	
	/** The registration id hash. */
	private String registrationIdHash;
}
