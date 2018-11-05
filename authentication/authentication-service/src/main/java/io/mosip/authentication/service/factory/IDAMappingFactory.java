package io.mosip.authentication.service.factory;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

public class IDAMappingFactory implements PropertySourceFactory {

	@Override
	public PropertySource<?> createPropertySource(String name, EncodedResource resource) throws IOException {
		Map readValue = new ObjectMapper().readValue(resource.getInputStream(), Map.class);
		Map<String, Object> unmodifiableMap = Collections.unmodifiableMap((Map) readValue.get("ida-mapping"));
		return new MapPropertySource("json-property", unmodifiableMap);
	}
}
