package io.mosip.registration.processor.message.sender.trigger.notification.uin;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.notification.template.generator.dto.ResponseDto;
import io.mosip.registration.processor.core.notification.template.generator.dto.SmsResponseDto;
import io.mosip.registration.processor.core.spi.message.sender.MessageNotificationService;
import io.mosip.registration.processor.message.sender.exception.ConfigurationNotFoundException;
import io.mosip.registration.processor.message.sender.exception.TemplateGenerationFailedException;

@RefreshScope
@Component
public class TriggerNotificationForUIN {

	@Value("${registration.processor.notification.type}")
	private String notificationTypes;

	@Value("${registration.processor.notification.emails}")
	private String notificationEmails;

	@Value("${registration.processor.notification.subject}")
	private String notificationEmailSubject;

	@Autowired
	private MessageNotificationService<SmsResponseDto, ResponseDto, MultipartFile[]> service;

	private static final String SMS_TEMPLATE_CODE = "SMS_TEMP_FOR_UIN_GEN";
	private static final String EMAIL_TEMPLATE_CODE = "EMAIL_TEMP_FOR_UIN_GEN";

	/** The Constant LOGGER. */
	private static final Logger LOG = LoggerFactory.getLogger(TriggerNotificationForUIN.class);

	public void triggerNotification(String uin) {

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

				if (notificationType.equalsIgnoreCase("SMS")) {
					service.sendSmsNotification(SMS_TEMPLATE_CODE, uin, "UIN", attributes);
				} else if (notificationType.equalsIgnoreCase("EMAIL")) {
					service.sendEmailNotification(EMAIL_TEMPLATE_CODE, uin, "UIN", attributes, ccEMailList,
							notificationEmailSubject, null);
				}
			}

		} catch (Exception e) {
			LOG.error("Notification trigger failed", e);
			throw new TemplateGenerationFailedException(PlatformErrorMessages.RPR_TEM_PROCESSING_FAILURE.getCode());
		}

	}

}
