package io.mosip.registration.dto.json.metadata;

import lombok.Data;

@Data
public class MetaData {
	private GeoLocation geoLocation;
	private String applicationType;
	private String registrationCategory;
	private String preRegistrationId;
	private String registrationId;
	private String registrationIdHash;
}
