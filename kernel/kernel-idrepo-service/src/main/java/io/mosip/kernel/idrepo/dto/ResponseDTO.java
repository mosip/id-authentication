package io.mosip.kernel.idrepo.dto;

import java.util.List;

import lombok.Data;

/**
 * The Class ResponseDTO.
 *
 * @author Manoj SP
 */
@Data
public class ResponseDTO {
	
	/** The entity. */
	private String entity;
	
	/** The identity. */
	private Object identity;
	
	private List<Documents> documents;
}
