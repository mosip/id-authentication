package io.mosip.authentication.core.authtype.dto;

import java.util.List;

import io.mosip.authentication.core.indauth.dto.AuthError;
import lombok.Data;

@Data
public class UpdateAuthtypeStatusResponseDto {
	
	private String id;

	/** Variable To hold version */
	private String version;

	/** The error List */
	private List<AuthError> errors;

	/** The resTime value */
	private String responseTime;

}
