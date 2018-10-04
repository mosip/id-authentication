package io.mosip.registration.processor.camel.bridge.processor;
/**
 * @author Mukul Puspam
 */
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import io.vertx.core.json.JsonObject;

/**
 * The Class BatchRequestProcessor.
 */
public class BatchRequestProcessor implements Processor {

	/* (non-Javadoc)
	 * @see org.apache.camel.Processor#process(org.apache.camel.Exchange)
	 */
	@Override
	public void process(Exchange exchange) throws Exception {

		JsonObject json = (JsonObject) exchange.getIn().getBody();
		String eid = (String) json.getValue("eid");

		System.out.println("BatchRequestProcessor: " + eid);
		if (eid.length() > 3) {
			exchange.getIn().setHeader("isValidEid", Boolean.TRUE);
		} else {
			exchange.getIn().setHeader("isValidEid", Boolean.FALSE);
		}

	}

}
