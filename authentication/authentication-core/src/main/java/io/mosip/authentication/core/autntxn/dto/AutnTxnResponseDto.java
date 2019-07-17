package io.mosip.authentication.core.autntxn.dto;

import java.util.List;
import java.util.Map;

import io.mosip.authentication.core.indauth.dto.AuthError;
import io.mosip.authentication.core.indauth.dto.BaseAuthResponseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 
 * @author Dinesh Karuppiah.T
 *
 */
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
