package io.mosip.kernel.core.idobjectvalidator.constant;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Manoj SP
 *
 */
public enum IdObjectValidatorLocationMapping {
	
	COUNTRY("Country", "0"),
	
	REGION("Region", "1"),
	
	PROVINCE("Province", "2"),
	
	CITY("City", "3"),
	
	LOCAL_ADMINISTRATIVE_AUTHORITY("Local Administrative Authority", "4"),
	
	POSTAL_CODE("Postal Code", "5");
	

	private final String hierarchyName;
	
	private final String level;
	
	IdObjectValidatorLocationMapping(String hierarchyName, String level) {
		this.hierarchyName = hierarchyName;
		this.level = level;
	}

	public String getHierarchyName() {
		return hierarchyName;
	}

	public String getLevel() {
		return level;
	}
	
	public static Map<String, String> getAllMapping() {
		return Arrays.stream(values()).parallel()
				.collect(Collectors.toMap(IdObjectValidatorLocationMapping::getLevel,
						IdObjectValidatorLocationMapping::getHierarchyName));
	}
}
