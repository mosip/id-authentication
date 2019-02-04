package io.mosip.registration.processor.camel.bridge;

/**
 * This is main class for Vertx Camel Bridge
 *
 * @author Pranav Kumar
 * @since 0.0.1
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
