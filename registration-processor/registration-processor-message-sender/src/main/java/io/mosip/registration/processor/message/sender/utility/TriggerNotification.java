package io.mosip.registration.processor.message.sender.utility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.processor.core.code.ApiName;
import io.mosip.registration.processor.core.constant.IdType;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.dto.config.GlobalConfig;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.core.notification.template.generator.dto.ResponseDto;
import io.mosip.registration.processor.core.notification.template.generator.dto.SmsResponseDto;
import io.mosip.registration.processor.core.notification.template.generator.dto.TemplateResponseDto;
import io.mosip.registration.processor.core.spi.message.sender.MessageNotificationService;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.message.sender.exception.ConfigurationNotFoundException;
import io.mosip.registration.processor.message.sender.exception.EmailIdNotFoundException;
import io.mosip.registration.processor.message.sender.exception.PhoneNumberNotFoundException;
import io.mosip.registration.processor.message.sender.exception.TemplateGenerationFailedException;
import io.mosip.registration.processor.message.sender.exception.TemplateNotFoundException;
import io.mosip.registration.processor.message.sender.utility.MessageSenderUtil;

/**
 * The Class TriggerNotification.
 * 
 * @author M1049387
 * @author M1048358
 */
@RefreshScope
@Component
public class TriggerNotification {

	/** The reg proc logger. */
	private static Logger regProcLogger = RegProcessorLogger.getLogger(TriggerNotification.class);

	/** The notification emails. */
	@Value("${registration.processor.notification.emails}")
	private String notificationEmails;

	/** The uin generated subject. */
	@Value("${registration.processor.uin.generated.subject}")
	private String uinGeneratedSubject;

	/** The duplicate uin subject. */
	@Value("${registration.processor.duplicate.uin.subject}")
	private String duplicateUinSubject;

	/** The reregister subject. */
	@Value("${registration.processor.reregister.subject}")
	private String reregisterSubject;

	/** The Constant TEMPLATES. */
	private static final String TEMPLATES = "templates";

	/** The rest client service. */
	@Autowired
	private RegistrationProcessorRestClientService<Object> restClientService;

	/** The service. */
	@Autowired
	private MessageNotificationService<SmsResponseDto, ResponseDto, MultipartFile[]> service;

	/** The utility. */
	@Autowired
	private MessageSenderUtil utility;

	/** The Constant SMS_TYPE. */
	private static final String SMS_TYPE = "SMS";

	/** The Constant EMAIL_TYPE. */
	private static final String EMAIL_TYPE = "EMAIL";

	/** The is template available. */
	boolean isTemplateAvailable = false;

	/** The sms template code. */
	private NotificationTemplateCode smsTemplateCode = null;

	/** The email template code. */
	private NotificationTemplateCode emailTemplateCode = null;

	/** The subject. */
	private String subject = "";

	/** The id type. */
	private IdType idType = null;

