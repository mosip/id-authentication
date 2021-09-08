package io.mosip.authentication.core.spi.profile;

import java.util.Map;

public interface AuthAnonymousProfileService {
	
	void storeAnonymousProfile(Map<String, Object> requestBody, Map<String, Object> responseBody, Map<String, Object> metadata);

}
