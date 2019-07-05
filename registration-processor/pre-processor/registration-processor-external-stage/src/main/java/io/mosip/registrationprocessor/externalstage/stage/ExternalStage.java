package io.mosip.registrationprocessor.externalstage.stage;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.abstractverticle.MosipEventBus;
import io.mosip.registration.processor.core.abstractverticle.MosipRouter;
import io.mosip.registration.processor.core.abstractverticle.MosipVerticleAPIManager;
import io.mosip.registration.processor.core.code.ApiName;
import io.mosip.registration.processor.core.code.EventId;
import io.mosip.registration.processor.core.code.EventName;
import io.mosip.registration.processor.core.code.EventType;
import io.mosip.registration.processor.core.code.ModuleName;
import io.mosip.registration.processor.core.code.RegistrationExceptionTypeCode;
import io.mosip.registration.processor.core.code.RegistrationTransactionStatusCode;
import io.mosip.registration.processor.core.code.RegistrationTransactionTypeCode;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.exception.util.PlatformSuccessMessages;
import io.mosip.registration.processor.core.logger.LogDescription;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.core.util.RegistrationExceptionMapperUtil;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;
import io.mosip.registration.processor.status.code.RegistrationStatusCode;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.service.RegistrationStatusService;
import io.mosip.registrationprocessor.externalstage.entity.MessageRequestDTO;
import io.mosip.registrationprocessor.externalstage.utils.StatusMessage;

/**
 * External stage verticle class
 *
 */
@Service
public class ExternalStage extends MosipVerticleAPIManager {
	/** The reg proc logger. */
	private static Logger regProcLogger = RegProcessorLogger.getLogger(ExternalStage.class);
	/** request id */
	private static final String ID = "io.mosip.registrationprocessor";
	/** request version */
	private static final String VERSION = "1.0";
	/** mosipEventBus */
	private MosipEventBus mosipEventBus;
	/** vertx Cluster Manager Url. */
	@Value("${vertx.cluster.configuration}")
	private String clusterManagerUrl;

	/** server port number. */
	@Value("${server.port}")
	private String port;

	@Autowired
	private AuditLogRequestBuilder auditLogRequestBuilder;

	/** The registration status service. */
	@Autowired
	private RegistrationStatusService<String, InternalRegistrationStatusDto, RegistrationStatusDto> registrationStatusService;

	/** rest client to send requests. */
	@Autowired
	private RegistrationProcessorRestClientService<Object> registrationProcessorRestService;

	/** Mosip router for APIs */
	@Autowired
	MosipRouter router;

	/** The description. */
	@Autowired
	LogDescription description;

	/** The Constant USER. */
	private static final String USER = "MOSIP_SYSTEM";

	@Autowired
	RegistrationExceptionMapperUtil registrationStatusMapperUtil;

