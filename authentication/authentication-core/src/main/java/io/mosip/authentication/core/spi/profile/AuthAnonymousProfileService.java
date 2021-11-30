package io.mosip.authentication.core.spi.profile;

import java.util.List;
import java.util.Map;

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
			Map<String, Object> responseMetadata, boolean status, List<Object> errors);

}
