package io.mosip.registration.processor.message.sender.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import io.mosip.registration.processor.message.sender.service.MessageNotificationService;

public class TriggerNotificationForUIN {
	
	@Value("${registration.processor.notification.type}")
	private String notificationTypes;
	
	@Value("${registration.processor.notification.emails}")
	private String notificationEmails;
	
	@Value("${registration.processor.notification.subject}")
	private String notificationEmailSubject;
	
	@Autowired
	MessageNotificationService service;
	
	private static final String SMS_TEMPLATE_CODE = "SMS_TEMP_FOR_UIN_GEN";
	private static final String EMAIL_TEMPLATE_CODE = "EMAIL_TEMP_FOR_UIN_GEN";

	public void triggerNotification(String uin) {

		Map<String, Object> attributes = new HashMap<>();

		String[] ccEMailList = notificationEmails.split("|");

		String[] allNotificationTypes = notificationTypes.split("|");
		
		for (String notificationType : allNotificationTypes) {
			
			if (notificationType.equalsIgnoreCase("SMS")) {
				
				service.sendSmsNotification(SMS_TEMPLATE_CODE, uin, "UIN", attributes);
			} 
			else if (notificationType.equalsIgnoreCase("EMAIL")) {
				
				service.sendEmailNotification(EMAIL_TEMPLATE_CODE, uin, "UIN", attributes, ccEMailList, notificationEmailSubject, null);

			}
		}
	}

}
