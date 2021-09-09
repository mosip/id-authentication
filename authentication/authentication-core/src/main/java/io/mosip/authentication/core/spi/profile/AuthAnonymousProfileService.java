package io.mosip.authentication.core.spi.profile;

import java.util.Map;

/**
 * 
 * @author Loganathan Sekar
 *
 */
public interface AuthAnonymousProfileService {
	
	void storeAnonymousProfile(Map<String, Object> requestBody, Map<String, Object> responseBody, Map<String, Object> requestMetadata, Map<String, Object> responseMetadata);

}
