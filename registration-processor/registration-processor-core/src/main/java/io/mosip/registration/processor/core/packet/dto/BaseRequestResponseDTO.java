package io.mosip.registration.processor.core.packet.dto;

import lombok.Data;

/**
 * Instantiates a new base request response DTO.
 * @author Rishabh Keshari
 */
@Data
public class BaseRequestResponseDTO {
	
	/** The id. */
	private String id;
	
	/** The ver. */
	private String version;
	
	/** The timestamp. */
	private String timestamp;

}
