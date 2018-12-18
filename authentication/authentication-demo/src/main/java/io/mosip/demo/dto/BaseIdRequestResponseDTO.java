package io.mosip.demo.dto;

import java.time.Instant;

import lombok.Data;

/**
 * The Class BaseIdRequestResponseDTO.
 *
 * @author Manoj SP
 */
@Data
public class BaseIdRequestResponseDTO {
	
	/** The id. */
	private String id;
	
	/** The ver. */
	private String ver;
	
	/** The timestamp. */
	private String timestamp;
}
