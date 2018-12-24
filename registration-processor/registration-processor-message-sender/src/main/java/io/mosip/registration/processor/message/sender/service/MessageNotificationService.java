package io.mosip.registration.processor.message.sender.service;

import java.util.List;

import org.springframework.stereotype.Service;

import io.mosip.registration.processor.message.sender.dto.MessageNotificationRequest;
import io.mosip.registration.processor.message.sender.dto.MessageNotificationResponse;

@Service
public interface MessageNotificationService {

	public MessageNotificationResponse sendSmsNotification(String templateTypeCode, String id, String idType, List<String> attributes );

	public MessageNotificationResponse sendEmailNotification(MessageNotificationRequest messageNotificationRequest);
}
