package io.mosip.authentication.service.integration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.constant.RestServicesConstants;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.util.dto.RestRequestDTO;
import io.mosip.authentication.service.factory.RestRequestFactory;
import io.mosip.authentication.service.helper.RestHelper;
import io.mosip.authentication.service.integration.dto.SmsRequestDto;
import io.mosip.kernel.core.logger.spi.Logger;

/**
 * The Class NotificationManager.
 *
 * @author Dinesh Karuppiah.T
 */

@Component
public class NotificationManager {


	
	/** Rest Helper */
	@Autowired
	private RestHelper restHelper;

	/** Rest Request Factory */
	@Autowired
	private RestRequestFactory restRequestFactory;

	/** Logger to log the actions */
	private static Logger logger = IdaLogger.getLogger(NotificationManager.class);
	
	/**
	 * Send sms notification.
	 *
	 * @param notificationMobileNo the notification mobile no
	 * @param message the message
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	public void sendSmsNotification(String notificationMobileNo, String message) throws IdAuthenticationBusinessException {
		try {
			SmsRequestDto smsRequestDto = new SmsRequestDto();
			smsRequestDto.setMessage(message);
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
	
	/**
	 * Send email notification.
	 *
	 * @param emailId the email id
	 * @param mailSubject the mail subject
	 * @param mailContent the mail content
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	public void sendEmailNotification(String emailId, String mailSubject, String mailContent)
			throws IdAuthenticationBusinessException {
		try {
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
