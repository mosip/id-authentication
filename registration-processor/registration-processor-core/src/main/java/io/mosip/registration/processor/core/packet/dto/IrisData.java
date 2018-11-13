package io.mosip.registration.processor.core.packet.dto;

import java.util.List;

import lombok.Data;

/**
 * Instantiates a new iris data.
 */
@Data
public class IrisData {
	
	/** The iris. */
	private List<Iris> iris;
	
	/** The num retry. */
	private int numRetry;
	
	/** The exception iris. */
	private List<ExceptionIris> exceptionIris;

}
