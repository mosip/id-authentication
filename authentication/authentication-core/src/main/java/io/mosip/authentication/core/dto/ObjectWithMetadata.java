package io.mosip.authentication.core.dto;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public interface ObjectWithMetadata {
	
	Map<String, Object> getMetadata();
	
	void setMetadata(Map<String, Object> metadata);
	
	default void putMetadata(String key, Object data) {
		if(getMetadata() == null) {
			setMetadata(new LinkedHashMap<>());
		}
		getMetadata().put(key, data);
	}

	default Optional<Object> getMetadata(String key) {
		return getMetadata(key, Object.class);
	}
	
	@SuppressWarnings("unchecked")
	default <T extends Object> Optional<T> getMetadata(String key, Class<T> clazz) {
		return Optional.ofNullable(getMetadata())
				.map(map -> map.get(key))
				.filter(obj -> clazz.isInstance(obj))
				.map(obj -> (T) obj);
	}

	default void copyMetadataTo(ObjectWithMetadata target, String key) {
		this.getMetadata(key).ifPresent(data -> target.putMetadata(key, data));
	}
}
