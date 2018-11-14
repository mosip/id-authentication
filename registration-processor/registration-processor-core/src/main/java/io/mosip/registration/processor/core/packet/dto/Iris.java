package io.mosip.registration.processor.core.packet.dto;

import lombok.Data;

/**
 * Instantiates a new iris.
 */
@Data
public class Iris {
	
	/** The iris image name. */
	private String irisImageName;
	
	/** The quality score. */
	private double qualityScore;
	
	/** The force captured. */
	private Boolean forceCaptured;
	
	/** The iris type. */
	private String irisType;
	
	/** The num retry. */
	private Integer numRetry;
}
