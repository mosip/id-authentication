/**
 * 
 */
package io.mosip.registration.processor.print.stage;

import java.io.IOException;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.apache.activemq.command.ActiveMQBytesMessage;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.abstractverticle.MosipEventBus;
import io.mosip.registration.processor.core.abstractverticle.MosipVerticleAPIManager;
import io.mosip.registration.processor.core.code.ApiName;
import io.mosip.registration.processor.core.code.EventId;
import io.mosip.registration.processor.core.code.EventName;
import io.mosip.registration.processor.core.code.EventType;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.core.queue.factory.MosipQueue;
import io.mosip.registration.processor.core.queue.factory.QueueListener;
import io.mosip.registration.processor.core.spi.queue.MosipQueueConnectionFactory;
import io.mosip.registration.processor.core.spi.queue.MosipQueueManager;
import io.mosip.registration.processor.core.util.JsonUtil;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;
import io.mosip.registration.processor.status.code.RegistrationStatusCode;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.service.RegistrationStatusService;
import io.vertx.core.Vertx;

/**
 * @author Ranjitha Siddegowda
 *
 */
public class ConsumerStage extends MosipVerticleAPIManager {

	/** The mosip event bus. */
	public static MosipEventBus mosipEventBus;

	/** The cluster manager url. */
	@Value("${vertx.cluster.configuration}")
	private String clusterManagerUrl;

	/** The mosip queue manager. */
	@Autowired
	private MosipQueueManager<MosipQueue, byte[]> mosipQueueManager;

	/** The mosip connection factory. */
	@Autowired
	private MosipQueueConnectionFactory<MosipQueue> mosipConnectionFactory;

	/** The username. */
	@Value("${registration.processor.queue.username}")
	private String username;

	/** The password. */
	@Value("${registration.processor.queue.password}")
	private String password;

	/** The url. */
	@Value("${registration.processor.queue.url}")
	private String url;

	/** The type of queue. */
	@Value("${registration.processor.queue.typeOfQueue}")
	private String typeOfQueue;

	/** The address. */
	@Value("${registration.processor.queue.address}")
	private String address;

	/** The print & postal service provider address. */
	private String printPostalAddress = "postal-service";

	/** The registration status service. */
	@Autowired
	RegistrationStatusService<String, InternalRegistrationStatusDto, RegistrationStatusDto> registrationStatusService;

	private String registrationId;

	/** The is transactional. */
	private boolean isTransactionSuccessful = false;

	/** The core audit request builder. */
	@Autowired
	private AuditLogRequestBuilder auditLogRequestBuilder;

	String description = null;

	/** The Constant USER. */
	private static final String USER = "MOSIP_SYSTEM";

	private static final String SUCCESS = "Success";

	private static final String RESEND = "Resend";

	InternalRegistrationStatusDto registrationStatusDto = new InternalRegistrationStatusDto();

	private MessageDTO stageObject;
	
	boolean isConnection = false;


	private static Logger regProcLogger = RegProcessorLogger.getLogger(PrintStage.class);

	public MessageDTO process(MessageDTO object) {
		this.registrationId =  object.getRid();
		this.stageObject = object;
		try {
			registrationStatusDto = registrationStatusService.getRegistrationStatus(registrationId);

			MosipQueue queue = mosipConnectionFactory.createConnection(typeOfQueue, username, password, url);
			
			if (!isConnection) {

				QueueListener listener = new QueueListener() {
					@Override
					public void setListener(Message message) {
						sendMessage(message);
					}
				};

				mosipQueueManager.consume(queue, printPostalAddress, listener);
				isConnection = true;
			}

		} catch (Exception e) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId, PlatformErrorMessages.RPR_PRT_PRINT_POST_ACK_FAILED.name() + e.getMessage()
							+ ExceptionUtils.getStackTrace(e));
		}
		return stageObject;
	}

	public void sendMessage(Message message) {
		try {
			String response = new String(((ActiveMQBytesMessage) message).getContent().data);

			JSONObject jsonObject = JsonUtil.objectMapperReadValue(response, JSONObject.class);
			String status = JsonUtil.getJSONValue(jsonObject, "Status");
			regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(),
					LoggerFileConstant.REGISTRATIONID.toString(), registrationId,
					"ConsumerStage::process()::exit");
			if (status.equals(SUCCESS)) {
				description = "Print and Post Completed for the regId : " + registrationId;
				registrationStatusDto.setStatusCode(RegistrationStatusCode.PRINT_AND_POST_COMPLETED.toString());
				registrationStatusDto.setStatusComment(description);
				registrationStatusDto.setUpdatedBy(USER);
				registrationStatusService.updateRegistrationStatus(registrationStatusDto);
			} else if (status.equals(RESEND)) {
				description = "Re-Send uin card with regId " + registrationId + " for printing";
				registrationStatusDto.setStatusCode(RegistrationStatusCode.RESEND_UIN_CARD_FOR_PRINTING.toString());
				registrationStatusDto.setStatusComment(description);
				registrationStatusDto.setUpdatedBy(USER);
				registrationStatusService.updateRegistrationStatus(registrationStatusDto);
				this.send(mosipEventBus, MessageBusAddress.PRINTING_BUS_RESEND, this.stageObject);
			}
			regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(),
					LoggerFileConstant.REGISTRATIONID.toString(), registrationId,
					"ConsumerStage::process()::exit");
			regProcLogger.info(LoggerFileConstant.SESSIONID.toString(),
					LoggerFileConstant.REGISTRATIONID.toString(), registrationId, description);
		} catch (IOException e) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId, PlatformErrorMessages.RPR_PRT_PRINT_POST_ACK_FAILED.name() + e.getMessage()
							+ ExceptionUtils.getStackTrace(e));
		} finally {
			String eventId = "";
			String eventName = "";
			String eventType = "";
			description = isTransactionSuccessful ? "Acknowledgement successfully consumed from the mosip queue"
					: "Resend the packet to printing stage";
			eventId = isTransactionSuccessful ? EventId.RPR_402.toString() : EventId.RPR_405.toString();
			eventName = eventId.equalsIgnoreCase(EventId.RPR_402.toString()) ? EventName.UPDATE.toString()
					: EventName.EXCEPTION.toString();
			eventType = eventId.equalsIgnoreCase(EventId.RPR_402.toString()) ? EventType.BUSINESS.toString()
					: EventType.SYSTEM.toString();

			auditLogRequestBuilder.createAuditRequestBuilder(description, eventId, eventName, eventType, registrationId,
					ApiName.AUDIT);
		}

	}
}
