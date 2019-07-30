package io.mosip.registration.processor.print.stage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.jms.Message;

import org.apache.activemq.command.ActiveMQBytesMessage;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.idvalidator.spi.UinValidator;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.pdfgenerator.exception.PDFGeneratorException;
import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.abstractverticle.MosipEventBus;
import io.mosip.registration.processor.core.abstractverticle.MosipRouter;
import io.mosip.registration.processor.core.abstractverticle.MosipVerticleAPIManager;
import io.mosip.registration.processor.core.code.ApiName;
import io.mosip.registration.processor.core.code.EventId;
import io.mosip.registration.processor.core.code.EventName;
import io.mosip.registration.processor.core.code.EventType;
import io.mosip.registration.processor.core.code.RegistrationTransactionStatusCode;
import io.mosip.registration.processor.core.code.RegistrationTransactionTypeCode;
import io.mosip.registration.processor.core.constant.IdType;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.constant.RegistrationType;
import io.mosip.registration.processor.core.exception.TemplateProcessingFailureException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.core.queue.factory.MosipQueue;
import io.mosip.registration.processor.core.queue.factory.QueueListener;
import io.mosip.registration.processor.core.queue.impl.exception.ConnectionUnavailableException;
import io.mosip.registration.processor.core.spi.print.service.PrintService;
import io.mosip.registration.processor.core.spi.queue.MosipQueueConnectionFactory;
import io.mosip.registration.processor.core.spi.queue.MosipQueueManager;
import io.mosip.registration.processor.core.util.JsonUtil;
import io.mosip.registration.processor.packet.storage.utils.Utilities;
import io.mosip.registration.processor.print.exception.PrintGlobalExceptionHandler;
import io.mosip.registration.processor.print.exception.QueueConnectionNotFound;
import io.mosip.registration.processor.print.service.dto.PrintQueueDTO;
import io.mosip.registration.processor.print.service.impl.PrintPostServiceImpl;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;
import io.mosip.registration.processor.status.code.RegistrationStatusCode;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.service.RegistrationStatusService;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

/**
 * The Class PrintStage.
 * 
 * @author M1048358 Alok
 * @author Ranjitha Siddegowda
 */
@RefreshScope
@Service
public class PrintStage extends MosipVerticleAPIManager {

	/** The Constant FILE_SEPARATOR. */
	public static final String FILE_SEPARATOR = File.separator;

	/** The Constant USER. */
	private static final String USER = "MOSIP_SYSTEM";

	/** The Constant UIN_CARD_PDF. */
	private static final String UIN_CARD_PDF = "uinPdf";

	/** The Constant UIN_TEXT_FILE. */
	private static final String UIN_TEXT_FILE = "textFile";

	/** The reg proc logger. */
	private static Logger regProcLogger = RegProcessorLogger.getLogger(PrintStage.class);

	/** The cluster manager url. */
	@Value("${vertx.cluster.configuration}")
	private String clusterManagerUrl;

	/** The core audit request builder. */
	@Autowired
	private AuditLogRequestBuilder auditLogRequestBuilder;

	/** The global exception handler. */
	@Autowired
	public PrintGlobalExceptionHandler globalExceptionHandler;

	@Autowired
	public Utilities utilities;

	/** The port. */
	@Value("${server.port}")
	private String port;

	/** The mosip event bus. */
	private MosipEventBus mosipEventBus;

	/** The registration status service. */
	@Autowired
	RegistrationStatusService<String, InternalRegistrationStatusDto, RegistrationStatusDto> registrationStatusService;

	/** The mosip queue manager. */
	@Autowired
	private MosipQueueManager<MosipQueue, byte[]> mosipQueueManager;

	/** The mosip connection factory. */
	@Autowired
	private MosipQueueConnectionFactory<MosipQueue> mosipConnectionFactory;

	/** The print service. */
	@Autowired
	private PrintService<Map<String, byte[]>> printService;

	/** The print post service. */
	@Autowired
	private PrintPostServiceImpl printPostService;

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
	/** The address. */
	@Value("${registration.processor.queue.printpostaladdress}")
	private String printPostalAddress;

	@Value("${server.servlet.path}")
	private String contextPath;

	@Autowired
	private UinValidator<String> uinValidatorImpl;

	/** Mosip router for APIs */
	@Autowired
	MosipRouter router;

	boolean isConnection = false;

	private static final String SUCCESS = "Success";

	private static final String RESEND = "Resend";

	private static final String CLASSNAME = "PrintStage";

	private static final String SEPERATOR = "::";

	private MosipQueue queue;

