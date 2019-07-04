package io.mosip.registration.processor.message.sender.stage;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.fsadapter.exception.FSAdapterException;
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
import io.mosip.registration.processor.core.code.RegistrationTransactionStatusCode;
import io.mosip.registration.processor.core.code.RegistrationTransactionTypeCode;
import io.mosip.registration.processor.core.constant.IdType;
import io.mosip.registration.processor.core.constant.JsonConstant;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.constant.PacketFiles;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.http.ResponseWrapper;
import io.mosip.registration.processor.core.logger.LogDescription;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.core.notification.template.generator.dto.ResponseDto;
import io.mosip.registration.processor.core.notification.template.generator.dto.SmsResponseDto;
import io.mosip.registration.processor.core.notification.template.generator.dto.TemplateResponseDto;
import io.mosip.registration.processor.core.packet.dto.FieldValue;
import io.mosip.registration.processor.core.packet.dto.PacketMetaInfo;
import io.mosip.registration.processor.core.spi.filesystem.manager.PacketManager;
import io.mosip.registration.processor.core.spi.message.sender.MessageNotificationService;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.core.util.IdentityIteratorUtil;
import io.mosip.registration.processor.core.util.JsonUtil;
import io.mosip.registration.processor.message.sender.constants.MessageSenderConstant;
import io.mosip.registration.processor.message.sender.dto.MessageSenderDto;
import io.mosip.registration.processor.message.sender.exception.ConfigurationNotFoundException;
import io.mosip.registration.processor.message.sender.exception.EmailIdNotFoundException;
import io.mosip.registration.processor.message.sender.exception.PhoneNumberNotFoundException;
import io.mosip.registration.processor.message.sender.exception.TemplateGenerationFailedException;
import io.mosip.registration.processor.message.sender.exception.TemplateNotFoundException;
import io.mosip.registration.processor.message.sender.util.StatusNotificationTypeMapUtil;
import io.mosip.registration.processor.message.sender.utility.MessageSenderStatusMessage;
import io.mosip.registration.processor.message.sender.utility.NotificationTemplateCode;
import io.mosip.registration.processor.message.sender.utility.NotificationTemplateType;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;
import io.mosip.registration.processor.status.code.RegistrationStatusCode;
import io.mosip.registration.processor.status.code.RegistrationType;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.dto.SyncTypeDto;
import io.mosip.registration.processor.status.dto.TransactionDto;
import io.mosip.registration.processor.status.service.RegistrationStatusService;
import io.mosip.registration.processor.status.service.TransactionService;

/**
 * The Class MessageSenderStage.
 * 
 * @author M1048358 Alok
 * @since 1.0.0
 */
@RefreshScope
@Service
public class MessageSenderStage extends MosipVerticleAPIManager {

	/** The reg proc logger. */
	private static Logger regProcLogger = RegProcessorLogger.getLogger(MessageSenderStage.class);

	/** The registration status service. */
	@Autowired
	private RegistrationStatusService<String, InternalRegistrationStatusDto, RegistrationStatusDto> registrationStatusService;

	/** The adapter. */
	@Autowired
	private PacketManager adapter;

	/** The cluster manager url. */
	@Value("${vertx.cluster.configuration}")
	private String clusterManagerUrl;

	/** The core audit request builder. */
	@Autowired
	private AuditLogRequestBuilder auditLogRequestBuilder;

	/** The notification emails. */
	@Value("${registration.processor.notification.emails}")
	private String notificationEmails;

	/** The uin generated subject. */
	@Value("${registration.processor.uin.generated.subject}")
	private String uinGeneratedSubject;

	@Value("${registration.processor.uin.activated.subject}")
	private String uinActivateSubject;

	@Value("${registration.processor.uin.deactivated.subject}")
	private String uinDeactivateSubject;

	/** The duplicate uin subject. */
	@Value("${registration.processor.duplicate.uin.subject}")
	private String duplicateUinSubject;

	/** The reregister subject. */
	@Value("${registration.processor.reregister.subject}")
	private String reregisterSubject;

	@Value("${mosip.registration.processor.notification.types}")
	private String notificationTypes;

	@Value("${registration.processor.updated.subject}")
	private String uinUpdatedSubject;

	/** The rest client service. */
	@Autowired
	private RegistrationProcessorRestClientService<Object> restClientService;

	/** The service. */
	@Autowired
	private MessageNotificationService<SmsResponseDto, ResponseDto, MultipartFile[]> service;

	@Autowired
	private TransactionService<TransactionDto> transactionStatusService;

	/** The Constant SMS_TYPE. */
	private static final String SMS_TYPE = "SMS";

	/** The Constant EMAIL_TYPE. */
	private static final String EMAIL_TYPE = "EMAIL";

