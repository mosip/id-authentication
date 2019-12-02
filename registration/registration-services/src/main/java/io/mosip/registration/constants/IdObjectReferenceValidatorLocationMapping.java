package io.mosip.registration.constants;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The Enum IdObjectReferenceValidatorLocationMapping.
 *
 * @author Manoj SP
 */
public enum IdObjectReferenceValidatorLocationMapping {
	
	COUNTRY("Country", "0"),
	
	REGION("Region", "1"),
	
	PROVINCE("Province", "2"),
	
	CITY("City", "3"),
	
	ZONE("Zone", "4"),
	
	POSTAL_CODE("Postal Code", "5");
	

	private final String hierarchyName;
	
	private final String level;
	
	/**
	 * Instantiates a new id object reference validator location mapping.
	 *
	 * @param hierarchyName the hierarchy name
	 * @param level the level
	 */
	IdObjectReferenceValidatorLocationMapping(String hierarchyName, String level) {
		this.hierarchyName = hierarchyName;
		this.level = level;
	}

	/**
	 * Gets the hierarchy name.
	 *
	 * @return the hierarchy name
	 */
	public String getHierarchyName() {
		return hierarchyName;
	}

	/**
	 * Gets the level.
	 *
	 * @return the level
	 */
	public String getLevel() {
		return level;
	}
	
	/**
	 * Gets the all mapping.
	 *
	 * @return the all mapping
	 */
	public static Map<String, String> getAllMapping() {
		return Arrays.stream(values()).parallel()
				.collect(Collectors.toMap(IdObjectReferenceValidatorLocationMapping::getLevel,
						IdObjectReferenceValidatorLocationMapping::getHierarchyName));
	}
}