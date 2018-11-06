package io.mosip.registration.processor.core.packet.dto;

import lombok.Data;

/**
 * Instantiates a new fingerprint.
 */
@Data
public class Fingerprint {
	
	/** The fingerprint image name. */
	private String fingerprintImageName;
	
	/** The quality score. */
	private Double qualityScore;
	
	/** The num retry. */
	private Integer numRetry;
	
	/** The force captured. */
	private Boolean forceCaptured;
	
	/** The finger type. */
	private String fingerType;

}
