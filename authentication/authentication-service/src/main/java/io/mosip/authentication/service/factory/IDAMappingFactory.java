package io.mosip.authentication.service.factory;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * @author Dinesh Karuppiah.T
 */
public class IDAMappingFactory implements PropertySourceFactory {
	/**
	 * To create Mapping Factory class for IDA Mapping Configuration
	 */
	@SuppressWarnings("unchecked")
	@Override
	public PropertySource<?> createPropertySource(String name, EncodedResource resource) throws IOException {
		Map<?, ?> readValue = new ObjectMapper().readValue(resource.getInputStream(), Map.class);
		Map<String, Object> unmodifiableMap = Collections.unmodifiableMap((Map<String, ?>) readValue.get("ida-mapping"));
		return new MapPropertySource("json-property", unmodifiableMap);
	}
}
