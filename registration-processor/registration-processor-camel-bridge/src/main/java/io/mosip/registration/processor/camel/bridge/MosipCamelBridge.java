package io.mosip.registration.processor.camel.bridge;
	
import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;

import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.vertx.camel.CamelBridge;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

/**
 * The Class MosipCamelBridge.
 * 
 * @author Mukul Puspam
 * @since 0.0.1
 */
public class MosipCamelBridge extends AbstractVerticle {

	/** The log. */
	static Logger log = LoggerFactory.getLogger(MosipCamelBridge.class);

	/**
	 * The main method.
	 *
	 * @param args            the arguments
	 */
	public static void main(String[] args) {
		MosipBridgeFactory.getEventBus();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.vertx.core.AbstractVerticle#start()
	 */
	@Override
	public void start() throws Exception {

		vertx.eventBus().consumer(MessageBusAddress.ERROR.getAddress(),
				message -> log.error("ERROR while doing operation >> " + message.body()));

		CamelContext camelContext = new DefaultCamelContext();

		camelContext.addRoutes(new MosipBridgeRoutes());
		camelContext.start();

		CamelBridge.create(vertx, new MosipBridgeMapping().getMapping(camelContext)).start();
	}

}
