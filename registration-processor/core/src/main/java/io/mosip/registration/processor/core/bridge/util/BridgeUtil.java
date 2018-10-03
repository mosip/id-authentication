package io.mosip.registration.processor.core.bridge.util;

import io.mosip.registration.processor.core.bridge.MosipCamelBridge;

public class BridgeUtil {

	private static String component = PropertyFileUtil.getProperty(MosipCamelBridge.class, "application.properties", "component");

	public static String getEndpoint(String address) {
		return component + address;
	}
}
