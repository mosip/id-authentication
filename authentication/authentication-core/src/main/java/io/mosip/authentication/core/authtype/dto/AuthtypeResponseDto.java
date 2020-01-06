package io.mosip.authentication.core.authtype.dto;

import java.util.List;
import java.util.Map;

import io.mosip.authentication.core.indauth.dto.AuthError;
import lombok.Data;

/**
 * 
 * @author Dinesh Karuppiah.T
 *
 */

@Data
public class AuthtypeResponseDto {
	
	/** Variable To hold id */
	private String id;

	/** Variable To hold version */
	private String version;

	/** The error List */
	private List<AuthError> errors;

	/** List to hold AutnTxnDto */
	private Map<String, List<AuthtypeStatus>> response;
	/** The id. */

	/** The resTime value */
	private String responseTime;

}
