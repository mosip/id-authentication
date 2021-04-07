package io.mosip.authentication.core.hotlist.dto;

import java.util.List;
import io.mosip.authentication.core.indauth.dto.AuthError;
import lombok.Data;

/**
 * @author Mamta A
 *
 */
@Data
public class HotlistResponseDTO {
	
	/** The id. */
	private String id;

	/** The version. */
	private String version;

	/** The response time. */
	private String responseTime;

	/** The error. */
	private List<AuthError> errors;
	
	/** The Response */
	private HotlistDTO response;
}
