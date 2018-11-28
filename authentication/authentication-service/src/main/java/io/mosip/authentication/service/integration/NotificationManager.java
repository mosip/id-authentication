package io.mosip.authentication.service.integration;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.constant.RestServicesConstants;
import io.mosip.authentication.core.dto.indauth.NotificationType;
import io.mosip.authentication.core.dto.indauth.SenderType;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.util.dto.RestRequestDTO;
import io.mosip.authentication.service.factory.RestRequestFactory;
import io.mosip.authentication.service.helper.RestHelper;
import io.mosip.authentication.service.integration.dto.SmsRequestDto;
import io.mosip.kernel.core.logger.spi.Logger;

/**
 * 
 * @author Dinesh Karuppiah.T
 */

@Component
public class NotificationManager {

	private static final String EMAIL = "email";

	private static final String SMS = "sms";

	private static final String NOTIFICATION_TYPE = "mosip.notification.type";

	/** ID Template manager */
	@Autowired
	private IdTemplateManager idTemplateManager;

	/** Environment */
	@Autowired
	private Environment environment;

	/** Rest Helper */
	@Autowired
	private RestHelper restHelper;

	/** Rest Request Factory */
	@Autowired
	private RestRequestFactory restRequestFactory;

	/** Logger to log the actions */
	private static Logger logger = IdaLogger.getLogger(NotificationManager.class);

	/** Property Name for Auth SMS Template */
	private static final String AUTH_SMS_TEMPLATE = "mosip.auth.sms.template";

	/** Property Name for OTP SMS Template */
	private static final String OTP_SMS_TEMPLATE = "mosip.otp.sms.template";

	/** Property Name for Auth Email Subject Template */
	private static final String AUTH_EMAIL_SUBJECT_TEMPLATE = "mosip.auth.mail.subject.template";

	/** Property Name for Auth Email Content Template */
	private static final String AUTH_EMAIL_CONTENT_TEMPLATE = "mosip.auth.mail.content.template";

	/** Property Name for OTP Subject Template */
	private static final String OTP_SUBJECT_TEMPLATE = "mosip.otp.mail.subject.template";

	/** Property Name for OTP Content Template */
	private static final String OTP_CONTENT_TEMPLATE = "mosip.otp.mail.content.template";

