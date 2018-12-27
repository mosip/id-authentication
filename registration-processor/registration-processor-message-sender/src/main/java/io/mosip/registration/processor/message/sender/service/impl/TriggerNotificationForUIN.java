package io.mosip.registration.processor.message.sender.service.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import io.mosip.registration.processor.core.notification.template.generator.dto.ResponseDto;
import io.mosip.registration.processor.core.notification.template.generator.dto.SmsResponseDto;
import io.mosip.registration.processor.core.spi.message.sender.MessageNotificationService;

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
	private MessageNotificationService<ResponseEntity<SmsResponseDto>,CompletableFuture<ResponseEntity<ResponseDto>>,MultipartFile[]> service;
	
	private static final String SMS_TEMPLATE_CODE = "SMS_TEMP_FOR_UIN_GEN";
	private static final String EMAIL_TEMPLATE_CODE = "EMAIL_TEMP_FOR_UIN_GEN";

	public void triggerNotification(String uin) {

		Map<String, Object> attributes = new HashMap<>();

		String[] ccEMailList = notificationEmails.split("\\|");

		String[] allNotificationTypes = notificationTypes.split("\\|");
		
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
