package io.mosip.registration.processor.core.idrepo.dto;

import lombok.Data;

/**
 * The Class BaseIdRequestResponseDTO.
 *
 * @author M1048358 Alok
 */
@Data
public class BaseIdRequestResponseDTO {
	
	/** The id. */
	private String id;
	
	/** The ver. */
	private String version;
	
	/** The timestamp. */
	private String timestamp;
}
