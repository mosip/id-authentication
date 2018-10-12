package io.mosip.registration.processor.camel.bridge.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import io.vertx.core.json.JsonObject;

/**
 * The Class RetryProcessor.
 * 
 * @author Mukul Puspam
 * @since 0.0.1
 */
public class RetryProcessor implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {
		JsonObject json = (JsonObject) exchange.getIn().getBody();
		String status = json.getString("status");
		exchange.getIn().setHeader("packetStatus", status);
	}

}
