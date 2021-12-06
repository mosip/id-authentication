package io.mosip.authentication.core.spi.profile;

import java.util.List;
import java.util.Map;

import io.mosip.authentication.core.indauth.dto.AuthError;

/**
 * The Interface AuthAnonymousProfileService.
 *
 * @author Loganathan Sekar
 */
public interface AuthAnonymousProfileService {
	
	/**
	 * Store anonymous profile.
	 *
	 * @param requestBody the request body
	 * @param requestMetadata the request metadata
	 * @param responseMetadata the response metadata
	 * @param status the status
	 * @param errors the errors
	 */
	void storeAnonymousProfile(Map<String, Object> requestBody, Map<String, Object> requestMetadata,
			Map<String, Object> responseMetadata, boolean status, List<AuthError> errors);

}
