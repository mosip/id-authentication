package io.mosip.registration.processor.core.packet.dto;

import lombok.Data;

@Data
public class MetaData {
	private GeoLocation geoLocation;
	private String applicationType;
	private String applicationCategory;
	private String preEnrollmentId;
	private String enrollmentId;
}
