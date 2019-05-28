package io.mosip.registration.processor.abis.messagequeue;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.jms.Message;

import org.apache.activemq.command.ActiveMQBytesMessage;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.processor.abis.exception.QueueConnectionNotFound;
import io.mosip.registration.processor.abis.queue.dto.AbisQueueDetails;
import io.mosip.registration.processor.abis.service.AbisService;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.exception.RegistrationProcessorCheckedException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.core.packet.dto.abis.AbisIdentifyRequestDto;
import io.mosip.registration.processor.core.packet.dto.abis.AbisIdentifyResponseDto;
import io.mosip.registration.processor.core.packet.dto.abis.AbisInsertRequestDto;
import io.mosip.registration.processor.core.packet.dto.abis.AbisInsertResponseDto;
import io.mosip.registration.processor.core.queue.factory.MosipQueue;
import io.mosip.registration.processor.core.queue.factory.QueueListener;
import io.mosip.registration.processor.core.spi.queue.MosipQueueManager;
import io.mosip.registration.processor.core.util.JsonUtil;
import io.mosip.registration.processor.packet.storage.utils.Utilities;

/**
 * The AbisMessageQueueImpl class.
 * 
 * @author jyoti-prakash
 * @author Kiran Raj
 * @author Girish Yarru
 */
@Component
public class AbisMessageQueueImpl {

	/** The utilities. */
	@Autowired
	Utilities utilities;

	/** The abis service. */
	@Autowired
	AbisService abisService;

	/** The mosip queue manager. */
	@Autowired
	private MosipQueueManager<MosipQueue, byte[]> mosipQueueManager;

	/** The Constant ABIS_INSERT. */
	private static final String ABIS_INSERT = "mosip.abis.insert";

	/** The Constant ABIS_IDENTIFY. */
	private static final String ABIS_IDENTIFY = "mosip.abis.identify";

	/** The Constant ID. */
	private static final String ID = "id";

	/** The abis insert request dto. */
	AbisInsertRequestDto abisInsertRequestDto;

	/** The identify request dto. */
	AbisIdentifyRequestDto identifyRequestDto;

	/** The reg proc logger. */
	private static Logger regProcLogger = RegProcessorLogger.getLogger(AbisMessageQueueImpl.class);

	/** The is connection. */
	boolean isConnection = false;

	/**
	 * Run abis queue.
	 *
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws RegistrationProcessorCheckedException
	 */
	public void runAbisQueue() throws RegistrationProcessorCheckedException {
		List<AbisQueueDetails> abisQueueDetails = utilities.getAbisQueueDetails();
		if (abisQueueDetails != null && !abisQueueDetails.isEmpty()) {

			for (int i = 0; i < abisQueueDetails.size(); i++) {
				String outBoundAddress = abisQueueDetails.get(i).getOutboundQueueName();
				MosipQueue queue = abisQueueDetails.get(i).getMosipQueue();
				QueueListener listener = new QueueListener() {
					@Override
					public void setListener(Message message) {
						consumeLogic(message, outBoundAddress, queue);
					}
				};
				mosipQueueManager.consume(abisQueueDetails.get(i).getMosipQueue(),
						abisQueueDetails.get(i).getInboundQueueName(), listener);
			}

			isConnection = true;
		} else {
			throw new QueueConnectionNotFound(PlatformErrorMessages.RPR_PRT_QUEUE_CONNECTION_NULL.getMessage());
		}

	}

	/**
	 * Consume logic.
	 *
	 * @param message
	 *            the message
	 * @param abismiddlewareaddress
	 *            the abismiddlewareaddress
	 * @param queue
	 *            the queue
	 * @return true, if successful
	 */
	@SuppressWarnings("unchecked")
	public boolean consumeLogic(Message message, String abismiddlewareaddress, MosipQueue queue) {

		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(), "",
				"AbisMessageQueueImpl::consumeLogic()::Entry()");
		boolean isrequestAddedtoQueue = false;
		String response = null;

		String request = new String(((ActiveMQBytesMessage) message).getContent().data);

		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(), "",
				"---request received from abis middle ware ---" + request);
		try {
			JSONObject object = JsonUtil.objectMapperReadValue(request, JSONObject.class);
			ObjectMapper obj = new ObjectMapper();
			String id = (String) object.get(ID);
			if (id.matches(ABIS_INSERT)) {
				abisInsertRequestDto = JsonUtil.objectMapperReadValue(request, AbisInsertRequestDto.class);
				AbisInsertResponseDto abisInsertResponseDto = abisService.insert(abisInsertRequestDto);
				response = obj.writeValueAsString(abisInsertResponseDto);
				regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(),
						LoggerFileConstant.REGISTRATIONID.toString(), "",
						"---response sent to abis middle ware ---" + response);
			}

			else if (id.matches(ABIS_IDENTIFY)) {
				identifyRequestDto = JsonUtil.objectMapperReadValue(request, AbisIdentifyRequestDto.class);
				AbisIdentifyResponseDto identifyResponseDto = abisService.performDedupe(identifyRequestDto);
				response = obj.writeValueAsString(identifyResponseDto);
				regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(),
						LoggerFileConstant.REGISTRATIONID.toString(), "",
						"---response sent to abis middle ware ---" + response);
			}

			else {
				object.put("respoQueueConnectionNotFoundnse", "invalid request");
				response = obj.writeValueAsString(object);
				regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(),
						LoggerFileConstant.REGISTRATIONID.toString(), "", "---invalid request received ---" + response);
			}

			isrequestAddedtoQueue = mosipQueueManager.send(queue, response.getBytes("UTF-8"), abismiddlewareaddress);
		} catch (Exception e) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					e.getMessage(), Arrays.toString(e.getStackTrace()));
		}

		return isrequestAddedtoQueue;
	}

}