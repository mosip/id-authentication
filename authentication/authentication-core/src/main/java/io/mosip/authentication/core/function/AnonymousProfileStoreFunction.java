package io.mosip.authentication.core.function;

import java.util.List;
import java.util.Map;

import io.mosip.authentication.core.indauth.dto.AuthError;

/**
 * The Interface AnonymousProfileStoreFunction.
 * 
 * @author Loganathan S
 */
@FunctionalInterface
public interface AnonymousProfileStoreFunction {
	
	/**
	 * Store anonymous profile.
	 *
	 * @param requestBody the request body
	 * @param responseBody the response body
	 * @param requestMetadata the request metadata
	 * @param responseMetadata the response metadata
	 */
	void storeAnonymousProfile(Map<String, Object> requestBody,
			Map<String, Object> requestMetadata, Map<String, Object> responseMetadata, boolean status, List<AuthError> errors);

}