	/**
	 * Deploy verticle.
	 */
	public void deployVerticle() {

		mosipEventBus = this.getEventBus(this, clusterManagerUrl, 50);
		this.consume(mosipEventBus, MessageBusAddress.PRINTING_BUS);

		queue = getQueueConnection();
		if (queue != null) {

			QueueListener listener = new QueueListener() {
				@Override
				public void setListener(Message message) {
					consumerListener(message);
				}
			};

			mosipQueueManager.consume(queue, printPostalAddress, listener);

		} else {
			throw new QueueConnectionNotFound(PlatformErrorMessages.RPR_PRT_QUEUE_CONNECTION_NULL.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.processor.core.spi.eventbus.EventBusManager#process(
	 * java.lang.Object)
	 */
	@Override
	public MessageDTO process(MessageDTO object) {
		object.setMessageBusAddress(MessageBusAddress.PRINTING_BUS);
		object.setInternalError(Boolean.FALSE);
		String description = null;
		boolean isTransactionSuccessful = false;

		String regId = object.getRid();
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
				regId, "PrintStage::process()::entry");
		try {
			InternalRegistrationStatusDto registrationStatusDto = registrationStatusService
					.getRegistrationStatus(regId);
			registrationStatusDto
					.setLatestTransactionTypeCode(RegistrationTransactionTypeCode.PRINT_SERVICE.toString());
			registrationStatusDto.setRegistrationStageName(this.getClass().getSimpleName());
			Map<String, byte[]> documentBytesMap = printService.getDocuments(IdType.RID, regId);
			boolean isAddedToQueue = sendToQueue(queue, documentBytesMap, 0, regId);

			if (isAddedToQueue) {
				object.setIsValid(Boolean.TRUE);
				isTransactionSuccessful = true;
				description = "Pdf added to the mosip queue for printing";
				registrationStatusDto.setStatusComment(description);
				registrationStatusDto
						.setLatestTransactionStatusCode(RegistrationTransactionStatusCode.PROCESSED.toString());
				registrationStatusDto
						.setLatestTransactionTypeCode(RegistrationTransactionTypeCode.PRINT_SERVICE.toString());

			} else {
				object.setIsValid(Boolean.FALSE);
				isTransactionSuccessful = false;
				description = "Pdf was not added to queue due to queue failure";
				registrationStatusDto.setStatusComment(description);
				registrationStatusDto
						.setLatestTransactionStatusCode(RegistrationTransactionStatusCode.FAILED.toString());
				registrationStatusDto
						.setLatestTransactionTypeCode(RegistrationTransactionTypeCode.PRINT_SERVICE.toString());

			}
			regProcLogger.info(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					regId, description);
			registrationStatusDto.setUpdatedBy(USER);
			registrationStatusService.updateRegistrationStatus(registrationStatusDto);
			printPostService.generatePrintandPostal(regId, queue, mosipQueueManager);

		} catch (PDFGeneratorException e) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					regId, PlatformErrorMessages.RPR_PRT_PDF_GENERATION_FAILED.name() + e.getMessage()
							+ ExceptionUtils.getStackTrace(e));
			description = CLASSNAME + SEPERATOR + "Pdf Generation failed for " + regId + SEPERATOR + e.getMessage();
			object.setInternalError(Boolean.TRUE);
		} catch (TemplateProcessingFailureException e) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					regId, PlatformErrorMessages.RPR_TEM_PROCESSING_FAILURE.name() + e.getMessage()
							+ ExceptionUtils.getStackTrace(e));
			description = CLASSNAME + SEPERATOR + "Template processing is failed for " + regId + SEPERATOR
					+ e.getMessage();
			object.setInternalError(Boolean.TRUE);
		} catch (QueueConnectionNotFound e) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					regId, PlatformErrorMessages.RPR_PRT_QUEUE_CONNECTION_NULL.name() + e.getMessage()
							+ ExceptionUtils.getStackTrace(e));
			description = CLASSNAME + SEPERATOR + "Queue Connection not found for " + regId + SEPERATOR
					+ e.getMessage();
			object.setInternalError(Boolean.TRUE);
		} catch (ConnectionUnavailableException e) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					regId, PlatformErrorMessages.RPR_MQI_UNABLE_TO_SEND_TO_QUEUE.name() + e.getMessage()
							+ ExceptionUtils.getStackTrace(e));
			description = CLASSNAME + SEPERATOR + "Queue Connection unavailable for " + regId + SEPERATOR
					+ e.getMessage();
			object.setInternalError(Boolean.TRUE);
		} catch (Exception e) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					regId, PlatformErrorMessages.RPR_PRT_PDF_GENERATION_FAILED.name() + e.getMessage()
							+ ExceptionUtils.getStackTrace(e));
			description = CLASSNAME + SEPERATOR + "Internal error occurred while processing registration id " + regId
					+ SEPERATOR + e.getMessage();
			object.setInternalError(Boolean.TRUE);
		} finally {
			String eventId = "";
			String eventName = "";
			String eventType = "";
			eventId = isTransactionSuccessful ? EventId.RPR_402.toString() : EventId.RPR_405.toString();
			eventName = eventId.equalsIgnoreCase(EventId.RPR_402.toString()) ? EventName.UPDATE.toString()
					: EventName.EXCEPTION.toString();
			eventType = eventId.equalsIgnoreCase(EventId.RPR_402.toString()) ? EventType.BUSINESS.toString()
					: EventType.SYSTEM.toString();

			auditLogRequestBuilder.createAuditRequestBuilder(description, eventId, eventName, eventType, regId,
					ApiName.AUDIT);
		}

		return object;
	}

	/**
	 * Send to queue.
	 *
	 * @param queue
	 *            the queue
	 * @param documentBytesMap
	 *            the document bytes map
	 * @param count
	 *            the count
	 * @param uin
	 *            the uin
	 * @return true, if successful
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private boolean sendToQueue(MosipQueue queue, Map<String, byte[]> documentBytesMap, int count, String regId)
			throws IOException {
		boolean isAddedToQueue = false;
		try {
			PrintQueueDTO queueDto = new PrintQueueDTO();
			queueDto.setPdfBytes(documentBytesMap.get(UIN_CARD_PDF));
			queueDto.setTextBytes(documentBytesMap.get(UIN_TEXT_FILE));
			queueDto.setRegId(regId);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject(queueDto);
			oos.flush();
			byte[] printQueueBytes = bos.toByteArray();
			isAddedToQueue = mosipQueueManager.send(queue, printQueueBytes, address);

		} catch (ConnectionUnavailableException e) {
			if (count < 5) {
				sendToQueue(queue, documentBytesMap, count + 1, regId);
			} else {
				regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),
						LoggerFileConstant.REGISTRATIONID.toString(), "",
						PlatformErrorMessages.RPR_MQI_UNABLE_TO_SEND_TO_QUEUE.name() + e.getMessage()
								+ ExceptionUtils.getStackTrace(e));
				throw new ConnectionUnavailableException(
						PlatformErrorMessages.RPR_MQI_UNABLE_TO_SEND_TO_QUEUE.getCode());
			}
		}

		return isAddedToQueue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.vertx.core.AbstractVerticle#start()
	 */
	@Override
	public void start() {
		router.setRoute(this.postUrl(vertx, MessageBusAddress.PRINTING_BUS, null));
		this.routes(router);
		this.createServer(router.getRouter(), Integer.parseInt(port));
	}

	/**
	 * contains all the routes in the stage.
	 *
	 * @param router
	 *            the router
	 */
	private void routes(MosipRouter router) {
		router.post(contextPath + "/resend");
		router.handler(this::reSendPrintPdf, this::failure);

	}

	/**
	 * This is for failure handler
	 * 
	 * @param routingContext
	 */
	private void failure(RoutingContext routingContext) {
		this.setResponse(routingContext, globalExceptionHandler.handler(routingContext.failure()));
	}

	/**
	 * Re send print pdf.
	 *
	 * @param ctx
	 *            the ctx
	 */
	public void reSendPrintPdf(RoutingContext ctx) {
		boolean isValidUin = false;
		JsonObject object = ctx.getBodyAsJson();
		MessageDTO messageDTO = new MessageDTO();
		try {
			regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					object.getString("regId"), "PrintStage::reSendPrintPdf()::entry");
			messageDTO.setRid(object.getString("regId"));
			String uin = object.getString("uin");
			String status = object.getString("status");
			isValidUin = uinValidatorImpl.validateId(uin);
			if (isValidUin && status.equalsIgnoreCase(RESEND)) {
				MessageDTO responseMessageDto = resendQueueResponse(messageDTO, status);
				if (!responseMessageDto.getIsValid()) {
					this.setResponse(ctx, RegistrationStatusCode.PROCESSED);
				} else {
					this.setResponse(ctx, "Caught internal error in messageDto");
				}
			} else {
				this.setResponse(ctx, "Invalid request");
			}

		} catch (Exception e) {
			this.setResponse(ctx, "Invalid request");
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					"", PlatformErrorMessages.RPR_BDD_UNKNOWN_EXCEPTION.name() + e.getMessage()
							+ ExceptionUtils.getStackTrace(e));
		}
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
				object.getString("regId"), "PrintStage::reSendPrintPdf()::exit");
	}

	public void consumerListener(Message message) {
		String description = null;
		String registrationId = null;
		boolean isTransactionSuccessful = false;
		try {

			String response = new String(((ActiveMQBytesMessage) message).getContent().data);
			JSONObject jsonObject = JsonUtil.objectMapperReadValue(response, JSONObject.class);
			String status = JsonUtil.getJSONValue(jsonObject, "Status");
			registrationId = JsonUtil.getJSONValue(jsonObject, "RegId");
			if (registrationId != null) {
				regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(),
						LoggerFileConstant.REGISTRATIONID.toString(), registrationId,
						"PrintStage::consumerListener()::entry");
				InternalRegistrationStatusDto registrationStatusDto = registrationStatusService
						.getRegistrationStatus(registrationId);
				if (status.equalsIgnoreCase(SUCCESS)) {
					isTransactionSuccessful = true;
					description = "Print and Post Completed for the regId : " + registrationId;
					registrationStatusDto.setStatusComment(description);
					registrationStatusDto
							.setLatestTransactionStatusCode(RegistrationTransactionStatusCode.PROCESSED.toString());
					registrationStatusDto.setLatestTransactionTypeCode(
							RegistrationTransactionTypeCode.PRINT_POSTAL_SERVICE.toString());
					registrationStatusDto.setUpdatedBy(USER);
					registrationStatusService.updateRegistrationStatus(registrationStatusDto);
				} else if (status.equalsIgnoreCase(RESEND)) {
					MessageDTO messageDTO = new MessageDTO();
					messageDTO.setReg_type(RegistrationType.valueOf(registrationStatusDto.getRegistrationType()));
					messageDTO.setRid(registrationId);
					description = "Re-Send uin card with regId " + registrationId + " for printing";
					registrationStatusDto.setStatusComment(description);
					registrationStatusDto.setLatestTransactionStatusCode(RESEND);
					registrationStatusDto.setLatestTransactionTypeCode(
							RegistrationTransactionTypeCode.PRINT_POSTAL_SERVICE.toString());
					registrationStatusDto.setUpdatedBy(USER);
					registrationStatusService.updateRegistrationStatus(registrationStatusDto);
					this.send(mosipEventBus, MessageBusAddress.PRINTING_BUS, messageDTO);
				}

				regProcLogger.info(LoggerFileConstant.SESSIONID.toString(),
						LoggerFileConstant.REGISTRATIONID.toString(), registrationId, description);
				regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(),
						LoggerFileConstant.REGISTRATIONID.toString(), registrationId,
						"PrintStage::consumerListener()::exit");
			} else {
				regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),
						LoggerFileConstant.REGISTRATIONID.toString(), registrationId,
						PlatformErrorMessages.RPR_PRT_PRINT_POST_ACK_FAILED.name());
			}

		} catch (IOException e) {
			isTransactionSuccessful = false;
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId, PlatformErrorMessages.RPR_PRT_PRINT_POST_ACK_FAILED.name() + e.getMessage()
							+ ExceptionUtils.getStackTrace(e));
			description = CLASSNAME + SEPERATOR + "Internal error occurred while processing registration id "
					+ registrationId + SEPERATOR + e.getMessage();
		} finally {
			String eventId = "";
			String eventName = "";
			String eventType = "";
			eventId = isTransactionSuccessful ? EventId.RPR_402.toString() : EventId.RPR_405.toString();
			eventName = eventId.equalsIgnoreCase(EventId.RPR_402.toString()) ? EventName.UPDATE.toString()
					: EventName.EXCEPTION.toString();
			eventType = eventId.equalsIgnoreCase(EventId.RPR_402.toString()) ? EventType.BUSINESS.toString()
					: EventType.SYSTEM.toString();
			auditLogRequestBuilder.createAuditRequestBuilder(description, eventId, eventName, eventType, registrationId,
					ApiName.AUDIT);
		}

	}

	private MosipQueue getQueueConnection() {
		return mosipConnectionFactory.createConnection(typeOfQueue, username, password, url);
	}

	@SuppressWarnings("unchecked")
	private MessageDTO resendQueueResponse(MessageDTO messageDto, String status) {
		JSONObject response = new JSONObject();

		try {
			response.put("RegId", messageDto.getRid());
			response.put("Status", status);
			mosipQueueManager.send(queue, response.toString().getBytes("UTF-8"), printPostalAddress);
			messageDto.setIsValid(Boolean.FALSE);
		} catch (UnsupportedEncodingException e) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					messageDto.getRid(), PlatformErrorMessages.RPR_CMB_UNSUPPORTED_ENCODING.name() + e.getMessage()
							+ ExceptionUtils.getStackTrace(e));
		}
		return messageDto;
	}

}
