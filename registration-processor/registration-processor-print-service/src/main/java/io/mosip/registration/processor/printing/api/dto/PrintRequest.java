package io.mosip.registration.processor.printing.api.dto;

import lombok.Data;

/**
 * Instantiates a new prints the request.
 * 
 * @author M1048358 Alok
 */
@Data
public class PrintRequest {

	/** The id. */
	private String id;

	/** The version. */
	private String version;

	/** The request time. */
	private String requestTime;

	/** The request. */
	private RequestDTO request;
}
