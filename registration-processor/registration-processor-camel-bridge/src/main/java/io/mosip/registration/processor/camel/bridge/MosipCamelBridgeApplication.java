package io.mosip.registration.processor.camel.bridge;

/**
 * @author M1041740
 *
 */
public class MosipCamelBridgeApplication {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		MosipBridgeFactory.getEventBus();
	}

}
