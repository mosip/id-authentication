package io.mosip.registration.processor.print.kernel.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * The Class ResponseDTO.
 *
 * @author M1048358 Alok
 */
@Data
@EqualsAndHashCode(callSuper=true)
public class ResponseDTO extends BaseRequestResponseDTO{

	/** The entity. */
	private String entity;

}
