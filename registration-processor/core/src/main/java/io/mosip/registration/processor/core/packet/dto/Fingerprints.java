package io.mosip.registration.processor.core.packet.dto;

import lombok.Data;

@Data
public class Fingerprints {
	private String fingerprintImageName;
	private double qualityScore;
	private boolean isForceCaptured;
	private String fingerType;

}
