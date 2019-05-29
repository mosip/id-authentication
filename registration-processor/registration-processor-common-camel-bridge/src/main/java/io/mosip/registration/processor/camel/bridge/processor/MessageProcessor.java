package io.mosip.registration.processor.camel.bridge.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.constant.RegistrationType;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.vertx.core.json.JsonObject;
/**
 * custom Processor class
 *
 */
public class MessageProcessor implements Processor {
	private static Logger regProcLogger = RegProcessorLogger.getLogger(MessageProcessor.class);
	/* (non-Javadoc)
	 * @see org.apache.camel.Processor#process(org.apache.camel.Exchange)
	 */
	@Override
	public void process(Exchange exchange) throws Exception {
		String jsonMessage = (String) exchange.getIn().getBody();
		regProcLogger.info(LoggerFileConstant.SESSIONID.toString(),
                LoggerFileConstant.APPLICATIONID.toString(), "Message recieved is", jsonMessage);
		
		JsonObject object=new JsonObject(jsonMessage);
		
		
		MessageDTO messageDto = new MessageDTO();
		if(object.containsKey("internalError")) {
		messageDto.setInternalError(object.getBoolean("internalError"));
		}
		if(object.containsKey("isValid")) {
		messageDto.setIsValid(object.getBoolean("isValid"));
		}
		if(object.containsKey("reg_type")) {
		messageDto.setReg_type(RegistrationType.valueOf(object.getString("reg_type")));
		}
		if(object.containsKey("retryCount")) {
		messageDto.setRetryCount(object.getInteger("retryCount"));
		}
		if(object.containsKey("rid")) {
		messageDto.setRid(object.getString("rid"));
		}
		if(object.containsKey("messageBusAddress")) {
			JsonObject address=object.getJsonObject("messageBusAddress");
			if(address.containsKey("address")) {
				messageDto.setMessageBusAddress(new MessageBusAddress(address.getString("address")));
			}
		}
		JsonObject jsonObject = JsonObject.mapFrom(messageDto);
		exchange.getIn().setBody(jsonObject);
		
	}
}
