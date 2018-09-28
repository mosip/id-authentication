package org.mosip.registration.dto.json.metadata;

import lombok.Data;

@Data
public class MetaData {
	private GeoLocation geoLocation;
	private String applicationType;
	private String applicationCategory;
	private String preEnrollmentId;
	private String enrollmentId;
}
