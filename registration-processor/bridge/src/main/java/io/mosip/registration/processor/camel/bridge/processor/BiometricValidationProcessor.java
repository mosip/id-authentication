package io.mosip.registration.processor.camel.bridge.processor;
/**
 * @author Mukul Puspam
 */
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import io.vertx.core.json.JsonObject;

/**
 * The Class BiometricValidationProcessor.
 */
public class BiometricValidationProcessor implements Processor {

	/* (non-Javadoc)
	 * @see org.apache.camel.Processor#process(org.apache.camel.Exchange)
	 */
	@Override
	public void process(Exchange exchange) throws Exception {
		JsonObject json = (JsonObject) exchange.getIn().getBody();
		boolean isValid = json.getBoolean("validBiometric");

		System.out.println("BiometricValidationProcessor: " + isValid);
		exchange.getIn().setHeader("hasValidBiometric", isValid);
	}
}
