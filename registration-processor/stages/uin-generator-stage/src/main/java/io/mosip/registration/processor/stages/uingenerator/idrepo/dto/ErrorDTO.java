package io.mosip.registration.processor.stages.uingenerator.idrepo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * The Class ErrorDTO.
 *
 * @author M1049387
 */
@Data
@AllArgsConstructor
public class ErrorDTO {

	/** The err code. */
	private String errCode;
	
	/** The err message. */
	private String errMessage;
}
