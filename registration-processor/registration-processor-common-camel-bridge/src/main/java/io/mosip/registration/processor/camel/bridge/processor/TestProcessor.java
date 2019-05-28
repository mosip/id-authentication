package io.mosip.registration.processor.camel.bridge.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import io.mosip.registration.processor.core.auth.dto.AuthRequestDTO;

public class TestProcessor implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {
		System.out.println("+++++++++++inside processor+++++++++++");
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
	}

}
