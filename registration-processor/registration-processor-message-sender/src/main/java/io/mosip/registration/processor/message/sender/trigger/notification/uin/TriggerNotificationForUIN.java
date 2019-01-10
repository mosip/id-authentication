package io.mosip.registration.processor.message.sender.trigger.notification.uin;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import io.mosip.registration.processor.core.constant.IdType;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.notification.template.generator.dto.ResponseDto;
import io.mosip.registration.processor.core.notification.template.generator.dto.SmsResponseDto;
import io.mosip.registration.processor.core.spi.message.sender.MessageNotificationService;
import io.mosip.registration.processor.message.sender.exception.ConfigurationNotFoundException;
import io.mosip.registration.processor.message.sender.exception.EmailIdNotFoundException;
import io.mosip.registration.processor.message.sender.exception.PhoneNumberNotFoundException;
import io.mosip.registration.processor.message.sender.exception.TemplateGenerationFailedException;

/**
 * The Class TriggerNotificationForUIN.
 */
@RefreshScope
@Component
public class TriggerNotificationForUIN {

	/** The notification types. */
	@Value("${registration.processor.notification.type}")
	private String notificationTypes;

	/** The notification emails. */
	@Value("${registration.processor.notification.emails}")
	private String notificationEmails;

	/** The notification email subject. */
	@Value("${registration.processor.notification.subject}")
	private String notificationEmailSubject;

	/** The service. */
	@Autowired
	private MessageNotificationService<SmsResponseDto, ResponseDto, MultipartFile[]> service;

	/** The Constant SMS_TEMPLATE_CODE. */
	private static final String SMS_TEMPLATE_CODE = "SMS_TEMP_FOR_UIN_GEN";
	
	/** The Constant EMAIL_TEMPLATE_CODE. */
	private static final String EMAIL_TEMPLATE_CODE = "EMAIL_TEMP_FOR_UIN_GEN";
	
	/** The Constant SMS_TYPE. */
	private static final String SMS_TYPE = "SMS";
	
	/** The Constant EMAIL_TYPE. */
	private static final String EMAIL_TYPE = "EMAIL";

	/** The Constant LOGGER. */
	private static final Logger LOG = LoggerFactory.getLogger(TriggerNotificationForUIN.class);

	/**
	 * Trigger notification.
	 *
	 * @param uin the uin
	 * @throws ApisResourceAccessException the apis resource access exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void triggerNotification(String uin) throws ApisResourceAccessException, IOException {

		Map<String, Object> attributes = new HashMap<>();
		String[] ccEMailList = null;
		try {
			if (notificationTypes.isEmpty()) {
				throw new ConfigurationNotFoundException(
						PlatformErrorMessages.RPR_TEM_CONFIGURATION_NOT_FOUND.getCode());
			}
			String[] allNotificationTypes = notificationTypes.split("\\|");

			if (notificationEmails != null && notificationEmails.length() > 0) {
				ccEMailList = notificationEmails.split("\\|");
			}

			for (String notificationType : allNotificationTypes) {

				if (notificationType.equalsIgnoreCase(SMS_TYPE)) {
					service.sendSmsNotification(SMS_TEMPLATE_CODE, uin, IdType.UIN, attributes);
				} else if (notificationType.equalsIgnoreCase(EMAIL_TYPE)) {
					service.sendEmailNotification(EMAIL_TEMPLATE_CODE, uin, IdType.UIN, attributes, ccEMailList,
							notificationEmailSubject, null);
				}
			}

		} catch (EmailIdNotFoundException | PhoneNumberNotFoundException | TemplateGenerationFailedException  e) {
			LOG.error("Notification trigger failed");
			throw new TemplateGenerationFailedException(PlatformErrorMessages.RPR_TEM_PROCESSING_FAILURE.getCode());
		}

	}

}
