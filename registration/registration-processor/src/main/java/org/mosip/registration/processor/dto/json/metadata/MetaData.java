package org.mosip.registration.processor.dto.json.metadata;

import lombok.Data;

@Data
public class MetaData {
	private GeoLocation geoLocation;
	private String applicationType;
	private String applicationCategory;
	private String preEnrollmentId;
	private String enrollmentId;
}
