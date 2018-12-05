package io.mosip.authentication.core.dto.idrepo;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * The Class ErrorDTO.
 *
 * @author Rakesh Roshan
 */
@Data
@AllArgsConstructor
public class ErrorDTO {

	/** The err code. */
	private String errCode;
	
	/** The err message. */
	private String errMessage;
}