package io.mosip.registration.processor.camel.bridge.util;

import io.mosip.registration.processor.camel.bridge.MosipCamelBridge;

/**
 * @author Mukul Puspam
 *
 */
public class BridgeUtil {

	private static String component = PropertyFileUtil.getProperty(MosipCamelBridge.class, "application.properties",
			"component");

	public static String getEndpoint(String address) {
		return component + address;
	}
}
