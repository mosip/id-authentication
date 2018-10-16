package io.mosip.registration.processor.stages.utils;

import java.util.HashMap;
import java.util.Map;

public class CheckSumUtil {
	
	private CheckSumUtil() {
		
	}
	
	private static final Map<String, String> CHECKSUM_MAP = new HashMap<>();
	
	static {
		CHECKSUM_MAP.put("jackson-core-2.2.2.jar", "7yufuay876876gfsadj");
		CHECKSUM_MAP.put("lombok-1.16.18.jar", "uygdfajkdjkHHD56TJHASDJKA");
		CHECKSUM_MAP.put("junit-4.12.jar", "65gfhab67586cjhsabcjk78");	
	}
	
	public static Map<String,String> getCheckSumMap() {
		return CHECKSUM_MAP;
	}

}