	/**
	 * Trigger notification.
	 *
	 * @param id
	 *            the id
	 * @param type
	 *            the type
	 */
	public void triggerNotification(String id, NotificationTemplateType type) {

		setTemplateAndSubject(type);

		Map<String, Object> attributes = new HashMap<>();
		String[] ccEMailList = null;
		try {
			String notificationTypes = getNotificationType();
			if (notificationTypes.isEmpty()) {
				throw new ConfigurationNotFoundException(
						PlatformErrorMessages.RPR_TEM_CONFIGURATION_NOT_FOUND.getCode());
			}
			String[] allNotificationTypes = notificationTypes.split("\\|");

			if (notificationEmails != null && notificationEmails.length() > 0) {
				ccEMailList = notificationEmails.split("\\|");
			}

			for (String notificationType : allNotificationTypes) {

				if (notificationType.equalsIgnoreCase(SMS_TYPE) && isTemplateAvailable(smsTemplateCode.name())) {

					service.sendSmsNotification(smsTemplateCode.name(), id, idType, attributes);
					regProcLogger.info(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.UIN.toString(), id,
							MessageSenderStatusMessage.SMS_NOTIFICATION_SUCCESS);

				} else if (notificationType.equalsIgnoreCase(EMAIL_TYPE)
						&& isTemplateAvailable(emailTemplateCode.name())) {

					service.sendEmailNotification(emailTemplateCode.name(), id, idType, attributes, ccEMailList,
							subject, null);
					regProcLogger.info(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.UIN.toString(), id,
							MessageSenderStatusMessage.EMAIL_NOTIFICATION_SUCCESS);

				} else {
					throw new TemplateNotFoundException(MessageSenderStatusMessage.TEMPLATE_NOT_FOUND);
				}
			}

		} catch (EmailIdNotFoundException | PhoneNumberNotFoundException | TemplateGenerationFailedException e) {

			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.UIN.toString(), id,
					e.getMessage() + ExceptionUtils.getStackTrace(e));
			throw new TemplateGenerationFailedException(PlatformErrorMessages.RPR_TEM_PROCESSING_FAILURE.getCode());
		} catch (JsonParseException | JsonMappingException jp) {

			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.UIN.toString(), id,
					jp.getMessage() + ExceptionUtils.getStackTrace(jp));
		} catch (TemplateNotFoundException tnf) {

			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.UIN.toString(), id,
					tnf.getMessage() + ExceptionUtils.getStackTrace(tnf));
		} catch (Exception ex) {

			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.UIN.toString(), id,
					ex.getMessage() + ExceptionUtils.getStackTrace(ex));
		}

	}

	/**
	 * Sets the template and subject.
	 *
	 * @param templatetype
	 *            the new template and subject
	 */
	private void setTemplateAndSubject(NotificationTemplateType templatetype) {
		switch (templatetype) {
		case UIN_CREATED:
			smsTemplateCode = NotificationTemplateCode.RPR_UIN_GEN_SMS;
			emailTemplateCode = NotificationTemplateCode.RPR_UIN_GEN_EMAIL;
			idType = IdType.UIN;
			subject = uinGeneratedSubject;
			break;
		case UIN_UPDATE:
			smsTemplateCode = NotificationTemplateCode.RPR_UIN_UPD_SMS;
			emailTemplateCode = NotificationTemplateCode.RPR_UIN_UPD_EMAIL;
			idType = IdType.UIN;
			subject = uinGeneratedSubject;
			break;
		case DUPLICATE_UIN:
			smsTemplateCode = NotificationTemplateCode.RPR_DUP_UIN_SMS;
			emailTemplateCode = NotificationTemplateCode.RPR_DUP_UIN_EMAIL;
			idType = IdType.RID;
			subject = duplicateUinSubject;
			break;
		case TECHNICAL_ISSUE:
			smsTemplateCode = NotificationTemplateCode.RPR_TEC_ISSUE_SMS;
			emailTemplateCode = NotificationTemplateCode.RPR_TEC_ISSUE_EMAIL;
			idType = IdType.RID;
			subject = reregisterSubject;
			break;
		default:
			break;
		}
	}

	/**
	 * Gets the notification type.
	 *
	 * @return the notification type
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private String getNotificationType() throws IOException {
		String getIdentityJsonString = MessageSenderUtil.getJson(utility.getConfigServerFileStorageURL(),
				utility.getGetGlobalConfigJson());
		ObjectMapper mapIdentityJsonStringToObject = new ObjectMapper();
		GlobalConfig jsonObject = mapIdentityJsonStringToObject.readValue(getIdentityJsonString, GlobalConfig.class);
		return jsonObject.getNotificationtype();
	}

	/**
	 * Checks if is template available.
	 *
	 * @param templateCode
	 *            the template code
	 * @return true, if is template available
	 * @throws ApisResourceAccessException
	 *             the apis resource access exception
	 */
	private boolean isTemplateAvailable(String templateCode) throws ApisResourceAccessException {

		List<String> pathSegments = new ArrayList<>();
		pathSegments.add(TEMPLATES);
		TemplateResponseDto template = (TemplateResponseDto) restClientService.getApi(ApiName.MASTER, pathSegments, "",
				"", TemplateResponseDto.class);
		template.getTemplates().forEach(dto -> {
			if (dto.getTemplateTypeCode().equalsIgnoreCase(templateCode)) {
				isTemplateAvailable = true;
			}
		});

		return isTemplateAvailable;
	}

}
