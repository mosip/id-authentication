package io.mosip.registration.processor.camel.bridge.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.vertx.core.json.JsonObject;

public class TestProcessor implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {
		String jsonMessage = (String) exchange.getIn().getBody();
		System.out.println("+++++++++++Message recieved is+++++++++++ "+jsonMessage);
		ObjectMapper objectMapper = new ObjectMapper();
		MessageDTO messageDto = objectMapper.readValue(jsonMessage, MessageDTO.class);
		JsonObject jsonObject = JsonObject.mapFrom(messageDto);
		exchange.getIn().setBody(jsonObject);
		
	}

}
