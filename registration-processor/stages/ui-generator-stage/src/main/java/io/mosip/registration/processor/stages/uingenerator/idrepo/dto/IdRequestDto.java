package io.mosip.registration.processor.stages.uingenerator.idrepo.dto;

import lombok.Data;

/**
 * The Class IdRequestDTO.
 *
 * @author Ranjitha Siddegowda
 */
@Data
public class IdRequestDto {
	
	/** The id. */
	private String id;
	
	/** The registration id. */
	private String registrationId;
	
	/** The request. */
	private RequestDto request;
	
	/** The time stamp. */
	private String timestamp;
	
	/** The version. */
	private String version;

}
