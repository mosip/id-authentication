package io.mosip.registration.processor.message.sender.service;

import org.springframework.stereotype.Service;

import io.mosip.registration.processor.message.sender.dto.MessageNotificationRequest;
import io.mosip.registration.processor.message.sender.dto.MessageNotificationResponse;

@Service
public interface MessageNotificationService {

	public MessageNotificationResponse sendSmsNotification(MessageNotificationRequest messageNotificationRequest);

	public MessageNotificationResponse sendEmailNotification(MessageNotificationRequest messageNotificationRequest);
}
