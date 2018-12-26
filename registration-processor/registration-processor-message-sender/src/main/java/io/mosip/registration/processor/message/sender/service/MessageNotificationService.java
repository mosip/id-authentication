package io.mosip.registration.processor.message.sender.service;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import io.mosip.kernel.emailnotification.dto.ResponseDto;
import io.mosip.kernel.smsnotification.dto.SmsRequestDto;

@Service
public interface MessageNotificationService {

	public ResponseEntity<SmsRequestDto> sendSmsNotification(String templateTypeCode, String id, String idType,
			Map<String, Object> attributes);

	public CompletableFuture<ResponseEntity<ResponseDto>> sendEmailNotification(String templateTypeCode, String id,
			String idType, Map<String, Object> attributes, String[] mailCc, String subject,
			MultipartFile[] attachment);
}
