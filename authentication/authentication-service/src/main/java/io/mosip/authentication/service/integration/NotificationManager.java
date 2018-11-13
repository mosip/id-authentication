package io.mosip.authentication.service.integration;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.constant.RestServicesConstants;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.RestServiceException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.util.dto.RestRequestDTO;
import io.mosip.authentication.service.factory.RestRequestFactory;
import io.mosip.authentication.service.helper.RestHelper;
import io.mosip.authentication.service.integration.dto.MailRequestDto;
import io.mosip.authentication.service.integration.dto.SmsRequestDto;
import io.mosip.authentication.service.integration.dto.SmsResponseDto;
import io.mosip.kernel.core.spi.logger.MosipLogger;

/**
 * 
 * @author Dinesh Karuppiah.T
 */

@Component
public class NotificationManager {

	@Autowired
	IdTemplateManager idTemplateManager;

	@Autowired
	private Environment environment;

	@Autowired
	private RestHelper restHelper;

	@Autowired
	private RestRequestFactory restRequestFactory;

	private static MosipLogger logger = IdaLogger.getLogger(NotificationManager.class);

	private static final String SENDER_AUTH = "auth";

	private static final String SENDER_OTP = "otp";

	private static final String AUTH_SMS_TEMPLATE = "mosip.auth.sms.template";

	private static final String OTP_SMS_TEMPLATE = "mosip.otp.sms.template";

	private static final String AUTH_EMAIL_SUBJECT_TEMPLATE = "mosip.auth.mail.subject.template";

	private static final String AUTH_EMAIL_CONTENT_TEMPLATE = "mosip.auth.mail.content.template";

	private static final String OTP_SUBJECT_TEMPLATE = "mosip.otp.mail.subject.template";

	private static final String OTP_CONTENT_TEMPLATE = "mosip.otp.mail.content.template";

	public void sendNotification(Set<NotificationType> notificationtype, Map<String, Object> values, String emailId,
			String phoneNumber, String sender) throws IdAuthenticationBusinessException {
		String contentTemplate = null;
		String subjectTemplate = null;

		if (notificationtype.contains(NotificationType.SMS)) {
			switch (sender) {
			case SENDER_AUTH:
				contentTemplate = environment.getProperty(AUTH_SMS_TEMPLATE);
				break;
			case SENDER_OTP:
				contentTemplate = environment.getProperty(OTP_SMS_TEMPLATE);
				break;
			default:
				break;
			}
			try {
				String smsTemplate = applyTemplate(values, contentTemplate);
				SmsRequestDto smsRequestDto = new SmsRequestDto();
				smsRequestDto.setMessage(smsTemplate);
				smsRequestDto.setNumber(phoneNumber);
				RestRequestDTO restRequestDTO = null;
				restRequestDTO = restRequestFactory.buildRequest(RestServicesConstants.NOTIFICATION_SERVICE,
						smsRequestDto, SmsResponseDto.class);
				restHelper.requestAsync(restRequestDTO);
			} catch (IDDataValidationException e) {
				logger.error("NA", "Inside SMS Notification >>>>>", e.getErrorCode(), e.getErrorText());
				throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.NOTIFICATION_FAILED, e);
			}

		}
		if (notificationtype.contains(NotificationType.EMAIL)) {

			switch (sender) {
			case SENDER_AUTH:
				subjectTemplate = environment.getProperty(AUTH_EMAIL_SUBJECT_TEMPLATE);
				contentTemplate = environment.getProperty(AUTH_EMAIL_CONTENT_TEMPLATE);
				break;
			case SENDER_OTP:
				subjectTemplate = environment.getProperty(OTP_SUBJECT_TEMPLATE);
				contentTemplate = environment.getProperty(OTP_CONTENT_TEMPLATE);
				break;
			default:
				break;
			}
			try {

				String mailSubject = applyTemplate(values, subjectTemplate);
				String mailContent = applyTemplate(values, contentTemplate);
				MailRequestDto mailRequestDto = new MailRequestDto();
				mailRequestDto.setMailContent(mailContent);
				mailRequestDto.setMailSubject(mailSubject);
				mailRequestDto.setMailTo(new String[] { emailId });
				RestRequestDTO restRequestDTO = null;
				restRequestDTO = restRequestFactory.buildRequest(RestServicesConstants.NOTIFICATION_SERVICE,
						mailRequestDto, null);
				restHelper.requestAsync(restRequestDTO);
			} catch (IDDataValidationException e) {
				logger.error("NA", "Inside Mail Notification >>>>>", e.getErrorCode(), e.getErrorText());
				throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.NOTIFICATION_FAILED, e);
			}

		}

	}

	private String applyTemplate(Map<String, Object> values, String templateName)
			throws IdAuthenticationBusinessException {
		try {
			return idTemplateManager.applyTemplate(templateName, values);
		} catch (IOException e) {
			// FIXME throw valid Exception
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.NOTIFICATION_FAILED, e);
		}
	}

}
