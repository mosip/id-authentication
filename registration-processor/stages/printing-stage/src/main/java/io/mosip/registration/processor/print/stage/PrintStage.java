package io.mosip.registration.processor.print.stage;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.pdfgenerator.exception.PDFGeneratorException;
import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.abstractverticle.MosipEventBus;
import io.mosip.registration.processor.core.abstractverticle.MosipVerticleAPIManager;
import io.mosip.registration.processor.core.code.EventId;
import io.mosip.registration.processor.core.code.EventName;
import io.mosip.registration.processor.core.code.EventType;
import io.mosip.registration.processor.core.constant.IdType;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.core.queue.factory.MosipQueue;
import io.mosip.registration.processor.core.queue.impl.exception.ConnectionUnavailableException;
import io.mosip.registration.processor.core.spi.print.service.PrintService;
import io.mosip.registration.processor.core.spi.queue.MosipQueueConnectionFactory;
import io.mosip.registration.processor.core.spi.queue.MosipQueueManager;
import io.mosip.registration.processor.message.sender.exception.TemplateProcessingFailureException;
import io.mosip.registration.processor.print.exception.PrintGlobalExceptionHandler;
import io.mosip.registration.processor.print.exception.QueueConnectionNotFound;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;
import io.mosip.registration.processor.status.code.RegistrationStatusCode;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.service.RegistrationStatusService;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

/**
 * The Class PrintStage.
 * 
 * @author M1048358 Alok
 */
@RefreshScope
@Service
public class PrintStage extends MosipVerticleAPIManager {

	/** The Constant FILE_SEPARATOR. */
	public static final String FILE_SEPARATOR = "\\";

	/** The Constant USER. */
	private static final String USER = "MOSIP_SYSTEM";

	/** The reg proc logger. */
	private static Logger regProcLogger = RegProcessorLogger.getLogger(PrintStage.class);

	/** The cluster manager url. */
	@Value("${vertx.ignite.configuration}")
	private String clusterManagerUrl;

	/** The primary language. */
	@Value("${primary.language}")
	private String langCode;

	/** The core audit request builder. */
	@Autowired
	private AuditLogRequestBuilder auditLogRequestBuilder;

	/** The global exception handler. */
	@Autowired
	public PrintGlobalExceptionHandler globalExceptionHandler;

	/** The port. */
	@Value("${server.port}")
	private String port;

	/** The mosip event bus. */
	private MosipEventBus mosipEventBus;

	/** The is transactional. */
	private boolean isTransactionSuccessful = false;

	@Autowired
	RegistrationStatusService<String, InternalRegistrationStatusDto, RegistrationStatusDto> registrationStatusService;

	@Autowired
	private MosipQueueManager<MosipQueue, byte[]> mosipQueueManager;

	@Autowired
	private MosipQueueConnectionFactory<MosipQueue> mosipConnectionFactory;

	@Autowired
	private PrintService<byte[]> printService;

	@Value("${registration.processor.queue.username}")
	private String username;

	@Value("${registration.processor.queue.password}")
	private String password;

	@Value("${registration.processor.queue.url}")
	private String url;

	@Value("${registration.processor.queue.typeOfQueue}")
	private String typeOfQueue;

	@Value("${registration.processor.queue.address}")
	private String address;

	/**
	 * Deploy verticle.
	 */
	public void deployVerticle() {
		mosipEventBus = this.getEventBus(this, clusterManagerUrl);
		this.consume(mosipEventBus, MessageBusAddress.PRINTING_BUS);
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
		String regId = object.getRid();

		try {
			InternalRegistrationStatusDto registrationStatusDto = registrationStatusService
					.getRegistrationStatus(regId);

			byte[] pdfBytes = printService.getPdf(IdType.RID, regId);

			MosipQueue queue = mosipConnectionFactory.createConnection(typeOfQueue, username, password, url);
			if (queue == null) {
				regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),
						LoggerFileConstant.REGISTRATIONID.toString(), regId,
						PlatformErrorMessages.RPR_PRT_QUEUE_CONNECTION_NULL.name());
				throw new QueueConnectionNotFound(PlatformErrorMessages.RPR_PRT_QUEUE_CONNECTION_NULL.getCode());
			}
			
			boolean isPdfAddedtoQueue = mosipQueueManager.send(queue, pdfBytes, address);

