package io.mosip.resident.dto;

import java.util.List;
import java.util.Map;


import lombok.Data;

@Data
public class AutnTxnResponseDto {

	/** Variable To hold id */
	private String id;

	/** Variable To hold version */
	private String version;

	/** The error List */
	private List<AuthError> errors;

	/** List to hold AutnTxnDto */
	private Map<String, List<AutnTxnDto>> response;
	/** The id. */

	/** The resTime value */
	private String responseTime;

}
