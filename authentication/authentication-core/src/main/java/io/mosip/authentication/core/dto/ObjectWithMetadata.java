package io.mosip.authentication.core.dto;

import java.util.Map;

public interface ObjectWithMetadata {
	
	Map<String, Object> getMetadata();
	
	void setMetadata(Map<String, Object> metadata);

}
