package io.mosip.registration.processor.core.packet.dto;

import lombok.Data;

/**
 * Instantiates a new photograph.
 */
@Data
public class Photograph {

	/** The photograph name. */
	private String photographName;
	
	/** The has exception photo. */
	private boolean hasExceptionPhoto;
	
	/** The exception photo name. */
	private String exceptionPhotoName;
	
	/** The quality score. */
	private Double qualityScore;
	
	/** The num retry. */
	private Integer numRetry;

}