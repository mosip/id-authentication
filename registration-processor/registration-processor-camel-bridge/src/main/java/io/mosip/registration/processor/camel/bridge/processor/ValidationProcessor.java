package io.mosip.registration.processor.camel.bridge.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import io.mosip.registration.processor.camel.bridge.statuscode.MessageEnum;
import io.vertx.core.json.JsonObject;

/**
 * The Class StructureValidationProcessor.
 * 
 * @author Mukul Puspam
 * @author Pranav Kumar
 * @since 0.0.1
 */
public class ValidationProcessor implements Processor {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.camel.Processor#process(org.apache.camel.Exchange)
	 */
	@Override
	public void process(Exchange exchange) throws Exception {
		JsonObject json = (JsonObject) exchange.getIn().getBody();

		boolean isValid = json.getBoolean(MessageEnum.IS_VALID.getParameter());
		boolean internalError = json.getBoolean(MessageEnum.INTERNAL_ERROR.getParameter());
		exchange.getIn().setHeader(MessageEnum.IS_VALID.getParameter(), isValid);
		exchange.getIn().setHeader(MessageEnum.INTERNAL_ERROR.getParameter(), internalError);
	}

}
