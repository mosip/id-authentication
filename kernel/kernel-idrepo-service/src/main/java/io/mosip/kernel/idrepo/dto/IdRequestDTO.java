package io.mosip.kernel.idrepo.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * The Class IdRequestDTO.
 *
 * @author Manoj SP
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class IdRequestDTO extends BaseIdRequestResponseDTO {
	
	private String requesttime;
	
	/** The request. */
	private RequestDTO request;
}
