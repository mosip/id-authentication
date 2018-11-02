package io.mosip.registration.processor.camel.bridge.util;

import io.mosip.registration.processor.camel.bridge.MosipCamelBridge;

/**
 * The Class BridgeUtil.
 *
 * @author Mukul Puspam
 */
public class BridgeUtil {

	/** The component. */
	private static String component = PropertyFileUtil.getProperty(MosipCamelBridge.class, "application.properties",
			"component");

	/**
	 * Gets the endpoint.
	 *
	 * @param address the address
	 * @return the endpoint
	 */
	public static String getEndpoint(String address) {
		return component + address;
	}
}
