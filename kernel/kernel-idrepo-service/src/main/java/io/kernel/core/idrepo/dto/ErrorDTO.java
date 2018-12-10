package io.kernel.core.idrepo.dto;

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
	private String errCode;
	
	/** The err message. */
	private String errMessage;
}