	/**
	 * Method to Send Notification to the Individual via SMS / E-Mail
	 * 
	 * @param notificationtype - specifies notification type
	 * @param values           - list of values to send notification
	 * @param emailId          - sender E-Mail ID
	 * @param phoneNumber      - sender Phone Number
	 * @param sender           - to specify the sender type
	 * @throws IdAuthenticationBusinessException
	 */
	public void sendNotification(Map<String, Object> values, String emailId, String phoneNumber, SenderType sender)
			throws IdAuthenticationBusinessException {
		String contentTemplate = null;
		String subjectTemplate = null;
		Set<NotificationType> notificationtype = new HashSet<>();
		String notificationtypeconfig = environment.getProperty(NOTIFICATION_TYPE);
		
		
		
		String notificationMobileNo = phoneNumber;
		//FIXME Taking from configuration for testing purpose.
		if(isNotNullorEmpty(phoneNumber)) {
			notificationMobileNo = environment.getProperty("test.notification.mobile.no");
		}

		if (isNotNullorEmpty(notificationtypeconfig)) {
			if (notificationtypeconfig.contains(",")) {
				String value[] = notificationtypeconfig.split(",");
				for (int i = 0; i < 2; i++) {
					String nvalue = "";
					nvalue = value[i];
					processNotification(emailId, notificationMobileNo, notificationtype, nvalue);
				}
			} else {
				processNotification(emailId, notificationMobileNo, notificationtype, notificationtypeconfig);
			}

		}

		if (notificationtype.contains(NotificationType.SMS)) {
			switch (sender) {
			case AUTH:
				contentTemplate = environment.getProperty(AUTH_SMS_TEMPLATE);
				break;
			case OTP:
				contentTemplate = environment.getProperty(OTP_SMS_TEMPLATE);
				break;
			}

			try {
				String smsTemplate = applyTemplate(values, contentTemplate);
				SmsRequestDto smsRequestDto = new SmsRequestDto();
				smsRequestDto.setMessage(smsTemplate);
				smsRequestDto.setNumber(notificationMobileNo);
				RestRequestDTO restRequestDTO = null;
				restRequestDTO = restRequestFactory.buildRequest(RestServicesConstants.SMS_NOTIFICATION_SERVICE,
						smsRequestDto, String.class);
				restHelper.requestAsync(restRequestDTO);
			} catch (IDDataValidationException e) {
				logger.error("NA", "Inside SMS Notification >>>>>", e.getErrorCode(), e.getErrorText());
				throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.NOTIFICATION_FAILED, e);
			}

		}
		if (notificationtype.contains(NotificationType.EMAIL)) {

			switch (sender) {
			case AUTH:
				subjectTemplate = environment.getProperty(AUTH_EMAIL_SUBJECT_TEMPLATE);
				contentTemplate = environment.getProperty(AUTH_EMAIL_CONTENT_TEMPLATE);
				break;
			case OTP:
				subjectTemplate = environment.getProperty(OTP_SUBJECT_TEMPLATE);
				contentTemplate = environment.getProperty(OTP_CONTENT_TEMPLATE);
				break;
			}

			try {
				String mailSubject = applyTemplate(values, subjectTemplate);
				String mailContent = applyTemplate(values, contentTemplate);
				RestRequestDTO restRequestDTO = null;
				MultiValueMap<String, String> mailRequestDto = new LinkedMultiValueMap<>();
				mailRequestDto.add("mailContent", mailContent);
				mailRequestDto.add("mailSubject", mailSubject);
				mailRequestDto.add("mailTo", emailId);
				restRequestDTO = restRequestFactory.buildRequest(RestServicesConstants.MAIL_NOTIFICATION_SERVICE,
						mailRequestDto, String.class);
				restHelper.requestAsync(restRequestDTO);
			} catch (IDDataValidationException e) {
				logger.error("NA", "Inside Mail Notification >>>>>", e.getErrorCode(), e.getErrorText());
				throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.NOTIFICATION_FAILED, e);
			}

		}

	}

	/**
	 * Reads notification type from property and set the notification type
	 * 
	 * @param emailId                - email id of Individual
	 * @param phoneNumber            - Phone Number of Individual
	 * @param notificationtype       - Notification type
	 * @param notificationtypeconfig - Notification type from the configuration
	 */

	private void processNotification(String emailId, String phoneNumber, Set<NotificationType> notificationtype,
			String notificationtypeconfig) {
		String type = notificationtypeconfig.toLowerCase();
		if (type.equals(SMS)) {
			if (isNotNullorEmpty(phoneNumber)) {
				notificationtype.add(NotificationType.SMS);
			} else {
				if (isNotNullorEmpty(emailId)) {
					notificationtype.add(NotificationType.EMAIL);
				}
			}
		}

		if (type.equals(EMAIL)) {
			if (isNotNullorEmpty(emailId)) {
				notificationtype.add(NotificationType.EMAIL);
			} else {
				if (isNotNullorEmpty(phoneNumber)) {
					notificationtype.add(NotificationType.SMS);
				}
			}
		}
	}

	/**
	 * To apply Templates for Email or SMS Notifications
	 * 
	 * @param values       - content for Template
	 * @param templateName - Template name to fetch
	 * @return
	 * @throws IdAuthenticationBusinessException
	 */
	private String applyTemplate(Map<String, Object> values, String templateName)
			throws IdAuthenticationBusinessException {
		try {
			Objects.requireNonNull(templateName);
			return idTemplateManager.applyTemplate(templateName, values);
		} catch (IOException e) {
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.NOTIFICATION_FAILED, e);
		}
	}

	private boolean isNotNullorEmpty(String value) {
		return value != null && !value.isEmpty() && value.trim().length() > 0;
	}

}
