package io.mosip.registration.processor.packet.service.util.checksum;

import java.util.HashMap;
import java.util.Map;

/**
 * The Class CheckSumUtil.
 * 
 * @author Sowmya
 */
public class CheckSumUtil {

	/**
	 * Instantiates a new check sum util.
	 */
	private CheckSumUtil() {

	}

	/** The Constant CHECKSUM_MAP. */
	private static final Map<String, String> CHECKSUM_MAP = new HashMap<>();

	static {
		CHECKSUM_MAP.put("registration-ui.jar", "uygdfajkdjkHHD56TJHASDJKA");
		CHECKSUM_MAP.put("registration-service.jar", "65gfhab67586cjhsabcjk78");
	}

	/**
	 * Gets the check sum map.
	 *
	 * @return the check sum map
	 */
	public static Map<String, String> getCheckSumMap() {
		return CHECKSUM_MAP;
	}

}
