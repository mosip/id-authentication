package io.mosip.registration.processor.camel.bridge.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.beans.factory.annotation.Autowired;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.rest.client.utils.RestApiClient;

/**
 * @author Pranav Kumar
 * 
 * @since 0.12.0
 *
 */
public class TokenGenerationProcessor implements Processor {
	/** The reg proc logger. */
	private static Logger regProcLogger = RegProcessorLogger.getLogger(TokenGenerationProcessor.class);

	@Autowired
	RestApiClient restApiClient;

	@Override
	public void process(Exchange exchange) throws Exception {
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(),
				"", "TokenGenerationProcessor::process()::entry");

		String token = restApiClient.getToken();
		exchange.getIn().setHeader("Cookie", token);
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(),
				"", "TokenGenerationProcessor::process()::exit");

	}

}
