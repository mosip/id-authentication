package io.mosip.registration.processor.camel.bridge.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import io.vertx.core.json.JsonObject;

/**
 * Processor for messages coming Batch Job
 *
 * @author Mukul Puspam
 * @since 0.0.1
 */
public class BatchRequestProcessor implements Processor {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.camel.Processor#process(org.apache.camel.Exchange)
	 */
	@Override
	public void process(Exchange exchange) throws Exception {

		JsonObject json = (JsonObject) exchange.getIn().getBody();

		String rid = (String) json.getValue("rid");

		if (rid.length() > 3) {
			exchange.getIn().setHeader("isValidEid", Boolean.TRUE);
		} else {
			exchange.getIn().setHeader("isValidEid", Boolean.FALSE);
		}

	}

}
