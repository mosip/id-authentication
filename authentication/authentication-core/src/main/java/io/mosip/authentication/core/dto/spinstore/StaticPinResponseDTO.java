package io.mosip.authentication.core.dto.spinstore;

import java.util.List;

import io.mosip.authentication.core.dto.indauth.AuthError;
import lombok.Data;

/**
 * This Class is used to provide Response for static pin value.
 * 
 * @author Prem Kumar
 *
 */
@Data
public class StaticPinResponseDTO {
	
	/** Variable to hold id */
	private String id;

	/** The value Version */
	private String version;

	/** variable to hold status */
	private boolean status;

	/** Variable to hold Response time */
	private String responseTime;

	/** The List of error values */
	private List<AuthError> errors;
}
