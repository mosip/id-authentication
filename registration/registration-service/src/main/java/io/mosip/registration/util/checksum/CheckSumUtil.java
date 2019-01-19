package io.mosip.registration.util.checksum;

import java.util.HashMap;
import java.util.Map;

public class CheckSumUtil {
	
	private CheckSumUtil() {
		
	}
	
	private static final Map<String, String> CHECKSUM_MAP = new HashMap<>();
	
	static {
		CHECKSUM_MAP.put("registration-ui.jar", "uygdfajkdjkHHD56TJHASDJKA");
		CHECKSUM_MAP.put("registration-service.jar", "65gfhab67586cjhsabcjk78");
	}
	
	public static Map<String,String> getCheckSumMap() {
		return CHECKSUM_MAP;
	}

}
