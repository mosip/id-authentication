package io.mosip.registration.processor.message.sender.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.mosip.registration.processor.message.sender.dto.MessageSenderDto;
import io.mosip.registration.processor.message.sender.stage.MessageSenderStage;
import io.mosip.registration.processor.message.sender.util.StatusNotificationTypeMapUtil;

@Configuration
public class MessageSenderBeanConfig {
		
	@Bean
	public MessageSenderStage getMessageSenderStage() {
		return new MessageSenderStage();
	}
	
	@Bean
	public StatusNotificationTypeMapUtil getStatusNotificationTypeMapUtil() {
		return new StatusNotificationTypeMapUtil();
	}
	
	@Bean
	public MessageSenderDto getMessageSenderDto() {
		return new MessageSenderDto();
	}

	
	
}
