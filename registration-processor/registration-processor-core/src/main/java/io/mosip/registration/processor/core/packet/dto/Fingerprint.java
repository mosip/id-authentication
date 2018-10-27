package io.mosip.registration.processor.core.packet.dto;

import lombok.Data;

@Data
public class Fingerprint {
	private String fingerprintImageName;
	private Double qualityScore;
	private Integer numRetry;
	private Boolean forceCaptured;
	private String fingerType;

}
