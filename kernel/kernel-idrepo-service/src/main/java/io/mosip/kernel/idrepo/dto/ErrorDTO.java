package io.mosip.kernel.idrepo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * The Class ErrorDTO.
 *
 * @author Manoj SP
 */
@Data
@AllArgsConstructor
public class ErrorDTO {

	/** The err code. */
	private String errorCode;
	
	/** The err message. */
	private String errorMessage;
}
