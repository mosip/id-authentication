package io.mosip.kernel.syncdata.dto;

import lombok.Data;


/**
 * Instantiates a new crypto manager request dto.
 */
@Data
/**
 * 
 * @author Srinivasan
 * @since 1.0.0
 *
 */
public class CryptoManagerRequestDto {

	/** The application id. */
	private String applicationId;
	
	/** The reference id. */
	private String referenceId;
	
	/** The time stamp. */
	private String timeStamp;
	
	/** The data. */
	private String data;
}

