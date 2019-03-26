package io.mosip.registration.processor.core.idrepo.dto;

import java.util.List;

import lombok.Data;

/**
 * The Class ResponseDTO.
 *
 * @author M1048358 Alok
 */
@Data
public class BaseRequestResponseDTO {
	
	/** The identity. */
	private Object identity;
	
	private List<Documents> documents;
}