			if (isPdfAddedtoQueue) {
				object.setIsValid(Boolean.TRUE);
				isTransactionSuccessful = true;
				description = "Pdf added to the mosip queue for printing";
				registrationStatusDto.setStatusCode(RegistrationStatusCode.PACKET_SENT_FOR_PRINTING.toString());
				registrationStatusDto.setStatusComment(description);
			} else {
				object.setIsValid(Boolean.FALSE);
				isTransactionSuccessful = false;
				description = "Pdf was not added to queue due to queue failure";
				registrationStatusDto.setStatusCode(RegistrationStatusCode.UNABLE_TO_SENT_FOR_PRINTING.toString());
				registrationStatusDto.setStatusComment(description);
			}

			registrationStatusDto.setUpdatedBy(USER);
			registrationStatusService.updateRegistrationStatus(registrationStatusDto);

		} catch (PDFGeneratorException e) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					regId, PlatformErrorMessages.RPR_PRT_PDF_GENERATION_FAILED.name() + e.getMessage()
							+ ExceptionUtils.getStackTrace(e));
			description = "Pdf Generation failed for : " + regId;
			object.setInternalError(Boolean.TRUE);
		} catch (TemplateProcessingFailureException e) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					regId, PlatformErrorMessages.RPR_TEM_PROCESSING_FAILURE.name() + e.getMessage()
							+ ExceptionUtils.getStackTrace(e));
			object.setInternalError(Boolean.TRUE);
		} catch (QueueConnectionNotFound e) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					regId, PlatformErrorMessages.RPR_PRT_QUEUE_CONNECTION_NULL.name() + e.getMessage()
							+ ExceptionUtils.getStackTrace(e));
			object.setInternalError(Boolean.TRUE);
		} catch(ConnectionUnavailableException e) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					regId, PlatformErrorMessages.RPR_MQI_UNABLE_TO_SEND_TO_QUEUE.name() + e.getMessage()
							+ ExceptionUtils.getStackTrace(e));
			object.setInternalError(Boolean.TRUE);
		} catch (Exception e) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					regId, PlatformErrorMessages.RPR_PRT_PDF_GENERATION_FAILED.name() + e.getMessage()
							+ ExceptionUtils.getStackTrace(e));
			description = "Internal error occured while processing registration  id : " + regId;
			object.setInternalError(Boolean.TRUE);
		} finally {
			String eventId = "";
			String eventName = "";
			String eventType = "";
			description = isTransactionSuccessful ? "Pdf generated and sent to mosip queue" : "Either pdf not generated or not sent to mosip queue";
			eventId = isTransactionSuccessful ? EventId.RPR_402.toString() : EventId.RPR_405.toString();
			eventName = eventId.equalsIgnoreCase(EventId.RPR_402.toString()) ? EventName.UPDATE.toString()
					: EventName.EXCEPTION.toString();
			eventType = eventId.equalsIgnoreCase(EventId.RPR_402.toString()) ? EventType.BUSINESS.toString()
					: EventType.SYSTEM.toString();

			auditLogRequestBuilder.createAuditRequestBuilder(description, eventId, eventName, eventType,regId);
		}

		return object;
	}

	/**
	 * Send message.
	 *
	 * @param messageDTO
	 *            the message DTO
	 */
	public void sendMessage(MessageDTO messageDTO) {
		this.send(this.mosipEventBus, MessageBusAddress.PRINTING_BUS, messageDTO);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.vertx.core.AbstractVerticle#start()
	 */
	@Override
	public void start() {
		Router router = this.postUrl(vertx);
		this.routes(router);
		this.createServer(router, Integer.parseInt(port));
	}

	/**
	 * contains all the routes in the stage.
	 *
	 * @param router
	 *            the router
	 */
	private void routes(Router router) {

		router.post("/v0.1/registration-processor/print-stage/resend").handler(ctx -> {
			reSendPrintPdf(ctx);
		}).failureHandler(failureHandler -> {
			this.setResponse(failureHandler, globalExceptionHandler.handler(failureHandler.failure()));
		});

		router.get("/print-stage/health").handler(ctx -> {
			this.setResponse(ctx, "Server is up and running");
		});
	}

	/**
	 * Re send print pdf.
	 *
	 * @param ctx
	 *            the ctx
	 */
	public void reSendPrintPdf(RoutingContext ctx) {
		JsonObject object = ctx.getBodyAsJson();
		MessageDTO messageDTO = new MessageDTO();
		messageDTO.setRid(object.getString("regId"));
		MessageDTO responceMessageDto = this.process(messageDTO);
		if (responceMessageDto.getIsValid()) {
			this.setResponse(ctx, RegistrationStatusCode.DOCUMENT_RESENT_TO_CAMEL_QUEUE);
		} else {
			this.setResponse(ctx, "Cought internal error in messageDto");
		}

	}

}
