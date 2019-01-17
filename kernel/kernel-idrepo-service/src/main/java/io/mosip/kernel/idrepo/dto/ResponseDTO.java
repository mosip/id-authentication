package io.mosip.kernel.idrepo.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * The Class ResponseDTO.
 *
 * @author Manoj SP
 */
@Data
@EqualsAndHashCode(callSuper=true)
public class ResponseDTO extends BaseRequestResponseDTO {
	
	/** The entity. */
	private String entity;
	
}
