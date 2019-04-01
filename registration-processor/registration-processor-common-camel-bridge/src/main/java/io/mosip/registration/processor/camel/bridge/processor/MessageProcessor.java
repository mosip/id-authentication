package io.mosip.registration.processor.camel.bridge.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
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
		JsonObject address=object.getJsonObject("messageBusAddress");
		
		MessageDTO messageDto = new MessageDTO();
		messageDto.setInternalError(object.getBoolean("internalError"));
		messageDto.setIsValid(object.getBoolean("isValid"));
		messageDto.setReg_type(object.getString("reg_type"));
		messageDto.setRetryCount(object.getInteger("retryCount"));
		messageDto.setRid(object.getString("rid"));
		messageDto.setMessageBusAddress(new MessageBusAddress(address.getString("address")));
		JsonObject jsonObject = JsonObject.mapFrom(messageDto);
		exchange.getIn().setBody(jsonObject);
		
	}
}
