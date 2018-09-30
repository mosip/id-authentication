package org.mosip.registration.util.checksum;

import java.util.HashMap;
import java.util.Map;

public class CheckSumUtil {
	
	private CheckSumUtil() {
		
	}

	public static final Map<String, String> CHECKSUM_MAP = new HashMap<>();
	
	static {
		CHECKSUM_MAP.put("lombok-1.16.18.jar", "uygdfajkdjkHHD56TJHASDJKA");
		CHECKSUM_MAP.put("junit-4.12.jar", "65gfhab67586cjhsabcjk78");
		CHECKSUM_MAP.put("jackson-core-2.2.2.jar", "7yufuay876876gfsadj");
	}

}
