package io.mosip.registration.processor.core.idrepo.dto;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * The Class ResponseDTO.
 *
 * @author M1048358 Alok
 */
@Data
public class ResponseDTO {

	/** The entity. */
	private String entity;
	
	/** The identity. */
	private Object identity;
	
	private List<Documents> documents;
	
	/** The status. */
	private String status;

}
