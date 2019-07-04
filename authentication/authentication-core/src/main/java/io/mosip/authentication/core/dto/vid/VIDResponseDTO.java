package io.mosip.authentication.core.dto.vid;

import java.util.List;

import io.mosip.authentication.core.indauth.dto.AuthError;
import lombok.Data;

/**
 * This class is a response for the creation of VID.
 *
 * @author Arun Bose S
 */

/**
 * Instantiates a new VID response DTO.
 */
@Data
public class VIDResponseDTO {

	/** The id. */
	private String id;

	/** The version. */
	private String version;

	/** The response time. */
	private String responseTime;

	/** The error. */
	private List<AuthError> errors;

	private ResponseDTO response;

}
