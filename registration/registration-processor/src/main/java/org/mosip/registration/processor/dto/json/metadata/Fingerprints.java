package org.mosip.registration.processor.dto.json.metadata;

import lombok.Data;

@Data
public class Fingerprints {
	private String fingerprintImageName;
	private double qualityScore;
	private boolean isForceCaptured;
	private String fingerType;

}
