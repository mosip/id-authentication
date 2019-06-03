package io.mosip.registration.processor.camel.bridge.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.beans.factory.annotation.Autowired;

import io.mosip.registration.processor.rest.client.utils.RestApiClient;

/**
 * @author Pranav Kumar
 * 
 * @since 0.12.0
 *
 */
public class TokenGenerationProcessor implements Processor {

	@Autowired
	RestApiClient restApiClient;

	@Override
	public void process(Exchange exchange) throws Exception {
		String token = restApiClient.getToken();
		exchange.getIn().setHeader("Cookie", token);
	}

}
