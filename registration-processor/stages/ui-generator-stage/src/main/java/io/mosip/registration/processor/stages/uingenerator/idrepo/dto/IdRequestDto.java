package io.mosip.registration.processor.stages.uingenerator.idrepo.dto;

import java.util.List;

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
	
	/** The uin. */
	private String uin;
	
	/** The status. */
	private String status;
	
	/** The registration id. */
	private String registrationId;
	
	/** The time stamp. */
	private String timestamp;
	
	/** The request. */
	private Object request;
	
	private List<Documents> documents;
}
