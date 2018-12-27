package io.mosip.registration.processor.core.spi.message.sender;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import io.mosip.registration.processor.core.notification.template.generator.dto.ResponseDto;
import io.mosip.registration.processor.core.notification.template.generator.dto.SmsResponseDto;

/**
 * The Interface MessageNotificationService
 * 
 * @author Alok Ranjan
 */

public interface MessageNotificationService {

	public ResponseEntity<SmsResponseDto> sendSmsNotification(String templateTypeCode, String id, String idType,
			Map<String, Object> attributes);

	public CompletableFuture<ResponseEntity<ResponseDto>> sendEmailNotification(String templateTypeCode, String id,
			String idType, Map<String, Object> attributes, String[] mailCc, String subject,
			MultipartFile[] attachment);
}
