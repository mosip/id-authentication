package io.mosip.authentication.core.dto;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * The Interface ObjectWithMetadata - base interface for objects with metadata.
 *
 * @author Loganathan Sekar
 */
public interface ObjectWithMetadata {
	
	/**
	 * Gets the metadata.
	 *
	 * @return the metadata
	 */
	Map<String, Object> getMetadata();
	
	/**
	 * Sets the metadata.
	 *
	 * @param metadata the metadata
	 */
	void setMetadata(Map<String, Object> metadata);
	
	/**
	 * Put metadata.
	 *
	 * @param key the key
	 * @param data the data
	 */
	default void putMetadata(String key, Object data) {
		if(getMetadata() == null) {
			setMetadata(new LinkedHashMap<>());
		}
		getMetadata().put(key, data);
	}
	
	/**
	 * Put all metadata.
	 *
	 * @param metadata the metadata
	 */
	default void putAllMetadata(Map<String, Object> metadata) {
		metadata.forEach(this::putMetadata);
	}

	/**
	 * Gets the metadata.
	 *
	 * @param key the key
	 * @return the metadata
	 */
	default Optional<Object> getMetadata(String key) {
		return getMetadata(key, Object.class);
	}
	
	/**
	 * Gets the metadata.
	 *
	 * @param <T> the generic type
	 * @param key the key
	 * @param clazz the clazz
	 * @return the metadata
	 */
	@SuppressWarnings("unchecked")
	default <T extends Object> Optional<T> getMetadata(String key, Class<T> clazz) {
		return Optional.ofNullable(getMetadata())
				.map(map -> map.get(key))
				.filter(obj -> clazz.isInstance(obj))
				.map(obj -> (T) obj);
	}

	/**
	 * Copy metadata to.
	 *
	 * @param target the target
	 * @param key the key
	 */
	default void copyMetadataTo(ObjectWithMetadata target, String key) {
		this.getMetadata(key).ifPresent(data -> target.putMetadata(key, data));
	}
	
	/**
	 * Copy all metada to.
	 *
	 * @param target the target
	 */
	default void copyAllMetadaTo(ObjectWithMetadata target) {
		if(this.getMetadata() != null && !this.getMetadata().isEmpty()) {
			this.getMetadata().keySet().forEach(key -> this.copyMetadataTo(target, key));
		}
	}
	
}
