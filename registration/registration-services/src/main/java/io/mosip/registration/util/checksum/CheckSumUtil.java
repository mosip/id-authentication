package io.mosip.registration.util.checksum;

import java.util.HashMap;
import java.util.Map;

import io.mosip.registration.update.SoftwareUpdateHandler;

/**
 * Class for CheckSum
 * 
 * @author YASWANTH S
 * @since 1.0.0
 */

public class CheckSumUtil {
	
	private CheckSumUtil() {
		
	}
	

	
	
	private static final String MOSIP_SERVICES = "mosip-services.jar";
	private static final String MOSIP_CLIENT = "mosip-client.jar";
	
	private static final Map<String, String> CHECKSUM_MAP = new HashMap<>();
	
	static {
		SoftwareUpdateHandler softwareUpdateHandler;
		softwareUpdateHandler = new SoftwareUpdateHandler();
		CHECKSUM_MAP.put(MOSIP_CLIENT, softwareUpdateHandler.getCheckSum(MOSIP_CLIENT, null));
		CHECKSUM_MAP.put(MOSIP_SERVICES, softwareUpdateHandler.getCheckSum(MOSIP_SERVICES, null));
		
//		CHECKSUM_MAP.put("registration-ui.jar", "uygdfajkdjkHHD56TJHASDJKA");
//		CHECKSUM_MAP.put("registration-service.jar", "65gfhab67586cjhsabcjk78");
	}
	
	/**
	 * Gets the check sum map.
	 *
	 * @return the check sum map
	 */
	public static Map<String,String> getCheckSumMap() {
		return CHECKSUM_MAP;
	}

}
