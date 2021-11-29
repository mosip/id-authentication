package io.mosip.authentication.core.function;

import java.util.Map;

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
	void storeAnonymousProfile(Map<String, Object> requestBody, Map<String, Object> responseBody,
			Map<String, Object> requestMetadata, Map<String, Object> responseMetadata);

}
