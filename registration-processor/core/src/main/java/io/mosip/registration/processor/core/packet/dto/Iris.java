package io.mosip.registration.processor.core.packet.dto;

import lombok.Data;

@Data
public class Iris {
	private String irisImageName;
	private double qualityScore;
	private Boolean forceCaptured;
	private String irisType;
}
