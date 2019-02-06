package io.mosip.registration.processor.message.sender.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartFile;

import io.mosip.registration.processor.core.notification.template.generator.dto.ResponseDto;
import io.mosip.registration.processor.core.notification.template.generator.dto.SmsResponseDto;
import io.mosip.registration.processor.core.spi.message.sender.MessageNotificationService;
import io.mosip.registration.processor.message.sender.service.impl.MessageNotificationServiceImpl;
import io.mosip.registration.processor.message.sender.template.generator.TemplateGenerator;
import io.mosip.registration.processor.message.sender.utility.MessageSenderUtil;

@Configuration
public class MsgNotificationBeanCofig {

	@Bean
	public MessageNotificationService<SmsResponseDto, ResponseDto, MultipartFile[]> getMessageNotificationService() {
		return new MessageNotificationServiceImpl();
	}
	
	@Bean 
	public MessageSenderUtil getMessageSenderUtil() {
		return new MessageSenderUtil();
	}
	
	@Bean
	public TemplateGenerator getTemplateGenerator() {
		return new TemplateGenerator();
	}
	
}
