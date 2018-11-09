package io.mosip.authentication.service.integration;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;

@Component
public class NotificationManager {

	@Autowired
	IdTemplateManager idTemplateManager;

	@Autowired
	private Environment environment;

	public boolean sendNotification(Set<NotificationType> notificationtype, Map<String, Object> values)
			throws IdAuthenticationBusinessException {

		String templateName;

		if (notificationtype.contains(NotificationType.SMS)) {
			// FIXME create property
			templateName = environment.getProperty("");
			String smsTemplate = applyTemplate(values, templateName);
			// FIXME call REST
		}

		if (notificationtype.contains(NotificationType.EMAIL)) {
			templateName = environment.getProperty("");
			String mailTemplate = applyTemplate(values, templateName);
			// FIXME call REST
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