	/** Mosip router for APIs */
	@Autowired
	MosipRouter router;

	/** The port. */
	@Value("${server.port}")
	private String port;

	/**
	 * Deploy verticle.
	 */
	public void deployVerticle() {
		MosipEventBus mosipEventBus = this.getEventBus(this, clusterManagerUrl);
		this.consume(mosipEventBus, MessageBusAddress.MESSAGE_SENDER_BUS);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see io.vertx.core.AbstractVerticle#start()
	 */
	@Override
	public void start() {
		router.setRoute(this.postUrl(vertx, MessageBusAddress.MESSAGE_SENDER_BUS, null));
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
		object.setMessageBusAddress(MessageBusAddress.MESSAGE_SENDER_BUS);
		boolean isTransactionSuccessful = false;
		LogDescription description=new LogDescription();
		String status;
		MessageSenderDto messageSenderDto=new MessageSenderDto();
		String id = object.getRid();
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(), id,
				"MessageSenderStage::process()::entry");
		InternalRegistrationStatusDto registrationStatusDto = registrationStatusService.getRegistrationStatus(id);
		status = registrationStatusDto.getLatestTransactionTypeCode() + "_"
				+ registrationStatusDto.getLatestTransactionStatusCode();

		registrationStatusDto.setLatestTransactionTypeCode(RegistrationTransactionTypeCode.NOTIFICATION.toString());
		registrationStatusDto.setRegistrationStageName(this.getClass().getSimpleName());

		try {
			InputStream packetMetaInfoStream = adapter.getFile(id, PacketFiles.PACKET_META_INFO.name());
			PacketMetaInfo packetMetaInfo = (PacketMetaInfo) JsonUtil.inputStreamtoJavaObject(packetMetaInfoStream,
					PacketMetaInfo.class);
			List<FieldValue> metadataList = packetMetaInfo.getIdentity().getMetaData();
			IdentityIteratorUtil identityIteratorUtil = new IdentityIteratorUtil();
			String regType = identityIteratorUtil.getFieldValue(metadataList, JsonConstant.REGISTRATIONTYPE);

			NotificationTemplateType type = null;
			StatusNotificationTypeMapUtil map = new StatusNotificationTypeMapUtil();

			if (registrationStatusDto.getStatusCode().equals(RegistrationStatusCode.PROCESSED.toString())) {
				if (registrationStatusDto.getRegistrationType().equalsIgnoreCase(SyncTypeDto.LOST.getValue()))
					type = NotificationTemplateType.LOST_UIN;
				if (registrationStatusDto.getRegistrationType().equalsIgnoreCase(SyncTypeDto.NEW.getValue()))
					type = NotificationTemplateType.UIN_CREATED;
				if (registrationStatusDto.getRegistrationType().equalsIgnoreCase(SyncTypeDto.UPDATE.getValue()))
					type = NotificationTemplateType.UIN_UPDATE;
			} else {
				type = map.getTemplateType(status);
			}
			if (type != null) {
				setTemplateAndSubject(type, regType, messageSenderDto);
			}

			Map<String, Object> attributes = new HashMap<>();
			String[] ccEMailList = null;

			if (notificationTypes == null || notificationTypes.isEmpty()) {
				description.setMessage(MessageSenderConstant.MESSAGE_SENDER_FAILED + id + "::"
						+ PlatformErrorMessages.RPR_TEM_CONFIGURATION_NOT_FOUND.getCode());
				throw new ConfigurationNotFoundException(
						PlatformErrorMessages.RPR_TEM_CONFIGURATION_NOT_FOUND.getCode());
			}
			String[] allNotificationTypes = notificationTypes.split("\\|");

			if (notificationEmails != null && notificationEmails.length() > 0) {
				ccEMailList = notificationEmails.split("\\|");
			}

			sendNotification(id, attributes, ccEMailList, allNotificationTypes, regType, messageSenderDto);

			isTransactionSuccessful = true;
			description.setMessage(MessageSenderConstant.MESSAGE_SENDER_NOTIF_SUCC + id);

			regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					id, "MessageSenderStage::process()::exit");
			regProcLogger.info(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					id, description.getMessage());

			registrationStatusDto.setStatusComment(description.getMessage());
			registrationStatusDto.setLatestTransactionStatusCode(RegistrationTransactionStatusCode.SUCCESS.toString());

			TransactionDto transactionDto = new TransactionDto(UUID.randomUUID().toString(),
					registrationStatusDto.getRegistrationId(), registrationStatusDto.getLatestRegistrationTransactionId(),
					registrationStatusDto.getLatestTransactionTypeCode(), "updated registration status record",
					registrationStatusDto.getLatestTransactionStatusCode(), registrationStatusDto.getStatusComment());

			transactionDto.setReferenceId(registrationStatusDto.getRegistrationId());
			transactionDto.setReferenceIdType(MessageSenderConstant.REFERENCE_TYPE_ID);
			transactionStatusService.addRegistrationTransaction(transactionDto);

			object.setIsValid(Boolean.TRUE);

		} catch (EmailIdNotFoundException | PhoneNumberNotFoundException | TemplateGenerationFailedException
				| ConfigurationNotFoundException e) {
			object.setInternalError(Boolean.TRUE);
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					id, e.getMessage() + ExceptionUtils.getStackTrace(e));
			description.setMessage(MessageSenderConstant.MESAGE_SENDER_EPTN_MISSING + id + "::" + e.getMessage());
		} catch (TemplateNotFoundException tnf) {
			object.setInternalError(Boolean.TRUE);
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					id, tnf.getMessage() + ExceptionUtils.getStackTrace(tnf));
			description.setMessage(MessageSenderConstant.MESAGE_SENDER_TEMP_MISSING + id + "::" + tnf.getMessage());
		} catch (FSAdapterException e) {
			object.setInternalError(Boolean.TRUE);
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.UIN.toString(), id,
					PlatformErrorMessages.RPR_TEM_PACKET_STORE_NOT_ACCESSIBLE.getMessage() + e.getMessage());
			description.setMessage(MessageSenderConstant.MESAGE_SENDER_PACKET_STORE + id);
		} catch (Exception ex) {
			object.setInternalError(Boolean.TRUE);
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					id, ex.getMessage() + ExceptionUtils.getStackTrace(ex));
			description.setMessage(MessageSenderConstant.MESAGE_SENDER_INTERNAL_ERROR + id + "::" + ex.getMessage());
		} finally {
			String eventId = isTransactionSuccessful ? EventId.RPR_402.toString() : EventId.RPR_405.toString();
			String eventName = eventId.equalsIgnoreCase(EventId.RPR_402.toString()) ? EventName.UPDATE.toString()
					: EventName.EXCEPTION.toString();
			String eventType = eventId.equalsIgnoreCase(EventId.RPR_402.toString()) ? EventType.BUSINESS.toString()
					: EventType.SYSTEM.toString();

			auditLogRequestBuilder.createAuditRequestBuilder(description.getMessage(), eventId, eventName, eventType,
					id, ApiName.AUDIT);
		}

		return object;
	}

	/**
	 * Send notification.
	 *
	 * @param id
	 *            the id
	 * @param attributes
	 *            the attributes
	 * @param ccEMailList
	 *            the cc E mail list
	 * @param allNotificationTypes
	 *            the all notification types
	 * @param regType
	 * @param messageSenderDto
	 * @throws Exception
	 *             the exception
	 */
	private void sendNotification(String id, Map<String, Object> attributes, String[] ccEMailList,
			String[] allNotificationTypes, String regType, MessageSenderDto messageSenderDto) throws Exception {
		for (String notificationType : allNotificationTypes) {

			if (notificationType.equalsIgnoreCase(SMS_TYPE)
					&& isTemplateAvailable(messageSenderDto)) {

				service.sendSmsNotification(messageSenderDto.getSmsTemplateCode().name(), id,
						messageSenderDto.getIdType(), attributes, regType);
				regProcLogger.info(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.UIN.toString(), id,
						MessageSenderStatusMessage.SMS_NOTIFICATION_SUCCESS);

			} else if (notificationType.equalsIgnoreCase(EMAIL_TYPE)
					&& isTemplateAvailable(messageSenderDto)) {

				service.sendEmailNotification(messageSenderDto.getEmailTemplateCode().name(), id,
						messageSenderDto.getIdType(), attributes, ccEMailList, messageSenderDto.getSubject(), null,
						regType);
				regProcLogger.info(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.UIN.toString(), id,
						MessageSenderStatusMessage.EMAIL_NOTIFICATION_SUCCESS);

			} else {
				throw new TemplateNotFoundException(MessageSenderStatusMessage.TEMPLATE_NOT_FOUND);
			}
		}
	}

	/**
	 * Sets the template and subject.
	 *
	 * @param templatetype
	 *            the new template and subject
	 * @param regType
	 * @param messageSenderDto
	 */
	private void setTemplateAndSubject(NotificationTemplateType templatetype, String regType, MessageSenderDto messageSenderDto) {
		switch (templatetype) {
		case LOST_UIN:
			messageSenderDto.setSmsTemplateCode(NotificationTemplateCode.RPR_LOST_UIN_SMS);
			messageSenderDto.setEmailTemplateCode(NotificationTemplateCode.RPR_LOST_UIN_EMAIL);
			messageSenderDto.setIdType(IdType.UIN);
			messageSenderDto.setSubject(uinGeneratedSubject);
			break;
		case UIN_CREATED:
			messageSenderDto.setSmsTemplateCode(NotificationTemplateCode.RPR_UIN_GEN_SMS);
			messageSenderDto.setEmailTemplateCode(NotificationTemplateCode.RPR_UIN_GEN_EMAIL);
			messageSenderDto.setIdType(IdType.UIN);
			messageSenderDto.setSubject(uinGeneratedSubject);
			break;
		case UIN_UPDATE:
			if (regType.equalsIgnoreCase(RegistrationType.NEW.name())) {
				messageSenderDto.setSmsTemplateCode(NotificationTemplateCode.RPR_UIN_UPD_SMS);
				messageSenderDto.setEmailTemplateCode(NotificationTemplateCode.RPR_UIN_UPD_EMAIL);
				messageSenderDto.setIdType(IdType.UIN);
				messageSenderDto.setSubject(uinGeneratedSubject);
			} else if (regType.equalsIgnoreCase(RegistrationType.ACTIVATED.name())) {
				messageSenderDto.setSmsTemplateCode(NotificationTemplateCode.RPR_UIN_REAC_SMS);
				messageSenderDto.setEmailTemplateCode(NotificationTemplateCode.RPR_UIN_REAC_EMAIL);
				messageSenderDto.setIdType(IdType.UIN);
				messageSenderDto.setSubject(uinActivateSubject);
			} else if (regType.equalsIgnoreCase(RegistrationType.DEACTIVATED.name())) {
				messageSenderDto.setSmsTemplateCode(NotificationTemplateCode.RPR_UIN_DEAC_SMS);
				messageSenderDto.setEmailTemplateCode(NotificationTemplateCode.RPR_UIN_DEAC_EMAIL);
				messageSenderDto.setIdType(IdType.UIN);
				messageSenderDto.setSubject(uinDeactivateSubject);
			} else if (regType.equalsIgnoreCase(RegistrationType.UPDATE.name())) {
				messageSenderDto.setSmsTemplateCode(NotificationTemplateCode.RPR_UIN_UPD_SMS);
				messageSenderDto.setEmailTemplateCode(NotificationTemplateCode.RPR_UIN_UPD_EMAIL);
				messageSenderDto.setIdType(IdType.UIN);
				messageSenderDto.setSubject(uinUpdatedSubject);
			}
			break;
		case DUPLICATE_UIN:
			messageSenderDto.setSmsTemplateCode(NotificationTemplateCode.RPR_DUP_UIN_SMS);
			messageSenderDto.setEmailTemplateCode(NotificationTemplateCode.RPR_DUP_UIN_EMAIL);
			messageSenderDto.setIdType(IdType.RID);
			messageSenderDto.setSubject(duplicateUinSubject);
			break;
		case TECHNICAL_ISSUE:
			messageSenderDto.setSmsTemplateCode(NotificationTemplateCode.RPR_TEC_ISSUE_SMS);
			messageSenderDto.setEmailTemplateCode(NotificationTemplateCode.RPR_TEC_ISSUE_EMAIL);
			messageSenderDto.setIdType(IdType.RID);
			messageSenderDto.setSubject(reregisterSubject);
			break;
		default:
			break;
		}
	}

	/**
	 * Checks if is template available.
	 *
	 * @param messageSenderDto
	 *            the template code
	 * @return true, if is template available
	 * @throws ApisResourceAccessException
	 *             the apis resource access exception
	 * @throws JsonProcessingException
	 * @throws ParseException
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 */
	private boolean isTemplateAvailable(MessageSenderDto messageSenderDto) throws ApisResourceAccessException, IOException {

		List<String> pathSegments = new ArrayList<>();
		ResponseWrapper<?> responseWrapper;
		TemplateResponseDto templateResponseDto = null;
		ObjectMapper mapper = new ObjectMapper();
		responseWrapper = (ResponseWrapper<?>) restClientService.getApi(ApiName.TEMPLATES, pathSegments, "", "",
				ResponseWrapper.class);
		templateResponseDto = mapper.readValue(mapper.writeValueAsString(responseWrapper.getResponse()),
				TemplateResponseDto.class);

		if (responseWrapper.getErrors() == null) {
			templateResponseDto.getTemplates().forEach(dto -> {
				if (dto.getTemplateTypeCode().equalsIgnoreCase(messageSenderDto.getSmsTemplateCode().name())) {
					messageSenderDto.setTemplateAvailable(true);
				}
			});
		}
		return messageSenderDto.isTemplateAvailable();
	}

}
