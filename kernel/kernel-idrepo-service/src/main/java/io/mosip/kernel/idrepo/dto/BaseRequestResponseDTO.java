package io.mosip.kernel.idrepo.dto;

import java.util.List;

import lombok.Data;

/**
 * The Class ResponseDTO.
 *
 * @author Manoj SP
 */
@Data
public class BaseRequestResponseDTO {
	
	/** The identity. */
	private Object identity;
	
	private List<Documents> documents;
}
