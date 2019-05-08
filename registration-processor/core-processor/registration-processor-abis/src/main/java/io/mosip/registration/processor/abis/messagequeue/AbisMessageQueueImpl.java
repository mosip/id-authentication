package io.mosip.registration.processor.abis.messagequeue;

import java.io.IOException;

import javax.jms.Message;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.activemq.command.ActiveMQBytesMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.processor.abis.exception.QueueConnectionNotFound;
import io.mosip.registration.processor.abis.service.AbisService;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.core.packet.dto.abis.AbisIdentifyRequestDto;
import io.mosip.registration.processor.core.packet.dto.abis.AbisIdentifyResponseDto;
import io.mosip.registration.processor.core.packet.dto.abis.AbisInsertRequestDto;
import io.mosip.registration.processor.core.packet.dto.abis.AbisInsertResponseDto;
import io.mosip.registration.processor.core.queue.factory.MosipQueue;
import io.mosip.registration.processor.core.queue.factory.QueueListener;
import io.mosip.registration.processor.core.spi.queue.MosipQueueConnectionFactory;
import io.mosip.registration.processor.core.spi.queue.MosipQueueManager;
import io.mosip.registration.processor.core.util.JsonUtil;
import io.vertx.core.json.JsonObject;

/**
 * The AbisMessageQueueImpl class
 *
 */
@Component
public class AbisMessageQueueImpl {
	@Value("${registration.processor.queue.username}")
	private String username;

	@Value("${registration.processor.queue.password}")
	private String password;

	@Value("${registration.processor.queue.url}")
	private String url;

	@Value("${registration.processor.queue.typeOfQueue}")
	private String typeOfQueue;

	@Value("${registration.processor.abis.inbound.queue1}")
	private String abis1;
	
	@Value("${registration.processor.abis.inbound.queue2}")
	private String abis2;
	
	@Value("${registration.processor.abis.inbound.queue3}")
	private String abis3;
	
	@Value("${registration.processor.abis.outbound.queue1}")
	private String abismiddlewareaddress1;
	
	@Value("${registration.processor.abis.outbound.queue2}")
	private String abismiddlewareaddress2;
	
	@Value("${registration.processor.abis.outbound.queue3}")
	private String abismiddlewareaddress3;
	
	@Autowired
	AbisService abisService;
	
	/** The mosip queue manager. */
	@Autowired
	private MosipQueueManager<MosipQueue, byte[]> mosipQueueManager;

	/** The mosip connection factory. */
	@Autowired
	private MosipQueueConnectionFactory<MosipQueue> mosipConnectionFactory;
	
	private MosipQueue queue;
	
	private static final String ABIS_INSERT = "mosip.abis.insert";
	
	private static final String ABIS_IDENTIFY = "mosip.abis.identify";
	
	private static final String ID = "id";
	
	AbisInsertRequestDto abisInsertRequestDto;
	
	AbisIdentifyRequestDto identifyRequestDto;
	
	/** The reg proc logger. */
	private static Logger regProcLogger = RegProcessorLogger.getLogger(AbisMessageQueueImpl.class);
	
	boolean isConnection = false;
	
	private MosipQueue getQueueConnection() {
		return mosipConnectionFactory.createConnection(typeOfQueue, username, password, url);
	}
	
	public void runAbisQueue() {

		queue = getQueueConnection();
		if (queue != null) {
			QueueListener listener1 = new QueueListener() {
				@Override
				public void setListener(Message message) {
					consumeLogic(message,abismiddlewareaddress1);
				}
			};
			
			QueueListener listener2 = new QueueListener() {
				@Override
				public void setListener(Message message) {
					consumeLogic(message,abismiddlewareaddress2);
				}
			};
			
			QueueListener listener3 = new QueueListener() {
				@Override
				public void setListener(Message message) {
					consumeLogic(message,abismiddlewareaddress3);
				}
			};
		
			mosipQueueManager.consume(queue, abis1, listener1);
			mosipQueueManager.consume(queue, abis2, listener2);
			mosipQueueManager.consume(queue, abis3, listener3);
			isConnection = true;
		} else {
			throw new QueueConnectionNotFound(PlatformErrorMessages.RPR_PRT_QUEUE_CONNECTION_NULL.getMessage());
		}
	}

	public boolean consumeLogic(Message message, String abismiddlewareaddress) {
		boolean isrequestAddedtoQueue = false;
		String response=null;			
		
		String request = new String(((ActiveMQBytesMessage) message).getContent().data);
		
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
				"","---received---"+request);
		try {
			JsonObject object=JsonUtil.objectMapperReadValue(request, JsonObject.class);
			ObjectMapper obj = new ObjectMapper(); 
			
			if(object.getString(ID).matches(ABIS_INSERT)) {
				abisInsertRequestDto = JsonUtil.objectMapperReadValue(request, AbisInsertRequestDto.class);
				AbisInsertResponseDto abisInsertResponseDto = abisService.insert(abisInsertRequestDto);
				response=obj.writeValueAsString(abisInsertResponseDto);
			}
			
			else if(object.getString(ID).matches(ABIS_IDENTIFY)) {
				identifyRequestDto = JsonUtil.objectMapperReadValue(request, AbisIdentifyRequestDto.class);
				AbisIdentifyResponseDto identifyResponseDto = abisService.performDedupe(identifyRequestDto);
				response=obj.writeValueAsString(identifyResponseDto);
			}
			
			else {
				object.put("respoQueueConnectionNotFoundnse", "invalid request");
				response=obj.writeValueAsString(object);
			}
			
			regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					"","---response---"+response);

			isrequestAddedtoQueue = mosipQueueManager.send(queue,  response.getBytes("UTF-8"),
						abismiddlewareaddress);
		} catch (Exception e) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					e.getMessage(),e.getStackTrace().toString());
		}
		
			
		return isrequestAddedtoQueue;
	}

}