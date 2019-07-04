package io.mosip.registration.processor.core.auth.dto;

import lombok.Data;

/**
 * @author Ranjitha Siddegowda
 *
 */
@Data
public class ResponseDTO {
	
	private boolean authStatus;
	
	private String staticToken;

}
