package io.mosip.registration.processor.camel.bridge.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import io.vertx.core.json.JsonObject;

/**
 * The Class DemographicValidationProcessor.
 * 
 * @author Mukul Puspam
 * @since 0.0.1
 */
public class DemographicValidationProcessor implements Processor {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.camel.Processor#process(org.apache.camel.Exchange)
	 */
	@Override
	public void process(Exchange exchange) throws Exception {
		JsonObject json = (JsonObject) exchange.getIn().getBody();
		boolean isValid = json.getBoolean("validDemographic");

		System.out.println("DemographicValidationProcessor: " + isValid);
		exchange.getIn().setHeader("hasValidDemographic", isValid);

	}
}
