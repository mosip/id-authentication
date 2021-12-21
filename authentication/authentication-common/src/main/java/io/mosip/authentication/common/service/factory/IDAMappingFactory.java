package io.mosip.authentication.common.service.factory;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.common.service.impl.match.IdaIdMapping;

/**
 * Mapping factory class to map Request and Entity Mapping JSON.
 *
 * @author Dinesh Karuppiah.T
 */
public class IDAMappingFactory implements PropertySourceFactory {

	private static final String VALUE = "value";
	private static final String IDENTITY = "identity";

	/**
	 * To create Mapping Factory class for IDA Mapping Configuration.
	 *
	 * @param name the name
	 * @param resource the resource
	 * @return the property source<?>
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public PropertySource<?> createPropertySource(String name, EncodedResource resource) throws IOException {
		Map<?, ?> readValue = new ObjectMapper().readValue(resource.getInputStream(), Map.class);
		Map<String, Object> propertiesMap = new LinkedHashMap<>((Map<String, Object>) readValue.get(IDENTITY));
		Map<String, Object> propertiesListMap = propertiesMap.entrySet().stream()
						.collect(Collectors.toMap(Entry::getKey, entry -> {
							Object value = entry.getValue();
							if(value instanceof Map) {
								Map<String, Object> valueMap = (Map<String, Object>) value;
								Object val = valueMap.get(VALUE);
								if(val instanceof String) {
									return Stream.of( ((String)val).split(","))
												.map(String::trim)
												.filter(str -> !str.isEmpty())
												.collect(Collectors.toList());
								}
							}
							return Collections.emptyList();
						}));
		propertiesListMap.put("dynamicAttributes", getDynamicAttributes(propertiesListMap));
		Map<String, Object> unmodifiableMap = Collections
				.unmodifiableMap(propertiesListMap);
		return new MapPropertySource("json-property", unmodifiableMap);
	}

	/**
	 * Gets the dynamic attributes.
	 *
	 * @param propertiesMap the properties map
	 * @return the dynamic attributes
	 */
	private Map<String, List<String>> getDynamicAttributes(Map<String, ?> propertiesMap) {
		Set<String> staticIdNames = Stream.of(IdaIdMapping.values())
				.map(IdaIdMapping::getIdname)
				.map(String::toLowerCase)
				.collect(Collectors.toSet());
		 return propertiesMap.entrySet()
							.stream()
							.filter(entry -> !staticIdNames.contains(entry.getKey().toLowerCase()))
							.filter(entry -> entry.getValue() instanceof List)
							.collect(Collectors.toMap(Entry::getKey, entry -> (List<String>) entry.getValue()));
	}
}
