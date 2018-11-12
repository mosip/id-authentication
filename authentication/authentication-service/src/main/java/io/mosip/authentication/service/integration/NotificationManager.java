package io.mosip.authentication.service.integration;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.constant.RestServicesConstants;
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

	public boolean sendNotification(Set<NotificationType> notificationtype, Map<String, Object> values)
			throws IdAuthenticationBusinessException, RestServiceException {

		String templateName = null;

		if (notificationtype.contains(NotificationType.SMS)) {
			templateName = environment.getProperty("");
			String smsTemplate = applyTemplate(values, templateName);
			SmsRequestDto smsRequestDto = new SmsRequestDto();
			smsRequestDto.setMessage("");
			smsRequestDto.setNumber("");
			RestRequestDTO restRequestDTO = null;
			restRequestDTO = restRequestFactory.buildRequest(RestServicesConstants.NOTIFICATION_SERVICE, smsRequestDto,
					SmsResponseDto.class);
			SmsResponseDto smsResponseDto = restHelper.requestSync(restRequestDTO);
			return smsResponseDto.getStatus().equalsIgnoreCase("success");
		}
		if (notificationtype.contains(NotificationType.EMAIL)) {
			templateName = environment.getProperty("");
			String mailTemplate = applyTemplate(values, templateName);
			MailRequestDto mailRequestDto = new MailRequestDto();
			mailRequestDto.setMailContent(mailTemplate);
			restRequestFactory.buildRequest(RestServicesConstants.NOTIFICATION_SERVICE, mailRequestDto, null);

		}

		return false;
	}

	private String applyTemplate(Map<String, Object> values, String templateName)
			throws IdAuthenticationBusinessException {
		try {
			return idTemplateManager.applyTemplate(templateName, values);
		} catch (IOException e) {
			// FIXME throw valid Exception
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.AD_FAD_MUTUALLY_EXCULUSIVE, e);
		}
	}

}