	/**
	 * method to deploy external stage verticle
	 */
	public void deployVerticle() {
		this.mosipEventBus = this.getEventBus(this, clusterManagerUrl, 50);
		this.consumeAndSend(mosipEventBus, MessageBusAddress.EXTERNAL_STAGE_BUS_IN,
				MessageBusAddress.EXTERNAL_STAGE_BUS_OUT);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see io.vertx.core.AbstractVerticle#start()
	 */
	@Override
	public void start() {

		router.setRoute(
				this.postUrl(vertx, MessageBusAddress.EXTERNAL_STAGE_BUS_IN, MessageBusAddress.EXTERNAL_STAGE_BUS_OUT));
		this.createServer(router.getRouter(), Integer.parseInt(port));
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

		boolean isTransactionSuccessful = false;
		String registrationId = object.getRid();
		object.setMessageBusAddress(MessageBusAddress.EXTERNAL_STAGE_BUS_IN);
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
				registrationId, "ExternalStage::process()::entry");
		InternalRegistrationStatusDto registrationStatusDto = registrationStatusService
				.getRegistrationStatus(registrationId);
		MessageRequestDTO requestdto = new MessageRequestDTO();
		requestdto.setId(ID);
		List<String> list = new ArrayList<String>();
		list.add(object.getRid());
		requestdto.setRequest(list);
		requestdto.setRequesttime(LocalDateTime.now().toString());
		requestdto.setVersion(VERSION);
		isTransactionSuccessful = false;
		try {
			registrationStatusDto
					.setLatestTransactionTypeCode(RegistrationTransactionTypeCode.EXTERNAL_INTEGRATION.toString());
			registrationStatusDto.setRegistrationStageName(this.getClass().getSimpleName());
			regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					"", "ExternalStage::process():: EIS service Api call started");
			Boolean temp = (Boolean) registrationProcessorRestService.postApi(ApiName.EISERVICE, "", "", requestdto,
					Boolean.class);

			regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					"",
					"ExternalStage::process():: EIS service Api call  ended with response data : " + temp.toString());
			if (temp) {
				registrationStatusDto
						.setLatestTransactionStatusCode(RegistrationTransactionStatusCode.SUCCESS.toString());
				registrationStatusDto.setStatusComment(StatusMessage.EXTERNAL_STAGE_SUCCESS);
				registrationStatusDto.setStatusCode(RegistrationStatusCode.PROCESSING.toString());
				object.setIsValid(true);
				object.setInternalError(false);
				isTransactionSuccessful = true;
				description.setMessage(
						PlatformSuccessMessages.RPR_EXTERNAL_STAGE_SUCCESS.getMessage() + " -- " + registrationId);
				description.setCode(PlatformSuccessMessages.RPR_EXTERNAL_STAGE_SUCCESS.getCode());

			} else {
				registrationStatusDto.setLatestTransactionStatusCode(registrationStatusMapperUtil
						.getStatusCode(RegistrationExceptionTypeCode.EXTERNAL_INTEGRATION_FAILED));
				registrationStatusDto.setStatusComment(StatusMessage.EXTERNAL_STAGE_FAILURE);
				registrationStatusDto.setStatusCode(RegistrationStatusCode.FAILED.toString());
				object.setIsValid(false);
				object.setInternalError(false);

				description
						.setMessage(PlatformErrorMessages.EXTERNAL_STAGE_FAILED.getMessage() + " -- " + registrationId);
				description.setCode(PlatformErrorMessages.EXTERNAL_STAGE_FAILED.getCode());
			}
			regProcLogger.info(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId, description.getMessage());
		} catch (ApisResourceAccessException e) {
			registrationStatusDto.setStatusComment(PlatformErrorMessages.RPR_SYS_API_RESOURCE_EXCEPTION.getMessage());
			registrationStatusDto.setStatusCode(RegistrationStatusCode.PROCESSING.toString());
			registrationStatusDto.setLatestTransactionStatusCode(registrationStatusMapperUtil
					.getStatusCode(RegistrationExceptionTypeCode.APIS_RESOURCE_ACCESS_EXCEPTION));
			description.setCode(PlatformErrorMessages.RPR_SYS_API_RESOURCE_EXCEPTION.getCode());
			description.setMessage(PlatformErrorMessages.RPR_SYS_API_RESOURCE_EXCEPTION.getMessage());
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), description.getCode(), registrationId,
					description.getMessage() + e.getMessage() + ExceptionUtils.getStackTrace(e));
			object.setInternalError(true);
			object.setIsValid(false);
		} finally {

			if (object.getInternalError()) {
				registrationStatusDto.setUpdatedBy(USER);
				int retryCount = registrationStatusDto.getRetryCount() != null
						? registrationStatusDto.getRetryCount() + 1
						: 1;

				registrationStatusDto.setRetryCount(retryCount);
			}
			registrationStatusService.updateRegistrationStatus(registrationStatusDto);
			if (isTransactionSuccessful)
				description.setMessage(PlatformSuccessMessages.RPR_PKR_PACKET_VALIDATE.getMessage());
			String eventId = isTransactionSuccessful ? EventId.RPR_402.toString() : EventId.RPR_405.toString();
			String eventName = isTransactionSuccessful ? EventName.UPDATE.toString() : EventName.EXCEPTION.toString();
			String eventType = isTransactionSuccessful ? EventType.BUSINESS.toString() : EventType.SYSTEM.toString();

			/** Module-Id can be Both Succes/Error code */
			String moduleId = isTransactionSuccessful ? PlatformSuccessMessages.RPR_EXTERNAL_STAGE_SUCCESS.getCode()
					: description.getCode();
			String moduleName = ModuleName.EXTERNAL.toString();
			auditLogRequestBuilder.createAuditRequestBuilder(description.getMessage(), eventId, eventName, eventType,
					moduleId, moduleName, registrationId);
		}

		regProcLogger.debug("", "", "sent to next stage --> ", object.toString());
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
				registrationId, "ExternalStage::process()::exit");
		return object;
	}

}
