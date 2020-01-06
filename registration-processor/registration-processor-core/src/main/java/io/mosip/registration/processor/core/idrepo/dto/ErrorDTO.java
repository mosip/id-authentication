package io.mosip.registration.processor.core.idrepo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The Class ErrorDTO.
 *
 * @author M1049387
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorDTO {


	/** The err code. */
	private String errorCode;
	
	/** The err message. */
	private String message;
}
