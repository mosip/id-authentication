package io.mosip.registration.processor.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.mosip.registration.processor.core.notification.template.mapping.RegistrationProcessorNotificationTemplate;
import io.mosip.registration.processor.core.packet.dto.demographicinfo.identify.RegistrationProcessorIdentity;

@Configuration
public class CoreConfigBean {

	@Bean
	public RegistrationProcessorIdentity getRegProcessorIdentityJson() {
		return new RegistrationProcessorIdentity();
	}
	
	@Bean RegistrationProcessorNotificationTemplate getRegistrationProcessorNotificationTemplate() {
		return new RegistrationProcessorNotificationTemplate();
	}
}
